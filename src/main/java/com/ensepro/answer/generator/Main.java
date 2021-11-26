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
import java.util.stream.Collectors;

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

    public static boolean generate(String[] args, Configuration config) throws IOException, ExecutionException, InterruptedException {
        if (args.length > 0 && args[0].equals("version")) {
            System.out.println("base");
            System.exit(0);
        }
        log.info("####################################################################################");
        log.info("#### STARTING PROCESS: {}", config);
        try {
            JavaResult.JavaResultBuilder resultBuilder = JavaResult.builder();
            final PythonResult pythonResult = JsonUtil.read2(config.getLoadFile(), PythonResult.class);

            final List<Triple> triples = new TripleMapper().map(pythonResult.getTriples());
            final Helper helper = pythonResult.getHelper();

            log.info("##### phrase: {} ", pythonResult.getPhrase());
            log.info("####################################################################################");

            final long startTime = System.nanoTime();

            final Score score = Score.builder()
                    .triples(triples)
                    .helper(helper)
                    .config(config)
                    .build();

            log.info("#### CALCULATING SCORE FOR L1:  size={}", triples.size());
            score.calculate();
            log.info("#### CALCULATING SCORE FOR L1 - DONE ");

            final AnswerGenerator answerGenerator = AnswerGenerator.builder()
                    .triples(triples)
                    .helper(helper)
                    .config(config)
                    .build();

            log.info("### Generating answer for L1");
            final List<Answer> answersL1 = answerGenerator.generateL1();
            log.info("### Generating answer for L1 - DONE - size={}", answersL1.size());
            resultBuilder.l_size(triples.size());
            resultBuilder.answer_size(answersL1.size());

            List<Answer> answers = answersL1;
            if (config.getLevel() > 1) {
                log.info("### Generating answer for L2");
                final List<Answer> answersL2 = answerGenerator.generateL2();
                log.info("### Generating answer for L2 - DONE - size={}", answersL2.size());
                answers.addAll(answersL2);
                resultBuilder.l_size(triples.size());
                resultBuilder.answer_size(answersL2.size());
            }

            log.info("### Sorting answers");
            Collections.sort(answers);
            log.info("### Sorting answers");

            answers = answers.stream()
                    .limit(config.getResultSize())
                    .collect(Collectors.toList());

            final long endTime = System.nanoTime();

            JsonUtil.save(config.getSaveFile(),
                    resultBuilder
                            .answers(answers)
//                            .helper(helper)
                            .stats(Collections.emptyMap())
                            .nanoSeconds(endTime - startTime)
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
                            .stats(stats)
                            .build()
            );
        } catch (Exception ex) {
            log.error("GENERIC ERROR", ex);
        }
        return true;
    }
}
