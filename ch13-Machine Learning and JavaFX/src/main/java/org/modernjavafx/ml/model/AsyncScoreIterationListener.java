package org.modernjavafx.ml.model;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.BaseTrainingListener;

public class AsyncScoreIterationListener extends BaseTrainingListener {
    
    private int printIterations = 10;
    ConcurrentLinkedQueue<Double> queue = new ConcurrentLinkedQueue<>();
    private int iteration;
    
    
    public AsyncScoreIterationListener(BiConsumer<Integer, Double> consumer) {
        this(10, consumer);
    }
    
    public AsyncScoreIterationListener(int printIterations, BiConsumer<Integer, Double> consumer) {
        this.printIterations = printIterations;
        new Thread(() -> {
            while(true) {
                if(!queue.isEmpty()) {
                    consumer.accept(iteration, queue.poll());
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void iterationDone(Model model, int iteration, int epoch) {
        this.iteration = iteration;
        if (printIterations <= 0)
            printIterations = 1;
        if (iteration % printIterations == 0) {
            double score = model.score();
            queue.add(score);
        }
    }
    

}
