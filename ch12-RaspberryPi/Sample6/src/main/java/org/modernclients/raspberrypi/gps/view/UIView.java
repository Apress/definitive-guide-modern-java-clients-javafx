package org.modernclients.raspberrypi.gps.view;

import com.airhacks.afterburner.views.FXMLView;

import java.util.ResourceBundle;

public class UIView extends FXMLView {

    public UIView() {
        this.bundle = ResourceBundle.getBundle("/org/modernclients/raspberrypi/gps/view/ui");
    }

}
