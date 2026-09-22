# Reto técnico QA Automatizador — TCS Ecuador

Automatización de [DemoBlaze](https://www.demoblaze.com/): flujo de compra E2E con Serenity BDD y Cucumber, y pruebas de Signup/Login con Karate.

La guía paso a paso que pide el enunciado está en [`readme.txt`](readme.txt). Los hallazgos están en [`conclusiones.txt`](conclusiones.txt).

## Qué cubre cada ejercicio

| Ejercicio | Qué automatiza | Cómo se ejecuta |
| --- | --- | --- |
| E2E (raíz) | Dos productos al carrito, ver el carrito, formulario y compra | `.\mvnw.cmd clean verify` |
| APIs (`karate-api`) | Signup nuevo, signup duplicado, login correcto, login incorrecto | `.\mvnw.cmd -f karate-api\pom.xml clean test` |
| Performance (`EjercicioPerformance`) | Carga de 20 TPS sobre el login de Fake Store, usuarios desde CSV | `k6 run scripts/login-load.js` (desde esa carpeta) |

Headless (sin ventana de Chrome): `.\mvnw.cmd clean verify -Pheadless`.

Requisitos: JDK 17, Google Chrome y salida a internet. Maven no hace falta instalarlo: el repo trae `mvnw.cmd`.

## Datos, no valores quemados en el feature

El E2E es un Scenario Outline. La tabla `Examples` solo elige el `caseId`.

- Productos: `src/test/resources/data/purchases.csv`
- Formulario de compra: `src/test/resources/data/buyers.json`
- Signup: `karate-api/src/test/resources/data/signup-cases.csv`
- Login: `karate-api/src/test/resources/data/login-cases.json`
- Textos de error de la API: variable `messages` en `karate-api/src/test/resources/karate-config.js`

## Reportes

Después de correr las pruebas:

| Informe | Ruta |
| --- | --- |
| Serenity | `target/site/serenity/index.html` |
| Cucumber | `target/cucumber-reports/index.html` |
| Karate | `karate-api/target/karate-reports/karate-summary.html` |
| k6 | `EjercicioPerformance/reports/summary.html` |

## Criterios del reto

**E2E:** los tests corren con Failsafe; se generan los informes de Serenity y de Cucumber; el feature usa la variable `<caseId>`; el outline consume CSV y JSON; la estructura es Screenplay (`tasks`, `ui`, `models`, `questions`); este README, `readme.txt` y `conclusiones.txt` explican la prueba.

**APIs:** signup y login cubren los casos pedidos (el único método que exponen esos dos recursos es POST); el informe es `karate-reports`; las features usan variables de `karate-config.js`; los outlines leen CSV y JSON.

El detalle de cada escenario, los perfiles (`-Psignup`, `-Plogin`, `-Psmoke`) y el contrato real de la API están en [`karate-api/readme.txt`](karate-api/readme.txt).

**Performance:** k6 2.3.0, 20 TPS, umbral de 1,5 s y error menor al 3 %. Pasos y versiones en [`EjercicioPerformance/readme.txt`](EjercicioPerformance/readme.txt). Hallazgos en [`EjercicioPerformance/conclusiones.txt`](EjercicioPerformance/conclusiones.txt).
