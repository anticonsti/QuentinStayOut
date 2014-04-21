import java.sql.*;
import java.io.*;

public class Location {

    PreparedStatement select=null;
    ResultSet result = null;
    Connection conn = null;

    public Location(Connection conn){
	this.conn=conn;
    }

    public void printLocation(){

	System.out.println("");
	System.out.print("Louer ? (O/N): ");
	String rep= Utils.readString("O|N");

	try{

	    if(rep.equals("O")){
		String id_logement="";
		boolean rsNext=false;
		do{
		    System.out.print("id_logement: ");
		    id_logement = Utils.readString("[0-9]{1,5}");
		    select = conn.prepareStatement("SELECT id_logement FROM propose_logement WHERE id_logement = " + id_logement);
		    result = select.executeQuery();
		    rsNext = result.next();
		    if(rsNext==false)
			System.out.println("non existant");
		}while( rsNext == false);
		
		System.out.print("Vous devez vous inscrire (O/N): ");
		String rep2 = Utils.readString("O|N");
		if(rep2.equals("O")){
		    Utils.printEntete("INSCRIPTION");
		    this.inscriptionLocataire();
		    System.out.println("inscription terminée");
		    Thread.sleep(1300);
		}
	    }

	}catch (SQLException | InterruptedException e) {
	    System.err.println(e.getMessage());
	}
    }

    public void inscriptionLocataire(){

    }

}