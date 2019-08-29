/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.security.AccessController;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ class Util
/*     */ {
/*  41 */   private static final Charset jnuEncoding = Charset.forName(
/*  42 */     (String)AccessController.doPrivileged(new GetPropertyAction("sun.jnu.encoding")));
/*     */   
/*     */ 
/*     */ 
/*     */   static Charset jnuEncoding()
/*     */   {
/*  48 */     return jnuEncoding;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static byte[] toBytes(String paramString)
/*     */   {
/*  56 */     return paramString.getBytes(jnuEncoding);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static String toString(byte[] paramArrayOfByte)
/*     */   {
/*  64 */     return new String(paramArrayOfByte, jnuEncoding);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static String[] split(String paramString, char paramChar)
/*     */   {
/*  74 */     int i = 0;
/*  75 */     for (int j = 0; j < paramString.length(); j++) {
/*  76 */       if (paramString.charAt(j) == paramChar)
/*  77 */         i++;
/*     */     }
/*  79 */     String[] arrayOfString = new String[i + 1];
/*  80 */     int k = 0;
/*  81 */     int m = 0;
/*  82 */     for (int n = 0; n < paramString.length(); n++) {
/*  83 */       if (paramString.charAt(n) == paramChar) {
/*  84 */         arrayOfString[(k++)] = paramString.substring(m, n);
/*  85 */         m = n + 1;
/*     */       }
/*     */     }
/*  88 */     arrayOfString[k] = paramString.substring(m, paramString.length());
/*  89 */     return arrayOfString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @SafeVarargs
/*     */   static <E> Set<E> newSet(E... paramVarArgs)
/*     */   {
/*  97 */     HashSet localHashSet = new HashSet();
/*  98 */     for (E ? : paramVarArgs) {
/*  99 */       localHashSet.add(?);
/*     */     }
/* 101 */     return localHashSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @SafeVarargs
/*     */   static <E> Set<E> newSet(Set<E> paramSet, E... paramVarArgs)
/*     */   {
/* 110 */     HashSet localHashSet = new HashSet(paramSet);
/* 111 */     for (E ? : paramVarArgs) {
/* 112 */       localHashSet.add(?);
/*     */     }
/* 114 */     return localHashSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static boolean followLinks(LinkOption... paramVarArgs)
/*     */   {
/* 121 */     boolean bool = true;
/* 122 */     for (LinkOption localLinkOption : paramVarArgs) {
/* 123 */       if (localLinkOption == LinkOption.NOFOLLOW_LINKS) {
/* 124 */         bool = false;
/* 125 */       } else { if (localLinkOption == null) {
/* 126 */           throw new NullPointerException();
/*     */         }
/* 128 */         throw new AssertionError("Should not get here");
/*     */       }
/*     */     }
/* 131 */     return bool;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */