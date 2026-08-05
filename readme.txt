================================================================================
DemoBlaze E2E Automation — Serenity BDD + Screenplay
================================================================================

Project
Automation of the complete purchase flow on https://www.demoblaze.com/
using Serenity BDD + Screenplay Pattern.

Prerequisites
- Java 17 or higher
- Maven (or use the included Maven Wrapper)
- Google Chrome installed

How to run the tests

Open PowerShell in the project folder and run:

.\mvnw.cmd clean verify

To run in headless mode:

.\mvnw.cmd clean verify -Pheadless

What the test does
1. Opens the DemoBlaze page
2. Adds two products to the cart
3. Opens the cart and verifies the products
4. Fills the purchase form
5. Completes the purchase
6. Verifies the success message and order ID
7. Closes the confirmation modal

Notes
- The project uses Screenplay Pattern
- No Thread.sleep is used
- WebDriverManager downloads the correct ChromeDriver automatically


Versión en español
------------------

Proyecto
Automatización del flujo completo de compra en https://www.demoblaze.com/
usando Serenity BDD + patrón Screenplay.

Requisitos
- Java 17 o superior
- Maven (o usar el Maven Wrapper incluido)
- Google Chrome instalado

Cómo ejecutar las pruebas

Abre PowerShell en la carpeta del proyecto y ejecuta:

.\mvnw.cmd clean verify

Para ejecutar en modo headless:

.\mvnw.cmd clean verify -Pheadless

Qué hace la prueba
1. Abre la página de DemoBlaze
2. Agrega dos productos al carrito
3. Abre el carrito y verifica los productos
4. Llena el formulario de compra
5. Completa la compra
6. Verifica el mensaje de éxito y el ID de la orden
7. Cierra el modal de confirmación

Notas
- El proyecto usa el patrón Screenplay
- No se utiliza Thread.sleep
- WebDriverManager descarga automáticamente el ChromeDriver correcto
================================================================================