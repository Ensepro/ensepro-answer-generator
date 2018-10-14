package com.ensepro.answer.generator.answer.producer;

import java.util.List;
import java.util.concurrent.Callable;

import com.ensepro.answer.generator.answer.ScoreCalculation;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.data.Triples;
import com.ensepro.answer.generator.data.answer.Answer;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnswerProducerLevel2 extends AnswerProducer {

    @Builder
    public AnswerProducerLevel2(final Triples triples,
        final List<Callable<Answer>> callables, final ScoreCalculation scoreCalculation) {
        super(triples, callables, scoreCalculation);
        setName("AnswerProducerLevel2");
    }

    @Override
    public void run() {
        log.info("Starting");
        triples.getTriples().forEach(triple1 -> {
            triples.getTriples().forEach(triple2 -> {
                if (shouldAdd(triple1, triple2)) {
                    put(triple1, triple2);
                }
            });
        });
        log.info("Finished");
    }

    private boolean shouldAdd(final Triple triple1, final Triple triple2) {
        boolean subject_igual, predicate_igual, object_igual;
        boolean subject_diferente, predicate_diferente, object_diferente;
        boolean subject_igual_object;

        subject_igual = triple1.getSubject().equals(triple2.getSubject());
        predicate_igual = triple1.getPredicate().equals(triple2.getPredicate());
        object_igual = triple1.getObject().equals(triple2.getObject());

        subject_diferente = !subject_igual;
        predicate_diferente = !predicate_igual;
        object_diferente = !object_igual;

        subject_igual_object = triple1.getSubject().equals(triple2.getObject());

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
