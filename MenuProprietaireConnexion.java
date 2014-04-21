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
	System.out.println("0 - déconnexion");
	System.out.println("1 - ajouter un logement");
	System.out.println("2 - visualiser les logements");
	System.out.println("3 - supprimer un logement");
	System.out.println("4 - offre promotionnelle");
	System.out.println("5 - modification d'un logement");
	System.out.println("6 - visualiser les locations");

	System.out.println("-------------------------------------------------------------");
	
	int choix = Utils.readInt();
	Logement lgm = new Logement(conn);

	try{
	    switch(choix){
	    case 0:
		break;

	    case 1:
		System.out.println("Votre logement est un appartement(A) ou une chambre(C)? entrer A ou C.");
		String typeLogement = Utils.readString("A|C");
		String nbPiece="", numChambre="";
		Boolean booltype=typeLogement.equals("A");
		if(booltype){
		    System.out.println("nombre de pièces: ");
		    nbPiece = Utils.readString("[1-9]+[0-9]{0,5}");
		} else {
		    System.out.println("numéro de chambre: ");
		    numChambre = Utils.readString("[0-9]{0,5}");
		}
	
		System.out.println("Adresse (1~20 caracteres): ");
		String adresse = Utils.readString("[0-9a-z]{1,20}");

		System.out.println("Surface: ");
		String surface= Utils.readString("[1-9]+[0-9]{0,5}");

		System.out.println("Ville entre (1~30 caracteres): ");
		String ville = Utils.readString("[A-Za-z]{1,20}");
	   
		System.out.println("Date début disponibilité (format YYYY-MM-DD): ");//format sql annee/mois/jour
		String dateDep = Utils.readString("date");

		System.out.println("Date fin disponibilité (format YYYY-MM-DD): ");
		String dateFin = Utils.readString("date");		

		//System.out.println("Prix du logement par jour.");
		//int prixJour = Utils.readInt();
		System.out.println("Prix: ");
		String prix = Utils.readString("[1-9]+[0-9]{0,5}");

		//IL FAUT VERIFIER QUE l'UTILISATEUR A REPONDU AUX prints FACULTATIF AVEC HASNEXT..() JE PENSE hasNextInt() hasNextLine()
		// Pour passer au champ suivant il faut appuyer sur Entree (gestion NextLine dans readString) + comme c'est facultatif, on met {0,...}
		System.out.println("(facultatif, appuyer sur Entree pour passer) Prix du logement par mois (cas 27 jours ou plus): ");
		String prixMois = Utils.readString("[1-9]{0,1}+[0-9]{0,5}");

		lgm.ajouterLogement(adresse, surface, ville, dateDep, dateFin, prix, prixMois, pseudo);
		int idLogement = lgm.getIdLogement(adresse, surface, ville);

		if(booltype)
		    lgm.ajouterAppartement(idLogement, nbPiece);
		else
		    lgm.ajouterChambre(idLogement, numChambre);


		// Pour ce qui suit, répondre par O ou N
		String typeSugg="", nomSugg="";
		System.out.println("(facultatif) Suggestions (O/N): ");
		if((Utils.readString("O|N")).equals("O")){
		    System.out.println("type (touristique/gastronomique): ");
		    typeSugg = Utils.readString("touristique|gastronomique");
		    System.out.println("(facultatif)   nom: ");
		    nomSugg = Utils.readString("[A-Za-z]{1,20}");
		    lgm.ajouterLogementSuggestion(typeSugg, nomSugg);
		    lgm.tableProposeSuggestion(typeSugg, nomSugg, idLogement);
		}

		String prest="", prixPrest="";
		System.out.println("(facultatif) Prestations (O/N):");
		if((Utils.readString("O|N")).equals("O")){
		    System.out.println("description: ");
		    prest = Utils.readString("[A-Za-z]{1,20}");
		    System.out.println("prix: ");
		    prixPrest= Utils.readString("[1-9]+[0-9]{0,5}");
		    lgm.ajouterLogementPrestation(prest, prixPrest);
		    lgm.tableProposePrestation(prest, prixPrest, idLogement);
		}	

		String photo="", rep="N";
		do{
		    System.out.println("(facultatif) Photos (O/N)");//boucle infini jusqu'a ce que l'utilisateur dit N
		    rep=Utils.readString("O|N");
		    if(rep.equals("O")){
			System.out.println("nom ");
			photo = Utils.readString("[A-Za-z]{1,20}");
			lgm.ajouterLogementPhoto(photo, idLogement);
		    }
		}while(rep.equals("O"));

		String nbVehicule="", prixTransport="";
		System.out.println("(facultatif) Transport (0/N)");
		if((Utils.readString("O|N")).equals("O")){
		    System.out.println("nombre véhicule: ");
		    nbVehicule = Utils.readString("[0-9]{1,5}");
		    System.out.println("prix transport: ");
		    prixTransport = Utils.readString("[0-9]{1,5}");
		    lgm.ajouterLogementTransport(nbVehicule, prixTransport);
		    lgm.tableProposeTransport(nbVehicule, prixTransport, idLogement);
		}

		break;

	    case 2: 
		lgm.listeLogement(lgm.getIdProprietaire(pseudo));
		break;

	    case 3:
		break;
	    case 4:
		break;
	    case 5:
		break;
	    case 6:
		break;

	    default:
		System.out.println("ERREUR");

		break;
	    }
	}catch (SQLException e) {
	    System.err.println(e.getMessage());
	}
    }





}