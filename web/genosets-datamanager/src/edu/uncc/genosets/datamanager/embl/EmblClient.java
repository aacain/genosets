/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.embl;

import edu.uncc.genosets.datamanager.fasta.Fasta;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aacain
 */
public abstract class EmblClient {

    public abstract String getEmblFile(String accessionId);

    public abstract String getFastaFile(String accessionId);

    public static EmblClient instantiate() {
        return new EmblClientImpl();
    }

    public static class EmblClientImpl extends EmblClient {

//        @Override
//        public String getEmblFile(String accessionId) {
//            String result = null;
//            try {
//                // Create a service proxy to get the embl file
//                WSDBFetchServerService service = (WSDBFetchServerService) new WSDBFetchServerServiceLocator();
//                WSDBFetchServer srvProxy = service.getWSDbfetch();
//                result = srvProxy.fetchData("embl:" + accessionId, "embl", "raw");
//            } catch (Exception ex) {
//                LoggerFactory.getLogger(this.getClass()).error("EMBL-Bank is currently unavailable. The requested file was not downloaded. Try again later.");
//            } finally {
//                return result;
//            }
//        }
        
        public String getEmblFile(String accessionId) {
            BufferedReader rd = null;
            StringBuilder emblString = new StringBuilder();
            try {
                URL u = null;
                try {
                    u = new URL("http://www.ebi.ac.uk/ena/data/view/" + accessionId + "&display=text");
                } catch (MalformedURLException ex) {
                    Logger.getLogger(EmblClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                // Connect
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) u.openConnection();
                } catch (IOException ex) {
                    Logger.getLogger(EmblClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                //read the header line
                String line = null;
                while ((line = rd.readLine()) != null) {
                    emblString.append(line).append("\n");
                }
            } catch (IOException ex) {
                Logger.getLogger(EmblClient.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    rd.close();
                    
                } catch (IOException ex) {
                    Logger.getLogger(EmblClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return emblString.toString();
        }

        public String getFastaFile(String accessionId) {
            BufferedReader rd = null;
            StringBuilder fasta = new StringBuilder();
            try {
                URL u = null;
                try {
                    u = new URL("http://www.ebi.ac.uk/Tools/dbfetch/dbfetch?db=embl&id=" + accessionId +"&format=fasta&style=raw&Retrieve=Retrieve");
                } catch (MalformedURLException ex) {
                    Logger.getLogger(EmblClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                // Connect
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) u.openConnection();
                } catch (IOException ex) {
                    Logger.getLogger(EmblClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                //read the header line
                String line = null;
                while ((line = rd.readLine()) != null) {
                    fasta.append(line).append("\n");
                }
            } catch (IOException ex) {
                Logger.getLogger(EmblClient.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    rd.close();
                    
                } catch (IOException ex) {
                    Logger.getLogger(EmblClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return fasta.toString();
        }

        public static void main(String[] args) throws IOException {
            String fastaString = EmblClient.instantiate().getFastaFile("KB850251");
            Fasta parse = Fasta.parse(fastaString.toString());
            for (Fasta.FastaItem fastaItem : parse.getItems()) {
                System.out.println("id: " + fastaItem.getId());
                System.out.println(fastaItem.getSequence().length());
            }
        }
    }
}
