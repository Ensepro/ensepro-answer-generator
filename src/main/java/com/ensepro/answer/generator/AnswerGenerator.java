package com.ensepro.answer.generator;

import com.ensepro.answer.generator.configuration.Configuration;
import com.ensepro.answer.generator.data.Answer;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.mapper.AnswerMapper;
import com.ensepro.answer.generator.validator.AnswerL2Validator;
import com.ensepro.answer.generator.validator.AnswerL3Validator;
import com.ensepro.answer.generator.validator.AnswerL4Validator;
import com.ensepro.answer.generator.validator.AnswerL5Validator;
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
        map.put(4, this::generateL4);
        map.put(5, this::generateL5);
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
            .map(this::createPairWithOtherForL2)
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
            .map(this::createPairWithOtherForL2)
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
            .map(this::createTriplesForL3)
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
            .map(this::createTriplesForL3)
            .flatMap(List::stream)
            .filter(validator::validate)
            .map(mapper::fromTriples)
            .collect(Collectors.toList()))
            .get();
    }

    @SneakyThrows
    public List<Answer> generateL4(final List<Triple> triples) {
        final AnswerMapper mapper = new AnswerMapper(helper);
        final AnswerL4Validator validator = new AnswerL4Validator();

        ForkJoinPool threadPool = new ForkJoinPool(config.getThreads());

        return threadPool.submit(() -> triples.parallelStream()
                .map(this::createTriplesForL4)
                .flatMap(List::stream)
                .filter(validator::validate)
                .map(mapper::fromTriples)
                .collect(Collectors.toList()))
                .get();
    }

    @SneakyThrows
    public List<Answer> generateL5(final List<Triple> triples) {
        final AnswerMapper mapper = new AnswerMapper(helper);
        final AnswerL5Validator validator = new AnswerL5Validator();

        ForkJoinPool threadPool = new ForkJoinPool(config.getThreads());

        return threadPool.submit(() -> triples.parallelStream()
                .map(this::createTriplesForL5)
                .flatMap(List::stream)
                .filter(validator::validate)
                .map(mapper::fromTriples)
                .collect(Collectors.toList()))
                .get();
    }


    private List<List<Triple>> createPairWithOtherForL2(final Triple triple1) {
        return triples.stream()
            .map(triple2 -> asList(triple1, triple2))
            .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesForL3(final Triple triple1) {
        return triples.stream()
            .map(triple2 -> createTriplesForL3Part2(triple1, triple2))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesForL3Part2(final Triple triple1, final Triple triple2) {
        return triples.stream()
            .map(triple3 -> asList(triple1, triple2, triple3))
            .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesForL4(final Triple triple1) {
        return triples.stream()
                .map(triple2 -> createTriplesForL4Part2(triple1, triple2))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesForL4Part2(final Triple triple1, final Triple triple2) {
        return triples.stream()
                .map(triple3 -> createTriplesForL4Part3(triple1, triple2, triple3))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesForL4Part3(final Triple triple1, final Triple triple2, final Triple triple3) {
        return triples.stream()
                .map(triple4 -> asList(triple1, triple2, triple3, triple4))
                .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesForL5(final Triple triple1) {
        return triples.stream()
                .map(triple2 -> createTriplesForL4Part2(triple1, triple2))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesForL5Part2(final Triple triple1, final Triple triple2) {
        return triples.stream()
                .map(triple3 -> createTriplesForL4Part3(triple1, triple2, triple3))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesForL5Part3(final Triple triple1, final Triple triple2, final Triple triple3) {
        return triples.stream()
                .map(triple4 -> createTriplesForL5Part4(triple1, triple2, triple3, triple4))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<List<Triple>> createTriplesForL5Part4(final Triple triple1, final Triple triple2, final Triple triple3, final Triple triple4) {
        return triples.stream()
                .map(triple5 -> asList(triple1, triple2, triple3, triple4, triple5))
                .collect(Collectors.toList());
    }
}
