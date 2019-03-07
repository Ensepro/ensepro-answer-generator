package com.ensepro.answer.generator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.ensepro.answer.generator.configuration.Configuration;
import com.ensepro.answer.generator.data.Answer;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.JavaResult;
import com.ensepro.answer.generator.data.PythonResult;
import com.ensepro.answer.generator.mapper.TripleMapper;
import com.ensepro.answer.generator.utils.JsonUtil;
import com.ensepro.answer.generator.answer.Score;
import com.ensepro.answer.generator.data.Triple;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Configuration config = Configuration.fromArgs(args);
        log.info("####################################################################################");
        log.info("#### STARTING PROCESS: {}", config);
        log.info("####################################################################################");

        PythonResult pythonResult = JsonUtil.read2(config.getLoadFile(), PythonResult.class);

        List<Triple> triples = new TripleMapper().map(pythonResult.getTriples());
        Helper helper = pythonResult.getHelper();

        Score score = Score.builder()
            .triples(triples)
            .helper(helper)
            .config(config)
            .build();

        log.info("#### CALCULATING SCORE FOR L1:  size={}", triples.size());
        score.calculate();
        log.info("#### CALCULATING SCORE FOR L1 - DONE", triples.size());

        AnswerGenerator answerGenerator = AnswerGenerator.builder()
            .triples(triples)
            .helper(helper)
            .config(config)
            .build();

        log.info("### Generating answer for L1");
        List<Answer> answersL1 = answerGenerator.generateL1();
        log.info("### Generating answer for L1 - DONE - size={}", answersL1.size());

        List<Answer> answers = answersL1;

        if (config.getLevel() > 1) {
            log.info("### Generating answer for L2");
            List<Answer> answersL2 = answerGenerator.generateL2();
            log.info("### Generating answer for L2 - DONE - size={}", answersL2.size());
            answers.addAll(answersL2);
        }

        log.info("### Sorting answers");
        Collections.sort(answers);
        log.info("### Sorting answers");

        answers = answers.stream()
            .limit(config.getResultSize())
            .collect(Collectors.toList());

        JsonUtil.save(config.getSaveFile(),
            JavaResult.builder()
                .answers(answers)
                .helper(helper)
                .build()
        );
    }
}
