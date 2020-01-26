package org.modernclients.propertiesandbindings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class BidirectionalBindingExample {
    
    public static void main(String[] args) {
        System.out.println("Constructing two StringProperty objects.");
        
        StringProperty prop1 = new SimpleStringProperty("");
        StringProperty prop2 = new SimpleStringProperty("");
        
        System.out.println("Calling bindBidirectional.");
        prop2.bindBidirectional(prop1);
        
        System.out.println("prop1.isBound() = " + prop1.isBound());
        System.out.println("prop2.isBound() = " + prop2.isBound());
        
        System.out.println("Calling prop1.set(\"prop1 says: Hi!\")");
        prop1.set("prop1 says: Hi!");
        System.out.println("prop2.get() returned: ");
        System.out.println(prop2.get());
        
        System.out.println("Calling prop2.set(prop2.get() + \"\\nprop2 says: Bye!\")");
        prop2.set(prop2.get() + "\nprop2 says: Bye!");
        
        System.out.println("prop1.get() returned:");
        System.out.println(prop1.get());
    }
}
