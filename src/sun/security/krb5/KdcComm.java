/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.Security;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import sun.security.krb5.internal.KRBError;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.NetClient;
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
/*     */ public final class KdcComm
/*     */ {
/*     */   private static int defaultKdcRetryLimit;
/*     */   private static int defaultKdcTimeout;
/*     */   private static int defaultUdpPrefLimit;
/*  75 */   private static final boolean DEBUG = Krb5.DEBUG;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final String BAD_POLICY_KEY = "krb5.kdc.bad.policy";
/*     */   
/*     */ 
/*     */ 
/*     */   private static enum BpType
/*     */   {
/*  85 */     NONE,  TRY_LAST,  TRY_LESS;
/*     */     private BpType() {} }
/*  87 */   private static int tryLessMaxRetries = 1;
/*  88 */   private static int tryLessTimeout = 5000;
/*     */   private static BpType badPolicy;
/*     */   private String realm;
/*     */   
/*     */   static {
/*  93 */     initStatic();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void initStatic()
/*     */   {
/* 100 */     String str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run() {
/* 103 */         return Security.getProperty("krb5.kdc.bad.policy");
/*     */       }
/*     */     });
/* 106 */     if (str1 != null) {
/* 107 */       str1 = str1.toLowerCase(Locale.ENGLISH);
/* 108 */       String[] arrayOfString1 = str1.split(":");
/* 109 */       if ("tryless".equals(arrayOfString1[0])) {
/* 110 */         if (arrayOfString1.length > 1) {
/* 111 */           String[] arrayOfString2 = arrayOfString1[1].split(",");
/*     */           try {
/* 113 */             int k = Integer.parseInt(arrayOfString2[0]);
/* 114 */             if (arrayOfString2.length > 1) {
/* 115 */               tryLessTimeout = Integer.parseInt(arrayOfString2[1]);
/*     */             }
/*     */             
/* 118 */             tryLessMaxRetries = k;
/*     */           }
/*     */           catch (NumberFormatException localNumberFormatException)
/*     */           {
/* 122 */             if (DEBUG) {
/* 123 */               System.out.println("Invalid krb5.kdc.bad.policy parameter for tryLess: " + str1 + ", use default");
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 129 */         badPolicy = BpType.TRY_LESS;
/* 130 */       } else if ("trylast".equals(arrayOfString1[0])) {
/* 131 */         badPolicy = BpType.TRY_LAST;
/*     */       } else {
/* 133 */         badPolicy = BpType.NONE;
/*     */       }
/*     */     } else {
/* 136 */       badPolicy = BpType.NONE;
/*     */     }
/*     */     
/*     */ 
/* 140 */     int i = -1;
/* 141 */     int j = -1;
/* 142 */     int m = -1;
/*     */     try
/*     */     {
/* 145 */       Config localConfig = Config.getInstance();
/* 146 */       String str2 = localConfig.get(new String[] { "libdefaults", "kdc_timeout" });
/* 147 */       i = parseTimeString(str2);
/*     */       
/* 149 */       str2 = localConfig.get(new String[] { "libdefaults", "max_retries" });
/* 150 */       j = parsePositiveIntString(str2);
/* 151 */       str2 = localConfig.get(new String[] { "libdefaults", "udp_preference_limit" });
/* 152 */       m = parsePositiveIntString(str2);
/*     */     }
/*     */     catch (Exception localException) {
/* 155 */       if (DEBUG) {
/* 156 */         System.out.println("Exception in getting KDC communication settings, using default value " + localException
/*     */         
/* 158 */           .getMessage());
/*     */       }
/*     */     }
/* 161 */     defaultKdcTimeout = i > 0 ? i : 30000;
/* 162 */     defaultKdcRetryLimit = j > 0 ? j : 3;
/*     */     
/*     */ 
/* 165 */     if (m < 0) {
/* 166 */       defaultUdpPrefLimit = 1465;
/* 167 */     } else if (m > 32700) {
/* 168 */       defaultUdpPrefLimit = 32700;
/*     */     } else {
/* 170 */       defaultUdpPrefLimit = m;
/*     */     }
/*     */     
/* 173 */     KdcAccessibility.access$000();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public KdcComm(String paramString)
/*     */     throws KrbException
/*     */   {
/* 182 */     if (paramString == null) {
/* 183 */       paramString = Config.getInstance().getDefaultRealm();
/* 184 */       if (paramString == null) {
/* 185 */         throw new KrbException(60, "Cannot find default realm");
/*     */       }
/*     */     }
/*     */     
/* 189 */     this.realm = paramString;
/*     */   }
/*     */   
/*     */   public byte[] send(byte[] paramArrayOfByte) throws IOException, KrbException
/*     */   {
/* 194 */     int i = getRealmSpecificValue(this.realm, "udp_preference_limit", defaultUdpPrefLimit);
/*     */     
/*     */ 
/* 197 */     boolean bool = (i > 0) && (paramArrayOfByte != null) && (paramArrayOfByte.length > i);
/*     */     
/*     */ 
/* 200 */     return send(paramArrayOfByte, bool);
/*     */   }
/*     */   
/*     */   private byte[] send(byte[] paramArrayOfByte, boolean paramBoolean)
/*     */     throws IOException, KrbException
/*     */   {
/* 206 */     if (paramArrayOfByte == null)
/* 207 */       return null;
/* 208 */     Config localConfig = Config.getInstance();
/*     */     
/* 210 */     if (this.realm == null) {
/* 211 */       this.realm = localConfig.getDefaultRealm();
/* 212 */       if (this.realm == null) {
/* 213 */         throw new KrbException(60, "Cannot find default realm");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 218 */     String str = localConfig.getKDCList(this.realm);
/* 219 */     if (str == null) {
/* 220 */       throw new KrbException("Cannot get kdc for realm " + this.realm);
/*     */     }
/*     */     
/* 223 */     Iterator localIterator = KdcAccessibility.list(str).iterator();
/* 224 */     if (!localIterator.hasNext()) {
/* 225 */       throw new KrbException("Cannot get kdc for realm " + this.realm);
/*     */     }
/* 227 */     byte[] arrayOfByte = null;
/*     */     try {
/* 229 */       arrayOfByte = sendIfPossible(paramArrayOfByte, (String)localIterator.next(), paramBoolean);
/*     */     } catch (Exception localException1) {
/* 231 */       int i = 0;
/* 232 */       while (localIterator.hasNext()) {
/*     */         try {
/* 234 */           arrayOfByte = sendIfPossible(paramArrayOfByte, (String)localIterator.next(), paramBoolean);
/* 235 */           i = 1;
/*     */         }
/*     */         catch (Exception localException2) {}
/*     */       }
/* 239 */       if (i == 0) throw localException1;
/*     */     }
/* 241 */     if (arrayOfByte == null) {
/* 242 */       throw new IOException("Cannot get a KDC reply");
/*     */     }
/* 244 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */   private byte[] sendIfPossible(byte[] paramArrayOfByte, String paramString, boolean paramBoolean)
/*     */     throws IOException, KrbException
/*     */   {
/*     */     try
/*     */     {
/* 253 */       byte[] arrayOfByte = send(paramArrayOfByte, paramString, paramBoolean);
/* 254 */       KRBError localKRBError = null;
/*     */       try {
/* 256 */         localKRBError = new KRBError(arrayOfByte);
/*     */       }
/*     */       catch (Exception localException2) {}
/*     */       
/* 260 */       if ((localKRBError != null) && (localKRBError.getErrorCode() == 52))
/*     */       {
/* 262 */         arrayOfByte = send(paramArrayOfByte, paramString, true);
/*     */       }
/* 264 */       KdcAccessibility.removeBad(paramString);
/* 265 */       return arrayOfByte;
/*     */     } catch (Exception localException1) {
/* 267 */       if (DEBUG) {
/* 268 */         System.out.println(">>> KrbKdcReq send: error trying " + paramString);
/*     */         
/* 270 */         localException1.printStackTrace(System.out);
/*     */       }
/* 272 */       KdcAccessibility.addBad(paramString);
/* 273 */       throw localException1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private byte[] send(byte[] paramArrayOfByte, String paramString, boolean paramBoolean)
/*     */     throws IOException, KrbException
/*     */   {
/* 282 */     if (paramArrayOfByte == null) {
/* 283 */       return null;
/*     */     }
/* 285 */     int i = 88;
/* 286 */     int j = getRealmSpecificValue(this.realm, "max_retries", defaultKdcRetryLimit);
/*     */     
/* 288 */     int k = getRealmSpecificValue(this.realm, "kdc_timeout", defaultKdcTimeout);
/*     */     
/* 290 */     if ((badPolicy == BpType.TRY_LESS) && 
/* 291 */       (KdcAccessibility.isBad(paramString))) {
/* 292 */       if (j > tryLessMaxRetries) {
/* 293 */         j = tryLessMaxRetries;
/*     */       }
/* 295 */       if (k > tryLessTimeout) {
/* 296 */         k = tryLessTimeout;
/*     */       }
/*     */     }
/*     */     
/* 300 */     String str1 = null;
/* 301 */     String str2 = null;
/*     */     int m;
/* 303 */     if (paramString.charAt(0) == '[') {
/* 304 */       m = paramString.indexOf(']', 1);
/* 305 */       if (m == -1) {
/* 306 */         throw new IOException("Illegal KDC: " + paramString);
/*     */       }
/* 308 */       str1 = paramString.substring(1, m);
/* 309 */       if (m != paramString.length() - 1) {
/* 310 */         if (paramString.charAt(m + 1) != ':') {
/* 311 */           throw new IOException("Illegal KDC: " + paramString);
/*     */         }
/* 313 */         str2 = paramString.substring(m + 2);
/*     */       }
/*     */     } else {
/* 316 */       m = paramString.indexOf(':');
/* 317 */       if (m == -1) {
/* 318 */         str1 = paramString;
/*     */       } else {
/* 320 */         int n = paramString.indexOf(':', m + 1);
/* 321 */         if (n > 0) {
/* 322 */           str1 = paramString;
/*     */         } else {
/* 324 */           str1 = paramString.substring(0, m);
/* 325 */           str2 = paramString.substring(m + 1);
/*     */         }
/*     */       }
/*     */     }
/* 329 */     if (str2 != null) {
/* 330 */       m = parsePositiveIntString(str2);
/* 331 */       if (m > 0) {
/* 332 */         i = m;
/*     */       }
/*     */     }
/* 335 */     if (DEBUG) {
/* 336 */       System.out.println(">>> KrbKdcReq send: kdc=" + str1 + (paramBoolean ? " TCP:" : " UDP:") + i + ", timeout=" + k + ", number of retries =" + j + ", #bytes=" + paramArrayOfByte.length);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 345 */     KdcCommunication localKdcCommunication = new KdcCommunication(str1, i, paramBoolean, k, j, paramArrayOfByte);
/*     */     try
/*     */     {
/* 348 */       byte[] arrayOfByte = (byte[])AccessController.doPrivileged(localKdcCommunication);
/* 349 */       if (DEBUG) {
/* 350 */         System.out.println(">>> KrbKdcReq send: #bytes read=" + (arrayOfByte != null ? arrayOfByte.length : 0));
/*     */       }
/*     */       
/* 353 */       return arrayOfByte;
/*     */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 355 */       Exception localException = localPrivilegedActionException.getException();
/* 356 */       if ((localException instanceof IOException)) {
/* 357 */         throw ((IOException)localException);
/*     */       }
/* 359 */       throw ((KrbException)localException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class KdcCommunication
/*     */     implements PrivilegedExceptionAction<byte[]>
/*     */   {
/*     */     private String kdc;
/*     */     private int port;
/*     */     private boolean useTCP;
/*     */     private int timeout;
/*     */     private int retries;
/*     */     private byte[] obuf;
/*     */     
/*     */     public KdcCommunication(String paramString, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
/*     */     {
/* 376 */       this.kdc = paramString;
/* 377 */       this.port = paramInt1;
/* 378 */       this.useTCP = paramBoolean;
/* 379 */       this.timeout = paramInt2;
/* 380 */       this.retries = paramInt3;
/* 381 */       this.obuf = paramArrayOfByte;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public byte[] run()
/*     */       throws IOException, KrbException
/*     */     {
/* 389 */       byte[] arrayOfByte = null;
/*     */       
/* 391 */       for (int i = 1; i <= this.retries; i++) {
/* 392 */         String str = this.useTCP ? "TCP" : "UDP";
/* 393 */         NetClient localNetClient = NetClient.getInstance(str, this.kdc, this.port, this.timeout);Object localObject1 = null;
/*     */         try {
/* 395 */           if (KdcComm.DEBUG) {
/* 396 */             System.out.println(">>> KDCCommunication: kdc=" + this.kdc + " " + str + ":" + this.port + ", timeout=" + this.timeout + ",Attempt =" + i + ", #bytes=" + this.obuf.length);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 407 */             localNetClient.send(this.obuf);
/*     */             
/*     */ 
/*     */ 
/* 411 */             arrayOfByte = localNetClient.receive();
/*     */           }
/*     */           catch (SocketTimeoutException localSocketTimeoutException) {
/* 414 */             if (KdcComm.DEBUG) {
/* 415 */               System.out.println("SocketTimeOutException with attempt: " + i);
/*     */             }
/*     */             
/* 418 */             if (i == this.retries) {
/* 419 */               arrayOfByte = null;
/* 420 */               throw localSocketTimeoutException;
/*     */             }
/*     */             
/* 423 */             if (localNetClient == null) continue; } if (localObject1 != null) try { localNetClient.close(); } catch (Throwable localThrowable2) { ((Throwable)localObject1).addSuppressed(localThrowable2); } else localNetClient.close();
/*     */         }
/*     */         catch (Throwable localThrowable3)
/*     */         {
/* 393 */           localObject1 = localThrowable3;throw localThrowable3;
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
/*     */         }
/*     */         finally
/*     */         {
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
/* 423 */           if (localNetClient != null) if (localObject1 != null) try { localNetClient.close(); } catch (Throwable localThrowable4) { ((Throwable)localObject1).addSuppressed(localThrowable4); } else localNetClient.close();
/*     */         } }
/* 425 */       return arrayOfByte;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int parseTimeString(String paramString)
/*     */   {
/* 437 */     if (paramString == null) {
/* 438 */       return -1;
/*     */     }
/* 440 */     if (paramString.endsWith("s")) {
/* 441 */       int i = parsePositiveIntString(paramString.substring(0, paramString.length() - 1));
/* 442 */       return i < 0 ? -1 : i * 1000;
/*     */     }
/* 444 */     return parsePositiveIntString(paramString);
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
/*     */ 
/*     */   private int getRealmSpecificValue(String paramString1, String paramString2, int paramInt)
/*     */   {
/* 461 */     int i = paramInt;
/*     */     
/* 463 */     if (paramString1 == null) { return i;
/*     */     }
/* 465 */     int j = -1;
/*     */     try
/*     */     {
/* 468 */       String str = Config.getInstance().get(new String[] { "realms", paramString1, paramString2 });
/* 469 */       if (paramString2.equals("kdc_timeout")) {
/* 470 */         j = parseTimeString(str);
/*     */       } else {
/* 472 */         j = parsePositiveIntString(str);
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {}
/*     */     
/*     */ 
/* 478 */     if (j > 0) { i = j;
/*     */     }
/* 480 */     return i;
/*     */   }
/*     */   
/*     */   private static int parsePositiveIntString(String paramString) {
/* 484 */     if (paramString == null) {
/* 485 */       return -1;
/*     */     }
/* 487 */     int i = -1;
/*     */     try
/*     */     {
/* 490 */       i = Integer.parseInt(paramString);
/*     */     } catch (Exception localException) {
/* 492 */       return -1;
/*     */     }
/*     */     
/* 495 */     if (i >= 0) {
/* 496 */       return i;
/*     */     }
/* 498 */     return -1;
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
/*     */   static class KdcAccessibility
/*     */   {
/* 512 */     private static Set<String> bads = new HashSet();
/*     */     
/*     */     private static synchronized void addBad(String paramString) {
/* 515 */       if (KdcComm.DEBUG) {
/* 516 */         System.out.println(">>> KdcAccessibility: add " + paramString);
/*     */       }
/* 518 */       bads.add(paramString);
/*     */     }
/*     */     
/*     */     private static synchronized void removeBad(String paramString) {
/* 522 */       if (KdcComm.DEBUG) {
/* 523 */         System.out.println(">>> KdcAccessibility: remove " + paramString);
/*     */       }
/* 525 */       bads.remove(paramString);
/*     */     }
/*     */     
/*     */     private static synchronized boolean isBad(String paramString) {
/* 529 */       return bads.contains(paramString);
/*     */     }
/*     */     
/*     */     private static synchronized void reset() {
/* 533 */       if (KdcComm.DEBUG) {
/* 534 */         System.out.println(">>> KdcAccessibility: reset");
/*     */       }
/* 536 */       bads.clear();
/*     */     }
/*     */     
/*     */     private static synchronized List<String> list(String paramString)
/*     */     {
/* 541 */       StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
/* 542 */       ArrayList localArrayList1 = new ArrayList();
/* 543 */       if (KdcComm.badPolicy == BpType.TRY_LAST) {
/* 544 */         ArrayList localArrayList2 = new ArrayList();
/* 545 */         while (localStringTokenizer.hasMoreTokens()) {
/* 546 */           String str = localStringTokenizer.nextToken();
/* 547 */           if (bads.contains(str)) localArrayList2.add(str); else {
/* 548 */             localArrayList1.add(str);
/*     */           }
/*     */         }
/* 551 */         localArrayList1.addAll(localArrayList2);
/*     */       }
/*     */       else
/*     */       {
/* 555 */         while (localStringTokenizer.hasMoreTokens()) {
/* 556 */           localArrayList1.add(localStringTokenizer.nextToken());
/*     */         }
/*     */       }
/* 559 */       return localArrayList1;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\KdcComm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */