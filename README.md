# 📧 Mail Service - Servicio de Envío de Correos

====================================================

Mail Service es un microservicio desarrollado con Spring Boot + Java que proporciona un servicio centralizado para el envío de correos electrónicos.

La aplicación puede recibir solicitudes de envío mediante:

🌐 API REST

📡 Kafka

📨 Envío mediante SMTP

💾 Persistencia en PostgreSQL

📎 Soporte para archivos adjuntos

👥 Gestión de múltiples destinatarios

🐳 Preparado para ejecución mediante Docker

🚀 Características principales

==============================

⚡ Backend con Spring Boot

☕ Java 21

🌐 API REST

📡 Integración con Apache Kafka

📨 Envío mediante SMTP

👥 Múltiples destinatarios

📄 Soporte para contenido HTML

📎 Archivos adjuntos

↩️ Reply-To

💾 Persistencia mediante PostgreSQL

📊 Estados de envío

❌ Registro de errores

📚 Swagger / OpenAPI

🐳 Docker

🔧 Configuración mediante variables de entorno

📨 Formas de envío

==================

Mail Service permite utilizar dos mecanismos diferentes para solicitar

el envío de un correo.

🌐 Envío mediante REST

=====================

Los microservicios pueden realizar una petición HTTP directamente

contra Mail Service.

Arquitectura:

Microservicio

```
 │

 │ REST

 ▼
```

Mail Service

```
 │

 │ SMTP

 ▼
```

Servidor de correo

```
 │

 ▼
```

Destinatario

Este mecanismo permite realizar envíos de forma síncrona y obtener

la respuesta directamente desde la API.

📡 Envío mediante Kafka

=======================

Mail Service también puede recibir solicitudes de envío mediante

Apache Kafka.

Arquitectura:

Microservicio

```
 │

 │ Kafka

 ▼
```

mail.send

```
 │

 ▼
```

Mail Service

```
 │

 │ SMTP

 ▼
```

Servidor de correo

```
 │

 ▼
```

Destinatario

El productor publica un mensaje en el topic correspondiente y

Mail Service procesa el evento mediante un consumidor Kafka.

Este mecanismo permite desacoplar el productor del proceso de envío

y utilizar un flujo basado en mensajería.

🔄 Arquitectura general

========================

Las dos vías de entrada utilizan la misma lógica de negocio y

terminan utilizando el mismo proceso de envío.

```
                ┌──────────────┐

                │ Microservicio│

                └──────┬───────┘

                       │

             ┌─────────┴─────────┐

             │                   │

            REST               Kafka

             │                   │

             ▼                   ▼

      ┌─────────────────────────────┐
      │        Mail Service         │
      │                             │
      │      Email Service          │
      └──────────────┬──────────────┘
                     │
                ┌────┴────┐
                │         │
                ▼         ▼
          PostgreSQL     SMTP
                          │
                          ▼
                     Destinatario
```

De esta forma, REST y Kafka actúan como diferentes mecanismos de

entrada al mismo servicio de correo.

📡 Kafka

=========

La integración con Kafka permite procesar solicitudes de envío

de forma asíncrona.

El flujo básico es:

Productor

│

▼

Kafka

│

│ mail.send

▼

Mail Service

│

▼

Procesamiento del email

│

▼

SMTP

Kafka permite desacoplar los microservicios productores del servicio

responsable del envío de correo.

📎 Archivos adjuntos

====================

Mail Service permite enviar archivos adjuntos mediante peticiones

multipart/form-data a través de la API REST.

Ejemplo:

📨 Email

├── 👥 Destinatarios

├── 📝 Asunto

├── 📄 Cuerpo

└── 📎 Adjuntos

```
├── documento.pdf

└── imagen.png
```

Los archivos adjuntos quedan asociados al correo correspondiente

en la base de datos.

🗄️ Persistencia

================

La aplicación utiliza PostgreSQL para almacenar la información

relacionada con los correos.

Se almacenan principalmente:

📧 Información del email

👥 Destinatarios

📎 Archivos adjuntos

📊 Estado del envío

🕐 Fechas de creación y envío

❌ Mensaje de error

La persistencia permite mantener un histórico de los correos

procesados y realizar seguimiento de los envíos.

📊 Estados

==========

Los correos disponen de un estado que permite conocer el resultado

del procesamiento.

Flujo básico:

QUEUED

│

▼

Procesamiento

│

├──► SENT

│

└──► FAILED

Esto permite identificar los envíos realizados correctamente y

aquellos que han producido un error.

📚 Swagger / OpenAPI

====================

La API REST dispone de documentación mediante Swagger / OpenAPI.

Una vez arrancada la aplicación puede accederse desde:

👉 http://localhost:8086/swagger-ui.html

🧩 Tecnologías

===============

Java              -> Lenguaje principal

Spring Boot       -> Framework backend

Spring Web        -> API REST

Spring Kafka      -> Integración con Kafka

Spring Mail       -> Envío SMTP

Spring Data JPA   -> Persistencia

Hibernate         -> ORM

PostgreSQL        -> Base de datos

Lombok            -> Reducción de código repetitivo

Swagger / OpenAPI -> Documentación API

Maven             -> Build tool

Docker            -> Deploy

🏗️ Arquitectura del proyecto

=============================

mail-service/

├── src/

│   ├── main/

│   │   ├── java/

│   │   │   └── com/julian/mail_service/

│   │   │

│   │   ├── controller/

│   │   │   └── EmailController

│   │   │

│   │   ├── service/

