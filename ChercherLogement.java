import java.sql.*;
import java.text.ParseException;
import java.io.*;

public class ChercherLogement {
    PreparedStatement select=null, select2=null, select3=null;
    ResultSet result = null, result2 = null, result3=null;
    Connection conn = null;

    public ChercherLogement(Connection conn){
	this.conn=conn;
    }

    public void printMenu(){
	System.out.print("\033c");
	System.out.println("TROUVER UN LOGEMENT :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - Retour");
	System.out.println("1 - Les logements disponibles");
	System.out.println("2 - Recherche par critère");
	System.out.println("-------------------------------------------------------------");
    }

    public void printMenuChercherLogement() throws ParseException{

	this.printMenu();
	int choix = Utils.readInt();
	Location loc = new Location(conn);

	try{

	    while( choix!=0 ){

		if( choix == 1 ){
		    Utils.printEntete("LOGEMENTS DISPONIBLES");
		    
		    if( this.afficheLogements() == 1 ){
			loc.printLocation();
			this.printMenu();
		    } else {
			System.out.println("aucun résultat");
			Thread.sleep(1300);
			this.printMenu();
		    }

		} else if( choix == 2){
		    Utils.printEntete("RECHERCHE AVANCEE (appuyer sur Entree pour passer)");

		    if( this.chercherLogements() == 1 ){
			loc.printLocation();
			this.printMenu();
		    } else {
			System.out.println("aucun résultat");
			Thread.sleep(1300);
			this.printMenu();
		    }

		} else {
		    this.printMenu();
		}

		choix = Utils.readInt();
	    }

	} catch(SQLException | InterruptedException e){
	    e.printStackTrace();
	}

    }

    public int afficheLogements()throws SQLException{

	int resultats = 0;
	String requete = "SELECT * FROM concerne";
	select = conn.prepareStatement(requete);
	result = select.executeQuery();
	if(result.next()!=false){

	    requete = "WITH dispo AS (SELECT DISTINCT id_logement, date_debut_dispo, date_fin_dispo FROM disponibilite NATURAL JOIN prix_logement NATURAL JOIN concerne ), dureelogement AS (SELECT id_logement, SUM(date_fin_dispo - date_debut_dispo ) AS dureelog FROM dispo GROUP BY id_logement ), logementsoccupes AS (SELECT id_logement, date_debut_location, date_fin_location FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location WHERE( date_debut_location, date_fin_location ) OVERLAPS ( date_debut_dispo, date_fin_dispo ) ), dureeoccupee AS (SELECT id_logement, SUM(date_fin_location-date_debut_location) AS dureeoccup FROM logementsoccupes GROUP BY id_logement), nbreservation AS (SELECT id_logement, COUNT(*) AS nb FROM logementsoccupes GROUP BY id_logement ), selectionne AS( (SELECT DISTINCT id_logement FROM logement EXCEPT SELECT id_logement FROM dureelogement NATURAL JOIN nbreservation NATURAL JOIN dureeoccupee WHERE dureeoccup + nb -1 = dureelog ) )";

	    String requete2 = requete + " SELECT * FROM selectionne ";
	    select = conn.prepareStatement(requete2);
	    result = select.executeQuery();

	} else{
	    requete ="SELECT * FROM logement";
	    select = conn.prepareStatement(requete);
	    result = select.executeQuery();

	}
	while (result.next()) {

	    System.out.println("");
	    resultats = 1;
	    String id_logement = String.valueOf(result.getInt(1));

	    select2=conn.prepareStatement("SELECT nom_proprietaire, prenom_proprietaire FROM proprietaire NATURAL JOIN propose_logement WHERE id_logement="+ id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next() ){
		System.out.println("Propriétaire: " +result2.getString(1) + " " + result2.getString(2));
	    }
	    
	    select3=conn.prepareStatement("SELECT numero_chambre FROM chambre WHERE id_logement = " + id_logement);
	    result3 = select3.executeQuery();
	    if( result3.next() ){
		System.out.println("id_logement: "+id_logement + ", chambre n°" + result3.getString(1));
	    } else {
		select3=conn.prepareStatement("SELECT nb_pieces FROM appartement WHERE id_logement = " + id_logement);
		result3 = select3.executeQuery();
		if( result3.next() )
		    System.out.println("id_logement: "+id_logement + ", appartement " + result3.getString(1) + " pièces ");
	    }

	    select2=conn.prepareStatement("SELECT adresse_logement, ville, surface FROM logement WHERE id_logement="+ id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next() ){
		System.out.println("Adresse: "+result2.getString(1) + ", " + result2.getString(2) );
		System.out.println("Surface: " +result2.getString(3));
	    }

	    select2=conn.prepareStatement("SELECT date_debut_dispo, date_fin_dispo, prix FROM disponibilite NATURAL JOIN prix_logement WHERE id_logement="+ id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next() ){
		System.out.println("Disponible de " + result2.getString(1) + " à " + result2.getString(2) );
		System.out.println("Prix/nuit: "+result2.getString(3));
	    }
	    System.out.println("");
	    System.out.println("--------------------------------------------------------------------------");
	}

	this.nbResultat(requete, conn, select, result, resultats);
	return resultats;
    }

    public int chercherLogements()throws SQLException {
	
	System.out.print("Appartement ou Chambre (A|C): ");
	String type_log = Utils.readString("A|C|");

	System.out.print("Adresse: ");
	String adresse = Utils.readString("[A-Za-z ]{0,100}");

	System.out.print("Surface: ");
	String surface = Utils.readString("[0-9]{0,5}");

	System.out.print("Ville: ");
	String ville = Utils.readString("[A-Za-z ]{0,20}");

	System.out.print("Date début disponibilité: ");
	String ddd = Utils.readString("(((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))|");

	System.out.print("Date fin disponibilité: ");
	String dfd = Utils.readString("(((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]))|");

	String avec_suggestions="", suggestions="";
	System.out.print("Avec suggestions ? (O/N): ");
	if( (avec_suggestions=Utils.readString("O|N")).equals("O")){
	    System.out.print("nom suggestion: ");
	    suggestions = Utils.readString("[A-Za-z ]{0,100}");
	}

	String avec_prestations="", prestations="";
	System.out.print("Avec prestations ? (O/N): ");
	if( (avec_prestations=Utils.readString("O|N")).equals("O")){
	    System.out.print("nom prestation: ");
	    prestations = Utils.readString("[A-Za-z ]{0,100}");
	}

	String avec_transport="", heure_aller="", heure_retour="";
	System.out.print("Avec transport ? (O/N): ");
	if( (avec_transport=Utils.readString("O|N")).equals("O")){

	    System.out.print("Heure aller (hh:mm): ");
	    heure_aller=Utils.readString("(\\d{2}:\\d{2})|");

	    System.out.print("Heure retour (hh:mm): ");
	    heure_retour=Utils.readString("(\\d{2}:\\d{2})|");
	}

	System.out.print("Prix/nuit (Inférieur[I], Supérieur[S], Egale[E]): ");
	String rapport = Utils.readString("[ISE]{0,1}");
	System.out.print("à: ");
	String prix = Utils.readString("[0-9]{0,10}");

	System.out.print("Affichage par prix croissant (O/N): ");
	String affichage = Utils.readString("[ON]{1}");
	System.out.println();
	
	if( type_log.equals("") && adresse.equals("") && surface.equals("") && ville.equals("") && ddd.equals("") && dfd.equals("") && avec_suggestions.equals("N") &&  avec_prestations.equals("N")  && avec_transport.equals("N") && prix.equals("")){
	    return 0;
	}
	
	String requete =" WITH dispo AS (SELECT DISTINCT id_logement, date_debut_dispo, date_fin_dispo FROM disponibilite NATURAL JOIN prix_logement NATURAL JOIN concerne ), dureelogement AS (SELECT id_logement, SUM(date_fin_dispo - date_debut_dispo ) AS dureelog FROM dispo GROUP BY id_logement ), logementsoccupes AS (SELECT id_logement, date_debut_location, date_fin_location FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location WHERE( date_debut_location, date_fin_location ) OVERLAPS ( date_debut_dispo, date_fin_dispo ) ), dureeoccupee AS (SELECT id_logement, SUM(date_fin_location-date_debut_location) AS dureeoccup FROM logementsoccupes GROUP BY id_logement), nbreservation AS (SELECT id_logement, COUNT(*) AS nb FROM logementsoccupes GROUP BY id_logement ), selectionne AS( (SELECT id_logement, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite  ";

	if ( type_log.equals("A") )
	    requete += " NATURAL JOIN appartement ";

	if ( type_log.equals("C") )
	    requete += " NATURAL JOIN chambre ";

	if( avec_suggestions.equals("O") )
	    requete += " NATURAL JOIN suggestion NATURAL JOIN propose_suggestion ";

	if( avec_prestations.equals("O") )
	    requete += " NATURAL JOIN prestation NATURAL JOIN propose_prestation ";

	if( avec_transport.equals("O") ) 
	    requete += " NATURAL JOIN avec_transport NATURAL JOIN propose_transport ";

	requete += " WHERE ";

	if(!adresse.equals(""))
	    requete += " adresse_logement ='"+adresse+"' AND ";

	if(!surface.equals(""))
	    requete += " surface = " + surface + " AND ";

	if(!ville.equals(""))
	    requete += " ville = '" + ville+"' AND " ;

	if(!ddd.equals(""))
	    requete += " date_debut_dispo >= DATE '" +ddd+"' AND " ;

	if(!dfd.equals(""))
	    requete += " date_fin_dispo >= DATE '" +dfd+"' AND " ;

	if(!suggestions.equals(""))
	    requete += " nom_suggestion = '" + suggestions +"' AND ";

	if(!prestations.equals(""))
	    requete += " description_prestation = '" + prestations+"' AND ";

	if(!prix.equals("")){
	    if(rapport.equals("I"))
		requete += " prix < " + prix + " AND ";
	    else if(rapport.equals("S"))
		requete += " prix > " + prix+ " AND ";
	    else
		requete += " prix = " + prix+ " AND ";
	}

	// on retire le AND en trop
	requete = requete.substring(0, requete.length()-4);

	if( !heure_aller.equals("") && !ddd.equals("") )
	    requete += " EXCEPT SELECT id_logement, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN avec_transport NATURAL JOIN concerne WHERE TIMESTAMP '"+ ddd+ " " + heure_aller +":00 ' > date_reservation - interval '30 minutes' AND TIMESTAMP '" +  ddd+ " " + heure_aller +":00 ' < date_reservation + interval '30 minutes' ";

	if( !heure_retour.equals("") && !dfd.equals("") )
	    requete += " EXCEPT SELECT id_logement, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN avec_transport NATURAL JOIN concerne WHERE TIMESTAMP '"+ dfd+ " " + heure_retour +":00 ' > date_reservation - interval '30 minutes' AND TIMESTAMP '" +  dfd + " " + heure_retour +":00 ' < date_reservation + interval '30 minutes' ";

	requete += " EXCEPT SELECT id_logement, prix FROM prix_logement NATURAL JOIN dureelogement NATURAL JOIN nbreservation NATURAL JOIN dureeoccupee WHERE dureeoccup + nb -1 = dureelog ";


	if(affichage.equals("O")){
	    requete += " ORDER BY prix )) ";
	} else
	    requete += " ))";
	
	String req = requete + "SELECT * FROM selectionne ";
	select = conn.prepareStatement(req);
	result = select.executeQuery();
	
	// affiche les résultats
	int resultats = 0;
	while (result.next()) {
	    resultats =1;
	    String id_logement = result.getString(1);
	    
	    select2 = conn.prepareStatement("SELECT * FROM logement WHERE id_logement=" + id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next()){
		System.out.println("id_logement: " + result2.getString(1));
		System.out.println("adresse_logement: " + result2.getString(2));
		System.out.println("surface: " + result2.getString(3));
		System.out.println("ville: "+result2.getString(4));
	    }

	    select2 = conn.prepareStatement("SELECT date_debut_dispo, date_fin_dispo, prix, prix_mois FROM disponibilite NATURAL JOIN prix_logement WHERE id_logement=" +  id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next()){
		System.out.println("date_debut_dispo: " +result2.getString(1));
		System.out.println("date_fin_dispo: " +result2.getString(2));
		System.out.println("prix/nuit: " + result2.getString(3));
		String prixMois = result2.getString(4);
		if( prixMois !=null )
		    System.out.println("prix/mois: " + result2.getString(4));
	    }

	    select2 = conn.prepareStatement("SELECT type_suggestion, nom_suggestion FROM suggestion NATURAL JOIN propose_suggestion WHERE id_logement=" +  id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next() ){
		System.out.println("type suggestion: " +result2.getString(1));
		System.out.println("nom suggestion: " +result2.getString(2));
	    }


	    select2 = conn.prepareStatement("SELECT description_prestation, prix_prestation FROM prestation NATURAL JOIN propose_prestation WHERE id_logement=" +  id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next() ){
		System.out.println("prestation: " +result2.getString(1));
		System.out.println("prix: " +result2.getString(2));
	    }


	    select2 = conn.prepareStatement("SELECT prix_transport FROM service_transport NATURAL JOIN propose_transport WHERE id_logement=" +  id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next() ){
		System.out.println("transport disponible: oui");
		System.out.println("prix: " +result2.getString(1));
	    }
	    

	    System.out.println("--------------------------------------------------------------------------");
	    System.out.println("");
	}

	this.nbResultat(requete, conn, select, result, resultats);
	return resultats;

    }


    public void nbResultat(String requete, Connection conn, PreparedStatement select, ResultSet result, int resultats) throws SQLException{
	if( resultats ==1)
	    select =  conn.prepareStatement( requete + " SELECT COUNT(*) FROM selectionne ");
	else 
	    select =  conn.prepareStatement(" SELECT COUNT(*) FROM logement");
	result = select.executeQuery();
	if(result.next())
	    System.out.println( result.getString(1) + " logement(s) ");
	System.out.println("");

    }

}