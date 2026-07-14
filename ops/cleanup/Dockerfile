FROM docker.io/alpine
RUN apk add --no-cache mariadb-client
COPY cleanup.sql /cleanup.sql
COPY crontab /etc/crontabs/root
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
