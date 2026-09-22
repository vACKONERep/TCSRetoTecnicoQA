@echo off
setlocal
rem Lets "k6 ..." work in this folder even when the terminal was opened
rem before k6 was added to PATH. Calls the real k6.exe, never this file.
set "K6EXE=%LOCALAPPDATA%\k6\k6.exe"
if exist "%K6EXE%" goto run
set "K6EXE=%~dp0..\.tools\k6\k6-v2.3.0-windows-amd64\k6.exe"
if exist "%K6EXE%" goto run

for /f "delims=" %%I in ('where k6.exe 2^>nul') do (
  set "K6EXE=%%I"
  goto run
)

echo No se encontro k6.exe. Cierra esta ventana, abre otra y prueba de nuevo.
echo Si sigue fallando, instala k6 2.3.0. Pasos en readme.txt.
exit /b 1

:run
"%K6EXE%" %*
exit /b %ERRORLEVEL%
