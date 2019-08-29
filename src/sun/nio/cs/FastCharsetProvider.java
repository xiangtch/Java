/*     */ package sun.nio.cs;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.spi.CharsetProvider;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FastCharsetProvider
/*     */   extends CharsetProvider
/*     */ {
/*     */   private Map<String, String> classMap;
/*     */   private Map<String, String> aliasMap;
/*     */   private Map<String, Charset> cache;
/*     */   private String packagePrefix;
/*     */   
/*     */   protected FastCharsetProvider(String paramString, Map<String, String> paramMap1, Map<String, String> paramMap2, Map<String, Charset> paramMap)
/*     */   {
/*  60 */     this.packagePrefix = paramString;
/*  61 */     this.aliasMap = paramMap1;
/*  62 */     this.classMap = paramMap2;
/*  63 */     this.cache = paramMap;
/*     */   }
/*     */   
/*     */   private String canonicalize(String paramString) {
/*  67 */     String str = (String)this.aliasMap.get(paramString);
/*  68 */     return str != null ? str : paramString;
/*     */   }
/*     */   
/*     */ 
/*     */   private static String toLower(String paramString)
/*     */   {
/*  74 */     int i = paramString.length();
/*  75 */     int j = 1;
/*  76 */     for (int k = 0; k < i; k++) {
/*  77 */       m = paramString.charAt(k);
/*  78 */       if ((m - 65 | 90 - m) >= 0) {
/*  79 */         j = 0;
/*  80 */         break;
/*     */       }
/*     */     }
/*  83 */     if (j != 0)
/*  84 */       return paramString;
/*  85 */     char[] arrayOfChar = new char[i];
/*  86 */     for (int m = 0; m < i; m++) {
/*  87 */       int n = paramString.charAt(m);
/*  88 */       if ((n - 65 | 90 - n) >= 0) {
/*  89 */         arrayOfChar[m] = ((char)(n + 32));
/*     */       } else
/*  91 */         arrayOfChar[m] = ((char)n);
/*     */     }
/*  93 */     return new String(arrayOfChar);
/*     */   }
/*     */   
/*     */   private Charset lookup(String paramString)
/*     */   {
/*  98 */     String str1 = canonicalize(toLower(paramString));
/*     */     
/*     */ 
/* 101 */     Object localObject = (Charset)this.cache.get(str1);
/* 102 */     if (localObject != null) {
/* 103 */       return (Charset)localObject;
/*     */     }
/*     */     
/* 106 */     String str2 = (String)this.classMap.get(str1);
/* 107 */     if (str2 == null) {
/* 108 */       return null;
/*     */     }
/* 110 */     if (str2.equals("US_ASCII")) {
/* 111 */       localObject = new US_ASCII();
/* 112 */       this.cache.put(str1, localObject);
/* 113 */       return (Charset)localObject;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 118 */       Class localClass = Class.forName(this.packagePrefix + "." + str2, true, 
/*     */       
/* 120 */         getClass().getClassLoader());
/* 121 */       localObject = (Charset)localClass.newInstance();
/* 122 */       this.cache.put(str1, localObject);
/* 123 */       return (Charset)localObject;
/*     */     }
/*     */     catch (ClassNotFoundException|IllegalAccessException|InstantiationException localClassNotFoundException) {}
/*     */     
/* 127 */     return null;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public final Charset charsetForName(String paramString)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: aload_0
/*     */     //   6: aload_1
/*     */     //   7: invokespecial 129	sun/nio/cs/FastCharsetProvider:canonicalize	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   10: invokespecial 131	sun/nio/cs/FastCharsetProvider:lookup	(Ljava/lang/String;)Ljava/nio/charset/Charset;
/*     */     //   13: aload_2
/*     */     //   14: monitorexit
/*     */     //   15: areturn
/*     */     //   16: astore_3
/*     */     //   17: aload_2
/*     */     //   18: monitorexit
/*     */     //   19: aload_3
/*     */     //   20: athrow
/*     */     // Line number table:
/*     */     //   Java source line #132	-> byte code offset #0
/*     */     //   Java source line #133	-> byte code offset #4
/*     */     //   Java source line #134	-> byte code offset #16
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	21	0	this	FastCharsetProvider
/*     */     //   0	21	1	paramString	String
/*     */     //   2	16	2	Ljava/lang/Object;	Object
/*     */     //   16	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	15	16	finally
/*     */     //   16	19	16	finally
/*     */   }
/*     */   
/*     */   public final Iterator<Charset> charsets()
/*     */   {
/* 139 */     new Iterator()
/*     */     {
/* 141 */       Iterator<String> i = FastCharsetProvider.this.classMap.keySet().iterator();
/*     */       
/*     */       public boolean hasNext() {
/* 144 */         return this.i.hasNext();
/*     */       }
/*     */       
/*     */       public Charset next() {
/* 148 */         String str = (String)this.i.next();
/* 149 */         return FastCharsetProvider.this.lookup(str);
/*     */       }
/*     */       
/*     */       public void remove() {
/* 153 */         throw new UnsupportedOperationException();
/*     */       }
/*     */     };
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\cs\FastCharsetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */