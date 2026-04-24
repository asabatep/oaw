1. Exportación de Plantillas para nuevas Instalaciones
    • Carga manual de plantillas actualizadas en el Servicio de Diagnóstico
    • Ejecución del script: `./scripts/ejecutarContenedorMysql.sh`. Genera archivos **.sql** en la ruta actual de ejecución del script. 
    • ✅Copiar los archivos **.sql.. a /sqls. 
2. Carga Automática en Inicialización.
    • ✅ Al arrancar el contenedor de mysql se cargarán las plantillas, si es la primera vez que se ejecuta el contenedor

🚀 Flujo Completo (Resumen)
    A. Plantillas actualizadas Carga manual en app
    B. Ejecutar ./scripts/ejecutarContenedorMysql.sh
    C. Mover ficheros generados *.sql a sqls
    D. MySQL inicia contenedor y ejecuta sqls (sólo primera instalación)
    E. Plantillas recreadas

📂 Estructura de Archivos final
text
sqls/
├── 03_plantilla_hallazgos.sql
├── 04_plantilla_Segmentos_v8.sql
├── 05_plantilla_Informe_global_v1_34_Accesible.sql
└── .....