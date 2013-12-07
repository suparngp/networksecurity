
import com.netsec.fs.finance.FinanceFileServer;

/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

/**
 *
 * @author suparngupta
 */
public class FinanceFSRunner {
    public static void main(String[] args){
        System.out.println( "Starting the Finance File Server");
        new FinanceFileServer().start(); 
        System.out.println( "Finance File Server initialized");
    }
}
