DROP DATABASE IF EXISTS `zk_with_mvc`;
CREATE DATABASE IF NOT EXISTS `zk_with_mvc`

DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

USE zk_with_mvc; 

CREATE TABLE IF NOT EXISTS `COUNTRIES`
(
    `country_id` INT NOT NULL AUTO_INCREMENT,
    `country_name` VARCHAR(250) NOT NULL,
    UNIQUE(country_name),
    PRIMARY KEY(country_id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `CITIES`
(
    `city_id` INT NOT NULL AUTO_INCREMENT,
    `country_id` INT NOT NULL,
    `city_name` VARCHAR(250) NOT NULL,
    UNIQUE(city_name),
    PRIMARY KEY(city_id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE `CITIES` ADD CONSTRAINT `fk_country_id` FOREIGN KEY (`country_id`) REFERENCES `COUNTRIES` (`country_id`);
