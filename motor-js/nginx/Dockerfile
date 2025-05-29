FROM alpine:3.21
RUN apk add --no-cache nginx
COPY reverse.conf /etc/nginx/http.d/default.conf
COPY nginx-entrypoint.sh /nginx-entrypoint.sh
ENTRYPOINT /nginx-entrypoint.sh