│   │   │   ├── EmailService

│   │   │   └── impl/

│   │   │       └── EmailServiceImpl

│   │   │

│   │   ├── kafka/

│   │   │   └── ...

│   │   │

│   │   ├── repository/

│   │   │   ├── EmailRepository

│   │   │   ├── RecipientRepository

│   │   │   └── AttachmentRepository

│   │   │

│   │   ├── entity/

│   │   │   ├── Email

│   │   │   ├── Recipient

│   │   │   └── Attachment

│   │   │

│   │   ├── dto/

│   │   │   ├── EmailRequest

│   │   │   ├── EmailMultipartRequest

│   │   │   └── EmailResponse

│   │   │

│   │   └── exception/

│   │       └── GlobalExceptionHandler

│   │

│   └── resources/

│       └── application.yml

│

└── pom.xml

🔧 Componentes principales

==========================

EmailController

* Gestiona las peticiones HTTP

* Recibe las solicitudes de envío REST

* Gestiona peticiones con y sin adjuntos

Kafka Consumer

* Consume los mensajes publicados en Kafka

* Recibe las solicitudes de envío asíncronas

* Delega el procesamiento al servicio de correo

EmailService

* Define las operaciones del servicio de correo

* Centraliza la lógica de negocio

* Es utilizado tanto por REST como por Kafka

EmailServiceImpl

* Implementa la lógica de envío

* Gestiona la persistencia

* Procesa los correos

* Actualiza el estado del envío

Repositories

* Gestionan el acceso a PostgreSQL mediante Spring Data JPA

DTOs

* Definen los modelos de entrada y salida de la API y los mensajes

  utilizados durante el procesamiento

GlobalExceptionHandler

* Centraliza la gestión de excepciones

* Devuelve respuestas de error controladas

⚙️ Configuración local

======================

Clonar el proyecto:

git clone https://github.com/jgf78/mail-service.git

cd mail-service

Configurar las variables de entorno necesarias para:

* SMTP

* PostgreSQL

* Kafka

Compilar:

mvn clean install -DskipTests

Arrancar la aplicación:

mvn spring-boot:run

La aplicación estará disponible en:

👉 http://localhost:8086

Swagger:

👉 http://localhost:8086/swagger-ui.html

🗄️ PostgreSQL

==============

La aplicación necesita una base de datos PostgreSQL.

Configuración mediante variables de entorno:

DB_HOST

DB_PORT

DB_NAME

DB_USERNAME

DB_PASSWORD

Ejemplo:

DB_HOST=localhost

DB_PORT=5432

DB_NAME=mailservice

DB_USERNAME=iagente

DB_PASSWORD=********

📨 Configuración SMTP

=====================

El servidor SMTP se configura mediante variables de entorno.

Ejemplo:

MAIL_HOST

MAIL_PORT

MAIL_USERNAME

MAIL_PASSWORD

MAIL_SMTP_AUTH

MAIL_SMTP_STARTTLS_ENABLE

Las credenciales y parámetros de conexión no se almacenan

directamente en el código fuente.

📡 Configuración Kafka

======================

La conexión con Kafka se configura mediante variables de entorno

y propiedades de Spring Kafka.

El servicio utiliza Kafka para recibir solicitudes de envío

mediante el topic:

mail.send

El flujo de mensajería es:

Productor

│

▼

mail.send

│

▼

Mail Service

│

▼

Envío SMTP

🐳 Docker

=========

La aplicación está preparada para ejecutarse mediante Docker.

Arquitectura:

```
              ┌───────────────┐
              │ Microservicio │
              └───────┬───────┘
                      │
                ┌─────┴─────┐
                │           │
               REST        Kafka
                │           │
                ▼           ▼
            ┌──────────────────┐
            │   Mail Service   │
            └────────┬─────────┘
                     │
                ┌────┴────┐
                │         │
                ▼         ▼
           PostgreSQL    SMTP
                          │
                          ▼
                     Destinatario
```

La configuración de SMTP, PostgreSQL y Kafka se proporciona

mediante variables de entorno.

🔐 Gestión de errores

=====================

La aplicación dispone de un sistema centralizado de gestión

de excepciones mediante GlobalExceptionHandler.

Se controlan, entre otros:

❌ Errores de validación

❌ Errores SMTP

❌ Peticiones incorrectas

❌ Archivos demasiado grandes

❌ Errores internos

Los errores producidos durante el envío pueden quedar registrados

en la información del correo.

📈 Estado actual

================

Funcionalidades disponibles:

✅ API REST

✅ Integración con Kafka

✅ Envío SMTP

✅ Múltiples destinatarios

✅ Contenido HTML

✅ Reply-To

✅ Archivos adjuntos

✅ Persistencia PostgreSQL

✅ Estados de envío

✅ Registro de errores

✅ Swagger / OpenAPI

✅ Docker

Mail Service está diseñado como un componente reutilizable para

otros microservicios que necesiten enviar correos electrónicos,

permitiendo elegir entre comunicación REST o mensajería Kafka.

📄 Licencia

===========

MIT License — uso libre y modificación.

👤 Autor

========

Julián Gómez Fernández

💻 Java Developer

⚙️ Spring Boot · Java · Docker · REST

📡 Kafka

📨 Servicios de correo

🗄️ PostgreSQL

📡 Arquitectura de microservicios

🧠 Frase final

==============

"REST cuando necesitas una respuesta.

Kafka cuando necesitas desacoplar.

Mail Service cuando necesitas enviar."
