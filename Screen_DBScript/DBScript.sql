// postgresql

select * from patient
select * from clinicaldata

-----------------------------

CREATE DATABASE clinicals;

CREATE TABLE patient (
    id SERIAL PRIMARY KEY,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    age INT
);

CREATE TABLE clinicaldata (
    id SERIAL PRIMARY KEY,
    patient_id INT REFERENCES patient(id),
    component_name VARCHAR(255) NOT NULL,
    component_value VARCHAR(255) NOT NULL,
    measured_date_time TIMESTAMP
);


INSERT INTO patient (last_name, first_name, age) VALUES
('John','Mccain',52),
('Siva','Shankar',32),
('Anthony','Simon',22),
('Bruce','Sanhurst',33),
('Abhram','Mani',55),
('Gandhi','Singh',12),
('Antti','Krovinan',27),
('Simba','White',24),
('Rose','Tanic',29),
('Rowling','Lte',49);


INSERT INTO clinicaldata (patient_id, component_name, component_value, measured_date_time) VALUES
(1,'bp','67/119','2018-07-09 19:34:24'),
(2,'bp','63/115','2018-06-19 19:34:24'),
(3,'bp','72/129','2018-07-26 19:34:24'),
(4,'bp','74/139','2018-08-03 19:34:24'),
(5,'bp','67/119','2018-08-29 19:34:24'),
(6,'bp','62/109','2018-07-12 19:34:24'),
(7,'bp','55/102','2018-06-13 19:34:24'),
(8,'bp','47/90','2018-08-02 19:34:24'),
(9,'bp','90/149','2018-06-01 19:34:24'),
(10,'bp','50/109','2018-07-09 19:34:24');


ALTER TABLE clinicaldata
ALTER COLUMN component_value TYPE INT
USING component_value::INT;

DROP TABLE IF EXISTS clinicaldata;
DROP TABLE IF EXISTS patient;


DROP DATABASE clinicals;

