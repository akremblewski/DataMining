package pl.edu.agh.ftj.datamaining.weka.webservice;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import pl.edu.agh.ftj.datamaining.weka.algorithm.WekaAlgorithm;
import pl.edu.agh.ftj.datamaining.weka.algorithm.WekaAnswer;

/**
 * Klasa implementująca inferfejs Webservisu dla Weki
 * Służy jako "most" łączący Webservisy z biblioteką Weka.
 * Webservis stworzony dla REST
 * adres:  http://localhost:8080/WekaRESTService/rest/{nazwaMetody}?{parametr1=aaa&parametr2=bbb}
 * wsdl:   ?? n/a
 * @author Szymon Skupien
 */
@Path("/")
public class WekaService implements IWekaService {

    /**
     * w założeniu obiekt, w którym będzie obsługiwana biblioteka Weki.
     * Na razie nie dostałem jeszcze informacji o tym jak klasa będzie wyglądała.
     */
    private WekaAlgorithm wekaAlgorithm = new WekaAlgorithm();


    /**
     * Funkcja odpowiadajaca na rzadanie GET http://localhost:8080/WekaRESTService/rest/
     * @return Strona html przekierowujaca na adres http://prgzsp.ftj.agh.edu.pl/trac/P3-DataMining
     */
    @GET
    @Produces("text/html")
    public String hello(){
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
        for (int i = 0; i < wekaAlgorithm.getAlgorithms().length; i++) {
            r += "<return>" + wekaAlgorithm.getAlgorithms()[i] + "</return>";
        }
        r += "</getAlgorithmsResponse>";
        return r;
      
    }



    /**
     * Funkcja przygotowujaca algorytm Weki
     *
     * @param algorithmType wybiera typ algorytmu
     * @param location      adres URL do webservisu dbapi
     * @param id            id do danych (do webservisu dbapi)
     * @param table         table do danych (do webservisu dbapi)
     * @param options       opcje algorytmu 
     * @return              String z odpowiedzia czy operacja wykonala sie poprawnie
     */
    @GET
    //@PUT
    @Produces("text/plain")
    @Path("/setAlgorithm") //np.  http:// ... rest/setAlgorithm?algorithmType=1&location=blabla&id=abc
    public String setAlgorithm(@QueryParam("algorithmType") Integer algorithmType, @QueryParam("location") String location, @QueryParam("id") String id, @QueryParam("table") String table) {
  
        if (algorithmType == null || location == null || id == null) {
            return "NieOk";
        }
        wekaAlgorithm.setAlgorithmType(algorithmType);
        
        //   wekaAlgorithm.setOptions(options);
        //TODO: wyciaganie danych z webserwisu dbapi; ustawienie parametrow algorytmu; wyslanie odpowiedzi, String[] options
        return "OKSet";
    }

    /**
     * Funkcja uruchamia dzialanie algorytmu
     * @return Zwraca przetworzone dane z Weki
     */
    @GET
    @Produces("application/xml")
    @Path("/runAlgorithm")
    public WekaAnswer runAlgorithm(){
        wekaAlgorithm.run();
        return wekaAlgorithm.getData();
    }



}
