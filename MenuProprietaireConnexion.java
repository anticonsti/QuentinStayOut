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
	System.out.println("0 - deconnexion");
	System.out.println("1 - ajouter un logement");
	System.out.println("2 - visualiser les logements");
	System.out.println("3 - supprimer un logement");
	System.out.println("4 - offre promotionnelle");
	System.out.println("5- modification d'un logement");
	System.out.println("6 - visualiser les locations");

	System.out.println("-------------------------------------------------------------");
	
	int choix = Utils.readInt();
	//try{
	    switch(choix){
	    case 0:
		break;

	    case 1:
		System.out.println("Adresse entre 1~20caracteres.");
		String adresse = Utils.readString("[0-9,a-z ]{1,20}");

		System.out.println("Surface entre 1~30caracteres");
		int surface= Utils.readInt();

		System.out.println("Ville entre 1~30caracteres");
		String ville = Utils.readString("[A-Z][a-z- ]{1,20}");
	   
		System.out.println("Date début disponibilité de entre 1~30caracteres");
		String ddd = Utils.readString("date");

		break;

	    default:
		System.out.println("ERREUR");
		break;
	    }
	    /*}catch (SQLException e) {
	    System.err.println(e.getMessage());
	    }*/
    }





}