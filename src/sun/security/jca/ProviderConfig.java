/*     */ package sun.security.jca;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.security.AccessController;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Provider;
/*     */ import java.security.ProviderException;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.PropertyExpander;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class ProviderConfig
/*     */ {
/*  46 */   private static final Debug debug = Debug.getInstance("jca", "ProviderConfig");
/*     */   
/*     */ 
/*     */ 
/*     */   private static final String P11_SOL_NAME = "sun.security.pkcs11.SunPKCS11";
/*     */   
/*     */ 
/*     */ 
/*     */   private static final String P11_SOL_ARG = "${java.home}/lib/security/sunpkcs11-solaris.cfg";
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int MAX_LOAD_TRIES = 30;
/*     */   
/*     */ 
/*  61 */   private static final Class[] CL_STRING = { String.class };
/*     */   
/*     */ 
/*     */   private final String className;
/*     */   
/*     */ 
/*     */   private final String argument;
/*     */   
/*     */ 
/*     */   private int tries;
/*     */   
/*     */ 
/*     */   private volatile Provider provider;
/*     */   
/*     */ 
/*     */   private boolean isLoading;
/*     */   
/*     */ 
/*     */   ProviderConfig(String paramString1, String paramString2)
/*     */   {
/*  81 */     if ((paramString1.equals("sun.security.pkcs11.SunPKCS11")) && (paramString2.equals("${java.home}/lib/security/sunpkcs11-solaris.cfg"))) {
/*  82 */       checkSunPKCS11Solaris();
/*     */     }
/*  84 */     this.className = paramString1;
/*  85 */     this.argument = expand(paramString2);
/*     */   }
/*     */   
/*     */   ProviderConfig(String paramString) {
/*  89 */     this(paramString, "");
/*     */   }
/*     */   
/*     */   ProviderConfig(Provider paramProvider) {
/*  93 */     this.className = paramProvider.getClass().getName();
/*  94 */     this.argument = "";
/*  95 */     this.provider = paramProvider;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void checkSunPKCS11Solaris()
/*     */   {
/* 102 */     Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Boolean run() {
/* 105 */         File localFile = new File("/usr/lib/libpkcs11.so");
/* 106 */         if (!localFile.exists()) {
/* 107 */           return Boolean.FALSE;
/*     */         }
/* 109 */         if ("false".equalsIgnoreCase(
/* 110 */           System.getProperty("sun.security.pkcs11.enable-solaris"))) {
/* 111 */           return Boolean.FALSE;
/*     */         }
/* 113 */         return Boolean.TRUE;
/*     */       }
/*     */     });
/* 116 */     if (localBoolean == Boolean.FALSE) {
/* 117 */       this.tries = 30;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean hasArgument() {
/* 122 */     return this.argument.length() != 0;
/*     */   }
/*     */   
/*     */   private boolean shouldLoad()
/*     */   {
/* 127 */     return this.tries < 30;
/*     */   }
/*     */   
/*     */   private void disableLoad()
/*     */   {
/* 132 */     this.tries = 30;
/*     */   }
/*     */   
/*     */   boolean isLoaded() {
/* 136 */     return this.provider != null;
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 140 */     if (this == paramObject) {
/* 141 */       return true;
/*     */     }
/* 143 */     if (!(paramObject instanceof ProviderConfig)) {
/* 144 */       return false;
/*     */     }
/* 146 */     ProviderConfig localProviderConfig = (ProviderConfig)paramObject;
/* 147 */     return (this.className.equals(localProviderConfig.className)) && 
/* 148 */       (this.argument.equals(localProviderConfig.argument));
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 152 */     return this.className.hashCode() + this.argument.hashCode();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 156 */     if (hasArgument()) {
/* 157 */       return this.className + "('" + this.argument + "')";
/*     */     }
/* 159 */     return this.className;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   synchronized Provider getProvider()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 160	sun/security/jca/ProviderConfig:provider	Ljava/security/Provider;
/*     */     //   4: astore_1
/*     */     //   5: aload_1
/*     */     //   6: ifnull +5 -> 11
/*     */     //   9: aload_1
/*     */     //   10: areturn
/*     */     //   11: aload_0
/*     */     //   12: invokespecial 179	sun/security/jca/ProviderConfig:shouldLoad	()Z
/*     */     //   15: ifne +5 -> 20
/*     */     //   18: aconst_null
/*     */     //   19: areturn
/*     */     //   20: aload_0
/*     */     //   21: getfield 156	sun/security/jca/ProviderConfig:isLoading	Z
/*     */     //   24: ifeq +48 -> 72
/*     */     //   27: getstatic 161	sun/security/jca/ProviderConfig:debug	Lsun/security/util/Debug;
/*     */     //   30: ifnull +40 -> 70
/*     */     //   33: getstatic 161	sun/security/jca/ProviderConfig:debug	Lsun/security/util/Debug;
/*     */     //   36: new 98	java/lang/StringBuilder
/*     */     //   39: dup
/*     */     //   40: invokespecial 171	java/lang/StringBuilder:<init>	()V
/*     */     //   43: ldc 8
/*     */     //   45: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   48: aload_0
/*     */     //   49: invokevirtual 173	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   52: invokevirtual 172	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   55: invokevirtual 186	sun/security/util/Debug:println	(Ljava/lang/String;)V
/*     */     //   58: new 96	java/lang/Exception
/*     */     //   61: dup
/*     */     //   62: ldc 6
/*     */     //   64: invokespecial 164	java/lang/Exception:<init>	(Ljava/lang/String;)V
/*     */     //   67: invokevirtual 163	java/lang/Exception:printStackTrace	()V
/*     */     //   70: aconst_null
/*     */     //   71: areturn
/*     */     //   72: aload_0
/*     */     //   73: iconst_1
/*     */     //   74: putfield 156	sun/security/jca/ProviderConfig:isLoading	Z
/*     */     //   77: aload_0
/*     */     //   78: dup
/*     */     //   79: getfield 155	sun/security/jca/ProviderConfig:tries	I
/*     */     //   82: iconst_1
/*     */     //   83: iadd
/*     */     //   84: putfield 155	sun/security/jca/ProviderConfig:tries	I
/*     */     //   87: aload_0
/*     */     //   88: invokespecial 180	sun/security/jca/ProviderConfig:doLoadProvider	()Ljava/security/Provider;
/*     */     //   91: astore_1
/*     */     //   92: aload_0
/*     */     //   93: iconst_0
/*     */     //   94: putfield 156	sun/security/jca/ProviderConfig:isLoading	Z
/*     */     //   97: goto +11 -> 108
/*     */     //   100: astore_2
/*     */     //   101: aload_0
/*     */     //   102: iconst_0
/*     */     //   103: putfield 156	sun/security/jca/ProviderConfig:isLoading	Z
/*     */     //   106: aload_2
/*     */     //   107: athrow
/*     */     //   108: aload_0
/*     */     //   109: aload_1
/*     */     //   110: putfield 160	sun/security/jca/ProviderConfig:provider	Ljava/security/Provider;
/*     */     //   113: aload_1
/*     */     //   114: areturn
/*     */     // Line number table:
/*     */     //   Java source line #168	-> byte code offset #0
/*     */     //   Java source line #169	-> byte code offset #5
/*     */     //   Java source line #170	-> byte code offset #9
/*     */     //   Java source line #172	-> byte code offset #11
/*     */     //   Java source line #173	-> byte code offset #18
/*     */     //   Java source line #175	-> byte code offset #20
/*     */     //   Java source line #178	-> byte code offset #27
/*     */     //   Java source line #179	-> byte code offset #33
/*     */     //   Java source line #180	-> byte code offset #58
/*     */     //   Java source line #182	-> byte code offset #70
/*     */     //   Java source line #185	-> byte code offset #72
/*     */     //   Java source line #186	-> byte code offset #77
/*     */     //   Java source line #187	-> byte code offset #87
/*     */     //   Java source line #189	-> byte code offset #92
/*     */     //   Java source line #190	-> byte code offset #97
/*     */     //   Java source line #189	-> byte code offset #100
/*     */     //   Java source line #190	-> byte code offset #106
/*     */     //   Java source line #191	-> byte code offset #108
/*     */     //   Java source line #192	-> byte code offset #113
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	115	0	this	ProviderConfig
/*     */     //   4	110	1	localProvider	Provider
/*     */     //   100	7	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   72	92	100	finally
/*     */   }
/*     */   
/*     */   private Provider doLoadProvider()
/*     */   {
/* 206 */     (Provider)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Provider run() {
/* 208 */         if (ProviderConfig.debug != null) {
/* 209 */           ProviderConfig.debug.println("Loading provider: " + ProviderConfig.this);
/*     */         }
/*     */         try {
/* 212 */           ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
/*     */           
/* 214 */           if (localClassLoader != null) {
/* 215 */             localObject1 = localClassLoader.loadClass(ProviderConfig.this.className);
/*     */           } else {
/* 217 */             localObject1 = Class.forName(ProviderConfig.this.className);
/*     */           }
/*     */           Object localObject2;
/* 220 */           if (!ProviderConfig.this.hasArgument()) {
/* 221 */             localObject2 = ((Class)localObject1).newInstance();
/*     */           } else {
/* 223 */             Constructor localConstructor = ((Class)localObject1).getConstructor(ProviderConfig.CL_STRING);
/* 224 */             localObject2 = localConstructor.newInstance(new Object[] { ProviderConfig.this.argument });
/*     */           }
/* 226 */           if ((localObject2 instanceof Provider)) {
/* 227 */             if (ProviderConfig.debug != null) {
/* 228 */               ProviderConfig.debug.println("Loaded provider " + localObject2);
/*     */             }
/* 230 */             return (Provider)localObject2;
/*     */           }
/* 232 */           if (ProviderConfig.debug != null) {
/* 233 */             ProviderConfig.debug.println(ProviderConfig.this.className + " is not a provider");
/*     */           }
/* 235 */           ProviderConfig.this.disableLoad();
/* 236 */           return null;
/*     */         }
/*     */         catch (Exception localException) {
/*     */           Object localObject1;
/* 240 */           if ((localException instanceof InvocationTargetException)) {
/* 241 */             localObject1 = ((InvocationTargetException)localException).getCause();
/*     */           } else {
/* 243 */             localObject1 = localException;
/*     */           }
/* 245 */           if (ProviderConfig.debug != null) {
/* 246 */             ProviderConfig.debug.println("Error loading provider " + ProviderConfig.this);
/* 247 */             ((Throwable)localObject1).printStackTrace();
/*     */           }
/*     */           
/* 250 */           if ((localObject1 instanceof ProviderException)) {
/* 251 */             throw ((ProviderException)localObject1);
/*     */           }
/*     */           
/* 254 */           if ((localObject1 instanceof UnsupportedOperationException)) {
/* 255 */             ProviderConfig.this.disableLoad();
/*     */           }
/* 257 */           return null;
/*     */         }
/*     */         catch (ExceptionInInitializerError localExceptionInInitializerError)
/*     */         {
/* 261 */           if (ProviderConfig.debug != null) {
/* 262 */             ProviderConfig.debug.println("Error loading provider " + ProviderConfig.this);
/* 263 */             localExceptionInInitializerError.printStackTrace();
/*     */           }
/* 265 */           ProviderConfig.this.disableLoad(); }
/* 266 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String expand(String paramString)
/*     */   {
/* 279 */     if (!paramString.contains("${")) {
/* 280 */       return paramString;
/*     */     }
/* 282 */     (String)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public String run() {
/*     */         try {
/* 285 */           return PropertyExpander.expand(this.val$value);
/*     */         } catch (GeneralSecurityException localGeneralSecurityException) {
/* 287 */           throw new ProviderException(localGeneralSecurityException);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jca\ProviderConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */