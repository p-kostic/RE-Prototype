package nl.reprototyping.util;


import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import javax.ejb.Singleton;
import java.util.Calendar;


@Singleton
public class ThemeService {
    private static final Location                LOCATION = new Location(52.101851, 5.099695);
    private static final String                  TIMEZONE = "Netherlands/Amsterdam";
    private final        SunriseSunsetCalculator calculator;

    public ThemeService() {
        calculator = new SunriseSunsetCalculator(LOCATION, TIMEZONE);
    }

    public Theme getTheme() {
        Calendar calendar = Calendar.getInstance();
        Calendar sunset = calculator.getCivilSunsetCalendarForDate(calendar);
        Calendar sunrise = calculator.getCivilSunriseCalendarForDate(calendar);

        if (calendar.before(sunset) && calendar.after(sunrise)) {
            return Theme.LIGHT;
        } else {
            return Theme.DARK;
        }
    }
}
