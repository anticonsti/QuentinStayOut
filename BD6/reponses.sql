SELECT COUNT(id_logement) FROM logement NATURAL JOIN appartement NATURAL JOIN disponibilite 
WHERE DATE '2015-04-04' >= date_debut_dispo 
AND DATE '2015-04-08' <= date_fin_dispo 
EXCEPT SELECT id_logement FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location 
WHERE date_debut_location >= '2014-04-04' 
AND date_fin_location <= '2015-04-08' ;


SELECT id_logement FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN propose_prestation NATURAL JOIN prestation 
WHERE ville = 'Pékin' 
AND EXTRACT(month FROM date_debut_dispo)=4 
AND EXTRACT(month FROM date_fin_dispo)=4 
AND description_prestation ='petit-déjeuner' 
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


SELECT id_logement, prix FROM logement NATURAL JOIN appartement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN propose_transport NATURAL JOIN propose_prestation NATURAL JOIN prestation 
WHERE ville = 'Moscou' 
AND date_debut_dispo >= '2014-07-08' 
AND date_fin_dispo - date_debut_dispo >= 5 
AND description_prestation = 'repas soir' 
EXCEPT SELECT id_logement FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location 
WHERE date_debut_location <= '2014-07-08' 
AND date_fin_location >= '2014-07-13' 
EXCEPT SELECT id_logement, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN avec_transport NATURAL JOIN concerne 
WHERE TIMESTAMP '2014-07-08 13:00:00' > date_reservation - interval '30 minutes' 
AND TIMESTAMP '2014-07-08 13:00:00' < date_reservation + interval '30 minutes' 
EXCEPT SELECT id_logement, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN avec_transport NATURAL JOIN concerne 
WHERE TIMESTAMP '2014-07-13 17:00:00' > date_reservation - interval '30 minutes' 
AND TIMESTAMP '2014-07-13 17:00:00' < date_reservation + interval '30 minutes' 
ORDER BY prix ;


SELECT id_logement FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite propose_suggestion NATURAL JOIN suggestion 
WHERE date_debut_dispo <= CURRENT_DATE + 1 
AND date_fin_dispo >= CURRENT_DATE + 1 
AND type_suggestion = 'touristique' 
EXCEPT SELECT id_logement FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location 
WHERE date_debut_location <= CURRENT_DATE + 1 
AND date_fin_location >= CURRENT_DATE + 1 ;




