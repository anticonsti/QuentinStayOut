import java.util.Scanner;
import java.sql.*;
import java.io.*;

class Logement{
    
    PreparedStatement insert=null;
    PreparedStatement select=null;
    ResultSet result = null;
    Connection conn = null;
    
    public Logement(Connection conn){
	this.conn=conn;
    }


    /*
    public void printMenuLogement(){

	System.out.println("Pour pouvoir publier une annonce, il faut que vous soyez connecte.");
    	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - blablablah");
	System.out.println("2 - blblblblah");
	System.out.println("-------------------------------------------------------------");
	int choix = Utils.readInt();
	
	//try{
	    switch(choix){

	    case 0 :
		System.out.print("\033c"); //nettoyage de l'ecran
		System.out.println("Veuillez entrer votre choix :");
		System.out.println("-------------------------------------------------------------");
		System.out.println("0 - fin");
		System.out.println("1 - heeeeeeeeeeeeein");
		System.out.println("2 - QUEEEEEEEEEEEEEE");
		System.out.println("-------------------------------------------------------------");
		break;

	    case 1 : 
		break;

	    case 2 :
		break;

	    default :
		//on recommence le switch puisque l'utilisateur a entre un nombre bizarre.
		printMenuLogement(); 
		break;
	    }
	    //}catch (SQLException e) {
	    //System.err.println(e.getMessage());
	    //}

    }    
    */

    public void ajouterLogement(String adr, int surface, String ville, String dateDep, String dateFin, int prix, String prixMois, String pseudo) throws SQLException{
	//il faudra appeler toutes les "sous" fonctions pour ajouter un logement.
	//cad ajouterLogementDispo/Logemement/Prix/Suggestion/Prestation/...

	this.tableLogement(adr, surface, ville);
	int id_logement=this.idLogement(adr, surface, ville);
	// ajoute dans la table propose_logement car non automatique...
	this.tableProposeLogement(id_logement, pseudo);

	this.tableDisponibilite(dateDep, dateFin);
	this.tablePrixLogement(id_logement, dateDep, dateFin, prix, prixMois);
    }
    
    public void supprimerLogement() throws SQLException{
    }
    
    public void modifierLogement() throws SQLException{
    }
    //-----------------------------------------------------------//

    public void tableLogement(String adr, int surface, String ville) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO logement(adresse_logement,surface,ville) VALUES(?,?,?)");

	insert.setString(1,adr);
	insert.setInt(2,surface);
	insert.setString(3,ville);
	insert.executeUpdate();  
    }

    public int idLogement(String adr, int surface, String ville)throws SQLException{
	int id_lgm=0;
	select = conn.prepareStatement("SELECT DISTINCT id_proprietaie FROM logement WHERE adresse_logement ='"+ adr +"'" + " AND surface =" + String.valueOf(surface) + " AND ville ='"+ ville +"'");
	result = select.executeQuery();
	while (result.next()) {
	    id_lgm=result.getInt(1);
	}
	return id_lgm;
    }

    public void tableProposeLogement(int id_logement, String pseudo) throws SQLException{

	int id_proprio=0;
	select = conn.prepareStatement("SELECT DISTINCT id_proprietaie FROM proprietaire WHERE pseudo ='"+ pseudo +"'");
	result = select.executeQuery();
	while (result.next()) {
	    id_proprio=result.getInt(1);
	}

	insert = conn.prepareStatement("INSERT INTO propose_logement VALUES(?,?)");
	insert.setInt(1,id_proprio);
	insert.setInt(2,id_logement);
	insert.executeUpdate();
    }


    public void tableDisponibilite(String dateDep, String dateFin) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO disponibilite(date_debut_dispo, date_fin_dispo) VALUES(?,?)");

	insert.setString(1,dateDep);
	insert.setString(2,dateFin);
	insert.executeUpdate();  
    }

    
    public void tablePrixLogement(int id_logement, String dateDep, String dateFin, int prix, String prixMois ){


    }


    public void ajouterLogementPrix(int prix) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO prix_logement(prix) VALUES(?)");

	insert.setInt(1,prix);
	insert.executeUpdate();  
    }
    
    public void ajouterLogementSuggestion(String type_sugg, String nom_sugg) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO suggestion(type_suggestion, nom_suggestion) VALUES(?,?)");

	insert.setString(1,type_sugg);
	insert.setString(2,nom_sugg);
	insert.executeUpdate();  
    }

    public void ajouterLogementPrestation(String desc_pr, String prix_pr) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO prestation(description_prestation, prix_prestation) VALUES(?,?)");

	insert.setString(1,desc_pr);
	insert.setString(2,prix_pr);
	insert.executeUpdate();  
    }

    public void ajouterLogementPhoto(String photo) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO photo(nom_photo) VALUES(?)");

	insert.setString(1,photo);
	insert.executeUpdate();  
    }


    public void ajouterLogementTransport(int nb, int prix) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO service_transport(nb_vehicule, prix_transport) VALUES (?,?)");

	insert.setInt(1,nb);
	insert.setInt(2,prix);
	insert.executeUpdate();  
    }


}