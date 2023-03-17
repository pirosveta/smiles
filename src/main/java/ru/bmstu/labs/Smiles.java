package ru.bmstu.labs;

import ru.bmstu.labs.graph.Graph;
import ru.bmstu.labs.issue.TransformerException;

public class Smiles {

    private final String smiles;

    public Smiles(Graph graph) throws TransformerException {
        this.smiles = new Transformer(graph).transform();
    }

    public String getSmiles() {
        return smiles;
    }
}
