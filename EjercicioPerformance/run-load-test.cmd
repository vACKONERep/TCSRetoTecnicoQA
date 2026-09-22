@echo off
setlocal
cd /d "%~dp0"

set "K6=%LOCALAPPDATA%\k6\k6.exe"
if not exist "%K6%" set "K6=%~dp0..\.tools\k6\k6-v2.3.0-windows-amd64\k6.exe"
if not exist "%K6%" (
  where k6 >nul 2>&1
  if errorlevel 1 (
    echo No se encontro k6.exe.
    echo Instala k6 2.3.0 y vuelve a abrir la terminal. Pasos en readme.txt.
    exit /b 1
  )
  set "K6=k6"
)

echo Usando: %K6%
"%K6%" run scripts\login-load.js
exit /b %ERRORLEVEL%
