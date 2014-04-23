import java.sql.*;
import java.io.*;

public class ChercherLogement {
    PreparedStatement select=null, select2=null;
    ResultSet result = null, result2 = null;
    Connection conn = null;

    public ChercherLogement(Connection conn){
	this.conn=conn;
    }

    public void printMenu(){
	System.out.print("\033c");
	System.out.println("TROUVER UN LOGEMENT :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - afficher les logements disponibles");
	System.out.println("2 - chercher un logement par critère");
	System.out.println("-------------------------------------------------------------");
    }

    public void printMenuChercherLogement(){

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
		    Utils.printEntete("RECHERCHE AVANCEE");

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
	String requete = "SELECT * FROM logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location WHERE date_debut_dispo = date_debut_location AND date_fin_dispo = date_fin_location";
	select = conn.prepareStatement(requete);
	result = select.executeQuery();

	while (result.next()) {

	    System.out.println("");
	    resultats = 1;
	    String id_logement = String.valueOf(result.getInt(1));

	    select2=conn.prepareStatement("SELECT nom_proprietaire, prenom_proprietaire FROM proprietaire NATURAL JOIN propose_logement WHERE id_logement="+ id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next() ){
		System.out.println("propriétaire: " +result2.getString(1) + " " + result2.getString(2));
	    }
	    
	    System.out.println("id_logement: "+id_logement);
	    System.out.println("adresse: "+result.getString(2) + ", " + result.getString(4) );
	    System.out.println("surface: " +result.getString(3));

	    select2=conn.prepareStatement("SELECT date_debut_dispo, date_fin_dispo, prix FROM disponibilite NATURAL JOIN prix_logement WHERE id_logement="+ id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next() ){
		System.out.println("disponible de " + result2.getString(1) + " à " + result2.getString(2) );
		System.out.println("prix: "+result2.getString(3));
	    }
	    System.out.println("");
	    System.out.println("--------------------------------------------------------------------------");
	}

	this.nbResultat(requete, conn, select, result);
	return resultats;
    }

    public int chercherLogements()throws SQLException {

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
	
	if(adresse.equals("") && surface.equals("") && ville.equals("") && ddd.equals("") && dfd.equals("") && avec_suggestions.equals("N") &&  avec_prestations.equals("N")  && avec_transport.equals("N") && prix.equals("")){
	    return 0;
	}

	String requete;
	if( avec_suggestions.equals("O") && avec_prestations.equals("N") ){

	    requete = "SELECT adresse_logement, surface, ville, date_debut_dispo, date_fin_dispo, nom_suggestion, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN suggestion NATURAL JOIN propose_suggestion WHERE";

	} else if( suggestions.equals("") && !prestations.equals("") ){

	    requete = "SELECT adresse_logement, surface, ville, date_debut_dispo, date_fin_dispo, description_prestation, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN prestation NATURAL JOIN propose_prestation WHERE";

	} else if( !suggestions.equals("") && !prestations.equals("") ){

	    requete = "SELECT adresse_logement, surface, ville, date_debut_dispo, date_fin_dispo, nom_suggestion, description_prestation, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN suggestion NATURAL JOIN propose_suggestion NATURAL JOIN prestation NATURAL JOIN propose_prestation WHERE";

	} else {

	    requete = "SELECT adresse_logement, surface, ville, date_debut_dispo, date_fin_dispo, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite WHERE";

	}

	int and = 0;
	if(!adresse.equals("")){
	    requete += " adresse_logement ='"+adresse+"'";
	    and=1;
	}
	if(!surface.equals("")){
	    if(and==1)
		requete += " AND surface = " + surface;
	    else{
		requete += " surface = " + surface;
		and=1;
	    }
	}
	if(!ville.equals("")){
	    if(and==1)
		requete += " AND ville = '" + ville+"'";
	    else{
		requete += " ville = '" + ville +"'";
		and=1;
	    }
	}
	if(!ddd.equals("")){
	    if(and==1)
		requete += " AND date_debut_dispo >= DATE '" +ddd+"'" ;
	    else{
		requete += " date_debut_dispo >= DATE '" +ddd+"'" ;
		and=1;
	    }
	}
	if(!dfd.equals("")){
	    if(and==1)
		requete += " AND date_fin_dispo >= DATE '" +dfd+"'" ;
	    else{
		requete += " date_fin_dispo >= DATE '" +dfd+"'" ;
		and=1;
	    }
	}
	if(!suggestions.equals("")){
	    if(and==1)
		requete += " AND nom_suggestion = '" + suggestions +"'";
	    else{
		requete += " nom_suggestion = '" + suggestions +"'";
		and=1;
	    }
	}
	if(!prestations.equals("")){
	    if(and==1)
		requete += " AND description_prestation = '" + prestations+"'";
	    else{
		requete += " description_prestation = '" + prestations+"'";
		and=1;
	    }
	}
	if(!prix.equals("")){
	    if(and==1)
		requete += " AND prix = " + prix;
	    else{
		requete += " prix = " + prix;
		and=1;
	    }
	    if(rapport.equals("I"))
	       requete.replace('=','<');
	    else if(rapport.equals("S"))
		requete.replace('=','>');
	}
	if(affichage.equals("O")){
	    requete += " ORDER BY prix";
	}

	select = conn.prepareStatement(requete);
	result = select.executeQuery();

	// compter le nombre de résultat
	this.nbResultat(requete, conn, select2, result2);
	
	// affiche les résultats
	int resultats = 0;
	while (result.next()) {
	    resultats =1;

	    System.out.println("adresse_logement :" + result.getString(1));
	    System.out.println("surface :" +String.valueOf(result.getInt(2)));
	    System.out.println("ville: "+result.getString(3));
	    System.out.println("date_debut_dispo: " +result.getString(4));
	    System.out.println("date_fin_dispo: " +result.getString(5));

	    if( !suggestions.equals("") && prestations.equals("") ){

		System.out.println("prestation: " +result.getString(6));
		System.out.println("prix: " +String.valueOf(result.getInt(7)));

	    } else if( suggestions.equals("") && !suggestions.equals("") ){

		System.out.println("suggestion: " +result.getString(6));
		System.out.println("prix: " +String.valueOf(result.getInt(7)));

	    } else if( !suggestions.equals("") && !suggestions.equals("") ){

		System.out.println("nom_suggestion: " +result.getString(6));
		System.out.println("description_prestation: " +result.getString(7));
		System.out.println("prix: " +String.valueOf(result.getInt(8)));

	    } else {
		System.out.println("prix: " +String.valueOf(result.getInt(6)));
	    }
	    System.out.println("--------------------------------------------------------------------------");
	    System.out.println("");
	}

	return resultats;

    }


    public void nbResultat(String requete, Connection conn, PreparedStatement select2, ResultSet result2) throws SQLException{
	requete = requete.substring(requete.lastIndexOf("FROM")-1);
	select2 = conn.prepareStatement( "SELECT COUNT(*) " + requete );
	result2 = select2.executeQuery();
	if(result2.next())
	    System.out.println( result2.getString(1) + " logement(s) ");
	System.out.println("");

    }

}