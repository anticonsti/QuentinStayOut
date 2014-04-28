import java.sql.*;
import java.io.*;

class Logement{
    
    PreparedStatement insert=null;
    PreparedStatement select=null, select2=null;
    PreparedStatement update=null;
    PreparedStatement delete=null;
    ResultSet result = null, result2=null;
    Connection conn = null;
    
    public Logement(Connection conn){
	this.conn=conn;
    }


    public void ajouterLogement(String adr, String surface, String ville, String dateDep, String dateFin, String prix, String prixMois,String pseudo) throws SQLException{
	//il faudra appeler toutes les "sous" fonctions pour ajouter un logement.
	//cad ajouterLogementDispo/Logemement/Prix/Suggestion/Prestation/...
	
	this.ajouterLogementLogement(adr, surface, ville);
	int id_logement=this.getIdLogement(adr, surface, ville);
	int id_proprio=this.getIdProprietaire(pseudo);
	// ajoute dans la table propose_logement car non automatique...
	this.tableProposeLogement(id_logement, id_proprio);

	this.ajouterLogementDisponibilite(dateDep, dateFin);
	this.tableLogementPrix(id_logement, dateDep, dateFin, prix, prixMois);	
    }

    
    public void supprimerLogement(int id_logement, int id_proprio) throws SQLException{

	// vérifie qu'il y a des logements
	String idl = String.valueOf(id_logement);
	select = conn.prepareStatement("SELECT id_logement FROM propose_logement WHERE id_proprietaire=" + String.valueOf(id_proprio) + " AND id_logement= " + idl + "EXCEPT SELECT id_logement FROM logement NATURAL JOIN concerne NATURAL JOIN location WHERE date_fin_location > CURRENT_TIMESTAMP");
	result = select.executeQuery();

	if(result.next()!=false){
	    delete = conn.prepareStatement("DELETE FROM logement WHERE id_logement="+idl);
	    delete.executeUpdate();
	    System.out.println("logement supprimé");
	} else {
	    System.out.println("rien à supprimer");
	}
	
    }

    //-----------------------------------------------------------//

    public int verifUniqueLogement(String adr, String typeLogement, String numChambre, String nbPiece) throws SQLException{

	if(typeLogement.equals("C")){
	    select = conn.prepareStatement("SELECT id_logement FROM logement NATURAL JOIN chambre WHERE adresse_logement= '"+adr + "' AND numero_chambre="+ numChambre);
	    result = select.executeQuery();
	    if (result.next())
		return 0;

	} else {
	    select = conn.prepareStatement("SELECT id_logement FROM logement NATURAL JOIN appartement WHERE adresse_logement= '"+adr + "' AND nb_pieces="+ nbPiece);
	    result = select.executeQuery();
	    if (result.next())
		return 0;
	}

	return 1;
    }


    public void ajouterLogementLogement(String adr, String surface, String ville) throws SQLException{

    	insert = conn.prepareStatement("INSERT INTO logement(adresse_logement,surface,ville) VALUES(?,?,?)");
    	insert.setString(1, adr);
    	insert.setInt(2, Integer.parseInt(surface));
    	insert.setString(3, ville);
    	insert.executeUpdate();  
    }


