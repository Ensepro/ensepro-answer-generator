package com.ensepro.answer.generator;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import com.ensepro.answer.generator.configuration.Configuration;
import com.ensepro.answer.generator.data.Answer;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.mapper.AnswerMapper;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.validator.AnswerL2Validator;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class AnswerGenerator {

    private final Helper helper;
    private final Configuration config;
    private final List<Triple> triples;

    public List<Answer> generateL1() throws ExecutionException, InterruptedException {
        final AnswerMapper mapper = new AnswerMapper(helper);

        final ForkJoinPool threadPool = new ForkJoinPool(config.getThreads());

        return threadPool.submit(() -> triples.parallelStream()
            .map(mapper::fromTriple)
            .collect(Collectors.toList())).get();
    }

    public List<Answer> generateL2() throws ExecutionException, InterruptedException {
        final AnswerMapper mapper = new AnswerMapper(helper);
        final AnswerL2Validator validator = new AnswerL2Validator();

        ForkJoinPool threadPool = new ForkJoinPool(config.getThreads());

        return threadPool.submit(() -> triples.parallelStream()
            .map(this::createPairWithOther)
            .flatMap(List::stream)
            .filter(validator::validate)
            .map(mapper::fromTriples)
            .collect(Collectors.toList())).get();
    }

    private List<List<Triple>> createPairWithOther(final Triple triple1) {
        return triples.stream()
            .map(triple2 -> asList(triple1, triple2))
            .collect(Collectors.toList());
    }

}
