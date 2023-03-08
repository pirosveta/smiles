package ru.bmstu.labs.parsing;

import ru.bmstu.labs.graph.Edge;
import ru.bmstu.labs.graph.Graph;
import ru.bmstu.labs.graph.Vertex;
import ru.bmstu.labs.issue.ParserException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Mol2GraphParser implements Parser<Graph> {

    private final String fileName;

    private Graph graph;

    public Mol2GraphParser(String fileName) {
        this.fileName = fileName + ".mol";
    }

    @Override
    public Graph getParsedStruct() throws ParserException {
        if (graph == null) {
            parseToStruct();
        }
        return graph;
    }

    private void parseToStruct() throws ParserException {
        graph = new Graph();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;

            int definitionStartLine = 4;
            int lineCounter = 0;
            int endOfVerticesDefinition = 0;
            int definitionEndLine = 0;

            while ((line = reader.readLine()) != null) {
                lineCounter++;

                if (lineCounter == definitionStartLine) {
                    String[] values = line.trim().split("\s+");

                    int verticesCount = Integer.parseInt(values[0]);
                    int edgesCount = Integer.parseInt(values[1]);

                    endOfVerticesDefinition = definitionStartLine + verticesCount;
                    definitionEndLine = endOfVerticesDefinition + edgesCount + 1;
                }

                if (lineCounter > definitionStartLine) {
                    if (lineCounter == definitionEndLine) {
                        break;
                    } else {
                        String[] values = line.trim().split("\s+");

                        if (lineCounter <= endOfVerticesDefinition) {
                            graph.addVertex(new Vertex(values[3]));
                        } else {
                            Vertex firstVertex = graph.getVertex(Integer.parseInt(values[0]) - 1);
                            Vertex secondVertex = graph.getVertex(Integer.parseInt(values[1]) - 1);

                            if (!firstVertex.getName().equals("H") && !secondVertex.getName().equals("H")) {
                                graph.addEdge(new Edge(firstVertex,
                                        secondVertex,
                                        Integer.parseInt(values[2])));
                            }
                        }
                    }
                }
            }

            reader.close();

            clearH();
        } catch (IOException e) {
            throw new ParserException(e.getMessage());
        }
    }

    private void clearH() {
        graph.getVertices().removeIf(vertex -> vertex.getName().equals("H"));
    }
}
