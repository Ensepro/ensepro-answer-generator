package com.ensepro.answer.generator;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Configuration config = Configuration.fromArgs(args);
        log.info("####################################################################################");
        log.info("#### STARTING PROCESS: {}", config);

        final PythonResult pythonResult = JsonUtil.read2(config.getLoadFile(), PythonResult.class);

        List<Triple> triples = new TripleMapper().map(pythonResult.getTriples());
        final Helper helper = pythonResult.getHelper();

        log.info("##### phrase: {} ", pythonResult.getPhrase());
        log.info("####################################################################################");
        final Score score = Score.builder()
            .triples(triples)
            .helper(helper)
            .config(config)
            .build();

        log.info("#### CALCULATING SCORE FOR L1:  size={}", triples.size());
        score.calculate();
        log.info("#### CALCULATING SCORE FOR L1 - DONE");

        final AnswerGenerator answerGenerator = AnswerGenerator.builder()
//            .triples(triples)
            .helper(helper)
            .config(config)
            .build();
        JavaResult.JavaResultBuilder resultBuilder = JavaResult.builder();
        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < config.getLevel(); i++) {
            log.info("### Generating answer for L" + (i + 1));

            final List<Answer> answersGenerated = answerGenerator.generate(i + 1, triples);
            Collections.sort(answersGenerated);

            triples = answersGenerated.stream()
                .limit(config.getUseLnXAnswerToNextLn())
                .map(Answer::getOriginalTriples)
                .flatMap(List::stream)
                .distinct()
                .collect(toList());

            resultBuilder.l_size(answersGenerated.size());
            answers.addAll(answersGenerated);

            log.info("### Generating answer for L" + (i + 1) + " - DONE - size={}", answersGenerated.size());
        }

        Collections.sort(answers);
        answers = answers.stream().limit(config.getResultSize()).collect(toList());

        JsonUtil.save(config.getSaveFile(),
                resultBuilder
                .answers(answers)
                .helper(helper)
                .build()
        );
    }
}
