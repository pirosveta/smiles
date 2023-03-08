package ru.bmstu.labs.graph;

import ru.bmstu.labs.issue.PrintsException;

public abstract class Print {

    private long prevPrintNumber = 1;
    private long printNumber = 1;

    public long getPrevPrintNumber() {
        return this.prevPrintNumber;
    }

    public long getPrintNumber() {
        return printNumber;
    }

    public void incPrintNumber(long increment) {
        this.printNumber += increment;
    }

    public void syncPrints() throws PrintsException {
        if (this.printNumber < 0) {
            throw new PrintsException("Print value below zero: " + this.printNumber);
        }
        this.prevPrintNumber = this.printNumber;
    }
}
