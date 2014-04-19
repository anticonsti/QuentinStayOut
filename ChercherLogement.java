import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class ChercherLogement {
    PreparedStatement insert=null;
    ResultSet rs = null;
    public ChercherLogement(){

    }
    public void printMenuChercherLogement(Connection conn){
	System.out.print("\033c");
	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - retour");
	System.out.println("1 - afficher tous les logements disponibles");
	System.out.println("2 - chercher un logement par critère");

	int c = Utils.readInt();
	System.out.print("\033c");

	switch(c){
	case 1 : 
	    try{
		this.afficheLogements(conn);
	    }
	    catch(SQLException e){
		e.printStackTrace();
	    }
	    break;
	case 2 : 
	    try{
	    this.chercherLogements(conn);
	    }
	    catch(SQLException e){
		e.printStackTrace();
	    }
	    break;
	default:
	    System.out.println("ERREUR");
	}

    }

    public  void afficheLogements(Connection conn)throws SQLException{
	insert = conn.prepareStatement("SELECT * INTO Proprietaire");
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
	System.out.println("HELLO");

    }

    public void chercherLogements(Connection conn)throws SQLException {
	insert = conn.prepareStatement("INSERT INTO Proprietaire VALUES(?,?,?,?,?)");
    }


}