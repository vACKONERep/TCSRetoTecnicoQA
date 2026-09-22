================================================================================
EJERCICIO 2 — APIs DemoBlaze — Karate
================================================================================

Proyecto : demoblaze-karate-api
Base URL : https://api.demoblaze.com
Recursos : POST /signup    POST /login

El enunciado pide probar:
  1. Crear un usuario nuevo en signup
  2. Intentar crear un usuario que ya existe
  3. Login con usuario y password correctos
  4. Login con usuario y password incorrectos

Esos cuatro casos (mas un login con usuario inexistente) son Scenario Outline.
No hay un escenario copiado por cada caso.

--------------------------------------------------------------------------------
1. ESTRUCTURA
--------------------------------------------------------------------------------

  karate-api/
  ├── pom.xml
  ├── readme.txt
  ├── conclusiones.txt
  └── src/test/
      ├── java/com/demoblaze/api/runners/
      │     ├── DemoBlazeApiTest.java    suite completa (la que corre Maven)
      │     ├── SignupRunner.java        solo signup, para depurar en el IDE
      │     └── LoginRunner.java         solo login, para depurar en el IDE
      └── resources/
            ├── karate-config.js         baseUrl, password y messages
            ├── data/
            │     ├── signup-cases.csv   outline de signup
            │     └── login-cases.json   outline de login
            ├── helpers/
            │     └── create-user.feature   alta previa; no es un test suelto
            └── features/
                  ├── signup.feature
                  └── login.feature

--------------------------------------------------------------------------------
2. REQUISITOS
--------------------------------------------------------------------------------

  - JDK 17 o superior (java -version)
  - Internet hasta https://api.demoblaze.com
  - Maven Wrapper del repositorio padre (no hace falta instalar Maven)

--------------------------------------------------------------------------------
3. COMO EJECUTAR
--------------------------------------------------------------------------------

Desde la raiz del repositorio, en PowerShell:

    .\mvnw.cmd -f karate-api\pom.xml clean test

Desde esta carpeta (karate-api):

    ..\mvnw.cmd clean test
    ..\mvnw.cmd test -Psignup     solo el tag @signup
    ..\mvnw.cmd test -Plogin      solo el tag @login
    ..\mvnw.cmd test -Psmoke      solo el tag @smoke (los dos outlines)

macOS / Linux, desde la raiz:

    ./mvnw -f karate-api/pom.xml clean test

--------------------------------------------------------------------------------
4. CASOS Y DE DONDE SALE CADA DATO
--------------------------------------------------------------------------------

Signup — src/test/resources/data/signup-cases.csv

  SU-01  flow=create      usuario nuevo, sin errorMessage
  SU-02  flow=duplicate   se crea y se vuelve a registrar; error de negocio

Login — src/test/resources/data/login-cases.json

  LG-01  flow=valid            password correcto, respuesta con Auth_token
  LG-02  flow=wrong_password   mismo usuario, password distinto
  LG-03  flow=unknown_user     usuario que no se registro

Variables (karate-config.js), usadas por todas las features:

  baseUrl            https://api.demoblaze.com
  defaultPassword    password de los usuarios generados
  messages           textos exactos que devuelve la API

  userAlreadyExists  "This user already exist."
  wrongPassword      "Wrong password."
  userDoesNotExist   "User does not exist."

La columna expectedKey del CSV/JSON no repite el texto: apunta a messages.
El username se arma en el escenario con timestamp + random, asi una segunda
corrida no choca con la primera. La API no tiene forma de borrar usuarios.

--------------------------------------------------------------------------------
5. REPORTES
--------------------------------------------------------------------------------

Despues de mvn test, abrir:

    target/karate-reports/karate-summary.html

PowerShell, desde esta carpeta:

    Invoke-Item target\karate-reports\karate-summary.html

En cada escenario el HTML muestra el request (entrada) y el response (salida),
el status y el resultado del match. Tambien queda:

    target/karate-reports/*.json     JSON de cada feature
    target/surefire-reports/         resultado JUnit

--------------------------------------------------------------------------------
6. CONTRATO REAL DE LA API (IMPORTANTE PARA LEER LOS TESTS)
--------------------------------------------------------------------------------

Los dos recursos se consumen con POST y Content-Type application/json.

Signup y Login responden HTTP 200 tambien cuando el negocio falla.
Por eso el test afirma el status 200 y, ademas, el cuerpo:

  Signup correcto     cuerpo vacio, sin errorMessage
  Signup duplicado    { "errorMessage": "This user already exist." }
                      (la API omite la "s" de "exists"; el test usa ese texto)
  Login correcto      texto, no JSON:  Auth_token: <valor>
  Password malo       { "errorMessage": "Wrong password." }
  Usuario inexistente { "errorMessage": "User does not exist." }

No se probaron GET, PUT ni DELETE: la pagina solo llama POST para estas
dos operaciones, y el enunciado pide esos dos servicios.
================================================================================
