# Cloud Stepper
<h2>Microservice API Gateway including Authentication and Authorization</h2>

V. 0.0.1

Cloud Stepper is Java based and consists out of three services.

Helpful if you want to create a backend with Microservice architecture.

Offer only one entry point (port) to the clients for communicate to the services.

<b> *All details you will find here: https://www.cloudstepper.info* </b>

Short summarization
===================

Supports
--------
+ REST services with Json formatted messages
+ GET, POST, DELETE
+ Authentication
+ Authorization

Nearly available out of the box.
No need of an installed Webserver.

This three services must run on the server:
- serviceProvider (the Gateway itself)
- userService (maintains the registered users)
- authenticationService (maintains permissions, roles and tokens)

Prerequisites:
- Install and start mysql or mariadb and create user with database
- Ensure that Java 1.8+ is available
- Configure
  - Service ports
  - Your activated Service-Domains
  - Your activated Microservices
  - optional ssh params
- Start the Java services
