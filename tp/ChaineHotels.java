import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class ChaineHotels {
    //static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    static Scanner in = new Scanner(System.in);
    static ConnectionHotel connecte;


    /** Imprime le menu a l'ecran.*/
    public static int printMenu() {
	int c = -1; // le choix de l'utilisateur
	
	System.out.print("\033c"); //nettoyage de l'ecran
	
	// -------------------
	// Impression du menu
	// -------------------
	
	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - fin");
	System.out.println("1 - creation de la table hotel");
	System.out.println("2 - suppression de la table hotel");
	System.out.println("3 - insertion de quelques tuples exemples dans la table hotel");
	System.out.println("4 - visualisation de la table hotel");
	System.out.println("5 - insertion d'un tuple utilisateur dans la table hotel");
	System.out.println("6 - suppression d'un tuple de la table hotel");
	System.out.println("7 - modification des etoiles d'un hotel donne");
	System.out.println("-------------------------------------------------------------");
	
	// ----------------------------
	// Lecture du choix utilisateur
	// ----------------------------
	
	c = readInt();
	
	System.out.print("\033c"); //nettoyage de l'ecran
	
	// -------------------------------
	// traitement du choix utilisateur
	// -------------------------------
	
	try 
	    {
		switch(c){
		case 1 : 
		    System.out.println("Creation de la table Hotel");
		    connecte.creationTable();
		    break;

		case 2 :
		    System.out.println("Suppression de la table hotel.");
		    connecte.suppressionTable();
		    break;

		case 3 :
		    System.out.println("Insertion de quelques tuples exemples dans la table hotel.");	
		    connecte.insertionTuplesPredefinis();
		    break;
		    
		case 4 :
		    System.out.println("Visualisation du contenu de la table Hotel");	
		    ResultSet contenu = connecte.contenuTable();
		    print("NUM", 6);
		    print("NOM", 40);
		    print("VILLE", 9);
		    print("DIRECTEUR", 40);
		    print("ETOILES", 1);
		    System.out.println();
		    while (contenu != null && contenu.next()) {
			print(String.valueOf(contenu.getInt("num")), 6);
			print(contenu.getString("nom"), 40);
			print(contenu.getString("ville"), 9);
			print(contenu.getString("directeur"), 40);
			print(String.valueOf(contenu.getInt("etoiles")), 1);
			System.out.println();
		    }
		    break;

		case 5 :	
		    System.out.println("Insertion tuples definis par l'utilisateur dans la table hotel.");
		    int num;
		    String nom;
		    String ville, directeur;
		    int etoiles;
		    System.out.println("numero");
		    num=readInt();
		    System.out.println("nom");
		    nom = readString();
		    System.out.println("ville");
		    ville =  readString();
		    System.out.println("directeur");
		    directeur =  readString();
		    System.out.println("etoiles");
		    etoiles =  readInt();
		    connecte.insertionTuplesUtilisateur(num,nom,ville,etoiles, directeur);
		    //System.out.println("A implementer");
		    //connecte.insertionTuplesUtilisateur(num, nom, ville,etoiles, directeur);
		    break;
		    
		case 6 :
		    System.out.println("Suppression d'un tuple de la table hotel avec demande du numero de l'hotel a supprimer");
		    System.out.println("A implementer");
		    // definir la  methode adequate dans ConnectionHotels
		    break;
		
		case 7 :
		    System.out.println("Modification des etoiles d'un hotel donne.");
		    System.out.println("A implementer");
		    // definir la  methode adequate dans ConnectionHotels
		    break;
		    
		case 0 : 
		    System.out.println("FIN");
		    break;
		    
		default : 
		    System.out.println("ERREUR!");
		}
	    }
	catch (SQLException e) {
	    System.err.println(e.getMessage());
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
	System.out.println("usage : java ChaineHotels <nomUtilisateur>");
	System.exit(1);
    }
    
    /**Cree la connexion a la base et attend les instructions de l'utilisateur.*/
    public static void main(String[] args) {
	// ---------------------------
	// Verification des parametres
	// ---------------------------
	
	if (args.length != 1)
	    usage();
	
	
	try 
	    {
		// -------------------
		// Connexion a la base
		// --------------------
		String password = PasswordField.readPassword("Entrer votre mot de passe pour vous connecter a Postgres: ");
		connecte = new ConnectionHotel(args[0], password);
		
		
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
