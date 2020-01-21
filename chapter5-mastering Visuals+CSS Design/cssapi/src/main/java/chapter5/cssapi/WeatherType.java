package chapter5.cssapi;

import javafx.scene.text.Text;
public enum WeatherType {
    SUNNY("\uf00d", false),
    CLOUDY("\uf013", false),
    RAIN("\uf019", false),
    THUNDERSTORM("\uf033", true);
    private final boolean dangerous;
    private final String c;
    WeatherType(String c, boolean dangerous) {
        this.c = c;
        this.dangerous = dangerous;
    }
    public boolean isDangerous() {
        return dangerous;
    }
    Text buildGraphic() {
        Text text = new Text(c);
        text.setStyle("-fx-font-family: 'Weather Icons Regular'; -fx-font-size: 25;");
        return text;
    }
}
