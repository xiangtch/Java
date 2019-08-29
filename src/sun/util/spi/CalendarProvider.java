package sun.util.spi;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.spi.LocaleServiceProvider;

public abstract class CalendarProvider
  extends LocaleServiceProvider
{
  public abstract Calendar getInstance(TimeZone paramTimeZone, Locale paramLocale);
}


/* Location:              E:\java_source\rt.jar!\sun\util\spi\CalendarProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */