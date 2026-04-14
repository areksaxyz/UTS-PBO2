@echo off
setlocal
echo [RUN] Starting run.bat

echo [RUN] Building project before launch...
call scripts\build.bat
if errorlevel 1 (
  echo [RUN] Build failed. Aborting launch.
  exit /b 1
)

echo [RUN] Build succeeded.
set "CP=target\classes;lib\mysql-connector-j-8.4.0.jar"

if defined JAVA_HOME (
  set "JAVA=%JAVA_HOME%\bin\java.exe"
  if not exist "%JAVA%" set "JAVA="
)
if not defined JAVA (
  where java >nul 2>&1
  if %ERRORLEVEL%==0 (
    for /f "delims=" %%i in ('where java ^| findstr /i /r /c:"java.exe"') do set "JAVA=%%i" & goto found_java
  )
  echo [RUN] Java tidak ditemukan.
  echo [RUN] Install JDK dan pastikan java tersedia di PATH atau set JAVA_HOME.
  exit /b 1
)
:found_java
set "JAVA2D=-Dsun.java2d.d3d=false -Dsun.java2d.opengl=false -Dsun.java2d.noddraw=true"

echo [RUN] JAVA=%JAVA%
echo [RUN] Launching Java application...
echo [RUN] Command: "%JAVA%" %JAVA2D% -cp "%CP%" id.ac.utb.pbo2.Aplikasi
"%JAVA%" %JAVA2D% -cp "%CP%" id.ac.utb.pbo2.Aplikasi
set EXIT_CODE=%ERRORLEVEL%
echo [RUN] Application exited with code %EXIT_CODE%.
endlocal
exit /b %EXIT_CODE%
