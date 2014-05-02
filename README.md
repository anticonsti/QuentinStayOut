Projet BD6

Nous avons utilise postgresql-9.3-1101.
Travaille sous Linux et Windows XP.
Nous avons utilise GIT pour communiquer efficacement:

Le fichier requetes.sql permette d'initialiser notre base de donnee : 21 tables.
Le fichier exemples.sql permette d'initialiser les exemples pour la soutenance.
Le fichier reponses.txt contient les reponses aux 12questions du projet.


Pour la table prix_logement le champ "prix_mois" correspond a un INTEGER qui est utilise pour calculer le "montant_total" a payer pour la location d'un mois de logement. Le calcul se fait par la formule suivante : (date_fin_location - date_debut_location)*prix*(1-prix_mois/100).

Pour la table offre_promotionnelle le champ "prix_offre_promo" correspond a un INTEGER qui est utilise pour calculer le "montant_total" a payer pour la location pendant la periode indique par "date_debut_offre_promo" et "date_fin_offre_promo". La formule est semblable a celui au-dessus.

La reduction de "prix_mois" concerne seulement les locations de duree >= 27jours.


Auteurs: Florent LAY & Jung-min OK
L3-Mathematiques et Informatique