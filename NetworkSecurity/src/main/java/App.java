

import com.netsec.auth.Server;
import com.netsec.mfs.MFSServer;
import com.netsec.fs.accounts.AccountsFileServer; 
import com.netsec.fs.finance.FinanceFileServer;
import com.netsec.fs.payroll.PayrollFileServer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Starting the authentication server" );
        new Server().start();
        System.out.println("Authentication server has been initialized");
        System.out.println("Starting the Enterprize Master File Server (MFS) server");
        new MFSServer().start();
        System.out.println("Enterprize Master File Server (MFS) initialized");
        System.out.println( "Starting the Accounts File Server");
        new AccountsFileServer().start(); 
        System.out.println( "Accounts File Server initialized");
        System.out.println( "Starting the Finance File Server");
        new FinanceFileServer().start(); 
        System.out.println( "Finance File Server initialized");
        System.out.println( "Starting the Payroll File Server");
        new PayrollFileServer().start(); 
        System.out.println( "Payroll File Server initialized");
        System.out.println("-----------------------------------------------------------");
    }
}
