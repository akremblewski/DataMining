package wekatest;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Cobweb;
import weka.clusterers.EM;
import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.FarthestFirst;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Klasa odpowiedzialna za komunikacje z biblioteka Weki
 * @author Bartłomiej Wojas, Adrian Kremblewski, Szymon Skupień
 * @version 1.0.0
 */
public class WekaAlgorithm {
    /**
     * Obiekt przechowujący dane do analizy.
     */
    private Instances data = null;
    /**
     * Typ algorytmu jaki ma zostac uzyty. Dostepne opcje: 1 - SimpleKMeans, 2 - EM, 3 - HierarchicalClusterer, 4 - Cobweb, 5 - FarthestFirst
     */
    private int algorithmType = 0;
    /**
     * Tablica parametrów wg. których ma funkcjonować algorytm.
     */
    private String[] options = null;
    /**
     * Obiekt zawierajace dane zwracajane przez Weke
     */
    private WekaAnswer wekaAnswer = new WekaAnswer();
    /**
     * Obiekt przechowujący informacje dotyczące wykonania algorytmów.
     */
    private String info = "\n==== WekaAnswer informations ====\n";
    /**
     * Przechowuje informację o tym czy podczas wykonywania algorytmów
     * doszło do błędu (false) czy też nie (true);
     */
    private boolean correct = true;
    /**
     * Domyślne opcje dla algorytmu SimpleKMeans
     */
    private String[] skmDefault = {"-O"};
    /**
     * Domyślne opcje dla algorytmu EM.
     */
    private String[] emDefault = {""};
    /**
     * Domyślne opcje dla algorytmuHierarchicalClusterer.
     */
    private String[] hcDefault = {""};
    /**
     * Domyślne opcje dla algorytmu CobWeb.
     */
    private String[] cwDefault = {""};
    /**
     * Domyślne opcje dla algorytmu FarthestFirst.
     */
    private String[] ffDefault = {""};
    /**
     * Tablica z nazwami udostępnianych algorytmów
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
        switch (algorithmType) {
            case 1:
                wekaAnswer.setAlgorithmName(algorithms[0]);
                wekaAnswer.setAlgorithmType(1);
                runSimpleKMeans();
                break;
            case 2:
                wekaAnswer.setAlgorithmName(algorithms[1]);
                wekaAnswer.setAlgorithmType(2);
                runEM();
                break;
            case 3:
                wekaAnswer.setAlgorithmName(algorithms[2]);
                wekaAnswer.setAlgorithmType(3);
                runHierarchicalClusterer();
                break;
            case 4:
                wekaAnswer.setAlgorithmName(algorithms[3]);
                wekaAnswer.setAlgorithmType(4);
                runCobweb();
                break;
            case 5:
                wekaAnswer.setAlgorithmName(algorithms[4]);
                wekaAnswer.setAlgorithmType(5);
                runFarthestFirst();
                break;
            default:
                wekaAnswer.setAlgorithmName("Unknown");
                log("Nieznany typ algorytmu.");
        }

        if (correct == false) {
            wekaAnswer = new WekaAnswer();
            wekaAnswer.setCorrect(correct);
        }
        wekaAnswer.setInfo(info);
    }

    /**
     * Metoda ustawiająca typ algorytmu jaki ma zostać użyty
     * @param algorithmType Typ algorytmu jaki ma zostac uzyty. Dostepne opcje: 1 - SimpleKMeans, 2 - EM, 3 - HierarchicalClusterer, 4 - Cobweb.
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
        wekaAnswer.setData(data);
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
        ClusterEvaluation eval = new ClusterEvaluation();

        try {
            try {
                if (options == null) {
                    throw new Exception("Options == null");
                }
                skm.setOptions(options);
            } catch (Exception e) {
                log("Niepoprawny obiekt Options.");
                log(e.getMessage());
                log("Algorytm zostanie uruchomiony z domyslnymi opcjami.");
                skm.setOptions(skmDefault);
            }
            try {
                skm.buildClusterer(data);
                eval.setClusterer(skm);
                eval.evaluateClusterer(data);
            } catch (Exception e) {
                log("Niepoprawny obiekt z danymi.");
                log(e.getMessage());
                correct = false;
            }
            //rozpoczęcie budowania obiektu z danymi
            try {
                wekaAnswer.setAssignments(skm.getAssignments());
            } catch(Exception e) {
                log(e.getMessage());
            }
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
            wekaAnswer.setClusterer(skm);
            wekaAnswer.setEval(eval);
        } catch (Exception e) {
            log(e.getMessage());
            correct = false;
        }

        if (correct) {
            log("OK");
        }
    }

    /**
     * Uruchamia algorytm EM.
     */
    private void runEM() {
        EM em = new EM();
        ClusterEvaluation eval = new ClusterEvaluation();

        try {
            try {
                if (options == null) {
                    throw new Exception("Options == null");
                }
                em.setOptions(options);
            } catch (Exception e) {
                log("Niepoprawny obiekt Options.");
                log(e.getMessage());
                log("Algorytm zostanie uruchomiony z domyslnymi opcjami.");
                em.setOptions(emDefault);
            }
            try {
                em.buildClusterer(data);
                eval.setClusterer(em);
                eval.evaluateClusterer(data);
            } catch (Exception e) {
                log("Niepoprawny obiekt z danymi.");
                log(e.getMessage());
                correct = false;
            }
            //rozpoczęcie budowania obiektu z danymi
            wekaAnswer.setClusterPriors(em.getClusterPriors());
            wekaAnswer.setClusterModelsNumericAtts(em.getClusterModelsNumericAtts());
            wekaAnswer.setMinStdDev(em.getMinStdDev());
            // wekaAnswer.setCapabilities(em.getCapabilities());
            wekaAnswer.setMaxIterations(em.getMaxIterations());
            wekaAnswer.setNumClusters(em.getNumClusters());
            wekaAnswer.setNumberOfClusters(em.getNumClusters());
            wekaAnswer.setOptions(options);
            wekaAnswer.setRevision(em.getRevision());
            wekaAnswer.setClusterer(em);
            wekaAnswer.setEval(eval);
        } catch (Exception e) {
            log(e.getMessage());
            correct = false;
        }

        if (correct) {
            log("OK");
        }
    }

