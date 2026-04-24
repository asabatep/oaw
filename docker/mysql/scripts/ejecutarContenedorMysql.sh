#!/bin/bash
containerName="oaw-dev-mysql"
docker cp exportar_plantillas.sh $containerName:/tmp/exportar_plantillas.sh 2>/dev/null
if [ $? -eq 0 ]; then
    echo "Copiado script $0 al contenedor $containerName"
fi
docker exec "$containerName" chmod +x /tmp/exportar_plantillas.sh
docker exec -it -w /tmp $containerName /tmp/exportar_plantillas.sh
docker cp "$containerName:/tmp/exports_plantillas/." . 2>/dev/null
if [ $? -ne 0 ]; then
    echo "Error al copiar los ficheros del $containerName" 
fi 
