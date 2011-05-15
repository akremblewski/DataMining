package pl.edu.agh.ftj.datamining.weka.webservice.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.edu.agh.ftj.datamining.weka.algorithm.WekaAnswer;
import pl.edu.agh.ftj.datamining.weka.webservice.WekaService;

/**
 * JUnit Test do klasy WekaService
 * @author Szymon Skupień
 * @version 1.0
 */
public class WekaServiceTest {

    /**
     * Adres do webservisu Weki 
     */
    private final String WekaRESTServiceURI = "http://localhost:8080/WekaRESTService/rest";
    /**
     * Klasa klienta WebServisu
     */
    private WekaRESTServiceClient client;

    /**
     * Metoda wykonuje sie przed wykonaniem każdego testu
     * tworzy nowy obiekt klienta servisu
     */
    @Before
    public void before() {
        this.client = new WekaRESTServiceClient(WekaRESTServiceURI);
    }

    /**
     * Test of getAlgorithms method, of class WekaService.
     */
    @Test
    public void testGetAlgorithms() {

        String expResult = "<getAlgorithmsResponse xmlns=\"http://webservice/weka/datamaining/ftj/agh/edu/pl/xsd\"><return>SimpleKMeans</return><return>EM</return><return>HierarchicalClusterer</return><return>Cobweb</return></getAlgorithmsResponse>";

        String response = client.getAlgorithms();

        assertEquals(expResult, response);

    }

    /**
     * Test of runAlgorithm method, of class WekaService.
     */
    @Test
    public void testRunAlgorithm() throws IOException, ClassNotFoundException {

        WekaAnswer response = client.runAlgorithm(1, "location", "ïd", "table", "opt");
        Response expResp = new WekaService().runAlgorithm(1, "location", "id", "table", "opt");
        byte[] expRespByte = (byte[]) expResp.getEntity();
        WekaAnswer expResponse = new WekaAnswer();
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(expRespByte);
            ObjectInput in = new ObjectInputStream(bis);
            expResponse = (WekaAnswer) in.readObject();
        } catch (IOException e) {
           // Logger.getLogger(WekaRESTServiceClient.class.getName()).log(Level.SEVERE, "blad w strumieniu bajtow", e);
        } catch (ClassNotFoundException ex) {
           // Logger.getLogger(WekaRESTServiceClient.class.getName()).log(Level.SEVERE, "blad w serializacji", ex);
        } catch (NullPointerException exx) {
   
         //   Logger.getLogger(WekaRESTServiceClient.class.getName()).log(Level.SEVERE, "ERROR NULL POINTER EXEPTION", exx);
        }

     

        assertEquals(response.getAlgorithmName(), expResponse.getAlgorithmName());
        assertEquals(response.getAlgorithmType(), expResponse.getAlgorithmType());
        assertEquals(response.getRevision(), expResponse.getRevision());

    }

    /**
     * Wykonuje się po każdym tescie
     * zamyka klienta i kasuje referencje objektu
     */
    @After
    public void after() {
        client.close();
        client = null;
    }
}
