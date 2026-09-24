# 📧 Mail Service - Servicio de Envío de Correos

====================================================

Mail Service es un microservicio desarrollado con Spring Boot + Java que proporciona un servicio centralizado para el envío de correos electrónicos.

La aplicación puede recibir solicitudes de envío mediante:

🌐 API REST

📡 Kafka

📨 Envío mediante SMTP

💾 Persistencia en PostgreSQL

📦 Almacenamiento de archivos mediante MinIO

📎 Soporte para archivos adjuntos

👥 Gestión de múltiples destinatarios

🐳 Preparado para ejecución mediante Docker

---

# 🚀 Características principales

==============================

⚡ Backend con Spring Boot

☕ Java 21

🌐 API REST

📡 Integración con Apache Kafka

📨 Envío mediante SMTP

👥 Múltiples destinatarios

📄 Soporte para contenido HTML

📎 Archivos adjuntos

📦 Almacenamiento de adjuntos mediante MinIO

↩️ Reply-To

💾 Persistencia mediante PostgreSQL

📊 Estados de envío

❌ Registro de errores

📚 Swagger / OpenAPI

🐳 Docker

🔧 Configuración mediante variables de entorno

---

# 📨 Formas de envío

==================

Mail Service permite utilizar dos mecanismos diferentes para solicitar el envío de un correo.

## 🌐 Envío mediante REST

=====================

Los microservicios pueden realizar una petición HTTP directamente contra Mail Service.

Arquitectura:

```text
Microservicio
     │
     │ REST
     ▼
Mail Service
     │
     ├──► PostgreSQL
     │
     ├──► MinIO
     │
     └──► SMTP
              │
              ▼
        Servidor de correo
              │
              ▼
         Destinatario
```

Este mecanismo permite realizar envíos de forma síncrona y obtener la respuesta directamente desde la API.

---

## 📡 Envío mediante Kafka

=======================

Mail Service también puede recibir solicitudes de envío mediante Apache Kafka.

Arquitectura:

```text
Microservicio
     │
     │ Kafka
     ▼
  mail.send
     │
     ▼
Mail Service
     │
     ├──► PostgreSQL
     │
     ├──► MinIO
     │
     └──► SMTP
              │
              ▼
        Servidor de correo
              │
              ▼
         Destinatario
```

El productor publica un mensaje en el topic correspondiente y Mail Service procesa el evento mediante un consumidor Kafka.

Este mecanismo permite desacoplar el productor del proceso de envío y utilizar un flujo basado en mensajería.

---

# 🔄 Arquitectura general

========================

Las dos vías de entrada utilizan la misma lógica de negocio y terminan utilizando el mismo proceso de envío.

```text
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
          └──────────────┬──────────────┘
                         │
              ┌──────────┼──────────┐
              │          │          │
              ▼          ▼          ▼
         PostgreSQL    MinIO       SMTP
              │          │          │
              │          │          ▼
              │          │     Servidor correo
              │          │          │
              │          │          ▼
              │          │      Destinatario
              │          │
              │          └──► Archivos adjuntos
              │
              └──► Email + destinatarios
                   + metadata + estado
```

De esta forma, REST y Kafka actúan como diferentes mecanismos de entrada al mismo servicio.

---

# 📡 Kafka

=========

La integración con Kafka permite procesar solicitudes de envío de forma asíncrona.

El flujo básico es:

```text
Productor
   │
   ▼
 Kafka
   │
   │ mail.send
   ▼
Mail Service
   │
   ├──► PostgreSQL
   │
   ├──► MinIO
   │
   ▼
Procesamiento del email
   │
   ▼
 SMTP
```

Kafka permite desacoplar los microservicios productores del servicio responsable del envío de correo.

---

# 📎 Archivos adjuntos

====================

Mail Service permite enviar archivos adjuntos tanto mediante REST como mediante eventos Kafka.

En REST se utilizan peticiones `multipart/form-data`.

En Kafka, los eventos pueden incluir el contenido del adjunto codificado en Base64.

Ejemplo:

```text
📨 Email

├── 👥 Destinatarios
├── 📝 Asunto
├── 📄 Cuerpo
└── 📎 Adjuntos
      ├── documento.pdf
      └── imagen.png
```

Los archivos adjuntos **no se almacenan como datos binarios en PostgreSQL**.

El funcionamiento es:

```text
Adjunto
   │
   ▼
Mail Service
   │
   ├──────────────► MinIO
   │                  │
   │                  └──► Fichero
   │
   └──────────────► PostgreSQL
                      │
                      └──► Metadata + storage_key
```

PostgreSQL almacena la información necesaria para identificar el archivo, mientras que MinIO almacena el contenido binario.

