import java.io.*;
import java.sql.*;

public class Test{

    public static void main(String args[]){
	try {
	    Class.forName("org.postgresql.Driver");
	    System.out.println("Driver O.K.");

	    String url = "jdbc:postgresql://localhost:5432/mydb";// ??
	    String user = "postgres";// ??
	    String passwd = "reverse";// ??

	    Connection conn = DriverManager.getConnection(url, user, passwd);
	    System.out.println("Connexion effective !");         
         
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}