/*     */ package sun.util.calendar;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TimeZone;
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
/*     */ public class LocalGregorianCalendar
/*     */   extends BaseCalendar
/*     */ {
/*     */   private String name;
/*     */   private Era[] eras;
/*     */   
/*     */   public static class Date
/*     */     extends BaseCalendar.Date
/*     */   {
/*     */     protected Date() {}
/*     */     
/*     */     protected Date(TimeZone paramTimeZone)
/*     */     {
/*  52 */       super();
/*     */     }
/*     */     
/*  55 */     private int gregorianYear = Integer.MIN_VALUE;
/*     */     
/*     */     public Date setEra(Era paramEra)
/*     */     {
/*  59 */       if (getEra() != paramEra) {
/*  60 */         super.setEra(paramEra);
/*  61 */         this.gregorianYear = Integer.MIN_VALUE;
/*     */       }
/*  63 */       return this;
/*     */     }
/*     */     
/*     */     public Date addYear(int paramInt)
/*     */     {
/*  68 */       super.addYear(paramInt);
/*  69 */       this.gregorianYear += paramInt;
/*  70 */       return this;
/*     */     }
/*     */     
/*     */     public Date setYear(int paramInt)
/*     */     {
/*  75 */       if (getYear() != paramInt) {
/*  76 */         super.setYear(paramInt);
/*  77 */         this.gregorianYear = Integer.MIN_VALUE;
/*     */       }
/*  79 */       return this;
/*     */     }
/*     */     
/*     */     public int getNormalizedYear()
/*     */     {
/*  84 */       return this.gregorianYear;
/*     */     }
/*     */     
/*     */     public void setNormalizedYear(int paramInt)
/*     */     {
/*  89 */       this.gregorianYear = paramInt;
/*     */     }
/*     */     
/*     */     void setLocalEra(Era paramEra) {
/*  93 */       super.setEra(paramEra);
/*     */     }
/*     */     
/*     */     void setLocalYear(int paramInt) {
/*  97 */       super.setYear(paramInt);
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 102 */       String str1 = super.toString();
/* 103 */       str1 = str1.substring(str1.indexOf('T'));
/* 104 */       StringBuffer localStringBuffer = new StringBuffer();
/* 105 */       Era localEra = getEra();
/* 106 */       if (localEra != null) {
/* 107 */         String str2 = localEra.getAbbreviation();
/* 108 */         if (str2 != null) {
/* 109 */           localStringBuffer.append(str2);
/*     */         }
/*     */       }
/* 112 */       localStringBuffer.append(getYear()).append('.');
/* 113 */       CalendarUtils.sprintf0d(localStringBuffer, getMonth(), 2).append('.');
/* 114 */       CalendarUtils.sprintf0d(localStringBuffer, getDayOfMonth(), 2);
/* 115 */       localStringBuffer.append(str1);
/* 116 */       return localStringBuffer.toString();
/*     */     }
/*     */   }
/*     */   
/*     */   static LocalGregorianCalendar getLocalGregorianCalendar(String paramString) {
/*     */     Properties localProperties;
/*     */     try {
/* 123 */       localProperties = CalendarSystem.getCalendarProperties();
/*     */     } catch (IOException|IllegalArgumentException localIOException) {
/* 125 */       throw new InternalError(localIOException);
/*     */     }
/*     */     
/* 128 */     String str1 = localProperties.getProperty("calendar." + paramString + ".eras");
/* 129 */     if (str1 == null) {
/* 130 */       return null;
/*     */     }
/* 132 */     ArrayList localArrayList = new ArrayList();
/* 133 */     StringTokenizer localStringTokenizer1 = new StringTokenizer(str1, ";");
/* 134 */     while (localStringTokenizer1.hasMoreTokens()) {
/* 135 */       localObject1 = localStringTokenizer1.nextToken().trim();
/* 136 */       StringTokenizer localStringTokenizer2 = new StringTokenizer((String)localObject1, ",");
/* 137 */       Object localObject2 = null;
/* 138 */       boolean bool = true;
/* 139 */       long l = 0L;
/* 140 */       Object localObject3 = null;
/*     */       
/* 142 */       while (localStringTokenizer2.hasMoreTokens()) {
/* 143 */         localObject4 = localStringTokenizer2.nextToken();
/* 144 */         int i = ((String)localObject4).indexOf('=');
/*     */         
/* 146 */         if (i == -1) {
/* 147 */           return null;
/*     */         }
/* 149 */         String str2 = ((String)localObject4).substring(0, i);
/* 150 */         String str3 = ((String)localObject4).substring(i + 1);
/* 151 */         if ("name".equals(str2)) {
/* 152 */           localObject2 = str3;
/* 153 */         } else if ("since".equals(str2)) {
/* 154 */           if (str3.endsWith("u")) {
/* 155 */             bool = false;
/* 156 */             l = Long.parseLong(str3.substring(0, str3.length() - 1));
/*     */           } else {
/* 158 */             l = Long.parseLong(str3);
/*     */           }
/* 160 */         } else if ("abbr".equals(str2)) {
/* 161 */           localObject3 = str3;
/*     */         } else {
/* 163 */           throw new RuntimeException("Unknown key word: " + str2);
/*     */         }
/*     */       }
/* 166 */       Object localObject4 = new Era((String)localObject2, (String)localObject3, l, bool);
/* 167 */       localArrayList.add(localObject4);
/*     */     }
/* 169 */     Object localObject1 = new Era[localArrayList.size()];
/* 170 */     localArrayList.toArray((Object[])localObject1);
/*     */     
/* 172 */     return new LocalGregorianCalendar(paramString, (Era[])localObject1);
/*     */   }
/*     */   
/*     */   private LocalGregorianCalendar(String paramString, Era[] paramArrayOfEra) {
/* 176 */     this.name = paramString;
/* 177 */     this.eras = paramArrayOfEra;
/* 178 */     setEras(paramArrayOfEra);
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/* 183 */     return this.name;
/*     */   }
/*     */   
/*     */   public Date getCalendarDate()
/*     */   {
/* 188 */     return getCalendarDate(System.currentTimeMillis(), newCalendarDate());
/*     */   }
/*     */   
/*     */   public Date getCalendarDate(long paramLong)
/*     */   {
/* 193 */     return getCalendarDate(paramLong, newCalendarDate());
/*     */   }
/*     */   
/*     */   public Date getCalendarDate(long paramLong, TimeZone paramTimeZone)
/*     */   {
/* 198 */     return getCalendarDate(paramLong, newCalendarDate(paramTimeZone));
/*     */   }
/*     */   
/*     */   public Date getCalendarDate(long paramLong, CalendarDate paramCalendarDate)
/*     */   {
/* 203 */     Date localDate = (Date)super.getCalendarDate(paramLong, paramCalendarDate);
/* 204 */     return adjustYear(localDate, paramLong, localDate.getZoneOffset());
/*     */   }
/*     */   
/*     */   private Date adjustYear(Date paramDate, long paramLong, int paramInt)
/*     */   {
/* 209 */     for (int i = this.eras.length - 1; i >= 0; i--) {
/* 210 */       Era localEra = this.eras[i];
/* 211 */       long l = localEra.getSince(null);
/* 212 */       if (localEra.isLocalTime()) {
/* 213 */         l -= paramInt;
/*     */       }
/* 215 */       if (paramLong >= l) {
/* 216 */         paramDate.setLocalEra(localEra);
/* 217 */         int j = paramDate.getNormalizedYear() - localEra.getSinceDate().getYear() + 1;
/* 218 */         paramDate.setLocalYear(j);
/* 219 */         break;
/*     */       }
/*     */     }
/* 222 */     if (i < 0) {
/* 223 */       paramDate.setLocalEra(null);
/* 224 */       paramDate.setLocalYear(paramDate.getNormalizedYear());
/*     */     }
/* 226 */     paramDate.setNormalized(true);
/* 227 */     return paramDate;
/*     */   }
/*     */   
/*     */   public Date newCalendarDate()
/*     */   {
/* 232 */     return new Date();
/*     */   }
/*     */   
/*     */   public Date newCalendarDate(TimeZone paramTimeZone)
/*     */   {
/* 237 */     return new Date(paramTimeZone);
/*     */   }
/*     */   
/*     */   public boolean validate(CalendarDate paramCalendarDate)
/*     */   {
/* 242 */     Date localDate1 = (Date)paramCalendarDate;
/* 243 */     Era localEra = localDate1.getEra();
/* 244 */     if (localEra != null) {
/* 245 */       if (!validateEra(localEra)) {
/* 246 */         return false;
/*     */       }
/* 248 */       localDate1.setNormalizedYear(localEra.getSinceDate().getYear() + localDate1.getYear() - 1);
/* 249 */       Date localDate2 = newCalendarDate(paramCalendarDate.getZone());
/* 250 */       localDate2.setEra(localEra).setDate(paramCalendarDate.getYear(), paramCalendarDate.getMonth(), paramCalendarDate.getDayOfMonth());
/* 251 */       normalize(localDate2);
/* 252 */       if (localDate2.getEra() != localEra) {
/* 253 */         return false;
/*     */       }
/*     */     } else {
/* 256 */       if (paramCalendarDate.getYear() >= this.eras[0].getSinceDate().getYear()) {
/* 257 */         return false;
/*     */       }
/* 259 */       localDate1.setNormalizedYear(localDate1.getYear());
/*     */     }
/* 261 */     return super.validate(localDate1);
/*     */   }
/*     */   
/*     */   private boolean validateEra(Era paramEra)
/*     */   {
/* 266 */     for (int i = 0; i < this.eras.length; i++) {
/* 267 */       if (paramEra == this.eras[i]) {
/* 268 */         return true;
/*     */       }
/*     */     }
/* 271 */     return false;
/*     */   }
/*     */   
/*     */   public boolean normalize(CalendarDate paramCalendarDate)
/*     */   {
/* 276 */     if (paramCalendarDate.isNormalized()) {
/* 277 */       return true;
/*     */     }
/*     */     
/* 280 */     normalizeYear(paramCalendarDate);
/* 281 */     Date localDate = (Date)paramCalendarDate;
/*     */     
/*     */ 
/* 284 */     super.normalize(localDate);
/*     */     
/* 286 */     int i = 0;
/* 287 */     long l1 = 0L;
/* 288 */     int j = localDate.getNormalizedYear();
/*     */     
/* 290 */     Era localEra = null;
/* 291 */     for (int k = this.eras.length - 1; k >= 0; k--) {
/* 292 */       localEra = this.eras[k];
/* 293 */       if (localEra.isLocalTime()) {
/* 294 */         CalendarDate localCalendarDate = localEra.getSinceDate();
/* 295 */         int n = localCalendarDate.getYear();
/* 296 */         if (j > n) {
/*     */           break;
/*     */         }
/* 299 */         if (j == n) {
/* 300 */           int i1 = localDate.getMonth();
/* 301 */           int i2 = localCalendarDate.getMonth();
/* 302 */           if (i1 > i2) {
/*     */             break;
/*     */           }
/* 305 */           if (i1 == i2) {
/* 306 */             int i3 = localDate.getDayOfMonth();
/* 307 */             int i4 = localCalendarDate.getDayOfMonth();
/* 308 */             if (i3 > i4) {
/*     */               break;
/*     */             }
/* 311 */             if (i3 == i4) {
/* 312 */               long l3 = localDate.getTimeOfDay();
/* 313 */               long l4 = localCalendarDate.getTimeOfDay();
/* 314 */               if (l3 >= l4) {
/*     */                 break;
/*     */               }
/* 317 */               k--;
/* 318 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       } else {
/* 323 */         if (i == 0) {
/* 324 */           l1 = super.getTime(paramCalendarDate);
/* 325 */           i = 1;
/*     */         }
/*     */         
/* 328 */         long l2 = localEra.getSince(paramCalendarDate.getZone());
/* 329 */         if (l1 >= l2) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/* 334 */     if (k >= 0) {
/* 335 */       localDate.setLocalEra(localEra);
/* 336 */       int m = localDate.getNormalizedYear() - localEra.getSinceDate().getYear() + 1;
/* 337 */       localDate.setLocalYear(m);
/*     */     }
/*     */     else {
/* 340 */       localDate.setEra(null);
/* 341 */       localDate.setLocalYear(j);
/* 342 */       localDate.setNormalizedYear(j);
/*     */     }
/* 344 */     localDate.setNormalized(true);
/* 345 */     return true;
/*     */   }
/*     */   
/*     */   void normalizeMonth(CalendarDate paramCalendarDate)
/*     */   {
/* 350 */     normalizeYear(paramCalendarDate);
/* 351 */     super.normalizeMonth(paramCalendarDate);
/*     */   }
/*     */   
/*     */   void normalizeYear(CalendarDate paramCalendarDate) {
/* 355 */     Date localDate = (Date)paramCalendarDate;
/*     */     
/*     */ 
/* 358 */     Era localEra = localDate.getEra();
/* 359 */     if ((localEra == null) || (!validateEra(localEra))) {
/* 360 */       localDate.setNormalizedYear(localDate.getYear());
/*     */     } else {
/* 362 */       localDate.setNormalizedYear(localEra.getSinceDate().getYear() + localDate.getYear() - 1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLeapYear(int paramInt)
/*     */   {
/* 372 */     return CalendarUtils.isGregorianLeapYear(paramInt);
/*     */   }
/*     */   
/*     */   public boolean isLeapYear(Era paramEra, int paramInt) {
/* 376 */     if (paramEra == null) {
/* 377 */       return isLeapYear(paramInt);
/*     */     }
/* 379 */     int i = paramEra.getSinceDate().getYear() + paramInt - 1;
/* 380 */     return isLeapYear(i);
/*     */   }
/*     */   
/*     */   public void getCalendarDateFromFixedDate(CalendarDate paramCalendarDate, long paramLong)
/*     */   {
/* 385 */     Date localDate = (Date)paramCalendarDate;
/* 386 */     super.getCalendarDateFromFixedDate(localDate, paramLong);
/* 387 */     adjustYear(localDate, (paramLong - 719163L) * 86400000L, 0);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\calendar\LocalGregorianCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */