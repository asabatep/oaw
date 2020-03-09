CREATE TABLE tanalisis_accesibilidad ( 
	id INT NOT NULL AUTO_INCREMENT , 
	id_analisis INT NOT NULL , 
	urls VARCHAR(2048) NOT NULL , 
	PRIMARY KEY (id)
);

ALTER TABLE basic_service ADD filename VARCHAR(1024) NULL;
ALTER TABLE etiqueta ADD UNIQUE(nombre);
ALTER TABLE categoria CHANGE categoria categoria VARCHAR(256);

INSERT INTO clasificacion_etiqueta (id_clasificacion, nombre) VALUES ('4', 'Otros');
