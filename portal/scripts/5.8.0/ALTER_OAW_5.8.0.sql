INSERT INTO tguidelines values (11, 'observatorio-une-en2019_pdf.xml');
INSERT INTO cartucho values (11, 'es.inteco.accesibilidad.CartuchoAccesibilidad', 1 , 'UNE-EN-2019-PDF', 15, 50, 11);
INSERT INTO usuario_cartucho(1,11);

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `observatorio_validator` (
  `status` tinyint(1) NOT NULL,
  `url` varchar(64) NOT NULL,
  `pdf_active` tinyint(1) NOT NULL,
  `pdf_percentage` int (3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `observatorio_validator` VALUES (1,'https://des-validador-oaw.redsara.es/api/validation-request/tracker', 1, 10);

ALTER TABLE `rastreos_realizados`
ADD `score_html` varchar(32) COLLATE utf8_bin DEFAULT NULL,
ADD `score_pdf` varchar(32) COLLATE utf8_bin DEFAULT NULL;