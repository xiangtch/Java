/*     */ package sun.security.krb5.internal.rcache;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.nio.file.attribute.PosixFilePermission;
/*     */ import java.security.AccessController;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.KrbApErrException;
/*     */ import sun.security.krb5.internal.ReplayCache;
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
/*     */ public class DflCache
/*     */   extends ReplayCache
/*     */ {
/*     */   private static final int KRB5_RV_VNO = 1281;
/*     */   private static final int EXCESSREPS = 30;
/*     */   private final String source;
/*     */   private static int uid;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 112 */       Class localClass = Class.forName("com.sun.security.auth.module.UnixSystem");
/* 113 */       uid = (int)
/* 114 */         ((Long)localClass.getMethod("getUid", new Class[0]).invoke(localClass.newInstance(), new Object[0])).longValue();
/*     */     }
/*     */     catch (Exception localException) {
/* 116 */       uid = -1;
/*     */     }
/*     */   }
/*     */   
/*     */   public DflCache(String paramString) {
/* 121 */     this.source = paramString;
/*     */   }
/*     */   
/*     */   private static String defaultPath() {
/* 125 */     return (String)AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir"));
/*     */   }
/*     */   
/*     */ 
/*     */   private static String defaultFile(String paramString)
/*     */   {
/* 131 */     int i = paramString.indexOf('/');
/* 132 */     if (i == -1)
/*     */     {
/* 134 */       i = paramString.indexOf('@');
/*     */     }
/* 136 */     if (i != -1)
/*     */     {
/* 138 */       paramString = paramString.substring(0, i);
/*     */     }
/* 140 */     if (uid != -1) {
/* 141 */       paramString = paramString + "_" + uid;
/*     */     }
/* 143 */     return paramString;
/*     */   }
/*     */   
/*     */   private static Path getFileName(String paramString1, String paramString2) { String str1;
/*     */     String str2;
/* 148 */     if (paramString1.equals("dfl")) {
/* 149 */       str1 = defaultPath();
/* 150 */       str2 = defaultFile(paramString2);
/* 151 */     } else if (paramString1.startsWith("dfl:")) {
/* 152 */       paramString1 = paramString1.substring(4);
/* 153 */       int i = paramString1.lastIndexOf('/');
/* 154 */       int j = paramString1.lastIndexOf('\\');
/* 155 */       if (j > i) i = j;
/* 156 */       if (i == -1)
/*     */       {
/* 158 */         str1 = defaultPath();
/* 159 */         str2 = paramString1;
/* 160 */       } else if (new File(paramString1).isDirectory())
/*     */       {
/* 162 */         str1 = paramString1;
/* 163 */         str2 = defaultFile(paramString2);
/*     */       }
/*     */       else {
/* 166 */         str1 = null;
/* 167 */         str2 = paramString1;
/*     */       }
/*     */     } else {
/* 170 */       throw new IllegalArgumentException();
/*     */     }
/* 172 */     return new File(str1, str2).toPath();
/*     */   }
/*     */   
/*     */   public void checkAndStore(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash) throws KrbApErrException
/*     */   {
/*     */     try
/*     */     {
/* 179 */       checkAndStore0(paramKerberosTime, paramAuthTimeWithHash);
/*     */     } catch (IOException localIOException) {
/* 181 */       KrbApErrException localKrbApErrException = new KrbApErrException(60);
/* 182 */       localKrbApErrException.initCause(localIOException);
/* 183 */       throw localKrbApErrException;
/*     */     }
/*     */   }
/*     */   
/*     */   private synchronized void checkAndStore0(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash) throws IOException, KrbApErrException
/*     */   {
/* 189 */     Path localPath = getFileName(this.source, paramAuthTimeWithHash.server);
/* 190 */     int i = 0;
/* 191 */     Storage localStorage = new Storage(null);Object localObject1 = null;
/*     */     try {
/* 193 */       try { i = localStorage.loadAndCheck(localPath, paramAuthTimeWithHash, paramKerberosTime);
/*     */       }
/*     */       catch (IOException localIOException) {
/* 196 */         Storage.create(localPath);
/* 197 */         i = localStorage.loadAndCheck(localPath, paramAuthTimeWithHash, paramKerberosTime);
/*     */       }
/* 199 */       localStorage.append(paramAuthTimeWithHash);
/*     */     }
/*     */     catch (Throwable localThrowable2)
/*     */     {
/* 191 */       localObject1 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/*     */ 
/* 200 */       if (localStorage != null) if (localObject1 != null) try { localStorage.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localStorage.close(); }
/* 201 */     if (i > 30) {
/* 202 */       Storage.expunge(localPath, paramKerberosTime);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Storage implements Closeable
/*     */   {
/*     */     SeekableByteChannel chan;
/*     */     
/*     */     private static void create(Path paramPath) throws IOException {
/* 211 */       SeekableByteChannel localSeekableByteChannel = createNoClose(paramPath);Object localObject = null;
/*     */       
/* 213 */       if (localSeekableByteChannel != null) if (localObject != null) try { localSeekableByteChannel.close(); } catch (Throwable localThrowable) { ((Throwable)localObject).addSuppressed(localThrowable); } else localSeekableByteChannel.close();
/* 214 */       makeMine(paramPath);
/*     */     }
/*     */     
/*     */     private static void makeMine(Path paramPath) throws IOException
/*     */     {
/*     */       try {
/* 220 */         HashSet localHashSet = new HashSet();
/* 221 */         localHashSet.add(PosixFilePermission.OWNER_READ);
/* 222 */         localHashSet.add(PosixFilePermission.OWNER_WRITE);
/* 223 */         Files.setPosixFilePermissions(paramPath, localHashSet);
/*     */       }
/*     */       catch (UnsupportedOperationException localUnsupportedOperationException) {}
/*     */     }
/*     */     
/*     */     private static SeekableByteChannel createNoClose(Path paramPath)
/*     */       throws IOException
/*     */     {
/* 231 */       SeekableByteChannel localSeekableByteChannel = Files.newByteChannel(paramPath, new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE });
/*     */       
/*     */ 
/*     */ 
/* 235 */       ByteBuffer localByteBuffer = ByteBuffer.allocate(6);
/* 236 */       localByteBuffer.putShort((short)1281);
/* 237 */       localByteBuffer.order(ByteOrder.nativeOrder());
/* 238 */       localByteBuffer.putInt(KerberosTime.getDefaultSkew());
/* 239 */       localByteBuffer.flip();
/* 240 */       localSeekableByteChannel.write(localByteBuffer);
/* 241 */       return localSeekableByteChannel;
/*     */     }
/*     */     
/*     */     private static void expunge(Path paramPath, KerberosTime paramKerberosTime) throws IOException
/*     */     {
/* 246 */       Path localPath = Files.createTempFile(paramPath.getParent(), "rcache", null, new FileAttribute[0]);
/* 247 */       SeekableByteChannel localSeekableByteChannel1 = Files.newByteChannel(paramPath, new OpenOption[0]);Object localObject1 = null;
/* 248 */       try { SeekableByteChannel localSeekableByteChannel2 = createNoClose(localPath);Object localObject2 = null;
/* 249 */         try { long l = paramKerberosTime.getSeconds() - readHeader(localSeekableByteChannel1);
/*     */           try {
/*     */             for (;;) {
/* 252 */               AuthTime localAuthTime = AuthTime.readFrom(localSeekableByteChannel1);
/* 253 */               if (localAuthTime.ctime > l) {
/* 254 */                 ByteBuffer localByteBuffer = ByteBuffer.wrap(localAuthTime.encode(true));
/* 255 */                 localSeekableByteChannel2.write(localByteBuffer);
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 261 */             if (localObject2 == null) break label129; } catch (BufferUnderflowException localBufferUnderflowException) { if (localSeekableByteChannel2 == null) break label192; } try { localSeekableByteChannel2.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject2).addSuppressed(localThrowable3); } label129: localSeekableByteChannel2.close();
/*     */         }
/*     */         catch (Throwable localThrowable4)
/*     */         {
/* 247 */           localObject2 = localThrowable4;throw localThrowable4; } finally {} } catch (Throwable localThrowable2) { label192: localObject1 = localThrowable2; throw localThrowable2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 261 */         if (localSeekableByteChannel1 != null) if (localObject1 != null) try { localSeekableByteChannel1.close(); } catch (Throwable localThrowable6) { ((Throwable)localObject1).addSuppressed(localThrowable6); } else localSeekableByteChannel1.close(); }
/* 262 */       makeMine(localPath);
/* 263 */       Files.move(localPath, paramPath, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int loadAndCheck(Path paramPath, AuthTimeWithHash paramAuthTimeWithHash, KerberosTime paramKerberosTime)
/*     */       throws IOException, KrbApErrException
/*     */     {
/* 273 */       i = 0;
/* 274 */       if (Files.isSymbolicLink(paramPath)) {
/* 275 */         throw new IOException("Symlink not accepted");
/*     */       }
/*     */       try
/*     */       {
/* 279 */         Set localSet = Files.getPosixFilePermissions(paramPath, new LinkOption[0]);
/* 280 */         if ((DflCache.uid != -1) && 
/* 281 */           (((Integer)Files.getAttribute(paramPath, "unix:uid", new LinkOption[0])).intValue() != DflCache.uid)) {
/* 282 */           throw new IOException("Not mine");
/*     */         }
/* 284 */         if ((localSet.contains(PosixFilePermission.GROUP_READ)) || 
/* 285 */           (localSet.contains(PosixFilePermission.GROUP_WRITE)) || 
/* 286 */           (localSet.contains(PosixFilePermission.GROUP_EXECUTE)) || 
/* 287 */           (localSet.contains(PosixFilePermission.OTHERS_READ)) || 
/* 288 */           (localSet.contains(PosixFilePermission.OTHERS_WRITE)) || 
/* 289 */           (localSet.contains(PosixFilePermission.OTHERS_EXECUTE))) {
/* 290 */           throw new IOException("Accessible by someone else");
/*     */         }
/*     */       }
/*     */       catch (UnsupportedOperationException localUnsupportedOperationException) {}
/*     */       
/* 295 */       this.chan = Files.newByteChannel(paramPath, new OpenOption[] { StandardOpenOption.WRITE, StandardOpenOption.READ });
/*     */       
/*     */ 
/* 298 */       long l1 = paramKerberosTime.getSeconds() - readHeader(this.chan);
/*     */       
/* 300 */       long l2 = 0L;
/* 301 */       int j = 0;
/*     */       try {
/*     */         for (;;) {
/* 304 */           l2 = this.chan.position();
/* 305 */           AuthTime localAuthTime = AuthTime.readFrom(this.chan);
/* 306 */           if ((localAuthTime instanceof AuthTimeWithHash)) {
/* 307 */             if (paramAuthTimeWithHash.equals(localAuthTime))
/*     */             {
/* 309 */               throw new KrbApErrException(34); }
/* 310 */             if (paramAuthTimeWithHash.isSameIgnoresHash(localAuthTime))
/*     */             {
/*     */ 
/* 313 */               j = 1;
/*     */             }
/*     */           }
/* 316 */           else if (paramAuthTimeWithHash.isSameIgnoresHash(localAuthTime))
/*     */           {
/*     */ 
/* 319 */             if (j == 0) {
/* 320 */               throw new KrbApErrException(34);
/*     */             }
/*     */           }
/*     */           
/* 324 */           if (localAuthTime.ctime < l1) {
/* 325 */             i++;
/*     */           } else {
/* 327 */             i--;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 335 */         return i;
/*     */       }
/*     */       catch (BufferUnderflowException localBufferUnderflowException)
/*     */       {
/* 331 */         this.chan.position(l2);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private static int readHeader(SeekableByteChannel paramSeekableByteChannel)
/*     */       throws IOException
/*     */     {
/* 340 */       ByteBuffer localByteBuffer = ByteBuffer.allocate(6);
/* 341 */       paramSeekableByteChannel.read(localByteBuffer);
/* 342 */       if (localByteBuffer.getShort(0) != 1281) {
/* 343 */         throw new IOException("Not correct rcache version");
/*     */       }
/* 345 */       localByteBuffer.order(ByteOrder.nativeOrder());
/* 346 */       return localByteBuffer.getInt(2);
/*     */     }
/*     */     
/*     */ 
/*     */     private void append(AuthTimeWithHash paramAuthTimeWithHash)
/*     */       throws IOException
/*     */     {
/* 353 */       ByteBuffer localByteBuffer = ByteBuffer.wrap(paramAuthTimeWithHash.encode(true));
/* 354 */       this.chan.write(localByteBuffer);
/* 355 */       localByteBuffer = ByteBuffer.wrap(paramAuthTimeWithHash.encode(false));
/* 356 */       this.chan.write(localByteBuffer);
/*     */     }
/*     */     
/*     */     public void close() throws IOException
/*     */     {
/* 361 */       if (this.chan != null) this.chan.close();
/* 362 */       this.chan = null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\rcache\DflCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */