package pl.edu.agh.ftj.datamining.weka.webservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.edu.agh.ftj.datamining.weka.algorithm.WekaAlgorithm;
import pl.edu.agh.ftj.datamining.weka.algorithm.WekaAnswer;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import pl.edu.agh.ftj.datamining.dbapi.webservice.DataAccessService;
import pl.edu.agh.ftj.datamining.dbapi.webservice.DataAccessServicePortType;


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
         
       
        int parm = checkParameters(algorithmType, id, table);
        WekaAnswer wekaAnswer = new WekaAnswer();
        byte[] bytes = null;
        switch(parm){
            case (1):
                wekaAnswer.setInfo("Brakuje typu algorytmu");
                break;
            case (3):
                wekaAnswer.setInfo("Brakuje id bazy danych");
                break;
            case (4):
                wekaAnswer.setInfo("Brakuje typu algorytmu oraz id bazy danych");
                break;
            case (5):
                wekaAnswer.setInfo("Brakuje nazwy tabeli");
                break;
            case (6):
                 wekaAnswer.setInfo("Brakuje typu algorytmu oraz nazwy tabeli");
                 break;
            case (8):
                 wekaAnswer.setInfo("Brakuje id oraz nazwy tabeli");
                 break;
            case (9):
                 wekaAnswer.setInfo("Brakuje typu algorytmu, id oraz nazwy tabeli");
                 break;
            default:
                break;
        }
        //jest jakis brak parametru
        if(parm != 0){
            wekaAnswer.setCorrect(false);
            //serializuje obiekt
            bytes = wekaAnswer2Byte(wekaAnswer);
            //wysylka
            return Response.ok(bytes, MediaType.APPLICATION_OCTET_STREAM).build();
        }

        //odpowiedz z DB
        String result = null;
        try {
            

           
                DataAccessService service = new DataAccessService();
                DataAccessServicePortType port = service.getDataAccessServiceHttpSoap11Endpoint();

                result = port.getData(id, table);

            
        } catch (Exception ex) {
            // cos poszlo nietak
            log.log(Level.ALL, "DataAccess ERROR!");
        }
     
        if(result == null){
            wekaAnswer.setInfo("Bład w polaczeniu z bazą danych");
            wekaAnswer.setCorrect(false);
            //serializuje obiekt
            bytes = wekaAnswer2Byte(wekaAnswer);
            //wysylka
            return Response.ok(bytes, MediaType.APPLICATION_OCTET_STREAM).build();
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
        wekaAnswer = null;
        wekaAnswer = alg.getData();

        //serializuje obiekt
        bytes = wekaAnswer2Byte(wekaAnswer);
        
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
        ArrayList<String> options_temp = new ArrayList<String>();

        for(int i=0; i<retStr.length; i++){
            String[] retStr2 = retStr[i].split(" ");
            options_temp.add("-" + retStr2[0]);
            if(retStr2.length==2)
                options_temp.add(retStr2[1]);

        }
        String [] optt = new String[options_temp.size()];
        for(int i=0; i<optt.length; i++){
            optt[i] = options_temp.get(i);
            System.out.println(optt[i]);
        }
        return optt;
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
     * Funkcja sprawdza poprawnosc otrzymanych parametrow
     * @param algorithmType
     * @param id
     * @param table
     * @param options
     * @return wartosc: 0=ok, 1=brak algorithmType, 3=brak id, 5=brak table.
     * Jesli brakuje 2 lub wiecej parametrow warstosci sie sumuja. 
     */
    private int checkParameters(Integer algorithmType, String id, String table){
        int ret = 0;
        if(algorithmType == null){
            ret += 1;
        }
        if(id == null){
            ret += 3;
        }
        if(table == null){
            ret += 5;
        }
        return ret;
    }

}
