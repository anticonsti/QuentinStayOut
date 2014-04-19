import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.io.BufferedReader;


public class Navigation {
    //static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    static Scanner in = new Scanner(System.in);
    static ConnectionAirChambre connecte;
    static ChercherLogement cl;

    /*
    public static void affichageMenuPrincipal(){

    }

    public static void affichageAnnonce(){

    }
    */
    /*
   public void inscription() throws SQLException {


    }


    public void connection(){

    }
    */

    public static void publierAnnonce(){

	System.out.println("Pour pouvoir publier une annonce, il faut que vous soyez connecte.");
    	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - S'inscrire");
	System.out.println("2 - Se connecter");
	System.out.println("-------------------------------------------------------------");
	int choix = readInt();

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
	    System.out.println("Nom entre 1~20caracteres.");
	    String nom = readString();
	    if(!nom.matches("[a-z]{1,20}")) System.out.println("Syntaxe incorrecte");
	   
	    System.out.println("Prenom entre 1~30caracteres");
	    String prenom = readString();
	    if(!nom.matches("[a-z]{1,30}"))  System.out.println("Syntaxe incorrecte");

	    System.out.println("Pseudo entre 1~30caracteres");
	    String pseudo = readString();
	    if(!nom.matches("[a-z]{1,30}"))  System.out.println("Syntaxe incorrecte");
	   
	    System.out.println("Mot de entre 1~30caracteres");
	    String mdp = readString();
	    if(!nom.matches("[a-z]{1,30}"))  System.out.println("Syntaxe incorrecte");

	    connecte.inscription(4654684, nom, prenom, pseudo, mdp);
	    //NOMBRE AU PIF POUR COMPILER CETTE MERDE

	    /*
	      si un client supprime son compte, son id_proprietaire ne sera pas repris par une autre personne :
	      de toute facon on n'aura jamais de INT nombre de clients sur le site. 
	    */
	    break;
	case 2 : //connection()
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

    }
    







    /** Imprime le menu a l'ecran.*/
    public static int printMenu() {
	int c = -1; // le choix de l'utilisateur
	
	// -------------------
	// Impression du menu
	// -------------------
	
		System.out.print("\033c"); //nettoyage de l'ecran
	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - fin");
	System.out.println("1 - Publier votre annonce");
	System.out.println("2 - Trouvez un logement");
	System.out.println("-------------------------------------------------------------");
	
	// ----------------------------
	// Lecture du choix utilisateur
	// ----------------------------
	
	c = readInt();
	
	System.out.print("\033c"); //nettoyage de l'ecran
	
	// -------------------------------
	// traitement du choix utilisateur
	// -------------------------------

	/*////////////////////////////////////AVEC TRY CATCH
	
	try{
	    switch(c){
	    case 1 : 
		publierAnnonce();
		break;

	    case 2 :
		//appelle la classe ChercherLogement
		break;
		
	    case 0 : 
		System.out.println("FIN");
		break;
		
	    default : affichageMenuPrincipal();
		break;
	    }
	}
	catch (SQLException e) {

	    System.err.println(e.getMessage());
	    }*/
	return c;

	///////////////////////////////////////AVEC TRY CATCH


	//SANS TRY CATCH => JE PENSE QUE C'EST INUTILE
	switch(c){
	case 1 : 
	    publierAnnonce();
	    break;

	case 2 :
	    //appelle la classe ChercherLogement
	    cl.printMenuChercherLogement();
	    break;
		
	case 0 : 
	    System.out.println("FIN");
	    break;
		
	default : 
	System.out.print("\033c"); //nettoyage de l'ecran
	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - fin");
	System.out.println("1 - Publier votre annonce");
	System.out.println("2 - Trouvez un logement");
	System.out.println("-------------------------------------------------------------");
	    break;
	}
       
	return c;


    }

    static public String readString(){
	try{
	    return in.next();
	}
	catch(Exception e){
	    e.printStackTrace();
	    return null;
	}
    }
    
    static public int readInt(){
	try{
	    return in.nextInt();   //lecture du choix utilisateur    
	}
	catch(Exception e){
	    in.nextLine();
	    e.printStackTrace();
	    return -1;
	}
    }
    
    public static void print(String s, int i) {
	System.out.print(s);
	for (i -= s.length(); i >= 0; i --)
	    System.out.print(" ");
    }
    
    /**Imprime les recommandations d'usage de la classe ChaineHotels a l'ecran.*/
    public static void usage() {
	System.out.println("Veuillez entrer votre nom identifiant pour Postgres.");
	System.out.println("usage : java java -cp ../postgresql-9.3-1101.jdbc41.jar: Navigation <nomUtilisateur>");
	System.exit(1);
    }
    
    /**Cree la connexion a la base et attend les instructions de l'utilisateur.*/
    public static void main(String[] args) {
	// ---------------------------
	// Verification des parametres
	// ---------------------------
	
	if (args.length != 1)
	    usage();

	
	try{
	    // -------------------
	    // Connexion a la base
	    // --------------------
	    String password = PasswordField.readPassword("Entrer votre mot de passe pour vous connecter a Postgres: ");
	    connecte = new ConnectionAirChambre(args[0], password);
		
		
	    // ---------------------------------------
	    // Impression du menu. Pour finir, tapez 0
	    // ---------------------------------------
		
	    int c = -1;
	    while(c != 0){
		c = printMenu();
		if (c != 0){
		    System.out.println("Appuyez sur entree.");
		    System.in.read();
		}
	    }
		
		
	    // -------------------------
	    // fermeture de la connexion
	    // -------------------------
	    connecte.close();
	    in.close();
		
	}
	catch(Exception e)
	    {
		e.printStackTrace();
	    }
    }
    
} 
