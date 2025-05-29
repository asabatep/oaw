#!/bin/sh

if ! test -f /etc/nginx/ssl/server.crt ; then
    mkdir -vp /etc/nginx/ssl
    openssl genrsa -out server.key 2048
    openssl req -new -key server.key -out server.csr -subj "/C=ES/ST=ZZ/L=ZZ/O=oaw/OU=oaw/CN=localhost"
    openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt
fi

exec nginx -g "daemon off;"
