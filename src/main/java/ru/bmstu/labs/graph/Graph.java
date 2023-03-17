package ru.bmstu.labs.graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private final List<Vertex> vertices;
    private final List<Edge> edges;

    public Graph() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public Vertex getVertex(int index) {
        return vertices.get(index);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
        edge.getFirstVertex().addEdge(edge);
        edge.getSecondVertex().addEdge(edge);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Edge edge : edges) {
            sb.append(edge.getFirstVertex().toString())
                    .append(" ")
                    .append(edge.getSecondVertex().toString())
                    .append(" ")
                    .append(edge.getNumber())
                    .append("\n");
        }

        return sb.toString();
    }
}
