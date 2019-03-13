package com.ensepro.answer.generator.domain;

public enum GrammarClass {

    PROP,
    SUB,
    VERB,
    ADJ;

    public boolean isProp() {
        return PROP.equals(this);
    }

    public boolean isAdj() {
        return ADJ.equals(this);
    }

    public boolean isVerb() {
        return VERB.equals(this);
    }

    public boolean isSub() {
        return SUB.equals(this);
    }
}
