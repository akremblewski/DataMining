package pl.edu.agh.ftj.datamining.weka.algorithm;

import weka.clusterers.Cobweb;
import weka.clusterers.EM;
import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.FarthestFirst;
import weka.core.Instances;

/**
 * Klasa odpowiedzialna za komunikację z biblioteką Weki.
 * @author Bartłomiej Wojas, Adrian Kremblewski, Szymon Skupień
 * @version 0.9.7
 */
public class WekaAlgorithm {
    /**
     * Obiekt przechowujący dane do analizy.
     */
    private Instances data = null;

    /**
     * Typ algorytmu jaki ma zostać użyty. Dostępne opcje: 1 - SimpleKMeans, 2 - EM, 3 - HierarchicalClusterer, 4 - Cobweb, 5 - FarthestFirst
     */
    private int algorithmType = 0;

    /**
     * Tablica parametrów wg. których ma funkcjonować algorytm.
     */
    private String[] options;

    /**
     * Obiekt zawierajacy dane zwracane przez Wekę.
     */
    private WekaAnswer wekaAnswer = new WekaAnswer();

    /**
     * Obiekt przechowujący informacje dotyczące wykonania algorytmów.
     */
    private String info = "\n==== WekaAnswer informations ====\n";

    /**
     * Przechowuje informację o tym czy podczas wykonywania algorytmów
     * doszło do błędu (false) czy też nie (true).
     */
    private boolean correct = true;

    /**
     * Tablica z nazwami udostępnianych algorytmów.
     */
    private static final String[] algorithms = {
        "SimpleKMeans",
        "EM",
        "HierarchicalClusterer",
        "Cobweb",
        "FarthestFirst"
    };

    /**
     * Metoda zwracająca obiekt z danymi będącymi wynikiem działania algorytmu.
     * @return Obiekt z danymi.
     */
    public WekaAnswer getData() {
        return wekaAnswer;
    }

    /**
     * Metoda rozpoczynająca proces przetwarzania. Inicjuje i uruchamia odpowiednie algorytmy.
     */
    public void run() {
        switch(algorithmType) {
            case 1: wekaAnswer.setAlgorithmName(algorithms[0]);
                    wekaAnswer.setAlgorithmType(1);
                    runSimpleKMeans();
                    break;
            case 2: wekaAnswer.setAlgorithmName(algorithms[1]);
                    wekaAnswer.setAlgorithmType(2);
                    runEM();
                    break;
            case 3: wekaAnswer.setAlgorithmName(algorithms[2]);
                    wekaAnswer.setAlgorithmType(3);
                    runHierarchicalClusterer();
                    break;
            case 4: wekaAnswer.setAlgorithmName(algorithms[3]);
                    wekaAnswer.setAlgorithmType(4);
                    runCobweb();
                    break;
            case 5: wekaAnswer.setAlgorithmName(algorithms[4]);
                    wekaAnswer.setAlgorithmType(5);
                    runFarthestFirst();
                    break;
            default: wekaAnswer.setAlgorithmName("Unknown");
                     log("Nieznany typ algorytmu.");
        }

        if(correct == false) {
            wekaAnswer = new WekaAnswer();
            wekaAnswer.setCorrect(correct);
        }
        wekaAnswer.setInfo(info);
    }

    /**
     * Metoda ustawiająca typ algorytmu jaki ma zostać użyty.
     * @param algorithmType Typ algorytmu jaki ma zostać użyty. Dostępne opcje: 1 - SimpleKMeans, 2 - EM, 3 - HierarchicalClusterer, 4 - Cobweb, 5 - FarthestFirst.
     */
    public void setAlgorithmType(int algorithmType) {
        this.algorithmType = algorithmType;
    }

    /**
     * Ustawia dane, które mają zostać przetworzone.
     * @param data Dane, które mają zostać poddane przetworzeniu.
     */
    public void setData(Instances data) {
        this.data = data;
    }

   /**
    * Ustawia opcje dla algorytmu.
    * Opis parametrów oraz algorytmów znajduje się na TracWiki projektu.
    * URL: http://prgzsp.ftj.agh.edu.pl/trac/P3-DataMining/wiki/Algorytmy
    * @param options Opcje wg. których ma działać algorytm.
    */
    public void setOptions(String[] options) {
        this.options = options;
    }

    /**
     * Metoda zwracająca dostępne nazwy algorytmów.
     * @return Tablica z nazwami algorytmów.
     */
    public static String[] getAlgorithms() {
        return algorithms;
    }

    /**
     * Uruchamia algorytm SimpleKMeans.
     */
    private void runSimpleKMeans() {
        SimpleKMeans skm = new SimpleKMeans();

        try {
            try {
                skm.setOptions(options);
            } catch(Exception e) {
                log("Niepoprawny obiekt Options.");
                log(e.getMessage());
                correct = false;
            }
            try {
                skm.buildClusterer(data);
            } catch(Exception e) {
                log("Niepoprawny obiekt z danymi.");
                log(e.getMessage());
                correct = false;
            }
            //rozpoczęcie budowania obiektu z danymi
            wekaAnswer.setAssignments(skm.getAssignments());
//            wekaAnswer.setCapabilities(skm.getCapabilities());
            wekaAnswer.setClusterCentroids(skm.getClusterCentroids());
            wekaAnswer.setClusterNominalCounts(skm.getClusterNominalCounts());
            wekaAnswer.setClusterSizes(skm.getClusterSizes());
            wekaAnswer.setClusterStandardDevs(skm.getClusterStandardDevs());
            wekaAnswer.setDistanceFunction(skm.getDistanceFunction());
            wekaAnswer.setMaxIterations(skm.getMaxIterations());
            wekaAnswer.setNumClusters(skm.getNumClusters());
            wekaAnswer.setOptions(options);
            wekaAnswer.setRevision(skm.getRevision());
            wekaAnswer.setSquaredError(skm.getSquaredError());
            wekaAnswer.setNumberOfClusters(skm.numberOfClusters());
        } catch(Exception e) {
            log(e.getMessage());
            correct = false;
        }
        
        if(correct) log("OK");
    }

