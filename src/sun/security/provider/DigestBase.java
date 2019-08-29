/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.security.DigestException;
/*     */ import java.security.MessageDigestSpi;
/*     */ import java.security.ProviderException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class DigestBase
/*     */   extends MessageDigestSpi
/*     */   implements Cloneable
/*     */ {
/*     */   private byte[] oneByte;
/*     */   private final String algorithm;
/*     */   private final int digestLength;
/*     */   private final int blockSize;
/*     */   byte[] buffer;
/*     */   private int bufOfs;
/*     */   long bytesProcessed;
/*     */   
/*     */   DigestBase(String paramString, int paramInt1, int paramInt2)
/*     */   {
/*  79 */     this.algorithm = paramString;
/*  80 */     this.digestLength = paramInt1;
/*  81 */     this.blockSize = paramInt2;
/*  82 */     this.buffer = new byte[paramInt2];
/*     */   }
/*     */   
/*     */   protected final int engineGetDigestLength()
/*     */   {
/*  87 */     return this.digestLength;
/*     */   }
/*     */   
/*     */   protected final void engineUpdate(byte paramByte)
/*     */   {
/*  92 */     if (this.oneByte == null) {
/*  93 */       this.oneByte = new byte[1];
/*     */     }
/*  95 */     this.oneByte[0] = paramByte;
/*  96 */     engineUpdate(this.oneByte, 0, 1);
/*     */   }
/*     */   
/*     */   protected final void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 101 */     if (paramInt2 == 0) {
/* 102 */       return;
/*     */     }
/* 104 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
/* 105 */       throw new ArrayIndexOutOfBoundsException();
/*     */     }
/* 107 */     if (this.bytesProcessed < 0L) {
/* 108 */       engineReset();
/*     */     }
/* 110 */     this.bytesProcessed += paramInt2;
/*     */     int i;
/* 112 */     if (this.bufOfs != 0) {
/* 113 */       i = Math.min(paramInt2, this.blockSize - this.bufOfs);
/* 114 */       System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, this.bufOfs, i);
/* 115 */       this.bufOfs += i;
/* 116 */       paramInt1 += i;
/* 117 */       paramInt2 -= i;
/* 118 */       if (this.bufOfs >= this.blockSize)
/*     */       {
/* 120 */         implCompress(this.buffer, 0);
/* 121 */         this.bufOfs = 0;
/*     */       }
/*     */     }
/*     */     
/* 125 */     if (paramInt2 >= this.blockSize) {
/* 126 */       i = paramInt1 + paramInt2;
/* 127 */       paramInt1 = implCompressMultiBlock(paramArrayOfByte, paramInt1, i - this.blockSize);
/* 128 */       paramInt2 = i - paramInt1;
/*     */     }
/*     */     
/* 131 */     if (paramInt2 > 0) {
/* 132 */       System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, 0, paramInt2);
/* 133 */       this.bufOfs = paramInt2;
/*     */     }
/*     */   }
/*     */   
/*     */   private int implCompressMultiBlock(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 139 */     for (; paramInt1 <= paramInt2; paramInt1 += this.blockSize) {
/* 140 */       implCompress(paramArrayOfByte, paramInt1);
/*     */     }
/* 142 */     return paramInt1;
/*     */   }
/*     */   
/*     */   protected final void engineReset()
/*     */   {
/* 147 */     if (this.bytesProcessed == 0L)
/*     */     {
/* 149 */       return;
/*     */     }
/* 151 */     implReset();
/* 152 */     this.bufOfs = 0;
/* 153 */     this.bytesProcessed = 0L;
/*     */   }
/*     */   
/*     */   protected final byte[] engineDigest()
/*     */   {
/* 158 */     byte[] arrayOfByte = new byte[this.digestLength];
/*     */     try {
/* 160 */       engineDigest(arrayOfByte, 0, arrayOfByte.length);
/*     */     }
/*     */     catch (DigestException localDigestException) {
/* 163 */       throw ((ProviderException)new ProviderException("Internal error").initCause(localDigestException));
/*     */     }
/* 165 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */   protected final int engineDigest(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws DigestException
/*     */   {
/* 171 */     if (paramInt2 < this.digestLength) {
/* 172 */       throw new DigestException("Length must be at least " + this.digestLength + " for " + this.algorithm + "digests");
/*     */     }
/*     */     
/* 175 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
/* 176 */       throw new DigestException("Buffer too short to store digest");
/*     */     }
/* 178 */     if (this.bytesProcessed < 0L) {
/* 179 */       engineReset();
/*     */     }
/* 181 */     implDigest(paramArrayOfByte, paramInt1);
/* 182 */     this.bytesProcessed = -1L;
/* 183 */     return this.digestLength;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract void implCompress(byte[] paramArrayOfByte, int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract void implDigest(byte[] paramArrayOfByte, int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */   abstract void implReset();
/*     */   
/*     */ 
/*     */ 
/*     */   public Object clone()
/*     */     throws CloneNotSupportedException
/*     */   {
/* 205 */     DigestBase localDigestBase = (DigestBase)super.clone();
/* 206 */     localDigestBase.buffer = ((byte[])localDigestBase.buffer.clone());
/* 207 */     return localDigestBase;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 217 */   static final byte[] padding = new byte[''];
/* 218 */   static { padding[0] = Byte.MIN_VALUE; }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\DigestBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */