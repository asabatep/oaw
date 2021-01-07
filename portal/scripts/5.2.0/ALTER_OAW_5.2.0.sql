DROP TABLE IF EXISTS `observatorio_extra_configuration`;
CREATE TABLE `observatorio_extra_configuration` ( 
	`id` INT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL ,
	`key` VARCHAR(255) NOT NULL , 
	`value` VARCHAR(255) NOT NULL , 
    PRIMARY KEY(`id`),
	UNIQUE (`key`)
);

INSERT INTO `observatorio_extra_configuration` (`name`, `key`, `value`) VALUES ('Umbral relanzamiento (% páginas)', 'umbral', '30');
INSERT INTO `observatorio_extra_configuration` (`name`, `key`, `value`) VALUES ('Timeout relanzamiento (ms)','timemout', '60');
INSERT INTO `observatorio_extra_configuration` (`name`, `key`, `value`) VALUES ('Anchura relanzamiento (niveles)','width', '10');
INSERT INTO `observatorio_extra_configuration` (`name`, `key`, `value`) VALUES ('Profundidad relanzamiento (niveles)','depth', '10');

