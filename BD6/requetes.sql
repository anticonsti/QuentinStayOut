CREATE TABLE proprietaire(
id_proprietaire SERIAL PRIMARY KEY,
nom_proprietaire VARCHAR(20) NOT NULL, 
prenom_proprietaire VARCHAR(30) NOT NULL, 
pseudo VARCHAR(30) UNIQUE NOT NULL, 
mot_de_passe VARCHAR(30) NOT NULL
);


CREATE TABLE logement(
id_logement SERIAL PRIMARY KEY,
adresse_logement VARCHAR(100) NOT NULL,
surface INT NOT NULL CHECK(surface>0), 
ville VARCHAR(20) NOT NULL
);


CREATE TABLE appartement(
id_logement INT REFERENCES logement(id_logement) ON UPDATE CASCADE ON DELETE CASCADE, 
nb_pieces INT CHECK(nb_pieces>0)
);


CREATE TABLE chambre(
id_logement INT REFERENCES logement(id_logement) ON UPDATE CASCADE ON DELETE CASCADE,
numero_chambre INT CHECK(numero_chambre>=0)
);


CREATE TABLE propose_logement(
id_proprietaire INT REFERENCES proprietaire(id_proprietaire) ON UPDATE CASCADE ON DELETE CASCADE, 
id_logement INT REFERENCES logement(id_logement) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE photo(
id_photo SERIAL PRIMARY KEY,
id_logement INT REFERENCES logement(id_logement) ON UPDATE CASCADE ON DELETE CASCADE,
nom_photo VARCHAR(100) DEFAULT ('sans nom')
);


CREATE TABLE offre_promotionnelle(
id_promo SERIAL PRIMARY KEY, 
id_logement INT REFERENCES logement(id_logement) ON UPDATE CASCADE ON DELETE CASCADE,
date_debut_offre_promo DATE NOT NULL, 
date_fin_offre_promo DATE NOT NULL, 
prix_offre_promo INT NOT NULL CHECK(prix_offre_promo>0), 
CHECK ( date_fin_offre_promo >= date_debut_offre_promo )
);


CREATE TABLE disponibilite(
id_dispo SERIAL PRIMARY KEY, 
date_debut_dispo DATE NOT NULL, 
date_fin_dispo DATE NOT NULL,
sejour_min INT CHECK(sejour_min>0),
CHECK ( date_fin_dispo >= date_debut_dispo )
);


CREATE TABLE suggestion(
id_suggestion SERIAL PRIMARY KEY, 
type_suggestion VARCHAR(30), 
nom_suggestion VARCHAR(50) NOT NULL
);


CREATE TABLE prestation(
id_prestation SERIAL PRIMARY KEY, 
description_prestation VARCHAR(100) NOT NULL, 
prix_prestation INT NOT NULL CHECK(prix_prestation>0)
);


CREATE TABLE prix_logement(
id_dispo INT REFERENCES disponibilite(id_dispo) ON UPDATE CASCADE ON DELETE CASCADE, 
id_logement INT REFERENCES logement(id_logement) ON UPDATE CASCADE ON DELETE CASCADE,
prix INT NOT NULL CHECK(prix>0),
prix_mois INT CHECK(prix_mois>0)
);


CREATE TABLE propose_prestation(
id_logement INT REFERENCES logement(id_logement) ON UPDATE CASCADE ON DELETE CASCADE, 
id_prestation INT REFERENCES prestation(id_prestation) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE propose_suggestion(
id_logement INT REFERENCES logement(id_logement) ON UPDATE CASCADE ON DELETE CASCADE, 
id_suggestion INT REFERENCES suggestion(id_suggestion) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE service_transport(
id_service_transport SERIAL PRIMARY KEY, 
nb_vehicule INT NOT NULL CHECK(nb_vehicule>=0),
prix_transport INT NOT NULL CHECK(prix_transport>0)
);


CREATE TABLE propose_transport(
id_logement INT REFERENCES logement(id_logement) ON UPDATE CASCADE ON DELETE CASCADE, 
id_service_transport INT REFERENCES service_transport(id_service_transport) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE location(
id_location SERIAL PRIMARY KEY, 
date_debut_location DATE NOT NULL,
date_fin_location DATE NOT NULL, 
montant_total INT NOT NULL CHECK(montant_total>0),
CHECK ( date_fin_location >= date_debut_location )
);


CREATE TABLE concerne(
id_logement INT REFERENCES logement(id_logement) ON UPDATE CASCADE ON DELETE CASCADE, 
id_location INT REFERENCES location(id_location) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE avec_transport(
id_location INT REFERENCES location(id_location) ON UPDATE CASCADE ON DELETE CASCADE, 
id_service_transport INT REFERENCES service_transport(id_service_transport) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE locataire(
id_locataire SERIAL PRIMARY KEY, 
nom_locataire VARCHAR(20) NOT NULL, 
prenom_locataire VARCHAR(30) NOT NULL, 
adresse_locataire VARCHAR(50) NOT NULL, 
num_tel VARCHAR(15) NOT NULL, 
email VARCHAR(50) NOT NULL
);


CREATE TABLE loge(
id_location INT REFERENCES location(id_location) ON UPDATE CASCADE ON DELETE CASCADE, 
id_locataire INT REFERENCES locataire(id_locataire) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE avec_prestation(
id_location INT REFERENCES location(id_location) ON UPDATE CASCADE ON DELETE CASCADE, 
id_prestation INT REFERENCES prestation(id_prestation) ON UPDATE CASCADE ON DELETE CASCADE
);