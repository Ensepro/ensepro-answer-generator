package com.ensepro.answer.generator.answer.producer;

import java.util.List;
import java.util.concurrent.Callable;

import com.ensepro.answer.generator.answer.ScoreCalculation;
import com.ensepro.answer.generator.data.Triples;
import com.ensepro.answer.generator.data.answer.Answer;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnswerProducerLevel1 extends AnswerProducer {

    @Builder
    public AnswerProducerLevel1(final Triples triples,
        final List<Callable<Answer>> callables, final ScoreCalculation scoreCalculation) {
        super(triples, callables, scoreCalculation);
        setName("AnswerProducerLevel1");
    }

    @Override
    public void run() {
        log.info("Starting");
        triples.getTriples().forEach(this::put);
        log.info("Finished");
    }
}