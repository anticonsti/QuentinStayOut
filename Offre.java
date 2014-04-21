import java.util.Scanner;
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

    public void printMenu(){
	System.out.print("\033c");
    	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - ajouter une offre ");
	System.out.println("2 - retirer une offre ");
	System.out.println("3 - modifier une offre ");
	System.out.println("-------------------------------------------------------------");

    }
    
    public void printMenuOffre(int id_proprio){
	
	this.printMenu();
	int choix = Utils.readInt();

	try{

	    while( choix !=0 ){

		if( choix == 1){
		    Utils.printEntete("AJOUTER UNE OFFRE");

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

		    this.ajouterOffre(dateDep, dateFin, prix, id_proprio);
		    Thread.sleep(1300);
		    this.printMenu();

		} else {
		    this.printMenu();
		}
		choix = Utils.readInt();
	    } // fin while

	} catch (SQLException | InterruptedException e) {
	    System.err.println(e.getMessage());
	}

    }

    
    public void ajouterOffre(String dateDep, String dateFin, String prix, int id_proprio) throws SQLException{

	select = conn.prepareStatement("SELECT id_logement FROM propose_logement WHERE id_proprietaire=" + String.valueOf(id_proprio) );
	result = select.executeQuery();

	if(result.next()!=false){

	    int id_log =result.getInt(1);
	    insert = conn.prepareStatement("INSERT INTO offre_promotionnelle(id_logement, date_debut_dispo, date_fin_dispo, prix) VALUES(?,?,?,?)");
	    insert.setInt(1, id_log);
	    insert.setDate(2, java.sql.Date.valueOf(dateDep));
	    insert.setDate(3, java.sql.Date.valueOf(dateFin));
	    insert.setInt(4, Integer.parseInt(prix));
	    insert.executeUpdate(); 

	} else {
	    System.out.println("ajouter un logement d'abord");
	}

    }


}