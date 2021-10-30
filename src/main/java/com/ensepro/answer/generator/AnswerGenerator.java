package com.ensepro.answer.generator;

import com.ensepro.answer.generator.configuration.Configuration;
import com.ensepro.answer.generator.data.Answer;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.mapper.AnswerMapper;
import com.ensepro.answer.generator.validator.AnswerL2Validator;
import com.ensepro.answer.generator.validator.AnswerL3Validator;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Slf4j
@Builder
public class AnswerGenerator {

    private final Helper helper;
    private final Configuration config;
    private List<Triple> triples;

    private final Supplier<Map<Integer, Function<List<Triple>, List<Answer>>>> generators = () -> {
        final Map<Integer, Function<List<Triple>, List<Answer>>> map = new HashMap<>();
        map.put(1, this::generateL1);
        map.put(2, this::generateL2);
        map.put(3, this::generateL3);
        return map;
    };


    public List<Answer> generate(final int level, final List<Triple> triples) {

        final Function<List<Triple>, List<Answer>> generator = generators.get().get(level);
        return generator.apply(triples);
    }


    @SneakyThrows
    public List<Answer> generateL1(final List<Triple> triples) {
        this.triples = triples;
        final AnswerMapper mapper = new AnswerMapper(helper);

        final ForkJoinPool threadPool = new ForkJoinPool(config.getThreads());

        return threadPool.submit(() -> triples.parallelStream()
            .map(mapper::fromTriple)
            .collect(Collectors.toList()))
            .get();
    }

    public List<Answer> generateL1() throws ExecutionException, InterruptedException {
        final AnswerMapper mapper = new AnswerMapper(helper);

        final ForkJoinPool threadPool = new ForkJoinPool(config.getThreads());

        return threadPool.submit(() -> triples.parallelStream()
            .map(mapper::fromTriple)
            .collect(Collectors.toList()))
            .get();
    }

    @SneakyThrows
    public List<Answer> generateL2(final List<Triple> triples) {
        this.triples = triples;
        final AnswerMapper mapper = new AnswerMapper(helper);
        final AnswerL2Validator validator = new AnswerL2Validator();

        ForkJoinPool threadPool = new ForkJoinPool(config.getThreads());

        return threadPool.submit(() -> triples.parallelStream()
            .map(this::createPairWithOther)
            .flatMap(List::stream)
            .filter(validator::validate)
            .map(mapper::fromTriples)
            .collect(Collectors.toList()))
            .get();
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


    public List<Answer> generateL3() throws ExecutionException, InterruptedException {
        this.triples = triples;
        final AnswerMapper mapper = new AnswerMapper(helper);
        final AnswerL3Validator validator = new AnswerL3Validator();

        ForkJoinPool threadPool = new ForkJoinPool(config.getThreads());

        return threadPool.submit(() -> triples.parallelStream()
            .map(this::createTriplesWithOtherL1)
            .flatMap(List::stream)
            .filter(validator::validate)
            .map(mapper::fromTriples)
            .collect(Collectors.toList()))
            .get();
    }

    @SneakyThrows
    public List<Answer> generateL3(final List<Triple> triples) {
        final AnswerMapper mapper = new AnswerMapper(helper);
        final AnswerL3Validator validator = new AnswerL3Validator();

        ForkJoinPool threadPool = new ForkJoinPool(config.getThreads());

        return threadPool.submit(() -> triples.parallelStream()
            .map(this::createTriplesWithOtherL1)
            .flatMap(List::stream)
            .filter(validator::validate)
            .map(mapper::fromTriples)
            .collect(Collectors.toList()))
            .get();
    }

    private List<List<Triple>> createPairWithOther(final Triple triple1) {
        return triples.stream()
            .map(triple2 -> asList(triple1, triple2))
            .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesWithOtherL1(final Triple triple1) {
        return triples.stream()
            .map(triple2 -> createTriplesWithOtherL2(triple1, triple2))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesWithOtherL2(final Triple triple1, final Triple triple2) {
        return triples.stream()
            .map(triple3 -> asList(triple1, triple2, triple3))
            .collect(Collectors.toList());
    }
}
