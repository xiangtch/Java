/*     */ package sun.security.util;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AlgorithmDecomposer
/*     */ {
/*  39 */   private static final Pattern transPattern = Pattern.compile("/");
/*     */   
/*  41 */   private static final Pattern pattern = Pattern.compile("with|and", 2);
/*     */   
/*     */ 
/*     */   private static Set<String> decomposeImpl(String paramString)
/*     */   {
/*  46 */     String[] arrayOfString1 = transPattern.split(paramString);
/*     */     
/*  48 */     HashSet localHashSet = new HashSet();
/*  49 */     for (String str1 : arrayOfString1) {
/*  50 */       if ((str1 != null) && (str1.length() != 0))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  59 */         String[] arrayOfString3 = pattern.split(str1);
/*     */         
/*  61 */         for (String str2 : arrayOfString3)
/*  62 */           if ((str2 != null) && (str2.length() != 0))
/*     */           {
/*     */ 
/*     */ 
/*  66 */             localHashSet.add(str2); }
/*     */       }
/*     */     }
/*  69 */     return localHashSet;
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
/*     */   public Set<String> decompose(String paramString)
/*     */   {
/*  82 */     if ((paramString == null) || (paramString.length() == 0)) {
/*  83 */       return new HashSet();
/*     */     }
/*     */     
/*  86 */     Set localSet = decomposeImpl(paramString);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  96 */     if ((localSet.contains("SHA1")) && (!localSet.contains("SHA-1"))) {
/*  97 */       localSet.add("SHA-1");
/*     */     }
/*  99 */     if ((localSet.contains("SHA-1")) && (!localSet.contains("SHA1"))) {
/* 100 */       localSet.add("SHA1");
/*     */     }
/*     */     
/*     */ 
/* 104 */     if ((localSet.contains("SHA224")) && (!localSet.contains("SHA-224"))) {
/* 105 */       localSet.add("SHA-224");
/*     */     }
/* 107 */     if ((localSet.contains("SHA-224")) && (!localSet.contains("SHA224"))) {
/* 108 */       localSet.add("SHA224");
/*     */     }
/*     */     
/*     */ 
/* 112 */     if ((localSet.contains("SHA256")) && (!localSet.contains("SHA-256"))) {
/* 113 */       localSet.add("SHA-256");
/*     */     }
/* 115 */     if ((localSet.contains("SHA-256")) && (!localSet.contains("SHA256"))) {
/* 116 */       localSet.add("SHA256");
/*     */     }
/*     */     
/*     */ 
/* 120 */     if ((localSet.contains("SHA384")) && (!localSet.contains("SHA-384"))) {
/* 121 */       localSet.add("SHA-384");
/*     */     }
/* 123 */     if ((localSet.contains("SHA-384")) && (!localSet.contains("SHA384"))) {
/* 124 */       localSet.add("SHA384");
/*     */     }
/*     */     
/*     */ 
/* 128 */     if ((localSet.contains("SHA512")) && (!localSet.contains("SHA-512"))) {
/* 129 */       localSet.add("SHA-512");
/*     */     }
/* 131 */     if ((localSet.contains("SHA-512")) && (!localSet.contains("SHA512"))) {
/* 132 */       localSet.add("SHA512");
/*     */     }
/*     */     
/* 135 */     return localSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Collection<String> getAliases(String paramString)
/*     */   {
/*     */     String[] arrayOfString;
/*     */     
/*     */ 
/* 145 */     if ((paramString.equalsIgnoreCase("DH")) || 
/* 146 */       (paramString.equalsIgnoreCase("DiffieHellman"))) {
/* 147 */       arrayOfString = new String[] { "DH", "DiffieHellman" };
/*     */     } else {
/* 149 */       arrayOfString = new String[] { paramString };
/*     */     }
/*     */     
/* 152 */     return Arrays.asList(arrayOfString);
/*     */   }
/*     */   
/*     */   private static void hasLoop(Set<String> paramSet, String paramString1, String paramString2) {
/* 156 */     if (paramSet.contains(paramString1)) {
/* 157 */       if (!paramSet.contains(paramString2)) {
/* 158 */         paramSet.add(paramString2);
/*     */       }
/* 160 */       paramSet.remove(paramString1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Set<String> decomposeOneHash(String paramString)
/*     */   {
/* 169 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 170 */       return new HashSet();
/*     */     }
/*     */     
/* 173 */     Set localSet = decomposeImpl(paramString);
/*     */     
/* 175 */     hasLoop(localSet, "SHA-1", "SHA1");
/* 176 */     hasLoop(localSet, "SHA-224", "SHA224");
/* 177 */     hasLoop(localSet, "SHA-256", "SHA256");
/* 178 */     hasLoop(localSet, "SHA-384", "SHA384");
/* 179 */     hasLoop(localSet, "SHA-512", "SHA512");
/*     */     
/* 181 */     return localSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String hashName(String paramString)
/*     */   {
/* 189 */     return paramString.replace("-", "");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\AlgorithmDecomposer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */