/**
 * Реалізація орієнтованого графа
 */

public class OrderedGraph extends Graph {

    /**
     * Створює орієнтований граф з заданою максимальною кількістю вершин
     * @param maxVerticesCount максимальна кількість вершин
     */

    public OrderedGraph(int maxVerticesCount){
        super(maxVerticesCount);
    }

    /**
     * Створює орієнтований граф з заданим списком вершин, матрицею суміжності,
     * максимальною кількістю вершин, та кількістю вершин
     * @param vertices список вершин
     * @param adjMatrix матриця суміжності
     * @param maxVerticesCount максимальна кількість вершин
     * @param nVertices кількість вершин
     */

    public OrderedGraph(Vertex[] vertices, double[][] adjMatrix, int maxVerticesCount, int nVertices){
        super(vertices, adjMatrix, maxVerticesCount, nVertices);
    }

    @Override
    protected void addEdgeToList(int start, int end) {
        adjList.get(start).add(end);
    }

    @Override
    protected void deleteEdgeFromList(int start, int end) {
        adjList.get(start).remove((Integer) end);
    }

    @Override
    public void addEdge(int start, int end, double weight) {
        adjMatrix[start][end] = weight;
    }

    @Override
    public boolean isSemiEulerian() {
        if(isEulerian()) return true;
        for (int i = 0; i < verticesCount; i++) {
            for (int j = 0; j < verticesCount; j++) {
                if(indeg(i) == outdeg(i) + 1 && indeg(j) == outdeg(j) - 1) {
                    for (int k = 0; k < verticesCount; k++) {
                        if(k != i && k != j){
                            if(indeg(k) != outdeg(k)) return false;
                        }
                    }
                }else{
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean isEulerian() {
        if(!isStronglyConnected()){
            return false;
        }

        for (int i = 0; i < verticesCount; i++) {
            if(indeg(i) != outdeg(i)) return false;
        }

        return true;
    }

    /**
     * Знаходить вхідну степінь вершини
     * @param v номер вершини
     * @return вхідну степінь вершини {@param v}
     */

    public int indeg(int v){
        return deg(v);
    }

    /**
     * Знаходить вихідну степінь вершини
     * @param v номер вершини
     * @return вихідну степінь вершини {@param v}
     */

    public int outdeg(int v){
        int sum = 0;
        for (int i = 0; i < verticesCount; i++) {
            if(adjMatrix[i][v] != 0) sum++;
        }

        return sum;
    }

    /**
     * Повертає список вершин графа у відсортованому порядку
     * @return список вершин графа у відсортованому порядку
     * @throws IllegalStateException якщо граф містить цикли
     */

    public Vertex[] topologicalSort(){
        Vertex[] sortedVertices = new Vertex[verticesCount];
        OrderedGraph copy = new OrderedGraph(vertices, adjMatrix, maxVerticesCount, verticesCount);
        while (copy.verticesCount > 0){
            int currentVertex = noSuccessors();
            if(currentVertex == -1){
                throw new IllegalStateException("Граф має цикли");
            }

            sortedVertices[copy.verticesCount - 1] = copy.vertices[currentVertex];
            copy.deleteVertex(currentVertex);
        }

        return sortedVertices;
    }

    /**
     * Перевіряє чи граф є слабо зв'язним
     * @return {@code true} якщо граф слабо зв'язний,
     * {@code false} - інакше
     */

    public boolean isWeaklyConnected(){
        double[][] newAdjMatrix = new double[verticesCount][verticesCount];
        for (int i = 0; i < verticesCount; i++) {
            for (int j = 0; j < verticesCount; j++) {
                if(adjMatrix[i][j] != 0){
                    newAdjMatrix[i][j] = adjMatrix[i][j];
                    newAdjMatrix[j][i] = adjMatrix[i][j];
                }
            }
        }
        NonOrderedGraph g = new NonOrderedGraph(vertices, newAdjMatrix, maxVerticesCount, verticesCount);

        return g.isConnected();
    }

    /**
     * Перевіряє чи граф є сильно зв'язним
     * @return {@code true} якщо граф сильно зв'язний,
     * {@code false} - інакше
     */

    public boolean isStronglyConnected(){
        for (int i = 0; i < verticesCount; i++) {
            if(dfs(i).size() < verticesCount) return false;
        }

        return true;
    }
}
