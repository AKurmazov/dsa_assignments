package mst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

// The task was done with the aid of this article: https://e-maxx.ru/algo/mst_kruskal
// Alexander Kurmazov BS19-05

public class MinSpanningForest {

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int n = Integer.parseInt(st.nextToken());
            int m = Integer.parseInt(st.nextToken());
            Graph<Integer, Integer> graph = new Graph<>(n, m);

            for (int i = 0; i < m; ++i) {
                st = new StringTokenizer(br.readLine());
                int from = Integer.parseInt(st.nextToken());
                int to = Integer.parseInt(st.nextToken());
                int weight = Integer.parseInt(st.nextToken());
                graph.addEdge(from, to, weight, false);
            }

            // Register the vertices that remained "unedged"
            ArrayList<Integer> disconnectedVertices = new ArrayList<>();
            for (int i = 1; i <= n; ++i) {
                if (!graph.regVertices.containsKey(i)) {
                    int vertex = graph.addVertexIfNotExists(i);
                    disconnectedVertices.add(vertex);
                }
            }
            Answer<Integer, Integer> answer = graph.kruskal();

            // Merge the answer into trees
            int treesCount = 0;
            HashMap<Integer, HashSet<Graph.Edge>> trees = new HashMap<>();
            for (int i = 0; i < answer.result.size(); ++i) {
                Graph.Edge edge = answer.result.get(i);
                int temp = answer.dsu.find(edge.from);
                if (trees.containsKey(temp)) {
                    trees.get(temp).add(edge);
                } else {
                    HashSet<Graph.Edge> newHashSet = new HashSet<>();
                    newHashSet.add(edge);
                    trees.put(temp, newHashSet);
                    treesCount++;
                }
            }

            // Print the final answer
            System.out.println(disconnectedVertices.size() + treesCount);
            for (int key: trees.keySet()) {
                HashSet<Graph.Edge> tree = trees.get(key);
                System.out.println((tree.size()+1) + " " + graph.vertices.get(key));
                for (Graph.Edge edge: tree)
                    System.out.println(graph.vertices.get(edge.from) + " " + graph.vertices.get(edge.to) + " " + edge.weight);
            }
            for (int i = 0; i < disconnectedVertices.size(); ++i) {
                System.out.println(1 + " " + graph.vertices.get(disconnectedVertices.get(i)));
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}

class Answer<T, E extends Number & Comparable<E>> {

    ArrayList<Graph<T, E>.Edge> result;
    DSU dsu;

}

class Graph<T, E extends Number & Comparable<E>> {

    class Edge {

        int from, to;
        E weight;

        Edge(int from, int to, E weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public E getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return this.from + " " + this.to + " " + this.weight;
        }

    }

    private int numVertexes, numEdges;

    Graph(int numVertexes, int numEdges) {
        this.numVertexes = numVertexes;
        this.numEdges = numEdges;
    }

    private ArrayList<Edge> edges = new ArrayList<>();
    ArrayList<T> vertices = new ArrayList<>();
    HashMap<T, Integer> regVertices = new HashMap<>(); // Added to check the existence of a Vertex in O(1)

    public int addVertexIfNotExists(T vertex) {
        // Adds a vertex in the graph or returns an existing one
        // Time complexity O(1)

        if (regVertices.containsKey(vertex)) {
            return regVertices.get(vertex);
        }
        vertices.add(vertex);

        int tempInd = vertices.size() - 1;
        regVertices.put(vertex, tempInd);

        return tempInd;
    }

    public void addEdge(T from, T to, E weight, boolean isUndirected) {
        // Adds an edge to the graph
        // Time complexity O(1)

        int fromInd = addVertexIfNotExists(from);
        int toInd = addVertexIfNotExists(to);
        edges.add(new Edge(fromInd, toInd, weight));
        if (isUndirected)
            edges.add(new Edge(toInd, fromInd, weight));
    }

    public Answer<T, E> kruskal() {
        // Kruskal's algorithm implementation
        // Time complexity O(E*logV) E - num of edges, V - num of vertices

        edges.sort(Comparator.comparing(Edge::getWeight));
        DSU dsu = new DSU(numVertexes);
        for (int i = 0; i < numVertexes; ++i) {
            dsu.parents[i] = i;
            dsu.size[i] = 1;
        }

        ArrayList<Edge> result = new ArrayList<>();
        for (int i = 0; i < numEdges; ++i) {
            Edge edge = edges.get(i);
            if (dsu.find(edge.from) != dsu.find(edge.to)) {
                result.add(edge);
                dsu.unite(edge.from, edge.to);
            }
        }

        Answer<T, E> answer = new Answer<>();
        answer.dsu = dsu;
        answer.result = result;

        return answer;
    }

}

class DSU {

    // Disjoint Set Union implementation

    int[] parents;
    int[] size;

    DSU(int size) {
        this.parents = new int[size];
        this.size = new int[size];
    }

    public int find(int vertex) {
        // Time complexity is O(logN)
        // But in fact it's close to O(1) for all reasonable numbers
        // Emaxx states that it won't exceed 4 for N <= 10^600
        // https://e-maxx.ru/algo/dsu

        if (vertex == parents[vertex])
            return vertex;
        parents[vertex] = find(parents[vertex]);
        return parents[vertex];
    }

    public void unite(int one, int another) {
        // The same time complexity as for find
        // O(logN) but in fact almost O(1)

        one = find(parents[one]);
        another = find(parents[another]);
        if (size[one] > size[another]) {
            parents[another] = one;
            size[one] += size[another];
        } else {
            parents[one] = another;
            size[another] += size[one];
        }
    }

}
