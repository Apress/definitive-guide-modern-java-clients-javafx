package org.modernclients.bridgefx.fxinswing3d;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {

    private static final Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
    private static final Rotate rotateY = new Rotate(-20, Rotate.Y_AXIS);
    private static final Rotate rotateZ = new Rotate(-20, Rotate.Z_AXIS);
    private static final Translate translateZ = new Translate(0, 0, -100);
    
    public static void main(String[] args) {
        // Do everything on the event thread to be Swing compliant
        SwingUtilities.invokeLater(() -> {
            var frame = new JFrame("JavaFX 11 3D integrated in Swing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            var jfxPanel = new JFXPanel();            
            var camera = createCamera();
            var box = new Box(10, 10, 10);
            var view = new Group(box, camera);
            var scene = new Scene(view, 640, 480);
            scene.setCamera(camera);            
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
            Platform.runLater(() -> animate());
        });
    }

    private static Camera createCamera() {
        Camera answer = new PerspectiveCamera(true);
        answer.getTransforms().addAll(rotateX, rotateY, rotateZ, translateZ);
        return answer;
    }

    private static void animate() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(translateZ.zProperty(), -20),
                        new KeyValue(rotateX.angleProperty(), 90),
                        new KeyValue(rotateY.angleProperty(), 90),
                        new KeyValue(rotateZ.angleProperty(), 90)),
                new KeyFrame(Duration.seconds(5),
                        new KeyValue(translateZ.zProperty(), -80),
                        new KeyValue(rotateX.angleProperty(), -90),
                        new KeyValue(rotateY.angleProperty(), -90),
                        new KeyValue(rotateZ.angleProperty(), -90))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }    
    
}
