import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;


public class Location {

    PreparedStatement select=null, select2=null;
    PreparedStatement insert=null;
    PreparedStatement delete=null;
    ResultSet result = null, result2=null;
    Connection conn = null;

    public Location(Connection conn){
	this.conn=conn;
    }

    public void printLocation(){

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
		    select = conn.prepareStatement("SELECT id_logement FROM propose_logement WHERE id_logement = " + id_logement);
		    result = select.executeQuery();
		    rsNext = result.next();
		    if(rsNext==false)
			System.out.println("Inexistant");
		}while( rsNext == false);
		
		// détails du logement
		Utils.printEntete("VOTRE LOGEMENT");
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

	select = conn.prepareStatement("SELECT * FROM logement WHERE id_logement = " + id_logement);
	result = select.executeQuery();
	if(result.next()) {
	    System.out.println("id_logement: " + String.valueOf(result.getInt(1)));
	    System.out.println("adresse: " +result.getString(2));
	    System.out.println("surface: " +result.getString(3));
	    System.out.println("ville: " +result.getString(4));
	}

	select = conn.prepareStatement("SELECT date_debut_dispo, date_fin_dispo, sejour_min, prix, prix_mois FROM disponibilite NATURAL JOIN prix_logement WHERE id_logement = " + id_logement);
	result = select.executeQuery();
	if(result.next()) {
	    System.out.println("date_debut_dispo: " + result.getDate(1));
	    System.out.println("date_fin_dispo: " +result.getDate(2));
	    int sej = result.getInt(3);
	    if( sej !=0 )
		System.out.println("sejour_min: " +String.valueOf(sej));
	    System.out.println("prix/nuit: " +String.valueOf(result.getInt(4)));
	    int prixM = result.getInt(5);
	    if( prixM !=0 )
		System.out.println("prix/mois: " +String.valueOf(result.getInt(5)));
	}

	select = conn.prepareStatement("SELECT type_suggestion, nom_suggestion FROM suggestion NATURAL JOIN propose_suggestion WHERE id_logement = " + id_logement);
	result = select.executeQuery();
	if(result.next()) {
	    System.out.println("type_suggestion: " +result.getString(1));
	    System.out.println("nom_suggestion: " +result.getString(2));
	}

	select = conn.prepareStatement("SELECT description_prestation, prix_prestation FROM prestation NATURAL JOIN propose_prestation WHERE id_logement = " + id_logement);
	result = select.executeQuery();
	if(result.next()) {
	    System.out.println("prestation: " +result.getString(1));
	    System.out.println("prix: " +String.valueOf(result.getInt(2)));
	}

	select = conn.prepareStatement("SELECT prix_transport FROM service_transport NATURAL JOIN propose_transport WHERE id_logement = " + id_logement);
	result = select.executeQuery();
	if(result.next()) {
	    System.out.println("service_transport: oui");
	    System.out.println("prix_transport: " +String.valueOf(result.getInt(1)));
	}

	System.out.println("");
    }


    public void inscriptionLocataire(String id_logement) throws SQLException {

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
	int sejourMin = 0;
	int erreurDate=1;
	do{
	    erreurDate=1;
	    System.out.print("Date début location (YYYY-MM-DD): ");
	    try{
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
		
		// vérifie la durée minimum
		int duree = Integer.parseInt(dateFin.substring(8)) - Integer.parseInt(dateDep.substring(8));
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
		    // puis on vérifie qu'on ne loue pas sur une période déjà prise
		    // période occupée par les locataires
		    select = conn.prepareStatement("SELECT date_debut_location, date_fin_location FROM logement NATURAL JOIN concerne NATURAL JOIN location WHERE id_logement = " + id_logement);
	
		    result = select.executeQuery();
		    while(result.next()) {
			dateDLocat=sdf.parse(result.getString(1));
			dateFLocat=sdf.parse(result.getString(2));

			if( ( date1.after(dateDLocat) && date1.before(dateFLocat) ) || (date2.after(dateDLocat) && date2.before(dateFLocat)) ){
			    erreurDate=0;
			    System.out.println("Erreur sur la date de location");
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
	String avecTransport="";
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String heure_aller="", heure_retour="";
	Date date_aller =null, date_retour=null;
	String rep_aller="", rep_retour="";

	// on vérifie qu'il y a un service de transport
	select = conn.prepareStatement("SELECT id_service_transport, prix_transport, nb_vehicule FROM propose_transport NATURAL JOIN service_transport WHERE id_logement =" + id_logement);
	result = select.executeQuery();
	if(result.next()) {
	    id_transport =  result.getInt(1);
	    prixTransport=result.getInt(2);
	    int nbVehicule = result.getInt(3);

	    // on supprime les réservations dépassées
	    delete = conn.prepareStatement("DELETE FROM avec_transport WHERE date_reservation + interval ' 30 minutes ' < CURRENT_TIMESTAMP ");
	    delete.executeUpdate();

	    // vérifie le nombre de réservation
	    select2 = conn.prepareStatement("SELECT COUNT(*) FROM avec_transport WHERE id_service_transport=" + id_transport );
	    result2 = select2.executeQuery();
	    if( result2.next()){
		int nbReservations = result2.getInt(1);
	    
		// vérifie qu'il reste des végicules disponibles
		if( nbReservations < nbVehicule){

		    System.out.print("Avec transport? (O/N): ");
		    if( (avecTransport=Utils.readString("O|N")).equals("O")){
		

			System.out.println("");
			// vérifie heure aller et retour
			int erreurHeure=1;
			do{
			    // comparer avec toutes les autres réservations
			    select2 = conn.prepareStatement("SELECT date_reservation FROM avec_transport WHERE id_service_transport =" + id_transport);

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
			    }
			    System.out.print("heure_retour (O/N): ");
			    // on arrête si N et N
			    if( (rep_retour=Utils.readString("O|N")).equals("N") && rep_aller.equals("N") )
				break;

			    System.out.print("heure (hh:mm): ");
			    heure_retour=Utils.readString("(\\d{2}:\\d{2})|");
			    try{
				date_retour= format.parse(dateFin + " " +heure_retour +":00");
			    } catch (ParseException ex){
				ex.printStackTrace();
			    }
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
					    select2 = conn.prepareStatement("SELECT date_reservation FROM avec_transport WHERE id_service_transport =" + id_transport + " AND date(date_reservation) = '" + dateDep +"'");
					    result2 = select2.executeQuery();
					    while(result2.next()) {
						System.out.println(result2.getString(1));
					    }

					    erreurHeure=0;
					    break;
					}
				    }
				}
				// de même pour le retour 
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
					    select2 = conn.prepareStatement("SELECT date_reservation FROM avec_transport WHERE id_service_transport =" + id_transport + " AND date(date_reservation) = '" + dateFin +"'");
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

	int duree = Integer.parseInt(dateFin.substring(8)) - Integer.parseInt(dateDep.substring(8));
	int montant=0;

	select = conn.prepareStatement("SELECT prix, prix_mois FROM prix_logement WHERE id_logement = " + id_logement);
	result = select.executeQuery();
	if(result.next()) {
	    // on applique prix/mois PB comment calculer pour 1 mois et quelques jours ??
	    if( duree > 28 ){
		montant +=result.getInt(2);
	    } else {
		// on applique prix/nuit
		montant +=(result.getInt(1)*duree);
	    }
	}
	montant += prixPrestation + prixTransport;

	int id_location =-1;
	insert = conn.prepareStatement("INSERT INTO location( date_debut_location, date_fin_location, montant_total) VALUES(?,?,?) RETURNING id_location " );
	insert.setDate(1, java.sql.Date.valueOf(dateDep));
	insert.setDate(2, java.sql.Date.valueOf(dateFin));
	insert.setInt(3, montant);
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
	if( avecTransport.equals("O") && rep_aller.equals("O") ){
	    insert = conn.prepareStatement("INSERT INTO avec_transport VALUES(?,?,?)");
	    insert.setInt(1, id_location);
	    insert.setInt(2, id_transport);
	    insert.setTimestamp(3, java.sql.Timestamp.valueOf(dateDep + " " +heure_aller + ":00"));
	    insert.executeUpdate();
	}

	if( avecTransport.equals("O") && rep_retour.equals("O") ){
	    insert = conn.prepareStatement("INSERT INTO avec_transport VALUES(?,?,?)");
	    insert.setInt(1, id_location);
	    insert.setInt(2, id_transport);
	    insert.setTimestamp(3, java.sql.Timestamp.valueOf(dateFin + " " +heure_retour + ":00"));
	    insert.executeUpdate();
	}


    }

}