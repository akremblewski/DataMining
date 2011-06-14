package wekatest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Attribute;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

/**
 * Klasa obiektu przechowującego dane wyprodukowane przez algorytmy Weki.
 * Obiekt ten będzie zwracany do silnika.
 * @author Bartłomiej Wojas, Adrian Kremblewski, Szymon Skupień
 * @version 0.9.8

 */
public class WekaAnswer implements Serializable {
    /**
     * Typ algorytmu jaki ma zostac uzyty. Dostepne opcje: 1 - SimpleKMeans, 2 - EM, 3 - HierarchicalClusterer, 4 - Cobweb, 5 - FarthestFirst.
     */
    private int algorithmType = -1;

    /**
     * Informacja o błędach lub o poprawności wykonanego algorytmu. Jeżeli jest ok w info znajdzie się string 'ok'
     * jeżeli będą błędy, tutaj znajdzie się wiadomość o napotkanym błędzie. Reszta pól będzie wtedy pusta.
     */
    private String info;

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
     * Odchylenia standardowe atrybutow numerycznych w klastrach.
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
     * Liczba klastrow do wygenerowania.
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
     * Blad kwadratowy. NaN jesli jest uzywana szybka kalkulacja dystansow.
     */
    private double squaredError = -1.;

    /**
     * Liczba klastrów.
     */
    private int numberOfClusters = -1;

    /**
     * Poprzedniki[priors](?) klastrów
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

    /**
     *
     */
    private boolean printNewick = false;

    /**
     * Obiekt klasteryzatora.
     */
    private Clusterer clusterer = null;

    /**
     * Obiekt ewaluacji modelu.
     */
    private ClusterEvaluation eval = null;

    /**
     * Obiekt z instancjami do analizy.
     */
    private Instances data = null;

    ////////////////////////////////////////////////////////////////////////////

    /**
    * Zwraca tablice indeksow pozwalajacych powiazac srodki klastrow z poszczegolnymi instancjami.
    * @return Tablica indeksów.
    */
    public int[] getAssignments() {
        return assignments;
    }

    /**
     * Ustawia tablice indeksow pozwalajacych powiazac srodki klastrow z poszczegolnymi instancjami.
     * @param assignments Tablica indeksów.
     */
    public void setAssignments(int[] assignments) {
        this.assignments = assignments;
    }

    /**
    * Oblicza i zwraca srodki wszystkich znalezionych klastrow w postaci zbioru instacji.
    * @return Zbior instancji bedacych srodkami wszystkich wyznaczonych klastrow.
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
    * Dla kazdego klastra zwraca liczbe czestotliwosci wystepowania wartosci dla poszczegolnych atrybutow.
    * @return Liczby czestotliwosci.
    */
    public int[][][] getClusterNominalCounts() {
        return clusterNominalCounts;
    }

    /**
     * Ustawia liczbe czestotliwosci wystepowania wartosci dla poszczegolnych atrybutow.
     * @param clusterNominalCounts Liczby czestotliwosci.
     */
    public void setClusterNominalCounts(int[][][] clusterNominalCounts) {
        this.clusterNominalCounts = clusterNominalCounts;
    }

    /**
    * Zwraca tablice, ktorej elementy to liczby instancji w kazdym z klastrow.
    * @return Tablica z liczbami instancji w klastrach.
    */
    public int[] getClusterSizes() {
        return clusterSizes;
    }

    /**
     * Ustawia tablice, ktorej elementy to liczby instancji w kazdym z klastrow.
     * @param clusterSizes tablica z liczbami instancji.
     */
    public void setClusterSizes(int[] clusterSizes) {
        this.clusterSizes = clusterSizes;
    }

    /**
    * Zwraca odchylenia standardowe atrybutow numerycznych w kazdym klastrze.
    * @return Odchylenia standardowe atrybutow numerycznych w klastrach
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
     * Ustawia odchylenia standardowe atrybutow numerycznych w kazdym klastrze.
     * @param clusterStandardDevs Odchylenia standardowe atrybutow numerycznych w klastrach.
     */
    public void setClusterStandardDevs(Instances clusterStandardDevs) {
        if(clusterStandardDevs != null)
            this.clusterStandardDevs = clusterStandardDevs.toString();
    }

