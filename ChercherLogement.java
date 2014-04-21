import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class ChercherLogement {
    PreparedStatement select=null;
    ResultSet result = null;
    Connection conn = null;

    public ChercherLogement(Connection conn){
	this.conn=conn;
    }

    public void printMenuChercherLogement(){
	System.out.print("\033c");
	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - afficher tous les logements disponibles");
	System.out.println("2 - chercher un logement par critère");

	int c = Utils.readInt();
	System.out.print("\033c");

	switch(c){

	case 0:
	    break;
	    
	case 1 : 
	    try{
		this.afficheLogements();
	    }
	    catch(SQLException e){
		e.printStackTrace();
	    }
	    break;

	case 2 : 
	    try{
	    this.chercherLogements();
	    }
	    catch(SQLException e){
		e.printStackTrace();
		//System.out.println("aucun résultat");
	    }
	    break;

	default:
	    System.out.println("ERREUR");
	}

    }

    public  void afficheLogements()throws SQLException{
	select = conn.prepareStatement("SELECT * FROM logement");
	Utils.print("id_logement", 15);
	Utils.print("| adresse", 40);
	Utils.print("| surface", 9);
	System.out.println("| ville");
	System.out.println("--------------------------------------------------------------------------");
	result = select.executeQuery();
	while (result.next()) {
	    Utils.print(String.valueOf(result.getInt(1)),15);
	    Utils.print(result.getString(2), 40);
	    Utils.print(result.getString(3), 9 );
	    System.out.println(result.getString(4));
	}

    }

    public void chercherLogements()throws SQLException {
	System.out.print("\033c");
	System.out.println("Recherche avancée:");
	System.out.println("-------------------------------------------------------------");
	System.out.print("Adresse: ");
	String adresse = Utils.readString("[A-Za-z ]{0,100}");

	System.out.print("Surface: ");
	String surface = Utils.readString("[0-9]{0,5}");

	System.out.print("Ville: ");
	String ville = Utils.readString("[A-Za-z ]{0,20}");

	System.out.print("Date début disponibilité: ");
	String ddd = Utils.readString("date");

	System.out.print("Date fin disponibilité: ");
	String dfd = Utils.readString("date");

	System.out.print("Suggestions: ");
	String suggestions = Utils.readString("[A-Za-z ]{0,100}");

	System.out.print("Prestations: ");
	String prestations = Utils.readString("[A-Za-z ]{0,100}");

	System.out.print("Prix: ");
	String prix = Utils.readString("[0-9]{0,10}");

	System.out.print("Affichage par prix croissant (O/N): ");
	String affichage = Utils.readString("[ON]{1}");
	System.out.println();
	
	if(adresse.equals("") && surface.equals("") && ville.equals("") && ddd.equals("") && dfd.equals("") && suggestions.equals("") && prestations.equals("") && prix.equals("")){
	    System.out.println("aucun résultat");
	    return;
	}

	String requete;
	if( !suggestions.equals("") && prestations.equals("") ){
	    requete = "SELECT adresse_logement, surface, ville, date_debut_dispo, date_fin_dispo, nom_suggestion, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN suggestion NATURAL JOIN propose_suggestion WHERE";
	} else if( suggestions.equals("") && !suggestions.equals("") ){
	    requete = "SELECT adresse_logement, surface, ville, date_debut_dispo, date_fin_dispo, description_prestation, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN prestation NATURAL JOIN propose_prestation WHERE";
	} else if( !suggestions.equals("") && !suggestions.equals("") ){
	    requete = "SELECT adresse_logement, surface, ville, date_debut_dispo, date_fin_dispo, nom_suggestion, description_prestation, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN suggestion NATURAL JOIN propose_suggestion NATURAL JOIN prestation NATURAL JOIN propose_prestation WHERE";
	} else {
	    requete = "SELECT adresse_logement, surface, ville, date_debut_dispo, date_fin_dispo, prix FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite WHERE";
	}

	int and = 0;
	if(!adresse.equals("")){
	    requete += "adresse_logement ='"+adresse+"'";
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
		requete += " AND ville = " + ville;
	    else{
		requete += " ville = " + ville;
		and=1;
	    }
	}
	if(!ddd.equals("")){
	    if(and==1)
		requete += " AND date_debut_dispo = DATE '" +ddd+"'" ;
	    else{
		requete += "date_debut_dispo = DATE '" +ddd+"'" ;
		and=1;
	    }
	}
	if(!dfd.equals("")){
	    if(and==1)
		requete += " AND date_debut_dispo = DATE '" +dfd+"'" ;
	    else{
		requete += "date_debut_dispo = DATE '" +dfd+"'" ;
		and=1;
	    }
	}
	if(!suggestions.equals("")){
	    if(and==1)
		requete += " AND nom_suggestion = " + suggestions;
	    else{
		requete += " nom_suggestion = " + suggestions;
		and=1;
	    }
	}
	if(!prestations.equals("")){
	    if(and==1)
		requete += " AND description_prestation = " + prestations;
	    else{
		requete += " description_prestation = " + prestations;
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
	}
	if(affichage.equals("O")){
	    requete += " ORDER BY prix";
	}

	select = conn.prepareStatement(requete);
	Utils.print("adresse_logement",15 );
	Utils.print("| surface", 9);
	Utils.print("| ville", 20);
	Utils.print("| date_debut_dispo",15 );
	Utils.print("| date_fin_dispo", 15);
	Utils.print("| nom_suggestion", 15);
	Utils.print("| description_prestation",15 );
	Utils.print("| prix", 9);
	System.out.println();
	
	result = select.executeQuery();
	while (result.next()) {
	    Utils.print(result.getString(1), 15);
	    Utils.print(String.valueOf(result.getInt(2)), 9 );
	    Utils.print(result.getString(3), 20);
	    Utils.print(result.getString(4), 15 );
	    Utils.print(result.getString(5), 15);
	    if( !suggestions.equals("") && prestations.equals("") ){
		Utils.print(result.getString(6), 15 );
		Utils.print(String.valueOf(result.getInt(7)), 9 );
	    } else if( suggestions.equals("") && !suggestions.equals("") ){
		Utils.print(result.getString(6), 15 );
		Utils.print(String.valueOf(result.getInt(7)), 9 );
	    } else if( !suggestions.equals("") && !suggestions.equals("") ){
		Utils.print(result.getString(6), 15 );
		Utils.print(result.getString(7), 15 );
		Utils.print(String.valueOf(result.getInt(8)), 9 );
	    } else {
		Utils.print(String.valueOf(result.getInt(6)), 9 );
	    }
	}

    }


}