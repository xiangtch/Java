/*     */ package sun.security.util;
/*     */ 
/*     */ import java.security.InvalidParameterException;
/*     */ import java.util.regex.PatternSyntaxException;
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
/*     */ public final class SecurityProviderConstants
/*     */ {
/*  38 */   private static final Debug debug = Debug.getInstance("jca", "ProviderConfig");
/*     */   public static final int DEF_DSA_KEY_SIZE;
/*     */   public static final int DEF_RSA_KEY_SIZE;
/*     */   public static final int DEF_DH_KEY_SIZE;
/*     */   public static final int DEF_EC_KEY_SIZE;
/*     */   private static final String KEY_LENGTH_PROP = "jdk.security.defaultKeySize";
/*     */   
/*  45 */   public static final int getDefDSASubprimeSize(int paramInt) { if (paramInt <= 1024)
/*  46 */       return 160;
/*  47 */     if (paramInt == 2048)
/*  48 */       return 224;
/*  49 */     if (paramInt == 3072) {
/*  50 */       return 256;
/*     */     }
/*  52 */     throw new InvalidParameterException("Invalid DSA Prime Size: " + paramInt);
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
/*     */   static
/*     */   {
/*  66 */     String str1 = GetPropertyAction.privilegedGetProperty("jdk.security.defaultKeySize");
/*  67 */     int i = 2048;
/*  68 */     int j = 2048;
/*  69 */     int k = 2048;
/*  70 */     int m = 256;
/*     */     
/*  72 */     if (str1 != null) {
/*     */       try {
/*  74 */         String[] arrayOfString1 = str1.split(",");
/*  75 */         for (String str2 : arrayOfString1) {
/*  76 */           String[] arrayOfString3 = str2.split(":");
/*  77 */           if (arrayOfString3.length != 2)
/*     */           {
/*  79 */             if (debug != null) {
/*  80 */               debug.println("Ignoring invalid pair in jdk.security.defaultKeySize property: " + str2);
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/*  85 */             String str3 = arrayOfString3[0].trim().toUpperCase();
/*  86 */             int i2 = -1;
/*     */             try {
/*  88 */               i2 = Integer.parseInt(arrayOfString3[1].trim());
/*     */             }
/*     */             catch (NumberFormatException localNumberFormatException) {
/*  91 */               if (debug != null) {
/*  92 */                 debug.println("Ignoring invalid value in jdk.security.defaultKeySize property: " + str2);
/*     */               }
/*     */               
/*  95 */               continue;
/*     */             }
/*  97 */             if (str3.equals("DSA")) {
/*  98 */               i = i2;
/*  99 */             } else if (str3.equals("RSA")) {
/* 100 */               j = i2;
/* 101 */             } else if (str3.equals("DH")) {
/* 102 */               k = i2;
/* 103 */             } else if (str3.equals("EC")) {
/* 104 */               m = i2;
/*     */             } else {
/* 106 */               if (debug == null) continue;
/* 107 */               debug.println("Ignoring unsupported algo in jdk.security.defaultKeySize property: " + str2); continue;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 112 */             if (debug != null) {
/* 113 */               debug.println("Overriding default " + str3 + " keysize with value from " + "jdk.security.defaultKeySize" + " property: " + i2);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (PatternSyntaxException localPatternSyntaxException)
/*     */       {
/* 120 */         if (debug != null) {
/* 121 */           debug.println("Unexpected exception while parsing jdk.security.defaultKeySize property: " + localPatternSyntaxException);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 126 */     DEF_DSA_KEY_SIZE = i;
/* 127 */     DEF_RSA_KEY_SIZE = j;
/* 128 */     DEF_DH_KEY_SIZE = k;
/* 129 */     DEF_EC_KEY_SIZE = m;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\SecurityProviderConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */