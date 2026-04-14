@echo off
setlocal
echo [BUILD] Starting build...
set "CP=lib\mysql-connector-j-8.4.0.jar"

if defined JAVA_HOME (
  set "JAVAC=%JAVA_HOME%\bin\javac.exe"
  if exist "%JAVAC%" goto found_javac
)

where javac >nul 2>&1
if %ERRORLEVEL%==0 (
  for /f "delims=" %%i in ('where javac ^| findstr /i /r /c:"javac.exe"') do set "JAVAC=%%i" & goto found_javac
)

echo [BUILD] JDK tidak ditemukan.
echo [BUILD] Install JDK dan pastikan javac tersedia di PATH atau set JAVA_HOME.
exit /b 1

:found_javac
if defined JAVA_HOME (
  echo [BUILD] JAVA_HOME=%JAVA_HOME%
) else (
  echo [BUILD] JAVA_HOME tidak diatur, menggunakan javac dari PATH.
)
echo [BUILD] JAVAC=%JAVAC%

echo [BUILD] Checking JDK...
if not exist "%JAVAC%" (
  echo [BUILD] JDK tidak ditemukan di %JAVAC%.
  echo [BUILD] Install JDK dan pastikan jalur javac valid.
  exit /b 1
)

echo [BUILD] Preparing compile sources...
if not exist target\classes mkdir target\classes
if exist target\sources.txt del target\sources.txt
for /r src\main\java %%f in (*.java) do echo %%f>>target\sources.txt

echo [BUILD] Compiling Java sources...
echo [BUILD] Command: "%JAVAC%" --release 17 -cp "%CP%" -d target\classes @target\sources.txt
"%JAVAC%" --release 17 -cp "%CP%" -d target\classes @target\sources.txt
if errorlevel 1 (
  echo [BUILD] Kompilasi gagal.
  del target\sources.txt 2>nul
  exit /b 1
)

echo [BUILD] Kompilasi selesai.
del target\sources.txt
endlocal
