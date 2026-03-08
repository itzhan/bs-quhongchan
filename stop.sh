#!/bin/bash
# ============================================
# 学生课堂管理系统 - 停止所有服务
# ============================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}正在停止所有服务...${NC}"

# 停止后端
if [ -f "$BASE_DIR/.server.pid" ]; then
  PID=$(cat "$BASE_DIR/.server.pid")
  if kill -0 $PID 2>/dev/null; then
    kill $PID 2>/dev/null
    echo -e "${GREEN}✓ 后端服务已停止 (PID: $PID)${NC}"
  fi
  rm -f "$BASE_DIR/.server.pid"
fi

# 停止管理端
if [ -f "$BASE_DIR/.admin.pid" ]; then
  PID=$(cat "$BASE_DIR/.admin.pid")
  if kill -0 $PID 2>/dev/null; then
    kill $PID 2>/dev/null
    echo -e "${GREEN}✓ 后台管理端已停止 (PID: $PID)${NC}"
  fi
  rm -f "$BASE_DIR/.admin.pid"
fi

# 停止展示端
if [ -f "$BASE_DIR/.web.pid" ]; then
  PID=$(cat "$BASE_DIR/.web.pid")
  if kill -0 $PID 2>/dev/null; then
    kill $PID 2>/dev/null
    echo -e "${GREEN}✓ 展示端前端已停止 (PID: $PID)${NC}"
  fi
  rm -f "$BASE_DIR/.web.pid"
fi

# 清理日志
rm -f "$BASE_DIR/.server.log" "$BASE_DIR/.admin.log" "$BASE_DIR/.web.log"

echo -e "${GREEN}所有服务已停止${NC}"
