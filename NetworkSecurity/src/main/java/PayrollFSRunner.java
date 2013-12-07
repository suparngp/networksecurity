
import com.netsec.fs.payroll.PayrollFileServer;

/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

/**
 *
 * @author suparngupta
 */
public class PayrollFSRunner {
    public static void main(String[] args){
        System.out.println( "Starting the Payroll File Server");
        new PayrollFileServer().start(); 
        System.out.println( "Payroll File Server initialized");
    }
}
