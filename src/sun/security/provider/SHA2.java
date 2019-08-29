/*     */ package sun.security.provider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class SHA2
/*     */   extends DigestBase
/*     */ {
/*     */   private static final int ITERATION = 64;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  47 */   private static final int[] ROUND_CONSTS = { 1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int[] W;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int[] state;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int[] initialHashes;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   SHA2(String paramString, int paramInt, int[] paramArrayOfInt)
/*     */   {
/*  79 */     super(paramString, paramInt, 64);
/*  80 */     this.initialHashes = paramArrayOfInt;
/*  81 */     this.state = new int[8];
/*  82 */     this.W = new int[64];
/*  83 */     implReset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void implReset()
/*     */   {
/*  90 */     System.arraycopy(this.initialHashes, 0, this.state, 0, this.state.length);
/*     */   }
/*     */   
/*     */   void implDigest(byte[] paramArrayOfByte, int paramInt) {
/*  94 */     long l = this.bytesProcessed << 3;
/*     */     
/*  96 */     int i = (int)this.bytesProcessed & 0x3F;
/*  97 */     int j = i < 56 ? 56 - i : 120 - i;
/*  98 */     engineUpdate(padding, 0, j);
/*     */     
/* 100 */     ByteArrayAccess.i2bBig4((int)(l >>> 32), this.buffer, 56);
/* 101 */     ByteArrayAccess.i2bBig4((int)l, this.buffer, 60);
/* 102 */     implCompress(this.buffer, 0);
/*     */     
/* 104 */     ByteArrayAccess.i2bBig(this.state, 0, paramArrayOfByte, paramInt, engineGetDigestLength());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int lf_ch(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 115 */     return paramInt1 & paramInt2 ^ (paramInt1 ^ 0xFFFFFFFF) & paramInt3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int lf_maj(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 126 */     return paramInt1 & paramInt2 ^ paramInt1 & paramInt3 ^ paramInt2 & paramInt3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int lf_R(int paramInt1, int paramInt2)
/*     */   {
/* 136 */     return paramInt1 >>> paramInt2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int lf_S(int paramInt1, int paramInt2)
/*     */   {
/* 146 */     return paramInt1 >>> paramInt2 | paramInt1 << 32 - paramInt2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int lf_sigma0(int paramInt)
/*     */   {
/* 155 */     return lf_S(paramInt, 2) ^ lf_S(paramInt, 13) ^ lf_S(paramInt, 22);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int lf_sigma1(int paramInt)
/*     */   {
/* 164 */     return lf_S(paramInt, 6) ^ lf_S(paramInt, 11) ^ lf_S(paramInt, 25);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int lf_delta0(int paramInt)
/*     */   {
/* 173 */     return lf_S(paramInt, 7) ^ lf_S(paramInt, 18) ^ lf_R(paramInt, 3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int lf_delta1(int paramInt)
/*     */   {
/* 182 */     return lf_S(paramInt, 17) ^ lf_S(paramInt, 19) ^ lf_R(paramInt, 10);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void implCompress(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 189 */     ByteArrayAccess.b2iBig64(paramArrayOfByte, paramInt, this.W);
/*     */     
/*     */ 
/*     */ 
/* 193 */     for (int i = 16; i < 64; i++) {
/* 194 */       this.W[i] = (lf_delta1(this.W[(i - 2)]) + this.W[(i - 7)] + lf_delta0(this.W[(i - 15)]) + this.W[(i - 16)]);
/*     */     }
/*     */     
/*     */ 
/* 198 */     i = this.state[0];
/* 199 */     int j = this.state[1];
/* 200 */     int k = this.state[2];
/* 201 */     int m = this.state[3];
/* 202 */     int n = this.state[4];
/* 203 */     int i1 = this.state[5];
/* 204 */     int i2 = this.state[6];
/* 205 */     int i3 = this.state[7];
/*     */     
/* 207 */     for (int i4 = 0; i4 < 64; i4++) {
/* 208 */       int i5 = i3 + lf_sigma1(n) + lf_ch(n, i1, i2) + ROUND_CONSTS[i4] + this.W[i4];
/* 209 */       int i6 = lf_sigma0(i) + lf_maj(i, j, k);
/* 210 */       i3 = i2;
/* 211 */       i2 = i1;
/* 212 */       i1 = n;
/* 213 */       n = m + i5;
/* 214 */       m = k;
/* 215 */       k = j;
/* 216 */       j = i;
/* 217 */       i = i5 + i6;
/*     */     }
/* 219 */     this.state[0] += i;
/* 220 */     this.state[1] += j;
/* 221 */     this.state[2] += k;
/* 222 */     this.state[3] += m;
/* 223 */     this.state[4] += n;
/* 224 */     this.state[5] += i1;
/* 225 */     this.state[6] += i2;
/* 226 */     this.state[7] += i3;
/*     */   }
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 230 */     SHA2 localSHA2 = (SHA2)super.clone();
/* 231 */     localSHA2.state = ((int[])localSHA2.state.clone());
/* 232 */     localSHA2.W = new int[64];
/* 233 */     return localSHA2;
/*     */   }
/*     */   
/*     */ 
/*     */   public static final class SHA224
/*     */     extends SHA2
/*     */   {
/* 240 */     private static final int[] INITIAL_HASHES = { -1056596264, 914150663, 812702999, -150054599, -4191439, 1750603025, 1694076839, -1090891868 };
/*     */     
/*     */ 
/*     */ 
/*     */     public SHA224()
/*     */     {
/* 246 */       super(28, INITIAL_HASHES);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static final class SHA256
/*     */     extends SHA2
/*     */   {
/* 254 */     private static final int[] INITIAL_HASHES = { 1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225 };
/*     */     
/*     */ 
/*     */ 
/*     */     public SHA256()
/*     */     {
/* 260 */       super(32, INITIAL_HASHES);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\SHA2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */