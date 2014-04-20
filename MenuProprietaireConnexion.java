import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class MenuProprietaireConnexion{
    PreparedStatement insert=null;
    PreparedStatement select=null;
    ResultSet result = null;
    Connection conn=null;
    
    public MenuProprietaireConnexion(Connection conn){
	this.conn=conn;
    }

    public void printMenuProprietaireConnexion(String pseudo){
	System.out.print("\033c");
	System.out.println("Compte " + pseudo);
    	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - déconnexion");
	System.out.println("1 - ajouter un logement");
	System.out.println("2 - visualiser les logements");
	System.out.println("3 - supprimer un logement");
	System.out.println("4 - offre promotionnelle");
	System.out.println("5 - modification d'un logement");
	System.out.println("6 - visualiser les locations");

	System.out.println("-------------------------------------------------------------");
	
	int choix = Utils.readInt();
	Logement lgm = new Logement(conn);

	try{
	    switch(choix){
	    case 0:
		break;

	    case 1:
		System.out.println("Votre logement est un appartement(A) ou une chambre(C)? entrer A ou C.");
		String typeLogement = Utils.readString("[0-9a-z]{1,20}");	
	
		System.out.println("Adresse (1~20 caracteres): ");
		String adresse = Utils.readString("[0-9a-z]{1,20}");

		System.out.println("Surface: ");
		int surface= Utils.readInt();

		System.out.println("Ville entre (1~30 caracteres): ");
		String ville = Utils.readString("[A-Za-z]{1,20}");
	   
		System.out.println("Date début disponibilité (format DD/MM/YYYY): ");//format sql annee/mois/jour
		String dateDep = Utils.readString("date");

		System.out.println("Date fin disponibilité (format DD/MM/YYYY): ");
		String dateFin = Utils.readString("date");		

		//System.out.println("Prix du logement par jour.");
		//int prixJour = Utils.readInt();
		System.out.println("Prix: ");
		int prix = Utils.readInt();

		//IL FAUT VERIFIER QUE l'UTILISATEUR A REPONDU AUX prints FACULTATIF AVEC HASNEXT..() JE PENSE hasNextInt() hasNextLine()
		// Pour passer au champ suivant il faut appuyer sur Entree
		// comme c'est facultatif, on met {0,...}
		System.out.println("(facultatif) Prix du logement par mois (cas 27 jours ou plus): ");
		String prixMois = Utils.readString("[0-9]{0,5}");

		System.out.println("(facultatif) Suggestions: ");
		String sugg = Utils.readString("[A-Za-z]{0,20}");		

		System.out.println("(facultatif) Prestations");
		String prest = Utils.readString("[A-Za-z]{0,20}");	

		System.out.println("(facultatif) Photos");//boucle infini jusqu'a ce que l'utilisateur dit FALSE
		String photo = Utils.readString("[A-Za-z]{0,20}");

		System.out.println("(facultatif) Transport");//il faut 2print pour nb vehicule,prix transport
		String transport = Utils.readString("[A-Za-z]{0,20}");
	    
	    
		lgm.ajouterLogementDispo(dateDep,dateFin);
		lgm.ajouterLogementLogement(adresse,surface,ville);
		lgm.ajouterLogementPrix(prix);

		/*
		  if(sugg!="") lgm.ajouterLogementSuggestion();
		  if(prest!="") lgm.ajouterLogementPrestation();
		  if(photo!="") lgm.ajouterLogementPhoto();
		  if(transport!="") lgm.ajouterLogementTransport();
		*/	    

		break;

	    case 2: 
		break;
	    case 3:
		break;
	    case 4:
		break;
	    case 5:
		break;
	    case 6:
		break;

	    default:
		System.out.println("ERREUR");

		break;
	    }
	}catch (SQLException e) {
	    System.err.println(e.getMessage());
	}
    }





}