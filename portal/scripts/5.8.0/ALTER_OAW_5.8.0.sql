INSERT INTO tguidelines values (11, 'observatorio-une-en2019_pdf.xml');
INSERT INTO cartucho values (11, 'es.inteco.accesibilidad.CartuchoAccesibilidad', 1 , 'UNE-EN-2019-PDF', 15, 50, 11);

CREATE TABLE `observatorio_validator` (
  `status` tinyint(1) NOT NULL,
  `url` varchar(1024) NOT NULL,
  `pdf_active` tinyint(1) NOT NULL,
  `pdf_percentage` int (3) NOT NULL
);
INSERT INTO `observatorio_validator` VALUES (1,'https://pre-validador-oaw.redsara.es/api/validation-request/tracker', 1, 10);


