INSERT INTO tguidelines (cod_guideline, des_guideline) VALUES (8, 'observatorio-une-2012-b.xml');
INSERT INTO cartucho (id_cartucho, nombre, instalado, aplicacion, numrastreos, numhilos, id_guideline) VALUES (8, 'es.inteco.accesibilidad.CartuchoAccesibilidad', 1, 'UNE-2012-B', 15, 50, 8);
INSERT INTO usuario_cartucho (id_usuario, id_cartucho) VALUES(1, 8);