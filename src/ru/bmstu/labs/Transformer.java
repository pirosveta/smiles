package ru.bmstu.labs;

import ru.bmstu.labs.graph.Edge;
import ru.bmstu.labs.graph.Graph;
import ru.bmstu.labs.graph.Vertex;
import ru.bmstu.labs.issue.PrintsException;
import ru.bmstu.labs.issue.TransformerException;

import java.util.*;

public class Transformer {

    private final Graph graph;
    private final List<Vertex> queue;
    public Integer lastCycleNumber = 1;

    private Edge lastEdge;

    public Transformer(Graph graph) {
        this.graph = graph;
        this.queue = new ArrayList<>();
        this.lastEdge = null;
    }

    public String transform() throws TransformerException {
        fillMorganPrints();

        Vertex minVertex = graph.getVertex(0);
        for (Vertex vertex : graph.getVertices()) {
            if (vertex.getPrintNumber() < minVertex.getPrintNumber()) {
                minVertex = vertex;
            }
        }

        lastEdge = null;
        queue.add(minVertex);
        findCycles();

        lastEdge = null;
        queue.add(minVertex);
        StringBuilder sb = new StringBuilder();
        formSmiles(sb, 1);

        return sb.toString();
    }

    private void fillMorganPrints() throws TransformerException {
        int iterationCounter = 0;
        int numberOfEqualsSteps = 0;
        int uniquePrints = getUniquePrints();
        int prevUniquePrints = uniquePrints;
        int verticesSize = graph.getVertices().size();

        while (uniquePrints != verticesSize && iterationCounter < 20) {
            iterationCounter++;
            nextMorganStep();

            uniquePrints = getUniquePrints();
            if (prevUniquePrints == uniquePrints) {
                numberOfEqualsSteps++;
            }
            if (numberOfEqualsSteps == 5) {
                reorderPrints();
                numberOfEqualsSteps = 0;
            }
            prevUniquePrints = uniquePrints;
        }
    }

    private void nextMorganStep() throws TransformerException {
        try {
            for (Vertex vertex : graph.getVertices()) {
                long increment = 0;
                for (Edge edge : vertex.getEdges()) {
                    if (!edge.getFirstVertex().equals(vertex)) {
                        increment += edge.getFirstVertex().getPrevPrintNumber();
                    } else {
                        increment += edge.getSecondVertex().getPrevPrintNumber();
                    }
                }

                vertex.incPrintNumber(increment);
            }

            for (Vertex vertex : graph.getVertices()) {
                vertex.syncPrints();

            }
        } catch (PrintsException e) {
            throw new TransformerException(e.getMessage());
        }
    }

    private int getUniquePrints() {
        Map<Long, Boolean> uniquePrints = new HashMap<>();
        for (Vertex vertex : graph.getVertices()) {
            uniquePrints.put(vertex.getPrintNumber(), false);
        }
        return uniquePrints.size();
    }

    private void reorderPrints() throws TransformerException {
        try {
            for (Vertex vertex : graph.getVertices()) {
                vertex.incPrintNumber(vertex.getPrintNumber() - 1);
                vertex.syncPrints();
            }
        } catch (PrintsException e) {
            throw new TransformerException(e.getMessage());
        }
    }

    private void findCycles() {
        Vertex currentVertex = queue.get(0);
        currentVertex.incPassed();

        for (Edge edge : currentVertex.getEdges()) {
            if (!edge.equals(lastEdge)) {
                lastEdge = edge;

                if (!edge.getFirstVertex().equals(currentVertex)) {
                    if (edge.getFirstVertex().isPassed()) {
                        int passedVertexIndex = queue.indexOf(edge.getFirstVertex());
                        for (int i = 0; i <= passedVertexIndex; i++) {
                            queue.get(i).setInCycle(true);
                        }
                        continue;
                    }
                    queue.add(0, edge.getFirstVertex());
                } else {
                    if (edge.getSecondVertex().isPassed()) {
                        int passedVertexIndex = queue.indexOf(edge.getSecondVertex());
                        for (int i = 0; i <= passedVertexIndex; i++) {
                            queue.get(i).setInCycle(true);
                        }
                        continue;
                    }
                    queue.add(0, edge.getSecondVertex());
                }

                findCycles();
            }
        }

        queue.remove(0);
    }

