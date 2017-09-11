DROP SCHEMA IF EXISTS pae CASCADE;

CREATE SCHEMA pae;

CREATE SEQUENCE pae.pk_utilisateur;
CREATE SEQUENCE pae.pk_entreprise;
CREATE SEQUENCE pae.pk_journee;
CREATE SEQUENCE pae.pk_personne_contact;
CREATE SEQUENCE pae.pk_participation;
CREATE SEQUENCE pae.pk_participation_personne_contact;


-------------------------CREATE TABLE------------------------------
/**
 * Table utilisateurs
 * Tous les champs sont obligatoires
 * pseudo UNIQUE !
 * date_inscription = date de l'enregistrement de l'utilisateur CURRENT_DATE
 * si inscription sur le site, responsable = FALSE !
*/
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

/**
 * Table entreprises
 * Tous les champs sont obligatoires SAUF date_derniere_participation
 * date_premier_contact = date de l'enregistrement de l'entreprise CURRENT_DATE
*/
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

/**
 * Table personnes_contact
 * Telephone ou email, autres champs obligatoires
*/
CREATE TABLE pae.personnes_contact(
	id_personne_contact INTEGER PRIMARY KEY DEFAULT NEXTVAL('pae.pk_personne_contact'),
	nom VARCHAR(255),
	prenom VARCHAR(255),
	telephone VARCHAR(255),
	email VARCHAR(255),
	actif BOOLEAN,
	id_entreprise INTEGER REFERENCES pae.entreprises(id_entreprise),
	version INTEGER,
	sexe VARCHAR(1)
);

/**
 * Table journees
 * Tous les champs sont obligatoires
 * ouverte DEFAULT TRUE
*/
CREATE TABLE pae.journees(
	id_journee INTEGER PRIMARY KEY DEFAULT NEXTVAL('pae.pk_journee'),
	date_journee DATE,
	ouverte BOOLEAN,
	version INTEGER
);

/**
 * Table participations
 * etat DEFAULT 'invitee' et confirmee | refusee | facturee | payee
 * annulee DEFAULT FALSE
*/
CREATE TABLE pae.participations(
	id_participation INTEGER PRIMARY KEY DEFAULT NEXTVAL('pae.pk_participation'),
	etat VARCHAR(45),
	annulee BOOLEAN,
	version INTEGER,
	id_journee INTEGER REFERENCES pae.journees(id_journee),
	id_entreprise INTEGER REFERENCES pae.entreprises(id_entreprise),
	commentaire text
);


/**
 * Table participations_personnes
 */
CREATE TABLE pae.participations_personnes(
	id_participation_personne INTEGER PRIMARY KEY DEFAULT NEXTVAL('pae.pk_participation_personne_contact'),
	version INTEGER,
	id_participation INTEGER REFERENCES pae.participations(id_participation),
	id_personne_contact INTEGER REFERENCES pae.personnes_contact(id_personne_contact)
);


