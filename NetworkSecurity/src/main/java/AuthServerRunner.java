
import com.netsec.auth.Server;

/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

/**
 *
 * @author suparngupta
 */
public class AuthServerRunner {
    public static void main(String[] args){
        System.out.println( "Starting the authentication server" );
        new Server().start();
        System.out.println("Authentication server has been initialized");
    }
}
