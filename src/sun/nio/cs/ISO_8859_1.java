/*     */ package sun.nio.cs;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ISO_8859_1
/*     */   extends Charset
/*     */   implements HistoricallyNamedCharset
/*     */ {
/*     */   public ISO_8859_1()
/*     */   {
/*  42 */     super("ISO-8859-1", StandardCharsets.aliases_ISO_8859_1);
/*     */   }
/*     */   
/*     */   public String historicalName() {
/*  46 */     return "ISO8859_1";
/*     */   }
/*     */   
/*     */   public boolean contains(Charset paramCharset) {
/*  50 */     return ((paramCharset instanceof US_ASCII)) || ((paramCharset instanceof ISO_8859_1));
/*     */   }
/*     */   
/*     */   public CharsetDecoder newDecoder()
/*     */   {
/*  55 */     return new Decoder(this, null);
/*     */   }
/*     */   
/*     */   public CharsetEncoder newEncoder() {
/*  59 */     return new Encoder(this, null);
/*     */   }
/*     */   
/*     */   private static class Decoder extends CharsetDecoder implements ArrayDecoder
/*     */   {
/*     */     private Decoder(Charset paramCharset) {
/*  65 */       super(1.0F, 1.0F);
/*     */     }
/*     */     
/*     */ 
/*     */     private CoderResult decodeArrayLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */     {
/*  71 */       byte[] arrayOfByte = paramByteBuffer.array();
/*  72 */       int i = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/*  73 */       int j = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
/*  74 */       assert (i <= j);
/*  75 */       i = i <= j ? i : j;
/*  76 */       char[] arrayOfChar = paramCharBuffer.array();
/*  77 */       int k = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
/*  78 */       int m = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
/*  79 */       assert (k <= m);
/*  80 */       k = k <= m ? k : m;
/*     */       try
/*     */       {
/*  83 */         while (i < j) {
/*  84 */           int n = arrayOfByte[i];
/*  85 */           if (k >= m)
/*  86 */             return CoderResult.OVERFLOW;
/*  87 */           arrayOfChar[(k++)] = ((char)(n & 0xFF));
/*  88 */           i++;
/*     */         }
/*  90 */         return CoderResult.UNDERFLOW;
/*     */       } finally {
/*  92 */         paramByteBuffer.position(i - paramByteBuffer.arrayOffset());
/*  93 */         paramCharBuffer.position(k - paramCharBuffer.arrayOffset());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     private CoderResult decodeBufferLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */     {
/* 100 */       int i = paramByteBuffer.position();
/*     */       try {
/* 102 */         while (paramByteBuffer.hasRemaining()) {
/* 103 */           int j = paramByteBuffer.get();
/* 104 */           if (!paramCharBuffer.hasRemaining())
/* 105 */             return CoderResult.OVERFLOW;
/* 106 */           paramCharBuffer.put((char)(j & 0xFF));
/* 107 */           i++;
/*     */         }
/* 109 */         return CoderResult.UNDERFLOW;
/*     */       } finally {
/* 111 */         paramByteBuffer.position(i);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */     {
/* 118 */       if ((paramByteBuffer.hasArray()) && (paramCharBuffer.hasArray())) {
/* 119 */         return decodeArrayLoop(paramByteBuffer, paramCharBuffer);
/*     */       }
/* 121 */       return decodeBufferLoop(paramByteBuffer, paramCharBuffer);
/*     */     }
/*     */     
/*     */     public int decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[] paramArrayOfChar) {
/* 125 */       if (paramInt2 > paramArrayOfChar.length)
/* 126 */         paramInt2 = paramArrayOfChar.length;
/* 127 */       int i = 0;
/* 128 */       while (i < paramInt2)
/* 129 */         paramArrayOfChar[(i++)] = ((char)(paramArrayOfByte[(paramInt1++)] & 0xFF));
/* 130 */       return i;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Encoder extends CharsetEncoder implements ArrayEncoder
/*     */   {
/*     */     private Encoder(Charset paramCharset) {
/* 137 */       super(1.0F, 1.0F);
/*     */     }
/*     */     
/*     */     public boolean canEncode(char paramChar) {
/* 141 */       return paramChar <= 'ÿ';
/*     */     }
/*     */     
/*     */     public boolean isLegalReplacement(byte[] paramArrayOfByte) {
/* 145 */       return true;
/*     */     }
/*     */     
/* 148 */     private final Surrogate.Parser sgp = new Surrogate.Parser();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private static int encodeISOArray(char[] paramArrayOfChar, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
/*     */     {
/* 155 */       for (int i = 0; 
/* 156 */           i < paramInt3; i++) {
/* 157 */         int j = paramArrayOfChar[(paramInt1++)];
/* 158 */         if (j > 255)
/*     */           break;
/* 160 */         paramArrayOfByte[(paramInt2++)] = ((byte)j);
/*     */       }
/* 162 */       return i;
/*     */     }
/*     */     
/*     */ 
/*     */     private CoderResult encodeArrayLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */     {
/* 168 */       char[] arrayOfChar = paramCharBuffer.array();
/* 169 */       int i = paramCharBuffer.arrayOffset();
/* 170 */       int j = i + paramCharBuffer.position();
/* 171 */       int k = i + paramCharBuffer.limit();
/* 172 */       assert (j <= k);
/* 173 */       j = j <= k ? j : k;
/* 174 */       byte[] arrayOfByte = paramByteBuffer.array();
/* 175 */       int m = paramByteBuffer.arrayOffset();
/* 176 */       int n = m + paramByteBuffer.position();
/* 177 */       int i1 = m + paramByteBuffer.limit();
/* 178 */       assert (n <= i1);
/* 179 */       n = n <= i1 ? n : i1;
/* 180 */       int i2 = i1 - n;
/* 181 */       int i3 = k - j;
/* 182 */       int i4 = i2 < i3 ? i2 : i3;
/*     */       try {
/* 184 */         int i5 = i4 <= 0 ? 0 : encodeISOArray(arrayOfChar, j, arrayOfByte, n, i4);
/* 185 */         j += i5;
/* 186 */         n += i5;
/* 187 */         CoderResult localCoderResult; if (i5 != i4) {
/* 188 */           if (this.sgp.parse(arrayOfChar[j], arrayOfChar, j, k) < 0)
/* 189 */             return this.sgp.error();
/* 190 */           return this.sgp.unmappableResult();
/*     */         }
/* 192 */         if (i4 < i3)
/* 193 */           return CoderResult.OVERFLOW;
/* 194 */         return CoderResult.UNDERFLOW;
/*     */       } finally {
/* 196 */         paramCharBuffer.position(j - i);
/* 197 */         paramByteBuffer.position(n - m);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     private CoderResult encodeBufferLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */     {
/* 204 */       int i = paramCharBuffer.position();
/*     */       try {
/* 206 */         while (paramCharBuffer.hasRemaining()) {
/* 207 */           char c = paramCharBuffer.get();
/* 208 */           CoderResult localCoderResult2; if (c <= 'ÿ') {
/* 209 */             if (!paramByteBuffer.hasRemaining())
/* 210 */               return CoderResult.OVERFLOW;
/* 211 */             paramByteBuffer.put((byte)c);
/* 212 */             i++;
/*     */           }
/*     */           else {
/* 215 */             if (this.sgp.parse(c, paramCharBuffer) < 0)
/* 216 */               return this.sgp.error();
/* 217 */             return this.sgp.unmappableResult();
/*     */           } }
/* 219 */         return CoderResult.UNDERFLOW;
/*     */       } finally {
/* 221 */         paramCharBuffer.position(i);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */     {
/* 228 */       if ((paramCharBuffer.hasArray()) && (paramByteBuffer.hasArray())) {
/* 229 */         return encodeArrayLoop(paramCharBuffer, paramByteBuffer);
/*     */       }
/* 231 */       return encodeBufferLoop(paramCharBuffer, paramByteBuffer);
/*     */     }
/*     */     
/* 234 */     private byte repl = 63;
/*     */     
/* 236 */     protected void implReplaceWith(byte[] paramArrayOfByte) { this.repl = paramArrayOfByte[0]; }
/*     */     
/*     */     public int encode(char[] paramArrayOfChar, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
/*     */     {
/* 240 */       int i = 0;
/* 241 */       int j = Math.min(paramInt2, paramArrayOfByte.length);
/* 242 */       int k = paramInt1 + j;
/* 243 */       while (paramInt1 < k)
/*     */       {
/* 245 */         int m = j <= 0 ? 0 : encodeISOArray(paramArrayOfChar, paramInt1, paramArrayOfByte, i, j);
/* 246 */         paramInt1 += m;
/* 247 */         i += m;
/* 248 */         if (m != j) {
/* 249 */           char c = paramArrayOfChar[(paramInt1++)];
/* 250 */           if ((Character.isHighSurrogate(c)) && (paramInt1 < k) && 
/* 251 */             (Character.isLowSurrogate(paramArrayOfChar[paramInt1]))) {
/* 252 */             if (paramInt2 > paramArrayOfByte.length) {
/* 253 */               k++;
/* 254 */               paramInt2--;
/*     */             }
/* 256 */             paramInt1++;
/*     */           }
/* 258 */           paramArrayOfByte[(i++)] = this.repl;
/* 259 */           j = Math.min(k - paramInt1, paramArrayOfByte.length - i);
/*     */         }
/*     */       }
/* 262 */       return i;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\cs\ISO_8859_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */