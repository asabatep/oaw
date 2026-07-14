UPDATE tanalisis_css tc
  JOIN tanalisis t                ON t.cod_analisis = tc.cod_analisis
  JOIN rastreos_realizados rr     ON rr.id          = t.cod_rastreo
  JOIN observatorios_realizados o ON o.id           = rr.id_obs_realizado
SET tc.codigo = NULL
WHERE o.fecha < NOW() - INTERVAL 1 MONTH
  AND tc.codigo IS NOT NULL;

UPDATE tanalisis t
  JOIN rastreos_realizados rr     ON rr.id = t.cod_rastreo
  JOIN observatorios_realizados o ON o.id  = rr.id_obs_realizado
SET t.cod_fuente = NULL
WHERE o.fecha < NOW() - INTERVAL 1 MONTH
  AND t.cod_fuente IS NOT NULL;

OPTIMIZE TABLE tanalisis_css;
OPTIMIZE TABLE tanalisis;
