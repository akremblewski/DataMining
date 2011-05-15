package pl.edu.agh.ftj.datamining.weka.webservice;

import javax.ws.rs.core.Response;
import pl.edu.agh.ftj.datamining.weka.algorithm.WekaAnswer;

/**
 * Interfejs do Webservisu udostępniającego funkcjonalność Weki
 * @author Szymon Skupien
 */
public interface IWekaService {

    /**
     * @return zwraca tablicę (XML) z nazwami dostępnych algorytmów
     */
    public String getAlgorithms();


     /**
     * Funkcja uruchamia dzialanie algorytmu
     * @param algorithmType wybiera typ algorytmu (od 0 do 3) (indeks tablicy z metody getAlgorithms)
     * @param location      adres URL do webservisu dbapi
     * @param id            id do danych (do webservisu dbapi)
     * @param table         table do danych (do webservisu dbapi)
     * @param options       opcje algorytmu podawane w ciągu
     * @return Zwraca WekaAnswer przetworzone dane z Weki w postaci ciągu bajtów (zserializowany obiekt WekaAnswer)
     */
    public Response runAlgorithm(Integer algorithmType, String location, String id, String table, String options);;

}
