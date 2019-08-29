/*     */ package sun.security.provider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SHA
/*     */   extends DigestBase
/*     */ {
/*     */   private int[] W;
/*     */   
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
/*     */   private static final int round1_kt = 1518500249;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int round2_kt = 1859775393;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int round3_kt = -1894007588;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int round4_kt = -899497514;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SHA()
/*     */   {
/*  59 */     super("SHA-1", 20, 64);
/*  60 */     this.state = new int[5];
/*  61 */     this.W = new int[80];
/*  62 */     implReset();
/*     */   }
/*     */   
/*     */ 
/*     */   public Object clone()
/*     */     throws CloneNotSupportedException
/*     */   {
/*  69 */     SHA localSHA = (SHA)super.clone();
/*  70 */     localSHA.state = ((int[])localSHA.state.clone());
/*  71 */     localSHA.W = new int[80];
/*  72 */     return localSHA;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void implReset()
/*     */   {
/*  79 */     this.state[0] = 1732584193;
/*  80 */     this.state[1] = -271733879;
/*  81 */     this.state[2] = -1732584194;
/*  82 */     this.state[3] = 271733878;
/*  83 */     this.state[4] = -1009589776;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void implDigest(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/*  90 */     long l = this.bytesProcessed << 3;
/*     */     
/*  92 */     int i = (int)this.bytesProcessed & 0x3F;
/*  93 */     int j = i < 56 ? 56 - i : 120 - i;
/*  94 */     engineUpdate(padding, 0, j);
/*     */     
/*  96 */     ByteArrayAccess.i2bBig4((int)(l >>> 32), this.buffer, 56);
/*  97 */     ByteArrayAccess.i2bBig4((int)l, this.buffer, 60);
/*  98 */     implCompress(this.buffer, 0);
/*     */     
/* 100 */     ByteArrayAccess.i2bBig(this.state, 0, paramArrayOfByte, paramInt, 20);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void implCompress(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 117 */     ByteArrayAccess.b2iBig64(paramArrayOfByte, paramInt, this.W);
/*     */     
/*     */ 
/*     */ 
/* 121 */     for (int i = 16; i <= 79; i++) {
/* 122 */       j = this.W[(i - 3)] ^ this.W[(i - 8)] ^ this.W[(i - 14)] ^ this.W[(i - 16)];
/* 123 */       this.W[i] = (j << 1 | j >>> 31);
/*     */     }
/*     */     
/* 126 */     i = this.state[0];
/* 127 */     int j = this.state[1];
/* 128 */     int k = this.state[2];
/* 129 */     int m = this.state[3];
/* 130 */     int n = this.state[4];
/*     */     
/*     */     int i2;
/* 133 */     for (int i1 = 0; i1 < 20; i1++) {
/* 134 */       i2 = (i << 5 | i >>> 27) + (j & k | (j ^ 0xFFFFFFFF) & m) + n + this.W[i1] + 1518500249;
/*     */       
/* 136 */       n = m;
/* 137 */       m = k;
/* 138 */       k = j << 30 | j >>> 2;
/* 139 */       j = i;
/* 140 */       i = i2;
/*     */     }
/*     */     
/*     */ 
/* 144 */     for (i1 = 20; i1 < 40; i1++) {
/* 145 */       i2 = (i << 5 | i >>> 27) + (j ^ k ^ m) + n + this.W[i1] + 1859775393;
/*     */       
/* 147 */       n = m;
/* 148 */       m = k;
/* 149 */       k = j << 30 | j >>> 2;
/* 150 */       j = i;
/* 151 */       i = i2;
/*     */     }
/*     */     
/*     */ 
/* 155 */     for (i1 = 40; i1 < 60; i1++) {
/* 156 */       i2 = (i << 5 | i >>> 27) + (j & k | j & m | k & m) + n + this.W[i1] + -1894007588;
/*     */       
/* 158 */       n = m;
/* 159 */       m = k;
/* 160 */       k = j << 30 | j >>> 2;
/* 161 */       j = i;
/* 162 */       i = i2;
/*     */     }
/*     */     
/*     */ 
/* 166 */     for (i1 = 60; i1 < 80; i1++) {
/* 167 */       i2 = (i << 5 | i >>> 27) + (j ^ k ^ m) + n + this.W[i1] + -899497514;
/*     */       
/* 169 */       n = m;
/* 170 */       m = k;
/* 171 */       k = j << 30 | j >>> 2;
/* 172 */       j = i;
/* 173 */       i = i2;
/*     */     }
/* 175 */     this.state[0] += i;
/* 176 */     this.state[1] += j;
/* 177 */     this.state[2] += k;
/* 178 */     this.state[3] += m;
/* 179 */     this.state[4] += n;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\SHA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */