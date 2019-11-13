package org.modernclients.raspberrypi.gps.service;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.Property;
import org.modernclients.raspberrypi.gps.model.GPSPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

public class NMEAParser {

    private static final Logger logger = Logger.getLogger(NMEAParser.class.getName());

    interface SentenceParser {
        boolean parse(String [] tokens, GPSPosition position);
    }

    private static final Map<String, SentenceParser> sentenceParsers = new HashMap<>();

    private final GPSPosition position;

    public NMEAParser(GPSPosition position) {
        this.position = position;
        sentenceParsers.put("GPGGA", new GPGGA());
        sentenceParsers.put("GPGGL", new GPGGL());
        sentenceParsers.put("GPRMC", new GPRMC());
        sentenceParsers.put("GPRMZ", new GPRMZ());
        sentenceParsers.put("GPVTG", new GPVTG());
    }

    public GPSPosition parse(final String line) {
        if (line.startsWith("$") && checksum(line)) {
            String[] tokens = line.substring(1).split(",");
            String type = tokens[0];
            if (sentenceParsers.containsKey(type)) {
                sentenceParsers.get(type).parse(tokens, position);
            }
            position.updatefix();
        }
        return position;
    }

    // parsers
    class GPGGA implements SentenceParser {
        @Override
        public boolean parse(String [] tokens, GPSPosition position) {
            parseCoordinate(tokens[2], tokens[3], "S", position.latitudeProperty());
            parseCoordinate(tokens[4], tokens[5], "W", position.longitudeProperty());
            doParse(tokens[1], Float::parseFloat, position.timeProperty());
            doParse(tokens[6], Integer::parseInt, position.qualityProperty());
            doParse(tokens[7], Integer::parseInt, position.satellitesProperty());
            return doParse(tokens[9], Float::parseFloat, position.altitudeProperty());
        }
    }

    class GPGGL implements SentenceParser {
        @Override
        public boolean parse(String [] tokens, GPSPosition position) {
            parseCoordinate(tokens[1], tokens[2], "S", position.latitudeProperty());
            parseCoordinate(tokens[3], tokens[4], "W", position.longitudeProperty());
            return doParse(tokens[5], Float::parseFloat, position.timeProperty());
        }
    }

    class GPRMC implements SentenceParser {
        @Override
        public boolean parse(String [] tokens, GPSPosition position) {
            doParse(tokens[1], Float::parseFloat, position.timeProperty());
            parseCoordinate(tokens[3], tokens[4], "S", position.latitudeProperty());
            parseCoordinate(tokens[5], tokens[6], "W", position.longitudeProperty());
            doParse(tokens[7], Float::parseFloat, position.velocityProperty());
            return doParse(tokens[8], Float::parseFloat, position.directionProperty());
        }
    }

    class GPVTG implements SentenceParser {
        @Override
        public boolean parse(String [] tokens, GPSPosition position) {
            return doParse(tokens[3], Float::parseFloat, position.directionProperty());
        }
    }

    class GPRMZ implements SentenceParser {
        @Override
        public boolean parse(String [] tokens, GPSPosition position) {
            return doParse(tokens[1], Float::parseFloat, position.altitudeProperty());
        }
    }

    private boolean parseCoordinate(String token, String direction, String defaultDirection, FloatProperty property) {
        if (token == null || token.isEmpty() || direction == null || direction.isEmpty()) {
            return false;
        }
        int minutesPosition = token.indexOf('.') - 2;
        if (minutesPosition < 0) {
            return false;
        }
        float minutes = Float.parseFloat(token.substring(minutesPosition));
        float decimalDegrees = Float.parseFloat(token.substring(minutesPosition)) / 60.0f;

        float degree = Float.parseFloat(token) - minutes;
        float wholeDegrees = (int) degree / 100;

        float coordinateDegrees = wholeDegrees + decimalDegrees;
        if (direction.startsWith(defaultDirection)) {
            coordinateDegrees = -coordinateDegrees;
        }
        property.setValue(coordinateDegrees);
        return true;
    }

    private <T> boolean doParse(String token, Function<String, T> operator, Property<T> property) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            property.setValue(operator.apply(token));
            return true;
        } catch (NumberFormatException nfe) { }
        return false;
    }

    private static boolean checksum(String line) {
        if (line == null || ! line.contains("$") || ! line.contains("*")) {
            return false;
        }
        String sentence = line.substring(1, line.lastIndexOf("*"));
        String lineChecksum = "0x" + line.substring(line.lastIndexOf("*") + 1);

        int c = 0;
        for (char s : sentence.toCharArray()) {
            c ^= s;
        }

        String hex = String.format("0x%02X", c);
        boolean result = hex.equals(lineChecksum);
        if (! result) {
            logger.warning("There was an error in the checksum of " + line);
        }
        return result;
    }
}
