package com.ensepro.answer.generator.answer.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.ensepro.answer.generator.answer.producer.AnswerProducer;
import com.ensepro.answer.generator.data.answer.Answer;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnswerConsumer extends Thread {

    private final AnswerProducer producer;
    private final ExecutorService executorService;
    private final List<Answer> answers = new ArrayList<>();

    @Builder
    public AnswerConsumer(final String name, final AnswerProducer producer, final ExecutorService executorService) {
        super(name);
        this.producer = producer;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        try {
            log.info("producer.join()");
            producer.join();
            log.info("executing callables (" + producer.getCallables().size() + ")");
            executorService.invokeAll(producer.getCallables())
                .stream()
                .map(this::mapToAnswer)
                .filter(Objects::nonNull)
                .forEach(answers::add);
            log.info("callables executed");

            executorService.shutdown();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    private Answer mapToAnswer(Future<Answer> answerFuture) {
        try {
            return answerFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /*
     Executes a "this.join()" to wait for this thread to die so all the values are calculated.
     */
    public List<Answer> getAnswers() {
        try {
            log.info("getAnswer() - waiting to finish");
            this.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        log.info("getAnswer() - returning");
        return this.answers;
    }
}
