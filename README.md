clinicals-project

I am a java developer. I want to create a application that captures the patient and his clinical data. 
Suggest a good tech stack for rest apis and single page frontend.

How can i create a clinicalapi spring boot project while using postgres sql database to save patient and his clinical data.

## Step 1: Create the project

VSCODE 
-> Create Java Project 
-> Spring Boot (Provided by Spring initializer Java Support)
-> Maven (Project Type)
-> 3.2.2 (version)
-> Java  (programming language using)
-> com.uday.patientclinicals.clinicalsapi
-> Jar (package type)
-> 25 (java version)
-> pick dependencies
    -> Spring Web (Web)
    -> Spring Data JPA (SQL)
    -> PostSQL Driver (SQL)
-> Choose Project folder: myjavaroot\PracticeApp2\clinicalapi
-> Project created -> Click on "open".

## Step 2: Take clinicals.sql execute in local database.

create database clinicals

create table patient 
create table clinicaldata

insert sample data into patient
insert sample data into clinicaldata

drop table
drop database 

## Step 3: add models, repository and controller using git co-pilot

src
>main>java>com>uday>patientclinicals>clinicalsapi
>ClinicalsApiApplication.java

## Create Models
new folder> models
new file> Patient.java

Patient.java
-------------
patient jpa model class with first name, last name, and age.

(OR)

patient jpa model class with primary key id, first name, last name, and age.

Add new file
ClinicalData.java

clinical data jpa model class with component name, component value and measured date time

Timestamp (java.sql)

# Foreign key relation on Model.

Patient.java
-------------
Patient -> OneToMany: ClinicalData 
import Jakarta.Persistent - fetch.

@OneToMany(mappedBy = "patient", casade= CascadeType.ALL,fetch = FetchType.EAGER)
private List<ClinicalData> clinicalData; 


ClinicalData.java
----------------------------

@Entity
@Table(name= "clinicaldata")
public class ClinicalData {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "patient_id",nullable=false)
    @JsonIgnore
    private Patient patient;

}


## Step 4: Create repo (repository) for the Models.

Create New folder: repos
> main>java>com>uday>patientclinicals>clinicalsapi
> repos

models.Patient.java -> patient jpa repository that uses patient model
models.clinicalData.java -> clinical data jpa repository that uses the clinical data model


main>java>com>uday>patientclinicals>clinicalsapi>repos>
patientRepository.java 

package com...;
import com..EntityClass;
@Repository
public class EntityClassRepository 
extends 
JpaRepository<EntityClass, Long>
{
    // generate code
}

similarly 
main>java>com>uday>patientclinicals>clinicalsapi>repos>
ClinicalDataRepository.java


## Step 5: Create Controller (model,repository,controller) for the Models/Repository.

patient rest controller that uses the patient repository and exposes a rest api to perform create, read, update and delete operations

src\main\java\com\uday\patientclinical\clinicalsapi\clinicalapi\controllers\PatientController.java


clinical data rest controller that uses the clinical data repository and exposes a rest api to perform create, read, update and delete operations.

src\main\java\com\uday\patientclinical\clinicalsapi\clinicalapi\controllers\ClinicalDataController.java

Errors/Exception and solutions:
----------------------------------

1) Could not obtain connection to query JDBC database metadata
   database name in appsettings.json
2) java -> jakarta import
3) Web server failed to start. Port 8080 was already in use.
   Change port from 8080 to 8081
4)  HHH100046: Could not obtain connection to query JDBC database metadata
The database clinicaldb doesn't exist in PostgreSQL yet. You need to execute the SQL script to create it. Let me help you run the script:


## Step 6: 
update the appsettings.json
spring.application.name=clinicalapi
spring.datasource.url=jdbc:postgresql://localhost:5432/clinicals
spring.datasource.username=test
spring.datasource.password=test
server.servlet.context-path=/patientservices

## Step 7: Run the application
Run the application using the main method in ClinicalsApiApplication.java. 
You can use an IDE like VScode, IntelliJ or Eclipse to run the application.

## Step 8: sample screenshots of postman to test the rest api
1) Create a patient
2) Get all patients
3) Get all clinical data for a patient