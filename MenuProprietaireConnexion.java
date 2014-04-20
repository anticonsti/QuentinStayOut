import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class MenuProprietaireConnexion{
    Connection conn=null;
    
    public MenuProprietaireConnexion(Connection conn){
	this.conn=conn;
    }

    public void printMenuProprietaireConnexion(){
	System.out.print("\033c");
	System.out.println("Compte");
    	System.out.println("Veuillez entrer votre choix :");
	System.out.println("-------------------------------------------------------------");
	System.out.println("0 - deconnexion");
	System.out.println("1 - ajouter un logement");
	System.out.println("2 - visualiser les logements");
	System.out.println("-------------------------------------------------------------");
	

    }





}