    public int getIdLogement(String adr, String surface, String ville) throws SQLException{

	int id_lgm=0;
	select = conn.prepareStatement("SELECT DISTINCT id_logement FROM logement WHERE adresse_logement ='"+ adr +"'" + " AND surface =" + surface + " AND ville ='"+ ville +"'");
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


    public int getIdProprietaire(String pseudo) throws SQLException{

	int id_proprio=0;
	select = conn.prepareStatement("SELECT DISTINCT id_proprietaire FROM proprietaire WHERE pseudo ='"+ pseudo +"'");
	result = select.executeQuery();
	while (result.next()) {
	    id_proprio=result.getInt(1);
	}
	return id_proprio;
    }


    public void tableProposeLogement(int id_logement, int id_proprio) throws SQLException{

	insert = conn.prepareStatement("INSERT INTO propose_logement VALUES(?,?)");
	insert.setInt(1, id_proprio);
	insert.setInt(2, id_logement);
	insert.executeUpdate();
    }


    public void ajouterLogementDisponibilite(String dateDep, String dateFin) throws SQLException{

	insert = conn.prepareStatement("INSERT INTO disponibilite(date_debut_dispo, date_fin_dispo) VALUES(?,?)");
	insert.setDate(1, java.sql.Date.valueOf(dateDep));
	insert.setDate(2, java.sql.Date.valueOf(dateFin));
	insert.executeUpdate();  
    }
 

    public void tableLogementPrix(int id_logement, String dateDep, String dateFin, String prix, String prixMois ) throws SQLException{

	int id_dispo = 0;
	select = conn.prepareStatement("SELECT DISTINCT id_dispo FROM disponibilite WHERE CAST(date_debut_dispo AS DATE)= '"+ dateDep + "' AND CAST(date_fin_dispo AS DATE) = '" + dateFin+"'");
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
	insert.setInt(3, Integer.parseInt(prix));
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



    public void tableProposeTransport(String ville, int id_logement) throws SQLException{
	
	int id_transport = 0;
	select = conn.prepareStatement("SELECT id_service_transport FROM service_transport WHERE ville_service_transport = '" + ville + "'" );
	result = select.executeQuery();
	if(result.next()) {
	    id_transport=result.getInt(1);

	    insert = conn.prepareStatement("INSERT INTO propose_transport VALUES(?,?)");	
	    insert.setInt(1, id_logement);
	    insert.setInt(2, id_transport);
	    insert.executeUpdate(); 
	}	       
    }


    public void listeLogement(int id_prop) throws SQLException{
	
	select = conn.prepareStatement("SELECT * FROM logement NATURAL JOIN propose_logement WHERE id_proprietaire=" + String.valueOf(id_prop) );
	Utils.print("id_logement", 10);
	Utils.print("| type", 15);
	Utils.print("| adresse", 15);
	Utils.print("| surface", 9);
	Utils.print("| ville", 15);
	Utils.print("| disponibilité", 23);	    
	System.out.println("| prix");	
	System.out.println("------------------------------------------------------------------------------------------------------");

	result = select.executeQuery();
	while (result.next()) {
	    String id_logement = result.getString(1);
	    Utils.print(id_logement,10);

	    select2=conn.prepareStatement("SELECT numero_chambre FROM chambre WHERE id_logement = " + id_logement);
	    result2 = select2.executeQuery();
	    if( result2.next() ){
		Utils.print("| chambre n°" + result2.getString(1), 15);
	    } else {
		select2=conn.prepareStatement("SELECT nb_pieces FROM appartement WHERE id_logement = " + id_logement);
		result2 = select2.executeQuery();
		if( result2.next() )
		    Utils.print("| apt. " + result2.getString(1) + " pièces", 15);
	    }

	    Utils.print("| "+result.getString(2), 15);
	    Utils.print("| "+result.getString(3), 9 );
	    Utils.print("| "+result.getString(4), 15);

	    select2 = conn.prepareStatement("SELECT date_debut_dispo, date_fin_dispo, sejour_min, prix, prix_mois FROM disponibilite NATURAL JOIN prix_logement WHERE id_logement = " + id_logement);
	    result2 = select2.executeQuery();
	    if(result2.next()) {
		Utils.print("| "+ result2.getString(1) + "-" + result2.getString(2), 23);
		System.out.println("| "+result2.getString(4));
		System.out.println("séjour min: " + String.valueOf(result2.getInt(3)) + ", %mois: "+  String.valueOf(result2.getInt(5)));


		select2 = conn.prepareStatement("SELECT type_suggestion, nom_suggestion FROM suggestion NATURAL JOIN propose_suggestion WHERE id_logement = " + id_logement);
		result2 = select2.executeQuery();
		if(result2.next()) {
		    System.out.println("suggestion: " +result2.getString(1) + ", " + result2.getString(2));
		}

		select2 = conn.prepareStatement("SELECT description_prestation, prix_prestation FROM prestation NATURAL JOIN propose_prestation WHERE id_logement = " + id_logement);
		result2 = select2.executeQuery();
		if(result2.next()) {
		    System.out.println("prestation: " +result2.getString(1) +", prix: "+String.valueOf(result2.getInt(2))  );
		}

		select2 = conn.prepareStatement("SELECT prix_transport FROM service_transport NATURAL JOIN propose_transport WHERE id_logement = " + id_logement);
		result2 = select2.executeQuery();
		if(result2.next()) {
		    System.out.println("service_transport: oui, prix: " + String.valueOf(result2.getInt(1)) );
		}

		System.out.println("");
		System.out.println("------------------------------------------------------------------------------------------------------");

	    }
	}
    }


    public int verifLocation(int id_prop) throws SQLException{

	select = conn.prepareStatement("SELECT id_logement FROM propose_logement NATURAL JOIN concerne WHERE id_proprietaire= " + String.valueOf(id_prop) );
	result = select.executeQuery();
	if ( result.next() )
	    return 1;

	return 0;
    }

    public void afficheListeLocation(int id_prop) throws SQLException{

	select = conn.prepareStatement("SELECT id_logement, nom_locataire, prenom_locataire, adresse_locataire, num_tel, email, date_reservation_location, date_debut_location, date_fin_location, montant_total, prix, prix_mois, id_location FROM prix_logement NATURAL JOIN propose_logement NATURAL JOIN concerne NATURAL JOIN location NATURAL JOIN loge NATURAL JOIN locataire WHERE id_proprietaire= " + String.valueOf(id_prop) );
	result = select.executeQuery();

	while( result.next() ){
	    System.out.println("");
	    String id_location =  result.getString(13);

	    System.out.println("id_location : "+ id_location);
	    System.out.println("Logement: "+ result.getString(1));
	    System.out.println("Locataire: "+ result.getString(2) + " " + result.getString(3));
	    System.out.println("Adresse: "+ result.getString(4));
	    System.out.println("Tél: "+ result.getString(5) +", email: " +  result.getString(6));
	    System.out.println("Date de réservation: "+  result.getString(7));
	    System.out.println("Période: "+ result.getString(8) + " -- " +  result.getString(9));
	    System.out.println("Montant total: "+ result.getString(10) + "euros");
	    System.out.println("dont: "+ result.getString(11) + "euros (logement)  (%mois: " + result.getString(12) +")");

	    select2 = conn.prepareStatement("SELECT prix_prestation, description_prestation FROM prestation NATURAL JOIN avec_prestation WHERE id_location = " + id_location);
	    result2 = select2.executeQuery();
	    if( result2.next() )
		System.out.println("    + "+ result2.getString(1) + "euros (" + result2.getString(2) + ")" );


	    select2 = conn.prepareStatement("SELECT prix_transport, date_reservation FROM service_transport NATURAL JOIN avec_transport WHERE id_location = " + id_location);
	    result2 = select2.executeQuery();
	    while( result2.next() )
		System.out.println("    + "+ result2.getString(1) + "euros (" + result2.getString(2) + ")" );

	    System.out.println("");
	    System.out.println("--------------------------------------------------------------------------");
	    
	}

	
    }
    
    public void supprimerLocation(int id_prop) throws SQLException{
	System.out.println("Client souhaitant se rétracter (O/N)");
	if( Utils.readString("O|N").equals("O")){
	    System.out.print("id_location: ");
	    int id_location = Utils.readInt();

	    select = conn.prepareStatement("SELECT id_location FROM concerne NATURAL JOIN logement NATURAL JOIN propose_logement WHERE id_proprietaire= " + String.valueOf(id_prop) + "AND id_location = " + id_location );
	    result = select.executeQuery();
	    if( result.next() ){

		select = conn.prepareStatement("SELECT id_location FROM location WHERE date_reservation_location >= CURRENT_DATE-7 AND date_reservation_location <= CURRENT_DATE AND id_location=" + id_location);
		result = select.executeQuery();
		if( result.next() ){
		    delete = conn.prepareStatement("DELETE FROM location WHERE id_location="+id_location);
		    delete.executeUpdate();
		    System.out.println("Supprimé");
		} else
		    System.out.println("7 jours pour se rétracter");
	    }else 
		System.out.println("Erreur");
	    
	}
    }

}
