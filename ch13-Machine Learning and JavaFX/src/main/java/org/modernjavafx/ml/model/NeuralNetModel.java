package org.modernjavafx.ml.model;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public interface NeuralNetModel {
    
    public String getId();
    
    public MultiLayerNetwork getModel(int[] inputShape, int numClasses);
    
}