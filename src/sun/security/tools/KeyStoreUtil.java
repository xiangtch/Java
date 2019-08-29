/*     */ package sun.security.tools;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.security.KeyStore;
/*     */ import java.text.Collator;
/*     */ import java.util.Locale;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KeyStoreUtil
/*     */ {
/*     */   private static final String JKS = "jks";
/*  55 */   private static final Collator collator = ;
/*     */   
/*     */   static {
/*  58 */     collator.setStrength(0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isWindowsKeyStore(String paramString)
/*     */   {
/*  66 */     return (paramString.equalsIgnoreCase("Windows-MY")) || 
/*  67 */       (paramString.equalsIgnoreCase("Windows-ROOT"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String niceStoreTypeName(String paramString)
/*     */   {
/*  74 */     if (paramString.equalsIgnoreCase("Windows-MY"))
/*  75 */       return "Windows-MY";
/*  76 */     if (paramString.equalsIgnoreCase("Windows-ROOT")) {
/*  77 */       return "Windows-ROOT";
/*     */     }
/*  79 */     return paramString.toUpperCase(Locale.ENGLISH);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static KeyStore getCacertsKeyStore()
/*     */     throws Exception
/*     */   {
/*  89 */     String str = File.separator;
/*  90 */     File localFile = new File(System.getProperty("java.home") + str + "lib" + str + "security" + str + "cacerts");
/*     */     
/*     */ 
/*  93 */     if (!localFile.exists()) {
/*  94 */       return null;
/*     */     }
/*  96 */     KeyStore localKeyStore = null;
/*  97 */     FileInputStream localFileInputStream = new FileInputStream(localFile);Object localObject1 = null;
/*  98 */     try { localKeyStore = KeyStore.getInstance("jks");
/*  99 */       localKeyStore.load(localFileInputStream, null);
/*     */     }
/*     */     catch (Throwable localThrowable2)
/*     */     {
/*  97 */       localObject1 = localThrowable2;throw localThrowable2;
/*     */     }
/*     */     finally {
/* 100 */       if (localFileInputStream != null) if (localObject1 != null) try { localFileInputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localFileInputStream.close(); }
/* 101 */     return localKeyStore;
/*     */   }
/*     */   
/*     */   public static char[] getPassWithModifier(String paramString1, String paramString2, ResourceBundle paramResourceBundle)
/*     */   {
/* 106 */     if (paramString1 == null)
/* 107 */       return paramString2.toCharArray();
/* 108 */     Object localObject1; if (collator.compare(paramString1, "env") == 0) {
/* 109 */       localObject1 = System.getenv(paramString2);
/* 110 */       if (localObject1 == null) {
/* 111 */         System.err.println(paramResourceBundle.getString("Cannot.find.environment.variable.") + paramString2);
/*     */         
/* 113 */         return null;
/*     */       }
/* 115 */       return ((String)localObject1).toCharArray();
/*     */     }
/* 117 */     if (collator.compare(paramString1, "file") == 0) {
/*     */       try {
/* 119 */         localObject1 = null;
/*     */         try {
/* 121 */           localObject1 = new URL(paramString2);
/*     */         } catch (MalformedURLException localMalformedURLException) {
/* 123 */           localObject2 = new File(paramString2);
/* 124 */           if (((File)localObject2).exists()) {
/* 125 */             localObject1 = ((File)localObject2).toURI().toURL();
/*     */           } else {
/* 127 */             System.err.println(paramResourceBundle.getString("Cannot.find.file.") + paramString2);
/*     */             
/* 129 */             return null;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 135 */         BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(((URL)localObject1).openStream()));Object localObject2 = null;
/* 136 */         try { String str = localBufferedReader.readLine();
/*     */           char[] arrayOfChar;
/* 138 */           if (str == null) {
/* 139 */             return new char[0];
/*     */           }
/*     */           
/* 142 */           return str.toCharArray();
/*     */         }
/*     */         catch (Throwable localThrowable1)
/*     */         {
/* 133 */           localObject2 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 143 */           if (localBufferedReader != null) { if (localObject2 != null) try { localBufferedReader.close(); } catch (Throwable localThrowable4) { ((Throwable)localObject2).addSuppressed(localThrowable4); } else { localBufferedReader.close();
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 149 */         System.err.println(paramResourceBundle.getString("Unknown.password.type.") + paramString1);
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 145 */         System.err.println(localIOException);
/* 146 */         return null;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 151 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\tools\KeyStoreUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */