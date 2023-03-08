package ru.bmstu.labs.graph;

import java.util.ArrayList;
import java.util.List;

public class Vertex extends Print {

    private final List<Edge> edges;

    private String name;
    private int order;
    private int passed;
    private boolean inCycle;

    public Vertex(String name) {
        this.edges = new ArrayList<>();
        this.name = name;
        this.order = 0;
        this.passed = 0;
        this.inCycle = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isPassed() {
        return passed > 0;
    }

    public void incPassed() {
        passed = passed + 1;
    }

    public boolean isInCycle() {
        return inCycle;
    }

    public void setInCycle(boolean inCycle) {
        this.inCycle = inCycle;
    }

    @Override
    public String toString() {
        return name;
    }
}
