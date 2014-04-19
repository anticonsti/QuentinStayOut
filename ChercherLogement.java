import java.util.Scanner;

public class ChercherLogement {

    public ChercherLogement(){

    }

    public void printMenuChercherLogement(){
	System.out.print("\033c");
	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - afficher tous les logements disponibles");
	System.out.println("2 - chercher un logement par critère");

	int c = Utils.readInt();
	System.out.print("\033c");

	switch(c){
	case 1 : 
	    afficheLogements();
	    break;
	case 2 : 
	    chercherLogements();
	    break;
	default:
	    System.out.println("ERREUR");
	}

    }

    public static void afficheLogements(){
	System.out.println("HELLO");

    }

    public static void chercherLogements(){

    }


}