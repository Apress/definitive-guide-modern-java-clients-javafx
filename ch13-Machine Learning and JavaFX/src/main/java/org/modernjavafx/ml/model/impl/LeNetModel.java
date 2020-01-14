package org.modernjavafx.ml.model.impl;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.zoo.model.LeNet;
import org.modernjavafx.ml.model.NeuralNetModel;

public class LeNetModel implements NeuralNetModel {

    @Override
    public String getId() {
        return "LeNet";
    }

    @Override
    public MultiLayerNetwork getModel(int[] inputShape, int numClasses) {
        LeNet leNet = LeNet.builder().inputShape(new int[] { 1, 28, 28 }).numClasses(numClasses).build();
        return  (MultiLayerNetwork) leNet.init();
    }
    
}