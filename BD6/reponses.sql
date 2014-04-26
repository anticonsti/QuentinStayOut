SELECT COUNT(*) FROM logement NATURAL JOIN appartement NATURAL JOIN disponibilite 
WHERE DATE '04/04/2015' >= date_debut_dispo 
AND DATE '08/04/2015' <= date_fin_dispo;


SELECT id_logement FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN propose_prestation NATURAL JOIN prestation 
WHERE ville = 'Pékin' 
AND EXTRACT(month FROM date_debut_dispo)=4 
AND EXTRACT(month FROM date_fin_dispo)=4 
AND description_prestation ='petit-déjeuner' 
AND date_fin_dispo - date_debut_dispo = 3
GROUP BY id_logement 
HAVING SUM(prix + prix_prestation) < 200 ;


SELECT AVG(prix) FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite 
WHERE date_fin_dispo - date_debut_dispo = 7 
AND EXTRACT(year FROM date_debut_dispo) = 2014 
AND EXTRACT(year FROM date_fin_dispo) = 2014; 


SELECT AVG(count) FROM 
( SELECT id_proprietaire, COUNT(id_logement) FROM propose_logement GROUP BY id_proprietaire ) AS tmp ;


SELECT COUNT(id_logement) FROM concerne NATURAL JOIN location 
WHERE EXTRACT(month FROM date_debut_location)>=1 
AND EXTRACT(month FROM date_fin_location) <=3 
AND EXTRACT(year FROM date_debut_location)=2014 ;


