package org.modernclients.bridgefx.fxinswing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Do everything on the event thread to be Swing compliant
        SwingUtilities.invokeLater(() -> {
            var frame = new JFrame("JavaFX 11 integrated in Swing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            var jfxPanel = new JFXPanel();            
            var button = new Button("Hello FX");
            var scene = new Scene(button);
            jfxPanel.setScene(scene);
            jfxPanel.setPreferredSize(new Dimension(200,100));
            var panel = new JPanel(new BorderLayout());
            panel.add(new JLabel("Hello Swing North"), BorderLayout.NORTH);
            panel.add(new JLabel("Hello Swing South"), BorderLayout.SOUTH);
            
            panel.add(jfxPanel, BorderLayout.CENTER);
            frame.setContentPane(panel);
            
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}
