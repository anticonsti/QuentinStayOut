import java.io.*;
import java.sql.*;
//import java.util.Scanner;

class ConnectionAirChambre {
    Connection conn;
  
    // connection a la base
    public ConnectionAirChambre(String login, String motPasse) throws SQLException, ClassNotFoundException{
	Class.forName("org.postgresql.Driver");
	conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb",login, motPasse);
	}

    // fermeture de la connection
    public void close() throws SQLException{ 
	conn.close();
    }

    public Connection getConnection(){
	return conn;
    }
	   
} 
