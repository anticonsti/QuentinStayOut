import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

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
	System.out.print("| 2 - retirer ");
	System.out.print("| 3 - modifier ");
	System.out.println("| 4 - liste ");
	System.out.println("-------------------------------------------------------------");
    }

    public void printMenu(){
	System.out.print("\033c");
    	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - ajouter une offre ");
	System.out.println("2 - retirer une offre ");
	System.out.println("3 - modifier une offre ");
	System.out.println("4 - liste des offres ");
	System.out.println("-------------------------------------------------------------");

    }
    
    public void printMenuOffre(int id_proprio){
	
	this.printMenu();
	int choix = Utils.readInt();

	try{

	    while( choix !=0 ){

		if( choix == 1){
		    Utils.printEntete("AJOUTER UNE OFFRE");

		    select = conn.prepareStatement("SELECT id_logement FROM propose_logement WHERE id_proprietaire=" + String.valueOf(id_proprio) );
		    result = select.executeQuery();

		    if(result.next()!=false){
			String id_logement="";
			boolean rsNext=false;
			do{
			    System.out.print("id_logement: ");
			    id_logement = Utils.readString("[0-9]{1,5}");
			    select = conn.prepareStatement("SELECT id_logement FROM propose_logement WHERE id_proprietaire=" + String.valueOf(id_proprio) + " AND id_logement = " + id_logement );
			    result = select.executeQuery();
			    rsNext = result.next();
			}while( rsNext == false);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateDep="",dateFin="";
			Date date1 =null, date2=null;
			do{
			    System.out.print("Date début offre_promo (format YYYY-MM-DD): ");//format sql annee/mois/jour
			    try{
				dateDep = Utils.readString("date");
				date1= sdf.parse(dateDep);

				System.out.print("Date fin offre_promp (format YYYY-MM-DD et date fin > date début): ");
				dateFin = Utils.readString("date");
				date2 = sdf.parse(dateFin);
			    } catch (ParseException ex){
				ex.printStackTrace();
			    }
			}while(!date2.after(date1));

			System.out.print("Prix: ");
			String prix = Utils.readString("[1-9]+[0-9]{0,5}");

			this.ajouterOffre(dateDep, dateFin, prix, id_logement);
			System.out.println("offre ajoutée");
			Thread.sleep(1300);
			this.printMenu();

		    } else {
			System.out.println("ajouter un logement d'abord");
			Thread.sleep(1300);
			this.printMenu();
		    }

		} else if( choix == 4){
		    Utils.printEntete("LISTE DES OFFRES");
		    this.listeOffre(id_proprio);
		    this.printRappelCommande();

		}else {
		    this.printMenu();
		}
		choix = Utils.readInt();
	    } // fin while

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
	Utils.print("| date_debut", 15);
	Utils.print("| date_fin", 9);
	System.out.println("| prix");
	System.out.println("--------------------------------------------------------------------------");

	if(result.next()!=false){
	    Utils.print(String.valueOf(result.getInt(1)),9);
	    Utils.print("| "+String.valueOf(result.getInt(2)),15);
	    Utils.print("| "+result.getString(3), 15);
	    Utils.print("| "+result.getString(4), 9 );
	    System.out.println("| "+ String.valueOf(result.getInt(1)));
	}

    }

}