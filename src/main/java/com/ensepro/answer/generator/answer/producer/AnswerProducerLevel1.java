package com.ensepro.answer.generator.answer.producer;

import java.util.List;
import java.util.concurrent.Callable;

import com.ensepro.answer.generator.answer.ScoreCalculation;
import com.ensepro.answer.generator.data.Triples;
import com.ensepro.answer.generator.data.answer.Answer;

import lombok.Builder;

public class AnswerProducerLevel1 extends AnswerProducer {

    @Builder
    public AnswerProducerLevel1(final Triples triples,
        final List<Callable<Answer>> callables, final ScoreCalculation scoreCalculation) {
        super(triples, callables, scoreCalculation);
        setName("AnswerProducerLevel1");
    }

    @Override
    public void run() {
        System.out.println(getName() + ": Starting");
        triples.getTriples().forEach(this::put);
        System.out.println(getName() + ": Finished");
    }
}