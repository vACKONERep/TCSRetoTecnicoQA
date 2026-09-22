================================================================================
EJERCICIO DE PERFORMANCE — PRUEBA DE CARGA DEL LOGIN
Herramienta: Grafana k6
================================================================================

Servicio bajo prueba
  POST https://fakestoreapi.com/auth/login
  Header: Content-Type: application/json
  Cuerpo: { "username": "<user>", "password": "<passwd>" }

Los usuarios no estan en el script. Salen de data/users.csv, columnas
user y passwd, que son las que pide el enunciado. El script las mapea a
username y password, que es lo que el servicio espera.

--------------------------------------------------------------------------------
1. VERSIONES
--------------------------------------------------------------------------------

Probado con:

  - Grafana k6 2.3.0 (commit e088784614, go1.26.8, windows/amd64)
  - Windows 10/11, PowerShell 5
  - Sin Java, sin Node y sin Maven. k6 es un binario.

k6 2.3.0 o superior. El script usa la API estable de k6 (http, checks,
thresholds, constant-arrival-rate y SharedArray). No usa extensiones.

Comprobar la version instalada:

  k6 version

--------------------------------------------------------------------------------
2. INSTALAR K6
--------------------------------------------------------------------------------

Windows (winget):

  winget install k6 --source winget

Windows, si no hay winget: bajar el zip de la release 2.3.0 y dejar k6.exe
en el PATH.

  https://github.com/grafana/k6/releases/download/v2.3.0/k6-v2.3.0-windows-amd64.zip

macOS:

  brew install k6

Linux (Debian/Ubuntu), ver la guia oficial si el repo aun no esta agregado:

  https://grafana.com/docs/k6/latest/set-up/install-k6/

Hace falta internet hacia https://fakestoreapi.com.

--------------------------------------------------------------------------------
3. COMO EJECUTAR
--------------------------------------------------------------------------------

Abre la terminal en ESTA carpeta (EjercicioPerformance).
No entres a scripts\. Si estas ahi, el comando de abajo no encuentra el archivo
y los reportes se intentan escribir en el lugar equivocado.

  cd ..    si el prompt termina en \scripts

Windows, en la ventana que ya tienes abierta (k6 todavia no esta en su PATH):

  k6 run scripts\login-load.js

Eso funciona porque en esta carpeta hay un k6.cmd que llama al k6.exe
instalado en %LOCALAPPDATA%\k6. No hace falta cerrar la ventana.
No uses .\run-load-test.cmd salvo que prefieras ese lanzador: hace lo mismo.

En una terminal nueva, abierta despues de instalar k6, el mismo comando
funciona desde cualquier carpeta porque k6 ya queda en el PATH del usuario.

macOS / Linux, desde esta carpeta:

  k6 run scripts/login-load.js

Que hace el escenario

  - executor constant-arrival-rate
  - 20 iteraciones por segundo durante 2 minutos (20 TPS)
  - cada iteracion es un POST de login
  - los 5 usuarios del CSV se rotan en orden
  - no hay sleep: la tasa la impone k6, no una pausa

Umbrales (si uno falla, k6 termina con codigo 99 y aun asi escribe reports/)

  - http_req_failed: rate < 0.03          error menor al 3%
  - http_req_duration: p(95) < 1500 ms    percentil 95 bajo 1,5 s
  - http_req_duration: max < 1500 ms      ninguna peticion pasa de 1,5 s
  - login_duration: p(95) < 1500 ms       misma regla sobre la metrica propia

El codigo 99 no es un fallo de instalacion. Quiere decir que el servicio
no cumplio un umbral. El detalle queda en reports/ y en conclusiones.txt.

Cada respuesta ademas se valida con checks:

  - status 201 (es el codigo que devuelve este login cuando acepta)
  - el JSON trae token
  - el tiempo de esa peticion es <= 1,5 s

Cambiar la duracion sin tocar el script (ejemplo, 1 minuto):

  Windows:  k6 run -e DURATION=1m scripts\login-load.js
  Linux:    k6 run -e DURATION=1m scripts/login-load.js

--------------------------------------------------------------------------------
4. REPORTES
--------------------------------------------------------------------------------

Al terminar quedan tres archivos en reports/:

  reports/resumen.txt     texto con TPS, percentiles y los tres criterios
  reports/summary.json    los mismos numeros, para otra herramienta
  reports/summary.html    la misma tabla para abrir en el navegador

Windows:

  Invoke-Item reports\summary.html
  Get-Content reports\resumen.txt

--------------------------------------------------------------------------------
5. ESTRUCTURA
--------------------------------------------------------------------------------

  EjercicioPerformance/
  ├── readme.txt                  este archivo
  ├── conclusiones.txt            hallazgos de la corrida
  ├── data/users.csv              user,passwd  (5 usuarios del enunciado)
  ├── scripts/login-load.js       escenario, umbrales y reporte
  └── reports/                    se genera al ejecutar

--------------------------------------------------------------------------------
6. NOTA SOBRE EL CODIGO HTTP
--------------------------------------------------------------------------------

El curl del enunciado no fija el status esperado. Este servicio, con las
credenciales del CSV, responde 201 y un JSON { "token": "..." }, no 200.
201 no cuenta como error en k6 (error es status 0 o >= 400). El check pide
201 para no dar por bueno un 200 vacio ni un 401.
================================================================================
