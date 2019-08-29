/*      */ package sun.util.calendar;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.DataInput;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.StreamCorruptedException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.time.LocalDateTime;
/*      */ import java.time.LocalTime;
/*      */ import java.time.ZoneOffset;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.zip.CRC32;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ZoneInfoFile
/*      */ {
/*      */   private static String versionId;
/*      */   
/*      */   public static String[] getZoneIds()
/*      */   {
/*   69 */     int i = regions.length + oldMappings.length;
/*   70 */     if (!USE_OLDMAPPING) {
/*   71 */       i += 3;
/*      */     }
/*   73 */     String[] arrayOfString = (String[])Arrays.copyOf(regions, i);
/*   74 */     int j = regions.length;
/*   75 */     if (!USE_OLDMAPPING) {
/*   76 */       arrayOfString[(j++)] = "EST";
/*   77 */       arrayOfString[(j++)] = "HST";
/*   78 */       arrayOfString[(j++)] = "MST";
/*      */     }
/*   80 */     for (int k = 0; k < oldMappings.length; k++) {
/*   81 */       arrayOfString[(j++)] = oldMappings[k][0];
/*      */     }
/*   83 */     return arrayOfString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String[] getZoneIds(int paramInt)
/*      */   {
/*   95 */     ArrayList localArrayList = new ArrayList();
/*   96 */     for (String str : getZoneIds()) {
/*   97 */       ZoneInfo localZoneInfo = getZoneInfo(str);
/*   98 */       if (localZoneInfo.getRawOffset() == paramInt) {
/*   99 */         localArrayList.add(str);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  106 */     ??? = (String[])localArrayList.toArray(new String[localArrayList.size()]);
/*  107 */     Arrays.sort(???);
/*  108 */     return (String[])???;
/*      */   }
/*      */   
/*      */   public static ZoneInfo getZoneInfo(String paramString) {
/*  112 */     if (paramString == null) {
/*  113 */       return null;
/*      */     }
/*  115 */     ZoneInfo localZoneInfo = getZoneInfo0(paramString);
/*  116 */     if (localZoneInfo != null) {
/*  117 */       localZoneInfo = (ZoneInfo)localZoneInfo.clone();
/*  118 */       localZoneInfo.setID(paramString);
/*      */     }
/*  120 */     return localZoneInfo;
/*      */   }
/*      */   
/*      */   private static ZoneInfo getZoneInfo0(String paramString) {
/*      */     try {
/*  125 */       ZoneInfo localZoneInfo = (ZoneInfo)zones.get(paramString);
/*  126 */       if (localZoneInfo != null) {
/*  127 */         return localZoneInfo;
/*      */       }
/*  129 */       String str = paramString;
/*  130 */       if (aliases.containsKey(paramString)) {
/*  131 */         str = (String)aliases.get(paramString);
/*      */       }
/*  133 */       int i = Arrays.binarySearch(regions, str);
/*  134 */       if (i < 0) {
/*  135 */         return null;
/*      */       }
/*  137 */       byte[] arrayOfByte = ruleArray[indices[i]];
/*  138 */       DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
/*  139 */       localZoneInfo = getZoneInfo(localDataInputStream, str);
/*  140 */       zones.put(paramString, localZoneInfo);
/*  141 */       return localZoneInfo;
/*      */     } catch (Exception localException) {
/*  143 */       throw new RuntimeException("Invalid binary time-zone data: TZDB:" + paramString + ", version: " + versionId, localException);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map<String, String> getAliasMap()
/*      */   {
/*  155 */     return Collections.unmodifiableMap(aliases);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getVersion()
/*      */   {
/*  164 */     return versionId;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ZoneInfo getCustomTimeZone(String paramString, int paramInt)
/*      */   {
/*  176 */     String str = toCustomID(paramInt);
/*  177 */     return new ZoneInfo(str, paramInt);
/*      */   }
/*      */   
/*      */   public static String toCustomID(int paramInt)
/*      */   {
/*  182 */     int j = paramInt / 60000;
/*  183 */     int i; if (j >= 0) {
/*  184 */       i = 43;
/*      */     } else {
/*  186 */       i = 45;
/*  187 */       j = -j;
/*      */     }
/*  189 */     int k = j / 60;
/*  190 */     int m = j % 60;
/*      */     
/*  192 */     char[] arrayOfChar = { 'G', 'M', 'T', i, '0', '0', ':', '0', '0' };
/*  193 */     if (k >= 10) {
/*  194 */       int tmp94_93 = 4; char[] tmp94_91 = arrayOfChar;tmp94_91[tmp94_93] = ((char)(tmp94_91[tmp94_93] + k / 10));
/*      */     }
/*  196 */     int tmp106_105 = 5; char[] tmp106_103 = arrayOfChar;tmp106_103[tmp106_105] = ((char)(tmp106_103[tmp106_105] + k % 10));
/*  197 */     if (m != 0) {
/*  198 */       char[] tmp124_120 = arrayOfChar;tmp124_120[7] = ((char)(tmp124_120[7] + m / 10)); char[] 
/*  199 */         tmp138_134 = arrayOfChar;tmp138_134[8] = ((char)(tmp138_134[8] + m % 10));
/*      */     }
/*  201 */     return new String(arrayOfChar);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  209 */   private static final Map<String, ZoneInfo> zones = new ConcurrentHashMap();
/*  210 */   private static Map<String, String> aliases = new HashMap();
/*      */   
/*      */   private static byte[][] ruleArray;
/*      */   
/*      */   private static String[] regions;
/*      */   
/*      */   private static int[] indices;
/*      */   
/*      */   private static final boolean USE_OLDMAPPING;
/*  219 */   private static String[][] oldMappings = { { "ACT", "Australia/Darwin" }, { "AET", "Australia/Sydney" }, { "AGT", "America/Argentina/Buenos_Aires" }, { "ART", "Africa/Cairo" }, { "AST", "America/Anchorage" }, { "BET", "America/Sao_Paulo" }, { "BST", "Asia/Dhaka" }, { "CAT", "Africa/Harare" }, { "CNT", "America/St_Johns" }, { "CST", "America/Chicago" }, { "CTT", "Asia/Shanghai" }, { "EAT", "Africa/Addis_Ababa" }, { "ECT", "Europe/Paris" }, { "IET", "America/Indiana/Indianapolis" }, { "IST", "Asia/Kolkata" }, { "JST", "Asia/Tokyo" }, { "MIT", "Pacific/Apia" }, { "NET", "Asia/Yerevan" }, { "NST", "Pacific/Auckland" }, { "PLT", "Asia/Karachi" }, { "PNT", "America/Phoenix" }, { "PRT", "America/Puerto_Rico" }, { "PST", "America/Los_Angeles" }, { "SST", "Pacific/Guadalcanal" }, { "VST", "Asia/Ho_Chi_Minh" } };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final long UTC1900 = -2208988800L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final long UTC2037 = 2145916799L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final long LDT2037 = 2114380800L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static
/*      */   {
/*  249 */     String str = ((String)AccessController.doPrivileged(new GetPropertyAction("sun.timezone.ids.oldmapping", "false"))).toLowerCase(Locale.ROOT);
/*  250 */     USE_OLDMAPPING = (str.equals("yes")) || (str.equals("true"));
/*  251 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Object run() {
/*      */         try {
/*  254 */           String str = System.getProperty("java.home") + File.separator + "lib";
/*  255 */           DataInputStream localDataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(str, "tzdb.dat"))));Object localObject1 = null;
/*      */           try
/*      */           {
/*  258 */             ZoneInfoFile.load(localDataInputStream);
/*      */           }
/*      */           catch (Throwable localThrowable2)
/*      */           {
/*  255 */             localObject1 = localThrowable2;throw localThrowable2;
/*      */           }
/*      */           finally
/*      */           {
/*  259 */             if (localDataInputStream != null) if (localObject1 != null) try { localDataInputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localDataInputStream.close();
/*      */           }
/*  261 */         } catch (Exception localException) { throw new Error(localException);
/*      */         }
/*  263 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private static void addOldMapping() {
/*  269 */     for (String[] arrayOfString1 : oldMappings) {
/*  270 */       aliases.put(arrayOfString1[0], arrayOfString1[1]);
/*      */     }
/*  272 */     if (USE_OLDMAPPING) {
/*  273 */       aliases.put("EST", "America/New_York");
/*  274 */       aliases.put("MST", "America/Denver");
/*  275 */       aliases.put("HST", "Pacific/Honolulu");
/*      */     } else {
/*  277 */       zones.put("EST", new ZoneInfo("EST", -18000000));
/*  278 */       zones.put("MST", new ZoneInfo("MST", -25200000));
/*  279 */       zones.put("HST", new ZoneInfo("HST", -36000000));
/*      */     }
/*      */   }
/*      */   
/*      */   public static boolean useOldMapping() {
/*  284 */     return USE_OLDMAPPING;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void load(DataInputStream paramDataInputStream)
/*      */     throws ClassNotFoundException, IOException
/*      */   {
/*  294 */     if (paramDataInputStream.readByte() != 1) {
/*  295 */       throw new StreamCorruptedException("File format not recognised");
/*      */     }
/*      */     
/*  298 */     String str1 = paramDataInputStream.readUTF();
/*  299 */     if (!"TZDB".equals(str1)) {
/*  300 */       throw new StreamCorruptedException("File format not recognised");
/*      */     }
/*      */     
/*  303 */     int i = paramDataInputStream.readShort();
/*  304 */     for (int j = 0; j < i; j++) {
/*  305 */       versionId = paramDataInputStream.readUTF();
/*      */     }
/*      */     
/*      */ 
/*  309 */     j = paramDataInputStream.readShort();
/*  310 */     String[] arrayOfString = new String[j];
/*  311 */     for (int k = 0; k < j; k++) {
/*  312 */       arrayOfString[k] = paramDataInputStream.readUTF();
/*      */     }
/*      */     
/*  315 */     k = paramDataInputStream.readShort();
/*  316 */     ruleArray = new byte[k][];
/*  317 */     for (int m = 0; m < k; m++) {
/*  318 */       byte[] arrayOfByte = new byte[paramDataInputStream.readShort()];
/*  319 */       paramDataInputStream.readFully(arrayOfByte);
/*  320 */       ruleArray[m] = arrayOfByte;
/*      */     }
/*      */     int n;
/*  323 */     for (m = 0; m < i; m++) {
/*  324 */       j = paramDataInputStream.readShort();
/*  325 */       regions = new String[j];
/*  326 */       indices = new int[j];
/*  327 */       for (n = 0; n < j; n++) {
/*  328 */         regions[n] = arrayOfString[paramDataInputStream.readShort()];
/*  329 */         indices[n] = paramDataInputStream.readShort();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  334 */     zones.remove("ROC");
/*  335 */     for (m = 0; m < i; m++) {
/*  336 */       n = paramDataInputStream.readShort();
/*  337 */       aliases.clear();
/*  338 */       for (int i1 = 0; i1 < n; i1++) {
/*  339 */         String str2 = arrayOfString[paramDataInputStream.readShort()];
/*  340 */         String str3 = arrayOfString[paramDataInputStream.readShort()];
/*  341 */         aliases.put(str2, str3);
/*      */       }
/*      */     }
/*      */     
/*  345 */     addOldMapping();
/*      */   }
/*      */   
/*      */   public static ZoneInfo getZoneInfo(DataInput paramDataInput, String paramString) throws Exception
/*      */   {
/*  350 */     int i = paramDataInput.readByte();
/*      */     
/*  352 */     int j = paramDataInput.readInt();
/*  353 */     long[] arrayOfLong1 = new long[j];
/*  354 */     for (int k = 0; k < j; k++) {
/*  355 */       arrayOfLong1[k] = readEpochSec(paramDataInput);
/*      */     }
/*  357 */     int[] arrayOfInt1 = new int[j + 1];
/*  358 */     for (int m = 0; m < arrayOfInt1.length; m++) {
/*  359 */       arrayOfInt1[m] = readOffset(paramDataInput);
/*      */     }
/*  361 */     m = paramDataInput.readInt();
/*  362 */     long[] arrayOfLong2 = new long[m];
/*  363 */     for (int n = 0; n < m; n++) {
/*  364 */       arrayOfLong2[n] = readEpochSec(paramDataInput);
/*      */     }
/*  366 */     int[] arrayOfInt2 = new int[m + 1];
/*  367 */     for (int i1 = 0; i1 < arrayOfInt2.length; i1++) {
/*  368 */       arrayOfInt2[i1] = readOffset(paramDataInput);
/*      */     }
/*  370 */     i1 = paramDataInput.readByte();
/*  371 */     ZoneOffsetTransitionRule[] arrayOfZoneOffsetTransitionRule = new ZoneOffsetTransitionRule[i1];
/*  372 */     for (int i2 = 0; i2 < i1; i2++) {
/*  373 */       arrayOfZoneOffsetTransitionRule[i2] = new ZoneOffsetTransitionRule(paramDataInput);
/*      */     }
/*  375 */     return getZoneInfo(paramString, arrayOfLong1, arrayOfInt1, arrayOfLong2, arrayOfInt2, arrayOfZoneOffsetTransitionRule);
/*      */   }
/*      */   
/*      */   public static int readOffset(DataInput paramDataInput) throws IOException {
/*  379 */     int i = paramDataInput.readByte();
/*  380 */     return i == 127 ? paramDataInput.readInt() : i * 900;
/*      */   }
/*      */   
/*      */   static long readEpochSec(DataInput paramDataInput) throws IOException {
/*  384 */     int i = paramDataInput.readByte() & 0xFF;
/*  385 */     if (i == 255) {
/*  386 */       return paramDataInput.readLong();
/*      */     }
/*  388 */     int j = paramDataInput.readByte() & 0xFF;
/*  389 */     int k = paramDataInput.readByte() & 0xFF;
/*  390 */     long l = (i << 16) + (j << 8) + k;
/*  391 */     return l * 900L - 4575744000L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  410 */   private static final long CURRT = System.currentTimeMillis() / 1000L;
/*      */   
/*      */ 
/*      */ 
/*      */   static final int SECONDS_PER_DAY = 86400;
/*      */   
/*      */ 
/*      */   static final int DAYS_PER_CYCLE = 146097;
/*      */   
/*      */ 
/*      */   static final long DAYS_0000_TO_1970 = 719528L;
/*      */   
/*      */ 
/*      */ 
/*      */   private static ZoneInfo getZoneInfo(String paramString, long[] paramArrayOfLong1, int[] paramArrayOfInt1, long[] paramArrayOfLong2, int[] paramArrayOfInt2, ZoneOffsetTransitionRule[] paramArrayOfZoneOffsetTransitionRule)
/*      */   {
/*  426 */     int i = 0;
/*  427 */     int j = 0;
/*  428 */     int k = 0;
/*  429 */     int[] arrayOfInt1 = null;
/*  430 */     boolean bool = false;
/*      */     
/*      */ 
/*  433 */     if (paramArrayOfLong1.length > 0) {
/*  434 */       i = paramArrayOfInt1[(paramArrayOfInt1.length - 1)] * 1000;
/*  435 */       bool = paramArrayOfLong1[(paramArrayOfLong1.length - 1)] > CURRT;
/*      */     }
/*      */     else {
/*  438 */       i = paramArrayOfInt1[0] * 1000;
/*      */     }
/*      */     
/*  441 */     long[] arrayOfLong = null;
/*  442 */     int[] arrayOfInt2 = null;
/*  443 */     int m = 0;
/*  444 */     int n = 0;
/*      */     
/*  446 */     if (paramArrayOfLong2.length != 0) {
/*  447 */       arrayOfLong = new long['ú'];
/*  448 */       arrayOfInt2 = new int[100];
/*      */       
/*      */ 
/*      */ 
/*  452 */       int i1 = getYear(paramArrayOfLong2[(paramArrayOfLong2.length - 1)], paramArrayOfInt2[(paramArrayOfLong2.length - 1)]);
/*      */       
/*  454 */       int i2 = 0;int i3 = 1;
/*  455 */       while ((i2 < paramArrayOfLong2.length) && (paramArrayOfLong2[i2] < -2208988800L))
/*      */       {
/*  457 */         i2++;
/*      */       }
/*  459 */       if (i2 < paramArrayOfLong2.length)
/*      */       {
/*  461 */         if (i2 < paramArrayOfLong2.length) {
/*  462 */           arrayOfInt2[0] = (paramArrayOfInt1[(paramArrayOfInt1.length - 1)] * 1000);
/*  463 */           m = 1;
/*      */         }
/*      */         
/*      */ 
/*  467 */         m = addTrans(arrayOfLong, n++, arrayOfInt2, m, -2208988800L, paramArrayOfInt2[i2], 
/*      */         
/*      */ 
/*      */ 
/*  471 */           getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, -2208988800L));
/*      */       }
/*      */       long l1;
/*  474 */       for (; i2 < paramArrayOfLong2.length; i2++) {
/*  475 */         l1 = paramArrayOfLong2[i2];
/*  476 */         if (l1 > 2145916799L)
/*      */         {
/*  478 */           i1 = 2037;
/*  479 */           break;
/*      */         }
/*  481 */         while (i3 < paramArrayOfLong1.length)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  489 */           long l4 = paramArrayOfLong1[i3];
/*  490 */           if (l4 >= -2208988800L) {
/*  491 */             if (l4 > l1)
/*      */               break;
/*  493 */             if (l4 < l1) {
/*  494 */               if (m + 2 >= arrayOfInt2.length) {
/*  495 */                 arrayOfInt2 = Arrays.copyOf(arrayOfInt2, arrayOfInt2.length + 100);
/*      */               }
/*  497 */               if (n + 1 >= arrayOfLong.length) {
/*  498 */                 arrayOfLong = Arrays.copyOf(arrayOfLong, arrayOfLong.length + 100);
/*      */               }
/*  500 */               m = addTrans(arrayOfLong, n++, arrayOfInt2, m, l4, paramArrayOfInt2[i2], paramArrayOfInt1[(i3 + 1)]);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  507 */           i3++;
/*      */         }
/*  509 */         if (m + 2 >= arrayOfInt2.length) {
/*  510 */           arrayOfInt2 = Arrays.copyOf(arrayOfInt2, arrayOfInt2.length + 100);
/*      */         }
/*  512 */         if (n + 1 >= arrayOfLong.length) {
/*  513 */           arrayOfLong = Arrays.copyOf(arrayOfLong, arrayOfLong.length + 100);
/*      */         }
/*  515 */         m = addTrans(arrayOfLong, n++, arrayOfInt2, m, l1, paramArrayOfInt2[(i2 + 1)], 
/*      */         
/*      */ 
/*  518 */           getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, l1));
/*      */       }
/*      */       
/*      */       int i6;
/*  522 */       while (i3 < paramArrayOfLong1.length) {
/*  523 */         l1 = paramArrayOfLong1[i3];
/*  524 */         if (l1 >= -2208988800L) {
/*  525 */           i6 = paramArrayOfInt2[i2];
/*  526 */           int i8 = indexOf(arrayOfInt2, 0, m, i6);
/*  527 */           if (i8 == m)
/*  528 */             m++;
/*  529 */           arrayOfLong[(n++)] = (l1 * 1000L << 12 | i8 & 0xF);
/*      */         }
/*      */         
/*  532 */         i3++; }
/*      */       int i7;
/*  534 */       int i9; long l3; int i10; if (paramArrayOfZoneOffsetTransitionRule.length > 1)
/*      */       {
/*  536 */         while (i1++ < 2037) {
/*  537 */           for (ZoneOffsetTransitionRule localZoneOffsetTransitionRule : paramArrayOfZoneOffsetTransitionRule) {
/*  538 */             long l5 = localZoneOffsetTransitionRule.getTransitionEpochSecond(i1);
/*  539 */             if (m + 2 >= arrayOfInt2.length) {
/*  540 */               arrayOfInt2 = Arrays.copyOf(arrayOfInt2, arrayOfInt2.length + 100);
/*      */             }
/*  542 */             if (n + 1 >= arrayOfLong.length) {
/*  543 */               arrayOfLong = Arrays.copyOf(arrayOfLong, arrayOfLong.length + 100);
/*      */             }
/*  545 */             m = addTrans(arrayOfLong, n++, arrayOfInt2, m, l5, 
/*      */             
/*      */ 
/*  548 */               localZoneOffsetTransitionRule.offsetAfter, 
/*  549 */               localZoneOffsetTransitionRule.standardOffset);
/*      */           }
/*      */         }
/*  552 */         ??? = paramArrayOfZoneOffsetTransitionRule[(paramArrayOfZoneOffsetTransitionRule.length - 2)];
/*  553 */         Object localObject2 = paramArrayOfZoneOffsetTransitionRule[(paramArrayOfZoneOffsetTransitionRule.length - 1)];
/*  554 */         arrayOfInt1 = new int[10];
/*  555 */         if ((((ZoneOffsetTransitionRule)???).offsetAfter - ((ZoneOffsetTransitionRule)???).offsetBefore < 0) && 
/*  556 */           (((ZoneOffsetTransitionRule)localObject2).offsetAfter - ((ZoneOffsetTransitionRule)localObject2).offsetBefore > 0))
/*      */         {
/*  558 */           Object localObject3 = ???;
/*  559 */           ??? = localObject2;
/*  560 */           localObject2 = localObject3;
/*      */         }
/*  562 */         arrayOfInt1[0] = (((ZoneOffsetTransitionRule)???).month - 1);
/*  563 */         i7 = ((ZoneOffsetTransitionRule)???).dom;
/*  564 */         i9 = ((ZoneOffsetTransitionRule)???).dow;
/*  565 */         if (i9 == -1) {
/*  566 */           arrayOfInt1[1] = i7;
/*  567 */           arrayOfInt1[2] = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*  577 */         else if ((i7 < 0) || (i7 >= 24)) {
/*  578 */           arrayOfInt1[1] = -1;
/*  579 */           arrayOfInt1[2] = toCalendarDOW[i9];
/*      */         } else {
/*  581 */           arrayOfInt1[1] = i7;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  586 */           arrayOfInt1[2] = (-toCalendarDOW[i9]);
/*      */         }
/*      */         
/*  589 */         arrayOfInt1[3] = (((ZoneOffsetTransitionRule)???).secondOfDay * 1000);
/*  590 */         arrayOfInt1[4] = toSTZTime[???.timeDefinition];
/*  591 */         arrayOfInt1[5] = (((ZoneOffsetTransitionRule)localObject2).month - 1);
/*  592 */         i7 = ((ZoneOffsetTransitionRule)localObject2).dom;
/*  593 */         i9 = ((ZoneOffsetTransitionRule)localObject2).dow;
/*  594 */         if (i9 == -1) {
/*  595 */           arrayOfInt1[6] = i7;
/*  596 */           arrayOfInt1[7] = 0;
/*      */ 
/*      */         }
/*  599 */         else if ((i7 < 0) || (i7 >= 24)) {
/*  600 */           arrayOfInt1[6] = -1;
/*  601 */           arrayOfInt1[7] = toCalendarDOW[i9];
/*      */         } else {
/*  603 */           arrayOfInt1[6] = i7;
/*  604 */           arrayOfInt1[7] = (-toCalendarDOW[i9]);
/*      */         }
/*      */         
/*  607 */         arrayOfInt1[8] = (((ZoneOffsetTransitionRule)localObject2).secondOfDay * 1000);
/*  608 */         arrayOfInt1[9] = toSTZTime[localObject2.timeDefinition];
/*  609 */         j = (((ZoneOffsetTransitionRule)???).offsetAfter - ((ZoneOffsetTransitionRule)???).offsetBefore) * 1000;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  621 */         if ((arrayOfInt1[2] == 6) && (arrayOfInt1[3] == 0) && (
/*  622 */           (paramString.equals("Asia/Amman")) || 
/*  623 */           (paramString.equals("Asia/Gaza")) || 
/*  624 */           (paramString.equals("Asia/Hebron")))) {
/*  625 */           arrayOfInt1[2] = 5;
/*  626 */           arrayOfInt1[3] = 86400000;
/*      */         }
/*      */         
/*      */ 
/*  630 */         if ((arrayOfInt1[2] == 7) && (arrayOfInt1[3] == 0) && (
/*  631 */           (paramString.equals("Asia/Amman")) || 
/*  632 */           (paramString.equals("Asia/Gaza")) || 
/*  633 */           (paramString.equals("Asia/Hebron")))) {
/*  634 */           arrayOfInt1[2] = 6;
/*  635 */           arrayOfInt1[3] = 86400000;
/*      */         }
/*      */         
/*  638 */         if ((arrayOfInt1[7] == 6) && (arrayOfInt1[8] == 0) && 
/*  639 */           (paramString.equals("Africa/Cairo"))) {
/*  640 */           arrayOfInt1[7] = 5;
/*  641 */           arrayOfInt1[8] = 86400000;
/*      */         }
/*      */       }
/*  644 */       else if (n > 0) {
/*  645 */         if (i1 < 2037)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  650 */           long l2 = 2114380800L - i / 1000;
/*      */           
/*  652 */           i7 = indexOf(arrayOfInt2, 0, m, i / 1000);
/*  653 */           if (i7 == m)
/*  654 */             m++;
/*  655 */           arrayOfLong[(n++)] = (l2 * 1000L << 12 | i7 & 0xF);
/*      */ 
/*      */         }
/*  658 */         else if (paramArrayOfLong2.length > 2)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  675 */           int i4 = paramArrayOfLong2.length;
/*  676 */           l3 = paramArrayOfLong2[(i4 - 2)];
/*  677 */           i9 = paramArrayOfInt2[(i4 - 2 + 1)];
/*  678 */           i10 = getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, l3);
/*  679 */           long l6 = paramArrayOfLong2[(i4 - 1)];
/*  680 */           int i13 = paramArrayOfInt2[(i4 - 1 + 1)];
/*  681 */           int i14 = getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, l6);
/*  682 */           if ((i9 > i10) && (i13 == i14))
/*      */           {
/*  684 */             i4 = paramArrayOfLong2.length - 2;
/*  685 */             ZoneOffset localZoneOffset1 = ZoneOffset.ofTotalSeconds(paramArrayOfInt2[i4]);
/*  686 */             ZoneOffset localZoneOffset2 = ZoneOffset.ofTotalSeconds(paramArrayOfInt2[(i4 + 1)]);
/*  687 */             LocalDateTime localLocalDateTime1 = LocalDateTime.ofEpochSecond(paramArrayOfLong2[i4], 0, localZoneOffset1);
/*      */             LocalDateTime localLocalDateTime2;
/*  689 */             if (localZoneOffset2.getTotalSeconds() > localZoneOffset1.getTotalSeconds()) {
/*  690 */               localLocalDateTime2 = localLocalDateTime1;
/*      */             } else {
/*  692 */               localLocalDateTime2 = localLocalDateTime1.plusSeconds(paramArrayOfInt2[(i4 + 1)] - paramArrayOfInt2[i4]);
/*      */             }
/*      */             
/*  695 */             i4 = paramArrayOfLong2.length - 1;
/*  696 */             localZoneOffset1 = ZoneOffset.ofTotalSeconds(paramArrayOfInt2[i4]);
/*  697 */             localZoneOffset2 = ZoneOffset.ofTotalSeconds(paramArrayOfInt2[(i4 + 1)]);
/*  698 */             localLocalDateTime1 = LocalDateTime.ofEpochSecond(paramArrayOfLong2[i4], 0, localZoneOffset1);
/*      */             LocalDateTime localLocalDateTime3;
/*  700 */             if (localZoneOffset2.getTotalSeconds() > localZoneOffset1.getTotalSeconds()) {
/*  701 */               localLocalDateTime3 = localLocalDateTime1.plusSeconds(paramArrayOfInt2[(i4 + 1)] - paramArrayOfInt2[i4]);
/*      */             } else {
/*  703 */               localLocalDateTime3 = localLocalDateTime1;
/*      */             }
/*  705 */             arrayOfInt1 = new int[10];
/*  706 */             arrayOfInt1[0] = (localLocalDateTime2.getMonthValue() - 1);
/*  707 */             arrayOfInt1[1] = localLocalDateTime2.getDayOfMonth();
/*  708 */             arrayOfInt1[2] = 0;
/*  709 */             arrayOfInt1[3] = (localLocalDateTime2.toLocalTime().toSecondOfDay() * 1000);
/*  710 */             arrayOfInt1[4] = 0;
/*  711 */             arrayOfInt1[5] = (localLocalDateTime3.getMonthValue() - 1);
/*  712 */             arrayOfInt1[6] = localLocalDateTime3.getDayOfMonth();
/*  713 */             arrayOfInt1[7] = 0;
/*  714 */             arrayOfInt1[8] = (localLocalDateTime3.toLocalTime().toSecondOfDay() * 1000);
/*  715 */             arrayOfInt1[9] = 0;
/*  716 */             j = (i9 - i10) * 1000;
/*      */           }
/*      */         }
/*      */       }
/*  720 */       if ((arrayOfLong != null) && (arrayOfLong.length != n)) {
/*  721 */         if (n == 0) {
/*  722 */           arrayOfLong = null;
/*      */         } else {
/*  724 */           arrayOfLong = Arrays.copyOf(arrayOfLong, n);
/*      */         }
/*      */       }
/*  727 */       if ((arrayOfInt2 != null) && (arrayOfInt2.length != m)) {
/*  728 */         if (m == 0) {
/*  729 */           arrayOfInt2 = null;
/*      */         } else {
/*  731 */           arrayOfInt2 = Arrays.copyOf(arrayOfInt2, m);
/*      */         }
/*      */       }
/*  734 */       if (arrayOfLong != null) {
/*  735 */         Checksum localChecksum = new Checksum(null);
/*  736 */         for (i2 = 0; i2 < arrayOfLong.length; i2++) {
/*  737 */           l3 = arrayOfLong[i2];
/*  738 */           i9 = (int)(l3 >>> 4 & 0xF);
/*  739 */           i10 = i9 == 0 ? 0 : arrayOfInt2[i9];
/*  740 */           int i11 = (int)(l3 & 0xF);
/*  741 */           int i12 = arrayOfInt2[i11];
/*  742 */           long l7 = l3 >> 12;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  748 */           localChecksum.update(l7 + i11);
/*  749 */           localChecksum.update(i11);
/*  750 */           localChecksum.update(i9 == 0 ? -1 : i9);
/*      */         }
/*  752 */         k = (int)localChecksum.getValue();
/*      */       }
/*      */     }
/*  755 */     return new ZoneInfo(paramString, i, j, k, arrayOfLong, arrayOfInt2, arrayOfInt1, bool);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int getStandardOffset(long[] paramArrayOfLong, int[] paramArrayOfInt, long paramLong)
/*      */   {
/*  771 */     for (int i = 0; 
/*  772 */         i < paramArrayOfLong.length; i++) {
/*  773 */       if (paramLong < paramArrayOfLong[i]) {
/*      */         break;
/*      */       }
/*      */     }
/*  777 */     return paramArrayOfInt[i];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int getYear(long paramLong, int paramInt)
/*      */   {
/*  785 */     long l1 = paramLong + paramInt;
/*  786 */     long l2 = Math.floorDiv(l1, 86400L);
/*  787 */     long l3 = l2 + 719528L;
/*      */     
/*  789 */     l3 -= 60L;
/*  790 */     long l4 = 0L;
/*  791 */     if (l3 < 0L)
/*      */     {
/*  793 */       l5 = (l3 + 1L) / 146097L - 1L;
/*  794 */       l4 = l5 * 400L;
/*  795 */       l3 += -l5 * 146097L;
/*      */     }
/*  797 */     long l5 = (400L * l3 + 591L) / 146097L;
/*  798 */     long l6 = l3 - (365L * l5 + l5 / 4L - l5 / 100L + l5 / 400L);
/*  799 */     if (l6 < 0L)
/*      */     {
/*  801 */       l5 -= 1L;
/*  802 */       l6 = l3 - (365L * l5 + l5 / 4L - l5 / 100L + l5 / 400L);
/*      */     }
/*  804 */     l5 += l4;
/*  805 */     int i = (int)l6;
/*      */     
/*  807 */     int j = (i * 5 + 2) / 153;
/*  808 */     int k = (j + 2) % 12 + 1;
/*  809 */     int m = i - (j * 306 + 5) / 10 + 1;
/*  810 */     l5 += j / 10;
/*  811 */     return (int)l5;
/*      */   }
/*      */   
/*  814 */   private static final int[] toCalendarDOW = { -1, 2, 3, 4, 5, 6, 7, 1 };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  825 */   private static final int[] toSTZTime = { 2, 0, 1 };
/*      */   
/*      */   private static final long OFFSET_MASK = 15L;
/*      */   
/*      */   private static final long DST_MASK = 240L;
/*      */   
/*      */   private static final int DST_NSHIFT = 4;
/*      */   
/*      */   private static final int TRANSITION_NSHIFT = 12;
/*      */   
/*      */   private static final int LASTYEAR = 2037;
/*      */   
/*      */   private static int indexOf(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  839 */     paramInt3 *= 1000;
/*  840 */     for (; paramInt1 < paramInt2; paramInt1++) {
/*  841 */       if (paramArrayOfInt[paramInt1] == paramInt3)
/*  842 */         return paramInt1;
/*      */     }
/*  844 */     paramArrayOfInt[paramInt1] = paramInt3;
/*  845 */     return paramInt1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static int addTrans(long[] paramArrayOfLong, int paramInt1, int[] paramArrayOfInt, int paramInt2, long paramLong, int paramInt3, int paramInt4)
/*      */   {
/*  852 */     int i = indexOf(paramArrayOfInt, 0, paramInt2, paramInt3);
/*  853 */     if (i == paramInt2)
/*  854 */       paramInt2++;
/*  855 */     int j = 0;
/*  856 */     if (paramInt3 != paramInt4) {
/*  857 */       j = indexOf(paramArrayOfInt, 1, paramInt2, paramInt3 - paramInt4);
/*  858 */       if (j == paramInt2)
/*  859 */         paramInt2++;
/*      */     }
/*  861 */     paramArrayOfLong[paramInt1] = (paramLong * 1000L << 12 | j << 4 & 0xF0 | i & 0xF);
/*      */     
/*      */ 
/*  864 */     return paramInt2;
/*      */   }
/*      */   
/*      */   private static class Checksum extends CRC32
/*      */   {
/*      */     public void update(int paramInt) {
/*  870 */       byte[] arrayOfByte = new byte[4];
/*  871 */       arrayOfByte[0] = ((byte)(paramInt >>> 24));
/*  872 */       arrayOfByte[1] = ((byte)(paramInt >>> 16));
/*  873 */       arrayOfByte[2] = ((byte)(paramInt >>> 8));
/*  874 */       arrayOfByte[3] = ((byte)paramInt);
/*  875 */       update(arrayOfByte);
/*      */     }
/*      */     
/*  878 */     void update(long paramLong) { byte[] arrayOfByte = new byte[8];
/*  879 */       arrayOfByte[0] = ((byte)(int)(paramLong >>> 56));
/*  880 */       arrayOfByte[1] = ((byte)(int)(paramLong >>> 48));
/*  881 */       arrayOfByte[2] = ((byte)(int)(paramLong >>> 40));
/*  882 */       arrayOfByte[3] = ((byte)(int)(paramLong >>> 32));
/*  883 */       arrayOfByte[4] = ((byte)(int)(paramLong >>> 24));
/*  884 */       arrayOfByte[5] = ((byte)(int)(paramLong >>> 16));
/*  885 */       arrayOfByte[6] = ((byte)(int)(paramLong >>> 8));
/*  886 */       arrayOfByte[7] = ((byte)(int)paramLong);
/*  887 */       update(arrayOfByte);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ZoneOffsetTransitionRule
/*      */   {
/*      */     private final int month;
/*      */     private final byte dom;
/*      */     private final int dow;
/*      */     private final int secondOfDay;
/*      */     private final boolean timeEndOfDay;
/*      */     private final int timeDefinition;
/*      */     private final int standardOffset;
/*      */     private final int offsetBefore;
/*      */     private final int offsetAfter;
/*      */     
/*      */     ZoneOffsetTransitionRule(DataInput paramDataInput) throws IOException {
/*  904 */       int i = paramDataInput.readInt();
/*  905 */       int j = (i & 0x380000) >>> 19;
/*  906 */       int k = (i & 0x7C000) >>> 14;
/*  907 */       int m = (i & 0xFF0) >>> 4;
/*  908 */       int n = (i & 0xC) >>> 2;
/*  909 */       int i1 = i & 0x3;
/*      */       
/*  911 */       this.month = (i >>> 28);
/*  912 */       this.dom = ((byte)(((i & 0xFC00000) >>> 22) - 32));
/*  913 */       this.dow = (j == 0 ? -1 : j);
/*  914 */       this.secondOfDay = (k == 31 ? paramDataInput.readInt() : k * 3600);
/*  915 */       this.timeEndOfDay = (k == 24);
/*  916 */       this.timeDefinition = ((i & 0x3000) >>> 12);
/*      */       
/*  918 */       this.standardOffset = (m == 255 ? paramDataInput.readInt() : (m - 128) * 900);
/*  919 */       this.offsetBefore = (n == 3 ? paramDataInput.readInt() : this.standardOffset + n * 1800);
/*  920 */       this.offsetAfter = (i1 == 3 ? paramDataInput.readInt() : this.standardOffset + i1 * 1800);
/*      */     }
/*      */     
/*      */     long getTransitionEpochSecond(int paramInt) {
/*  924 */       long l = 0L;
/*  925 */       if (this.dom < 0) {
/*  926 */         l = toEpochDay(paramInt, this.month, lengthOfMonth(paramInt, this.month) + 1 + this.dom);
/*  927 */         if (this.dow != -1) {
/*  928 */           l = previousOrSame(l, this.dow);
/*      */         }
/*      */       } else {
/*  931 */         l = toEpochDay(paramInt, this.month, this.dom);
/*  932 */         if (this.dow != -1) {
/*  933 */           l = nextOrSame(l, this.dow);
/*      */         }
/*      */       }
/*  936 */       if (this.timeEndOfDay) {
/*  937 */         l += 1L;
/*      */       }
/*  939 */       int i = 0;
/*  940 */       switch (this.timeDefinition) {
/*      */       case 0: 
/*  942 */         i = 0;
/*  943 */         break;
/*      */       case 1: 
/*  945 */         i = -this.offsetBefore;
/*  946 */         break;
/*      */       case 2: 
/*  948 */         i = -this.standardOffset;
/*      */       }
/*      */       
/*  951 */       return l * 86400L + this.secondOfDay + i;
/*      */     }
/*      */     
/*      */     static final boolean isLeapYear(int paramInt) {
/*  955 */       return ((paramInt & 0x3) == 0) && ((paramInt % 100 != 0) || (paramInt % 400 == 0));
/*      */     }
/*      */     
/*      */     static final int lengthOfMonth(int paramInt1, int paramInt2) {
/*  959 */       switch (paramInt2) {
/*      */       case 2: 
/*  961 */         return isLeapYear(paramInt1) ? 29 : 28;
/*      */       case 4: 
/*      */       case 6: 
/*      */       case 9: 
/*      */       case 11: 
/*  966 */         return 30;
/*      */       }
/*  968 */       return 31;
/*      */     }
/*      */     
/*      */     static final long toEpochDay(int paramInt1, int paramInt2, int paramInt3)
/*      */     {
/*  973 */       long l1 = paramInt1;
/*  974 */       long l2 = paramInt2;
/*  975 */       long l3 = 0L;
/*  976 */       l3 += 365L * l1;
/*  977 */       if (l1 >= 0L) {
/*  978 */         l3 += (l1 + 3L) / 4L - (l1 + 99L) / 100L + (l1 + 399L) / 400L;
/*      */       } else {
/*  980 */         l3 -= l1 / -4L - l1 / -100L + l1 / -400L;
/*      */       }
/*  982 */       l3 += (367L * l2 - 362L) / 12L;
/*  983 */       l3 += paramInt3 - 1;
/*  984 */       if (l2 > 2L) {
/*  985 */         l3 -= 1L;
/*  986 */         if (!isLeapYear(paramInt1)) {
/*  987 */           l3 -= 1L;
/*      */         }
/*      */       }
/*  990 */       return l3 - 719528L;
/*      */     }
/*      */     
/*      */     static final long previousOrSame(long paramLong, int paramInt) {
/*  994 */       return adjust(paramLong, paramInt, 1);
/*      */     }
/*      */     
/*      */     static final long nextOrSame(long paramLong, int paramInt) {
/*  998 */       return adjust(paramLong, paramInt, 0);
/*      */     }
/*      */     
/*      */     static final long adjust(long paramLong, int paramInt1, int paramInt2) {
/* 1002 */       int i = (int)Math.floorMod(paramLong + 3L, 7L) + 1;
/* 1003 */       if ((paramInt2 < 2) && (i == paramInt1)) {
/* 1004 */         return paramLong;
/*      */       }
/* 1006 */       if ((paramInt2 & 0x1) == 0) {
/* 1007 */         j = i - paramInt1;
/* 1008 */         return paramLong + (j >= 0 ? 7 - j : -j);
/*      */       }
/* 1010 */       int j = paramInt1 - i;
/* 1011 */       return paramLong - (j >= 0 ? 7 - j : -j);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\util\calendar\ZoneInfoFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */