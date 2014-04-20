import java.util.Scanner;
import java.sql.*;
import java.io.*;





class Logement{
    
    PreparedStatement insert=null;
    PreparedStatement select=null;
    ResultSet result = null;
    Connection conn = null;
    
    public Logement(Connection conn){
	this.conn=conn;
    }


    /*
    public void printMenuLogement(){

	System.out.println("Pour pouvoir publier une annonce, il faut que vous soyez connecte.");
    	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - blablablah");
	System.out.println("2 - blblblblah");
	System.out.println("-------------------------------------------------------------");
	int choix = Utils.readInt();
	
	//try{
	    switch(choix){

	    case 0 :
		System.out.print("\033c"); //nettoyage de l'ecran
		System.out.println("Veuillez entrer votre choix :");
		System.out.println("-------------------------------------------------------------");
		System.out.println("0 - fin");
		System.out.println("1 - heeeeeeeeeeeeein");
		System.out.println("2 - QUEEEEEEEEEEEEEE");
		System.out.println("-------------------------------------------------------------");
		break;

	    case 1 : 
		break;

	    case 2 :
		break;

	    default :
		//on recommence le switch puisque l'utilisateur a entre un nombre bizarre.
		printMenuLogement(); 
		break;
	    }
	    //}catch (SQLException e) {
	    //System.err.println(e.getMessage());
	    //}

    }    
    */

    public void ajouterLogement(){
		
	System.out.println("Votre logement est un appartement(A) ou une chambre(C)? entrer A ou C.");
	String adresse = Utils.readString("[0-9a-z]{1,20}");	
	
	System.out.println("Adresse entre 1~20caracteres.");
	String adresse = Utils.readString("[0-9a-z]{1,20}");

	System.out.println("Surface: ");
	int surface= Utils.readInt();

	System.out.println("Ville entre 1~30caracteres");
	String ville = Utils.readString("[A-Za-z]{1,20}");
	   
	System.out.println("Date début disponibilité en format JJ/MM/AA");
	String ddd = Utils.readString("date");

	System.out.println("Date fin disponibilité en format JJ/MM/AA");
	String ddd = Utils.readString("date");		

	System.out.println("Prix du logement par jour.");
	int prixJour = Utils.readString("date");		

	System.out.println("(facultatif) Prix du logement par mois (cas 27jours ou plus)");
	if(sc.hasNextInt()) {int prixMois = Utils.readString();}

	System.out.println("(facultatif) Suggestions");
	if(sc.hasNextLine()){String sugg = Utils.readString("date");}		

	System.out.println("(facultatif) Prestations");
	if(sc.hasNextLine()){String prest = Utils.readString("date");}		

	System.out.println("(facultatif) Photos");
	if(sc.hasNextLine()){String photo = Utils.readString("date");}	




	insert = conn.prepareStatement("INSERT INTO logement VALUES(?,?,?,?)");
	
	
    }
    
    public void supprimerLogement(){
    }
    
    public void modifierLogement(){
    }





}