    private void formSmiles(StringBuilder sb, int orderCounter) {
        Vertex currentVertex = queue.get(0);
        currentVertex.setOrder(orderCounter++);

        if (lastEdge != null && lastEdge.getNumber() == 2) {
            sb.append("=");
        }
        sb.append(currentVertex.getName());

        if (currentVertex.isInCycle()) {
            int cycleVariants = 0;
            TreeMap<Long, Map.Entry<Vertex, Edge>> vertices = new TreeMap<>();

            for (Edge edge : currentVertex.getEdges()) {
                if (!edge.equals(lastEdge)) {
                    Vertex firstVertex = edge.getFirstVertex();
                    Vertex secondVertex = edge.getSecondVertex();

                    if (!firstVertex.equals(currentVertex)) {
                        if (firstVertex.isInCycle()) {
                            cycleVariants++;
                        }
                        vertices.put(firstVertex.getPrintNumber(), Map.entry(firstVertex, edge));
                    } else {
                        if (secondVertex.isInCycle()) {
                            cycleVariants++;
                        }
                        vertices.put(secondVertex.getPrintNumber(), Map.entry(secondVertex, edge));
                    }
                }
            }

            if (cycleVariants > 1) {
                sb.append(lastCycleNumber);

                vertices.lastEntry().getValue().getKey().setName(
                        vertices.lastEntry().getValue().getKey().getName() + lastCycleNumber
                );

                lastCycleNumber++;

                lastEdge = vertices.firstEntry().getValue().getValue();
                queue.add(0, vertices.firstEntry().getValue().getKey());
                formSmiles(sb, orderCounter);
            } else {
                if (vertices.size() > 1) {

                    if (vertices.firstEntry().getValue().getKey().getOrder() != 0) {
                        lastEdge = vertices.lastEntry().getValue().getValue();
                        queue.add(0, vertices.lastEntry().getValue().getKey());
                    } else {
                        lastEdge = vertices.firstEntry().getValue().getValue();
                        queue.add(0, vertices.firstEntry().getValue().getKey());
                    }
                } else {
                    lastEdge = vertices.lastEntry().getValue().getValue();
                    queue.add(0, vertices.lastEntry().getValue().getKey());
                }
                formSmiles(sb, orderCounter);
            }
        } else {
            TreeMap<Long, Map.Entry<Vertex, Edge>> vertices = new TreeMap<>();

            for (Edge edge : currentVertex.getEdges()) {
                if (!edge.equals(lastEdge)) {
                    Vertex firstVertex = edge.getFirstVertex();
                    Vertex secondVertex = edge.getSecondVertex();

                    if (!firstVertex.equals(currentVertex)) {
                        vertices.put(firstVertex.getPrintNumber(), Map.entry(firstVertex, edge));
                    } else {
                        vertices.put(secondVertex.getPrintNumber(), Map.entry(secondVertex, edge));
                    }
                }
            }

            if (!vertices.isEmpty()) {

                if (vertices.size() > 1) {
                    sb.append("(");
                    lastEdge = vertices.firstEntry().getValue().getValue();
                    queue.add(0, vertices.firstEntry().getValue().getKey());
                    formSmiles(sb, orderCounter);
                    sb.append(")");
                }

                lastEdge = vertices.lastEntry().getValue().getValue();
                queue.add(0, vertices.lastEntry().getValue().getKey());
                formSmiles(sb, orderCounter);
            }
        }

        queue.remove(0);
    }
}
