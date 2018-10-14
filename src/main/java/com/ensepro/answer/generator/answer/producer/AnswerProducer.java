package com.ensepro.answer.generator.answer.producer;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.concurrent.Callable;

import com.ensepro.answer.generator.answer.ScoreCalculation;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.data.Triples;
import com.ensepro.answer.generator.data.answer.Answer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class AnswerProducer extends Thread {

    protected final Triples triples;
    @Getter
    private final List<Callable<Answer>> callables;
    private final ScoreCalculation scoreCalculation;

    @Override
    public abstract void run();

    void put(final Triple... list) {
        put(asList(list));
    }

    private void put(final List<Triple> list) {
        callables.add(() -> scoreCalculation.calculate(list));
    }


}
