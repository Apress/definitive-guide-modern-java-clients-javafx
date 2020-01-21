package chapter5.cssapi;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import java.util.List;

public class WeatherIcon extends Label {

    private static final String STYLE_CLASS       = "weather-icon";
    private static final String WEATHER_PROP_NAME = "-fx-weather";
    private static final String PSEUDO_CLASS_NAME = "dangerous";
    private static PseudoClass DANGEROUS_PSEUDO_CLASS = PseudoClass.getPseudoClass(PSEUDO_CLASS_NAME);

    private static final StyleablePropertyFactory<WeatherIcon> STYLEABLE_PROPERTY_FACTORY =
            new StyleablePropertyFactory<>(Region.getClassCssMetaData());

    private static CssMetaData<WeatherIcon, WeatherType> WEATHER_TYPE_METADATA =
            STYLEABLE_PROPERTY_FACTORY.createEnumCssMetaData(
                    WeatherType.class, WEATHER_PROP_NAME, x -> x.weatherTypeProperty);

    public WeatherIcon() {
        getStyleClass().setAll(STYLE_CLASS);
    }

    public WeatherIcon(WeatherType weatherType ) {
        this();
        setWeather( weatherType);
    }

    private BooleanProperty dangerous = new BooleanPropertyBase(false) {
        public void invalidated() {
            pseudoClassStateChanged(DANGEROUS_PSEUDO_CLASS, get());
        }
        @Override public Object getBean() {
            return WeatherIcon.this;
        }
        @Override public String getName() {
            return PSEUDO_CLASS_NAME;
        }
    };

    private StyleableObjectProperty<WeatherType> weatherTypeProperty = new StyleableObjectProperty<>(WeatherType.SUNNY) {
        @Override
        public CssMetaData<? extends Styleable, WeatherType> getCssMetaData() {
            return WEATHER_TYPE_METADATA;
        }
        @Override
        public Object getBean() {
            return WeatherIcon.this;
        }
        @Override
        public String getName() {
            return WEATHER_PROP_NAME;
        }
        @Override
        protected void invalidated() {
            WeatherType weatherType = get();
            dangerous.set( weatherType.isDangerous());
            setGraphic(weatherType.buildGraphic());
            setText(get().toString());
        }
    };

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return List.of(WEATHER_TYPE_METADATA);
    }

    public WeatherType weatherProperty() {
        return weatherTypeProperty.get();
    }
    public void setWeather(WeatherType weather) {
        this.weatherTypeProperty.set(weather);
    }
    public WeatherType getWeather() {
        return weatherTypeProperty.get();
    }
}

