import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class ChercherLogement {
    PreparedStatement insert=null;
    ResultSet rs = null;
    Connection conn = null;

    public ChercherLogement(Connection conn){
	this.conn=conn;
    }

    public void printMenuChercherLogement(){
	System.out.print("\033c");
	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - afficher tous les logements disponibles");
	System.out.println("2 - chercher un logement par critère");

	int c = Utils.readInt();
	System.out.print("\033c");

	switch(c){

	case 0:
	    break;
	    
	case 1 : 
	    try{
		this.afficheLogements();
	    }
	    catch(SQLException e){
		e.printStackTrace();
	    }
	    break;

	case 2 : 
	    try{
	    this.chercherLogements();
	    }
	    catch(SQLException e){
		e.printStackTrace();
	    }
	    break;

	default:
	    System.out.println("ERREUR");
	}

    }

    public  void afficheLogements()throws SQLException{
	insert = conn.prepareStatement("SELECT * FROM Proprietaire");
	rs = insert.executeQuery();
	while (rs.next()) {
	    System.out.print(rs.getInt(1));
	    System.out.print(": ");
	    System.out.print(rs.getString(2));
	    System.out.print(": ");
	    System.out.print(rs.getString(3));
	    System.out.print(": ");
	    System.out.print(rs.getString(4));
	    System.out.print(": ");
	    System.out.println(rs.getString(5));
	}

    }

    public void chercherLogements()throws SQLException {

    }


}