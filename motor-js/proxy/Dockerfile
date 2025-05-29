FROM node:20
WORKDIR /opt/proxy
RUN npm cache clean --force && npm install request regex --save
COPY index.js /opt/proxy/index.js
ENTRYPOINT node /opt/proxy/index.js
