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

    public void printMenu(){
	System.out.print("\033c");
	System.out.println("MODIFICATION D'UN LOGEMENT");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - Retour");
	System.out.println("1 - Appartement");
	System.out.println("2 - Chambre");
	System.out.println("-------------------------------------------------------------");

    }

    public void modif(int id_prop) {

	try {
	    if( this.modifiable(id_prop)== 1){

		this.printMenu();
		int choix = Utils.readInt();


		while( choix !=0 ){
		    switch(choix){

		    case 1:
			if(this.verifAppartementModifiable(id_prop)==1){
			    this.listeLogementModifiable(id_prop, 1);
			    System.out.print("Appartement à modifier: ");
			    int id_logement = Utils.readInt();
			    int id_modif = this.choixLogementAModifier(id_prop, 1, id_logement);

			    if( id_modif != -1){
				System.out.print("Nombre de pièces: ");
				int nb =  Utils.readInt();
				this.modifierLogementAppartement(id_logement, nb);

				System.out.println("Disponibilité: ");
				System.out.print("Début: ");
				String dateDep =  Utils.readString("date");
				System.out.print("Fin: ");
				String dateFin =  Utils.readString("date");
				this.modifierLogementDispo(id_logement, dateDep, dateFin);

				System.out.print("Prix/jour: ");
				String prix = Utils.readString("[1-9]+[0-9]{0,5}");
				this.modifierLogementPrix(id_logement, prix);

				System.out.print("% mois: ");
				String prixMois = Utils.readString("[1-9]+[0-9]{0,5}|");
				if( !prixMois.equals(""))
				    this.modifierLogementPrixMois(id_logement, prixMois);

				System.out.println("Modification effectuée");
				Thread.sleep(1300);
				this.printMenu();
			    } else {
				System.out.println("Erreur");
				Thread.sleep(1300);
				this.printMenu();
			    }
			} else {
			    System.out.println("Rien à modifier");
			    Thread.sleep(1300);
			    this.printMenu();
			}
			break;

		    case 2:
			if(this.verifChambreModifiable(id_prop)==1){
			    this.listeLogementModifiable(id_prop, 2);
			    System.out.print("Chambre à modifier: ");
			    int id_logement = Utils.readInt();
			    int id_modif = this.choixLogementAModifier(id_prop, 2, id_logement);

			    if( id_modif != -1){
				System.out.print("Surface: ");
				int surface =  Utils.readInt();
				System.out.print("Numéro: ");
				int num =  Utils.readInt();
				this.modifierLogementChambre(id_logement, num, surface);

				System.out.println("Disponibilité: ");
				System.out.print("Début: ");
				String dateDep =  Utils.readString("date");
				System.out.print("Fin: ");
				String dateFin =  Utils.readString("date");
				this.modifierLogementDispo(id_logement, dateDep, dateFin);

				System.out.print("Prix/jour: ");
				String prix = Utils.readString("[1-9]+[0-9]{0,5}");
				this.modifierLogementPrix(id_logement, prix);

				System.out.print("% mois: ");
				String prixMois = Utils.readString("[1-9]+[0-9]{0,5}|");
				if( !prixMois.equals(""))
				    this.modifierLogementPrixMois(id_logement, prixMois);

				System.out.println("Modification effectuée");
				Thread.sleep(1300);
				this.printMenu();
			    } else {
				System.out.println("Erreur");
				Thread.sleep(1300);
				this.printMenu();
			    }
			}  else {
			    System.out.println("Rien à modifier");
			    Thread.sleep(1300);
			    this.printMenu();
			}
			break;

		    default:
			this.printMenu();
			break;
		    }
		    choix = Utils.readInt();
		}
	    }
	}catch (SQLException | InterruptedException e) {
	    System.err.println(e.getMessage());
	}
		
    }

    /*
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
    */

    /*
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
    */

   public void modifierLogementPrix(int id_logement, String prix) throws SQLException{

       String req ="UPDATE prix_logement SET prix=? WHERE id_logement="+String.valueOf(id_logement);
	
       update = conn.prepareStatement(req);
       update.setInt(1, Integer.parseInt(prix));
       // execute update SQL statement
       update.executeUpdate();
    }

    public void modifierLogementPrixMois(int id_logement, String prixMois) throws SQLException{

	String req ="UPDATE prix_logement SET prix_mois=? WHERE id_logement="+String.valueOf(id_logement);
	
       update = conn.prepareStatement(req);
       update.setInt(1, Integer.parseInt(prixMois));
       // execute update SQL statement
       update.executeUpdate();
    }
	
    public void modifierLogementDispo(int id_logement, String dateDep, String dateFin) throws SQLException{

	String id_dispo="";
	select = conn.prepareStatement("SELECT id_dispo FROM prix_logement WHERE id_logement="+id_logement);
	result = select.executeQuery();
	if( result.next() )
	    id_dispo= result.getString(1);

	String req="UPDATE disponibilite SET date_debut_dispo=?, date_fin_dispo=? WHERE id_dispo="+id_dispo;

	update = conn.prepareStatement(req);
	update.setDate(1, java.sql.Date.valueOf(dateDep));
	update.setDate(2, java.sql.Date.valueOf(dateFin));
	// execute update SQL statement
	update.executeUpdate();
    }

    public void modifierLogementAppartement(int id_logement, int pieces) throws SQLException{
    	String req="UPDATE appartement SET nb_pieces=? WHERE id_logement="+id_logement;

    	update = conn.prepareStatement(req);
    	update.setInt(1, pieces);
    	// execute update SQL statement
    	update.executeUpdate();
		
    }

    public void modifierLogementChambre(int id_logement, int numero, int surface) throws SQLException{
    	//MODIFIE le numero de la chambre
    	String req="UPDATE chambre SET numero_chambre=? WHERE id_logement="+id_logement;
    	update = conn.prepareStatement(req);
    	update.setInt(1, numero);
    	// execute update SQL statement
    	update.executeUpdate();
		
    	//MODIFIE la surface de la chambre = surface du logement
    	req="UPDATE logement SET surface=? WHERE id_logement="+id_logement;
    	update = conn.prepareStatement(req);
    	update.setInt(1, surface);
    	// execute update SQL statement
    	update.executeUpdate();
    }


    public int modifiable(int id_prop) throws SQLException{

	// regarde s'il y a des logements libres, si oui retourne 1 
	select = conn.prepareStatement("SELECT id_logement FROM logement NATURAL JOIN propose_logement WHERE id_proprietaire =" + String.valueOf(id_prop) + " EXCEPT SELECT id_logement FROM logement NATURAL JOIN concerne NATURAL JOIN location WHERE date_fin_location > CURRENT_TIMESTAMP");

	result = select.executeQuery();
	if(result.next())
	    return 1;

	return 0;
    }


    public int verifAppartementModifiable(int id_prop) throws SQLException{

	// regarde s'il y a des logements libres, si oui retourne 1 
	select = conn.prepareStatement("SELECT id_logement FROM logement NATURAL JOIN appartement NATURAL JOIN propose_logement WHERE id_proprietaire =" + String.valueOf(id_prop) + " EXCEPT SELECT id_logement FROM logement NATURAL JOIN concerne NATURAL JOIN location WHERE date_fin_location > CURRENT_TIMESTAMP");

	result = select.executeQuery();
	if(result.next())
	    return 1;

	return 0;
    }


    public int verifChambreModifiable(int id_prop) throws SQLException{

	// regarde s'il y a des logements libres, si oui retourne 1 
	select = conn.prepareStatement("SELECT id_logement FROM logement NATURAL JOIN chambre NATURAL JOIN propose_logement WHERE id_proprietaire =" + String.valueOf(id_prop) + " EXCEPT SELECT id_logement FROM logement NATURAL JOIN concerne NATURAL JOIN location WHERE date_fin_location > CURRENT_TIMESTAMP");

	result = select.executeQuery();
	if(result.next())
	    return 1;

	return 0;
    }
    

    public void listeLogementModifiable(int id_prop, int choix) throws SQLException{

	// choix==1  type_logement=appartement , sinon type_logement=chambre
	    
	select = conn.prepareStatement("SELECT id_logement, adresse_logement, surface, ville FROM logement NATURAL JOIN propose_logement WHERE id_proprietaire =" + String.valueOf(id_prop) + " EXCEPT SELECT id_logement, adresse_logement, surface, ville FROM logement NATURAL JOIN concerne NATURAL JOIN location WHERE date_fin_location > CURRENT_TIMESTAMP");

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

	    if(choix == 1){
		select2=conn.prepareStatement("SELECT nb_pieces FROM appartement WHERE id_logement = " + id_logement);
		result2 = select2.executeQuery();
		if( result2.next() )
		    Utils.print("| apt. " + result2.getString(1) + " pièces", 15);
	    } else {
		select2=conn.prepareStatement("SELECT numero_chambre FROM chambre WHERE id_logement = " + id_logement);
		result2 = select2.executeQuery();
		if( result2.next() )
		    Utils.print("| chambre n°" + result2.getString(1), 15);
	    }

	    Utils.print("| "+result.getString(2), 15);
	    Utils.print("| "+result.getString(3), 9 );
	    System.out.println("| "+result.getString(4));
	}
    }


    public int choixLogementAModifier(int id_prop, int choix, int id_logement) throws SQLException{

	select = conn.prepareStatement("SELECT id_logement FROM logement NATURAL JOIN propose_logement WHERE id_proprietaire ="
			+ String.valueOf(id_prop) + " AND id_logement = "
			+ String.valueOf(id_logement) + " EXCEPT SELECT id_logement FROM logement NATURAL JOIN concerne NATURAL JOIN location WHERE date_fin_location > CURRENT_TIMESTAMP");

	result = select.executeQuery();
	if(result.next())
	    return result.getInt(1);
	else
	    return -1;
    }

}