    /**
    * Pobiera funkcje odleglosci, ktora jest aktualnie w uzyciu.
    * @return Obiekt zawierajacy m.in. funkcje dystansu, wszystkie instancje, a takze pozwalajacy na obliczenie odleglosci miedzy poszczegolnymi instancjami.
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
    * Zwraca maksymalna liczbe interacji jakie moga byc wykonane.
    * @return Maksymalna liczba iteracji.
    */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Ustawia maksymalna liczbe interacji jakie moga byc wykonane.
     * @param maxIterations Maksymalna liczba iteracji.
     */
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
    * Zwraca liczbę klastrów do wygenerowania.
    * @return Liczba klastrow do wygenerowania.
    */
    public int getNumClusters() throws Exception {
        return clusterer.numberOfClusters();
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
    * Zwraca lancuch z rewizja.
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
     * Zwraca typ algorytmu jaki zostal uzyty.
     * Dostepne opcje: 1 - SimpleKMeans, 2 - EM, 3 - HierarchicalClusterer, 4 - Cobweb.
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
    * Zwraca liczbe wyznaczonych klastrow.
    * @return Liczba klastrow.
    */
    public int getNumberOfClusters() throws Exception {
        return clusterer.numberOfClusters();
    }

    /**
     * Ustawia liczbę wyznaczonych klastrów.
     * @param numberOfClusters Liczba klastrów.
     */
    public void setNumberOfClusters(int numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
    }

    /**
    * Zwraca blad kwadratowy dla wszystkich klastrow.
    * @return Blad kwadratowy. NaN jesli jest uzywana szybka kalkulacja dystansow.
    */
    public double getSquaredError() {
        return squaredError;
    }

    /**
     * Ustawia blad kwadratowy dla wszystkich klastrow.
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

    /**
     * Zwraca obiekt klasteryzatora.
     * @return Obiekt klasteryzatora.
     */
    public Clusterer getClusterer() {
        return clusterer;
    }

    /**
     * Ustawia obiekt klasteryzatora.
     * @param clusterer Obiekt klasteryzatora
     */
    public void setClusterer(Clusterer clusterer) {
        this.clusterer = clusterer;
    }

    /**
     * Zwraca obiekt ewaluacji modelu.
     * @return Obiekt ewaluacji modelu.
     */
    public ClusterEvaluation getEval() {
        return eval;
    }

    /**
     * Ustawia obiekt ewaluacji modelu.
     * @param eval Obiekt ewaluacji modelu.
     */
    public void setEval(ClusterEvaluation eval) {
        this.eval = eval;
    }

    /**
     * Zwraca tylko te instancje, które należą do klastra o podanym numerze.
     * @param numClust Numer klastra
     * @return Instancje należące do podanego klastra.
     */
    public Instances getClusterInstances(int numClust) {
        Instances inst = new Instances(data);
        inst.clear();

        try {
            for (int i = 0; i < data.numInstances(); ++i) {
                if (clusterer.clusterInstance(data.instance(i)) == numClust) {
                    inst.add(data.instance(i));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return inst;
    }

    /**
     * Zwraca obiekt z instancjami, które miały zostać poddane analizie.
     * @return Obiekt z instancjami
     */
    public Instances getData() {
        return data;
    }

    /**
     * Ustawia obiekt z instancjami, które miał zostać poddane analizie.
     * @param data Obiekt z instancjami.
     */
    public void setData(Instances data) {
        this.data = data;
    }

    /**
     * Zwraca listę dostępnych atrybutów.
     * @return Lista dostępnych atrybutów.
     */
    public List<String> getAttributeNames() {
        Enumeration e = data.enumerateAttributes();
        List<String> attributeNames = new ArrayList<String>();

        while (e.hasMoreElements()) {
            attributeNames.add(((Attribute)e.nextElement()).name());
        }

        return attributeNames;
    }

    /**
     * Metoda zwracająca listę wartości dla danej instancji.
     * @param inst Analizowana instancja
     * @param attrX Nazwa atrybutu dla osi X.
     * @param attrY Nazwa atrybutu dla osi Y
     * @return Lista dwuelementowa z wartościami kolejno dla osi X i Y.
     */
    public List<Object> getValueForInstance(Instance inst, String attrX, String attrY) {
        List<Object> value = new ArrayList<Object>();
        Attribute atX = inst.attribute(getAttributeNames().indexOf(attrX));
        Attribute atY = inst.attribute(getAttributeNames().indexOf(attrY));

        if (atX.isNominal()) {
            double val = inst.value(atX);
            value.add(atX.value((int)val));
        } else {
            value.add(inst.value(atX));
        }

        if (atY.isNominal()) {
            double val = inst.value(atY);
            value.add(atY.value((int)val));
        } else {
            value.add(inst.value(atY));
        }

        return value;
    }
}