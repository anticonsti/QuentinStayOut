import java.io.*;
import java.sql.*;
import java.util.Scanner;

class ConnectionAirChambre {
    Connection conn; // la connexion a la base
    Statement st;
    PreparedStatement insert;
    PreparedStatement delete;
    PreparedStatement update;
    int id_proprietaire=1;
    
    // connection a la base
    public ConnectionAirChambre(String login, String motPasse) throws SQLException, ClassNotFoundException{
	Class.forName("org.postgresql.Driver");
	conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb",login, motPasse);
    }
    
    // fermeture de la connection
    public void close() throws SQLException{ 
	conn.close();
    }

    public void inscription(int id_proprietaire, String nom, String prenom, String pseudo, String mdp) throws SQLException {
	Scanner sc = new Scanner(System.in);
	insert = conn.prepareStatement("INSERT INTO Proprietaire VALUES(?,?,?,?,?)");

	insert.setInt(1,id_proprietaire);
	insert.setString(2,nom);
	insert.setString(3,prenom);
	insert.setString(4,pseudo);
	insert.setString(5,mdp);
	insert.executeUpdate();
	   
	//si un client supprime son compte, son id_proprietaire ne sera pas repris par une autre personne : de toute facon on n'aura jamais de INT nombre de clients sur le site. 
	System.out.println("Inscription terminee! ");

    }	 
		 
    public void connectionProprio() throws SQLException {
	System.out.println("A implementer!");
    }
		 
    public ResultSet contenuTable() throws SQLException{
	System.out.println("A implementer!");
	return null; // a remplacer par le resultat
    }
	   
} 
