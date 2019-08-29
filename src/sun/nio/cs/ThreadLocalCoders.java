/*     */ package sun.nio.cs;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ThreadLocalCoders
/*     */ {
/*     */   private static final int CACHE_SIZE = 3;
/*     */   
/*     */   private static abstract class Cache
/*     */   {
/*  43 */     private ThreadLocal<Object[]> cache = new ThreadLocal();
/*     */     private final int size;
/*     */     
/*     */     Cache(int paramInt) {
/*  47 */       this.size = paramInt;
/*     */     }
/*     */     
/*     */     abstract Object create(Object paramObject);
/*     */     
/*     */     private void moveToFront(Object[] paramArrayOfObject, int paramInt) {
/*  53 */       Object localObject = paramArrayOfObject[paramInt];
/*  54 */       for (int i = paramInt; i > 0; i--)
/*  55 */         paramArrayOfObject[i] = paramArrayOfObject[(i - 1)];
/*  56 */       paramArrayOfObject[0] = localObject;
/*     */     }
/*     */     
/*     */     abstract boolean hasName(Object paramObject1, Object paramObject2);
/*     */     
/*     */     Object forName(Object paramObject) {
/*  62 */       Object[] arrayOfObject = (Object[])this.cache.get();
/*  63 */       if (arrayOfObject == null) {
/*  64 */         arrayOfObject = new Object[this.size];
/*  65 */         this.cache.set(arrayOfObject);
/*     */       } else {
/*  67 */         for (int i = 0; i < arrayOfObject.length; i++) {
/*  68 */           Object localObject2 = arrayOfObject[i];
/*  69 */           if (localObject2 != null)
/*     */           {
/*  71 */             if (hasName(localObject2, paramObject)) {
/*  72 */               if (i > 0)
/*  73 */                 moveToFront(arrayOfObject, i);
/*  74 */               return localObject2;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*  80 */       Object localObject1 = create(paramObject);
/*  81 */       arrayOfObject[(arrayOfObject.length - 1)] = localObject1;
/*  82 */       moveToFront(arrayOfObject, arrayOfObject.length - 1);
/*  83 */       return localObject1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*  88 */   private static Cache decoderCache = new Cache(3) {
/*     */     boolean hasName(Object paramAnonymousObject1, Object paramAnonymousObject2) {
/*  90 */       if ((paramAnonymousObject2 instanceof String))
/*  91 */         return ((CharsetDecoder)paramAnonymousObject1).charset().name().equals(paramAnonymousObject2);
/*  92 */       if ((paramAnonymousObject2 instanceof Charset))
/*  93 */         return ((CharsetDecoder)paramAnonymousObject1).charset().equals(paramAnonymousObject2);
/*  94 */       return false;
/*     */     }
/*     */     
/*  97 */     Object create(Object paramAnonymousObject) { if ((paramAnonymousObject instanceof String))
/*  98 */         return Charset.forName((String)paramAnonymousObject).newDecoder();
/*  99 */       if ((paramAnonymousObject instanceof Charset))
/* 100 */         return ((Charset)paramAnonymousObject).newDecoder();
/* 101 */       if (!$assertionsDisabled) throw new AssertionError();
/* 102 */       return null;
/*     */     }
/*     */   };
/*     */   
/*     */   public static CharsetDecoder decoderFor(Object paramObject) {
/* 107 */     CharsetDecoder localCharsetDecoder = (CharsetDecoder)decoderCache.forName(paramObject);
/* 108 */     localCharsetDecoder.reset();
/* 109 */     return localCharsetDecoder;
/*     */   }
/*     */   
/* 112 */   private static Cache encoderCache = new Cache(3) {
/*     */     boolean hasName(Object paramAnonymousObject1, Object paramAnonymousObject2) {
/* 114 */       if ((paramAnonymousObject2 instanceof String))
/* 115 */         return ((CharsetEncoder)paramAnonymousObject1).charset().name().equals(paramAnonymousObject2);
/* 116 */       if ((paramAnonymousObject2 instanceof Charset))
/* 117 */         return ((CharsetEncoder)paramAnonymousObject1).charset().equals(paramAnonymousObject2);
/* 118 */       return false;
/*     */     }
/*     */     
/* 121 */     Object create(Object paramAnonymousObject) { if ((paramAnonymousObject instanceof String))
/* 122 */         return Charset.forName((String)paramAnonymousObject).newEncoder();
/* 123 */       if ((paramAnonymousObject instanceof Charset))
/* 124 */         return ((Charset)paramAnonymousObject).newEncoder();
/* 125 */       if (!$assertionsDisabled) throw new AssertionError();
/* 126 */       return null;
/*     */     }
/*     */   };
/*     */   
/*     */   public static CharsetEncoder encoderFor(Object paramObject) {
/* 131 */     CharsetEncoder localCharsetEncoder = (CharsetEncoder)encoderCache.forName(paramObject);
/* 132 */     localCharsetEncoder.reset();
/* 133 */     return localCharsetEncoder;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\cs\ThreadLocalCoders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */