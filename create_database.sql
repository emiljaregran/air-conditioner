CREATE DATABASE air_conditioners;
USE air_conditioners;

CREATE TABLE dim_aircons (
	id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(30) NOT NULL,
	temperatureUnit VARCHAR(2) NOT NULL,
	electricityPriceUnit VARCHAR(3) NOT NULL
);

CREATE TABLE dim_date (
	id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    year SMALLINT UNSIGNED NOT NULL,
    month SMALLINT UNSIGNED NOT NULL,
    day SMALLINT UNSIGNED NOT NULL
);

CREATE TABLE dim_time (
	id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    hour SMALLINT UNSIGNED NOT NULL
);

INSERT INTO dim_aircons (name, temperatureUnit, electricityPriceUnit) 
	VALUES("A", "°C", "SEK");
                   
INSERT INTO dim_aircons (name, temperatureUnit, electricityPriceUnit) 
	VALUES("B", "°C", "SEK");

INSERT INTO dim_aircons (name, temperatureUnit, electricityPriceUnit) 
	VALUES("C", "°C", "SEK");

# Populates dim_date with values
DELIMITER //
CREATE PROCEDURE fill_dim_date(IN startdate DATE, IN stopdate DATE)
BEGIN
    DECLARE currentdate DATE;
    SET currentdate = startdate;
    WHILE currentdate < stopdate DO
        INSERT INTO dim_date (year, month, day) 
        VALUES (
				YEAR(currentdate),
                MONTH(currentdate),
                DAY(currentdate));
        SET currentdate = ADDDATE(currentdate,INTERVAL 1 DAY);
    END WHILE;
END
//
DELIMITER ;

CALL fill_dim_date('2019-01-01','2029-01-01');

# Populates dim_time with values
DELIMITER //
CREATE PROCEDURE fill_dim_time()
BEGIN
    DECLARE currenthour SMALLINT;
	SET currenthour = 0;
	WHILE currenthour < 24 DO
		INSERT INTO dim_time (hour)
        VALUES (currenthour);
        SET currenthour = currenthour + 1;
	END WHILE;
END
//
DELIMITER ;

CALL fill_dim_time();

CREATE TABLE fact_readings (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    airconId SMALLINT UNSIGNED NOT NULL,
    dateId SMALLINT UNSIGNED NOT NULL,
    timeId SMALLINT UNSIGNED NOT NULL,
	temperature FLOAT NOT NULL,
    powerConsumption MEDIUMINT UNSIGNED NOT NULL,
    electricityPrice FLOAT NOT NULL,
    PRIMARY KEY (id, airconId, dateId, timeId),
    FOREIGN KEY (airconId) REFERENCES dim_aircons(id),
    FOREIGN KEY (dateId) REFERENCES dim_date(id),
    FOREIGN KEY (timeId) REFERENCES dim_time(id)
);