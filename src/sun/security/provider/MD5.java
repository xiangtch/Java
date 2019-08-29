/*     */ package sun.security.provider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MD5
/*     */   extends DigestBase
/*     */ {
/*     */   private int[] state;
/*     */   
/*     */ 
/*     */   private int[] x;
/*     */   
/*     */ 
/*     */   private static final int S11 = 7;
/*     */   
/*     */ 
/*     */   private static final int S12 = 12;
/*     */   
/*     */ 
/*     */   private static final int S13 = 17;
/*     */   
/*     */ 
/*     */   private static final int S14 = 22;
/*     */   
/*     */ 
/*     */   private static final int S21 = 5;
/*     */   
/*     */ 
/*     */   private static final int S22 = 9;
/*     */   
/*     */ 
/*     */   private static final int S23 = 14;
/*     */   
/*     */ 
/*     */   private static final int S24 = 20;
/*     */   
/*     */ 
/*     */   private static final int S31 = 4;
/*     */   
/*     */ 
/*     */   private static final int S32 = 11;
/*     */   
/*     */ 
/*     */   private static final int S33 = 16;
/*     */   
/*     */ 
/*     */   private static final int S34 = 23;
/*     */   
/*     */ 
/*     */   private static final int S41 = 6;
/*     */   
/*     */ 
/*     */   private static final int S42 = 10;
/*     */   
/*     */ 
/*     */   private static final int S43 = 15;
/*     */   
/*     */ 
/*     */   private static final int S44 = 21;
/*     */   
/*     */ 
/*     */ 
/*     */   public MD5()
/*     */   {
/*  66 */     super("MD5", 16, 64);
/*  67 */     this.state = new int[4];
/*  68 */     this.x = new int[16];
/*  69 */     implReset();
/*     */   }
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException
/*     */   {
/*  74 */     MD5 localMD5 = (MD5)super.clone();
/*  75 */     localMD5.state = ((int[])localMD5.state.clone());
/*  76 */     localMD5.x = new int[16];
/*  77 */     return localMD5;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void implReset()
/*     */   {
/*  85 */     this.state[0] = 1732584193;
/*  86 */     this.state[1] = -271733879;
/*  87 */     this.state[2] = -1732584194;
/*  88 */     this.state[3] = 271733878;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void implDigest(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/*  97 */     long l = this.bytesProcessed << 3;
/*     */     
/*  99 */     int i = (int)this.bytesProcessed & 0x3F;
/* 100 */     int j = i < 56 ? 56 - i : 120 - i;
/* 101 */     engineUpdate(padding, 0, j);
/*     */     
/* 103 */     ByteArrayAccess.i2bLittle4((int)l, this.buffer, 56);
/* 104 */     ByteArrayAccess.i2bLittle4((int)(l >>> 32), this.buffer, 60);
/* 105 */     implCompress(this.buffer, 0);
/*     */     
/* 107 */     ByteArrayAccess.i2bLittle(this.state, 0, paramArrayOfByte, paramInt, 16);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int FF(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */   {
/* 117 */     paramInt1 += (paramInt2 & paramInt3 | (paramInt2 ^ 0xFFFFFFFF) & paramInt4) + paramInt5 + paramInt7;
/* 118 */     return (paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6) + paramInt2;
/*     */   }
/*     */   
/*     */   private static int GG(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/* 122 */     paramInt1 += (paramInt2 & paramInt4 | paramInt3 & (paramInt4 ^ 0xFFFFFFFF)) + paramInt5 + paramInt7;
/* 123 */     return (paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6) + paramInt2;
/*     */   }
/*     */   
/*     */   private static int HH(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/* 127 */     paramInt1 += (paramInt2 ^ paramInt3 ^ paramInt4) + paramInt5 + paramInt7;
/* 128 */     return (paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6) + paramInt2;
/*     */   }
/*     */   
/*     */   private static int II(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/* 132 */     paramInt1 += (paramInt3 ^ (paramInt2 | paramInt4 ^ 0xFFFFFFFF)) + paramInt5 + paramInt7;
/* 133 */     return (paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6) + paramInt2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void implCompress(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 142 */     ByteArrayAccess.b2iLittle64(paramArrayOfByte, paramInt, this.x);
/*     */     
/* 144 */     int i = this.state[0];
/* 145 */     int j = this.state[1];
/* 146 */     int k = this.state[2];
/* 147 */     int m = this.state[3];
/*     */     
/*     */ 
/* 150 */     i = FF(i, j, k, m, this.x[0], 7, -680876936);
/* 151 */     m = FF(m, i, j, k, this.x[1], 12, -389564586);
/* 152 */     k = FF(k, m, i, j, this.x[2], 17, 606105819);
/* 153 */     j = FF(j, k, m, i, this.x[3], 22, -1044525330);
/* 154 */     i = FF(i, j, k, m, this.x[4], 7, -176418897);
/* 155 */     m = FF(m, i, j, k, this.x[5], 12, 1200080426);
/* 156 */     k = FF(k, m, i, j, this.x[6], 17, -1473231341);
/* 157 */     j = FF(j, k, m, i, this.x[7], 22, -45705983);
/* 158 */     i = FF(i, j, k, m, this.x[8], 7, 1770035416);
/* 159 */     m = FF(m, i, j, k, this.x[9], 12, -1958414417);
/* 160 */     k = FF(k, m, i, j, this.x[10], 17, -42063);
/* 161 */     j = FF(j, k, m, i, this.x[11], 22, -1990404162);
/* 162 */     i = FF(i, j, k, m, this.x[12], 7, 1804603682);
/* 163 */     m = FF(m, i, j, k, this.x[13], 12, -40341101);
/* 164 */     k = FF(k, m, i, j, this.x[14], 17, -1502002290);
/* 165 */     j = FF(j, k, m, i, this.x[15], 22, 1236535329);
/*     */     
/*     */ 
/* 168 */     i = GG(i, j, k, m, this.x[1], 5, -165796510);
/* 169 */     m = GG(m, i, j, k, this.x[6], 9, -1069501632);
/* 170 */     k = GG(k, m, i, j, this.x[11], 14, 643717713);
/* 171 */     j = GG(j, k, m, i, this.x[0], 20, -373897302);
/* 172 */     i = GG(i, j, k, m, this.x[5], 5, -701558691);
/* 173 */     m = GG(m, i, j, k, this.x[10], 9, 38016083);
/* 174 */     k = GG(k, m, i, j, this.x[15], 14, -660478335);
/* 175 */     j = GG(j, k, m, i, this.x[4], 20, -405537848);
/* 176 */     i = GG(i, j, k, m, this.x[9], 5, 568446438);
/* 177 */     m = GG(m, i, j, k, this.x[14], 9, -1019803690);
/* 178 */     k = GG(k, m, i, j, this.x[3], 14, -187363961);
/* 179 */     j = GG(j, k, m, i, this.x[8], 20, 1163531501);
/* 180 */     i = GG(i, j, k, m, this.x[13], 5, -1444681467);
/* 181 */     m = GG(m, i, j, k, this.x[2], 9, -51403784);
/* 182 */     k = GG(k, m, i, j, this.x[7], 14, 1735328473);
/* 183 */     j = GG(j, k, m, i, this.x[12], 20, -1926607734);
/*     */     
/*     */ 
/* 186 */     i = HH(i, j, k, m, this.x[5], 4, -378558);
/* 187 */     m = HH(m, i, j, k, this.x[8], 11, -2022574463);
/* 188 */     k = HH(k, m, i, j, this.x[11], 16, 1839030562);
/* 189 */     j = HH(j, k, m, i, this.x[14], 23, -35309556);
/* 190 */     i = HH(i, j, k, m, this.x[1], 4, -1530992060);
/* 191 */     m = HH(m, i, j, k, this.x[4], 11, 1272893353);
/* 192 */     k = HH(k, m, i, j, this.x[7], 16, -155497632);
/* 193 */     j = HH(j, k, m, i, this.x[10], 23, -1094730640);
/* 194 */     i = HH(i, j, k, m, this.x[13], 4, 681279174);
/* 195 */     m = HH(m, i, j, k, this.x[0], 11, -358537222);
/* 196 */     k = HH(k, m, i, j, this.x[3], 16, -722521979);
/* 197 */     j = HH(j, k, m, i, this.x[6], 23, 76029189);
/* 198 */     i = HH(i, j, k, m, this.x[9], 4, -640364487);
/* 199 */     m = HH(m, i, j, k, this.x[12], 11, -421815835);
/* 200 */     k = HH(k, m, i, j, this.x[15], 16, 530742520);
/* 201 */     j = HH(j, k, m, i, this.x[2], 23, -995338651);
/*     */     
/*     */ 
/* 204 */     i = II(i, j, k, m, this.x[0], 6, -198630844);
/* 205 */     m = II(m, i, j, k, this.x[7], 10, 1126891415);
/* 206 */     k = II(k, m, i, j, this.x[14], 15, -1416354905);
/* 207 */     j = II(j, k, m, i, this.x[5], 21, -57434055);
/* 208 */     i = II(i, j, k, m, this.x[12], 6, 1700485571);
/* 209 */     m = II(m, i, j, k, this.x[3], 10, -1894986606);
/* 210 */     k = II(k, m, i, j, this.x[10], 15, -1051523);
/* 211 */     j = II(j, k, m, i, this.x[1], 21, -2054922799);
/* 212 */     i = II(i, j, k, m, this.x[8], 6, 1873313359);
/* 213 */     m = II(m, i, j, k, this.x[15], 10, -30611744);
/* 214 */     k = II(k, m, i, j, this.x[6], 15, -1560198380);
/* 215 */     j = II(j, k, m, i, this.x[13], 21, 1309151649);
/* 216 */     i = II(i, j, k, m, this.x[4], 6, -145523070);
/* 217 */     m = II(m, i, j, k, this.x[11], 10, -1120210379);
/* 218 */     k = II(k, m, i, j, this.x[2], 15, 718787259);
/* 219 */     j = II(j, k, m, i, this.x[9], 21, -343485551);
/*     */     
/* 221 */     this.state[0] += i;
/* 222 */     this.state[1] += j;
/* 223 */     this.state[2] += k;
/* 224 */     this.state[3] += m;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\MD5.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */