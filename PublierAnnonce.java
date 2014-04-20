import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class PublierAnnonce {
    PreparedStatement insert=null;
    PreparedStatement select=null;
    ResultSet result = null;
    Connection conn = null;

    public PublierAnnonce(Connection conn){
	this.conn=conn;
    }

    public void printMenuPublierAnnonce(){

	System.out.println("Pour pouvoir publier une annonce, il faut que vous soyez connecte.");
    	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - S'inscrire");
	System.out.println("2 - Se connecter");
	System.out.println("-------------------------------------------------------------");
	int choix = Utils.readInt();

	try{
	    switch(choix){

	    case 0 :
		System.out.print("\033c"); //nettoyage de l'ecran
		System.out.println("Veuillez entrer votre choix :");
		System.out.println("-------------------------------------------------------------");
		System.out.println("0 - fin");
		System.out.println("1 - Publier votre annonce");
		System.out.println("2 - Trouvez un logement");
		System.out.println("-------------------------------------------------------------");
		break;

	    case 1 : 
		System.out.println("Nom (1~20caracteres)");
		String nom = Utils.readString("[A-Z]+[a-z]{1,20}");
	   
		System.out.println("Prenom (1~30caracteres)");
		String prenom = Utils.readString("[A-Z]+[a-z]{1,30}");

		System.out.println("Pseudo (1~30caracteres)");
		String pseudo = Utils.readString("[A-Z]+[a-z]{1,30}");
	   
		System.out.println("Mot de passe (1~30caracteres)");
		String mdp = Utils.readString("[A-Za-z0-9]{1,30}");

		//NOMBRE AU PIF
		this.inscription(4654684, nom, prenom, pseudo, mdp);

		//  si un client supprime son compte, son id_proprietaire ne sera pas repris par une autre personne :
		//  de toute facon on n'aura jamais de INT nombre de clients sur le site. 
		break;

	    case 2 :
		System.out.print("Pseudo: ");
		String id = Utils.readString("[a-z]{1,20}");
	   
		//System.out.print("\nPassword : ");
		//String pw = Utils.readString();
		String pw = Utils.readPassword("Password: ");
		if(!pw.matches("[A-Za-z0-9]{1,30}"))  System.out.println("Syntaxe incorrecte");

		if( this.connection(id,pw) == 0 ){
		    MenuProprietaireConnexion mpc = new MenuProprietaireConnexion(conn);
		    mpc.printMenuProprietaireConnexion(id);
		}
		    
		break;

	    default :
		System.out.println("Pour pouvoir publier une annonce, il faut que vous soyez connecte.");
		System.out.println("Veuillez entrer votre choix :");
		System.out.println("-------------------------------------------------------------");
		System.out.println("0 - retour");
		System.out.println("1 - S'inscrire");
		System.out.println("2 - Se connecter");
		System.out.println("-------------------------------------------------------------");
		break;
	    }
	}catch (SQLException e) {
	    System.err.println(e.getMessage());
	}

    }


    public void inscription(int id_proprietaire, String nom, String prenom, String pseudo, String mdp) throws SQLException {
	insert = conn.prepareStatement("INSERT INTO Proprietaire VALUES(?,?,?,?,?)");

	insert.setInt(1,id_proprietaire);
	insert.setString(2,nom);
	insert.setString(3,prenom);
	insert.setString(4,pseudo);
	insert.setString(5,mdp);
	insert.executeUpdate();
	   
	//si un client supprime son compte, son id_proprietaire ne sera pas repris par une autre personne :
	//de toute facon on n'aura jamais de INT nombre de clients sur le site. 
	System.out.println("Inscription terminee! ");

    }


    public int connection(String id, String pw) throws SQLException{

	select = conn.prepareStatement("SELECT mot_de_passe FROM proprietaire WHERE pseudo='"+id+"'");
	result = select.executeQuery();

	if(result.next()){
	    if(result.getString(1).equals(pw)){
		System.out.println("Connexion etablie");
		return 0;
	    }
	    else
		System.out.println("Le mot de passe est incorrecte");
	}else
	    System.out.print("Le pseudo n'existe pas");

	return 1;

    }

}