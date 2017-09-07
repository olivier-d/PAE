/*
Nom : demo.sql
Script SQL afin d'initialiser la DB pour la démo
*/

---------------------------DROP SCHEMA------------------------------
DROP SCHEMA IF EXISTS pae CASCADE;


--------------------------CREATE SCHEMA-----------------------------
CREATE SCHEMA pae;


-------------------------CREATE SEQUENCE----------------------------
CREATE SEQUENCE pae.pk_utilisateur;
CREATE SEQUENCE pae.pk_entreprise;
CREATE SEQUENCE pae.pk_journee;
CREATE SEQUENCE pae.pk_personne_contact;
CREATE SEQUENCE pae.pk_participation;
CREATE SEQUENCE pae.pk_participation_personne_contact;


-------------------------CREATE TABLE-------------------------------
CREATE TABLE pae.utilisateurs(
	id_utilisateur INTEGER PRIMARY KEY DEFAULT NEXTVAL('pae.pk_utilisateur'),
	pseudo VARCHAR(255),
	nom VARCHAR (255),
	prenom VARCHAR (255),
	email VARCHAR (255),
	date_inscription DATE,
	responsable BOOLEAN,
	mdp VARCHAR (255),
	version INTEGER
);

CREATE TABLE pae.entreprises(
	id_entreprise INTEGER PRIMARY KEY DEFAULT NEXTVAL('pae.pk_entreprise'),
	nom_entreprise VARCHAR(255),
	rue VARCHAR(255),
	numero VARCHAR(45),
	boite VARCHAR(45),
	code_postal VARCHAR(45),
	commune VARCHAR(100),
	date_premier_contact DATE,
	date_derniere_participation DATE,
	createur INTEGER REFERENCES pae.utilisateurs(id_utilisateur),
	version INTEGER
);

CREATE TABLE pae.personnes_contact(
	id_personne_contact INTEGER PRIMARY KEY DEFAULT NEXTVAL('pae.pk_personne_contact'),
	nom VARCHAR(255),
	prenom VARCHAR(255),
	telephone VARCHAR(255),
	email VARCHAR(255),
	actif BOOLEAN,
	id_entreprise INTEGER REFERENCES pae.entreprises(id_entreprise),
	version INTEGER
);

CREATE TABLE pae.journees(
	id_journee INTEGER PRIMARY KEY DEFAULT NEXTVAL('pae.pk_journee'),
	date_journee DATE,
	ouverte BOOLEAN,
	version INTEGER
);

CREATE TABLE pae.participations(
	id_participation INTEGER PRIMARY KEY DEFAULT NEXTVAL('pae.pk_participation'),
	etat VARCHAR(45),
	annulee BOOLEAN,
	version INTEGER,
	id_journee INTEGER REFERENCES pae.journees(id_journee),
	id_entreprise INTEGER REFERENCES pae.entreprises(id_entreprise)
);

CREATE TABLE pae.participations_personnes(
	id_participation_personne INTEGER PRIMARY KEY DEFAULT NEXTVAL('pae.pk_participation_personne_contact'),
	version INTEGER,
	id_participation INTEGER REFERENCES pae.participations(id_participation),
	id_personne_contact INTEGER REFERENCES pae.personnes_contact(id_personne_contact)
);


--------------------------- INSERT ---------------------------------
INSERT INTO pae.utilisateurs(id_utilisateur, pseudo, nom, prenom, email, date_inscription, responsable, mdp, version) VALUES (1,'blehmann','Lehmann','Brigitte','brigitte.lehmann@vinci.be','2017-05-08',TRUE,'$2a$10$wwPzuWNWqOvkK60KgTU9Ee903Bp32ru5rPiOq/0LbHhVyK2fd2gA6',1);
INSERT INTO pae.utilisateurs(id_utilisateur, pseudo, nom, prenom, email, date_inscription, responsable, mdp, version) VALUES (2,'dgrolaux','Grolaux','Donatien','donatien.grolaux@vinci.be','2017-05-08',TRUE,'$2a$10$MJH82kqAYW//jap4D4S9P.YTpfQCkbkw5Sai3vsAiv6NgSjC/xiI2',1);
ALTER SEQUENCE pae.pk_utilisateur RESTART WITH 3;

INSERT INTO pae.entreprises(id_entreprise, nom_entreprise, rue, numero, boite, code_postal, commune, date_premier_contact, date_derniere_participation, createur, version) VALUES (1,'Accenture','Waterloolaan','16','','B-1000','Brussels','2017-05-08','2016-10-26',1,4);
INSERT INTO pae.entreprises(id_entreprise, nom_entreprise, rue, numero, boite, code_postal, commune, date_premier_contact, date_derniere_participation, createur, version) VALUES (2,'CodeltBlue','Avenue de l''espérance','40b','','1348','LLN','2017-05-08','2013-11-13',1,2);
INSERT INTO pae.entreprises(id_entreprise, nom_entreprise, rue, numero, boite, code_postal, commune, date_premier_contact, date_derniere_participation, createur, version) VALUES (3,'STERIA','Boulevard du Souverain','36','','B-1170','Bruxelles','2017-05-08','2016-10-26',1,5);
INSERT INTO pae.entreprises(id_entreprise, nom_entreprise, rue, numero, boite, code_postal, commune, date_premier_contact, date_derniere_participation, createur, version) VALUES (4,'Eezee-IT','rue André Dumont','5','','B-1435','Mont-Saint-Guibert','2017-05-08','2013-11-13',1,2);
INSERT INTO pae.entreprises(id_entreprise, nom_entreprise, rue, numero, boite, code_postal, commune, date_premier_contact, date_derniere_participation, createur, version) VALUES (5,'Bewan','Drève Richelle','161L','b46','B-1410','Waterloo','2017-05-08','2016-10-26',1,3);
ALTER SEQUENCE pae.pk_entreprise RESTART WITH 6;

