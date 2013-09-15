
package com.doex.demo.phone;

import android.content.Context;
import android.telephony.SignalStrength;

public class RadioMonitorAnalysis {
    public static final int RADIO_LEVEL_GREEN = 1;
    public static final int RADIO_LEVEL_YELLOW = 2;
    public static final int RADIO_LEVEL_RED = 3;
    public static final int RADIO_LEVEL_UNKNOW = 4;
    public static final int WAIT_CALL_TIME = 3;

    public static int getGsmLevel(int asu) {
        int level = RADIO_LEVEL_RED;
        if (asu <= 2 || asu == 99)
            level = RADIO_LEVEL_UNKNOW;
        else if (asu >= 12)
            level = RADIO_LEVEL_GREEN;
        else if (asu >= 8)
            level = RADIO_LEVEL_YELLOW;
        else if (asu >= 3)
            level = RADIO_LEVEL_RED;
        return level;
    }

    public static int getCdmaLevel(SignalStrength signalStrength) {
        final int cdmaDbm = signalStrength.getCdmaDbm();
        final int cdmaEcio = signalStrength.getCdmaEcio();
        int levelDbm;
        int levelEcio;

        if (cdmaDbm >= -75)
            levelDbm = RADIO_LEVEL_GREEN;
        else if (cdmaDbm >= -85)
            levelDbm = RADIO_LEVEL_YELLOW;
        else if (cdmaDbm >= -95)
            levelDbm = RADIO_LEVEL_RED;
        else if (cdmaDbm >= -100)
            levelDbm = RADIO_LEVEL_RED;
        else
            levelDbm = RADIO_LEVEL_UNKNOW;

        // Ec/Io are in dB*10
        if (cdmaEcio >= -90)
            levelEcio = RADIO_LEVEL_GREEN;
        else if (cdmaEcio >= -110)
            levelEcio = RADIO_LEVEL_YELLOW;
        else if (cdmaEcio >= -130)
            levelEcio = RADIO_LEVEL_RED;
        else if (cdmaEcio >= -150)
            levelEcio = RADIO_LEVEL_RED;
        else
            levelEcio = RADIO_LEVEL_UNKNOW;

        int level = (levelDbm < levelEcio) ? levelDbm : levelEcio;
        return level;
    }

    public static int getRadioLevel(SignalStrength signalStrength) {
        if (signalStrength.isGsm()) {
            return getGsmLevel(signalStrength.getGsmSignalStrength());
        } else {
            return getCdmaLevel(signalStrength);
        }
    }

}