Esto permite mantener la base de datos ligera y separar la persistencia de datos estructurados del almacenamiento de archivos.

---

# 📦 MinIO

=============

Mail Service utiliza MinIO como almacenamiento de objetos para los archivos adjuntos.

Los archivos se almacenan en un bucket específico:

```text
mail-attachments
```

Cada adjunto dispone de una clave única (`storage_key`) que permite localizar el objeto almacenado en MinIO.

La aplicación utiliza una abstracción de almacenamiento mediante `StorageService`, permitiendo desacoplar la lógica de negocio de la implementación concreta de almacenamiento.

Flujo:

```text
Mail Service
     │
     │ upload
     ▼
   MinIO
     │
     ▼
mail-attachments
     │
     └──► storage_key
```

Durante el envío del correo, Mail Service recupera el archivo desde MinIO y lo incorpora al mensaje SMTP.

---

# 🗄️ Persistencia

================

La aplicación utiliza PostgreSQL para almacenar la información relacionada con los correos.

Se almacenan principalmente:

📧 Información del email

👥 Destinatarios

📎 Metadata de los archivos adjuntos

🔑 Referencia `storage_key` de los adjuntos

📊 Estado del envío

🕐 Fechas de creación y envío

❌ Mensaje de error

El contenido binario de los archivos adjuntos se almacena en MinIO y no directamente en PostgreSQL.

La persistencia permite mantener un histórico de los correos procesados y realizar seguimiento de los envíos.

---

# 📊 Estados

==========

Los correos disponen de un estado que permite conocer el resultado del procesamiento.

Flujo básico:

```text
QUEUED
   │
   ▼
PROCESSING
   │
   ├──► SENT
   │
   └──► ERROR
```

Esto permite identificar los envíos realizados correctamente y aquellos que han producido un error.

---

# 📚 Swagger / OpenAPI

====================

La API REST dispone de documentación mediante Swagger / OpenAPI.

Una vez arrancada la aplicación puede accederse desde:

👉 http://localhost:8086/swagger-ui.html

---

# 🧩 Tecnologías

===============

Java 21              -> Lenguaje principal

Spring Boot          -> Framework backend

Spring Web           -> API REST

Spring Kafka         -> Integración con Kafka

Spring Mail          -> Envío SMTP

Spring Data JPA      -> Persistencia

Hibernate             -> ORM

PostgreSQL           -> Base de datos

MinIO                -> Almacenamiento de objetos

Lombok               -> Reducción de código repetitivo

Swagger / OpenAPI    -> Documentación API

Maven                -> Build tool

Docker               -> Deploy

---

# 🏗️ Arquitectura del proyecto

=============================

```text
mail-service/

├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/julian/mail_service/
│   │   │
│   │   ├── config/
│   │   │   └── MinioConfig
│   │   │
│   │   ├── controller/
│   │   │   └── EmailController
│   │   │
│   │   ├── service/
│   │   │   ├── EmailSenderService
│   │   │   ├── StorageService
│   │   │   └── impl/
│   │   │       ├── EmailSenderServiceImpl
│   │   │       └── MinioStorageServiceImpl
│   │   │
│   │   ├── kafka/
│   │   │   └── ...
│   │   │
│   │   ├── repository/
│   │   │   ├── EmailRepository
│   │   │   ├── EmailRecipientRepository
│   │   │   └── EmailAttachmentRepository
│   │   │
│   │   ├── entity/
│   │   │   ├── Email
│   │   │   ├── EmailRecipient
│   │   │   └── EmailAttachment
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
```

---

# 🔧 Componentes principales

==========================

### EmailController

* Gestiona las peticiones HTTP
* Recibe las solicitudes de envío REST
* Gestiona peticiones con y sin adjuntos

### Kafka Consumer

* Consume los mensajes publicados en Kafka
* Recibe las solicitudes de envío asíncronas
* Delega el procesamiento al servicio de correo

### EmailSenderService

* Define las operaciones del servicio de correo
* Centraliza la lógica de negocio
* Es utilizado tanto por REST como por Kafka

### EmailSenderServiceImpl

* Implementa la lógica de envío
* Gestiona la persistencia
* Procesa los correos
* Gestiona los archivos adjuntos
* Utiliza MinIO para el almacenamiento de archivos
* Actualiza el estado del envío

### StorageService

* Define la abstracción para el almacenamiento de archivos
* Permite subir, descargar y eliminar objetos

### MinioStorageServiceImpl

* Implementa `StorageService` utilizando MinIO
* Gestiona la subida de archivos
* Gestiona la descarga de archivos
* Gestiona la eliminación de objetos

### Repositories

* Gestionan el acceso a PostgreSQL mediante Spring Data JPA

