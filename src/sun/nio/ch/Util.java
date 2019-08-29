/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import sun.misc.Cleaner;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.misc.VM;
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
/*     */ public class Util
/*     */ {
/*  48 */   private static final int TEMP_BUF_POOL_SIZE = IOUtil.IOV_MAX;
/*     */   
/*     */ 
/*  51 */   private static final long MAX_CACHED_BUFFER_SIZE = getMaxCachedBufferSize();
/*     */   
/*     */ 
/*  54 */   private static ThreadLocal<BufferCache> bufferCache = new ThreadLocal()
/*     */   {
/*     */ 
/*     */     protected BufferCache initialValue()
/*     */     {
/*  59 */       return new BufferCache();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static long getMaxCachedBufferSize()
/*     */   {
/*  71 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run()
/*     */       {
/*  75 */         return System.getProperty("jdk.nio.maxCachedBufferSize");
/*     */       }
/*     */     });
/*  78 */     if (str != null) {
/*     */       try {
/*  80 */         long l = Long.parseLong(str);
/*  81 */         if (l >= 0L) {
/*  82 */           return l;
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  90 */     return Long.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isBufferTooLarge(int paramInt)
/*     */   {
/*  98 */     return paramInt > MAX_CACHED_BUFFER_SIZE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isBufferTooLarge(ByteBuffer paramByteBuffer)
/*     */   {
/* 106 */     return isBufferTooLarge(paramByteBuffer.capacity());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class BufferCache
/*     */   {
/*     */     private ByteBuffer[] buffers;
/*     */     
/*     */ 
/*     */     private int count;
/*     */     
/*     */     private int start;
/*     */     
/*     */ 
/*     */     private int next(int paramInt)
/*     */     {
/* 123 */       return (paramInt + 1) % Util.TEMP_BUF_POOL_SIZE;
/*     */     }
/*     */     
/*     */     BufferCache() {
/* 127 */       this.buffers = new ByteBuffer[Util.TEMP_BUF_POOL_SIZE];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     ByteBuffer get(int paramInt)
/*     */     {
/* 136 */       assert (!Util.isBufferTooLarge(paramInt));
/*     */       
/* 138 */       if (this.count == 0) {
/* 139 */         return null;
/*     */       }
/* 141 */       ByteBuffer[] arrayOfByteBuffer = this.buffers;
/*     */       
/*     */ 
/* 144 */       Object localObject = arrayOfByteBuffer[this.start];
/* 145 */       if (((ByteBuffer)localObject).capacity() < paramInt) {
/* 146 */         localObject = null;
/* 147 */         int i = this.start;
/* 148 */         while ((i = next(i)) != this.start) {
/* 149 */           ByteBuffer localByteBuffer = arrayOfByteBuffer[i];
/* 150 */           if (localByteBuffer == null)
/*     */             break;
/* 152 */           if (localByteBuffer.capacity() >= paramInt) {
/* 153 */             localObject = localByteBuffer;
/* 154 */             break;
/*     */           }
/*     */         }
/* 157 */         if (localObject == null) {
/* 158 */           return null;
/*     */         }
/* 160 */         arrayOfByteBuffer[i] = arrayOfByteBuffer[this.start];
/*     */       }
/*     */       
/*     */ 
/* 164 */       arrayOfByteBuffer[this.start] = null;
/* 165 */       this.start = next(this.start);
/* 166 */       this.count -= 1;
/*     */       
/*     */ 
/* 169 */       ((ByteBuffer)localObject).rewind();
/* 170 */       ((ByteBuffer)localObject).limit(paramInt);
/* 171 */       return (ByteBuffer)localObject;
/*     */     }
/*     */     
/*     */     boolean offerFirst(ByteBuffer paramByteBuffer)
/*     */     {
/* 176 */       assert (!Util.isBufferTooLarge(paramByteBuffer));
/*     */       
/* 178 */       if (this.count >= Util.TEMP_BUF_POOL_SIZE) {
/* 179 */         return false;
/*     */       }
/* 181 */       this.start = ((this.start + Util.TEMP_BUF_POOL_SIZE - 1) % Util.TEMP_BUF_POOL_SIZE);
/* 182 */       this.buffers[this.start] = paramByteBuffer;
/* 183 */       this.count += 1;
/* 184 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */     boolean offerLast(ByteBuffer paramByteBuffer)
/*     */     {
/* 190 */       assert (!Util.isBufferTooLarge(paramByteBuffer));
/*     */       
/* 192 */       if (this.count >= Util.TEMP_BUF_POOL_SIZE) {
/* 193 */         return false;
/*     */       }
/* 195 */       int i = (this.start + this.count) % Util.TEMP_BUF_POOL_SIZE;
/* 196 */       this.buffers[i] = paramByteBuffer;
/* 197 */       this.count += 1;
/* 198 */       return true;
/*     */     }
/*     */     
/*     */     boolean isEmpty()
/*     */     {
/* 203 */       return this.count == 0;
/*     */     }
/*     */     
/*     */     ByteBuffer removeFirst() {
/* 207 */       assert (this.count > 0);
/* 208 */       ByteBuffer localByteBuffer = this.buffers[this.start];
/* 209 */       this.buffers[this.start] = null;
/* 210 */       this.start = next(this.start);
/* 211 */       this.count -= 1;
/* 212 */       return localByteBuffer;
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
/*     */   public static ByteBuffer getTemporaryDirectBuffer(int paramInt)
/*     */   {
/* 225 */     if (isBufferTooLarge(paramInt)) {
/* 226 */       return ByteBuffer.allocateDirect(paramInt);
/*     */     }
/*     */     
/* 229 */     BufferCache localBufferCache = (BufferCache)bufferCache.get();
/* 230 */     ByteBuffer localByteBuffer = localBufferCache.get(paramInt);
/* 231 */     if (localByteBuffer != null) {
/* 232 */       return localByteBuffer;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 237 */     if (!localBufferCache.isEmpty()) {
/* 238 */       localByteBuffer = localBufferCache.removeFirst();
/* 239 */       free(localByteBuffer);
/*     */     }
/* 241 */     return ByteBuffer.allocateDirect(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void releaseTemporaryDirectBuffer(ByteBuffer paramByteBuffer)
/*     */   {
/* 249 */     offerFirstTemporaryDirectBuffer(paramByteBuffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void offerFirstTemporaryDirectBuffer(ByteBuffer paramByteBuffer)
/*     */   {
/* 260 */     if (isBufferTooLarge(paramByteBuffer)) {
/* 261 */       free(paramByteBuffer);
/* 262 */       return;
/*     */     }
/*     */     
/* 265 */     assert (paramByteBuffer != null);
/* 266 */     BufferCache localBufferCache = (BufferCache)bufferCache.get();
/* 267 */     if (!localBufferCache.offerFirst(paramByteBuffer))
/*     */     {
/* 269 */       free(paramByteBuffer);
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
/*     */   static void offerLastTemporaryDirectBuffer(ByteBuffer paramByteBuffer)
/*     */   {
/* 282 */     if (isBufferTooLarge(paramByteBuffer)) {
/* 283 */       free(paramByteBuffer);
/* 284 */       return;
/*     */     }
/*     */     
/* 287 */     assert (paramByteBuffer != null);
/* 288 */     BufferCache localBufferCache = (BufferCache)bufferCache.get();
/* 289 */     if (!localBufferCache.offerLast(paramByteBuffer))
/*     */     {
/* 291 */       free(paramByteBuffer);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void free(ByteBuffer paramByteBuffer)
/*     */   {
/* 299 */     ((DirectBuffer)paramByteBuffer).cleaner().clean();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static ByteBuffer[] subsequence(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*     */   {
/* 306 */     if ((paramInt1 == 0) && (paramInt2 == paramArrayOfByteBuffer.length))
/* 307 */       return paramArrayOfByteBuffer;
/* 308 */     int i = paramInt2;
/* 309 */     ByteBuffer[] arrayOfByteBuffer = new ByteBuffer[i];
/* 310 */     for (int j = 0; j < i; j++)
/* 311 */       arrayOfByteBuffer[j] = paramArrayOfByteBuffer[(paramInt1 + j)];
/* 312 */     return arrayOfByteBuffer;
/*     */   }
/*     */   
/*     */   static <E> Set<E> ungrowableSet(Set<E> paramSet) {
/* 316 */     new Set()
/*     */     {
/* 318 */       public int size() { return this.val$s.size(); }
/* 319 */       public boolean isEmpty() { return this.val$s.isEmpty(); }
/* 320 */       public boolean contains(Object paramAnonymousObject) { return this.val$s.contains(paramAnonymousObject); }
/* 321 */       public Object[] toArray() { return this.val$s.toArray(); }
/* 322 */       public <T> T[] toArray(T[] paramAnonymousArrayOfT) { return this.val$s.toArray(paramAnonymousArrayOfT); }
/* 323 */       public String toString() { return this.val$s.toString(); }
/* 324 */       public Iterator<E> iterator() { return this.val$s.iterator(); }
/* 325 */       public boolean equals(Object paramAnonymousObject) { return this.val$s.equals(paramAnonymousObject); }
/* 326 */       public int hashCode() { return this.val$s.hashCode(); }
/* 327 */       public void clear() { this.val$s.clear(); }
/* 328 */       public boolean remove(Object paramAnonymousObject) { return this.val$s.remove(paramAnonymousObject); }
/*     */       
/*     */       public boolean containsAll(Collection<?> paramAnonymousCollection) {
/* 331 */         return this.val$s.containsAll(paramAnonymousCollection);
/*     */       }
/*     */       
/* 334 */       public boolean removeAll(Collection<?> paramAnonymousCollection) { return this.val$s.removeAll(paramAnonymousCollection); }
/*     */       
/*     */       public boolean retainAll(Collection<?> paramAnonymousCollection) {
/* 337 */         return this.val$s.retainAll(paramAnonymousCollection);
/*     */       }
/*     */       
/*     */       public boolean add(E paramAnonymousE) {
/* 341 */         throw new UnsupportedOperationException();
/*     */       }
/*     */       
/* 344 */       public boolean addAll(Collection<? extends E> paramAnonymousCollection) { throw new UnsupportedOperationException(); }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 353 */   private static Unsafe unsafe = Unsafe.getUnsafe();
/*     */   
/*     */   private static byte _get(long paramLong) {
/* 356 */     return unsafe.getByte(paramLong);
/*     */   }
/*     */   
/*     */   private static void _put(long paramLong, byte paramByte) {
/* 360 */     unsafe.putByte(paramLong, paramByte);
/*     */   }
/*     */   
/*     */   static void erase(ByteBuffer paramByteBuffer) {
/* 364 */     unsafe.setMemory(((DirectBuffer)paramByteBuffer).address(), paramByteBuffer.capacity(), (byte)0);
/*     */   }
/*     */   
/*     */   static Unsafe unsafe() {
/* 368 */     return unsafe;
/*     */   }
/*     */   
/* 371 */   private static int pageSize = -1;
/*     */   
/*     */   static int pageSize() {
/* 374 */     if (pageSize == -1)
/* 375 */       pageSize = unsafe().pageSize();
/* 376 */     return pageSize;
/*     */   }
/*     */   
/* 379 */   private static volatile Constructor<?> directByteBufferConstructor = null;
/*     */   
/*     */   private static void initDBBConstructor() {
/* 382 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Void run() {
/*     */         try {
/* 385 */           Class localClass = Class.forName("java.nio.DirectByteBuffer");
/* 386 */           Constructor localConstructor = localClass.getDeclaredConstructor(new Class[] { Integer.TYPE, Long.TYPE, FileDescriptor.class, Runnable.class });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 391 */           localConstructor.setAccessible(true);
/* 392 */           Util.access$302(localConstructor);
/*     */ 
/*     */         }
/*     */         catch (ClassNotFoundException|NoSuchMethodException|IllegalArgumentException|ClassCastException localClassNotFoundException)
/*     */         {
/* 397 */           throw new InternalError(localClassNotFoundException);
/*     */         }
/* 399 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static MappedByteBuffer newMappedByteBuffer(int paramInt, long paramLong, FileDescriptor paramFileDescriptor, Runnable paramRunnable)
/*     */   {
/* 408 */     if (directByteBufferConstructor == null)
/* 409 */       initDBBConstructor();
/*     */     MappedByteBuffer localMappedByteBuffer;
/* 411 */     try { localMappedByteBuffer = (MappedByteBuffer)directByteBufferConstructor.newInstance(new Object[] { new Integer(paramInt), new Long(paramLong), paramFileDescriptor, paramRunnable });
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (InstantiationException|IllegalAccessException|InvocationTargetException localInstantiationException)
/*     */     {
/*     */ 
/*     */ 
/* 419 */       throw new InternalError(localInstantiationException);
/*     */     }
/* 421 */     return localMappedByteBuffer;
/*     */   }
/*     */   
/* 424 */   private static volatile Constructor<?> directByteBufferRConstructor = null;
/*     */   
/*     */   private static void initDBBRConstructor() {
/* 427 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Void run() {
/*     */         try {
/* 430 */           Class localClass = Class.forName("java.nio.DirectByteBufferR");
/* 431 */           Constructor localConstructor = localClass.getDeclaredConstructor(new Class[] { Integer.TYPE, Long.TYPE, FileDescriptor.class, Runnable.class });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 436 */           localConstructor.setAccessible(true);
/* 437 */           Util.access$402(localConstructor);
/*     */ 
/*     */         }
/*     */         catch (ClassNotFoundException|NoSuchMethodException|IllegalArgumentException|ClassCastException localClassNotFoundException)
/*     */         {
/* 442 */           throw new InternalError(localClassNotFoundException);
/*     */         }
/* 444 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static MappedByteBuffer newMappedByteBufferR(int paramInt, long paramLong, FileDescriptor paramFileDescriptor, Runnable paramRunnable)
/*     */   {
/* 453 */     if (directByteBufferRConstructor == null)
/* 454 */       initDBBRConstructor();
/*     */     MappedByteBuffer localMappedByteBuffer;
/* 456 */     try { localMappedByteBuffer = (MappedByteBuffer)directByteBufferRConstructor.newInstance(new Object[] { new Integer(paramInt), new Long(paramLong), paramFileDescriptor, paramRunnable });
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (InstantiationException|IllegalAccessException|InvocationTargetException localInstantiationException)
/*     */     {
/*     */ 
/*     */ 
/* 464 */       throw new InternalError(localInstantiationException);
/*     */     }
/* 466 */     return localMappedByteBuffer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 472 */   private static volatile String bugLevel = null;
/*     */   
/*     */   static boolean atBugLevel(String paramString) {
/* 475 */     if (bugLevel == null) {
/* 476 */       if (!VM.isBooted())
/* 477 */         return false;
/* 478 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.ch.bugLevel"));
/*     */       
/* 480 */       bugLevel = str != null ? str : "";
/*     */     }
/* 482 */     return bugLevel.equals(paramString);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */