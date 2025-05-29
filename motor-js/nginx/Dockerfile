FROM alpine:3.21
RUN apk add --no-cache nginx openssl && \
    mkdir -vp /etc/nginx/ssl && \
    cd /etc/nginx/ssl && \
    openssl genrsa -out server.key 2048 && \
    openssl req -new -key server.key -out server.csr -subj "/C=ES/ST=ZZ/L=ZZ/O=oaw/OU=oaw/CN=localhost" && \
    openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt && \
    rm server.csr
COPY reverse.conf /etc/nginx/http.d/default.conf
COPY nginx-entrypoint.sh /nginx-entrypoint.sh
ENTRYPOINT /nginx-entrypoint.sh
