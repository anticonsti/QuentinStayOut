import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
//indente le 2 mai 2014

public class Offre{

	PreparedStatement insert=null;
	PreparedStatement select=null;
	PreparedStatement delete=null;
	ResultSet result = null;
	Connection conn = null;

	public Offre(Connection conn){
		this.conn= conn;
	}

	public void printRappelCommande(){
		System.out.println("-------------------------------------------------------------");
		System.out.print("0 - retour ");
		System.out.print("| 1 - ajouter ");
		System.out.println("| 2 - liste ");
		System.out.println("-------------------------------------------------------------");
	}

	public void printMenu(){
		System.out.print("\033c");
		System.out.println("OFFRE PROMOTIONNELLE:");
		System.out.println("-------------------------------------------------------------");
		System.out.println("0 - Retour");
		System.out.println("1 - Ajouter une offre ");
		System.out.println("2 - Liste des offres ");
		System.out.println("-------------------------------------------------------------");

	}

	public void printMenuOffre(int id_proprio){

		this.printMenu();
		int choix = Utils.readInt();

		try{

			while( choix !=0 ){

				switch(choix){

				case 1:
					Utils.printEntete("AJOUTER UNE OFFRE");

					select = conn.prepareStatement("SELECT id_logement FROM propose_logement WHERE id_proprietaire=" + String.valueOf(id_proprio) );
					result = select.executeQuery();

					if(result.next()!=false){
						String id_logement="";

						System.out.print("id_logement: ");
						id_logement = Utils.readString("[0-9]{1,5}");

						select = conn.prepareStatement("WITH dispo AS (SELECT DISTINCT id_logement, date_debut_dispo, date_fin_dispo FROM disponibilite NATURAL JOIN prix_logement NATURAL JOIN concerne ), dureelogement AS (SELECT id_logement, SUM(date_fin_dispo - date_debut_dispo ) AS dureelog FROM dispo GROUP BY id_logement ), logementsoccupes AS (SELECT id_logement, date_debut_location, date_fin_location FROM logement NATURAL JOIN prix_logement NATURAL JOIN disponibilite NATURAL JOIN concerne NATURAL JOIN location WHERE( date_debut_location, date_fin_location ) OVERLAPS ( date_debut_dispo, date_fin_dispo ) ), dureeoccupee AS (SELECT id_logement, SUM(date_fin_location-date_debut_location) AS dureeoccup FROM logementsoccupes GROUP BY id_logement), nbreservation AS (SELECT id_logement, COUNT(*) AS nb FROM logementsoccupes GROUP BY id_logement ) SELECT DISTINCT id_logement FROM propose_logement WHERE id_proprietaire =" + String.valueOf(id_proprio)+ " AND id_logement = " + id_logement +" EXCEPT SELECT id_logement FROM dureelogement NATURAL JOIN nbreservation NATURAL JOIN dureeoccupee WHERE dureeoccup + nb -1 = dureelog ");

						result = select.executeQuery();

						if( result.next()==false){
							System.out.println("Erreur");
							Thread.sleep(1300);
							this.printMenu();
							break;
						}

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String dateDep="",dateFin="";
						Date date1 =null, date2=null;
						do{
							System.out.print("Date début offre_promo (format YYYY-MM-DD): ");//format sql annee/mois/jour
							try{
								dateDep = Utils.readString("date");
								date1= sdf.parse(dateDep);

								System.out.print("Date fin offre_promo (format YYYY-MM-DD et date fin > date début): ");
								dateFin = Utils.readString("date");
								date2 = sdf.parse(dateFin);
							} catch (ParseException ex){
								ex.printStackTrace();
							}
						}while(!date2.after(date1));

						System.out.print("Pourcentage de réduction: ");
						String prix = Utils.readString("[1-9]+[0-9]{0,2}");

						this.ajouterOffre(dateDep, dateFin, prix, id_logement);
						System.out.println("Offre ajoutée");
						Thread.sleep(1300);
						this.printMenu();

					} else {
						System.out.println("Ajouter un logement d'abord");
						Thread.sleep(1300);
						this.printMenu();
					}
					break;

				case 2:
					Utils.printEntete("LISTE DES OFFRES");
					this.listeOffre(id_proprio);
					do{
						System.out.println("Retour (O)?");
					} while( !Utils.readString("O").equals("O") );
					this.printMenu();
					break;

				default:
					this.printMenu();
					break;
				}
				choix = Utils.readInt();
			}

		} catch (SQLException | InterruptedException e) {
			System.err.println(e.getMessage());
		}

	}


	public void ajouterOffre(String dateDep, String dateFin, String prix, String id_logement) throws SQLException{

		insert = conn.prepareStatement("INSERT INTO offre_promotionnelle(id_logement, date_debut_offre_promo, date_fin_offre_promo, prix_offre_promo) VALUES(?,?,?,?)");
		insert.setInt(1, Integer.parseInt(id_logement));
		insert.setDate(2, java.sql.Date.valueOf(dateDep));
		insert.setDate(3, java.sql.Date.valueOf(dateFin));
		insert.setInt(4, Integer.parseInt(prix));
		insert.executeUpdate(); 
	}


	public void listeOffre(int id_proprio) throws SQLException{

		select = conn.prepareStatement("SELECT * FROM offre_promotionnelle NATURAL JOIN propose_logement WHERE id_proprietaire=" + String.valueOf(id_proprio) );
		result = select.executeQuery();

		Utils.print("id_promo", 9);
		Utils.print("| id_logement", 15);
		Utils.print("| date_debut", 13);
		Utils.print("| date_fin", 13);
		System.out.println("| prix");
		System.out.println("--------------------------------------------------------------------------");

		if(result.next()!=false){
			Utils.print(String.valueOf(result.getInt(1)),9);
			Utils.print("| "+String.valueOf(result.getInt(2)),15);
			Utils.print("| "+result.getString(3), 13);
			Utils.print("| "+result.getString(4), 13);
			System.out.println("| "+ String.valueOf(result.getInt(1)));
		}

	}

}