package ru.bmstu.labs.parsing;

import ru.bmstu.labs.issue.ParserException;

public interface Parser<S> {

    S getParsedStruct() throws ParserException;
}
