#!/bin/bash
# ============================================
# 学生课堂管理系统 - Mac 全自动启动脚本
# ============================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
SERVER_DIR="$BASE_DIR/classroom-server"
ADMIN_DIR="$BASE_DIR/classroom-admin"
WEB_DIR="$BASE_DIR/classroom-web"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

DB_USER="root"
DB_PASS="ab123168"
DB_NAME="classroom_db"

echo ""
echo -e "${BLUE}╔══════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   ${BOLD}学生课堂管理系统 - Mac 全自动启动脚本${NC}${BLUE}     ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════╝${NC}"
echo ""

# ============================================
# [1/5] 自动检测 & 安装环境
# ============================================
echo -e "${YELLOW}[1/5] 检测并安装运行环境...${NC}"

# -- Homebrew --
if ! command -v brew &>/dev/null; then
  echo -e "  ${BLUE}→${NC} 正在安装 Homebrew..."
  /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
  # Apple Silicon 路径
  if [ -f "/opt/homebrew/bin/brew" ]; then
    eval "$(/opt/homebrew/bin/brew shellenv)"
  fi
fi
echo -e "  ${GREEN}✓${NC} Homebrew: $(brew --version | head -1)"

# -- Java 17 --
if ! command -v java &>/dev/null || ! java -version 2>&1 | grep -q "17"; then
  echo -e "  ${BLUE}→${NC} 正在安装 JDK 17 (Temurin)..."
  brew install --cask temurin@17
fi
echo -e "  ${GREEN}✓${NC} Java: $(java -version 2>&1 | head -1)"

# -- Maven --
if ! command -v mvn &>/dev/null; then
  echo -e "  ${BLUE}→${NC} 正在安装 Maven..."
  brew install maven
fi
echo -e "  ${GREEN}✓${NC} Maven: $(mvn -version 2>&1 | head -1)"

# -- Node.js --
if ! command -v node &>/dev/null; then
  echo -e "  ${BLUE}→${NC} 正在安装 Node.js LTS..."
  brew install node@20
  brew link --overwrite node@20
fi
echo -e "  ${GREEN}✓${NC} Node.js: $(node -v)"

# -- pnpm --
if ! command -v pnpm &>/dev/null; then
  echo -e "  ${BLUE}→${NC} 正在安装 pnpm..."
  brew install pnpm
fi
echo -e "  ${GREEN}✓${NC} pnpm: $(pnpm -v)"

# -- MySQL --
if ! command -v mysql &>/dev/null; then
  echo -e "  ${BLUE}→${NC} 正在安装 MySQL..."
  brew install mysql
  brew services start mysql
  sleep 3
  echo -e "  ${BLUE}→${NC} 设置 MySQL root 密码..."
  mysqladmin -u root password "$DB_PASS" 2>/dev/null || true
fi
# 确保 MySQL 服务正在运行
if ! pgrep -x mysqld &>/dev/null; then
  echo -e "  ${BLUE}→${NC} 启动 MySQL 服务..."
  brew services start mysql
  sleep 3
fi
echo -e "  ${GREEN}✓${NC} MySQL: $(mysql --version 2>&1 | head -1)"

echo -e "  ${GREEN}${BOLD}环境检测完成 ✓${NC}"
echo ""

# ============================================
# [2/5] 自动初始化数据库
# ============================================
echo -e "${YELLOW}[2/5] 自动初始化数据库...${NC}"

if mysql -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1;" > /dev/null 2>&1; then
  echo -e "  ${GREEN}✓${NC} MySQL 连接成功"
else
  echo -e "  ${RED}✗ 无法连接 MySQL (用户: $DB_USER)，请检查 MySQL 是否已启动${NC}"
  echo -e "  ${YELLOW}提示: 如果密码不同，请修改此脚本中的 DB_PASS 变量${NC}"
  exit 1
fi

DB_EXISTS=$(mysql -u"$DB_USER" -p"$DB_PASS" -e "SHOW DATABASES LIKE '$DB_NAME';" 2>/dev/null | grep "$DB_NAME")
if [ -n "$DB_EXISTS" ]; then
  echo -e "  ${GREEN}✓${NC} 数据库 ${DB_NAME} 已存在，跳过导入"
else
  echo -e "  ${BLUE}→${NC} 导入 schema.sql (建库建表+初始账号)..."
  mysql -u"$DB_USER" -p"$DB_PASS" < "$SERVER_DIR/sql/schema.sql" 2>/dev/null
  echo -e "  ${BLUE}→${NC} 导入 data.sql (测试数据)..."
  mysql -u"$DB_USER" -p"$DB_PASS" < "$SERVER_DIR/sql/data.sql" 2>/dev/null
  if [ $? -eq 0 ]; then
    echo -e "  ${GREEN}✓${NC} 数据库初始化完成"
  else
    echo -e "  ${RED}✗ 数据库初始化失败${NC}"
    exit 1
  fi
fi
echo ""

# ============================================
# [3/5] 清理被占用端口
# ============================================
echo -e "${YELLOW}[3/5] 检查并清理端口占用...${NC}"

for PORT in 8089 8848 3000; do
  PID=$(lsof -ti:$PORT 2>/dev/null)
  if [ -n "$PID" ]; then
    echo -e "  ${YELLOW}⚠${NC} 端口 $PORT 被占用 (PID: $PID)，正在终止..."
    kill -9 $PID 2>/dev/null
    sleep 1
    echo -e "  ${GREEN}✓${NC} 端口 $PORT 已释放"
  else
    echo -e "  ${GREEN}✓${NC} 端口 $PORT 空闲"
  fi
done
echo ""

# ============================================
# [4/5] 安装前端依赖 (如需要)
# ============================================
echo -e "${YELLOW}[4/5] 检查前端依赖...${NC}"

if [ ! -d "$ADMIN_DIR/node_modules" ]; then
  echo -e "  ${BLUE}→${NC} 安装管理端依赖 (首次)..."
  cd "$ADMIN_DIR" && pnpm install 2>&1 | tail -3
fi
echo -e "  ${GREEN}✓${NC} 管理端依赖就绪"

if [ ! -d "$WEB_DIR/node_modules" ]; then
  echo -e "  ${BLUE}→${NC} 安装展示端依赖 (首次)..."
  cd "$WEB_DIR" && pnpm install 2>&1 | tail -3
fi
echo -e "  ${GREEN}✓${NC} 展示端依赖就绪"
echo ""

# ============================================
# [5/5] 启动所有服务 (日志实时输出到文件)
# ============================================
echo -e "${YELLOW}[5/5] 启动所有服务...${NC}"

# 后端
echo -e "  ${BLUE}→${NC} 启动后端 (Spring Boot, 端口 8089)..."
cd "$SERVER_DIR"
mvn spring-boot:run -Dspring-boot.run.profiles=default > "$BASE_DIR/.server.log" 2>&1 &
SERVER_PID=$!
echo $SERVER_PID > "$BASE_DIR/.server.pid"

# 等待后端就绪
echo -ne "  等待后端启动"
for i in $(seq 1 60); do
  HTTP_CODE=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8089/api/ 2>/dev/null)
  if [ "$HTTP_CODE" != "000" ]; then
    echo ""
    echo -e "  ${GREEN}✓${NC} 后端已启动 (PID: $SERVER_PID)"
    break
  fi
  echo -ne "."
  sleep 2
done
if [ $i -eq 60 ]; then
  echo ""
  echo -e "  ${YELLOW}⚠ 后端启动超时，查看日志: tail -f $BASE_DIR/.server.log${NC}"
fi

# 管理端
echo -e "  ${BLUE}→${NC} 启动管理端 (端口 8848)..."
cd "$ADMIN_DIR"
pnpm dev > "$BASE_DIR/.admin.log" 2>&1 &
ADMIN_PID=$!
echo $ADMIN_PID > "$BASE_DIR/.admin.pid"
sleep 3
echo -e "  ${GREEN}✓${NC} 管理端已启动 (PID: $ADMIN_PID)"

# 展示端
echo -e "  ${BLUE}→${NC} 启动展示端 (端口 3000)..."
cd "$WEB_DIR"
pnpm dev > "$BASE_DIR/.web.log" 2>&1 &
WEB_PID=$!
echo $WEB_PID > "$BASE_DIR/.web.pid"
sleep 3
echo -e "  ${GREEN}✓${NC} 展示端已启动 (PID: $WEB_PID)"
echo ""

# ============================================
# 启动完成 - 打印信息
# ============================================
echo -e "${BLUE}╔══════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║           ${GREEN}${BOLD}系统启动完成！${NC}${BLUE}                     ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════╝${NC}"
echo ""
echo -e "  ${CYAN}${BOLD}服务访问地址${NC}"
echo -e "  ├── 后端 API:     ${BOLD}http://localhost:8089/api${NC}"
echo -e "  ├── 后台管理端:   ${BOLD}http://localhost:8848${NC}"
echo -e "  └── 展示端前端:   ${BOLD}http://localhost:3000${NC}"
echo ""
echo -e "  ${CYAN}${BOLD}系统账号信息${NC}"
echo -e "  ┌──────────┬────────────────┬────────────┬──────────┐"
echo -e "  │ ${BOLD}角色${NC}     │ ${BOLD}用户名${NC}         │ ${BOLD}密码${NC}       │ ${BOLD}姓名${NC}     │"
echo -e "  ├──────────┼────────────────┼────────────┼──────────┤"
echo -e "  │ ${RED}管理员${NC}   │ admin          │ admin123   │ 系统管理员│"
echo -e "  ├──────────┼────────────────┼────────────┼──────────┤"
echo -e "  │ ${YELLOW}教师${NC}     │ teacher001     │ admin123   │ 张老师   │"
echo -e "  │ ${YELLOW}教师${NC}     │ teacher002     │ admin123   │ 李教授   │"
echo -e "  │ ${YELLOW}教师${NC}     │ teacher003     │ admin123   │ 王老师   │"
echo -e "  ├──────────┼────────────────┼────────────┼──────────┤"
echo -e "  │ ${GREEN}学生${NC}     │ 2022102310001  │ admin123   │ 李明     │"
echo -e "  │ ${GREEN}学生${NC}     │ 2022102310002  │ admin123   │ 王芳     │"
echo -e "  └──────────┴────────────────┴────────────┴──────────┘"
echo -e "  ${YELLOW}(共30名学生, 学号 2022102310001~30, 密码均为 admin123)${NC}"
echo ""
echo -e "  ${CYAN}${BOLD}查看日志${NC}"
echo -e "  ├── 后端日志:   tail -f $BASE_DIR/.server.log"
echo -e "  ├── 管理端日志: tail -f $BASE_DIR/.admin.log"
echo -e "  └── 展示端日志: tail -f $BASE_DIR/.web.log"
echo ""
echo -e "  ${YELLOW}停止所有服务:${NC} bash stop.sh"
echo ""

# ============================================
# 实时监控日志 (合并输出, 有报错会显示)
# ============================================
echo -e "${CYAN}${BOLD}实时日志监控 (按 Ctrl+C 停止监控，服务不会停止)${NC}"
echo -e "${BLUE}────────────────────────────────────────────────${NC}"
tail -f "$BASE_DIR/.server.log" "$BASE_DIR/.admin.log" "$BASE_DIR/.web.log" 2>/dev/null
