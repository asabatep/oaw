#!/bin/sh
set -eu

: "${DB_HOST:=oawdb}"
: "${DB_USER:=oawdb}"
: "${DB_PASS:=oawdb}"
: "${DB_NAME:=oawdb}"

umask 077
cat > /root/.my.cnf <<EOF
[client]
host=${DB_HOST}
user=${DB_USER}
password=${DB_PASS}
database=${DB_NAME}
EOF

exec crond -f -l 6
