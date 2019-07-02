/**
 * Реалізація неорієнтованого графа
 */

public class NonOrderedGraph extends Graph {

    /**
     * Створює неорієнтований граф з заданою максимальною кількістю вершин
     * @param maxVerticesCount максимальна кількість вершин
     */

    public NonOrderedGraph(int maxVerticesCount){
        super(maxVerticesCount);
    }

    /**
     * Створює неорієнтований граф з заданим списком вершин, матрицею суміжності,
     * максимальною кількістю вершин, та кількістю вершин
     * @param vertices список вершин
     * @param adjMatrix матриця суміжності
     * @param maxVerticesCount максимальна кількість вершин
     * @param nVertices кількість вершин
     */

    public NonOrderedGraph(Vertex[] vertices, double[][] adjMatrix, int maxVerticesCount, int nVertices){
        super(vertices, adjMatrix, maxVerticesCount, nVertices);
    }

    @Override
    protected void addEdgeToList(int start, int end) {
        adjList.get(start).add(end);
        adjList.get(end).add(start);
    }

    @Override
    protected void deleteEdgeFromList(int start, int end) {
        adjList.get(start).remove((Integer) end);
        adjList.get(end).remove((Integer) start);
    }

    @Override
    public void addEdge(int start, int end, double weight) {
        adjMatrix[start][end] = weight;
        adjMatrix[end][start] = weight;
    }

    /**
     * Створює остовне дерево з мінімальним добутком ваг всіх ребер
     * @return граф, який являє собою остовне дерево з
     * мінімальним добутком ваг всіх ребер
     */

    public NonOrderedGraph minimalProductTree(){
        NonOrderedGraph copy = new NonOrderedGraph(vertices, adjMatrix, maxVerticesCount, verticesCount);
        for (int i = 0; i < verticesCount; i++) {
            for (int j = 0; j < verticesCount; j++) {
                if(adjMatrix[i][j] != 0){
                    copy.adjMatrix[i][j] = Math.log(adjMatrix[i][j]);
                }
            }
        }
        NonOrderedGraph result = copy.minimalSpanningTree();
        for (int i = 0; i < verticesCount; i++) {
            for (int j = 0; j < verticesCount; j++) {
                if(result.adjMatrix[i][j] != 0){
                    result.adjMatrix[i][j] = Math.exp(result.adjMatrix[i][j]);
                }
            }
        }

        return result;
    }

    /**
     * Створює остовне дерево з мінімальною сумою ваг всіх ребер
     * @return граф, який являє собою остовне дерево
     * з мінімальною сумою ваг всіх ребер
     */

    public NonOrderedGraph minimalSpanningTree(){
        NonOrderedGraph g = new NonOrderedGraph(maxVerticesCount);
        minimalSpanningTree(0, g);

        for (int j = 0; j < verticesCount; j++) {
            vertices[j].wasVisited = false;
        }

        return g;
    }

    private void minimalSpanningTree(int index, Graph g) {
        vertices[index].wasVisited = true;
        g.addVertex(vertices[index].label);
        int v;
        while ((v = getMinimalUnvisitedVertex(index)) != -1) {
            g.addEdge(index, v, adjMatrix[index][v]);
            minimalSpanningTree(v, g);
        }
    }

    /**
     * Перевіряє чи є даний граф є зв'язним
     * @return логічне {@code true} якщо граф зв'язний,
     * {@code false} - інакше
     */

    public boolean isConnected(){
        for (int i = 0; i < verticesCount; i++) {
            if(deg(i) < 1) return false;
        }

        return true;
    }

    @Override
    public boolean isSemiEulerian() {
        if(!isConnected()) return false;
        int oddDegVertices = 0;
        for (int i = 0; i < verticesCount; i++) {
            if(deg(i) % 2 == 1) oddDegVertices++;
            if(oddDegVertices > 2) return false;
        }

        return oddDegVertices == 0 || oddDegVertices == 2;
    }

    @Override
    public boolean isEulerian(){
        if(!isConnected()) return false;
        for (int i = 0; i < verticesCount; i++) {
            if(deg(i) % 2 == 1) return false;
        }

        return true;
    }

    private int dfsCount(int v, boolean[] isVisited)
    {
        isVisited[v] = true;
        int count = 1;
        for (int adj : adjList.get(v)) {
            if (!isVisited[adj]) {
                count = count + dfsCount(adj, isVisited);
            }
        }

        return count;
    }
}
