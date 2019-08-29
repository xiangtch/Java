/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Provider;
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
/*     */ public final class MD4
/*     */   extends DigestBase
/*     */ {
/*     */   private int[] state;
/*     */   private int[] x;
/*     */   private static final int S11 = 3;
/*     */   private static final int S12 = 7;
/*     */   private static final int S13 = 11;
/*     */   private static final int S14 = 19;
/*     */   private static final int S21 = 3;
/*     */   private static final int S22 = 5;
/*     */   private static final int S23 = 9;
/*     */   private static final int S24 = 13;
/*     */   private static final int S31 = 3;
/*     */   private static final int S32 = 9;
/*     */   private static final int S33 = 11;
/*     */   private static final int S34 = 15;
/*  68 */   private static final Provider md4Provider = new Provider("MD4Provider", 1.8D, "MD4 MessageDigest") { private static final long serialVersionUID = -8850464997518327965L;
/*     */   };
/*     */   
/*  71 */   static { AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Void run() {
/*  73 */         MD4.md4Provider.put("MessageDigest.MD4", "sun.security.provider.MD4");
/*  74 */         return null;
/*     */       }
/*     */     }); }
/*     */   
/*     */   public static MessageDigest getInstance()
/*     */   {
/*     */     try {
/*  81 */       return MessageDigest.getInstance("MD4", md4Provider);
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/*  84 */       throw new ProviderException(localNoSuchAlgorithmException);
/*     */     }
/*     */   }
/*     */   
/*     */   public MD4()
/*     */   {
/*  90 */     super("MD4", 16, 64);
/*  91 */     this.state = new int[4];
/*  92 */     this.x = new int[16];
/*  93 */     implReset();
/*     */   }
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException
/*     */   {
/*  98 */     MD4 localMD4 = (MD4)super.clone();
/*  99 */     localMD4.state = ((int[])localMD4.state.clone());
/* 100 */     localMD4.x = new int[16];
/* 101 */     return localMD4;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void implReset()
/*     */   {
/* 109 */     this.state[0] = 1732584193;
/* 110 */     this.state[1] = -271733879;
/* 111 */     this.state[2] = -1732584194;
/* 112 */     this.state[3] = 271733878;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void implDigest(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 121 */     long l = this.bytesProcessed << 3;
/*     */     
/* 123 */     int i = (int)this.bytesProcessed & 0x3F;
/* 124 */     int j = i < 56 ? 56 - i : 120 - i;
/* 125 */     engineUpdate(padding, 0, j);
/*     */     
/* 127 */     ByteArrayAccess.i2bLittle4((int)l, this.buffer, 56);
/* 128 */     ByteArrayAccess.i2bLittle4((int)(l >>> 32), this.buffer, 60);
/* 129 */     implCompress(this.buffer, 0);
/*     */     
/* 131 */     ByteArrayAccess.i2bLittle(this.state, 0, paramArrayOfByte, paramInt, 16);
/*     */   }
/*     */   
/*     */   private static int FF(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
/* 135 */     paramInt1 += (paramInt2 & paramInt3 | (paramInt2 ^ 0xFFFFFFFF) & paramInt4) + paramInt5;
/* 136 */     return paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6;
/*     */   }
/*     */   
/*     */   private static int GG(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
/* 140 */     paramInt1 += (paramInt2 & paramInt3 | paramInt2 & paramInt4 | paramInt3 & paramInt4) + paramInt5 + 1518500249;
/* 141 */     return paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6;
/*     */   }
/*     */   
/*     */   private static int HH(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
/* 145 */     paramInt1 += (paramInt2 ^ paramInt3 ^ paramInt4) + paramInt5 + 1859775393;
/* 146 */     return paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void implCompress(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 155 */     ByteArrayAccess.b2iLittle64(paramArrayOfByte, paramInt, this.x);
/*     */     
/* 157 */     int i = this.state[0];
/* 158 */     int j = this.state[1];
/* 159 */     int k = this.state[2];
/* 160 */     int m = this.state[3];
/*     */     
/*     */ 
/* 163 */     i = FF(i, j, k, m, this.x[0], 3);
/* 164 */     m = FF(m, i, j, k, this.x[1], 7);
/* 165 */     k = FF(k, m, i, j, this.x[2], 11);
/* 166 */     j = FF(j, k, m, i, this.x[3], 19);
/* 167 */     i = FF(i, j, k, m, this.x[4], 3);
/* 168 */     m = FF(m, i, j, k, this.x[5], 7);
/* 169 */     k = FF(k, m, i, j, this.x[6], 11);
/* 170 */     j = FF(j, k, m, i, this.x[7], 19);
/* 171 */     i = FF(i, j, k, m, this.x[8], 3);
/* 172 */     m = FF(m, i, j, k, this.x[9], 7);
/* 173 */     k = FF(k, m, i, j, this.x[10], 11);
/* 174 */     j = FF(j, k, m, i, this.x[11], 19);
/* 175 */     i = FF(i, j, k, m, this.x[12], 3);
/* 176 */     m = FF(m, i, j, k, this.x[13], 7);
/* 177 */     k = FF(k, m, i, j, this.x[14], 11);
/* 178 */     j = FF(j, k, m, i, this.x[15], 19);
/*     */     
/*     */ 
/* 181 */     i = GG(i, j, k, m, this.x[0], 3);
/* 182 */     m = GG(m, i, j, k, this.x[4], 5);
/* 183 */     k = GG(k, m, i, j, this.x[8], 9);
/* 184 */     j = GG(j, k, m, i, this.x[12], 13);
/* 185 */     i = GG(i, j, k, m, this.x[1], 3);
/* 186 */     m = GG(m, i, j, k, this.x[5], 5);
/* 187 */     k = GG(k, m, i, j, this.x[9], 9);
/* 188 */     j = GG(j, k, m, i, this.x[13], 13);
/* 189 */     i = GG(i, j, k, m, this.x[2], 3);
/* 190 */     m = GG(m, i, j, k, this.x[6], 5);
/* 191 */     k = GG(k, m, i, j, this.x[10], 9);
/* 192 */     j = GG(j, k, m, i, this.x[14], 13);
/* 193 */     i = GG(i, j, k, m, this.x[3], 3);
/* 194 */     m = GG(m, i, j, k, this.x[7], 5);
/* 195 */     k = GG(k, m, i, j, this.x[11], 9);
/* 196 */     j = GG(j, k, m, i, this.x[15], 13);
/*     */     
/*     */ 
/* 199 */     i = HH(i, j, k, m, this.x[0], 3);
/* 200 */     m = HH(m, i, j, k, this.x[8], 9);
/* 201 */     k = HH(k, m, i, j, this.x[4], 11);
/* 202 */     j = HH(j, k, m, i, this.x[12], 15);
/* 203 */     i = HH(i, j, k, m, this.x[2], 3);
/* 204 */     m = HH(m, i, j, k, this.x[10], 9);
/* 205 */     k = HH(k, m, i, j, this.x[6], 11);
/* 206 */     j = HH(j, k, m, i, this.x[14], 15);
/* 207 */     i = HH(i, j, k, m, this.x[1], 3);
/* 208 */     m = HH(m, i, j, k, this.x[9], 9);
/* 209 */     k = HH(k, m, i, j, this.x[5], 11);
/* 210 */     j = HH(j, k, m, i, this.x[13], 15);
/* 211 */     i = HH(i, j, k, m, this.x[3], 3);
/* 212 */     m = HH(m, i, j, k, this.x[11], 9);
/* 213 */     k = HH(k, m, i, j, this.x[7], 11);
/* 214 */     j = HH(j, k, m, i, this.x[15], 15);
/*     */     
/* 216 */     this.state[0] += i;
/* 217 */     this.state[1] += j;
/* 218 */     this.state[2] += k;
/* 219 */     this.state[3] += m;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\MD4.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */