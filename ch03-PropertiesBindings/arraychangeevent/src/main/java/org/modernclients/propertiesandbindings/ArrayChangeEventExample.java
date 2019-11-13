package org.modernclients.propertiesandbindings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableIntegerArray;

public class ArrayChangeEventExample {
    public static void main(String[] args) {
        final ObservableIntegerArray ints =
                FXCollections.observableIntegerArray(10, 20);
        ints.addListener((array,
                          sizeChanged, from, to) -> {
            StringBuilder sb =
                    new StringBuilder("\tObservable Array = ")
                            .append(array)
                            .append("\n")
                            .append("\t\tsizeChanged = ")
                            .append(sizeChanged).append("\n")
                            .append("\t\tfrom = ")
                            .append(from).append("\n")
                            .append("\t\tto = ")
                            .append(to)
                            .append("\n");
            System.out.println(sb.toString());
        });

        ints.ensureCapacity(20);

        System.out.println("Calling addAll(30, 40):");
        ints.addAll(30, 40);

        final int[] src = {50, 60, 70};
        System.out.println("Calling addAll(src, 1, 2):");
        ints.addAll(src, 1, 2);

        System.out.println("Calling set(0, src, 0, 1):");
        ints.set(0, src, 0, 1);

        System.out.println("Calling setAll(src):");
        ints.setAll(src);

        ints.trimToSize();

        final ObservableIntegerArray ints2 =
                FXCollections.observableIntegerArray();
        ints2.resize(ints.size());

        System.out.println("Calling copyTo(0, ints2," +
                " 0, ints.size()):");
        ints.copyTo(0, ints2, 0, ints.size());

        System.out.println("\tDestination = " + ints2);
    }
}