INSERT INTO pae.personnes_contact(id_personne_contact, nom, prenom, telephone, email, actif, id_entreprise, version) VALUES (1,'Van Dyck','Marijke','','marijke.vandyck@accenture.com',TRUE,1,0);
INSERT INTO pae.personnes_contact(id_personne_contact, nom, prenom, telephone, email, actif, id_entreprise, version) VALUES (2,'Marecaux','Aimée','','aimee.marecaux@accenture.com',TRUE,1,0);
INSERT INTO pae.personnes_contact(id_personne_contact, nom, prenom, telephone, email, actif, id_entreprise, version) VALUES (3,'Lepape','Vincent','0479/97 95 05','Vincent.lepape@CodeItBlue.Com',TRUE,2,0);
INSERT INTO pae.personnes_contact(id_personne_contact, nom, prenom, telephone, email, actif, id_entreprise, version) VALUES (4,'Alvarez','Roberto','','roberto.alvarez@steria.be',TRUE,3,0);
INSERT INTO pae.personnes_contact(id_personne_contact, nom, prenom, telephone, email, actif, id_entreprise, version) VALUES (5,'Rigo','Nicolas','0032 478 88 02 55','nicolas.rigo@eezee-it.com',TRUE,4,0);
INSERT INTO pae.personnes_contact(id_personne_contact, nom, prenom, telephone, email, actif, id_entreprise, version) VALUES (6,'BRASSINNE','Virginie','','Virginie.BRASSINNE@bewan.be',TRUE,5,0);
INSERT INTO pae.personnes_contact(id_personne_contact, nom, prenom, telephone, email, actif, id_entreprise, version) VALUES (7,'Croiset','Isabelle','','isabelle.croiset@bewan.be',TRUE,5,0);
INSERT INTO pae.personnes_contact(id_personne_contact, nom, prenom, telephone, email, actif, id_entreprise, version) VALUES (8,'Dedecker','Bénédicte','','drh@bewan.be',TRUE,5,0);
INSERT INTO pae.personnes_contact(id_personne_contact, nom, prenom, telephone, email, actif, id_entreprise, version) VALUES (9,'De Troyer','Stéphanie','','stephanie.de.troyer@accenture.com',TRUE,1,0);
ALTER SEQUENCE pae.pk_personne_contact RESTART WITH 10;

INSERT INTO pae.journees(id_journee, date_journee, ouverte, version) VALUES (1,'2013-11-13',FALSE,1);
INSERT INTO pae.journees(id_journee, date_journee, ouverte, version) VALUES (2,'2014-11-12',FALSE,1);
INSERT INTO pae.journees(id_journee, date_journee, ouverte, version) VALUES (3,'2015-10-28',FALSE,1);
INSERT INTO pae.journees(id_journee, date_journee, ouverte, version) VALUES (4,'2016-10-26',FALSE,1);
ALTER SEQUENCE pae.pk_journee RESTART WITH 5;

INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (1,'PAYEE',FALSE,3,1,5);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (2,'PAYEE',FALSE,3,1,2);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (3,'PAYEE',FALSE,3,1,4);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (4,'PAYEE',FALSE,3,1,3);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (5,'CONFIRMEE',TRUE,1,2,4);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (6,'REFUSEE',FALSE,1,2,2);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (7,'PAYEE',FALSE,3,2,3);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (8,'PAYEE',FALSE,3,2,1);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (9,'REFUSEE',FALSE,1,2,5);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (10,'PAYEE',FALSE,3,3,3);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (11,'PAYEE',FALSE,3,3,1);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (12,'REFUSEE',FALSE,1,3,5);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (13,'PAYEE',FALSE,3,4,3);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (14,'PAYEE',FALSE,3,4,1);
INSERT INTO pae.participations(id_participation, etat, annulee, version, id_journee, id_entreprise) VALUES (15,'PAYEE',FALSE,3,4,5);
ALTER SEQUENCE pae.pk_participation RESTART WITH 16;

INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (1,0,2,3);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (2,0,4,4);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (3,0,3,5);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (4,0,1,6);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (5,0,1,7);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (6,0,8,9);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (7,0,7,4);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (8,0,11,2);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (9,0,11,1);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (10,0,10,4);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (11,0,13,4);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (12,0,14,2);
INSERT INTO pae.participations_personnes(id_participation_personne, version, id_participation, id_personne_contact) VALUES (13,0,15,8);
ALTER SEQUENCE pae.pk_participation_personne_contact RESTART WITH 14;









