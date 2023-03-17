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

        lastCycleNumber = 1;
        queue.add(minVertex);
        findCycles(null);

        lastEdge = null;
        lastCycleNumber = 1;
        queue.add(minVertex);
        StringBuilder sb = new StringBuilder();
        formSmiles(null, sb, 1);

        return sb.toString();
    }

    private void fillMorganPrints() throws TransformerException {
        int iterationCounter = 0;
        int numberOfEqualsSteps = 0;
        int uniquePrints = getUniquePrints();
        int prevUniquePrints = uniquePrints;
        int verticesSize = graph.getVertices().size();

        while (uniquePrints != verticesSize && iterationCounter < 10) {
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

    private void findCycles(Vertex prevVertex) {
        Vertex currentVertex = queue.get(0);
        currentVertex.incPassed();

        for (Edge edge : currentVertex.getEdges()) {
            if (!edge.getFirstVertex().equals(currentVertex) && !edge.getFirstVertex().equals(prevVertex)) {
                if (edge.getFirstVertex().isPassed()) {
                    int passedVertexIndex = queue.indexOf(edge.getFirstVertex());
                    int curCycleNumber = lastCycleNumber;

                    for (int i = 0; i <= passedVertexIndex; i++) {
                        if (queue.get(i).getCycleNumber() != 0) {
                            curCycleNumber = queue.get(i).getCycleNumber();
                            break;
                        }
                    }

                    for (int i = 0; i <= passedVertexIndex; i++) {
                        queue.get(i).setCycleNumber(curCycleNumber);
                    }

                    lastCycleNumber++;
                    continue;
                }

                queue.add(0, edge.getFirstVertex());
                findCycles(currentVertex);
            } else if (!edge.getSecondVertex().equals(currentVertex) && !edge.getSecondVertex().equals(prevVertex)) {
                if (edge.getSecondVertex().isPassed()) {
                    int passedVertexIndex = queue.indexOf(edge.getSecondVertex());
                    int curCycleNumber = lastCycleNumber;

                    for (int i = 0; i <= passedVertexIndex; i++) {
                        if (queue.get(i).getCycleNumber() != 0) {
                            curCycleNumber = queue.get(i).getCycleNumber();
                            break;
                        }
                    }

                    for (int i = 0; i <= passedVertexIndex; i++) {
                        queue.get(i).setCycleNumber(curCycleNumber);
                    }

                    lastCycleNumber++;
                    continue;
                }

                queue.add(0, edge.getSecondVertex());
                findCycles(currentVertex);
            }

        }

        queue.remove(0);
    }

    private void formSmiles(Vertex prevVertex, StringBuilder sb, int orderCounter) {
        Vertex currentVertex = queue.get(0);
        if (currentVertex.getOrder() != 0) {
            return;
        }
        currentVertex.setOrder(orderCounter++);

        if (prevVertex != null && lastEdge.getNumber() == 2) {
            sb.append("=");
        }
        sb.append(currentVertex.getName());

        if (currentVertex.isInCycle()) {
            int cycleVariants = 0;
            List<Map.Entry<Vertex, Edge>> vertices = new ArrayList<>();

            for (Edge edge : currentVertex.getEdges()) {
                Vertex firstVertex = edge.getFirstVertex();
                Vertex secondVertex = edge.getSecondVertex();

                if (!firstVertex.equals(currentVertex) && !firstVertex.equals(prevVertex)) {
                    if (firstVertex.isInCycle()) {
                        cycleVariants++;
                    }
                    vertices.add(Map.entry(firstVertex, edge));
                } else if (!secondVertex.equals(currentVertex) && !secondVertex.equals(prevVertex)) {
                    if (secondVertex.isInCycle()) {
                        cycleVariants++;
                    }
                    vertices.add(Map.entry(secondVertex, edge));
                }
            }

            vertices.sort(Comparator.comparingLong(o -> o.getKey().getPrintNumber()));

            if (cycleVariants > 1 && currentVertex.getName().length() == 1
                    && vertices.get(0).getKey().getCycleNumber() == vertices.get(vertices.size() - 1).getKey().getCycleNumber()) {
                sb.append(lastCycleNumber);

                if (vertices.get(1).getKey().getName().length() == 1) {
                    vertices.get(1).getKey().setName(
                            vertices.get(1).getKey().getName() + lastCycleNumber
                    );
                }

                lastCycleNumber++;

                lastEdge = vertices.get(0).getValue();
                queue.add(0, vertices.get(0).getKey());
                formSmiles(currentVertex, sb, orderCounter);
            } else {
                if (vertices.size() > 1) {

                    if (vertices.get(0).getKey().getOrder() != 0) {
                        lastEdge = vertices.get(vertices.size() - 1).getValue();
                        queue.add(0, vertices.get(vertices.size() - 1).getKey());
                    } else if (vertices.get(vertices.size() - 1).getKey().getOrder() != 0) {
                        lastEdge = vertices.get(0).getValue();
                        queue.add(0, vertices.get(0).getKey());
                    } else {
                        // ветвим не цикл
                        if (!vertices.get(0).getKey().isInCycle()) {
                            sb.append("(");
                            lastEdge = vertices.get(0).getValue();
                            queue.add(0, vertices.get(0).getKey());
                            formSmiles(currentVertex, sb, orderCounter);
                            sb.append(")");

                            lastEdge = vertices.get(vertices.size() - 1).getValue();
                            queue.add(0, vertices.get(vertices.size() - 1).getKey());
                        } else {
                            sb.append("(");
                            lastEdge = vertices.get(vertices.size() - 1).getValue();
                            queue.add(0, vertices.get(vertices.size() - 1).getKey());
                            formSmiles(currentVertex, sb, orderCounter);
                            sb.append(")");

                            lastEdge = vertices.get(0).getValue();
                            queue.add(0, vertices.get(0).getKey());
                        }
                    }

                    formSmiles(currentVertex, sb, orderCounter);
                } else if (vertices.get(0).getKey().getOrder() == 0) {
                    lastEdge = vertices.get(vertices.size() - 1).getValue();
                    queue.add(0, vertices.get(vertices.size() - 1).getKey());
                    formSmiles(currentVertex, sb, orderCounter);
                }
            }
        } else {
            List<Map.Entry<Vertex, Edge>> vertices = new ArrayList<>();

            for (Edge edge : currentVertex.getEdges()) {
                Vertex firstVertex = edge.getFirstVertex();
                Vertex secondVertex = edge.getSecondVertex();

                if (!edge.getFirstVertex().equals(currentVertex) && !edge.getFirstVertex().equals(prevVertex)) {
                    vertices.add(Map.entry(firstVertex, edge));
                } else if (!edge.getSecondVertex().equals(currentVertex) && !edge.getSecondVertex().equals(prevVertex)) {
                    vertices.add(Map.entry(secondVertex, edge));
                }
            }

            vertices.sort(Comparator.comparingLong(o -> o.getKey().getPrintNumber()));

            if (!vertices.isEmpty()) {

                if (vertices.size() > 1) {
                    sb.append("(");
                    lastEdge = vertices.get(0).getValue();
                    queue.add(0, vertices.get(0).getKey());
                    formSmiles(currentVertex, sb, orderCounter);
                    sb.append(")");
                }

                lastEdge = vertices.get(vertices.size() - 1).getValue();
                queue.add(0, vertices.get(vertices.size() - 1).getKey());
                formSmiles(currentVertex, sb, orderCounter);
            }
        }

        queue.remove(0);
    }
}
