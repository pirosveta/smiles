package ru.bmstu.labs;

import ru.bmstu.labs.issue.ParserException;
import ru.bmstu.labs.issue.TransformerException;
import ru.bmstu.labs.parsing.Mol2GraphParser;

public class Main {

    public static void main(String[] args) {
        getSmiles("folate");
        getSmiles("adrenaline");
        getSmiles("cocaine");
        getSmiles("methane");
        getSmiles("ethane");
        getSmiles("propane");
        getSmiles("hexane");
        getSmiles("isobutane");
        getSmiles("dimethylethylphenol");
        getSmiles("ethylmethylketone");
        getSmiles("toluene");
        getSmiles("acetaminophen");
        getSmiles("diphenyl");
        getSmiles("3-amino-2-naphthoic_acid");
    }

    public static String getSmiles(String moleculeName) {
        Mol2GraphParser parser = new Mol2GraphParser(moleculeName);
        try {
            Smiles smiles = new Smiles(parser.getParsedStruct());
            System.out.printf("Smiles of %s:\n%s\n", moleculeName, smiles.getSmiles());
            return smiles.getSmiles();
        } catch (ParserException e) {
            System.out.println("Error while parsing: " + e.getMessage());
        } catch (TransformerException e) {
            System.out.println("Error while transforming: " + e.getMessage());
        }

        return "";
    }
}
