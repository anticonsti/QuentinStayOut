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

    public void printMenu(){
	System.out.print("\033c");
	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - afficher les logements disponibles");
	System.out.println("2 - chercher un logement par critère");
	System.out.println("-------------------------------------------------------------");
    }

    public void printMenuChercherLogement(){

	this.printMenu();
	int choix = Utils.readInt();

	try{

	    while( choix!=0 ){
		if( choix == 1 ){
		    Utils.printEntete("LOGEMENTS DISPONIBLES");
		    this.afficheLogements();
		} else if( choix == 2){
		    Utils.printEntete("RECHERCHE AVANCEE");
		    this.chercherLogements();
		} else {
		    this.printMenu();
		}

		choix = Utils.readInt();
	    }

	} catch(SQLException e){
	    e.printStackTrace();
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
	    Utils.print("| " +result.getString(2), 40);
	    Utils.print("| " +result.getString(3), 9 );
	    System.out.println("| " +result.getString(4));
	}

    }

    public void chercherLogements()throws SQLException {

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
		requete += " AND date_debut_dispo = DATE '" +ddd+"'" ;
	    else{
		requete += " date_debut_dispo = DATE '" +ddd+"'" ;
		and=1;
	    }
	}
	if(!dfd.equals("")){
	    if(and==1)
		requete += " AND date_debut_dispo = DATE '" +dfd+"'" ;
	    else{
		requete += " date_debut_dispo = DATE '" +dfd+"'" ;
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
	}
	if(affichage.equals("O")){
	    requete += " ORDER BY prix";
	}

	select = conn.prepareStatement(requete);

	System.out.println();
	
	result = select.executeQuery();

	int in = 0;
	while (result.next()) {
	    in =1;

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
	if(in==0)
	    System.out.println("aucun résultat");


    }


}