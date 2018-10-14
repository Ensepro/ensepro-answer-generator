package com.ensepro.answer.generator.answer;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.ensepro.answer.generator.answer.consumer.AnswerConsumer;
import com.ensepro.answer.generator.answer.producer.AnswerProducer;
import com.ensepro.answer.generator.answer.producer.AnswerProducerLevel1;
import com.ensepro.answer.generator.answer.producer.AnswerProducerLevel2;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Triples;
import com.ensepro.answer.generator.data.answer.Answer;

import lombok.Builder;

@Builder
public class AnswerGenerator {

    private static final int THREADS = 20;

    private final Helper helper;
    private final Triples triples;
    private final ScoreCalculation scoreCalculator;
    private final Integer level;

    private final List<AnswerProducer> producers = new ArrayList<>();
    private final List<AnswerConsumer> consumers = new ArrayList<>();

    public List<Answer> execute() throws InterruptedException {
        createProducers();
        createConsumers();

        final Set<Answer> answers = new HashSet<>();

        consumers.forEach(consumer -> answers.addAll(consumer.getAnswers()));

        return new ArrayList<>(answers);
    }

    private void createConsumers() {
        producers.forEach(producer -> {
            final AnswerConsumer consumer = AnswerConsumer.builder()
                .executorService(Executors.newFixedThreadPool(THREADS))
                .producer(producer)
                .name(producer.getName() + ".Consumer")
                .build();
            consumer.start();
            consumers.add(consumer);
        });

    }

    private void createProducers() {
        AnswerProducer producerLevel1 = AnswerProducerLevel1.builder()
            .triples(triples)
            .callables(Collections.synchronizedList(new ArrayList<>()))
            .scoreCalculation(scoreCalculator)
            .build();
        AnswerProducer producerLevel2 = AnswerProducerLevel2.builder()
            .triples(triples)
            .callables(Collections.synchronizedList(new ArrayList<>()))
            .scoreCalculation(scoreCalculator)
            .build();
//        AnswerProducer producerLevel3 = AnswerProducerLevel3.builder()
//            .triples(triples)
//            .callables(callables)
//            .scoreCalculation(scoreCalculator)
//            .build();

        producers.add(producerLevel1);
        producerLevel1.start();
        if (this.level > 1) {
            producers.add(producerLevel2);
            producerLevel2.start();
        }
//        TODO disable now to make sure it doesn't happen.
//        if (this.level > 2) {
//            producers.add(producerLevel3);
//            producerLevel3.start();
//        }
    }


}
