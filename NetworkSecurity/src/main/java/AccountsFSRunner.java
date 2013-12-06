
import com.netsec.auth.Server;
import com.netsec.fs.accounts.AccountsFileServer;

/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

/**
 *
 * @author suparngupta
 */
public class AccountsFSRunner {
    public static void main(String[] args){
        System.out.println( "Starting the Accounts File Server");
        new AccountsFileServer().start(); 
        System.out.println( "Accounts File Server initialized");
    }
}
