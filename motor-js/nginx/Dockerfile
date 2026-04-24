FROM nginx:1.13.8-alpine

# Instala openssl y herramientas básicas
RUN apk add --no-cache openssl

# Copiamos la configuración de lo que debe hacer nginx
COPY reverse.conf /etc/nginx/conf.d/default.conf

# Genera o regenera certificados si están caducados
COPY generarCertificados.sh /app/generarCertificados.sh 
RUN chmod 755 /app/generarCertificados.sh 

ENTRYPOINT ["/app/generarCertificados.sh"]

# Comando principal para arrancar el nginx
CMD ["nginx", "-g", "daemon off;"]