# 🚀 OAW (Observatorio de Accesibilidad Web)

A web accessibility tracker and analysis platform.

## ⚡ Quick Start

On a machine with Docker and Docker Compose installed, clone this repository and run:

```bash
docker-compose up -d
```

Please be patient—the build process and initial database population may take several minutes.

Once the application is running, access it at: [http://localhost:8080/oaw](http://localhost:8080/oaw)  
Login with:

- **Username:** `admin`
- **Password:** `changeme`

> ⚠️ You should change the password on your first login.

### 📧 Email Interception with Mailpit

Emails sent by the application are intercepted by [Mailpit](https://github.com/axllent/mailpit) and can be viewed at:  
[http://localhost:8025](http://localhost:8025)

To enable actual email sending:
- Remove the `ports` configuration from the `mailpit` service in `docker-compose.yml`.
- Configure environment variables as needed for either:
  - [SMTP Forwarding](https://mailpit.axllent.org/docs/configuration/smtp-forward/)
  - [SMTP Relaying](https://mailpit.axllent.org/docs/configuration/smtp-relay/)

### 🛠️ Technology Stack

The Docker containers include:

- **OS:** [Rocky Linux 9](https://rockylinux.org/)
- **Java:** [OpenJDK 8](https://openjdk.org/projects/jdk8/)
- **Maven:** [Maven 3](https://maven.apache.org/)
- **Tomcat:** [Tomcat 9](https://tomcat.apache.org/)
- **Database:** [MariaDB 11.4](https://mariadb.org/)
- **Proxy Renderer:** [Node.js 20](https://nodejs.org/)

---

## 🧩 Components

This repository includes three main applications:

- **OAW:** Java web application.
- **Motor JS:** Proxy-based rendering tool built on [Prerender](https://github.com/prerender/prerender).
- **WCAG EM Tool:** Custom fork of the [WCAG-EM Report Tool](https://github.com/w3c/wcag-em-report-tool) with export support for ODS format.

---

### 💻 OAW (Java Web Application)

The OAW application is structured into several Maven modules:

- `common`: Common utility functions.
- `crawler`: Web crawler implementation.
- `intavcore`: Core analysis engine.
- `oaw`: Parent project for building the full system.
- `portal`: Web front-end for the Accessibility Observatory.

#### 🏗️ Build Instructions

1. Install Java 8 and Maven 3.
2. Update the following configuration files to match your environment:

   - `oaw/pom.xml` — Path to Tomcat installation.
   - `portal/profiles/desarrollo/context.xml` — Database connection settings.
   - `portal/profiles/desarrollo/mail.properties` — Mail configuration.

3. Compile the project:

```bash
mvn clean install -P desarrollo -DskipTests
```

The resulting `.war` file will be located in the `portal/target` directory.

---

### 🔄 Motor JS (Proxy Rendering Service)

The `motor-js` directory contains a three-part proxy rendering system:

- `proxy`: Entry point that listens for HTTP/S requests.
- `nginx`: Forwards incoming requests to the rendering service and handles HTTPS.
- `renderer`: Runs [Prerender](https://github.com/prerender/prerender) to render requested web pages and return the HTML content.

---

### 📊 WCAG EM Tool (Customized Report Tool)

The `wcagemtool` directory contains a custom version of the [WCAG-EM Report Tool](https://github.com/w3c/wcag-em-report-tool), modified to allow exporting results in a custom ODS (OpenDocument Spreadsheet) format.
