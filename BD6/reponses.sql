WITH 
dispo AS 
(SELECT DISTINCT id_logement, date_debut_dispo, date_fin_dispo FROM disponibilite NATURAL JOIN prix_logement NATURAL JOIN concerne ),
dureelogement AS 
(SELECT id_logement, SUM(date_fin_dispo - date_debut_dispo ) AS dureelog FROM dispo GROUP BY id_logement ),
logementsoccupes AS 
(SELECT id_logement, date_debut_location, date_fin_location FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location WHERE( date_debut_location, date_fin_location ) OVERLAPS ( date_debut_dispo, date_fin_dispo ) ),
dureeoccupee AS 
(SELECT id_logement, SUM(date_fin_location-date_debut_location) AS dureeoccup FROM logementsoccupes GROUP BY id_logement),
nbreservation AS 
(SELECT id_logement, COUNT(*) AS nb FROM logementsoccupes GROUP BY id_logement ),
libre AS (
SELECT id_logement FROM logement EXCEPT 
SELECT id_logement FROM dureelogement NATURAL JOIN nbreservation NATURAL JOIN dureeoccupee WHERE dureeoccup + nb -1 = dureelog ),
selectionne AS (
SELECT id_logement FROM libre 
NATURAL JOIN logement NATURAL JOIN appartement NATURAL JOIN disponibilite NATURAL JOIN prix_logement 
WHERE ville='Paris' AND ( DATE '2015-04-04', DATE '2015-04-08' ) OVERLAPS ( date_debut_dispo, date_fin_dispo ) )
SELECT COUNT(*) FROM selectionne;


SELECT id_logement FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN propose_prestation NATURAL JOIN prestation 
WHERE ville = 'Pekin' 
AND EXTRACT(month FROM date_debut_dispo)=4 
AND EXTRACT(month FROM date_fin_dispo)=4 
AND description_prestation ='petit-dejeuner' 
AND date_fin_dispo - date_debut_dispo >= 3 
GROUP BY id_logement 
HAVING SUM(prix*3 + prix_prestation*3) < 200 ;


SELECT AVG(prix) FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite 
WHERE date_fin_dispo - date_debut_dispo >= 7 
AND EXTRACT(year FROM date_debut_dispo) = 2014 
AND EXTRACT(year FROM date_fin_dispo) = 2014; 


SELECT AVG(count) FROM 
( SELECT id_proprietaire, COUNT(id_logement) FROM propose_logement GROUP BY id_proprietaire ) AS tmp ;


SELECT COUNT(id_logement) FROM concerne NATURAL JOIN location 
WHERE EXTRACT(month FROM date_debut_location)>=1 
AND EXTRACT(month FROM date_fin_location) <=3 
AND EXTRACT(year FROM date_debut_location)=2014 ;


SELECT AVG(date_fin_dispo - date_debut_dispo) FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite;


SELECT AVG(prix) FROM logement NATURAL JOIN prix_logement;


WITH 
dispo AS 
(SELECT DISTINCT id_logement, date_debut_dispo, date_fin_dispo FROM disponibilite NATURAL JOIN prix_logement NATURAL JOIN concerne ),
dureelogement AS 
(SELECT id_logement, SUM(date_fin_dispo - date_debut_dispo ) AS dureelog FROM dispo GROUP BY id_logement ),
logementsoccupes AS 
(SELECT id_logement, date_debut_location, date_fin_location FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location WHERE( date_debut_location, date_fin_location ) OVERLAPS ( date_debut_dispo, date_fin_dispo ) ),
dureeoccupee AS 
(SELECT id_logement, SUM(date_fin_location-date_debut_location) AS dureeoccup FROM logementsoccupes GROUP BY id_logement),
nbreservation AS 
(SELECT id_logement, COUNT(*) AS nb FROM logementsoccupes GROUP BY id_logement ),
libre AS (
SELECT id_logement FROM logement EXCEPT 
SELECT id_logement FROM dureelogement NATURAL JOIN nbreservation NATURAL JOIN dureeoccupee WHERE dureeoccup + nb -1 = dureelog )
SELECT id_logement, prix FROM libre NATURAL JOIN logement NATURAL JOIN appartement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN propose_transport NATURAL JOIN propose_prestation NATURAL JOIN prestation
WHERE ville = 'Moscou' 
AND date_debut_dispo >= '2014-07-08' 
AND date_fin_dispo - date_debut_dispo >= 5 
AND description_prestation = 'diner'
EXCEPT SELECT id_logement, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN avec_transport NATURAL JOIN concerne 
WHERE TIMESTAMP '2014-07-08 13:00:00' > date_reservation - interval '30 minutes' 
AND TIMESTAMP '2014-07-08 13:00:00' < date_reservation + interval '30 minutes' 
EXCEPT SELECT id_logement, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN avec_transport NATURAL JOIN concerne 
WHERE TIMESTAMP '2014-07-13 17:00:00' > date_reservation - interval '30 minutes' 
AND TIMESTAMP '2014-07-13 17:00:00' < date_reservation + interval '30 minutes' 
ORDER BY prix ;


SELECT id_logement, prix FROM logement NATURAL JOIN appartement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN propose_transport NATURAL JOIN propose_prestation NATURAL JOIN prestation 
WHERE ville = 'Moscou' 
AND date_debut_dispo >= '2014-07-08' 
AND date_fin_dispo - date_debut_dispo >= 5 
AND description_prestation = 'diner' 
EXCEPT SELECT id_logement, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location 
WHERE date_debut_location <= '2014-07-08' 
AND date_fin_location >= '2014-07-13' 
EXCEPT SELECT id_logement, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN avec_transport NATURAL JOIN concerne 
WHERE TIMESTAMP '2014-07-08 13:00:00' > date_reservation - interval '30 minutes' 
AND TIMESTAMP '2014-07-08 13:00:00' < date_reservation + interval '30 minutes' 
EXCEPT SELECT id_logement, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN avec_transport NATURAL JOIN concerne 
WHERE TIMESTAMP '2014-07-13 17:00:00' > date_reservation - interval '30 minutes' 
AND TIMESTAMP '2014-07-13 17:00:00' < date_reservation + interval '30 minutes' 
ORDER BY prix ;


WITH 
dispo AS 
(SELECT DISTINCT id_logement, date_debut_dispo, date_fin_dispo FROM disponibilite NATURAL JOIN prix_logement NATURAL JOIN concerne ),
dureelogement AS 
(SELECT id_logement, SUM(date_fin_dispo - date_debut_dispo ) AS dureelog FROM dispo GROUP BY id_logement ),
logementsoccupes AS 
(SELECT id_logement, date_debut_location, date_fin_location FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location WHERE( date_debut_location, date_fin_location ) OVERLAPS ( date_debut_dispo, date_fin_dispo ) ),
dureeoccupee AS 
(SELECT id_logement, SUM(date_fin_location-date_debut_location) AS dureeoccup FROM logementsoccupes GROUP BY id_logement),
nbreservation AS 
(SELECT id_logement, COUNT(*) AS nb FROM logementsoccupes GROUP BY id_logement ),
libre AS (
SELECT id_logement FROM logement EXCEPT 
SELECT id_logement FROM dureelogement NATURAL JOIN nbreservation NATURAL JOIN dureeoccupee WHERE dureeoccup + nb -1 = dureelog ) 
SELECT id_logement FROM libre 
NATURAL JOIN logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN propose_suggestion NATURAL JOIN suggestion 
WHERE date_debut_dispo <= CURRENT_DATE + 1 
AND date_fin_dispo >= CURRENT_DATE + 1 
AND type_suggestion = 'touristique';


SELECT id_proprietaire, id_logement FROM propose_logement NATURAL JOIN logement WHERE ville='Rio de Janeiro';


WITH
somme AS 
( SELECT SUM(montant_total) AS som FROM location NATURAL JOIN concerne NATURAL JOIN propose_logement NATURAL JOIN proprietaire WHERE nom_proprietaire='Bettencourt' ) 
SELECT som*0.9 FROM somme;


WITH 
nbtotal AS 
(SELECT COUNT(id_logement) AS denominateur FROM logement WHERE ville = 'Berlin' ), 
occup AS 
(SELECT COUNT(id_logement) AS numerateur FROM logement NATURAL JOIN concerne WHERE ville = 'Berlin' )
SELECT numerateur*1.0/( CASE denominateur WHEN 0 THEN NULL ELSE denominateur END )*1.0 AS Taux_de_remplissage FROM nbtotal, occup ;


