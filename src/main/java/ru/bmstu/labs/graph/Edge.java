package ru.bmstu.labs.graph;

public class Edge {

    private final Vertex firstVertex;
    private final Vertex secondVertex;
    private final int number;

    public Edge(Vertex firstVertex, Vertex secondVertex, int number) {
        this.firstVertex = firstVertex;
        this.secondVertex = secondVertex;
        this.number = number;
    }

    public Vertex getFirstVertex() {
        return firstVertex;
    }

    public Vertex getSecondVertex() {
        return secondVertex;
    }

    public int getNumber() {
        return number;
    }
}
