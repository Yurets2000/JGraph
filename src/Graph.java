import java.util.*;

/**
 * Базова реалізація графа.
 * Реалізований граф використовує матрицю суміжності для
 * опису суміжності вершин, проте також може використовувати
 * список суміжності, для пришвидшення виконання деяких алгоритмів
 */

public abstract class Graph {

    public static class Vertex {
        char label;
        boolean wasVisited;

        public Vertex(char label) {
            this.label = label;
            wasVisited = false;
        }

        public char getLabel() {
            return label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Vertex)) return false;
            Vertex vertex = (Vertex) o;
            return label == vertex.label &&
                    wasVisited == vertex.wasVisited;
        }

        @Override
        public int hashCode() {
            return Objects.hash(label, wasVisited);
        }
    }

    protected final double INFINITY = Double.POSITIVE_INFINITY;
    protected int verticesCount;
    protected int maxVerticesCount;
    protected Vertex[] vertices;
    protected double[][] adjMatrix;
    protected LinkedList<LinkedList<Integer>> adjList;

    /**
     * Створює граф з заданою максимальною кількістю вершин
     * @param maxVerticesCount максимальна кількість вершин
     */

    public Graph(int maxVerticesCount) {
        this.verticesCount = 0;
        this.maxVerticesCount = maxVerticesCount;
        vertices = new Vertex[maxVerticesCount];
        adjMatrix = new double[maxVerticesCount][maxVerticesCount];
    }

    /**
     * Створює граф з заданим списком вершин, матрицею суміжності,
     * максимальною кількістю вершин, та кількістю вершин
     * @param vertices список вершин
     * @param adjMatrix матриця суміжності
     * @param maxVerticesCount максимальна кількість вершин
     * @param nVertices кількість вершин
     */

    public Graph(Vertex[] vertices, double[][] adjMatrix, int maxVerticesCount, int nVertices){
        this(maxVerticesCount);
        System.arraycopy(vertices, 0, this.vertices, 0, vertices.length);
        for (int i = 0; i < adjMatrix.length; i++) {
            System.arraycopy(adjMatrix[i], 0, this.adjMatrix[i], 0, adjMatrix[i].length);
        }
        this.verticesCount = nVertices;
    }

    /**
     * Додає ребро між двома заданими вершинами через список суміжності
     * @param start номер першої вершини
     * @param end номер другої вершини
     */

    protected abstract void addEdgeToList(int start, int end);

    /**
     * Видаляє ребро між двома заданими вершинами через список суміжності
     * @param start номер першої вершини
     * @param end номер другої вершини
     */

    protected abstract void deleteEdgeFromList(int start, int end);

    /**
     * Додає ребро між двома заданими вершинами з заданою вагою через матрицю суміжності
     * @param start номер першої вершини
     * @param end номер другої вершини
     * @param weight вага ребра
     */

    public abstract void addEdge(int start, int end, double weight);

    /**
     * Перевіряє чи граф є напівейлеровим
     * @return логічне {@code true} якщо граф напівейлровий,
     * {@code false} - інакше
     */

    public abstract boolean isSemiEulerian();

    /**
     * Перевіряє чи граф є ейлеровим
     * @return логічне {@code true} якщо граф ейлровий,
     * {@code false} - інакше
     */

    public abstract boolean isEulerian();

    /**
     * Додає вершину з заданою позначкою в граф
     * @param label позначка вершини
     */

    public void addVertex(char label) {
        vertices[verticesCount++] = new Vertex(label);
    }

    /**
     * Видаляє вершину з графа
     * @param index номер вершини
     */

    public void deleteVertex(int index){
        if(index != verticesCount - 1) {
            System.arraycopy(vertices, index + 1, vertices, index, verticesCount - 1 - index);

            for (int i = index; i < verticesCount - 1; i++) {
                System.arraycopy(adjMatrix[index + 1], 0, adjMatrix[index], 0, verticesCount);
            }
            for (int i = 0; i < verticesCount; i++) {
                System.arraycopy(adjMatrix[i], index + 1, adjMatrix[i], index, verticesCount - 1 - index);
            }
        }else{
            Arrays.fill(adjMatrix[index], 0);
            for (int i = 0; i < verticesCount; i++) {
                adjMatrix[i][index] = 0;
            }
        }
        verticesCount--;
    }

    /**
     * Визначає степінь заданої вершини
     * @param v номер вершини
     * @return степінь вершини {@param v}
     */

    public int deg(int v){
        int sum = 0;
        for (int i = 0; i < verticesCount; i++) {
            if(adjMatrix[v][i] != 0) sum++;
        }

        return sum;
    }

    /**
     * Перевіряє чи граф містить петлі
     * @return логічне {@code true} якщо граф містить хоча б одну петлю,
     * {@code false} - інакше
     */

    public boolean containsLoop(){
        for (int i = 0; i < verticesCount; i++) {
            if(adjMatrix[i][i] != 0) return true;
        }

        return false;
    }

    /**
     * Робить обхід графа в глибину
     * @param startIndex номер вершини, з якої починається обхід графа
     * @return список вершин, які були відвідані
     */

    public LinkedList<Integer> dfs(int startIndex) {
        LinkedList<Integer> result = new LinkedList<>();
        subDfs(startIndex, result);
        for (int j = 0; j < verticesCount; j++) {
            vertices[j].wasVisited = false;
        }

        return result;
    }

    private void subDfs(int index, LinkedList<Integer> list) {
        vertices[index].wasVisited = true;
        list.add(index);
        LinkedList<Integer> adjacent = getAdjacentVertices(index);
        for(int v : adjacent){
            if(!vertices[v].wasVisited) {
                subDfs(v, list);
            }
        }
    }

    /**
     * Робить обхід графа в ширину
     * @param startIndex номер вершини, з якої починається обхід графа
     * @return список вершин, які були відвідані
     */

    public LinkedList<Integer> bfs(int startIndex) {
        LinkedList<Integer> result = new LinkedList<>();
        vertices[startIndex].wasVisited = true;
        result.add(startIndex);
        LinkedList<Integer> queue = new LinkedList<>();
        queue.add(startIndex);
        while (!queue.isEmpty()) {
            int v1 = queue.remove();
            LinkedList<Integer> adjacent = getAdjacentVertices(v1);
            for(int v2: adjacent){
                if(!vertices[v2].wasVisited) {
                    vertices[v2].wasVisited = true;
                    result.add(v2);
                    queue.offer(v2);
                }
            }
        }

        for (int j = 0; j < verticesCount; j++) {
            vertices[j].wasVisited = false;
        }

        return result;
    }

    /**
     * Реалізує алгоритм Флойда-Варшала, що знаходить
     * найкоротший шлях між двома вершинами для всіх вершин графа
     * @return матрицю досяжності графа
     */

    public double[][] floydWarshall(){
        int n = verticesCount;
        double[][] w = new double[n][n];
        for(int i = 0; i < n; i++){
            for (int j = 0; j < n; j++) {
                if(adjMatrix[i][j] == 0) w[i][j] = INFINITY;
                else w[i][j] = adjMatrix[i][j];
            }
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    w[i][j] = Math.min(w[i][j], w[i][k] + w[k][j]);
                }
            }
        }

        return w;
    }

    /**
     * Реалізує алгоритм Герхольцера для знаходження
     * ейлерового шляху в графі, якщо такий існує.
     * Якщо граф не містить ейлрового шляху, то результат не передбачуваний,
     * тому варто спочатку перевірити чи містить граф ейлровий шлях методом isSemiEulerian
     * @return список всіх вершин ейлерового шляху, якщо такий існує
     */

    public LinkedList<Integer> getEulerianPath(){
        LinkedList<Integer> path = new LinkedList<>();
        LinkedList<Integer> circuit = new LinkedList<>();
        int currVertex = 0;
        for (int i = 0; i < verticesCount; i++) {
            if(adjList.get(i).size() % 2 == 1){
                currVertex = i;
                break;
            }
        }
        path.push(currVertex);
        while (!path.isEmpty()){
            if(!adjList.get(currVertex).isEmpty()){
                path.push(currVertex);
                int nextVertex = adjList.get(currVertex).getLast();
                deleteEdgeFromList(currVertex, nextVertex);
                currVertex = nextVertex;
            }else{
                circuit.add(currVertex);
                currVertex = path.pop();
            }
        }

        return circuit;
    }

    /**
     * Реалізує підхід пошуку з поверненням для знаходження
     * гамільтонового шляху в графі, якщо такий існує.
     * @return список всіх вершин гамільтонового шляху, якщо такий існує
     * @throws IllegalStateException якщо граф не має гамільтонового шляху
     */

    public LinkedList<Integer> getHamiltonPath(){
        int[] path = new int[verticesCount];
        Arrays.fill(path, -1);
        path[0] = 0;
        if(!subHamilton(path, 1)){
            throw new IllegalStateException("Граф не має гамільтонового шляху");
        }

        LinkedList<Integer> result = new LinkedList<>();
        for (int i1 : path) {
            result.add(i1);
        }

        return result;
    }

    private boolean subHamilton(int[] path, int pos){
        if(pos == verticesCount) return true;
        for (int v = 1; v < verticesCount; v++) {
            if(isSafe(v, path, pos)){
                path[pos] = v;
                if (subHamilton(path, pos + 1)) return true;
                //Якщо додавання вершини v не веде до вирішення, то видаляємо її з path
                path[pos] = -1;
            }
        }

        return false;
    }

    private boolean isSafe(int v, int[] path, int pos){

        if (adjMatrix[path[pos - 1]][v] == 0)
            return false;

        for (int i = 0; i < pos; i++)
            if (path[i] == v)
                return false;

        return true;
    }

    /**
     * Реалізує алгоритм Дейкстри для знаходження найкоротшого шляху
     * між двома вершинами графа
     * @param source номер початкової вершини
     * @param target номер кінцевої вершини
     * @return список всіх вершин найкоротшого шляху
     */

    public LinkedList<Integer> dijkstra(int source, int target) {
        int n = verticesCount;
        double[] d = new double[n];
        int[] prev = new int[n];
        LinkedList<Integer> result = new LinkedList<>();
        HashSet<Integer> nonVisited = new HashSet<>();

        for (int v = 0; v < n; v++) {
            nonVisited.add(v);
            d[v] = INFINITY;
        }
        d[source] = 0;

        while (!nonVisited.isEmpty()) {
            int u = getMinimalVertex(nonVisited, d);
            nonVisited.remove(u);
            if(u == target) break;

            HashSet<Integer> neighbors = getVertexNeighbors(nonVisited, u);
            for(int v: neighbors){
                double alt = d[u] + adjMatrix[u][v];
                if(alt < d[v]){
                    d[v] = alt;
                    prev[v] = u;
                }
            }
        }

        int prevVertex = target;
        while(prevVertex != source){
            result.push(prevVertex);
            prevVertex = prev[prevVertex];
        }
        result.push(source);

        return result;
    }

    private HashSet<Integer> getVertexNeighbors(HashSet<Integer> vertices, int v){
        HashSet<Integer> neighbors = new HashSet<>();
        for (int i = 0; i < verticesCount; i++) {
            if(adjMatrix[v][i] != 0 && vertices.contains(i)){
                neighbors.add(i);
            }
        }

        return neighbors;
    }

    private int getMinimalVertex(HashSet<Integer> vertices, double[] d){
        return vertices.stream().min(Comparator.comparingDouble(a -> d[a])).get();
    }

    /**
     * Заповнює список суміжності графу
     */

    protected final void fillAdjacentList(){
        adjList = new LinkedList<>();
        for (int i = 0; i < verticesCount; i++) {
            LinkedList<Integer> subList = new LinkedList<>();
            for (int j = 0; j < verticesCount; j++) {
                if(adjMatrix[i][j] != 0){
                    subList.add(j);
                }
            }
            adjList.add(subList);
        }
    }

    /**
     * Знаходить всі вершини, суміжні з заданою вершиною
     * @param index номер вершини, для якої знаходяться суміжні вершини
     * @return список суміжних вершин
     */

    protected final LinkedList<Integer> getAdjacentVertices(int index){
        LinkedList<Integer> adjacent = new LinkedList<>();
        for (int i = 0; i < verticesCount; i++) {
            if (adjMatrix[index][i] != 0) {
                adjacent.push(i);
            }
        }

        return adjacent;
    }

    /**
     * Знаходить наступну не відвідану вершину, яка суміжна заданій
     * @param index номер вершини, для якої необхідно знайти
     * наступну суміжну, не відвідану вершину
     * @return вершина, суміжна до заданої, і яка не була відвідана,
     * якщо така існує, та -1 інакше
     */

    protected final int getUnvisitedVertex(int index) {
        for (int i = 0; i < verticesCount; i++) {
            if (adjMatrix[index][i] != 0 && !vertices[i].wasVisited) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Знаходить наступну не відвідану вершину, яка суміжна заданій,
     * і довжина ребра до якої є найменшою
     * @param index номер вершини, для якої необхідно знайти
     * суміжну, не відвідану вершину
     * @return вершина, суміжна до заданої, і яка не була відвідана
     * та є найближчою до заданої вершини, якщо така існує, та -1 інакше
     */

    protected final int getMinimalUnvisitedVertex(int index) {
        double minValue = INFINITY;
        int minIndex = -1;
        for (int i = 1; i < verticesCount; i++) {
            if (adjMatrix[index][i] != 0 && adjMatrix[index][i] < minValue && !vertices[i].wasVisited) {
                minValue = adjMatrix[index][i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    /**
     * Знаходить ізольовану вершину, якщо така існує
     * @return ізольовану вершину, якщо така існує, та -1 інакше
     */

    protected final int noSuccessors(){
        boolean isEdge;

        for (int i = 0; i < verticesCount; i++) {
            isEdge = false;
            for (int j = 0; j < verticesCount; j++) {
                if(adjMatrix[i][j] != 0){
                    isEdge = true;
                    break;
                }
            }
            if(!isEdge){
                return i;
            }
        }

        return -1;
    }

    /**
     * Повертає список вершин графа
     * @return список вершин графа
     */

    public Vertex[] getVertices() {
        return vertices;
    }

    /**
     * Повертає список суміжності графа
     * @return список суміжності графа
     */

    public double[][] getAdjMatrix() {
        return adjMatrix;
    }

    /**
     * Повертає кількість вершин графа
     * @return кількість вершин графа
     */

    public int getVerticesCount() {
        return verticesCount;
    }
}
