
import com.netsec.auth.Server;
import com.netsec.mfs.MFSServer;

/*
 * The contents of this file cannot be used anywhere without the seeking prior permission from author
 */

/**
 *
 * @author suparngupta
 */
public class MFSRunner {
    public static void main(String[] args){
        System.out.println("Starting the Enterprize Master File Server (MFS)");
        new MFSServer().start();
        System.out.println("Enterprize Master File Server (MFS) initialized");
    }
}
