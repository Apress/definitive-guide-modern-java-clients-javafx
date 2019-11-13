package org.modernclients.raspberrypi.gps.service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.serial.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.modernclients.raspberrypi.gps.model.GPSPosition;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class GPSService {

    private static final Logger logger = Logger.getLogger(GPSService.class.getName());

    @Inject
    private GPSPosition gpsPosition;

    private Serial serial;
    private GpioController gpio;
    private NMEAParser nmea;
    private StringBuilder gpsOutput;

    private final StringProperty line = new SimpleStringProperty();

    @PostConstruct
    private void postConstruct() {
        if (! "monocle".equals(System.getProperty("embedded"))) {
            return;
        }

        nmea = new NMEAParser(gpsPosition);
        gpsOutput = new StringBuilder();

        gpio = GpioFactory.getInstance();
        serial = SerialFactory.createInstance();
        serial.addListener(event -> {
            try {
                String s = event.getString(Charset.defaultCharset())
                        .replaceAll("\n", "")
                        .replaceAll("\r", "");
                gpsOutput.append(s);
                processReading();
            } catch (IOException e) {
                logger.warning("Error processing event " + event);
                e.printStackTrace();
            }
        });

        SerialConfig config = new SerialConfig();
        try {
            String defaultPort = SerialPort.getDefaultPort();
            logger.info("Connecting to default port = " + defaultPort);
            config.device(defaultPort)
                    .baud(Baud._9600)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);

            serial.open(config);
            logger.info("Connected: " + serial.isOpen());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processReading() {
        if (gpsOutput == null || gpsOutput.toString().isEmpty()) {
            return;
        }
        
        String reading = gpsOutput.toString().trim();

        if (! reading.contains("$")) {
            return;
        }
        
        String[] split = reading.split("\\$");
        
        for (int i = 0; i < split.length - 1; i++) {
            String line = "$" + split[i];
            gpsOutput.delete(0 , line.length());
            if (line.length() > 1) {
                logger.fine("GPS: " + line);
                Platform.runLater(() -> {
                    nmea.parse(line);
                    this.line.set(line);
                });
            }
            if (i == split.length - 2) {
                gpsOutput.insert(0, "$");
            }
        }
    }

    public final StringProperty lineProperty() {
        return line;
    }

    public void stop() {
        logger.info("Stopping Serial and GPIO");
        if (serial != null) {
            try {
                serial.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (gpio != null) {
            gpio.shutdown();
        }
    }
}
