package pl.edu.agh.ftj.datamining.weka.webservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceRef;
import pl.edu.agh.ftj.datamining.dbapi.webservice.DataAccessService;
import pl.edu.agh.ftj.datamining.dbapi.webservice.DataAccessServicePortType;
import pl.edu.agh.ftj.datamining.weka.algorithm.WekaAlgorithm;
import pl.edu.agh.ftj.datamining.weka.algorithm.WekaAnswer;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Klasa implementująca inferfejs Webservisu dla Weki
 * Służy jako "most" łączący Webservisy z biblioteką Weka.
 * Webservis stworzony dla REST
 * adres:  http://localhost:8080/WekaRESTService/rest/{nazwaMetody}?{parametr1=aaa&parametr2=bbb}
 * wadl:   http://localhost:8080/WekaRESTService/rest/application.wadl
 * @author Szymon Skupien
 */
@Path("/")
public class WekaService implements IWekaService {

    //Adres do WSDL webservisu ustawiony odgornie
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/axis2/services/DataAccessService.wsdl")
    private DataAccessService service;

    private static final Logger log = Logger.getLogger("WekaRESTServiceLog");

    /**
     * Funkcja odpowiadajaca na zadanie GET http://localhost:8080/WekaRESTService/rest/
     * @return Strona html przekierowujaca na adres http://prgzsp.ftj.agh.edu.pl/trac/P3-DataMining
     */
    @GET
    @Produces("text/html")
    public String hello() {
        //troche jak z czasow servletow, ale proste i dziala - przekierowanie
        return "<html><head><META HTTP-EQUIV=\"Refresh\" CONTENT=\"1; URL=http://prgzsp.ftj.agh.edu.pl/trac/P3-DataMining\"></head><body></body></html>";
    }

    /**
     * @return zwraca tablice z nazwami dostepnych algorytmow w postaci XML'a
     */
    @GET
    @Produces("application/xml")
    @Path("/getAlgorithms")
    public String getAlgorithms() {

        String r = "<getAlgorithmsResponse xmlns=\"http://webservice/weka/datamaining/ftj/agh/edu/pl/xsd\">";
        for (int i = 0; i < WekaAlgorithm.getAlgorithms().length; i++) {
            r += "<return>" + WekaAlgorithm.getAlgorithms()[i] + "</return>";
        }
        r += "</getAlgorithmsResponse>";
        return r;

    }

    /**
     * Funkcja uruchamia dzialanie algorytmu
     * @param algorithmType wybiera typ algorytmu
     * @param id            id do danych (do webservisu dbapi)
     * @param table         table do danych (do webservisu dbapi)
     * @param options       opcje algorytmu
     * @return Zwraca przetworzone dane z Weki w postaci zserializowanego obiektu WekaAnswer zserializowanej (ciÄ…g bajtĂłw)
     */
    @GET
    @Produces("application/octet-stream")
    @Path("/runAlgorithm")
    public Response runAlgorithm(@QueryParam("algorithmType") Integer algorithmType,/* @QueryParam("location") String location, */@QueryParam("id") String id, @QueryParam("table") String table, @QueryParam("options") String options) {
         
        //brakuje jednej z opcji
        if(algorithmType == null || id == null || table == null || options == null){
                
                final WekaAnswer wekaAnswer = new WekaAnswer();

                //TODO:ustawic dokladniejsze info
                wekaAnswer.setInfo("Brakuje jednej z opcji");
             
                //serializuje obiekt
                byte[] bytes = wekaAnswer2Byte(wekaAnswer);

               
                //wysylka
                return Response.ok(bytes, MediaType.APPLICATION_OCTET_STREAM).build();
        }
        // zeby nie bylo null, wstawiam dane testowe
        String result = pogoda;
        try {
            
            //DataAccessService service = new DataAccessService(new URL(location), new QName("DataAccess"));
            
            DataAccessServicePortType port = service.getDataAccessServiceHttpSoap11Endpoint();

            //wywoluje zdalna metode WebServisu DbAPI
            result = port.getData(id, table);
            //System.out.println("Result = "+result);
        } catch (Exception ex) {
            // cos poszlo nietak
            log.log(Level.ALL, "DataAccess ERROR!!!!!!!!!!");
        }

        // String to Instances
        Instances data = getInstancesFromString(result);

        // String to String[]
        String[] opt = parseStringOptions(options);


        //tworze algorytm
        WekaAlgorithm alg = new WekaAlgorithm();

        //ustawiam niezbedne opcje
        alg.setData(data);
        alg.setAlgorithmType(algorithmType);
        alg.setOptions(opt);
        alg.run();

        //pobieram odpowiedz
        final WekaAnswer wekaAnswer = alg.getData();

        //serializuje obiekt
        byte[] bytes = wekaAnswer2Byte(wekaAnswer);
        
        //wysylka
        return Response.ok(bytes, MediaType.APPLICATION_OCTET_STREAM).build();

    }


    /**
     * Funkcja konwertujaca string do klasy instances weki
     * @param data String
     * @return Instances weki
     */
    private Instances getInstancesFromString(String dane){
        
        DataSource source = null;
        Instances data = null;
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(dane.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            source = new DataSource(is);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            data = source.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    /**
     * Funkcja konwertujaca ciag opcji do tablicy opcji
     * @param options String
     * @return
     */
    private String[] parseStringOptions(String options){
        String[] retStr = options.split(";");
        for(int i=0; i<retStr.length; i++){
            retStr[i] = "-"+retStr[i];
        }
        return retStr;
    }


    /**
     * FUnkcja ktora serializuje obiekt klasy WekaAnswer do ciagu bajtow
     * @param ans WekaAnswer
     * @return byte[]
     */
    private byte[] wekaAnswer2Byte(WekaAnswer ans){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(ans);
            bytes = bos.toByteArray();
        } catch (Exception e) {

            log.log(Level.ALL, "runAlgorithm error:");
            log.log(Level.ALL, e.getMessage());
        }
       // System.out.println(bytes.toString());
        return bytes;
    }

/**
 * Dane Testowe
 */
      public String pogoda = "@relation Test\n"
            + "@attribute pogoda {slonecznie, pochmurno, deszczowo}\n"
            + "@attribute temperatura real\n"
            + "@attribute wilgotnosc real\n"
            + "@attribute wiatr {TRUE, FALSE}\n"
            + "@attribute zabawa {tak, nie}\n\n"
            + "@data\n"
            + "slonecznie,85,85,FALSE,nie\n"
            + "slonecznie,80,90,TRUE,nie\n"
            + "pochmurno,83,86,FALSE,tak\n"
            + "deszczowo,70,96,FALSE,tak\n"
            + "deszczowo,68,80,FALSE,tak\n"
            + "deszczowo,65,70,TRUE,nie\n"
            + "pochmurno,64,65,TRUE,tak\n"
            + "slonecznie,72,95,FALSE,nie\n"
            + "slonecznie,69,70,FALSE,tak\n"
            + "deszczowo,75,80,FALSE,tak\n"
            + "slonecznie,75,70,TRUE,tak\n"
            + "pochmurno,72,90,TRUE,tak\n"
            + "pochmurno,81,75,FALSE,tak\n"
            + "deszczowo,71,91,TRUE,nie";

}
