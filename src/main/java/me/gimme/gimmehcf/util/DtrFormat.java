package me.gimme.gimmehcf.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DtrFormat {

    public static String oneDecimal(double dtr) {
        DecimalFormat df = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH));
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        df.setRoundingMode(RoundingMode.HALF_UP);

        return df.format(dtr);
    }

}
