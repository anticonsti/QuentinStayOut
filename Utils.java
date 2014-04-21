import java.util.Scanner;

public class Utils{

    static public String readString(String motif){
	int erreur=0;
	if(motif.equals("date"))
	    motif = "((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";
	do{
	    Scanner in = new Scanner(System.in);
	    String s = in.nextLine();
	    if(!s.matches(motif)){
		System.out.println("Syntaxe incorrecte, réessayer");
		erreur=-1;
	    }
	    else
		return s;
	}while(erreur==-1);
	return null;

	/*
	Scanner in = new Scanner(System.in);
	try{
	    return in.next();
	}
	catch(Exception e){
	    e.printStackTrace();
	    return null;
	    }*/

    }
    
    static public int readInt(){
	int erreur=0;
	Scanner in;
	do{
	    in = new Scanner(System.in);
	    try{
		return in.nextInt();   //lecture du choix utilisateur    
	    }
	    catch(Exception e){
		in.nextLine();
		System.out.println("Veuillez entrer un nombre");
		//e.printStackTrace();
		erreur= -1;
	    }
	}while(erreur==-1);
	return -1;
    }


    /**
     *@param prompt The prompt to display to the user
     *@return The password as entered by the user
     */
    public static String readPassword (String prompt) {
	String password = "";
	try {
	    password = new String(System.console().readPassword("%s", prompt));
	}
	catch (Exception ioe) {
	    ioe.printStackTrace();
	}
	return password;
    }


    public static void print(String s, int i) {
	System.out.print(s);
	for (i -= s.length(); i >= 0; i --)
	    System.out.print(" ");
    }


    public static void printEntete(String nom){
	System.out.print("\033c");
	System.out.println("-------------------------------------------------------------");
	System.out.println(nom);
	System.out.println("-------------------------------------------------------------");
    }

}