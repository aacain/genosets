/*
 */
package edu.uncc.genosets.util.interpro;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.rpc.ServiceException;
import org.apache.commons.cli.Options;
import uk.ac.ebi.webservices.axis1.stubs.iprscan.InputParameters;
import uk.ac.ebi.webservices.axis1.stubs.iprscan.JDispatcherService_Service;
import uk.ac.ebi.webservices.axis1.stubs.iprscan.JDispatcherService_ServiceLocator;
import uk.ac.ebi.webservices.axis1.stubs.iprscan.WsResultType;

/**
 *
 * @author aacain
 */
public class InterProScan {

    public String run() {
        IPRScanClient client = new IPRScanClient();
        JDispatcherService_Service service = new JDispatcherService_ServiceLocator();
        //return null;

        try {
            client.getSrvProxy();
            //Submit a job
            InputParameters params = new InputParameters();
            params.setNocrc(Boolean.FALSE);
            String jobId = null;
            //params.setSequence("AEW17187.1");
            //params.setSequence("B0CK30");
            params.setSequence("MKKLIYPTLITSLFLTACVSGDKDYIETPAPSQIADLRDNDNDGVINARDICPGTPRGAQIDNDGCAEYVEQSDKKDLKILFANNSSEISPIFKSEIRTMAEFLAEYPETSIQLQGFASQQGNAEYNIRLSELRASAVRVALINYGVNPDKIETIGFGDTLLTAKGDSAVSHALNRRVVATVVGFKGDVVDEWNIFTRKKK");
            System.out.println(System.currentTimeMillis());
            jobId = client.runApp("cainjunk@carolina.rr.com", "title", params);
            String[] results = client.getResults(jobId, "C:\\Users\\lucy\\Documents\\output\\" + jobId, "xml");
            WsResultType[] resultTypes = client.getResultTypes(jobId);
            for (WsResultType wsResultType : resultTypes) {
                System.out.println(wsResultType.getIdentifier());
            }
            System.out.println(System.currentTimeMillis());
            for (String string : results) {
                System.out.println(string);
            }
        } catch (ServiceException ex) {
            Logger.getLogger(this.getClass().toString()).log(Level.SEVERE, "Service error" , ex);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().toString()).log(Level.SEVERE, "Misc exception" , ex);
        } 
        return null;
    }

    public static void addGenericOptions(Options options) {
        options.addOption("help", "help", false, "help on using this client");
        options.addOption("async", "async", false, "perform an asynchronous job");
        options.addOption("polljob", "polljob", false, "poll for the status of an asynchronous job and get the results");
        options.addOption("status", "status", false, "poll for the status of an asynchronous job");
        options.addOption("email", "email", true, "Your email address");
        options.addOption("jobid", "jobid", true, "Job identifier of an asynchronous job");
        options.addOption("stdout", "stdout", false, "print to standard output");
        options.addOption("outfile", "outfile", true, "file name to save the results");
        options.addOption("outformat", "outformat", true, "Output format (txt or xml)");
        options.addOption("quiet", "quiet", false, "Decrease output messages");
        options.addOption("verbose", "verbose", false, "Increase output messages");
        options.addOption("params", "params", false, "List parameters");
        options.addOption("paramDetail", "paramDetail", true, "List parameter information");
        options.addOption("resultTypes", "resultTypes", false, "List result types for job");
        options.addOption("debugLevel", "debugLevel", true, "Debug output");
        options.addOption("endpoint", "endpoint", true, "Service endpoint URL");
    }

    public static void addApplicationSpecificOptions(Options options) {
        options.addOption("multifasta", "multifasta", false,
                "Multiple fasta sequence input");
        // Application specific options
        options.addOption("appl", "appl", true, "Signature methods");
        options.addOption("app", "app", true, "Signature methods");
        options.addOption("crc", "crc", false, "Enable CRC");
        options.addOption("nocrc", "nocrc", false, "Disable CRC");
        options.addOption("goterms", "goterms", false, "Enable GO terms");
        options.addOption("nogoterms", "nogoterms", false, "Disable GO terms");
        options.addOption("sequence", true,
                "sequence file or datbase entry database:acc.no");
    }
    
    public static void main(String[] args){
        InterProScan scan = new InterProScan();
        scan.run();
    }
}
