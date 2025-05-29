#!/bin/sh

mkdir -vp /srv/files
chown -Rv tomcat:tomcat /srv/files

rm -vf /etc/tomcat/conf.d/zzz-local.conf

if ! test -z "$JAVA_OPTS" ; then
    echo "JAVA_OPTS=\"\$JAVA_OPTS $JAVA_OPTS\"" > /etc/tomcat/conf.d/zzz-local.conf
fi

runuser -u tomcat /usr/libexec/tomcat/server start
