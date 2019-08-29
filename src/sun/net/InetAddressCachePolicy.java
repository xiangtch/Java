/*     */ package sun.net;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Security;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class InetAddressCachePolicy
/*     */ {
/*     */   private static final String cachePolicyProp = "networkaddress.cache.ttl";
/*     */   private static final String cachePolicyPropFallback = "sun.net.inetaddr.ttl";
/*     */   private static final String negativeCachePolicyProp = "networkaddress.cache.negative.ttl";
/*     */   private static final String negativeCachePolicyPropFallback = "sun.net.inetaddr.negative.ttl";
/*     */   public static final int FOREVER = -1;
/*     */   public static final int NEVER = 0;
/*     */   public static final int DEFAULT_POSITIVE = 30;
/*  59 */   private static int cachePolicy = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  69 */   private static int negativeCachePolicy = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean propertySet;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean propertyNegativeSet;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  88 */     Integer localInteger = (Integer)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Integer run() {
/*     */         try {
/*  92 */           String str1 = Security.getProperty("networkaddress.cache.ttl");
/*  93 */           if (str1 != null) {
/*  94 */             return Integer.valueOf(str1);
/*     */           }
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException1) {}
/*     */         
/*     */         try
/*     */         {
/* 101 */           String str2 = System.getProperty("sun.net.inetaddr.ttl");
/* 102 */           if (str2 != null) {
/* 103 */             return Integer.decode(str2);
/*     */           }
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException2) {}
/*     */         
/* 108 */         return null;
/*     */       }
/*     */     });
/*     */     
/* 112 */     if (localInteger != null) {
/* 113 */       cachePolicy = localInteger.intValue();
/* 114 */       if (cachePolicy < 0) {
/* 115 */         cachePolicy = -1;
/*     */       }
/* 117 */       propertySet = true;
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/* 122 */     else if (System.getSecurityManager() == null) {
/* 123 */       cachePolicy = 30;
/*     */     }
/*     */     
/* 126 */     localInteger = (Integer)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Integer run() {
/*     */         try {
/* 130 */           String str1 = Security.getProperty("networkaddress.cache.negative.ttl");
/* 131 */           if (str1 != null) {
/* 132 */             return Integer.valueOf(str1);
/*     */           }
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException1) {}
/*     */         
/*     */         try
/*     */         {
/* 139 */           String str2 = System.getProperty("sun.net.inetaddr.negative.ttl");
/* 140 */           if (str2 != null) {
/* 141 */             return Integer.decode(str2);
/*     */           }
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException2) {}
/*     */         
/* 146 */         return null;
/*     */       }
/*     */     });
/*     */     
/* 150 */     if (localInteger != null) {
/* 151 */       negativeCachePolicy = localInteger.intValue();
/* 152 */       if (negativeCachePolicy < 0) {
/* 153 */         negativeCachePolicy = -1;
/*     */       }
/* 155 */       propertyNegativeSet = true;
/*     */     }
/*     */   }
/*     */   
/*     */   public static synchronized int get() {
/* 160 */     return cachePolicy;
/*     */   }
/*     */   
/*     */   public static synchronized int getNegative() {
/* 164 */     return negativeCachePolicy;
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
/*     */   public static synchronized void setIfNotSet(int paramInt)
/*     */   {
/* 180 */     if (!propertySet) {
/* 181 */       checkValue(paramInt, cachePolicy);
/* 182 */       cachePolicy = paramInt;
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
/*     */   public static synchronized void setNegativeIfNotSet(int paramInt)
/*     */   {
/* 199 */     if (!propertyNegativeSet)
/*     */     {
/*     */ 
/*     */ 
/* 203 */       negativeCachePolicy = paramInt;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void checkValue(int paramInt1, int paramInt2)
/*     */   {
/* 213 */     if (paramInt1 == -1) {
/* 214 */       return;
/*     */     }
/* 216 */     if ((paramInt2 == -1) || (paramInt1 < paramInt2) || (paramInt1 < -1))
/*     */     {
/*     */ 
/*     */ 
/* 220 */       throw new SecurityException("can't make InetAddress cache more lax");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\InetAddressCachePolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */