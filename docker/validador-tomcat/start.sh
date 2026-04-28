#!/bin/bash
export TOMCAT_HOME=/usr/local/tomcat
export HOME=/usr/local/newValidator

YELLOW='\033[0;33m'
NC='\033[0m'

WAR_FILE=$(ls -t /usr/local/tomcat/target/validador_oaw-*.war | head -n 1)
cp "$WAR_FILE" /usr/local/tomcat/webapps/ROOT.war

echo -e "${YELLOW}++++ Arrancando Tomcat${NC}"
$TOMCAT_HOME/bin/startup.sh
sleep 20

sleep 60
echo -e "${YELLOW}++ Ya puede acceder al Nuevo Validador {NC}"

tail -f /usr/local/tomcat/logs/catalina.out
