import java.sql.*;
import java.io.*;

public class ModifierLogement{

    PreparedStatement insert=null;
    PreparedStatement select=null, select2=null;
    PreparedStatement update=null;
    PreparedStatement delete=null;
    ResultSet result = null, result2=null;
    Connection conn = null;

    public ModifierLogement(Connection conn){
	this.conn=conn;
    }



    public void modifierLogement(int id_prop,int id_logement, String prix, String prixMois,
				 String dateDep, String dateFin, String dateDepPromo, String dateFinPromo,
				 String prixPromo, String pieces, String numero, String surface) throws SQLException{
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
    	modifierLogementChambre(id_logement,numero,surface);
    	modifierLogementPrix(id_prop, prix, prixMois, prixB, prixMoisB);
    	//possible que pour les logements libre
    	modifierLogementDispo(id_logement, dateDep, dateFin);
    	//suggestion et prestation
    
    }


    public void modifierLogementPrix(int id_prop, String prix, String prixMois,boolean prixB, boolean prixMoisB) throws SQLException{

	String req ="UPDATE prix_logement SET ";
	if(prixB && prixMoisB) req +="prix=?, "+prix+"prixMois=?"+prixMois; //modif prix et prixMois
	else if(prixB && !prixMoisB) req+="prix=?"+prix; //modif prix uniquement
	else req+="prixMois=?"+prixMois; //modif prixMois uniquement
	
	req+=" WHERE id_prop="+id_prop;
	
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



    /*
      public void modifierOffrepromo(int id_logement, String dateDepPromo, String dateFinPromo, String prixPromo) throws SQLException{       
      String req="UPDATE offre_promotionnelle SET date_debut_offre_promo=?, date_fin_offre_promo=?, prix_offre_promo=? WHERE id_logement="+id_logement;

      update = conn.prepareStatement(req);
      update.setDate(1, java.sql.Date.valueOf(dateDepPromo));
      update.setDate(2, java.sql.Date.valueOf(dateFinPromo));
      update.setInt(3, Integer.parseInt(prixPromo));
      // execute update SQL statement
      update.executeUpdate();


      }
    */  

    public void modifierLogementAppartement(int id_logement, String pieces) throws SQLException{
    	String req="UPDATE appartement SET nb_pieces=? WHERE id_logement="+id_logement;

    	update = conn.prepareStatement(req);
    	update.setInt(1, Integer.parseInt(pieces));
    	// execute update SQL statement
    	update.executeUpdate();
		
    }

    public void modifierLogementChambre(int id_logement, String numero, String surface) throws SQLException{
    	String req="UPDATE chambre SET numero_chambre=? WHERE id_logement="+id_logement;
    	update = conn.prepareStatement(req);
    	update.setInt(1, Integer.parseInt(numero));
    	// execute update SQL statement
    	update.executeUpdate();
		
    }


    public int modifiable(int id_prop) throws SQLException{

	// regarde s'il y a des logements libres, si oui retourne 1 
	select = conn.prepareStatement("SELECT id_logement FROM logement NATURAL JOIN propose_logement WHERE id_proprietaire =" + String.valueOf(id_prop) + " EXCEPT SELECT id_logement FROM logement NATURAL JOIN concerne");

	result = select.executeQuery();
	if(result.next())
	    return 1;

	return 0;
    }


    public void listeLogementModifiable(int id_prop) throws SQLException{

	select = conn.prepareStatement("SELECT id_logement, adresse_logement, surface, ville FROM logement NATURAL JOIN propose_logement WHERE id_proprietaire =" + String.valueOf(id_prop) + " EXCEPT SELECT id_logement, adresse_logement, surface, ville FROM logement NATURAL JOIN concerne");

	result = select.executeQuery();
	Utils.print("id_logement", 10);
	Utils.print("| type", 15);
	Utils.print("| adresse", 15);
	Utils.print("| surface", 9);
	System.out.println("| ville");
	System.out.println("--------------------------------------------------------------------------");

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


    public int choixLogementAModifier(int id_prop) throws SQLException{
	
	System.out.print("Logement à modifier: ");
	int id_logement = Utils.readInt();

	select = conn.prepareStatement("SELECT id_logement FROM logement NATURAL JOIN propose_logement WHERE id_proprietaire ="
			+ String.valueOf(id_prop) + " AND id_logement = "
			+ String.valueOf(id_logement) + " EXCEPT SELECT id_logement FROM logement NATURAL JOIN concerne ");

	result = select.executeQuery();
	if(result.next())
	    return result.getInt(1);
	else
	    return -1;
    }

}