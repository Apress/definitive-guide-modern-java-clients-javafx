package org.modernclient;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class HelloNd4j {

    public static void main(String[] args) {
        INDArray a = Nd4j.zeros(3,5);
        System.out.println("Matrix a has 3 rows and 5 columns:\n"+a);
        System.out.println("++++++++++++++++++++++++++++++\n");

        INDArray b = Nd4j.create(new double[] {0.,1.,2.,3.,4.,5.}, new int[] {2,3});
        INDArray c = Nd4j.create(new double[] {2.,-1.,3.}, new int[] {3,1});
        System.out.println("Matrix b has 2 rows ad 3 columns:\n"+b);
        System.out.println("++++++++++++++++++++++++++++++\n");
        System.out.println("Vector c has 3 elements:\n"+c);
        System.out.println("++++++++++++++++++++++++++++++\n");

        INDArray d = b.mmul(c);
        System.out.println("matrix product of b x c  =\n"+d);
        System.out.println("++++++++++++++++++++++++++++++\n");
    }

}
