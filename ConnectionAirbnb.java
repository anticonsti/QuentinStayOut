import java.io.*;
import java.sql.*;

class ConnectionAirbnb {
    Connection conn; // la connexion a la base
    Statement st;
    PreparedStatement insert;
    PreparedStatement delete;
    PreparedStatement update;
    
    // connection a la base
    public ConnectionAirbnb(String login, String motPasse) throws SQLException, ClassNotFoundException{
	// -------------------
	// Connexion a la base
	// --------------------
	Class.forName("org.postgresql.Driver");
	conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb",login, motPasse);
    }

    // fermeture de la connection
    public void close() throws SQLException{ 
	conn.close();
    }

    public  void creationTable() throws SQLException {
	System.out.println("A implementer!");
    }	 
                    
    public void suppressionTable() throws SQLException {
	System.out.println("A implementer!");
    }
    
    public void insertionTuplesPredefinis() throws SQLException{
	System.out.println("A implementer!");
    }
    
    public ResultSet contenuTable() throws SQLException{
	System.out.println("A implementer!");
	return null; // a remplacer par le resultat
    }
	   
    public void insertionTuplesUtilisateur
	(int num,String nom,String ville,int etoiles,String directeur) throws SQLException{
	insert.setInt(1,num);
	insert.setString(2,nom);
	insert.setString(3,ville);
	insert.setString(4,directeur);
	insert.setInt(5,etoiles);
	insert.executeUpdate();
    }

} 
