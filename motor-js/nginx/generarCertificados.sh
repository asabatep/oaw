#!/bin/sh
CERT_DIR=/etc/nginx/ssl 
if [ -z "$CERT_DAYS" ]; then
    CERT_DAYS=9999
fi
if [ ! -f "$CERT_DIR/server.key" ] || [ ! -f "$CERT_DIR/server.crt" ]; then \
    echo 'Generando nuevos certificados SSL...'
    mkdir -p "$CERT_DIR" 
    openssl genpkey -algorithm RSA -out "$CERT_DIR/server.key" &&
    openssl req -new -key "$CERT_DIR/server.key" -x509 -days $CERT_DAYS -out "$CERT_DIR/server.crt" -subj '/C=ES/ST=Madrid/L=Madrid/O=Dev/CN=localhost'
    if [ $? -eq 0 ]; then
        echo 'Finalizada generación de certificados SSL correctamente.'
    fi
elif ! openssl x509 -checkend 86400 -noout -in "$CERT_DIR/server.crt" ; then 
    echo 'Certificado caducado (menos de 1 día), regenerando...' 
    rm "$CERT_DIR/server.key"
    rm "$CERT_DIR/server.crt"
    openssl genpkey -algorithm RSA -out "$CERT_DIR/server.key" &&
    openssl req -new -key "$CERT_DIR/server.key" -x509 -days $CERT_DAYS -out "$CERT_DIR/server.crt" -subj '/C=ES/ST=Madrid/L=Madrid/O=Dev/CN=localhost'
    if [ $? -eq 0 ]; then
        echo 'Finalizada regeneración de certificados SSL por caducidad.'
    fi
else
    echo 'Verficación correcta de certificados SSL'
fi

echo "Ejecutando: $@"
exec "$@"
