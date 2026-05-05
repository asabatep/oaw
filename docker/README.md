# Deployment Instructions

In this section, you will find the various instructions to start the application using Docker Compose.


## Deployment on Linux

The following instructions have been performed on Ubuntu 22.04. It's possible that on other distributions or versions of the same, some of these steps might be different or even unnecessary.


### 1. Prerequisites

To perform the following steps, you need to navigate to the `docker` folder and have the following software installed on your system:

* [Docker](https://docs.docker.com/get-started/overview/) 29.3.0
* [Docker Compose](https://docs.docker.com/compose/) 5.1.0
* [Git](https://git-scm.com/install/) 2.34.1

*Note: The specified versions are those with which the dockerized version has been developed. It might work with other versions.

### 2. Database Selection (Optional)

This step is not necessary unless you want to change the name or URL to which the database points.

The selection of the database is done within the specified profile. For deployment using Docker, the profile used is `docker`. You can find this profile in the path `portal/profiles/docker`, and the relevant file is `context.xml`.

Inside this file, you can find the `url` of the database that will be used.

```xml
<Context path="/oaw" reloadable="true">
	<Resource auth="Container" driverClassName="com.mysql.cj.jdbc.Driver"
	type="javax.sql.DataSource" name="jdbc/oaw" url="jdbc:mysql://mysql:3306/OAW"
	maxActive="100" maxIdle="10" maxWait="-1"
	validationQuery="SELECT 1 as dbcp_connection_test" removeAbandoned="true"
	testOnBorrow="true" timeBetweenEvictionRunsMillis="60000"
	testWhileIdle="true" defaultTransactionIsolation="READ_UNCOMMITTED"
	username="root" password="root" />
</Context>
```

### 3. Initializing the project and compiling the WAR files.

In the folder of the main proyect execute

```bash
git submodule update --init --recursive
./compile.sh
```

### 4. Optional: do this step if you changed the docker profile properties in the project before compiling.

This step copies the new properties to the `docker/oaw-properties` folder.

```bash
./updatePropertiesDocker.sh
```

**Note:** The default properties are located in the `docker/oaw-properties` folder. You can edit them here before starting the service (it's simpler). The most relevant properties file is `mail.properties`, which defines the mail service; by default, it uses the maildev service.

### 5. Startup

Once the `wars` files are generated, navigate to the `docker` folder located in the root directory and start the containers using `docker compose`.

```bash
cd docker
docker compose up -d --build
```

The database volume is located in the `/docker/volumes/mysql` folder. Remember that if this folder is present in the directory, MySQL will internally load the existing database and will not generate a new one from the SQL scripts.

To reset the database, simply delete the `/docker/volumes/mysql` folder.

**Note:** Do not confuse this folder with `docker/mysql`. The latter contains the necessary SQL scripts to generate the initial volume.

### 6. Checks


If all the steps have been executed correctly, you should find the deployed application at [http://localhost:7010/oaw](http://localhost:7010/oaw). The default user credentials are admin / admin.

Mail service is located at http://localhost:1080. You can view the emails sent by the service. Download the email and open it with another application (such as Thunderbird or Outlook) to see the attachments (ZIP files).

