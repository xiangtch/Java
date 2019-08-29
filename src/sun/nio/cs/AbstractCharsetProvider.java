/*     */ package sun.nio.cs;
/*     */ 
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.spi.CharsetProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import sun.misc.ASCIICaseInsensitiveComparator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AbstractCharsetProvider
/*     */   extends CharsetProvider
/*     */ {
/*  51 */   private Map<String, String> classMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  56 */   private Map<String, String> aliasMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  61 */   private Map<String, String[]> aliasNameMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  66 */   private Map<String, SoftReference<Charset>> cache = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
/*     */   
/*     */   private String packagePrefix;
/*     */   
/*     */   protected AbstractCharsetProvider()
/*     */   {
/*  72 */     this.packagePrefix = "sun.nio.cs";
/*     */   }
/*     */   
/*     */   protected AbstractCharsetProvider(String paramString) {
/*  76 */     this.packagePrefix = paramString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static <K, V> void put(Map<K, V> paramMap, K paramK, V paramV)
/*     */   {
/*  83 */     if (!paramMap.containsKey(paramK))
/*  84 */       paramMap.put(paramK, paramV);
/*     */   }
/*     */   
/*     */   private static <K, V> void remove(Map<K, V> paramMap, K paramK) {
/*  88 */     Object localObject = paramMap.remove(paramK);
/*  89 */     assert (localObject != null);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void charset(String paramString1, String paramString2, String[] paramArrayOfString)
/*     */   {
/*  95 */     synchronized (this) {
/*  96 */       put(this.classMap, paramString1, paramString2);
/*  97 */       for (int i = 0; i < paramArrayOfString.length; i++)
/*  98 */         put(this.aliasMap, paramArrayOfString[i], paramString1);
/*  99 */       put(this.aliasNameMap, paramString1, paramArrayOfString);
/* 100 */       this.cache.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void deleteCharset(String paramString, String[] paramArrayOfString) {
/* 105 */     synchronized (this) {
/* 106 */       remove(this.classMap, paramString);
/* 107 */       for (int i = 0; i < paramArrayOfString.length; i++)
/* 108 */         remove(this.aliasMap, paramArrayOfString[i]);
/* 109 */       remove(this.aliasNameMap, paramString);
/* 110 */       this.cache.clear();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void init() {}
/*     */   
/*     */   private String canonicalize(String paramString)
/*     */   {
/* 119 */     String str = (String)this.aliasMap.get(paramString);
/* 120 */     return str != null ? str : paramString;
/*     */   }
/*     */   
/*     */ 
/*     */   private Charset lookup(String paramString)
/*     */   {
/* 126 */     SoftReference localSoftReference = (SoftReference)this.cache.get(paramString);
/* 127 */     if (localSoftReference != null) {
/* 128 */       localObject = (Charset)localSoftReference.get();
/* 129 */       if (localObject != null) {
/* 130 */         return (Charset)localObject;
/*     */       }
/*     */     }
/*     */     
/* 134 */     Object localObject = (String)this.classMap.get(paramString);
/*     */     
/* 136 */     if (localObject == null) {
/* 137 */       return null;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 142 */       Class localClass = Class.forName(this.packagePrefix + "." + (String)localObject, true, 
/*     */       
/* 144 */         getClass().getClassLoader());
/*     */       
/* 146 */       Charset localCharset = (Charset)localClass.newInstance();
/* 147 */       this.cache.put(paramString, new SoftReference(localCharset));
/* 148 */       return localCharset;
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 150 */       return null;
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 152 */       return null;
/*     */     } catch (InstantiationException localInstantiationException) {}
/* 154 */     return null;
/*     */   }
/*     */   
/*     */   public final Charset charsetForName(String paramString)
/*     */   {
/* 159 */     synchronized (this) {
/* 160 */       init();
/* 161 */       return lookup(canonicalize(paramString));
/*     */     }
/*     */   }
/*     */   
/*     */   public final Iterator<Charset> charsets()
/*     */   {
/*     */     final ArrayList localArrayList;
/* 168 */     synchronized (this) {
/* 169 */       init();
/* 170 */       localArrayList = new ArrayList(this.classMap.keySet());
/*     */     }
/*     */     
/* 173 */     new Iterator() {
/* 174 */       Iterator<String> i = localArrayList.iterator();
/*     */       
/*     */       public boolean hasNext() {
/* 177 */         return this.i.hasNext();
/*     */       }
/*     */       
/*     */       /* Error */
/*     */       public Charset next()
/*     */       {
/*     */         // Byte code:
/*     */         //   0: aload_0
/*     */         //   1: getfield 57	sun/nio/cs/AbstractCharsetProvider$1:i	Ljava/util/Iterator;
/*     */         //   4: invokeinterface 65 1 0
/*     */         //   9: checkcast 30	java/lang/String
/*     */         //   12: astore_1
/*     */         //   13: aload_0
/*     */         //   14: getfield 58	sun/nio/cs/AbstractCharsetProvider$1:this$0	Lsun/nio/cs/AbstractCharsetProvider;
/*     */         //   17: dup
/*     */         //   18: astore_2
/*     */         //   19: monitorenter
/*     */         //   20: aload_0
/*     */         //   21: getfield 58	sun/nio/cs/AbstractCharsetProvider$1:this$0	Lsun/nio/cs/AbstractCharsetProvider;
/*     */         //   24: aload_1
/*     */         //   25: invokestatic 62	sun/nio/cs/AbstractCharsetProvider:access$000	(Lsun/nio/cs/AbstractCharsetProvider;Ljava/lang/String;)Ljava/nio/charset/Charset;
/*     */         //   28: aload_2
/*     */         //   29: monitorexit
/*     */         //   30: areturn
/*     */         //   31: astore_3
/*     */         //   32: aload_2
/*     */         //   33: monitorexit
/*     */         //   34: aload_3
/*     */         //   35: athrow
/*     */         // Line number table:
/*     */         //   Java source line #181	-> byte code offset #0
/*     */         //   Java source line #182	-> byte code offset #13
/*     */         //   Java source line #183	-> byte code offset #20
/*     */         //   Java source line #184	-> byte code offset #31
/*     */         // Local variable table:
/*     */         //   start	length	slot	name	signature
/*     */         //   0	36	0	this	1
/*     */         //   12	13	1	str	String
/*     */         //   18	15	2	Ljava/lang/Object;	Object
/*     */         //   31	4	3	localObject1	Object
/*     */         // Exception table:
/*     */         //   from	to	target	type
/*     */         //   20	30	31	finally
/*     */         //   31	34	31	finally
/*     */       }
/*     */       
/*     */       public void remove()
/*     */       {
/* 188 */         throw new UnsupportedOperationException();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public final String[] aliases(String paramString) {
/* 194 */     synchronized (this) {
/* 195 */       init();
/* 196 */       return (String[])this.aliasNameMap.get(paramString);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\cs\AbstractCharsetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */