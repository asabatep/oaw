
#!/bin/bash
docker run -it --rm -v "$(pwd)":/app/src -w /app/src kajum/oaw_maven_bash:latest -c "cd oaw && mvn clean install -Pdocker -DskipTests"
docker run -it --rm -v "$(pwd)/validador-oaw":/app/src -w /app/src kajum/oaw_maven_bash:latest -c "mvn clean install"