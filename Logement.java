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


    public void ajouterLogement(String adr, String surface, String ville, String dateDep, String dateFin, String prix, String prixMois, String pseudo) throws SQLException{
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
	select = conn.prepareStatement("SELECT * FROM propose_logement WHERE id_proprietaire=" + String.valueOf(id_proprio) + " AND id_logement= " + idl );
	result = select.executeQuery();

	if(result.next()!=false){
	    delete = conn.prepareStatement("DELETE FROM logement WHERE id_logement="+idl);
	    delete.executeUpdate();
	    System.out.println("logement supprimé");
	} else {
	    System.out.println("rien à supprimer");
	}
	
    }
    
    public void modifierLogement(int id_prop,int id_logement, String prix, String prixMois,
    			String dateDep, String dateFin, String dateDepPromo, String dateFinPromo,
    			String prixPromo, String pieces, String numero) throws SQLException{
    	//1. affiche les logements du proprio, affichage de 1,2,3... suivi d'adresse
    	//2. recupere le nombre entre par l'utilisateur 
    	//3. demande ce qu'il veut modifier, print("Disponibilite, prix, offrepromo, nbpiece, numchambre,suggestion,prestation");
    	//=> nbpiece et numchambre peuvent changer via les travaux. Adresse,ville NON
    	//4. apres la modification on affiche toutes les informations liees a ce logement
    	//5. qqch comme : print("0-retour, 1-modifier un autre logement");
    	//ON APPELLE LES "SOUS"FONCTIONS MODIF
    	boolean prixB=false, prixMoisB=false;
    	
    	if(prix!="") prixB=true;
    	if(prixMois!="") prixMoisB=true;
    	
    	modifierLogementAppartement(id_logement,pieces);
    	modifierLogementChambre(id_logement,numero);
    	modifierLogementPrix(id_prop, prix, prixMois, prixB, prixMoisB);
    	//possible que pour les logements libre
    	modifierLogementDispo(id_logement, dateDep, dateFin);
    	modifierOffrepromo(id_logement, dateDepPromo, dateFinPromo, prixPromo);
    	//suggestion et prestation
    
    
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
	System.out.println("| ville");
	System.out.println("--------------------------------------------------------------------------");

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
	    System.out.println("| "+result.getString(4));
	}
    }


    public void modifierLogementPrix(int id_prop, String prix, String prixMois,boolean prixB, boolean prixMoisB) throws SQLException{
	
	String req ="UPDATE prix_logement SET ";
	if(prixB && prixMoisB) req +="prix=?,"+prix+"prixMois=?"+prixMois; //modif prix et prixMois
	else if(prixB && !prixMoisB) req+="prix=?"+prix; //modif prix uniquement
	else req+="prixMois=?"+prixMois; //modif prixMois uniquement
	
	req+=" WHERE id_prop=?"+id_prop;
	
	update = conn.prepareStatement(req);
	update.setString(1, prix);
	update.setString(2, prixMois);
	// execute update SQL statement
	update.executeUpdate();
	
    }
	
	
    public void modifierLogementDispo(int id_logement, String dateDep, String dateFin) throws SQLException{

	String req="UPDATE disponibilite SET date_debut_dispo=?, date_fin_dispo=? WHERE id_logement="+id_logement;

	update = conn.prepareStatement(req);
	update.setDate(1, java.sql.Date.valueOf(dateDep));
	update.setDate(2, java.sql.Date.valueOf(dateFin));
	// execute update SQL statement
	update.executeUpdate();
    }

    public void modifierOffrepromo(int id_logement, String dateDepPromo, String dateFinPromo, String prixPromo) throws SQLException{       
    	String req="UPDATE offre_promotionnelle SET date_debut_offre_promo=?, date_fin_offre_promo=?, prix_offre_promo=? WHERE id_logement="+id_logement;

    	update = conn.prepareStatement(req);
    	update.setDate(1, java.sql.Date.valueOf(dateDepPromo));
    	update.setDate(2, java.sql.Date.valueOf(dateFinPromo));
    	update.setInt(3, Integer.parseInt(prixPromo));
    	// execute update SQL statement
    	update.executeUpdate();


    }
  
	public void modifierLogementAppartement(int id_logement, String pieces) throws SQLException{
    	String req="UPDATE appartement SET nb_pieces=? WHERE id_logement="+id_logement;

    	update = conn.prepareStatement(req);
    	update.setInt(1, Integer.parseInt(pieces));
    	// execute update SQL statement
    	update.executeUpdate();
		
	}
	public void modifierLogementChambre(int id_logement, String numero) throws SQLException{
    	String req="UPDATE chambre SET numero_chambre=? WHERE id_logement="+id_logement;

    	update = conn.prepareStatement(req);
    	update.setInt(1, Integer.parseInt(numero));
    	// execute update SQL statement
    	update.executeUpdate();
		
	}
    

}
