package org.modernclients.bridgefx.webviewinswing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javafx.application.Platform;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Do everything on the event thread to be Swing compliant
        SwingUtilities.invokeLater(() -> {
            var frame = new JFrame("JavaFX 11 WebView integrated in Swing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            var jfxPanel = new JFXPanel();

            var panel = new JPanel(new BorderLayout());
            panel.add(new JLabel("Hello Swing North"), BorderLayout.NORTH);
            panel.add(new JLabel("Hello Swing South"), BorderLayout.SOUTH);
            
            Platform.runLater(() -> {
                var webView = new WebView();
                var scene = new Scene(webView);
                webView.getEngine().load("https://openjfx.io/");
                jfxPanel.setScene(scene);
                jfxPanel.setPreferredSize(new Dimension(400,600));
                SwingUtilities.invokeLater(() -> {
                    panel.add(jfxPanel, BorderLayout.CENTER);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                });
            });
            frame.setContentPane(panel);
            
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}
