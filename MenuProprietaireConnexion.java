import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
//indente le 2 mai 2014
public class MenuProprietaireConnexion{
	PreparedStatement insert=null;
	PreparedStatement select=null;
	ResultSet result = null;
	Connection conn=null;

	public MenuProprietaireConnexion(Connection conn){
		this.conn=conn;
	}

	public void printMenu(String pseudo){
		System.out.print("\033c");
		System.out.println("Compte " + pseudo);
		System.out.println("-------------------------------------------------------------");
		System.out.println("0 - Déconnexion");
		System.out.println("1 - Ajouter un logement");
		System.out.println("2 - Liste des logements");
		System.out.println("3 - Supprimer un logement");
		System.out.println("4 - Modification d'un logement");
		System.out.println("5 - Offre promotionnelle");
		System.out.println("6 - Liste des locations");
		System.out.println("-------------------------------------------------------------");

	}

	public void printMenuProprietaireConnexion(String pseudo){

		this.printMenu(pseudo);
		int choix = Utils.readInt();
		Logement lgm = new Logement(conn);
		Offre offre = new Offre(conn);
		ModifierLogement modifLogement = new ModifierLogement(conn);

		try{

			int id_proprio = lgm.getIdProprietaire(pseudo);

			while( choix !=0 ){
				switch(choix){
				case 1:
					Utils.printEntete("AJOUTER UN LOGEMENT");

					System.out.print("Votre logement est un appartement(A) ou une chambre(C)? entrer A ou C: ");
					String typeLogement = Utils.readString("A|C");
					String nbPiece="", numChambre="";
					Boolean booltype=typeLogement.equals("A");
					if(booltype){
						System.out.print("Nombre de pièces: ");
						nbPiece = Utils.readString("[1-9]+[0-9]{0,5}");
					} else {
						System.out.print("Numéro de chambre: ");
						numChambre = Utils.readString("[0-9]{0,5}");
					}

					System.out.print("Adresse (1~20 caracteres): ");
					String adresse = Utils.readString("[0-9a-z ,']{1,20}");

					// vérifie l'unicité avant de continuer
					if( lgm.verifUniqueLogement(adresse, typeLogement, numChambre, nbPiece ) == 0 ){
						System.out.println("Logement déjà existant");
						Thread.sleep(1300);
						this.printMenu(pseudo);
						break;
					}

					System.out.print("Surface: ");
					String surface= Utils.readString("[1-9]+[0-9]{0,5}");

					System.out.print("Ville entre (1~30 caracteres): ");
					String ville = Utils.readString("[A-Za-z ]{1,20}");

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String dateDep="",dateFin="";
					Date date1 =null, date2=null;
					int erreurDate=1;
					System.out.println("Disponibilité (format YYYY-MM-DD) (<=1mois: 27j)");
					do{
						erreurDate=1;
						System.out.print("Date début: ");//format sql annee/mois/jour
						try{
							dateDep = Utils.readString("date");
							date1= sdf.parse(dateDep);

							System.out.print("Date fin: ");
							dateFin = Utils.readString("date");
							date2 = sdf.parse(dateFin);

							long diff = date2.getTime() - date1.getTime();
							long diffDays = diff / (24 * 60 * 60 * 1000);
							if(!date2.after(date1) || (diffDays > 27) ){
								System.out.println("erreur sur la date");
								erreurDate=0;
							}
						} catch (ParseException ex){
							ex.printStackTrace();
						}
					}while(erreurDate==0);


					System.out.print("Prix/nuit: ");
					String prix = Utils.readString("[1-9]+[0-9]{0,5}");

					//IL FAUT VERIFIER QUE l'UTILISATEUR A REPONDU AUX prints FACULTATIF AVEC HASNEXT..() JE PENSE hasNextInt() hasNextLine()
					// Pour passer au champ suivant il faut appuyer sur Entree (gestion NextLine dans readString) + comme c'est facultatif, on met {0,...}
					System.out.print("(facultatif, appuyer sur Entree pour passer) % mois (cas 27 jours ou plus): ");
					String prixMois = Utils.readString("[1-9]{0,1}+[0-9]{0,5}");


					// ajoute le logement
					lgm.ajouterLogement(adresse, surface, ville, dateDep, dateFin, prix, prixMois, pseudo);
					int idLogement = lgm.getIdLogement(adresse, surface, ville);

					// ajoute dans la table appartement ou chambre
					if(booltype)
						lgm.ajouterAppartement(idLogement, nbPiece);
					else
						lgm.ajouterChambre(idLogement, numChambre);


					// Pour ce qui suit, répondre par O ou N
					/*
		    String typeSugg="", nomSugg="";
		    System.out.print("(facultatif) Suggestions (O/N): ");
		    if((Utils.readString("O|N")).equals("O")){
			System.out.print("type (touristique/gastronomique): ");
			typeSugg = Utils.readString("touristique|gastronomique");
			System.out.print("nom: ");
			nomSugg = Utils.readString("[A-Za-z ]{1,20}");
			// ajoute dans la table suggestion et propose_suggestion
			lgm.ajouterLogementSuggestion(typeSugg, nomSugg);
			lgm.tableProposeSuggestion(typeSugg, nomSugg, idLogement);
		    }
					 */

					//----------------------------AJOUT BOUCLE WHILE [BEGIN] 
					String repSugg="N", typeSugg="",nomSugg="";
					int maxSugg=0;
					do{
						System.out.print("(facultatif) Suggestions (O/N): ");
						if(repSugg.equals("O") && maxSugg<5){
							System.out.print("type (touristique/gastronomique): ");
							typeSugg = Utils.readString("touristique|gastronomique");
							System.out.print("nom: ");
							nomSugg = Utils.readString("[A-Za-z ]{1,20}");
							// ajoute dans la table suggestion et propose_suggestion
							lgm.ajouterLogementSuggestion(typeSugg, nomSugg);
							lgm.tableProposeSuggestion(typeSugg, nomSugg, idLogement);
							maxSugg++;//On peut ajouter maximum 5 suggestions
						}
					}while(repSugg.equals("O"));
					//----------------------------AJOUT BOUCLE WHILE [END]



					/*
		    String prest="", prixPrest="";
		    System.out.print("(facultatif) Prestations (O/N): ");
		    if((Utils.readString("O|N")).equals("O")){
			System.out.print("description: ");
			prest = Utils.readString("[A-Za-z ]{1,20}");
			System.out.print("prix: ");
			prixPrest= Utils.readString("[1-9]+[0-9]{0,5}");
			// ajoute dans la table prestation et propose_prestation
			lgm.ajouterLogementPrestation(prest, prixPrest);
			lgm.tableProposePrestation(prest, prixPrest, idLogement);
		    }
					 */

					//----------------------------AJOUT BOUCLE WHILE [BEGIN] 
					String repPrest="N", prixPrest="",prest="";
					int maxPrest=0;
					do{
						System.out.print("(facultatif) Prestations (O/N): ");//boucle infini jusqu'a ce que l'utilisateur dit N
						repPrest=Utils.readString("O|N");
						if(repPrest.equals("O") && maxPrest<5){
							System.out.print("description: ");
							prest = Utils.readString("[A-Za-z ]{1,20}");
							System.out.print("prix: ");
							prixPrest= Utils.readString("[1-9]+[0-9]{0,5}");
							// ajoute dans la table prestation et propose_prestation
							lgm.ajouterLogementPrestation(prest, prixPrest);
							lgm.tableProposePrestation(prest, prixPrest, idLogement);
							maxPrest++;//On peut ajouter maximum 5 prestations
						}
					}while(repPrest.equals("O"));
					//----------------------------AJOUT BOUCLE WHILE [END]

					String photo="", rep="N";
					int maxPhoto=0;
					do{
						System.out.print("(facultatif) Photos (O/N): ");//boucle infini jusqu'a ce que l'utilisateur dit N
						rep=Utils.readString("O|N");
						if(rep.equals("O") && maxPhoto<10){
							System.out.print("nom: ");
							photo = Utils.readString("[A-Za-z ]{1,20}");
							// ajoute photo
							lgm.ajouterLogementPhoto(photo, idLogement);
							maxPhoto++;//On peut ajouter maximum 10 photos
						}
					}while(rep.equals("O"));

					// ajoute dans la table propose_transport 
					// si ville logement = ville service transport 
					lgm.tableProposeTransport(ville, idLogement);

					System.out.println("Logement ajouté");
					Thread.sleep(1300);
					this.printMenu(pseudo);

					break;

				case 2:
					Utils.printEntete("LISTE DES LOGEMENTS");
					lgm.listeLogement(id_proprio);
					do{
						System.out.println("Retour (O)?");
					} while( !Utils.readString("O").equals("O") );
					this.printMenu(pseudo);
					break;

				case 3:
					Utils.printEntete("SUPPRIMER UN LOGEMENT");
					System.out.print("id_logement: ");
					int id = Utils.readInt();
					lgm.supprimerLogement(id, id_proprio);
					Thread.sleep(1300);
					this.printMenu(pseudo);
					break;

				case 4:
					modifLogement.modif(id_proprio);
					this.printMenu(pseudo);
					break;

				case 5:
					Utils.printEntete("GESTION DES OFFRES PROMOTIONNELLES");
					offre.printMenuOffre(id_proprio);
					this.printMenu(pseudo);
					break;

				case 6:
					Utils.printEntete("LISTE DES LOCATIONS");
					if( lgm.verifLocation(id_proprio) == 1 ){
						lgm.afficheListeLocation(id_proprio);
						lgm.supprimerLocation(id_proprio);
						Thread.sleep(1300);
						this.printMenu(pseudo);
					} else {
						System.out.println("Pas de locations");
						Thread.sleep(1300);
						this.printMenu(pseudo);
					}
					break;

				default:
					this.printMenu(pseudo);
					break;
				}
				choix = Utils.readInt();
			} // fin while

		} catch (SQLException | InterruptedException e) {
			System.err.println(e.getMessage());
		}	

	}
}