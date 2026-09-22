================================================================================
RETO TECNICO — QA AUTOMATIZADOR — TCS ECUADOR
DemoBlaze: E2E con Serenity BDD + Cucumber, y APIs con Karate
================================================================================

El repositorio resuelve los ejercicios del examen practico.

  E2E (raiz)                         Flujo de compra en https://www.demoblaze.com/
  APIs (carpeta karate-api)          Signup y Login en https://api.demoblaze.com
  Performance (EjercicioPerformance) Carga del login con k6

Archivos pedidos por el enunciado:
  readme.txt          este archivo (pasos de ejecucion del E2E y el indice)
  conclusiones.txt    hallazgos del ejercicio E2E
  karate-api/readme.txt
  karate-api/conclusiones.txt
  EjercicioPerformance/readme.txt
  EjercicioPerformance/conclusiones.txt

--------------------------------------------------------------------------------
1. REQUISITOS
--------------------------------------------------------------------------------

  - JDK 17 o superior. Comprobar con:  java -version
  - Google Chrome instalado (solo el E2E; el driver lo baja WebDriverManager)
  - Internet, porque la pagina y la API son publicas
  - No hace falta instalar Maven. Usar el Maven Wrapper de la raiz:
      Windows:     mvnw.cmd
      macOS/Linux: ./mvnw

--------------------------------------------------------------------------------
2. EJERCICIO 1 — EJECUTAR EL E2E
--------------------------------------------------------------------------------

Abrir una terminal en la raiz de este repositorio.

  Con ventana de Chrome (Windows):
      .\mvnw.cmd clean verify

  Sin ventana, para CI o una maquina sin escritorio:
      .\mvnw.cmd clean verify -Pheadless

  macOS / Linux:
      ./mvnw clean verify
      ./mvnw clean verify -Pheadless

Que hace cada ejemplo del Scenario Outline

  1. Abre https://www.demoblaze.com/
  2. Agrega los dos productos del caso (archivo purchases.csv)
  3. Abre el carrito y comprueba que esos dos productos estan
  4. Completa el formulario con el comprador del caso (archivo buyers.json)
  5. Finaliza la compra
  6. Verifica "Thank you for your purchase!" y que la confirmacion traiga un Id
  7. Cierra el modal de confirmacion

Casos que corren hoy

  PHONES-01   Samsung galaxy s6 + Nokia lumia 1520   comprador buyer-01 (Quito)
  PHONES-02   Nexus 6 + Samsung galaxy s7            comprador buyer-02 (Guayaquil)

De donde salen los datos (no estan quemados en el feature)

  src/test/resources/data/purchases.csv   productos, por caseId
  src/test/resources/data/buyers.json     formulario, por buyerId
  src/test/resources/features/purchase_flow.feature
      Scenario Outline. La tabla Examples solo elige el caseId (<caseId>).

Para agregar otra combinacion: nueva fila en el CSV, el buyer en el JSON
si aun no existe, y el caseId en Examples. Los steps no se tocan.

--------------------------------------------------------------------------------
3. REPORTES DEL E2E
--------------------------------------------------------------------------------

Se generan al terminar "mvn verify".

  Serenity (living documentation, pasos Screenplay):
      target/site/serenity/index.html

  Cucumber (resumen HTML, JSON y JUnit):
      target/cucumber-reports/index.html
      target/cucumber-reports/cucumber.json
      target/cucumber-reports/cucumber.xml

  Timeline de Cucumber:
      target/cucumber-timeline/index.html

  Windows:
      Invoke-Item target\site\serenity\index.html
      Invoke-Item target\cucumber-reports\index.html

--------------------------------------------------------------------------------
4. EJERCICIO 2 — EJECUTAR LAS APIS
--------------------------------------------------------------------------------

Desde la raiz del repositorio (Windows):

      .\mvnw.cmd -f karate-api\pom.xml clean test

Solo signup, solo login, o el tag smoke:

      .\mvnw.cmd -f karate-api\pom.xml test -Psignup
      .\mvnw.cmd -f karate-api\pom.xml test -Plogin
      .\mvnw.cmd -f karate-api\pom.xml test -Psmoke

El detalle de carpetas, casos y el contrato real de la API esta en
karate-api/readme.txt.

--------------------------------------------------------------------------------
5. REPORTE DE LAS APIS
--------------------------------------------------------------------------------

      karate-api/target/karate-reports/karate-summary.html

Abrir un escenario muestra la entrada (request JSON) y la salida (response).
Karate tambien deja el JSON de Cucumber junto a ese HTML.

  Windows, desde la raiz:
      Invoke-Item karate-api\target\karate-reports\karate-summary.html

--------------------------------------------------------------------------------
5b. PERFORMANCE — PRUEBA DE CARGA
--------------------------------------------------------------------------------

La prueba de k6 vive en EjercicioPerformance/. Ahi estan su readme.txt
(con la version de k6) y su conclusiones.txt. Resumen:

  cd EjercicioPerformance
  k6 run scripts\login-load.js

Reporte: EjercicioPerformance/reports/summary.html

--------------------------------------------------------------------------------
6. ESTRUCTURA
--------------------------------------------------------------------------------

  pom.xml                         build del E2E (Serenity + Cucumber + Failsafe)
  src/test/java/.../tasks         tareas Screenplay (agregar, carrito, compra)
  src/test/java/.../ui            user interface (Home, Product, Cart, Order)
  src/test/java/.../models        PurchaseData y PurchaseCase
  src/test/java/.../questions     lecturas del carrito y de la confirmacion
  src/test/java/.../data          catalogo que lee el CSV y el JSON
  src/test/resources/features     purchase_flow.feature (Scenario Outline)
  src/test/resources/data         purchases.csv y buyers.json
  karate-api/                     proyecto Maven independiente de las APIs
  EjercicioPerformance/           prueba de carga k6 (login, 20 TPS, CSV)
  readme.txt / conclusiones.txt   entregables del E2E
  README.md                       misma guia, en formato de GitHub

--------------------------------------------------------------------------------
7. CRITERIOS DEL CORREO Y DONDE QUEDAN CUBIERTOS
--------------------------------------------------------------------------------

E2E
  1. Los tests corren con: .\mvnw.cmd clean verify
  2. Informes Serenity y Cucumber: seccion 3 de este archivo
  3. Feature con variable <caseId>; url, actor y mensaje en TestData
  4. Scenario Outline alimentado por purchases.csv y buyers.json
  5. tasks, ui, models, questions e interactions
  6. Este readme.txt
  7. conclusiones.txt

APIs
  1. POST /signup y POST /login, alta nueva, usuario repetido,
     login correcto y login incorrecto
  2. Informe karate-reports: seccion 5
  3. baseUrl, defaultPassword y messages en karate-config.js
  4. Scenario Outline: signup-cases.csv y login-cases.json

--------------------------------------------------------------------------------
8. SI ALGO FALLA
--------------------------------------------------------------------------------

  - "java no se reconoce": instalar JDK 17 y abrir una terminal nueva.
  - Chrome no abre: instalar Google Chrome, o usar -Pheadless.
  - El E2E falla al buscar un producto: el nombre en purchases.csv tiene que
    ser exacto al de la tienda (mayusculas incluidas), y el producto tiene
    que estar en la primera pagina (categoria Phones).
  - La API responde 200 con errorMessage: es el comportamiento del sitio,
    no un fallo del test. Ver conclusiones.
  - Limpiar salidas viejas: borrar las carpetas target y volver a correr.
================================================================================
