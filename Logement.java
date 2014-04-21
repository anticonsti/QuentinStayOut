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

	this.ajouterLogementLogement(adr, surface, ville);
	int id_logement=this.getIdLogement(adr, surface, ville);
	// ajoute dans la table propose_logement car non automatique...
	this.tableProposeLogement(id_logement, pseudo);

	this.ajouterLogementDisponibilite(dateDep, dateFin);
	this.tableLogementPrix(id_logement, dateDep, dateFin, prix, prixMois);	
    }
    
    public void supprimerLogement() throws SQLException{
    }
    
    public void modifierLogement() throws SQLException{
    }

    //-----------------------------------------------------------//


    public void ajouterLogementLogement(String adr, int surface, String ville) throws SQLException{

	insert = conn.prepareStatement("INSERT INTO logement(adresse_logement,surface,ville) VALUES(?,?,?)");
	insert.setString(1, adr);
	insert.setInt(2, surface);
	insert.setString(3, ville);
	insert.executeUpdate();  
    }


    public int getIdLogement(String adr, int surface, String ville) throws SQLException{

	int id_lgm=0;
	select = conn.prepareStatement("SELECT DISTINCT id_proprietaie FROM logement WHERE adresse_logement ='"+ adr +"'" + " AND surface =" + String.valueOf(surface) + " AND ville ='"+ ville +"'");
	result = select.executeQuery();
	while (result.next()) {
	    id_lgm=result.getInt(1);
	}
	return id_lgm;
    }

    
    public void ajouterAppartement(int id_logement, String nb) throws SQLException{

	insert = conn.prepareStatement("INSERT INTO appartement VALUES(?,?)");
	insert.setInt(1, id_logement);
	insert.setInt(2, Integer.parseInt(nb));
	insert.executeUpdate();  
    }


    public void ajouterChambre(int id_logement, String num) throws SQLException{

	insert = conn.prepareStatement("INSERT INTO chambre VALUES(?,?)");
	insert.setInt(1, id_logement);
	insert.setInt(2, Integer.parseInt(num));
	insert.executeUpdate();  
    }


    public void tableProposeLogement(int id_logement, String pseudo) throws SQLException{

	int id_proprio=0;
	select = conn.prepareStatement("SELECT DISTINCT id_proprietaie FROM proprietaire WHERE pseudo ='"+ pseudo +"'");
	result = select.executeQuery();
	while (result.next()) {
	    id_proprio=result.getInt(1);
	}

	insert = conn.prepareStatement("INSERT INTO propose_logement VALUES(?,?)");
	insert.setInt(1, id_proprio);
	insert.setInt(2, id_logement);
	insert.executeUpdate();
    }


    public void ajouterLogementDisponibilite(String dateDep, String dateFin) throws SQLException{

	insert = conn.prepareStatement("INSERT INTO disponibilite(date_debut_dispo, date_fin_dispo) VALUES(?,?)");
	insert.setString(1, dateDep);
	insert.setString(2, dateFin);
	insert.executeUpdate();  
    }
 

    public void tableLogementPrix(int id_logement, String dateDep, String dateFin, int prix, String prixMois ) throws SQLException{

	int id_dispo = 0;
	select = conn.prepareStatement("SELECT DISTINCT id_dispo FROM disponibilite WHERE date_debut_dispo = DATE "+ dateDep + " AND date_fin_dispo = DATE " + dateFin);
	result = select.executeQuery();
	while (result.next()) {
	    id_dispo=result.getInt(1);
	}

	String requete = " INSERT INTO prix_logement(id_dispo, id_logement,prix ";
	if(prixMois.equals(""))
	    requete += ")  VALUES(?,?,?)";
	else
	    requete +=", prix_mois) VALUES(?,?,?,?)";

	insert = conn.prepareStatement(requete);
	insert.setInt(1, id_dispo);
	insert.setInt(2, id_logement);
	insert.setInt(3, prix);
	if(!prixMois.equals(""))
	    insert.setInt(4, Integer.parseInt(prixMois));
	insert.executeUpdate();

    }


    public void ajouterLogementSuggestion(String type_sugg, String nom_sugg) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO suggestion(type_suggestion, nom_suggestion) VALUES(?,?)");

	insert.setString(1, type_sugg);
	insert.setString(2, nom_sugg);
	insert.executeUpdate();  
    }

    
    public void tableProposeSuggestion(String type_sugg, String nom_sugg, int id_logement) throws SQLException{
	
	int id_sugg = 0;
	select = conn.prepareStatement("SELECT DISTINCT id_suggestion FROM suggestion WHERE type_suggestion = '"+ type_sugg + "' AND nom_suggestion = '" + nom_sugg +"'");
	result = select.executeQuery();
	while (result.next()) {
	    id_sugg=result.getInt(1);
	}

	insert = conn.prepareStatement("INSERT INTO propose_suggestion VALUES(?,?)");	
	insert.setInt(1, id_logement);
	insert.setInt(2, id_sugg);
	insert.executeUpdate(); 	       
    }


    public void ajouterLogementPrestation(String desc_pr, String prix_pr) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO prestation(description_prestation, prix_prestation) VALUES(?,?)");

	insert.setString(1, desc_pr);
	insert.setInt(2, Integer.parseInt(prix_pr));
	insert.executeUpdate();  
    }


   public void tableProposePrestation(String desc_pr, String prix_pr, int id_logement) throws SQLException{
	
	int id_pr = 0;
	select = conn.prepareStatement("SELECT DISTINCT id_prestation FROM prestation WHERE description_prestation = '"+ desc_pr + "' AND prix_prestation = " + prix_pr );
	result = select.executeQuery();
	while (result.next()) {
	    id_pr=result.getInt(1);
	}

	insert = conn.prepareStatement("INSERT INTO propose_prestation VALUES(?,?)");	
	insert.setInt(1, id_logement);
	insert.setInt(2, id_pr);
	insert.executeUpdate(); 	       
    }


    public void ajouterLogementPhoto(String photo, int idLogement) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO photo(id_logement, nom_photo) VALUES(?,?)");
	insert.setInt(1, idLogement);
	insert.setString(2, photo);
	insert.executeUpdate();  
    }


    public void ajouterLogementTransport(String nb, String prix) throws SQLException{
	insert = conn.prepareStatement("INSERT INTO service_transport(nb_vehicule, prix_transport) VALUES (?,?)");

	insert.setInt(1, Integer.parseInt(nb));
	insert.setInt(2, Integer.parseInt(prix));
	insert.executeUpdate();  
    }


    public void tableProposeTransport(String nb, String prix, int id_logement) throws SQLException{
	
	int id_transport = 0;
	select = conn.prepareStatement("SELECT DISTINCT id_service_transport FROM transport WHERE nb_vehicule = "+ nb + " AND prix_transport = " + prix );
	result = select.executeQuery();
	while (result.next()) {
	    id_transport=result.getInt(1);
	}

	insert = conn.prepareStatement("INSERT INTO propose_transport VALUES(?,?)");	
	insert.setInt(1, id_logement);
	insert.setInt(2, id_transport);
	insert.executeUpdate(); 	       
    }

}