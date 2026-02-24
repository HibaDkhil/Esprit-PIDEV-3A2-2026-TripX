@echo off
setlocal

echo ===================================================
echo TRIPX (Using IntelliJ IDEA Tools)
echo ===================================================

:: Hardcoded paths found on system
set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1\jbr"
set "MAVEN_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1\plugins\maven\lib\maven3"

echo Setting up environment...
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo.
echo Checking Java version...
java -version
if %errorlevel% neq 0 (
    echo [ERROR] Failed to run Java from %JAVA_HOME%
    pause
    exit /b
)

echo.
echo Checking Maven version...
call mvn -version
if %errorlevel% neq 0 (
    echo [ERROR] Failed to run Maven from %MAVEN_HOME%
    pause
    exit /b
)

echo.
echo ===================================================
echo BUILDING & RUNNING PROJECT
echo ===================================================
echo.
echo NOTE: Ensure database 'tripx_db' is created!
echo.

call mvn clean javafx:run

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Build/Run failed.
    echo 1. Check if database is running and setup (database_setup.sql)
    echo 2. Check build logs above.
    pause
    exit /b
)

echo.
echo [SUCCESS] Application finished.
pause
