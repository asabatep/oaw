/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE `OAW`;

LOCK TABLES `ambitos_lista` WRITE;
/*!40000 ALTER TABLE `ambitos_lista` DISABLE KEYS */;
INSERT INTO `ambitos_lista` VALUES (1,'AGE','Administración General del Estado'),(2,'CCAA','Comunidades Autónomas'),(3,'EELL','Entidades Locales'),(4,'Otros','Otros');
/*!40000 ALTER TABLE `ambitos_lista` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `cartucho` WRITE;
/*!40000 ALTER TABLE `cartucho` DISABLE KEYS */;
INSERT INTO `cartucho` VALUES (1,'es.inteco.accesibilidad.CartuchoAccesibilidad',0,'UNE-2004',15,50,4),(2,'es.inteco.accesibilidad.CartuchoAccesibilidad',0,'UNE-2012',15,50,7),(8,'es.inteco.accesibilidad.CartuchoAccesibilidad',1,'UNE-2012-B',15,50,8),(9,'es.inteco.accesibilidad.CartuchoAccesibilidad',1,'UNE-EN301549:2019',15,50,9),(10,'es.inteco.accesibilidad.CartuchoAccesibilidad',1,'Accesibilidad',15,50,10);
/*!40000 ALTER TABLE `cartucho` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `clasificacion_etiqueta` WRITE;
/*!40000 ALTER TABLE `clasificacion_etiqueta` DISABLE KEYS */;
INSERT INTO `clasificacion_etiqueta` VALUES (1,'Temática'),(2,'Distribución'),(3,'Recurrencia'),(4,'Otros'),(5,'Tipo de Sitio Web');
/*!40000 ALTER TABLE `clasificacion_etiqueta` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `complejidades_lista` WRITE;
/*!40000 ALTER TABLE `complejidades_lista` DISABLE KEYS */;
INSERT INTO complejidades_lista VALUES(1, 'Baja', 4, 4),(2, 'Media', 4, 8),(3, 'Alta', 5, 10),(4, 'Única', 1, 1);
/*!40000 ALTER TABLE `complejidades_lista` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `etiqueta` WRITE;
/*!40000 ALTER TABLE `etiqueta` DISABLE KEYS */;
INSERT INTO `etiqueta` VALUES(1, 'Etiqueta - DEMO', 4);
/*!40000 ALTER TABLE `etiqueta` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `dependencia` WRITE;
/*!40000 ALTER TABLE `dependencia` DISABLE KEYS */;
INSERT INTO `dependencia` VALUES(1, 'Dependencia - DEMO', '', 1, 1, NULL, 'D-DEMO');
/*!40000 ALTER TABLE `dependencia` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `dependencia_ambito` WRITE;
/*!40000 ALTER TABLE `dependencia_ambito` DISABLE KEYS */;
INSERT INTO `dependencia_ambito` VALUES(1, 1, 4);
/*!40000 ALTER TABLE `dependencia_ambito` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `categorias_lista` WRITE;
/*!40000 ALTER TABLE `categorias_lista` DISABLE KEYS */;
INSERT INTO `categorias_lista` VALUES(110, 'Segmento - Demo', 1, 'Seg-Demo', 0);
/*!40000 ALTER TABLE `categorias_lista` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `lista` WRITE;
/*!40000 ALTER TABLE `lista` DISABLE KEYS */;
INSERT INTO `lista` VALUES(1, 4, 'Semilla - DEMO', 'https://asturias.es', 110, 'S-DEMO', NULL, 1, 0, 0, 4, 4, 'Semilla de demostración');
/*!40000 ALTER TABLE `lista` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `semilla_dependencia` WRITE;
/*!40000 ALTER TABLE `semilla_dependencia` DISABLE KEYS */;
INSERT INTO `semilla_dependencia` VALUES(1, 1);
/*!40000 ALTER TABLE `semilla_dependencia` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `semilla_etiqueta` WRITE;
/*!40000 ALTER TABLE `semilla_etiqueta` DISABLE KEYS */;
INSERT INTO `semilla_etiqueta` VALUES(1, 1);
/*!40000 ALTER TABLE `semilla_etiqueta` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `observatorio` WRITE;
/*!40000 ALTER TABLE `observatorio` DISABLE KEYS */;
INSERT INTO `observatorio` VALUES(16, 'Observatorio - DEMO', 1, 1, 1, NOW(), 9, 1, 1, 9, 1, 4, 4, '1');
/*!40000 ALTER TABLE `observatorio` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `observatorio_categoria` WRITE;
/*!40000 ALTER TABLE `observatorio_categoria` DISABLE KEYS */;
INSERT INTO `observatorio_categoria` VALUES(16, 110);
/*!40000 ALTER TABLE `observatorio_categoria` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `languages` WRITE;
/*!40000 ALTER TABLE `languages` DISABLE KEYS */;
INSERT INTO `languages` VALUES (1,'idioma.espanol','es'),(2,'idioma.ingles','en');
/*!40000 ALTER TABLE `languages` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `observatorio_proxy` WRITE;
/*!40000 ALTER TABLE `observatorio_proxy` DISABLE KEYS */;
INSERT INTO `observatorio_proxy` VALUES (1,'oaw-dev-proxy','7013');
/*!40000 ALTER TABLE `observatorio_proxy` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `observatorio_validator` WRITE;
/*!40000 ALTER TABLE `observatorio_validator` DISABLE KEYS */;
INSERT INTO `observatorio_validator` VALUES (1,'http://validador-tomcat:8080/api/validation-request/tracker', 1, 10);
/*!40000 ALTER TABLE `observatorio_validator` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `observatorio_tipo` WRITE;
/*!40000 ALTER TABLE `observatorio_tipo` DISABLE KEYS */;
INSERT INTO `observatorio_tipo` VALUES (1,'AGE'),(2,'CCAA'),(3,'EELL'),(4,'OTROS');
/*!40000 ALTER TABLE `observatorio_tipo` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `periodicidad` WRITE;
/*!40000 ALTER TABLE `periodicidad` DISABLE KEYS */;
INSERT INTO `periodicidad` VALUES (1,'Diario',1,NULL),(2,'Semanal',7,NULL),(3,'Quincenal',15,NULL),(4,'Mensual',NULL,'0 min hour daymonth month/1 ? year/1'),(5,'Trimestral',NULL,'0 min hour daymonth month/3 ? year/1'),(6,'Semestral',NULL,'0 min hour daymonth month/6 ? year/1'),(7,'Anual',NULL,'0 min hour daymonth month ? year/1');
/*!40000 ALTER TABLE `periodicidad` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'Administrador',1),(2,'Configurador',1),(3,'Visualizador',1),(4,'Responsable cliente',2),(5,'Visualizador cliente',2),(6,'Observatorio',3);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `tguidelines` WRITE;
/*!40000 ALTER TABLE `tguidelines` DISABLE KEYS */;
INSERT INTO `tguidelines` VALUES (4,'observatorio-inteco-1-0.xml'),(7,'observatorio-une-2012.xml'),(8,'observatorio-une-2012-b.xml'),(9,'observatorio-une-en2019.xml'),(10,'observatorio-accesibilidad.xml');
/*!40000 ALTER TABLE `tguidelines` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `tipo_lista` WRITE;
/*!40000 ALTER TABLE `tipo_lista` DISABLE KEYS */;
INSERT INTO `tipo_lista` VALUES (1,'semilla'),(2,'lista_rastreable'),(3,'lista_no_rastreable'),(4,'semilla_observatorio');
/*!40000 ALTER TABLE `tipo_lista` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `tipo_rol` WRITE;
/*!40000 ALTER TABLE `tipo_rol` DISABLE KEYS */;
INSERT INTO `tipo_rol` VALUES (1,'Normal'),(2,'Cliente'),(3,'Observatorio');
/*!40000 ALTER TABLE `tipo_rol` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'admin','21232f297a57a5a743894a0e4a801fc3','Administrador','','','ejemplo@ejemplo.org'),(2,'Programado','21232f297a57a5a743894a0e4a801fc3','','','','ejemplo@ejemplo.org');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `usuario_cartucho` WRITE;
/*!40000 ALTER TABLE `usuario_cartucho` DISABLE KEYS */;
INSERT INTO `usuario_cartucho` VALUES (1,1),(1,2),(1,8),(1,9),(1,10);
/*!40000 ALTER TABLE `usuario_cartucho` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `usuario_rol` WRITE;
/*!40000 ALTER TABLE `usuario_rol` DISABLE KEYS */;
INSERT INTO `usuario_rol` VALUES (1,1),(2,1);
/*!40000 ALTER TABLE `usuario_rol` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `observatorio_range` WRITE;
/*!40000 ALTER TABLE `observatorio_range` DISABLE KEYS */;
INSERT INTO observatorio_range VALUES
(1, 'Mantiene', -0.5, 0.5, '<=', '<=', 3, '#c0c0c0'),
(2, 'Mejora', 0.5, 2.5, '<', '<', 4, '#5ebe41'),
(3, 'Mejora mucho', 2.5, NULL, '<=', '', 5, '#007700'),
(4, 'Empeora', -2.5, -0.5, '<', '<', 2, '#cf7b30'),
(5, 'Empeora mucho', NULL, -2.5, '', '<=', 1, '#ff0000');
/*!40000 ALTER TABLE `observatorio_range` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `rastreo` WRITE;
/*!40000 ALTER TABLE `rastreo` DISABLE KEYS */;
INSERT INTO `rastreo` VALUES(1182, 'Observatorio - DEMO-Semilla - DEMO', NOW(), 1, 1, 1, NOW(), NULL, NULL, 1, NULL, 16, 9, 1, 1, 1, 1, 0, 0);
/*!40000 ALTER TABLE `rastreo` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `cartucho_rastreo` WRITE;
/*!40000 ALTER TABLE `cartucho_rastreo` DISABLE KEYS */;
INSERT INTO `cartucho_rastreo` VALUES (9,1182);
/*!40000 ALTER TABLE `cartucho_rastreo` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

INSERT INTO tguidelines values (11, 'observatorio-une-en2019_pdf.xml');
INSERT INTO cartucho values (11, 'es.inteco.accesibilidad.CartuchoAccesibilidad', 1 , 'UNE-EN-2019-PDF', 15, 50, 11);

/*Caruchos sin comprobación de enlaces rotos*/
insert into cartucho(nombre, instalado, aplicacion,numrastreos,numhilos,id_guideline) values ('es.inteco.accesibilidad.CartuchoAccesibilidad',1,'UNE-EN301549:2019_NO_BROKEN_LNK',15,50,12);
insert into cartucho(nombre, instalado, aplicacion,numrastreos,numhilos,id_guideline) values ('es.inteco.accesibilidad.CartuchoAccesibilidad',1,'UNE-EN-2019-PDF_NO_BROKEN_LNK',15,50,13);
insert into tguidelines(cod_guideline,des_guideline) values (12, 'observatorio-une-en2019-nobroken.xml');
insert into tguidelines(cod_guideline,des_guideline) values (13, 'observatorio-une-en2019_pdf-nobroken.xml');
