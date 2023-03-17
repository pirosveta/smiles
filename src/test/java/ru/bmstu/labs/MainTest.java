package ru.bmstu.labs;

public class MainTest {

    public void mainTest() {
        assert Main.getSmiles("tests/methane").equals("C");
        assert Main.getSmiles("tests/ethane").equals("CC");
        assert Main.getSmiles("tests/propane").equals("CCC");
        assert Main.getSmiles("tests/hexane").equals("CCCCCC");
        assert Main.getSmiles("tests/isobutane").equals("CC(C)C");
//        assert Main.getSmiles("tests/dimethylethylphenol").equals("");
//        assert Main.getSmiles("tests/ethylmethylketone").equals("");
//        assert Main.getSmiles("tests/toluene").equals("");
//        assert Main.getSmiles("tests/acetaminophen").equals("");
//        assert Main.getSmiles("tests/diphenyl").equals("");
//        assert Main.getSmiles("tests/3-amino-2-naphthoic_acid").equals("");

    }
}
