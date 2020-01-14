package org.modernjavafx.ml.model;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

public class DL4JHelper {
    
    private static final DataNormalization SCALER = new ImagePreProcessingScaler(0, 1);
    
    
    private DL4JHelper() {
        
    }
    
    public static DataSetIterator createIterator(File dir, int width, int height, int channels, int batchSize, int seed)
            throws IOException {
        FileSplit imagesSplit = new FileSplit(dir, NativeImageLoader.ALLOWED_FORMATS, new Random(seed));
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator(); 
        ImageRecordReader imagesRecordReader = new ImageRecordReader(height, width, channels, labelMaker);
        imagesRecordReader.initialize(imagesSplit);
        int numClasses = imagesRecordReader.getLabels().size();
        DataSetIterator imagesIter = new RecordReaderDataSetIterator(imagesRecordReader, batchSize, 1, numClasses);
        SCALER.fit(imagesIter);
        imagesIter.setPreProcessor(SCALER);
        return imagesIter;
    }

}
