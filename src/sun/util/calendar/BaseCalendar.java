/*     */ package sun.util.calendar;
/*     */ 
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
/*     */ public abstract class BaseCalendar
/*     */   extends AbstractCalendar
/*     */ {
/*     */   public static final int JANUARY = 1;
/*     */   public static final int FEBRUARY = 2;
/*     */   public static final int MARCH = 3;
/*     */   public static final int APRIL = 4;
/*     */   public static final int MAY = 5;
/*     */   public static final int JUNE = 6;
/*     */   public static final int JULY = 7;
/*     */   public static final int AUGUST = 8;
/*     */   public static final int SEPTEMBER = 9;
/*     */   public static final int OCTOBER = 10;
/*     */   public static final int NOVEMBER = 11;
/*     */   public static final int DECEMBER = 12;
/*     */   public static final int SUNDAY = 1;
/*     */   public static final int MONDAY = 2;
/*     */   public static final int TUESDAY = 3;
/*     */   public static final int WEDNESDAY = 4;
/*     */   public static final int THURSDAY = 5;
/*     */   public static final int FRIDAY = 6;
/*     */   public static final int SATURDAY = 7;
/*     */   private static final int BASE_YEAR = 1970;
/*  70 */   private static final int[] FIXED_DATES = { 719163, 719528, 719893, 720259, 720624, 720989, 721354, 721720, 722085, 722450, 722815, 723181, 723546, 723911, 724276, 724642, 725007, 725372, 725737, 726103, 726468, 726833, 727198, 727564, 727929, 728294, 728659, 729025, 729390, 729755, 730120, 730486, 730851, 731216, 731581, 731947, 732312, 732677, 733042, 733408, 733773, 734138, 734503, 734869, 735234, 735599, 735964, 736330, 736695, 737060, 737425, 737791, 738156, 738521, 738886, 739252, 739617, 739982, 740347, 740713, 741078, 741443, 741808, 742174, 742539, 742904, 743269, 743635, 744000, 744365 };
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
/*     */   public static abstract class Date
/*     */     extends CalendarDate
/*     */   {
/*     */     protected Date() {}
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
/*     */     protected Date(TimeZone paramTimeZone)
/*     */     {
/* 148 */       super();
/*     */     }
/*     */     
/*     */     public Date setNormalizedDate(int paramInt1, int paramInt2, int paramInt3) {
/* 152 */       setNormalizedYear(paramInt1);
/* 153 */       setMonth(paramInt2).setDayOfMonth(paramInt3);
/* 154 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */     public abstract int getNormalizedYear();
/*     */     
/*     */ 
/*     */     public abstract void setNormalizedYear(int paramInt);
/*     */     
/*     */ 
/* 164 */     int cachedYear = 2004;
/* 165 */     long cachedFixedDateJan1 = 731581L;
/* 166 */     long cachedFixedDateNextJan1 = this.cachedFixedDateJan1 + 366L;
/*     */     
/*     */     protected final boolean hit(int paramInt) {
/* 169 */       return paramInt == this.cachedYear;
/*     */     }
/*     */     
/*     */     protected final boolean hit(long paramLong) {
/* 173 */       return (paramLong >= this.cachedFixedDateJan1) && (paramLong < this.cachedFixedDateNextJan1);
/*     */     }
/*     */     
/*     */     protected int getCachedYear() {
/* 177 */       return this.cachedYear;
/*     */     }
/*     */     
/*     */     protected long getCachedJan1() {
/* 181 */       return this.cachedFixedDateJan1;
/*     */     }
/*     */     
/*     */     protected void setCache(int paramInt1, long paramLong, int paramInt2) {
/* 185 */       this.cachedYear = paramInt1;
/* 186 */       this.cachedFixedDateJan1 = paramLong;
/* 187 */       this.cachedFixedDateNextJan1 = (paramLong + paramInt2);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean validate(CalendarDate paramCalendarDate) {
/* 192 */     Date localDate = (Date)paramCalendarDate;
/* 193 */     if (localDate.isNormalized()) {
/* 194 */       return true;
/*     */     }
/* 196 */     int i = localDate.getMonth();
/* 197 */     if ((i < 1) || (i > 12)) {
/* 198 */       return false;
/*     */     }
/* 200 */     int j = localDate.getDayOfMonth();
/* 201 */     if ((j <= 0) || (j > getMonthLength(localDate.getNormalizedYear(), i))) {
/* 202 */       return false;
/*     */     }
/* 204 */     int k = localDate.getDayOfWeek();
/* 205 */     if ((k != Integer.MIN_VALUE) && (k != getDayOfWeek(localDate))) {
/* 206 */       return false;
/*     */     }
/*     */     
/* 209 */     if (!validateTime(paramCalendarDate)) {
/* 210 */       return false;
/*     */     }
/*     */     
/* 213 */     localDate.setNormalized(true);
/* 214 */     return true;
/*     */   }
/*     */   
/*     */   public boolean normalize(CalendarDate paramCalendarDate) {
/* 218 */     if (paramCalendarDate.isNormalized()) {
/* 219 */       return true;
/*     */     }
/*     */     
/* 222 */     Date localDate = (Date)paramCalendarDate;
/* 223 */     TimeZone localTimeZone = localDate.getZone();
/*     */     
/*     */ 
/*     */ 
/* 227 */     if (localTimeZone != null) {
/* 228 */       getTime(paramCalendarDate);
/* 229 */       return true;
/*     */     }
/*     */     
/* 232 */     int i = normalizeTime(localDate);
/* 233 */     normalizeMonth(localDate);
/* 234 */     long l1 = localDate.getDayOfMonth() + i;
/* 235 */     int j = localDate.getMonth();
/* 236 */     int k = localDate.getNormalizedYear();
/* 237 */     int m = getMonthLength(k, j);
/*     */     
/* 239 */     if ((l1 <= 0L) || (l1 > m)) {
/* 240 */       if ((l1 <= 0L) && (l1 > -28L)) {
/* 241 */         m = getMonthLength(k, --j);
/* 242 */         l1 += m;
/* 243 */         localDate.setDayOfMonth((int)l1);
/* 244 */         if (j == 0) {
/* 245 */           j = 12;
/* 246 */           localDate.setNormalizedYear(k - 1);
/*     */         }
/* 248 */         localDate.setMonth(j);
/* 249 */       } else if ((l1 > m) && (l1 < m + 28)) {
/* 250 */         l1 -= m;
/* 251 */         j++;
/* 252 */         localDate.setDayOfMonth((int)l1);
/* 253 */         if (j > 12) {
/* 254 */           localDate.setNormalizedYear(k + 1);
/* 255 */           j = 1;
/*     */         }
/* 257 */         localDate.setMonth(j);
/*     */       } else {
/* 259 */         long l2 = l1 + getFixedDate(k, j, 1, localDate) - 1L;
/* 260 */         getCalendarDateFromFixedDate(localDate, l2);
/*     */       }
/*     */     } else {
/* 263 */       localDate.setDayOfWeek(getDayOfWeek(localDate));
/*     */     }
/* 265 */     paramCalendarDate.setLeapYear(isLeapYear(localDate.getNormalizedYear()));
/* 266 */     paramCalendarDate.setZoneOffset(0);
/* 267 */     paramCalendarDate.setDaylightSaving(0);
/* 268 */     localDate.setNormalized(true);
/* 269 */     return true;
/*     */   }
/*     */   
/*     */   void normalizeMonth(CalendarDate paramCalendarDate) {
/* 273 */     Date localDate = (Date)paramCalendarDate;
/* 274 */     int i = localDate.getNormalizedYear();
/* 275 */     long l1 = localDate.getMonth();
/* 276 */     if (l1 <= 0L) {
/* 277 */       long l2 = 1L - l1;
/* 278 */       i -= (int)(l2 / 12L + 1L);
/* 279 */       l1 = 13L - l2 % 12L;
/* 280 */       localDate.setNormalizedYear(i);
/* 281 */       localDate.setMonth((int)l1);
/* 282 */     } else if (l1 > 12L) {
/* 283 */       i += (int)((l1 - 1L) / 12L);
/* 284 */       l1 = (l1 - 1L) % 12L + 1L;
/* 285 */       localDate.setNormalizedYear(i);
/* 286 */       localDate.setMonth((int)l1);
/*     */     }
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
/*     */ 
/*     */ 
/*     */   public int getYearLength(CalendarDate paramCalendarDate)
/*     */   {
/* 303 */     return isLeapYear(((Date)paramCalendarDate).getNormalizedYear()) ? 366 : 365;
/*     */   }
/*     */   
/*     */   public int getYearLengthInMonths(CalendarDate paramCalendarDate) {
/* 307 */     return 12;
/*     */   }
/*     */   
/* 310 */   static final int[] DAYS_IN_MONTH = { 31, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
/*     */   
/*     */ 
/* 313 */   static final int[] ACCUMULATED_DAYS_IN_MONTH = { -30, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
/*     */   
/*     */ 
/*     */ 
/* 317 */   static final int[] ACCUMULATED_DAYS_IN_MONTH_LEAP = { -30, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335 };
/*     */   
/*     */ 
/*     */   public int getMonthLength(CalendarDate paramCalendarDate)
/*     */   {
/* 322 */     Date localDate = (Date)paramCalendarDate;
/* 323 */     int i = localDate.getMonth();
/* 324 */     if ((i < 1) || (i > 12)) {
/* 325 */       throw new IllegalArgumentException("Illegal month value: " + i);
/*     */     }
/* 327 */     return getMonthLength(localDate.getNormalizedYear(), i);
/*     */   }
/*     */   
/*     */   private int getMonthLength(int paramInt1, int paramInt2)
/*     */   {
/* 332 */     int i = DAYS_IN_MONTH[paramInt2];
/* 333 */     if ((paramInt2 == 2) && (isLeapYear(paramInt1))) {
/* 334 */       i++;
/*     */     }
/* 336 */     return i;
/*     */   }
/*     */   
/*     */   public long getDayOfYear(CalendarDate paramCalendarDate) {
/* 340 */     return getDayOfYear(((Date)paramCalendarDate).getNormalizedYear(), paramCalendarDate
/* 341 */       .getMonth(), paramCalendarDate
/* 342 */       .getDayOfMonth());
/*     */   }
/*     */   
/*     */   final long getDayOfYear(int paramInt1, int paramInt2, int paramInt3) {
/* 346 */     return 
/* 347 */       paramInt3 + (isLeapYear(paramInt1) ? ACCUMULATED_DAYS_IN_MONTH_LEAP[paramInt2] : ACCUMULATED_DAYS_IN_MONTH[paramInt2]);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getFixedDate(CalendarDate paramCalendarDate)
/*     */   {
/* 353 */     if (!paramCalendarDate.isNormalized()) {
/* 354 */       normalizeMonth(paramCalendarDate);
/*     */     }
/* 356 */     return getFixedDate(((Date)paramCalendarDate).getNormalizedYear(), paramCalendarDate
/* 357 */       .getMonth(), paramCalendarDate
/* 358 */       .getDayOfMonth(), (Date)paramCalendarDate);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getFixedDate(int paramInt1, int paramInt2, int paramInt3, Date paramDate)
/*     */   {
/* 364 */     int i = (paramInt2 == 1) && (paramInt3 == 1) ? 1 : 0;
/*     */     
/*     */ 
/* 367 */     if ((paramDate != null) && (paramDate.hit(paramInt1))) {
/* 368 */       if (i != 0) {
/* 369 */         return paramDate.getCachedJan1();
/*     */       }
/* 371 */       return paramDate.getCachedJan1() + getDayOfYear(paramInt1, paramInt2, paramInt3) - 1L;
/*     */     }
/*     */     
/*     */ 
/* 375 */     int j = paramInt1 - 1970;
/* 376 */     if ((j >= 0) && (j < FIXED_DATES.length)) {
/* 377 */       l1 = FIXED_DATES[j];
/* 378 */       if (paramDate != null) {
/* 379 */         paramDate.setCache(paramInt1, l1, isLeapYear(paramInt1) ? 366 : 365);
/*     */       }
/* 381 */       return i != 0 ? l1 : l1 + getDayOfYear(paramInt1, paramInt2, paramInt3) - 1L;
/*     */     }
/*     */     
/* 384 */     long l1 = paramInt1 - 1L;
/* 385 */     long l2 = paramInt3;
/*     */     
/* 387 */     if (l1 >= 0L) {
/* 388 */       l2 += 365L * l1 + l1 / 4L - l1 / 100L + l1 / 400L + (367 * paramInt2 - 362) / 12;
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 398 */       l2 = l2 + (365L * l1 + CalendarUtils.floorDivide(l1, 4L) - CalendarUtils.floorDivide(l1, 100L) + CalendarUtils.floorDivide(l1, 400L) + CalendarUtils.floorDivide(367 * paramInt2 - 362, 12));
/*     */     }
/*     */     
/* 401 */     if (paramInt2 > 2) {
/* 402 */       l2 -= (isLeapYear(paramInt1) ? 1L : 2L);
/*     */     }
/*     */     
/*     */ 
/* 406 */     if ((paramDate != null) && (i != 0)) {
/* 407 */       paramDate.setCache(paramInt1, l2, isLeapYear(paramInt1) ? 366 : 365);
/*     */     }
/*     */     
/* 410 */     return l2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void getCalendarDateFromFixedDate(CalendarDate paramCalendarDate, long paramLong)
/*     */   {
/* 420 */     Date localDate = (Date)paramCalendarDate;
/*     */     int i;
/*     */     long l1;
/*     */     boolean bool;
/* 424 */     if (localDate.hit(paramLong)) {
/* 425 */       i = localDate.getCachedYear();
/* 426 */       l1 = localDate.getCachedJan1();
/* 427 */       bool = isLeapYear(i);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 432 */       i = getGregorianYearFromFixedDate(paramLong);
/* 433 */       l1 = getFixedDate(i, 1, 1, null);
/* 434 */       bool = isLeapYear(i);
/*     */       
/* 436 */       localDate.setCache(i, l1, bool ? 366 : 365);
/*     */     }
/*     */     
/* 439 */     int j = (int)(paramLong - l1);
/* 440 */     long l2 = l1 + 31L + 28L;
/* 441 */     if (bool) {
/* 442 */       l2 += 1L;
/*     */     }
/* 444 */     if (paramLong >= l2) {
/* 445 */       j += (bool ? 1 : 2);
/*     */     }
/* 447 */     int k = 12 * j + 373;
/* 448 */     if (k > 0) {
/* 449 */       k /= 367;
/*     */     } else {
/* 451 */       k = CalendarUtils.floorDivide(k, 367);
/*     */     }
/* 453 */     long l3 = l1 + ACCUMULATED_DAYS_IN_MONTH[k];
/* 454 */     if ((bool) && (k >= 3)) {
/* 455 */       l3 += 1L;
/*     */     }
/* 457 */     int m = (int)(paramLong - l3) + 1;
/* 458 */     int n = getDayOfWeekFromFixedDate(paramLong);
/* 459 */     assert (n > 0) : ("negative day of week " + n);
/* 460 */     localDate.setNormalizedYear(i);
/* 461 */     localDate.setMonth(k);
/* 462 */     localDate.setDayOfMonth(m);
/* 463 */     localDate.setDayOfWeek(n);
/* 464 */     localDate.setLeapYear(bool);
/* 465 */     localDate.setNormalized(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getDayOfWeek(CalendarDate paramCalendarDate)
/*     */   {
/* 472 */     long l = getFixedDate(paramCalendarDate);
/* 473 */     return getDayOfWeekFromFixedDate(l);
/*     */   }
/*     */   
/*     */   public static final int getDayOfWeekFromFixedDate(long paramLong)
/*     */   {
/* 478 */     if (paramLong >= 0L) {
/* 479 */       return (int)(paramLong % 7L) + 1;
/*     */     }
/* 481 */     return (int)CalendarUtils.mod(paramLong, 7L) + 1;
/*     */   }
/*     */   
/*     */ 
/* 485 */   public int getYearFromFixedDate(long paramLong) { return getGregorianYearFromFixedDate(paramLong); }
/*     */   
/*     */   final int getGregorianYearFromFixedDate(long paramLong) {
/*     */     long l;
/*     */     int n;
/*     */     int i;
/*     */     int i1;
/*     */     int j;
/*     */     int i2;
/*     */     int k;
/*     */     int i3;
/*     */     int m;
/* 497 */     if (paramLong > 0L) {
/* 498 */       l = paramLong - 1L;
/* 499 */       n = (int)(l / 146097L);
/* 500 */       i = (int)(l % 146097L);
/* 501 */       i1 = i / 36524;
/* 502 */       j = i % 36524;
/* 503 */       i2 = j / 1461;
/* 504 */       k = j % 1461;
/* 505 */       i3 = k / 365;
/* 506 */       m = k % 365 + 1;
/*     */     } else {
/* 508 */       l = paramLong - 1L;
/* 509 */       n = (int)CalendarUtils.floorDivide(l, 146097L);
/* 510 */       i = (int)CalendarUtils.mod(l, 146097L);
/* 511 */       i1 = CalendarUtils.floorDivide(i, 36524);
/* 512 */       j = CalendarUtils.mod(i, 36524);
/* 513 */       i2 = CalendarUtils.floorDivide(j, 1461);
/* 514 */       k = CalendarUtils.mod(j, 1461);
/* 515 */       i3 = CalendarUtils.floorDivide(k, 365);
/* 516 */       m = CalendarUtils.mod(k, 365) + 1;
/*     */     }
/* 518 */     int i4 = 400 * n + 100 * i1 + 4 * i2 + i3;
/* 519 */     if ((i1 != 4) && (i3 != 4)) {
/* 520 */       i4++;
/*     */     }
/* 522 */     return i4;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean isLeapYear(CalendarDate paramCalendarDate)
/*     */   {
/* 531 */     return isLeapYear(((Date)paramCalendarDate).getNormalizedYear());
/*     */   }
/*     */   
/*     */   boolean isLeapYear(int paramInt) {
/* 535 */     return CalendarUtils.isGregorianLeapYear(paramInt);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\calendar\BaseCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */