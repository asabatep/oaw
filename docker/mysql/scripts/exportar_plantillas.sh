#!/bin/bash

# Lista de nombres de plantillas
plantillas=(
    "hallazgos"
    "Segmentos_v8"
    "Informe_global_v1_34_Accesible"
    "Generica_v12"
    "Complejidades_v8"
    "Evol_Segmentos_v6"
    "Informe_global_v1_4_CON_PDF"
    "GENERICA_CON_PDF"
    "Generica_Declaracion_Accesibildad"
    "IRA (ODS) - protegido"
    "IRA (XLSX) - desprotegido"
)

directorioSalida="/tmp/exports_plantillas"
mkdir -p $directorioSalida

# Contador de archivos (empieza en 3)
contador=3

# Iterar sobre cada plantilla
for nombre in "${plantillas[@]}"; do

    #Al nombre del fichero de salida le vamos a quitar los espacios
    nombreSinEspacios="${nombre// /}"
    # Nombre del fichero (03, 04, 05...)
    fichero=$(printf "%02d_plantilla_%s.sql" "$contador" "$nombreSinEspacios")
    #Si existe el fichero lo borramos
    rm -rf "$directorioSalida/$fichero" 2>/dev/null
    echo "-- Establecemos la BD OAW en la que debe cargar el insert" > $directorioSalida/"$fichero" 
    echo "use OAW;" >> $directorioSalida/"$fichero" 
    
    # Comando mysqldump
    mysqldump -u root -proot \
        --no-create-info --hex-blob \
        OAW observatorio_plantillas \
        --where="nombre='$nombre'" \
        >> $directorioSalida/"$fichero" 2>/dev/null
    
    echo "Generado: $fichero (Plantilla='$nombre')"
    
    # Incrementar contador
    ((contador++))
done

echo "Todas las plantillas exportadas."