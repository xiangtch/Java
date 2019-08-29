/*     */ package sun.util.calendar;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Properties;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class CalendarSystem
/*     */ {
/*  79 */   private static volatile boolean initialized = false;
/*     */   
/*     */ 
/*     */   private static ConcurrentMap<String, String> names;
/*     */   
/*     */ 
/*     */   private static ConcurrentMap<String, CalendarSystem> calendars;
/*     */   
/*     */   private static final String PACKAGE_NAME = "sun.util.calendar.";
/*     */   
/*  89 */   private static final String[] namePairs = { "gregorian", "Gregorian", "japanese", "LocalGregorianCalendar", "julian", "JulianCalendar" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void initNames()
/*     */   {
/* 102 */     ConcurrentHashMap localConcurrentHashMap = new ConcurrentHashMap();
/*     */     
/*     */ 
/*     */ 
/* 106 */     StringBuilder localStringBuilder = new StringBuilder();
/* 107 */     for (int i = 0; i < namePairs.length; i += 2) {
/* 108 */       localStringBuilder.setLength(0);
/* 109 */       String str = "sun.util.calendar." + namePairs[(i + 1)];
/* 110 */       localConcurrentHashMap.put(namePairs[i], str);
/*     */     }
/* 112 */     synchronized (CalendarSystem.class) {
/* 113 */       if (!initialized) {
/* 114 */         names = localConcurrentHashMap;
/* 115 */         calendars = new ConcurrentHashMap();
/* 116 */         initialized = true;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 121 */   private static final Gregorian GREGORIAN_INSTANCE = new Gregorian();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Gregorian getGregorianCalendar()
/*     */   {
/* 130 */     return GREGORIAN_INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CalendarSystem forName(String paramString)
/*     */   {
/* 144 */     if ("gregorian".equals(paramString)) {
/* 145 */       return GREGORIAN_INSTANCE;
/*     */     }
/*     */     
/* 148 */     if (!initialized) {
/* 149 */       initNames();
/*     */     }
/*     */     
/* 152 */     Object localObject = (CalendarSystem)calendars.get(paramString);
/* 153 */     if (localObject != null) {
/* 154 */       return (CalendarSystem)localObject;
/*     */     }
/*     */     
/* 157 */     String str = (String)names.get(paramString);
/* 158 */     if (str == null) {
/* 159 */       return null;
/*     */     }
/*     */     
/* 162 */     if (str.endsWith("LocalGregorianCalendar"))
/*     */     {
/* 164 */       localObject = LocalGregorianCalendar.getLocalGregorianCalendar(paramString);
/*     */     } else {
/*     */       try {
/* 167 */         Class localClass = Class.forName(str);
/* 168 */         localObject = (CalendarSystem)localClass.newInstance();
/*     */       } catch (Exception localException) {
/* 170 */         throw new InternalError(localException);
/*     */       }
/*     */     }
/* 173 */     if (localObject == null) {
/* 174 */       return null;
/*     */     }
/* 176 */     CalendarSystem localCalendarSystem = (CalendarSystem)calendars.putIfAbsent(paramString, localObject);
/* 177 */     return (CalendarSystem)(localCalendarSystem == null ? localObject : localCalendarSystem);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Properties getCalendarProperties()
/*     */     throws IOException
/*     */   {
/* 189 */     Properties localProperties = null;
/*     */     try {
/* 191 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.home"));
/*     */       
/* 193 */       localObject = str + File.separator + "lib" + File.separator + "calendars.properties";
/*     */       
/* 195 */       localProperties = (Properties)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Properties run() throws IOException {
/* 198 */           Properties localProperties = new Properties();
/* 199 */           FileInputStream localFileInputStream = new FileInputStream(this.val$fname);Object localObject1 = null;
/* 200 */           try { localProperties.load(localFileInputStream);
/*     */           }
/*     */           catch (Throwable localThrowable2)
/*     */           {
/* 199 */             localObject1 = localThrowable2;throw localThrowable2;
/*     */           } finally {
/* 201 */             if (localFileInputStream != null) if (localObject1 != null) try { localFileInputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localFileInputStream.close(); }
/* 202 */           return localProperties;
/*     */         }
/*     */       });
/*     */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 206 */       Object localObject = localPrivilegedActionException.getCause();
/* 207 */       if ((localObject instanceof IOException))
/* 208 */         throw ((IOException)localObject);
/* 209 */       if ((localObject instanceof IllegalArgumentException)) {
/* 210 */         throw ((IllegalArgumentException)localObject);
/*     */       }
/*     */       
/* 213 */       throw new InternalError((Throwable)localObject);
/*     */     }
/* 215 */     return localProperties;
/*     */   }
/*     */   
/*     */   public abstract String getName();
/*     */   
/*     */   public abstract CalendarDate getCalendarDate();
/*     */   
/*     */   public abstract CalendarDate getCalendarDate(long paramLong);
/*     */   
/*     */   public abstract CalendarDate getCalendarDate(long paramLong, CalendarDate paramCalendarDate);
/*     */   
/*     */   public abstract CalendarDate getCalendarDate(long paramLong, TimeZone paramTimeZone);
/*     */   
/*     */   public abstract CalendarDate newCalendarDate();
/*     */   
/*     */   public abstract CalendarDate newCalendarDate(TimeZone paramTimeZone);
/*     */   
/*     */   public abstract long getTime(CalendarDate paramCalendarDate);
/*     */   
/*     */   public abstract int getYearLength(CalendarDate paramCalendarDate);
/*     */   
/*     */   public abstract int getYearLengthInMonths(CalendarDate paramCalendarDate);
/*     */   
/*     */   public abstract int getMonthLength(CalendarDate paramCalendarDate);
/*     */   
/*     */   public abstract int getWeekLength();
/*     */   
/*     */   public abstract Era getEra(String paramString);
/*     */   
/*     */   public abstract Era[] getEras();
/*     */   
/*     */   public abstract void setEra(CalendarDate paramCalendarDate, String paramString);
/*     */   
/*     */   public abstract CalendarDate getNthDayOfWeek(int paramInt1, int paramInt2, CalendarDate paramCalendarDate);
/*     */   
/*     */   public abstract CalendarDate setTimeOfDay(CalendarDate paramCalendarDate, int paramInt);
/*     */   
/*     */   public abstract boolean validate(CalendarDate paramCalendarDate);
/*     */   
/*     */   public abstract boolean normalize(CalendarDate paramCalendarDate);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\calendar\CalendarSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */