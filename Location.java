import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class Location {

    PreparedStatement select=null;
    PreparedStatement select2=null;
    ResultSet result = null;
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
	    System.out.println("sejour_min: " +String.valueOf(result.getInt(3)));
	    System.out.println("prix/nuit: " +String.valueOf(result.getInt(4)));
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
	    System.out.println("type_suggestion: " +result.getString(1));
	    System.out.println("nom_suggestion: " +String.valueOf(result.getInt(2)));
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
	Date date1 =null, date2=null;
	do{
	    System.out.print("Date début location (YYYY-MM-DD): ");
	    try{
		dateDep = Utils.readString("date");
		date1= sdf.parse(dateDep);

		System.out.print("Date fin location (YYYY-MM-DD): ");
		dateFin = Utils.readString("date");
		date2 = sdf.parse(dateFin);
		if(!date2.after(date1))
		    System.out.println("Erreur sur la date de fin de location");
	    } catch (ParseException ex){
		ex.printStackTrace();
	    }
	}while(!date2.after(date1));

	// pour les prestations, on vérifie s'il y en a d'abord
	int prixPrestation=0;
	String avecPrestation="";
	select = conn.prepareStatement("SELECT prix_prestation FROM prestation NATURAL JOIN propose_prestation WHERE id_logement = " + id_logement);
	result = select.executeQuery();
	if(result.next()) {
	    System.out.print("Avec prestations? (O/N): ");
	    if( (avecPrestation=Utils.readString("O|N")).equals("O")){
		prixPrestation=result.getInt(1);
	    }
	}

	// pour les transports, on vérifie s'il reste des véhicules disponibles
	// comment controler les véhicules disponibles en tenant compte des réservations déjà faites
	int prixTransport=0;
	String avecTransport="";
	select = conn.prepareStatement("SELECT prix_transport FROM service_transport NATURAL JOIN propose_transport WHERE id_logement = " + id_logement + " AND nb_vehicule > 0 ");
	result = select.executeQuery();
	if(result.next()) {
	    System.out.print("Avec transport? (O/N): ");
	    if( (avecTransport=Utils.readString("O|N")).equals("O")){
		prixTransport=result.getInt(1);
	    }
	}

	
	// controler avant d'inserer dans locataire, location, loge, concerne, avec_prestation, avec_transport
	
	
	/*

	  // insertion dans la table locataire
	  insert= conn.prepareStatement("INSERT INTO locataire(nom_locataire, prenom_locataire, adresse_locataire, num_tel, email) VALUES(?,?,?,?,?)");
	  insert.setString(1, nom);
	  insert.setString(2, prenom);
	  insert.setString(3, adresse);
	  insert.setString(4, num);
	  insert.setString(5, email);
	  insert.executeUpdate(); 


	  // insertion dans la table location, besoin du montant total
	  // appliquer le prix selon la durée de location

	  int duree = Integer.parseInt(dateFin.substring(7)) - Integer.parseInt(dateDep.substring(7))
	  int montant=0;

	  select = conn.prepareStatement("SELECT prix, prix_mois FROM prix_logement WHERE id_logement = " + id_logement);
	  result = select.executeQuery();
	  if(result.next()) {
	  montant +=result.getInt(1);
	  }

	  montant += prixPrestation + prixTransport;
	  insert = conn.prepareStatement("INSERT INTO location( date_debut_location, date_fin_location, montant_total) VALUES(?,?,?)");
	  insert.setDate(1, java.sql.Date.valueOf(dateDep));
	  insert.setDate(2, java.sql.Date.valueOf(dateFin));
	  insert.setInt(3, montant);
	  insert.executeUpdate(); 
    


	  // insertion dans la table loge


	  // insertion dans la table concerne
	  

	  // insertion dans la table avec_prestation si nécessaire


	  // insertion dans la table avec_transport si nécessaire


	 */
    }

}