    /**
     * Uruchamia algorytm EM.
     */
    private void runEM() {
        EM em = new EM();

        try {
            try {
                em.setOptions(options);
            } catch(Exception e) {
                log("Niepoprawny obiekt Options.");
                log(e.getMessage());
                correct = false;
            }
            try {
                em.buildClusterer(data);
            } catch(Exception e) {
                log("Niepoprawny obiekt z danymi.");
                log(e.getMessage());
                correct = false;
            }
            //rozpoczęcie budowania obiektu z danymi
            wekaAnswer.setClusterPriors(em.getClusterPriors());
            wekaAnswer.setClusterModelsNumericAtts(em.getClusterModelsNumericAtts());
            wekaAnswer.setMinStdDev(em.getMinStdDev());
//            wekaAnswer.setCapabilities(em.getCapabilities());
            wekaAnswer.setMaxIterations(em.getMaxIterations());
            wekaAnswer.setNumClusters(em.getNumClusters());
            wekaAnswer.setOptions(options);
            wekaAnswer.setRevision(em.getRevision());
        } catch(Exception e) {
            log(e.getMessage());
            correct = false;
        }

        if(correct) log("OK");
    }

    /**
     * Uruchamia algorytm HierarchicalClusterer.
     */
    private void runHierarchicalClusterer() {
        HierarchicalClusterer hc = new HierarchicalClusterer();

        try {
            try {
                hc.setOptions(options);
            } catch(Exception e) {
                log("Niepoprawny obiekt Options.");
                log(e.getMessage());
                correct = false;
            }
            try {
                hc.buildClusterer(data);
            } catch(Exception e) {
                log("Niepoprawny obiekt z danymi.");
                log(e.getMessage());
                correct = false;
            }
            //rozpoczęcie budowania obiektu z danymi
//            wekaAnswer.setCapabilities(hc.getCapabilities());
            wekaAnswer.setDistanceFunction(hc.getDistanceFunction());
//            wekaAnswer.setLinkType(hc.getLinkType());
            wekaAnswer.setNumClusters(hc.getNumClusters());
            wekaAnswer.setOptions(options);
            wekaAnswer.setPrintNewick(hc.getPrintNewick());
            wekaAnswer.setRevision(hc.getRevision());
            wekaAnswer.setGraph(hc.graph());
            wekaAnswer.setGraphType(hc.graphType());
        } catch(Exception e) {
            log(e.getMessage());
            correct = false;
        }

        if(correct) log("OK");
    }

    /**
     * Uruchamia algorytm Cobweb.
     */
    private void runCobweb() {
        Cobweb cw = new Cobweb();

        try {
            try {
                cw.setOptions(options);
            } catch(Exception e) {
                log("Niepoprawny obiekt Options.");
                log(e.getMessage());
                correct = false;
            }
            try {
                cw.buildClusterer(data);
            } catch(Exception e) {
                log("Niepoprawny obiekt z danymi.");
                log(e.getMessage());
                correct = false;
            }
            //rozpoczęcie budowania obiektu z danymi
            wekaAnswer.setAcuity(cw.getAcuity());
//            wekaAnswer.setCapabilities(cw.getCapabilities());
            wekaAnswer.setCutoff(cw.getCutoff());
            wekaAnswer.setOptions(options);
            wekaAnswer.setRevision(cw.getRevision());
            wekaAnswer.setGraph(cw.graph());
            wekaAnswer.setGraphType(cw.graphType());
        } catch(Exception e) {
            log(e.getMessage());
            correct = false;
        }

        if(correct) log("OK");
    }

    /**
     * Uruchamia algorytm FarthestFirst.
     */
    private void runFarthestFirst(){
        FarthestFirst ff = new FarthestFirst();

        try {
            try {
                ff.setOptions(options);
            } catch(Exception e) {
                log("Niepoprawny obiekt Options.");
                log(e.getMessage());
                correct = false;
            }
            try {
                ff.buildClusterer(data);
            } catch(Exception e) {
                log("Niepoprawny obiekt z danymi.");
                log(e.getMessage());
                correct = false;
            }
            //rozpoczęcie budowania obiektu z danymi
            wekaAnswer.setNumClusters(ff.getNumClusters());
            wekaAnswer.setOptions(options);
            wekaAnswer.setRevision(ff.getRevision());
            wekaAnswer.setNumberOfClusters(ff.numberOfClusters());
        } catch(Exception e) {
            log(e.getMessage());
            correct = false;
        }

        if(correct) log("OK");
    }

    /**
     * Metoda zapisująca w logu informacje o błędach lub poprawności wykonania algorytmu.
     * Log jest następnie dostępny w obiekcie WekaAnswer przy użyciu metody getInfo().
     * @param msg Wiadomość, która ma zostać zapisana w logu.
     */
    private void log(String msg) {
        info += "\n[INFO] " + wekaAnswer.getAlgorithmName() + ": " + msg;
    }
}
