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

    public void printMenu(){
	System.out.print("\033c");
	System.out.println("PUBLIER ANNONCE");
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
		Utils.printEntete("INSCRIPTION");

		System.out.println("Nom (1~20caracteres)");
		String nom = Utils.readString("[A-Za-z]{1,20}");
	   
		System.out.println("Prenom (1~30caracteres)");
		String prenom = Utils.readString("[A-Za-z]{1,30}");

		int verifPseudo=1;
		String pseudo = "";
		do {
		    verifPseudo=1;
		    System.out.println("Pseudo (1~30caracteres)");
		    pseudo = Utils.readString("[A-Za-z0-9]{1,30}");

		    // vérifie si le pseudo n'existe pas
		    try{
			select = conn.prepareStatement("SELECT pseudo FROM proprietaire");
			result = select.executeQuery();
			while(result.next()){
			    if(pseudo.equals(result.getString(1))){
				System.out.println("pseudo existant");
				verifPseudo=0;
				break;
			    }
			}
		    } catch (SQLException e) {
			System.err.println(e.getMessage());
		    }
		}while(verifPseudo==0);
	   
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

		// affichage après 1.3s 
		try {
		    Thread.sleep(1300);
		} catch (InterruptedException e) {
		    System.err.println(e.getMessage());
		}
		this.printMenu();

	    } else if( choix ==2 ){

		Utils.printEntete("CONNEXION");

		System.out.print("Pseudo: ");
		String id = Utils.readString("[A-Za-z0-9]{1,20}");
		String pw = Utils.readPassword("Password: ");
		try{
		    if( this.connection(id,pw) == 1 ){
			MenuProprietaireConnexion mpc = new MenuProprietaireConnexion(conn);
			mpc.printMenuProprietaireConnexion(id);
		    } else{
			try {
			    Thread.sleep(1300);
			} catch (InterruptedException e) {
			    System.err.println(e.getMessage());
			}
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