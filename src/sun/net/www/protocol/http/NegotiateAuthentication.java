/*     */ package sun.net.www.protocol.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Authenticator.RequestorType;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.util.Base64;
/*     */ import java.util.Base64.Decoder;
/*     */ import java.util.Base64.Encoder;
/*     */ import java.util.HashMap;
/*     */ import sun.net.www.HeaderParser;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.util.logging.PlatformLogger;
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
/*     */ class NegotiateAuthentication
/*     */   extends AuthenticationInfo
/*     */ {
/*     */   private static final long serialVersionUID = 100L;
/*  49 */   private static final PlatformLogger logger = ;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final HttpCallerInfo hci;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  59 */   static HashMap<String, Boolean> supported = null;
/*  60 */   static ThreadLocal<HashMap<String, Negotiator>> cache = null;
/*     */   private static final boolean cacheSPNEGO;
/*     */   
/*     */   static {
/*  64 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.spnego.cache", "true"));
/*     */     
/*  66 */     cacheSPNEGO = Boolean.parseBoolean(str);
/*     */   }
/*     */   
/*     */ 
/*  70 */   private Negotiator negotiator = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public NegotiateAuthentication(HttpCallerInfo paramHttpCallerInfo)
/*     */   {
/*  77 */     super(Authenticator.RequestorType.PROXY == paramHttpCallerInfo.authType ? 'p' : 's', paramHttpCallerInfo.scheme
/*  78 */       .equalsIgnoreCase("Negotiate") ? AuthScheme.NEGOTIATE : AuthScheme.KERBEROS, paramHttpCallerInfo.url, "");
/*     */     
/*     */ 
/*  81 */     this.hci = paramHttpCallerInfo;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean supportsPreemptiveAuthorization()
/*     */   {
/*  89 */     return false;
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
/*     */   private static synchronized boolean isSupportedImpl(HttpCallerInfo paramHttpCallerInfo)
/*     */   {
/* 129 */     if (supported == null) {
/* 130 */       supported = new HashMap();
/*     */     }
/* 132 */     String str = paramHttpCallerInfo.host;
/* 133 */     str = str.toLowerCase();
/* 134 */     if (supported.containsKey(str)) {
/* 135 */       return ((Boolean)supported.get(str)).booleanValue();
/*     */     }
/*     */     
/* 138 */     Negotiator localNegotiator = Negotiator.getNegotiator(paramHttpCallerInfo);
/* 139 */     if (localNegotiator != null) {
/* 140 */       supported.put(str, Boolean.valueOf(true));
/*     */       
/*     */ 
/* 143 */       if (cache == null) {
/* 144 */         cache = new ThreadLocal()
/*     */         {
/*     */           protected HashMap<String, Negotiator> initialValue() {
/* 147 */             return new HashMap();
/*     */           }
/*     */         };
/*     */       }
/* 151 */       ((HashMap)cache.get()).put(str, localNegotiator);
/* 152 */       return true;
/*     */     }
/* 154 */     supported.put(str, Boolean.valueOf(false));
/* 155 */     return false;
/*     */   }
/*     */   
/*     */   private static synchronized HashMap<String, Negotiator> getCache()
/*     */   {
/* 160 */     if (cache == null) return null;
/* 161 */     return (HashMap)cache.get();
/*     */   }
/*     */   
/*     */   protected boolean useAuthCache()
/*     */   {
/* 166 */     return (super.useAuthCache()) && (cacheSPNEGO);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHeaderValue(URL paramURL, String paramString)
/*     */   {
/* 174 */     throw new RuntimeException("getHeaderValue not supported");
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
/*     */   public boolean isAuthorizationStale(String paramString)
/*     */   {
/* 187 */     return false;
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
/*     */   public synchronized boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString)
/*     */   {
/*     */     try
/*     */     {
/* 203 */       byte[] arrayOfByte = null;
/* 204 */       String[] arrayOfString = paramString.split("\\s+");
/* 205 */       if (arrayOfString.length > 1) {
/* 206 */         arrayOfByte = Base64.getDecoder().decode(arrayOfString[1]);
/*     */       }
/* 208 */       String str = this.hci.scheme + " " + Base64.getEncoder().encodeToString(arrayOfByte == null ? 
/* 209 */         firstToken() : nextToken(arrayOfByte));
/*     */       
/* 211 */       paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), str);
/* 212 */       return true;
/*     */     } catch (IOException localIOException) {}
/* 214 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] firstToken()
/*     */     throws IOException
/*     */   {
/* 225 */     this.negotiator = null;
/* 226 */     HashMap localHashMap = getCache();
/* 227 */     if (localHashMap != null) {
/* 228 */       this.negotiator = ((Negotiator)localHashMap.get(getHost()));
/* 229 */       if (this.negotiator != null) {
/* 230 */         localHashMap.remove(getHost());
/*     */       }
/*     */     }
/* 233 */     if (this.negotiator == null) {
/* 234 */       this.negotiator = Negotiator.getNegotiator(this.hci);
/* 235 */       if (this.negotiator == null) {
/* 236 */         IOException localIOException = new IOException("Cannot initialize Negotiator");
/* 237 */         throw localIOException;
/*     */       }
/*     */     }
/*     */     
/* 241 */     return this.negotiator.firstToken();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] nextToken(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 252 */     return this.negotiator.nextToken(paramArrayOfByte);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static boolean isSupported(HttpCallerInfo paramHttpCallerInfo)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aconst_null
/*     */     //   1: astore_1
/*     */     //   2: invokestatic 269	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*     */     //   5: invokevirtual 268	java/lang/Thread:getContextClassLoader	()Ljava/lang/ClassLoader;
/*     */     //   8: astore_1
/*     */     //   9: goto +41 -> 50
/*     */     //   12: astore_2
/*     */     //   13: getstatic 254	sun/net/www/protocol/http/NegotiateAuthentication:logger	Lsun/util/logging/PlatformLogger;
/*     */     //   16: getstatic 255	sun/util/logging/PlatformLogger$Level:FINER	Lsun/util/logging/PlatformLogger$Level;
/*     */     //   19: invokevirtual 297	sun/util/logging/PlatformLogger:isLoggable	(Lsun/util/logging/PlatformLogger$Level;)Z
/*     */     //   22: ifeq +28 -> 50
/*     */     //   25: getstatic 254	sun/net/www/protocol/http/NegotiateAuthentication:logger	Lsun/util/logging/PlatformLogger;
/*     */     //   28: new 136	java/lang/StringBuilder
/*     */     //   31: dup
/*     */     //   32: invokespecial 264	java/lang/StringBuilder:<init>	()V
/*     */     //   35: ldc 5
/*     */     //   37: invokevirtual 267	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   40: aload_2
/*     */     //   41: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   44: invokevirtual 265	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   47: invokevirtual 296	sun/util/logging/PlatformLogger:finer	(Ljava/lang/String;)V
/*     */     //   50: aload_1
/*     */     //   51: ifnull +19 -> 70
/*     */     //   54: aload_1
/*     */     //   55: dup
/*     */     //   56: astore_2
/*     */     //   57: monitorenter
/*     */     //   58: aload_0
/*     */     //   59: invokestatic 290	sun/net/www/protocol/http/NegotiateAuthentication:isSupportedImpl	(Lsun/net/www/protocol/http/HttpCallerInfo;)Z
/*     */     //   62: aload_2
/*     */     //   63: monitorexit
/*     */     //   64: ireturn
/*     */     //   65: astore_3
/*     */     //   66: aload_2
/*     */     //   67: monitorexit
/*     */     //   68: aload_3
/*     */     //   69: athrow
/*     */     //   70: aload_0
/*     */     //   71: invokestatic 290	sun/net/www/protocol/http/NegotiateAuthentication:isSupportedImpl	(Lsun/net/www/protocol/http/HttpCallerInfo;)Z
/*     */     //   74: ireturn
/*     */     // Line number table:
/*     */     //   Java source line #97	-> byte code offset #0
/*     */     //   Java source line #99	-> byte code offset #2
/*     */     //   Java source line #105	-> byte code offset #9
/*     */     //   Java source line #100	-> byte code offset #12
/*     */     //   Java source line #101	-> byte code offset #13
/*     */     //   Java source line #102	-> byte code offset #25
/*     */     //   Java source line #107	-> byte code offset #50
/*     */     //   Java source line #110	-> byte code offset #54
/*     */     //   Java source line #111	-> byte code offset #58
/*     */     //   Java source line #112	-> byte code offset #65
/*     */     //   Java source line #114	-> byte code offset #70
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	75	0	paramHttpCallerInfo	HttpCallerInfo
/*     */     //   1	54	1	localClassLoader	ClassLoader
/*     */     //   12	29	2	localSecurityException	SecurityException
/*     */     //   56	11	2	Ljava/lang/Object;	Object
/*     */     //   65	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   2	9	12	java/lang/SecurityException
/*     */     //   58	64	65	finally
/*     */     //   65	68	65	finally
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\http\NegotiateAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */