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
	System.out.println("0 - Retour");
	System.out.println("1 - S'inscrire");
	System.out.println("2 - Se connecter");
	System.out.println("-------------------------------------------------------------");
    }

    public void printMenuPublierAnnonce(){
	
	this.printMenu();
	int choix = Utils.readInt();

	try{
	    while( choix!=0 ){

		switch(choix){

		case 1:
		    Utils.printEntete("INSCRIPTION");

		    System.out.print("Nom (1~20caracteres): ");
		    String nom = Utils.readString("[A-Za-z]{1,20}");
		    System.out.print("Prenom (1~30caracteres): ");
		    String prenom = Utils.readString("[A-Za-z]{1,30}");

		    int verifPseudo=1;
		    String pseudo = "";
		    do {
			verifPseudo=1;
			System.out.print("Pseudo (1~30caracteres): ");
			pseudo = Utils.readString("[A-Za-z0-9]{1,30}");

			// vérifie si le pseudo n'existe pas
			select = conn.prepareStatement("SELECT pseudo FROM proprietaire");
			result = select.executeQuery();
			while(result.next()){
			    if(pseudo.equals(result.getString(1))){
				System.out.println("Pseudo existant");
				verifPseudo=0;
				break;
			    }
			}
		    }while(verifPseudo==0);
	   
		    String mdp="",mdp2="";
		    do{
			System.out.print("Mot de passe (1~30caracteres): ");
			mdp = Utils.readString("[A-Za-z0-9]{1,30}");

			System.out.print("Saisir à nouveau le mot de passe: ");
			mdp2 = Utils.readString("[A-Za-z0-9]{1,30}");
		    }while(!mdp.equals(mdp2)); 

		    this.inscription(nom, prenom, pseudo, mdp);
		    Thread.sleep(1300);
		    this.printMenu();
		    break;


		case 2 :
		    Utils.printEntete("CONNEXION");

		    System.out.print("Pseudo: ");
		    String id = Utils.readString("[A-Za-z0-9]{1,20}");
		    String pw = Utils.readPassword("Password: ");

		    if( this.connection(id,pw) == 1 ){
			MenuProprietaireConnexion mpc = new MenuProprietaireConnexion(conn);
			mpc.printMenuProprietaireConnexion(id);
		    } else
			Thread.sleep(1300);

		    this.printMenu();
		    break;

		default:
		    this.printMenu();
		
		}
		choix = Utils.readInt();
	    }
	}catch (SQLException | InterruptedException e) {
	    System.err.println(e.getMessage());
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
	    } else
		System.out.println("Le mot de passe est incorrecte");
	}else
	    System.out.print("Le pseudo n'existe pas");

	return 0;

    }

}