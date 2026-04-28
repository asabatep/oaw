#!/bin/bash
YELLOW='\033[0;33m'     # Amarillo normal
RED='\033[0;31m'        # Rojo normal  
GREEN='\033[0;32m'      # Verde normal
NC='\033[0m'            # Normal/Reset (sin color)

destination_properties="./docker/oaw-properties"

echo -e "${YELLOW}++++ Verificando properties Oaw ++++${NC}"
#Ficheros de properties para validar su existencia y en caso negativo copiarlos a la ruta
properties="./crawler/target/classes/crawler.core.properties
./intavcore/target/classes/intav.properties
./intavcore/target/classes/cacheintav.properties
./portal/target/classes/basic.service.properties
./portal/target/classes/certificados.properties
./portal/target/classes/check.descriptions.properties
./portal/target/classes/check.patterns.properties
./portal/target/classes/crawler.properties
./portal/target/classes/log4j.properties
./portal/target/classes/mail.properties
./portal/target/classes/pdf.properties
./portal/target/classes/propertiesmanager.properties
./portal/target/classes/language.properties
./portal/target/classes/management.properties
./portal/target/classes/returnPaths.properties
./portal/target/classes/role.properties
./portal/target/classes/basic.service.default.properties"
for file in $properties; do
    base_file=${file##*/}
    destination_file="$destination_properties/${base_file}"
    
    if [ ! -e "$destination_file" ]; then
        if [ -e "$file" ]; then
            echo "Copiando $file a $destination_file..."
            cp "$file" "$destination_file"
            if [ $? -eq 0 ]; then
                echo -e "${GREEN}✓ ${base_file} copiado correctamente.${NC}"
            else
                echo -e "${RED}✗ Error al copiar ${base_file}${NC}"
            fi
        else
            echo -e "${RED}⚠ Fichero origen $file no encontrado.${NC}"
        fi
    else
        echo "✓ $destination_file ya existe."
    fi
done