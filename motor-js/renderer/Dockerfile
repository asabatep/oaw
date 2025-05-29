FROM alpine:3.21

COPY server.js /opt/server.js
RUN apk add --no-cache nodejs npm chromium harfbuzz nss freetype ttf-freefont && \
    addgroup -S node && adduser -S -G node node && \
    chown -Rv node:node /opt
USER node
WORKDIR /opt
RUN npm install --save prerender
CMD node /opt/server.js
