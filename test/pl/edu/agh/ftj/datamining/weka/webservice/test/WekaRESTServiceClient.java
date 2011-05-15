package pl.edu.agh.ftj.datamining.weka.webservice.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import pl.edu.agh.ftj.datamining.weka.algorithm.WekaAnswer;

/** Jersey REST client generated for REST resource: WekaService [/]<br>
 *  USAGE:<pre>
 *        WekaRESTServiceClient client = new WekaRESTServiceClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 *  </pre>
 * @author Szymon Skupien
 * @version 0.2
 */
public class WekaRESTServiceClient {

    private WebResource webResource;
    private Client client;
    /*
     * Podstawowy adres do WebServisu REST 
     */
    private String base_uri;

    /*
     * Konstruktor klienta, łączy się z webservisem
     * @param adres uri webservisu rest
     */
    public WekaRESTServiceClient(String base_uri) {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        this.base_uri = base_uri;
        webResource = client.resource(this.base_uri);
    }

    /*
     * Metoda wywołuje getAlgorithms z WebServisu
     * @return XML z dostępnymi algorytmami
     */
    public String getAlgorithms() throws UniformInterfaceException {
        return webResource.path("getAlgorithms").accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(String.class);
    }

    /**
     * Uruchamia algorytm, w parametrach podaje się:
     * @param typ algorytmu
     * @param lokacja WS DBApi
     * @param id (DBApi)
     * @param table (DBApi)
     * @param options 
     * @return odpowiedz WekaAnswer
     */
    public WekaAnswer runAlgorithm(int algorithmType, String location, String id, String table, String options) {
        
        WekaAnswer wekaAns = new WekaAnswer();
        byte[] odpBytes;

        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("algorithmType", String.valueOf(algorithmType));
        queryParams.add("location", location);
        queryParams.add("id", id);
        queryParams.add("table", table);
        queryParams.add("options", options);

        InputStream odp = webResource.path("runAlgorithm").queryParams(queryParams).accept(MediaType.APPLICATION_OCTET_STREAM_TYPE).get(InputStream.class);
        //System.out.println(odp.toString());
       int ile = 0;
        try {
           // ile = odp.read(odpBytes);
            odpBytes = readFromStream(odp);
            ile = odpBytes.length;
           // System.out.println(new String(odpBytes));
            ByteArrayInputStream bis = new ByteArrayInputStream(odpBytes);
            ObjectInput in = new ObjectInputStream(bis);
            wekaAns = (WekaAnswer) in.readObject();
        }
        catch (IOException e) {
           Logger.getLogger(WekaRESTServiceClient.class.getName()).log(Level.SEVERE, "blad w strumieniu bajtow", e);
        }
        catch(ClassNotFoundException ex){
            Logger.getLogger(WekaRESTServiceClient.class.getName()).log(Level.SEVERE, "blad w serializacji", ex);
        }
        catch(NullPointerException exx){
           // System.out.println("taaaakk");
            Logger.getLogger(WekaRESTServiceClient.class.getName()).log(Level.SEVERE, "ERROR NULL POINTER EXEPTION", exx);
        }
       // System.out.println("ile: " + String.valueOf(ile));
        return wekaAns;
    }

    /*
     * Zamyka połączenie
     */
    public void close() {
        client.destroy();
    }


    /**
     * Funkcja konwertuje InputStream do tablicy bajtów
     * http://books.google.pl/books?id=_jQtCL5_vAcC&lpg=PA71&dq=StreamingOutput%20rest%20java&pg=PA73#v=onepage&q=StreamingOutput%20rest%20java&f=false
     * @param stream InputStream
     * @return tablica bajtów byte[]
     * @throws IOException
     */
    public byte[] readFromStream(InputStream stream) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1000];
        int  wasRead = 0;
        do{
            wasRead = stream.read(buffer);
            if(wasRead > 0){
                baos.write(buffer, 0, wasRead);
            }
        }
        while(wasRead > -1);

        return baos.toByteArray();
    }
}
