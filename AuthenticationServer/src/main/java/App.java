

import com.netsec.auth.Server;
import com.netsec.mfs.MFSServer;
import java.io.File;

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
        System.out.println("Authentication server has been started");
        System.out.println("Starting the MFS server");
        new MFSServer().start();
        System.out.println("MFS server started");
    }
}
