import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.util.Scanner;


public class Navigation {
    static Scanner in = new Scanner(System.in);
    static ConnectionAirChambre connecte;


    /** Imprime le menu a l'ecran.
     * @throws ParseException */
    public static int printMenu() throws ParseException {
	int c = -1; // le choix de l'utilisateur
	
	// Impression du menu
	System.out.print("\033c"); //nettoyage de l'ecran
	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - Fin");
	System.out.println("1 - Publier votre annonce");
	System.out.println("2 - Trouvez un logement");
	System.out.println("-------------------------------------------------------------");
	
	// Lecture du choix utilisateur
	c = Utils.readInt();
	
	System.out.print("\033c"); //nettoyage de l'ecran

	PublierAnnonce pa = new PublierAnnonce(connecte.getConnection());
	ChercherLogement cl = new ChercherLogement(connecte.getConnection());

	// traitement du choix utilisateur
	switch(c){
	case 1 : 
	    pa.printMenuPublierAnnonce();
	    break;

	case 2 :
	    cl.printMenuChercherLogement();
	    break;
		
	case 0 : 
	    System.out.println("FIN");
	    break;
		
	default : 
	    System.out.print("\033c"); //nettoyage de l'ecran
	    System.out.println("Veuillez entrer votre choix :");
	    System.out.println("-------------------------------------------------------------");
	    System.out.println("0 - Fin");
	    System.out.println("1 - Publier votre annonce");
	    System.out.println("2 - Trouvez un logement");
	    System.out.println("-------------------------------------------------------------");
	    break;
	}
	return c;
    }

 
    /**Imprime les recommandations d'usage de la classe ChaineHotels a l'ecran.*/
    public static void usage() {
	System.out.println("Veuillez entrer votre nom identifiant pour Postgres.");
	System.out.println("usage : java -cp postgresql-9.3-1101.jdbc41.jar: Navigation <nomUtilisateur>");
	System.exit(1);
    }
    

    /**Cree la connexion a la base et attend les instructions de l'utilisateur.*/
    public static void main(String[] args) {

	// Verification des parametres	
    	if (args.length != 1)
	    usage();
	
	try{
	    // Connexion a la base
	    String password = Utils.readPassword("Entrer votre mot de passe pour vous connecter a Postgres: ");
	    
	    connecte = new ConnectionAirChambre(args[0], password);

	    // Impression du menu. Pour finir, tapez 0
	    int c = -1;
	    while(c != 0){
		c = printMenu();
		if (c != 0){
		    System.out.println("Appuyez sur entree.");
		    System.in.read();
		}
	    }

	    // fermeture de la connexion
	    connecte.close();
	    in.close();	
	}
	catch(Exception e){
	    e.printStackTrace();
	}
    }
    
} 
