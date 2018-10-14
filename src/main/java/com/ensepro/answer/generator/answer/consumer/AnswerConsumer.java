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
            System.out.println(getName() + ": producer.join()");
            producer.join();
            System.out.println(getName() + ": executing callables (" + producer.getCallables().size() + ")");
            executorService.invokeAll(producer.getCallables())
                .stream()
                .map(this::mapToAnswer)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(answers::add);
            System.out.println(getName() + ": callables executed");
        } catch (InterruptedException e) {
            //TODO remove
            e.printStackTrace();
        }
    }

    private Answer mapToAnswer(Future<Answer> answerFuture) {
        try {
            answerFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            //TODO remove
            e.printStackTrace();
        }
        return null;
    }

    /*
     Executes a "this.join()" to wait for this thread to die so all the values are calculated.
     */
    public List<Answer> getAnswers() {
        try {
            System.out.println(getName() + ": getAnswer() - waiting to finish");
            this.join();
        } catch (InterruptedException e) {
            //TODO remove
            e.printStackTrace();
        }
        System.out.println(getName() + ": getAnswer() - returning");
        return this.answers;
    }
}
