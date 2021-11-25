package com.ensepro.answer.generator;

import com.ensepro.answer.generator.answer.Score;
import com.ensepro.answer.generator.configuration.Configuration;
import com.ensepro.answer.generator.data.Answer;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.JavaResult;
import com.ensepro.answer.generator.data.PythonResult;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.mapper.TripleMapper;
import com.ensepro.answer.generator.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.stream.Collectors.toList;

@Slf4j
public class Main {

    public static void main(String[] args)
            throws IOException, ExecutionException, InterruptedException {

        Configuration config = Configuration.fromArgs(args);
        final Duration timeout = Duration.ofSeconds(120);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        final Future<Boolean> handler = executor.submit(() -> generate(args, config));

        try {
            handler.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            handler.cancel(true);
            final Map<String, Object> stats = new HashMap<>();
            stats.put("outOutMemory", false);
            stats.put("exception", "Timeout");
            stats.put("detail", "Timeout");
            JsonUtil.save(
                    config.getSaveFile(),
                    JavaResult.builder()
                            .answers(Collections.emptyList())
                            .stats(stats)
                            .build()
            );
        }
        executor.shutdownNow();
    }

    private static boolean generate(String[] args, Configuration config) throws IOException {
        if (args.length > 0 && args[0].equals("version")) {
            System.out.println("slm1-all4");
            System.exit(0);
        }

        log.info("####################################################################################");
        log.info("#### STARTING PROCESS: {}", config);

        final PythonResult pythonResult = JsonUtil.read2(config.getLoadFile(), PythonResult.class);

        List<Triple> triples = new TripleMapper().map(pythonResult.getTriples());
        final Helper helper = pythonResult.getHelper();

        log.info("##### phrase: {} ", pythonResult.getPhrase());
        log.info("####################################################################################");

        try {
            final long startTime = System.nanoTime();

            final Score score = Score.builder().triples(triples).helper(helper).config(config).build();

            log.info("#### CALCULATING SCORE FOR L1:  size={}", triples.size());
            score.calculate();
            log.info("#### CALCULATING SCORE FOR L1 - DONE");

            final AnswerGenerator answerGenerator =
                    AnswerGenerator.builder()
                            //            .triples(triples)
                            .helper(helper)
                            .config(config)
                            .build();
            JavaResult.JavaResultBuilder resultBuilder = JavaResult.builder();
            List<Answer> answers = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                log.info("### Generating answer for L" + (i + 1) + " - l_size={}", triples.size());
                resultBuilder.l_size(triples.size());
                final List<Answer> answersGenerated = answerGenerator.generate(i + 1, triples);
                Collections.sort(answersGenerated);
                answers.addAll(answersGenerated);
                resultBuilder.answer_size(answersGenerated.size());

                if (!config.getSlm1OnlyL1() || i == 0) {
                    log.info("applying 'fator sl1m'");
                    triples =
                            answersGenerated.stream()
                                    .limit(config.getSlm1Factor())
                                    .map(Answer::getOriginalTriples)
                                    .flatMap(List::stream)
                                    .distinct()
                                    .collect(toList());
                }
                log.info("### Generating answer for L" + (i + 1) + " - DONE - a_size={}, l_size={}", answersGenerated.size(), triples.size());
            }

            Collections.sort(answers);
            answers = answers.stream().limit(config.getResultSize()).collect(toList());

            final long endTime = System.nanoTime();
            JsonUtil.save(
                    config.getSaveFile(),
                    resultBuilder
                            .answers(answers)
//                            .helper(helper)
                            .nanoSeconds(endTime - startTime)
                            .stats(Collections.emptyMap())
                            .build()
            );

        } catch (OutOfMemoryError ex) {
            log.info("ERROR OUT OF MEMORY");
            final Map<String, Object> stats = new HashMap<>();
            stats.put("outOutMemory", true);
            stats.put("exception", ex.getCause().getClass());
            stats.put("detail", ex.getMessage());
            JavaResult.JavaResultBuilder resultBuilder = JavaResult.builder();
            JsonUtil.save(
                    config.getSaveFile(),
                    resultBuilder
                            .answers(Collections.emptyList())
//                            .helper(helper)
                            .stats(stats)
                            .build()
            );
        } catch (Exception ex) {
            log.info("GENERIC ERROR");
        }
        return true;
    }

}
