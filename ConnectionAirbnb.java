import java.io.*;
import java.sql.*;
import java.util.Scanner;

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

    public void inscription() throws SQLException {
	Scanner sc = new Scanner(System.in);
	
	System.out.println("Nom entre 1~20caracteres.");
	String nom = sc.nextLine();
	if(!nom.matches("[a-z]{1,20}")) inscription();
	   
	System.out.println("Prenom entre 1~30caracteres");
	String prenom = sc.nextLine();
	if(!nom.matches("[a-z]{1,30}")) inscription();

	System.out.println("Pseudo entre 1~30caracteres");
	String pseudo = sc.nextLine();
	if(!nom.matches("[a-z]{1,30}")) inscription();
	   
	System.out.println("Mot de entre 1~30caracteres");
	String mdp = sc.nextLine();
	if(!nom.matches("[a-z]{1,30}")) inscription();

	insert.setString(1,nom);
	insert.setString(2,prenom);
	insert.setString(3,pseudo);
	insert.setString(4,mdp);
	insert.executeUpdate();
	   
	System.out.println("Inscription terminee! "); 

    }	 
		 
    public void connectionProprio() throws SQLException {
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