    /**
     * Uruchamia algorytm HierarchicalClusterer.
     */
    private void runHierarchicalClusterer() {
        HierarchicalClusterer hc = new HierarchicalClusterer();
        ClusterEvaluation eval = new ClusterEvaluation();

        try {
            try {
                if (options == null) {
                    throw new Exception("Options == null");
                }
                hc.setOptions(options);
            } catch (Exception e) {
                log("Niepoprawny obiekt Options.");
                log(e.getMessage());
                log("Algorytm zostanie uruchomiony z domyslnymi opcjami.");
                hc.setOptions(hcDefault);
            }
            try {
                hc.buildClusterer(data);
                eval.setClusterer(hc);
                eval.evaluateClusterer(data);
            } catch (Exception e) {
                log("Niepoprawny obiekt z danymi.");
                log(e.getMessage());
                correct = false;
            }
            //rozpoczęcie budowania obiektu z danymi
            // wekaAnswer.setCapabilities(hc.getCapabilities());
            wekaAnswer.setDistanceFunction(hc.getDistanceFunction());
            // wekaAnswer.setLinkType(hc.getLinkType());
            wekaAnswer.setNumClusters(hc.getNumClusters());
            wekaAnswer.setNumberOfClusters(hc.getNumClusters());
            wekaAnswer.setOptions(options);
            wekaAnswer.setPrintNewick(hc.getPrintNewick());
            wekaAnswer.setRevision(hc.getRevision());
            wekaAnswer.setGraph(hc.graph());
            wekaAnswer.setGraphType(hc.graphType());
            wekaAnswer.setClusterer(hc);
            wekaAnswer.setEval(eval);
        } catch (Exception e) {
            log(e.getMessage());
            correct = false;
        }

        if (correct) {
            log("OK");
        }
    }

    /**
     * Uruchamia algorytm Cobweb.
     */
    private void runCobweb() {
        Cobweb cw = new Cobweb();
        ClusterEvaluation eval = new ClusterEvaluation();

        try {
            try {
                if (options == null) {
                    throw new Exception("Options == null");
                }
                cw.setOptions(options);
            } catch (Exception e) {
                log("Niepoprawny obiekt Options.");
                log(e.getMessage());
                log("Algorytm zostanie uruchomiony z domyslnymi opcjami.");
                cw.setOptions(cwDefault);
            }
            try {
                cw.buildClusterer(data);
                for (Instance current : data)
                    cw.updateClusterer(current);
                cw.updateFinished();
                eval.setClusterer(cw);
                eval.evaluateClusterer(data);
            } catch (Exception e) {
                log("Niepoprawny obiekt z danymi.");
                log(e.getMessage());
                correct = false;
            }
            //rozpoczęcie budowania obiektu z danymi
            wekaAnswer.setAcuity(cw.getAcuity());
            // wekaAnswer.setCapabilities(cw.getCapabilities());
            wekaAnswer.setCutoff(cw.getCutoff());
            wekaAnswer.setOptions(options);
            wekaAnswer.setRevision(cw.getRevision());
            wekaAnswer.setGraph(cw.graph());
            wekaAnswer.setGraphType(cw.graphType());
            wekaAnswer.setClusterer(cw);
            wekaAnswer.setEval(eval);
            wekaAnswer.setNumClusters(cw.numberOfClusters());
            wekaAnswer.setNumberOfClusters(cw.numberOfClusters());
        } catch (Exception e) {
            log(e.getMessage());
            correct = false;
        }

        if (correct) {
            log("OK");
        }
    }

    /**
     * Uruchamia algorytm FarthestFirst.
     */
    private void runFarthestFirst() {
        FarthestFirst ff = new FarthestFirst();
        ClusterEvaluation eval = new ClusterEvaluation();

        try {
            try {
                if (options == null) {
                    throw new Exception("Options == null");
                }
                ff.setOptions(options);
            } catch (Exception e) {
                log("Niepoprawny obiekt Options.");
                log(e.getMessage());
                log("Algorytm zostanie uruchomiony z domyslnymi opcjami.");
                ff.setOptions(ffDefault);
            }
            try {
                ff.buildClusterer(data);
                eval.setClusterer(ff);
                eval.evaluateClusterer(data);
            } catch (Exception e) {
                log("Niepoprawny obiekt z danymi.");
                log(e.getMessage());
                correct = false;
            }
            //rozpoczęcie budowania obiektu z danymi
            wekaAnswer.setNumClusters(ff.getNumClusters());
            wekaAnswer.setOptions(options);
            wekaAnswer.setRevision(ff.getRevision());
            wekaAnswer.setNumClusters(ff.numberOfClusters());
            wekaAnswer.setNumberOfClusters(ff.numberOfClusters());
            wekaAnswer.setClusterer(ff);
            wekaAnswer.setEval(eval);
        } catch (Exception e) {
            log(e.getMessage());
            correct = false;
        }

        if (correct) {
            log("OK");
        }
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
