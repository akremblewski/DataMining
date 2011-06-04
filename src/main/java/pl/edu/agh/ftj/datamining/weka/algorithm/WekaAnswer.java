package pl.edu.agh.ftj.datamining.weka.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

/**
 * Klasa obiektu przechowującego dane wyprodukowane przez algorytmy Weki.
 * Obiekt ten będzie zwracany do silnika.
 * @author Bartłomiej Wojas, Adrian Kremblewski, Szymon Skupień
 * @version 0.9.4
 */
public class WekaAnswer {
    /**
     * Typ algorytmu jaki ma zostać użyty. Dostępne opcje: 1 - SimpleKMeans, 2 - EM, 3 - HierarchicalClusterer, 4 - Cobweb, 5 - FarthestFirst.
     */
    private int algorithmType = -1;

    /**
     * Informacja o błędach lub o poprawności wykonanego algorytmu. Jeżeli jest ok w info znajdzie się string 'ok'
     * jeżeli będą błędy, tutaj znajdzie się wiadomość o napotkanym błędzie. Reszta pól będzie wtedy pusta.
     */
    private String info = null;

    /**
     * Przechowuje informację o tym, czy obiekt WekaAnswer został poprawnie utworzony (wartość true).
     * Jeśli wystąpił błąd (wartość false) wtedy wszystkie pola klasy będą puste.
     */
    private boolean correct = true;

    /**
     * Nazwa użytego algorytmu.
     */
    private String algorithmName = null;

    /**
     * Tablica indeksów pozwalających powiązać środki klastrów z poszczególnymi instancjami.
     */
    private int[] assignments = null;

//    /**
//     * Standardowe możliwości jakie posiada wybrany typ algorytmu.
//     */
//    private Capabilities capabilities = null;

    /**
     * Zbiór instancji będących środkami wszystkich wyznaczonych klastrów.
     */
    private String clusterCentroids = null;

    /**
     * Liczba częstotliwości występowania wartości dla poszczególnych atrybutów.
     */
    private int[][][] clusterNominalCounts = null;

    /**
     * Tablica z liczbami instancji w klastrach.
     */
    private int[] clusterSizes = null;

    /**
     * Odchylenia standardowe atrybutów numerycznych w klastrach.
     */
    private String clusterStandardDevs = null;

    /**
     * Przechowuje dane instancji dla obiektu funkcji dystansu.
     */
    private String instancesForDistanceFunction = null;

    /**
     * Przechowuje atrybuty dla obiektu funkcji dystansu.
     */
    private String attributeIndicesForDistanceFunction = null;

    /**
     * Przechowuje informację dla obiektu funkcji dystansu dotyczącą indeksów atrybutów.
     */
    private boolean invertSelectionForDistanceFunction = false;

    /**
     * Przechowuje opcje dla obiektu funkcji dystnasu.
     */
    private String[] optionsForDistanseFunction = null;

    /**
     * Maksymalna liczba iteracji.
     */
    private int maxIterations = -1;

    /**
     * Liczba klastrów do wygenerowania.
     */
    private int numClusters = -1;

    /**
     * Opcje wg. kt�rych dzia�a algorytm.
     */
    private String[] options = null;

    /**
     * �ancuch z rewizja
     */
    private String revision = null;

    /**
     * Błąd kwadratowy. NaN jeśli jest używana szybka kalkulacja dystansów.
     */
    private double squaredError = -1.;

    /**
     * Liczba klastrów.
     */
    private int numberOfClusters = -1;

    /**
     * Poprzedniki[priors](?) klastrów.
     */
    private double[] clusterPriors = null;

    /**
     * Rozkłady normalne dla modeli klastra.
     */
    private double[][][] clusterModelsNumericAtts = null;

    /**
     * Minimalne dopuszczalne odchylenie standardowe.
     */
    private double minStdDev = -1;

    /**
     *
     */
    private double acuity = -1.;

    /**
     *
     */
    private double cutoff = -1.;

    /**
     *
     */
    private String graph = null;

    /**
     *
     */
    private int graphType = -1;

//    /**
//     *
//     */
//    private SelectedTag linkType = null;

    /**
     *
     */
    private boolean printNewick = false;

    ////////////////////////////////////////////////////////////////////////////

    /**
    * Zwraca tablicę indeksów pozwalających powiązać środki klastrów z poszczególnymi instancjami.
    * @return Tablica indeksów.
    */
    public int[] getAssignments() {
        return assignments;
    }

    /**
     * Ustawia tablicę indeksów pozwalających powiązać środki klastrów z poszczególnymi instancjami.
     * @param assignments Tablica indeksów.
     */
    public void setAssignments(int[] assignments) {
        this.assignments = assignments;
    }

//    /**
//     * Zwraca obiekt z możliwościami jakie posiada użyty typ algorytmu.
//     * @return Obiekt możliwości.
//     */
//    public Capabilities getCapabilities() {
//        return capabilities;
//    }

//    /**
//     * Ustawia możliwości jakie posiadał użyty typ algorytmu.
//     * @param capabilities Obiekt z możliwościami użytego algorytmu.
//     */
//    public void setCapabilities(Capabilities capabilities) {
//        this.capabilities = capabilities;
//    }

    /**
    * Oblicza i zwraca środki wszystkich znalezionych klastrów w postaci zbioru instacji.
    * @return Zbiór instancji bądących środkami wszystkich wyznaczonych klastrów.
    */
    public Instances getClusterCentroids() {
        if(clusterCentroids != null) {
            BufferedReader reader = new BufferedReader(new StringReader(clusterCentroids));
            ArffReader arff = null;
            try {
                arff = new ArffReader(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return arff.getData();
        } else {
            return null;
        }
    }

    /**
     * Ustawia obiekt ze środkami klastrów.
     * @param clusterCentroids Instacje będące środkami klastrów.
     */
    public void setClusterCentroids(Instances clusterCentroids) {
        if(clusterCentroids != null)
            this.clusterCentroids = clusterCentroids.toString();
    }

    /**
    * Dla każdego klastra zwraca liczbę częstotliwości występowania wartości dla poszczególnych atrybutów.
    * @return Liczby częstotliwości.
    */
    public int[][][] getClusterNominalCounts() {
        return clusterNominalCounts;
    }

    /**
     * Ustawia liczbę częstotliwości występowania wartości dla poszczególnych atrybutów.
     * @param clusterNominalCounts Liczby częstotliwości.
     */
    public void setClusterNominalCounts(int[][][] clusterNominalCounts) {
        this.clusterNominalCounts = clusterNominalCounts;
    }

    /**
    * Zwraca tablicę, której elementy to liczby instancji w każdym z klastrów.
    * @return Tablica z liczbami instancji w klastrach.
    */
    public int[] getClusterSizes() {
        return clusterSizes;
    }

    /**
     * Ustawia tablicę, której elementy to liczby instancji w każdym z klastrów.
     * @param clusterSizes tablica z liczbami instancji.
     */
    public void setClusterSizes(int[] clusterSizes) {
        this.clusterSizes = clusterSizes;
    }

    /**
    * Zwraca odchylenia standardowe atrybutów numerycznych w każdym klastrze.
    * @return Odchylenia standardowe atrybutów numerycznych w klastrach
    */
    public Instances getClusterStandardDevs() {
        if(clusterStandardDevs != null) {
            BufferedReader reader = new BufferedReader(new StringReader(clusterStandardDevs));
            ArffReader arff = null;
            try {
                arff = new ArffReader(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return arff.getData();
        } else {
            return null;
        }
    }

    /**
     * Ustawia odchylenia standardowe atrybutów numerycznych w każdym klastrze.
     * @param clusterStandardDevs Odchylenia standardowe atrybutów numerycznych w klastrach.
     */
    public void setClusterStandardDevs(Instances clusterStandardDevs) {
        if(clusterStandardDevs != null)
            this.clusterStandardDevs = clusterStandardDevs.toString();
    }

    /**
    * Pobiera funkcję odleglości, która jest aktualnie w użyciu.
    * @return Obiekt zawierający m.in. funkcje dystansu, wszystkie instancje, a także pozwalający na obliczenie odleglości między poszczególnymi instancjami.
    */
    public DistanceFunction getDistanceFunction() {
        DistanceFunction d = new EuclideanDistance();
        d.setAttributeIndices(attributeIndicesForDistanceFunction);
        BufferedReader reader = new BufferedReader(new StringReader(instancesForDistanceFunction));
        ArffReader arff = null;
        try {
            arff = new ArffReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        d.setInstances(arff.getData());
        d.setInvertSelection(invertSelectionForDistanceFunction);
        return d;
    }

    /**
     * Ustawia własną funkcję obliczającą odległość między klastrami.
     * @param distanceFunction Obiekt z funkcją dystansu.
     */
    public void setDistanceFunction(DistanceFunction distanceFunction) {
        this.instancesForDistanceFunction = distanceFunction.getInstances().toString();
        this.attributeIndicesForDistanceFunction = distanceFunction.getAttributeIndices();
        this.invertSelectionForDistanceFunction = distanceFunction.getInvertSelection();
        this.optionsForDistanseFunction = distanceFunction.getOptions().clone();

       // this.distanceFunction = distanceFunction;
    }

    /**
    * Zwraca maksymalną liczbę iteracji jakie mogą być wykonane.
    * @return Maksymalna liczba iteracji.
    */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Ustawia maksymalną liczbę iteracji jakie mogą być wykonane.
     * @param maxIterations Maksymalna liczba iteracji.
     */
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
    * Zwraca liczbę klastrów do wygenerowania.
    * @return Liczba klastrów do wygenerowania.
    */
    public int getNumClusters() {
        return numClusters;
    }

    /**
     * Ustawia liczbę klastrów do wygenerowania.
     * @param numClusters Liczba klastrów do wygenerowania.
     */
    public void setNumClusters(int numClusters) {
        this.numClusters = numClusters;
    }

    /**
     * Zwraca tablicę opcji, wg. których funkcjonował algorytm.
     * @return Tablica opcji algorytmu.
     */
    public String[] getOptions() {
        return options;
    }

    /**
     * Ustawia tablicę opcji, wg. których funkcjonował algorytm.
     * @param options Tablica opcji algorytmu.
     */
    public void setOptions(String[] options) {
        this.options = options;
    }

    /**
    * Zwraca łancuch z rewizją.
    * @return Rewizja.
    */
    public String getRevision() {
        return revision;
    }

    /**
     * Ustawia łańcuch z rewizją.
     * @param revision
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * Zwraca typ algorytmu jaki został użyty.
     * Dostępne opcje: 1 - SimpleKMeans, 2 - EM, 3 - HierarchicalClusterer, 4 - Cobweb, 5 - FarthestFirst.
     * @return Typ algorytmu.
     */
    public int getAlgorithmType() {
        return algorithmType;
    }

    /**
     * Ustawia typ algorytmu jaki został użyty.
     * @param algorithmType Typ algorytmu.
     */
    public void setAlgorithmType(int algorithmType) {
        this.algorithmType = algorithmType;
    }

    /**
    * Zwraca liczbe wyznaczonych klastrów.
    * @return Liczba klastrów.
    */
    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    /**
     * Ustawia liczbę wyznaczonych klastrów.
     * @param numberOfClusters Liczba klastrów.
     */
    public void setNumberOfClusters(int numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
    }

    /**
    * Zwraca błąd kwadratowy dla wszystkich klastrów.
    * @return Błąd kwadratowy. NaN jeśli jest używana szybka kalkulacja dystansów.
    */
    public double getSquaredError() {
        return squaredError;
    }

    /**
     * Ustawia błąd kwadratowy dla wszystkich klastrów.
     * @param squaredError Błąd kwadratowy dla wszystkich klastrów.
     */
    public void setSquaredError(double squaredError) {
        this.squaredError = squaredError;
    }

    /**
     * Zwraca rozkłady normalne dla modeli klastra.
     * @return Rozkłady normalne dla modeli klastra.
     */
    public double[][][] getClusterModelsNumericAtts() {
        return clusterModelsNumericAtts;
    }

    /**
     * Ustawia rozkłady normalne dla modeli klastra.
     * @param clusterModelsNumericAtts Rozkłady normalne dla modeli klastra.
     */
    public void setClusterModelsNumericAtts(double[][][] clusterModelsNumericAtts) {
        this.clusterModelsNumericAtts = clusterModelsNumericAtts;
    }

    /**
     * Zwraca poprzedniki[priors](?) klastra.
     * @return Poprzedniki klastra.
     */
    public double[] getClusterPriors() {
        return clusterPriors;
    }

    /**
     * poprzedniki[priors](?) klastra.
     * @param clusterPriors Poprzedniki klastra.
     */
    public void setClusterPriors(double[] clusterPriors) {
        this.clusterPriors = clusterPriors;
    }

    /**
     * Zwraca minimalne dopuszczalne odchylenie standardowe.
     * @return  Minimalne dopuszczalne odchylenie standardowe.
     */
    public double getMinStdDev() {
        return minStdDev;
    }

    /**
     * Ustawia minimalne dopuszczalne odchylenie standardowe.
     * @param minStdDev Minimalne dopuszczalne odchylenie standardowe.
     */
    public void setMinStdDev(double minStdDev) {
        this.minStdDev = minStdDev;
    }

    /**
     * Zwraca �a�cuch z informacj� o typie algorytmu i jego nazwie.
     * @return lancuch z informacja o algorytmie.
     */
    @Override
    public String toString() {
        return "Algorithm: " + algorithmType + "; Name: " + getAlgorithmName();
    }

    /**
     * Ustawia nazwę algorytmu.
     * @param algorithmName Nazwa algorytmu.
     */
    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

/* --- Cobweb, HierarchicalClusterer --- */

    /**
     * @return the acuity
     */
    public double getAcuity() {
        return acuity;
    }

    /**
     * @param acuity the acuity to set
     */
    public void setAcuity(double acuity) {
        this.acuity = acuity;
    }

    /**
     * @return the cutoff
     */
    public double getCutoff() {
        return cutoff;
    }

    /**
     * @param cutoff the cutoff to set
     */
    public void setCutoff(double cutoff) {
        this.cutoff = cutoff;
    }

    /**
     * @return the graph
     */
    public String getGraph() {
        return graph;
    }

    /**
     * @param graph the graph to set
     */
    public void setGraph(String graph) {
        this.graph = graph;
    }

    /**
     * @return the graphType
     */
    public int getGraphType() {
        return graphType;
    }

    /**
     * @param graphType the graphType to set
     */
    public void setGraphType(int graphType) {
        this.graphType = graphType;
    }

//    /**
//     * @return the linkType
//     */
//    public SelectedTag getLinkType() {
//        return linkType;
//    }
//
//    /**
//     * @param linkType the linkType to set
//     */
//    public void setLinkType(SelectedTag linkType) {
//        this.linkType = linkType;
//    }

    /**
     * @return the printNewick
     */
    public boolean isPrintNewick() {
        return printNewick;
    }

    /**
     * @param printNewick the printNewick to set
     */
    public void setPrintNewick(boolean printNewick) {
        this.printNewick = printNewick;
    }

    /**
     * @return the algorithmName
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * Informacja o błędach lub o poprawności wykonanego algorytmu. Jeżeli jest ok w info znajdzie się string 'ok'
     * jeżeli będą błędy, tutaj znajdzie się wiadomość o napotkanym błędzie. Reszta pól będzie wtedy pusta.
     */
    public String getInfo() {
        if(info == null) {
            info = "\n==== WekaAnswer informations ====\n";
        }
        return info;
    }

    /**
     * Informacja o błędach lub o poprawności wykonanego algorytmu. Jeżeli jest ok w info znajdzie się string 'ok'
     * jeżeli będą błędy, tutaj znajdzie się wiadomość o napotkanym błędzie. Reszta pól będzie wtedy pusta.
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Zwraca informację o poprawności obiektu WekaAnswer.
     * @return TRUE - jeśli obiekt został utworzony poprawnie. FALSE - w przeciwnym przypadku.
     */
    public boolean isCorrect() {
        return correct;
    }

    /**
     * Ustawia parametr informujący o poprawności obiektu.
     * @param value TRUE - jeśli obiekt poprawny, FALSE - w przeciwnym przypadku.
     */
    public void setCorrect(boolean value) {
        correct = value;
    }
}