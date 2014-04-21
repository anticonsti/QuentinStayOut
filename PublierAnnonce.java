import java.util.Scanner;
import java.sql.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;


public class PublierAnnonce {
    PreparedStatement insert=null;
    PreparedStatement select=null;
    ResultSet result = null;
    Connection conn = null;

    public PublierAnnonce(Connection conn){
	this.conn=conn;
    }

    public void printMenu(){
	System.out.print("\033c");
	System.out.println("Pour pouvoir publier une annonce, il faut que vous soyez connecte.");
    	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - S'inscrire");
	System.out.println("2 - Se connecter");
	System.out.println("-------------------------------------------------------------");
    }

    public void printMenuPublierAnnonce(){
	
	this.printMenu();
	int choix = Utils.readInt();

	while( choix!=0 ){

	    if( choix ==1 ){
		System.out.print("\033c");
		System.out.println("-------------------------------------------------------------");
		System.out.println("INSCRIPTION");
		System.out.println("-------------------------------------------------------------");

		System.out.println("Nom (1~20caracteres)");
		String nom = Utils.readString("[A-Za-z]{1,20}");
	   
		System.out.println("Prenom (1~30caracteres)");
		String prenom = Utils.readString("[A-Za-z]{1,30}");

		System.out.println("Pseudo (1~30caracteres)");
		String pseudo = Utils.readString("[A-Za-z0-9]{1,30}");
	   
		String mdp="",mdp2="";
		do{
		    System.out.println("Mot de passe (1~30caracteres)");
		    mdp = Utils.readString("[A-Za-z0-9]{1,30}");

		    System.out.println("Saisir à nouveau le mot de passe");
		    mdp2 = Utils.readString("[A-Za-z0-9]{1,30}");
		}while(!mdp.equals(mdp2)); 

		try{
		    this.inscription(nom, prenom, pseudo, mdp);
		} catch (SQLException e) {
		    System.err.println(e.getMessage());
		}

		// affichage après 2s 
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			public void run(){
			    System.out.print("\033c");
			    System.out.println("Pour pouvoir publier une annonce, il faut que vous soyez connecte.");
			    System.out.println("Veuillez entrer votre choix :");
			    System.out.println("-------------------------------------------------------------");
			    System.out.println("0 - retour");
			    System.out.println("1 - S'inscrire");
			    System.out.println("2 - Se connecter");
			    System.out.println("-------------------------------------------------------------");
			}
		    }, 2000);

	    } else if( choix ==2 ){

		System.out.print("\033c");
		System.out.println("-------------------------------------------------------------");
		System.out.println("CONNEXION");
		System.out.println("-------------------------------------------------------------");

		System.out.print("Pseudo: ");
		String id = Utils.readString("[A-Za-z0-9]{1,20}");
		String pw = Utils.readPassword("Password: ");
		try{
		    if( this.connection(id,pw) == 1 ){
			MenuProprietaireConnexion mpc = new MenuProprietaireConnexion(conn);
			mpc.printMenuProprietaireConnexion(id);
		    }
		} catch (SQLException e) {
		    System.err.println(e.getMessage());
		}
		this.printMenu();

	    } else {
		this.printMenu();
	    }
	    choix = Utils.readInt();
	}

	/*
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
		String nom = Utils.readString("[A-Za-z]{1,20}");
	   
		System.out.println("Prenom (1~30caracteres)");
		String prenom = Utils.readString("[A-Za-z]{1,30}");

		System.out.println("Pseudo (1~30caracteres)");
		String pseudo = Utils.readString("[A-Za-z0-9]{1,30}");
	   
		System.out.println("Mot de passe (1~30caracteres)");
		String mdp = Utils.readString("[A-Za-z0-9]{1,30}");

		this.inscription(nom, prenom, pseudo, mdp);

		//  si un client supprime son compte, son id_proprietaire ne sera pas repris par une autre personne :
		//  de toute facon on n'aura jamais de INT nombre de clients sur le site. 
		break;

	    case 2 :
		System.out.print("Pseudo: ");
		String id = Utils.readString("[A-Za-z0-9]{1,20}");
	   
		String pw = Utils.readPassword("Password: ");

		if( this.connection(id,pw) == 1 ){
		    MenuProprietaireConnexion mpc = new MenuProprietaireConnexion(conn);
		    mpc.printMenuProprietaireConnexion(id);
		}else{
		    System.out.println();
		}
		    
		break;

	    default :
		//on recommence le switch puisque l'utilisateur a entre un nombre bizarre.
		printMenuPublierAnnonce();
		break;
	    }
	}catch (SQLException e) {
	    System.err.println(e.getMessage());
	}
	*/
    }


    public void inscription(String nom, String prenom, String pseudo, String mdp) throws SQLException {

	insert = conn.prepareStatement("INSERT INTO proprietaire(nom_proprietaire,prenom_proprietaire,pseudo,mot_de_passe) VALUES(?,?,?,?)");

	insert.setString(1,nom);
	insert.setString(2,prenom);
	insert.setString(3,pseudo);
	insert.setString(4,mdp);
	insert.executeUpdate();  
	System.out.println("Inscription terminee! ");

    }


    public int connection(String id, String pw) throws SQLException{

	select = conn.prepareStatement("SELECT mot_de_passe FROM proprietaire WHERE pseudo='"+id+"'");
	result = select.executeQuery();

	if(result.next()){
	    if(result.getString(1).equals(pw)){
		System.out.println("Connexion etablie");
		return 1;
	    }
	    else
		System.out.println("Le mot de passe est incorrecte");
	}else
	    System.out.print("Le pseudo n'existe pas");

	return 0;

    }

}