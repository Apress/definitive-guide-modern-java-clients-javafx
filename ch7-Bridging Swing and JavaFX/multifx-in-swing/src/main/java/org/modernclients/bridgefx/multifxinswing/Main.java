package org.modernclients.bridgefx.multifxinswing;

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
            var frame = new JFrame("JavaFX 11 integrated in Swing (multiple)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            var northJfxPanel = new JFXPanel();            
            var northButton = new Button("Hello FX North");
            var northScene = new Scene(northButton);
            northJfxPanel.setScene(northScene);
            northJfxPanel.setPreferredSize(new Dimension(200,50));
            var southJfxPanel = new JFXPanel();            
            var southButton = new Button("Hello FX South");
            var southScene = new Scene(southButton);
            southJfxPanel.setScene(southScene);
            southJfxPanel.setPreferredSize(new Dimension(200,50));
            var panel = new JPanel(new BorderLayout());
            panel.add(northJfxPanel, BorderLayout.NORTH);
            panel.add(southJfxPanel, BorderLayout.SOUTH);
            
            panel.add(new JLabel("Hello Swing"), BorderLayout.CENTER);
            frame.setContentPane(panel);
            
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}
