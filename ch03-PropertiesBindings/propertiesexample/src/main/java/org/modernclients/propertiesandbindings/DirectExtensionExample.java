package org.modernclients.propertiesandbindings;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


public class DirectExtensionExample {
    public static void main(String[] args) {
        System.out.println("Constructing x with value 2.0.");
        final DoubleProperty x = new SimpleDoubleProperty(null, "x", 2.0);
        
        System.out.println("Constructing y with value 3.0");
        final DoubleProperty y = new SimpleDoubleProperty(null, "y", 3.0);
        
        System.out.println("Creating binding area with dependencies x and y.");
        DoubleBinding area = new DoubleBinding() {
            {
                super.bind(x, y);
            }

            @Override
            protected double computeValue() {
                System.out.println("computeValue() is called.");
                return x.get() * y.get();
            }
        };
        
        System.out.println("area.get() = " + area.get());
        
        System.out.println("area.get() = " + area.get());

        System.out.println("Setting x to 5");
        x.set(5);
        System.out.println("Setting y to 7");
        y.set(7);
        
        System.out.println("area.get() = " + area.get());
    }
}
