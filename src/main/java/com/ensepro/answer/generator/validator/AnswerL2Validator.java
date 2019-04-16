package com.ensepro.answer.generator.validator;

import java.util.List;

import com.ensepro.answer.generator.data.Triple;

public class AnswerL2Validator {

    public boolean validate(final List<Triple> triples) {
        return this.validate(triples.get(0), triples.get(1));
    }

    public boolean validate(final Triple triple1, final Triple triple2) {
        boolean subject_igual, predicate_igual, object_igual;
        boolean subject_diferente, predicate_diferente, object_diferente;
        boolean subject_igual_object;

        subject_igual = triple1.getSubject().equals(triple2.getSubject());
        predicate_igual = triple1.getPredicate().equals(triple2.getPredicate());
        object_igual = triple1.getObject().equals(triple2.getObject());

        subject_diferente = !subject_igual;
        predicate_diferente = !predicate_igual;
        object_diferente = !object_igual;

        subject_igual_object = triple1.getSubject().equals(triple2.getObject())
            || triple2.getSubject().equals(triple1.getObject());

        if (subject_igual && predicate_igual && object_igual) {
            return false;
        }

        if (subject_igual && predicate_diferente && object_diferente) {
            return true;
        }

        if (subject_diferente && predicate_diferente && object_diferente && subject_igual_object) {
            return true;
        }

        if (subject_igual && predicate_igual && object_diferente) {
            return true;
        }

        if (subject_diferente && predicate_igual && object_igual) {
            return true;
        }

        return false;
    }

}
