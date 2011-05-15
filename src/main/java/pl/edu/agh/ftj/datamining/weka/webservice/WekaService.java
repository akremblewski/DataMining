package pl.edu.agh.ftj.datamining.weka.webservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
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

    private static final Logger log = Logger.getLogger("WekaRESTServiceLog");

    /**
     * Funkcja odpowiadajaca na rzadanie GET http://localhost:8080/WekaRESTService/rest/
     * @return Strona html przekierowujaca na adres http://prgzsp.ftj.agh.edu.pl/trac/P3-DataMining
     */
    @GET
    @Produces("text/html")
    public String hello() {
        //troche jak z czasow servletow, ale proste i dziala - przekierowanie
        return "<html><head><META HTTP-EQUIV=\"Refresh\" CONTENT=\"1; URL=http://prgzsp.ftj.agh.edu.pl/trac/P3-DataMining\"></head><body></body></html>";
    }

    /**
     * @return zwraca tablicę z nazwami dostępnych algorytmów (inaczej sie nie da,
     * trzeba zrobic klase opakowujaca String'a)
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
     * @param location      adres URL do webservisu dbapi
     * @param id            id do danych (do webservisu dbapi)
     * @param table         table do danych (do webservisu dbapi)
     * @param options       opcje algorytmu
     * @return Zwraca przetworzone dane z Weki w postaci zserializowanego obiektu WekaAnswer zserializowanej (ciąg bajtów)
     */
    @GET
    @Produces("application/octet-stream")
    @Path("/runAlgorithm")
    public Response runAlgorithm(@QueryParam("algorithmType") Integer algorithmType, @QueryParam("location") String location, @QueryParam("id") String id, @QueryParam("table") String table, @QueryParam("options") String options) {

    //TODO: odczyt obiektu Instances z WebServisu DBApi

        DataSource source = null;
        Instances data = null;
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(pogoda.getBytes("UTF-8"));
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


        //TODO odczyt opcji ze stringu
        String[] opt = new String[1];
        opt[0] = "-O";
        WekaAlgorithm alg = new WekaAlgorithm();

        alg.setData(data);
        alg.setAlgorithmType(algorithmType);
        alg.setOptions(opt);
        alg.run();
        final WekaAnswer wekaAnswer = alg.getData();


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(wekaAnswer);
            bytes = bos.toByteArray();
        } catch (Exception e) {

            log.log(Level.ALL, "runAlgorithm error:");
            log.log(Level.ALL, e.getMessage());
        }

        return Response.ok(bytes, MediaType.APPLICATION_OCTET_STREAM).build();

    }


    /**
     * Dane testowe
     */
    private String pogoda = "@relation Test\n"
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
