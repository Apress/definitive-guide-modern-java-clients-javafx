package chapter5.applying;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ApplyingStyles extends Application {
    private Label label = new Label("Stylized label");
    // Simplistic implementation of numeric field
    private TextField widthField = new TextField("200") {
        @Override
        public void replaceText(int start, int end, String text) {
            if (text.matches("[0-9]*")) {
                super.replaceText(start, end, text);
            }
        }
        @Override
        public void replaceSelection(String replacement) {
            if (replacement.matches("[0-9]*")) {
                super.replaceSelection(replacement);
            }
        }
    };
    private void updateLabelStyle() {
        label.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10;" +
                        "-fx-pref-width: " + widthField.getText() + "px;"
        );
    }
    @Override
    public void start(Stage primaryStage) {
        updateLabelStyle();
        widthField.setOnAction( e -> updateLabelStyle());
        VBox root = new VBox(10, label, widthField);
        root.setStyle(
                "-fx-background-color: lightblue;" +
                "-fx-padding: 20px;");
        Scene scene = new Scene( root, 250, 100 );
        primaryStage.setTitle("My first CSS application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
