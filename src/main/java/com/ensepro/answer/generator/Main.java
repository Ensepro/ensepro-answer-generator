package com.ensepro.answer.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public static void main(String[] args) throws IOException {
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

        score.calculate();

        AnswerGenerator answerGenerator = AnswerGenerator.builder()
            .triples(triples)
            .helper(helper)
            .build();

        List<Answer> answers = new ArrayList<>();
        answers.addAll(answerGenerator.generateL1());
        answers.addAll(answerGenerator.generateL2());

        Collections.sort(answers);

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
