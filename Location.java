import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.DateFormat;
//indente le 2 mai 2014

public class Location {

	PreparedStatement select=null, select2=null;
	PreparedStatement insert=null;
	PreparedStatement delete=null;
	ResultSet result = null, result2=null;
	Connection conn = null;

	public Location(Connection conn){
		this.conn=conn;
	}

	public void printLocation() throws ParseException{

		System.out.println("");
		System.out.print("Voir un logement ? (O/N): ");

		try{

			// réponse de l'utilisateur
			if(Utils.readString("O|N").equals("O")){
				String id_logement="";
				boolean rsNext=false;

				// sélection d'un logement
				do{
					System.out.print("id_logement: ");
					id_logement = Utils.readString("[0-9]{1,5}");

					String requete = "SELECT * FROM concerne";
					select = conn.prepareStatement(requete);
					result = select.executeQuery();

					if(result.next()){
						select = conn.prepareStatement("WITH dispo AS (SELECT DISTINCT id_logement, date_debut_dispo, date_fin_dispo FROM disponibilite NATURAL JOIN prix_logement NATURAL JOIN concerne ), dureelogement AS (SELECT id_logement, SUM(date_fin_dispo - date_debut_dispo ) AS dureelog FROM dispo GROUP BY id_logement ), logementsoccupes AS (SELECT id_logement, date_debut_location, date_fin_location FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location WHERE( date_debut_location, date_fin_location ) OVERLAPS ( date_debut_dispo, date_fin_dispo ) ), dureeoccupee AS (SELECT id_logement, SUM(date_fin_location-date_debut_location) AS dureeoccup FROM logementsoccupes GROUP BY id_logement), nbreservation AS (SELECT id_logement, COUNT(*) AS nb FROM logementsoccupes GROUP BY id_logement ) SELECT id_logement FROM logement WHERE id_logement=" + id_logement +" EXCEPT SELECT id_logement FROM dureelogement NATURAL JOIN nbreservation NATURAL JOIN dureeoccupee WHERE dureeoccup + nb -1 = dureelog ");

					} else 
						select = conn.prepareStatement("SELECT id_logement FROM logement WHERE id_logement="+ id_logement);

					result = select.executeQuery();
					rsNext = result.next();
					if(rsNext==false)
						System.out.println("Inexistant");
				}while( rsNext == false);

				// détails du logement
				Utils.printEntete("VOTRE LOGEMENT " + id_logement);
				this.detailsLogement(id_logement);

				System.out.print("Louer ? (O/N): ");

				// réponse de l'utilisateur
				if( Utils.readString("O|N").equals("O")){

					Utils.printEntete("FORMULAIRE A REMPLIR POUR LE LOGEMENT " + id_logement);
					this.inscriptionLocataire(id_logement);
					System.out.println("Inscription terminée");
					Thread.sleep(1300);

				}
			}

		}catch (SQLException | InterruptedException e) {
			System.err.println(e.getMessage());
		}
	}


	public void detailsLogement(String id_logement) throws SQLException {

		select=conn.prepareStatement("SELECT nom_proprietaire, prenom_proprietaire FROM proprietaire NATURAL JOIN propose_logement WHERE id_logement="+ id_logement);
		result = select.executeQuery();
		if( result.next() ){
			System.out.println("Propriétaire: " +result.getString(1) + " " + result.getString(2));
		}

		select=conn.prepareStatement("SELECT numero_chambre FROM chambre WHERE id_logement = " + id_logement);
		result = select.executeQuery();
		if( result.next() ){
			System.out.println("Chambre n°" + result.getString(1));
		} else {
			select=conn.prepareStatement("SELECT nb_pieces FROM appartement WHERE id_logement = " + id_logement);
			result = select.executeQuery();
			if( result.next() )
				System.out.println("Appartement " + result.getString(1) + " pièces ");
		}

		select = conn.prepareStatement("SELECT adresse_logement, surface, ville FROM logement WHERE id_logement = " + id_logement);
		result = select.executeQuery();
		if(result.next()) {
			System.out.println("Adresse: " +result.getString(1) + ", " + result.getString(3));
			System.out.println("Surface: " +result.getString(2));
		}

		select = conn.prepareStatement("SELECT date_debut_dispo, date_fin_dispo, sejour_min, prix, prix_mois FROM disponibilite NATURAL JOIN prix_logement WHERE id_logement = " + id_logement);
		result = select.executeQuery();
		if(result.next()) {
			System.out.println("Disponibilité: " + result.getString(1) + " -- " + result.getString(2));
			int sej = result.getInt(3);
			if( sej !=0 )
				System.out.println("Séjour min: " +String.valueOf(sej) + "jours");
			System.out.println("Prix/nuit: " +String.valueOf(result.getInt(4)) +"euros");
			int prixM = result.getInt(5);
			if( prixM !=0 )
				System.out.println("%mois: " +String.valueOf(result.getInt(5)));

			select2 = conn.prepareStatement("SELECT prix_offre_promo, date_debut_offre_promo, date_fin_offre_promo FROM offre_promotionnelle WHERE id_logement = " + id_logement + " AND (DATE'"+ result.getString(1) +"', DATE '"+  result.getString(2) +"') OVERLAPS (date_debut_offre_promo, date_fin_offre_promo)");
			result2 = select2.executeQuery();
			if( result2.next() )
				System.out.println("Offre promo: " +result2.getString(1) + " ("+ result2.getString(2)+" -- " +  result2.getString(3)+ ")"); 
		}

		select = conn.prepareStatement("SELECT type_suggestion, nom_suggestion FROM suggestion NATURAL JOIN propose_suggestion WHERE id_logement = " + id_logement);
		result = select.executeQuery();
		while(result.next()) {
			System.out.println("suggestion " +result.getString(1)+ ": " + result.getString(2));

		}

		select = conn.prepareStatement("SELECT description_prestation, prix_prestation FROM prestation NATURAL JOIN propose_prestation WHERE id_logement = " + id_logement);
		result = select.executeQuery();
		while(result.next()) {
			System.out.println("prestation: " +result.getString(1) +", prix: " +String.valueOf(result.getInt(2)) + "euros");
		}

		select = conn.prepareStatement("SELECT prix_transport FROM service_transport NATURAL JOIN propose_transport WHERE id_logement = " + id_logement);
		result = select.executeQuery();
		if(result.next()) {
			System.out.println("service_transport: oui, prix_transport: " +String.valueOf(result.getInt(1)) +"euros");
		}

		System.out.println("");
		System.out.println("Période occupée: ");
		select = conn.prepareStatement("SELECT date_debut_location, date_fin_location FROM logement NATURAL JOIN concerne NATURAL JOIN location WHERE id_logement = " + id_logement);
		result = select.executeQuery();
		while(result.next()) {
			System.out.println(result.getString(1) + " -- " + result.getString(2));
		}
		System.out.println("");
	}


	public void inscriptionLocataire(String id_logement) throws SQLException, ParseException {

		System.out.print("Nom: ");
		String nom = Utils.readString("[A-Za-z]{1,20}");

		System.out.print("Prénom: ");
		String prenom = Utils.readString("[A-Za-z]{1,30}");

		System.out.print("Adresse: ");
		String adresse = Utils.readString("[A-Za-z ]{1,100}");

		System.out.print("Numéro de téléphone (xxxxxxxxxx): ");
		String num = Utils.readString("\\d{10}");

		System.out.print("Email: ");
		String email = Utils.readString("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateDep="",dateFin="";
		Date date1 =null, date2=null, dateDProprio=null, dateFProprio=null, dateDLocat=null, dateFLocat=null;
		int sejourMin = 0, erreurDate=1;
		long duree = 0;
		do{
			erreurDate=1;
			try{
				System.out.print("Date début location (YYYY-MM-DD): ");
				dateDep = Utils.readString("date");
				date1= sdf.parse(dateDep);

				System.out.print("Date fin location (YYYY-MM-DD): ");
				dateFin = Utils.readString("date");
				date2 = sdf.parse(dateFin);

				// vérifie la période à louer
				// période proposée par le propriétaire
				select = conn.prepareStatement("SELECT date_debut_dispo, date_fin_dispo, sejour_min FROM disponibilite NATURAL JOIN prix_logement WHERE id_logement = " + id_logement);
				result = select.executeQuery();
				if(result.next()) {
					dateDProprio=sdf.parse(result.getString(1));
					dateFProprio=sdf.parse(result.getString(2));
					if( result.getString(3) != null){
						sejourMin= result.getInt(3);
					}
				}

				long diff = date2.getTime() - date1.getTime();
				duree = diff / (24 * 60 * 60 * 1000);

				//int duree = Integer.parseInt(dateFin.substring(8)) - Integer.parseInt(dateDep.substring(8));
				if(duree < sejourMin ){
					erreurDate=0;
					System.out.println("Erreur sur la durée de location");
				}

				// vérifie que la période à louer est bien dans la période proposée par le propriétaire
				if( !date2.after(date1) || date1.before(dateDProprio) || date2.after(dateFProprio) ){
					erreurDate=0;
					System.out.println("Erreur sur la date de location");
				}

				if( erreurDate !=0){
					// vérifie qu'on ne loue pas sur une période déjà prise
					select = conn.prepareStatement("SELECT date_debut_location, date_fin_location FROM logement NATURAL JOIN concerne NATURAL JOIN location WHERE id_logement = " + id_logement);

					result = select.executeQuery();
					while(result.next()) {
						dateDLocat=sdf.parse(result.getString(1));
						dateFLocat=sdf.parse(result.getString(2));

						if( date1.equals(dateDLocat) || date1.equals(dateFLocat) || date2.equals(dateDLocat) || date2.equals(dateFLocat) || (date1.after(dateDLocat) && date1.before(dateFLocat) ) || (date2.after(dateDLocat) && date2.before(dateFLocat)) ){
							erreurDate=0;
							System.out.println("Déjà occupé");
							break;
						}
					}
				}
			} catch (ParseException ex){
				ex.printStackTrace();
			}
		}while(erreurDate==0);

		// pour les prestations, on vérifie s'il y en a d'abord
		int id_prestation=-1, prixPrestation=0;
		String avecPrestation="";

		select = conn.prepareStatement("SELECT id_prestation, prix_prestation FROM prestation NATURAL JOIN propose_prestation WHERE id_logement = " + id_logement);
		result = select.executeQuery();
		if(result.next()) {
			System.out.print("Avec prestations? (O/N): ");
			if( (avecPrestation=Utils.readString("O|N")).equals("O")){
				id_prestation=result.getInt(1);
				prixPrestation=result.getInt(2);
			}
		}

		// pour les transports, on vérifie s'il reste des véhicules disponibles
		// controler les véhicules disponibles en tenant compte des réservations déjà faites

		int id_transport=-1, prixTransport=0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String avecTransportAller="", avecTransportRetour="", heure_aller="", heure_retour="", rep_aller="", rep_retour="";
		Date date_aller =null, date_retour=null;

		// on vérifie qu'il y a un service de transport
		select = conn.prepareStatement("SELECT id_service_transport, prix_transport, nb_vehicule FROM propose_transport NATURAL JOIN service_transport WHERE id_logement =" + id_logement);
		result = select.executeQuery();
		if(result.next()) {
			id_transport =  result.getInt(1);
			prixTransport=result.getInt(2);
			int nbVehicule = result.getInt(3);

			// vérifie le nombre de réservation pour l'aller
			select2 = conn.prepareStatement("SELECT COUNT(id_service_transport) FROM avec_transport WHERE id_service_transport=" + id_transport + " AND date(date_reservation) = '" + dateDep +"'");
			result2 = select2.executeQuery();
			if( result2.next()){
				int nbReservations = result2.getInt(1);

				System.out.print("Avec transport aller? (O/N): ");
				if( (avecTransportAller=Utils.readString("O|N")).equals("O")){

					// vérifie heure
					int erreurHeure=1;
					do{
						erreurHeure=1;
						System.out.print("heure_aller (O/N): ");
						if( (rep_aller=Utils.readString("O|N")).equals("O") ){

							System.out.print("heure (hh:mm): ");
							heure_aller=Utils.readString("(\\d{2}:\\d{2})|");
							try{
								date_aller = format.parse(dateDep + " " +heure_aller + ":00");
							} catch (ParseException ex){
								ex.printStackTrace();
							}
						} else
							break;

						// vérifie qu'il reste des végicules disponibles
						if( nbReservations < nbVehicule)
							break;

						// comparer avec toutes les autres réservations
						String requete ="SELECT date_reservation FROM avec_transport WHERE id_service_transport =" + id_transport+ " AND date(date_reservation) = '" + dateDep +"'";
						select2 = conn.prepareStatement(requete);
						// vérifie si c'est disponible
						result2 = select2.executeQuery();
						Date date_reserv= null;
						while(result2.next()) {
							try{
								date_reserv=format.parse(result2.getString(1));
							} catch (ParseException ex){
								ex.printStackTrace();
							}
							// si c'est à la même date
							if(date_reserv.equals(date_aller)){
								long diff_aller = Math.abs( date_reserv.getTime() - date_aller.getTime() );
								long diffHours_aller = diff_aller / (60 * 60 * 1000) % 24;
								System.out.println("diffH :" + diffHours_aller);
								// si c'est à la même heure
								if( diffHours_aller==0){
									long diffMinutes_aller = diff_aller / (60 * 1000) % 60;
									System.out.println("diffmin :" + diffMinutes_aller);
									// indisponible si diff < 30 
									if(diffMinutes_aller < 30){
										System.out.println("impossible");
										// affiche les réservations déjà faites
										System.out.println("Liste des réservations: ");
										select2 = conn.prepareStatement(requete);
										result2 = select2.executeQuery();
										while(result2.next()) {
											System.out.println(result2.getString(1));
										}

										erreurHeure=0;
										break;
									}
								}
							}
						}

					} while(erreurHeure==0);
				}

			}

			// vérifie le nombre de réservation pour le retour 
			select2 = conn.prepareStatement("SELECT COUNT(id_service_transport) FROM avec_transport WHERE id_service_transport=" + id_transport + " AND date(date_reservation) = '" + dateFin +"'");
			result2 = select2.executeQuery();
			if( result2.next()){
				int nbReservations = result2.getInt(1);
				if( rep_aller.equals("O") )
					nbReservations++;

				System.out.print("Avec transport retour? (O/N): ");
				if( (avecTransportRetour=Utils.readString("O|N")).equals("O")){
					// vérifie heure 
					int erreurHeure=1;
					do{
						erreurHeure=1;

						System.out.print("heure_retour (O/N): ");
						if( (rep_retour=Utils.readString("O|N")).equals("N"))
							break;

						System.out.print("heure (hh:mm): ");
						heure_retour=Utils.readString("(\\d{2}:\\d{2})|");
						try{
							date_retour= format.parse(dateFin + " " +heure_retour +":00");
						} catch (ParseException ex){
							ex.printStackTrace();
						}

						if( nbReservations < nbVehicule)
							break;

						String requete = "SELECT date_reservation FROM avec_transport WHERE id_service_transport =" + id_transport + " AND date(date_reservation) = '" + dateFin +"'";
						select2 = conn.prepareStatement(requete);
						// vérifie si c'est disponible
						result2 = select2.executeQuery();
						Date date_reserv= null;
						while(result2.next()) {
							if(date_reserv.equals(date_retour)){
								// getTime() en ms
								long diff_retour = Math.abs( date_reserv.getTime() - date_retour.getTime() );
								long diffHours_retour = diff_retour / (60 * 60 * 1000) % 24;

								// si c'est à la même heure
								if( diffHours_retour==0){
									long diffMinutes_retour = diff_retour / (60 * 1000) % 60;

									// indisponible si diff < 30 
									if(diffMinutes_retour < 30){
										System.out.println("impossible");
										// affiche les réservations déjà faites
										System.out.println("Liste des réservations: ");
										select2 = conn.prepareStatement(requete);
										result2 = select2.executeQuery();
										while(result2.next()) {
											System.out.println(result2.getString(1));
										}
										erreurHeure=0;
										break;
									}
								}
							}
						}
					} while(erreurHeure==0);

				}
			}

		}

		// insertion dans locataire, location, loge, concerne, avec_prestation, avec_transport

		// insertion dans la table locataire
		int id_locataire = -1;

		// récupère id_locataire après insertion avec RETURNING
		insert= conn.prepareStatement("INSERT INTO locataire(nom_locataire, prenom_locataire, adresse_locataire, num_tel, email) VALUES(?,?,?,?,?) RETURNING id_locataire " );
		insert.setString(1, nom);
		insert.setString(2, prenom);
		insert.setString(3, adresse);
		insert.setString(4, num);
		insert.setString(5, email);
		result=insert.executeQuery();
		if(result.next())
			id_locataire = result.getInt(1);


		// insertion dans la table location
		// besoin du montant total
		// appliquer le prix selon la durée de location

		float montant=0;

		select = conn.prepareStatement("SELECT prix, prix_mois FROM prix_logement WHERE id_logement = " + id_logement);
		result = select.executeQuery();
		if(result.next()) {
			// on applique %/mois
			if( duree >= 27 ){
				int prix = result.getInt(1);
				montant += prix -((result.getInt(2)/100.0)*prix);
				montant *= duree;
			} else {
				// on applique prix/nuit
				montant +=(result.getInt(1)*duree);
			}
		}

		// réduction 10% 
		select = conn.prepareStatement("SELECT COUNT(*) FROM location NATURAL JOIN loge NATURAL JOIN locataire WHERE date_debut_location < current_date AND date_debut_location > current_date - interval '6 months' AND nom_locataire='" + nom +"' AND prenom_locataire='"+ prenom +"'");
		result = select.executeQuery();
		if(result.next())
			if( result.getInt(1) >= 2 )
				montant *= 0.9;

		// regarder offre spéciale 
		select = conn.prepareStatement("SELECT date_debut_offre_promo, date_fin_offre_promo, prix_offre_promo FROM offre_promotionnelle WHERE id_logement="+id_logement + " AND date_debut_offre_promo = '" + dateDep + "' AND date_fin_offre_promo = '" + dateFin +"'");
		result = select.executeQuery();
		if(result.next()){
			montant -= (montant*(result.getInt(3)/100.0));
		}

		montant += prixPrestation*duree;

		// prix transport aller/retour
		if(rep_aller.equals("O"))
			montant += prixTransport;

		if(rep_retour.equals("O"))
			montant += prixTransport;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//get current date time with Date()
		Date date = new Date();

		int id_location =-1;
		insert = conn.prepareStatement("INSERT INTO location( date_debut_location, date_fin_location, montant_total, date_reservation_location) VALUES(?,?,?,?) RETURNING id_location " );
		insert.setDate(1, java.sql.Date.valueOf(dateDep));
		insert.setDate(2, java.sql.Date.valueOf(dateFin));
		insert.setFloat(3, montant);
		insert.setDate(4, java.sql.Date.valueOf(dateFormat.format(date)));
		result=insert.executeQuery();
		if(result.next())
			id_location = result.getInt(1);


		// insertion dans la table loge
		insert = conn.prepareStatement("INSERT INTO loge VALUES(?,?)");
		insert.setInt(1, id_location);
		insert.setInt(2, id_locataire);
		insert.executeUpdate();

		// insertion dans la table concerne
		insert = conn.prepareStatement("INSERT INTO concerne VALUES(?,?)");
		insert.setInt(1, Integer.parseInt(id_logement));
		insert.setInt(2, id_location);
		insert.executeUpdate();

		// insertion dans la table avec_prestation si nécessaire
		if( avecPrestation.equals("O")){
			insert = conn.prepareStatement("INSERT INTO avec_prestation VALUES(?,?)");
			insert.setInt(1, id_location);
			insert.setInt(2, id_prestation);
			insert.executeUpdate();
		}


		// insertion dans la table avec_transport si nécessaire
		if( avecTransportAller.equals("O") && rep_aller.equals("O") ){
			insert = conn.prepareStatement("INSERT INTO avec_transport VALUES(?,?,?)");
			insert.setInt(1, id_location);
			insert.setInt(2, id_transport);
			insert.setTimestamp(3, java.sql.Timestamp.valueOf(dateDep + " " +heure_aller + ":00"));
			insert.executeUpdate();
		}

		if( avecTransportRetour.equals("O") && rep_retour.equals("O") ){
			insert = conn.prepareStatement("INSERT INTO avec_transport VALUES(?,?,?)");
			insert.setInt(1, id_location);
			insert.setInt(2, id_transport);
			insert.setTimestamp(3, java.sql.Timestamp.valueOf(dateFin + " " +heure_retour + ":00"));
			insert.executeUpdate();
		}


	}

}