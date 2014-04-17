CREATE TABLE proprietaire(
id_proprietaire INT PRIMARY KEY NOT NULL,
nom_proprietaire VARCHAR(20) NOT NULL, 
prenom_proprietaire VARCHAR(30) NOT NULL, 
pseudo VARCHAR(30) NOT NULL, 
mot_de_passe VARCHAR(30) NOT NULL
);


CREATE TABLE logement(
id_logement INT PRIMARY KEY NOT NULL,
adresse_logement VARCHAR(100) NOT NULL,
surface INT NOT NULL CHECK(surface>0), 
ville VARCHAR(20) NOT NULL
);


CREATE TABLE propose_logement(
id_proprietaire INT REFERENCES proprietaire(id_proprietaire) ON DELETE CASCADE, 
id_logement INT REFERENCES logement(id_logement) ON DELETE CASCADE
);


CREATE TABLE photo(
id_photo INT PRIMARY KEY NOT NULL,
id_logement INT REFERENCES logement(id_logement),
nom_photo VARCHAR(100) DEFAULT ('sans nom')
);


CREATE TABLE offre_promotionnelle(
id_promo INT PRIMARY KEY, 
id_logement INT REFERENCES logement(id_logement),
date_debut_offre_promo DATE NOT NULL, 
date_fin_offre_promo DATE NOT NULL, 
prix_offre_promo INT NOT NULL CHECK(prix_offre_promo>0), 
CHECK ( date_fin_offre_promo >= date_debut_offre_promo )
);


CREATE TABLE disponibilite(
id_dispo INT PRIMARY KEY, 
date_debut_dispo DATE NOT NULL, 
date_fin_dispo DATE NOT NULL
CHECK ( date_fin_dispo >= date_debut_dispo )
);


CREATE TABLE suggestion(
id_suggestion INT PRIMARY KEY, 
type_suggestion VARCHAR(30), 
nom_suggestion VARCHAR(50) NOT NULL
);


CREATE TABLE prestation(
id_prestation INT PRIMARY KEY, 
description_prestation VARCHAR(100) NOT NULL, 
prix_prestation INT NOT NULL CHECK(prix_prestation>0)
);


CREATE TABLE prix_logement(
id_dispo INT REFERENCES disponibilite(id_dispo), 
id_logement INT REFERENCES logement(id_logement),
prix INT NOT NULL CHECK(prix>0) 
);


CREATE TABLE propose_prestation(
id_logement INT REFERENCES logement(id_logement), 
id_prestation INT REFERENCES prestation(id_prestation)
);


CREATE TABLE service_transport(
id_service_transport INT PRIMARY KEY, 
nb_vehicule INT NOT NULL CHECK(nb_vehicule>=0),
prix_transport INT NOT NULL CHECK(prix_transport>0)
);


CREATE TABLE propose_transport(
id_logement INT REFERENCES logement(id_logement), 
id_service_transport INT REFERENCES service_transport(id_service_transport)
);


CREATE TABLE location(
id_location INT PRIMARY KEY, 
date_debut_location DATE NOT NULL,
date_fin_location DATE NOT NULL, 
montant_total INT NOT NULL CHECK(montant_total>0),
CHECK ( date_fin_location >= date_debut_location )
);


CREATE TABLE concerne(
id_logement INT REFERENCES logement(id_logement), 
id_location INT REFERENCES location(id_location) 
);


CREATE TABLE avec_transport(
id_location INT REFERENCES location(id_location), 
id_service_transport INT REFERENCES service_transport(id_service_transport)
);


CREATE TABLE locataire(
id_locataire INT PRIMARY KEY, 
nom_locataire VARCHAR(20) NOT NULL, 
prenom_locataire VARCHAR(30) NOT NULL, 
adresse_locataire VARCHAR(50) NOT NULL, 
num_tel VARCHAR(15) NOT NULL, 
email VARCHAR(50) NOT NULL
);


CREATE TABLE loge(
id_location INT REFERENCES location(id_location), 
id_locataire INT REFERENCES locataire(id_locataire)
);


CREATE TABLE avec_prestation(
id_location INT REFERENCES location(id_location), 
id_prestation INT REFERENCES prestation(id_prestation)
);