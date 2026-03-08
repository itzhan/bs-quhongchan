@echo off
chcp 65001 >nul 2>&1
setlocal EnableDelayedExpansion

set BASE_DIR=%~dp0
set SERVER_DIR=%BASE_DIR%classroom-server
set ADMIN_DIR=%BASE_DIR%classroom-admin
set WEB_DIR=%BASE_DIR%classroom-web

:: Kill occupied ports
for %%P in (8089 8848 3000) do (
    for /f "tokens=5" %%A in ('netstat -ano ^| findstr :%%P ^| findstr LISTENING 2^>nul') do (
        taskkill /F /PID %%A >nul 2>&1
    )
)

:: Start backend
echo Starting backend (port 8089)...
cd /d "%SERVER_DIR%"
start "Backend" cmd /c "mvn spring-boot:run -Dspring-boot.run.profiles=default 2>&1"

:: Wait for backend
echo Waiting for backend...
timeout /t 30 /nobreak >nul

:: Start admin frontend
echo Starting admin (port 8848)...
cd /d "%ADMIN_DIR%"
if not exist node_modules (
    call pnpm install
)
start "Admin" cmd /c "set NODE_OPTIONS=--max-old-space-size=4096 && pnpm dev 2>&1"

:: Start web frontend
echo Starting web (port 3000)...
cd /d "%WEB_DIR%"
if not exist node_modules (
    call pnpm install
)
start "Web" cmd /c "pnpm dev 2>&1"

timeout /t 5 /nobreak >nul

echo.
echo ============================================
echo   All services started!
echo ============================================
echo.
echo   Backend API:   http://localhost:8089/api
echo   Admin Panel:   http://localhost:8848
echo   Web Portal:    http://localhost:3000
echo.
echo   ---- Accounts ----
echo   Admin:    admin          / admin123
echo   Teacher:  teacher001     / admin123
echo   Teacher:  teacher002     / admin123
echo   Teacher:  teacher003     / admin123
echo   Student:  2022102310001  / admin123
echo   Student:  2022102310002  / admin123
echo   (All accounts use password: admin123)
echo.
echo   Stop: close all terminal windows or run stop.bat
echo ============================================
pause
