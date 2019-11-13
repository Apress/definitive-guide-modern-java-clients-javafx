package org.modernclients.raspberrypi.gps.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GPSPosition {

    // time
    private final FloatProperty time = new SimpleFloatProperty(this, "time");
    public final FloatProperty timeProperty() {
       return time;
    }
    public final float getTime() {
       return time.get();
    }
    public final void setTime(float value) {
        time.set(value);
    }

    // latitude
    private final FloatProperty latitude = new SimpleFloatProperty(this, "latitude");
    public final FloatProperty latitudeProperty() {
       return latitude;
    }
    public final float getLatitude() {
       return latitude.get();
    }
    public final void setLatitude(float value) {
        latitude.set(value);
    }

    // longitude
    private final FloatProperty longitude = new SimpleFloatProperty(this, "longitude");
    public final FloatProperty longitudeProperty() {
       return longitude;
    }
    public final float getLongitude() {
       return longitude.get();
    }
    public final void setLongitude(float value) {
        longitude.set(value);
    }

    // direction
    private final FloatProperty direction = new SimpleFloatProperty(this, "direction");
    public final FloatProperty directionProperty() {
       return direction;
    }
    public final float getDirection() {
       return direction.get();
    }
    public final void setDirection(float value) {
        direction.set(value);
    }

    // altitude
    private final FloatProperty altitude = new SimpleFloatProperty(this, "altitude");
    public final FloatProperty altitudeProperty() {
       return altitude;
    }
    public final float getAltitude() {
       return altitude.get();
    }
    public final void setAltitude(Float value) {
        altitude.set(value);
    }

    // velocity
    private final FloatProperty velocity = new SimpleFloatProperty(this, "velocity");
    public final FloatProperty velocityProperty() {
       return velocity;
    }
    public final float getVelocity() {
       return velocity.get();
    }
    public final void setVelocity(float value) {
        velocity.set(value);
    }

    // satellites
    private final IntegerProperty satellites = new SimpleIntegerProperty(this, "satellites");
    public final IntegerProperty satellitesProperty() { return satellites; }
    public final int getSatellites() { return satellites.get(); }
    public final void setSatellites(int value) { satellites.set(value); }

    // quality
    private final IntegerProperty quality = new SimpleIntegerProperty(this, "quality");
    public final IntegerProperty qualityProperty() {
       return quality;
    }
    public final int getQuality() {
       return quality.get();
    }
    public final void setQuality(int value) {
        quality.set(value);
    }

    // fixed
    private final BooleanProperty fixed = new SimpleBooleanProperty(this, "fixed");
    public final BooleanProperty fixedProperty() {
       return fixed;
    }
    public final boolean isFixed() {
       return fixed.get();
    }
    public final void setFixed(boolean value) {
        fixed.set(value);
    }

    public void updatefix() {
        fixed.set(quality.get() > 0);
    }

    @Override
    public String toString() {
        return "GPSPosition{" +
                "time=" + time.get() +
                ", latitude=" + latitude.get() +
                ", longitude=" + longitude.get() +
                ", direction=" + direction.get() +
                ", altitude=" + altitude.get() +
                ", velocity=" + velocity.get() +
                ", quality=" + quality.get() +
                ", satellites =" + satellites.get() +
                ", fixed=" + fixed.get() +
                '}';
    }
}