### DTOs

* Definen los modelos de entrada y salida de la API y los mensajes utilizados durante el procesamiento

### GlobalExceptionHandler

* Centraliza la gestión de excepciones
* Devuelve respuestas de error controladas

---

# ⚙️ Configuración local

======================

Clonar el proyecto:

```bash
git clone https://github.com/jgf78/mail-service.git
cd mail-service
```

Configurar las variables de entorno necesarias para:

* SMTP
* PostgreSQL
* Kafka
* MinIO

Compilar:

```bash
mvn clean install -DskipTests
```

Arrancar la aplicación:

```bash
mvn spring-boot:run
```

La aplicación estará disponible en:

👉 http://localhost:8086

Swagger:

👉 http://localhost:8086/swagger-ui.html

---

# 🗄️ PostgreSQL

==============

La aplicación necesita una base de datos PostgreSQL.

Configuración mediante variables de entorno:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
```

Ejemplo:

```text
DB_HOST=localhost
DB_PORT=5432
DB_NAME=mailservice
DB_USERNAME=iagente
DB_PASSWORD=********
```

---

# 📦 MinIO

==========

La conexión con MinIO se configura mediante variables de entorno:

```text
MINIO_ENDPOINT
MINIO_ACCESS_KEY
MINIO_SECRET_KEY
MINIO_BUCKET
```

Ejemplo:

```text
MINIO_ENDPOINT=http://minio:9000
MINIO_ACCESS_KEY=mail-service
MINIO_SECRET_KEY=********
MINIO_BUCKET=mail-attachments
```

Las credenciales de MinIO no deben almacenarse directamente en el código fuente.

---

# 📨 Configuración SMTP

=====================

El servidor SMTP se configura mediante variables de entorno.

Ejemplo:

```text
MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
MAIL_SMTP_AUTH
MAIL_SMTP_STARTTLS_ENABLE
```

Las credenciales y parámetros de conexión no se almacenan directamente en el código fuente.

---

# 📡 Configuración Kafka

======================

La conexión con Kafka se configura mediante variables de entorno y propiedades de Spring Kafka.

El servicio utiliza Kafka para recibir solicitudes de envío mediante el topic:

```text
mail.send
```

El flujo de mensajería es:

```text
Productor
   │
   ▼
mail.send
   │
   ▼
Mail Service
   │
   ├──► PostgreSQL
   ├──► MinIO
   └──► SMTP
```

---

# 🐳 Docker

=========

La aplicación está preparada para ejecutarse mediante Docker.

Arquitectura:

```text
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
              ┌────────┼─────────┐
              │        │         │
              ▼        ▼         ▼
         PostgreSQL  MinIO      SMTP
                       │          │
                       │          ▼
                       │     Destinatario
                       │
                       └──► Adjuntos
```

La configuración de SMTP, PostgreSQL, Kafka y MinIO se proporciona mediante variables de entorno.

---

# 🔐 Gestión de errores

=====================

La aplicación dispone de un sistema centralizado de gestión de excepciones mediante `GlobalExceptionHandler`.

Se controlan, entre otros:

❌ Errores de validación

❌ Errores SMTP

❌ Peticiones incorrectas

❌ Archivos demasiado grandes

❌ Errores internos

Los errores producidos durante el envío pueden quedar registrados en la información del correo.

---

# 📈 Estado actual

================

Funcionalidades disponibles:

✅ API REST

✅ Integración con Kafka

✅ Envío SMTP

✅ Múltiples destinatarios

✅ Contenido HTML

✅ Reply-To

✅ Archivos adjuntos

✅ Almacenamiento de adjuntos mediante MinIO

✅ Persistencia PostgreSQL

✅ Metadata y referencias de almacenamiento mediante `storage_key`

✅ Estados de envío

✅ Registro de errores

✅ Swagger / OpenAPI

✅ Docker

Mail Service está diseñado como un componente reutilizable para otros microservicios que necesiten enviar correos electrónicos, permitiendo elegir entre comunicación REST o mensajería Kafka y utilizando MinIO para el almacenamiento de archivos adjuntos.

---

# 📄 Licencia

===========

MIT License — uso libre y modificación.

---

# 👤 Autor

========

Julián Gómez Fernández

💻 Java Developer

⚙️ Spring Boot · Java · Docker · REST

📡 Kafka

📨 Servicios de correo

🗄️ PostgreSQL

📦 MinIO

📡 Arquitectura de microservicios

---

# 🧠 Frase final

==============

> "REST cuando necesitas una respuesta.
>
> Kafka cuando necesitas desacoplar.
>
> MinIO cuando necesitas almacenar.
>
> Mail Service cuando necesitas enviar."
