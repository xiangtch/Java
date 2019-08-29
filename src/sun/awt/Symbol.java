/*     */ package sun.awt;
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
/*     */ public class Symbol
/*     */   extends Charset
/*     */ {
/*     */   public Symbol()
/*     */   {
/*  34 */     super("Symbol", null);
/*     */   }
/*     */   
/*  37 */   public CharsetEncoder newEncoder() { return new Encoder(this); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CharsetDecoder newDecoder()
/*     */   {
/*  44 */     throw new Error("Decoder is not implemented for Symbol Charset");
/*     */   }
/*     */   
/*     */   public boolean contains(Charset paramCharset) {
/*  48 */     return paramCharset instanceof Symbol;
/*     */   }
/*     */   
/*     */   private static class Encoder extends CharsetEncoder {
/*     */     public Encoder(Charset paramCharset) {
/*  53 */       super(1.0F, 1.0F);
/*     */     }
/*     */     
/*     */     public boolean canEncode(char paramChar) {
/*  57 */       if ((paramChar >= '∀') && (paramChar <= '⋯')) {
/*  58 */         if (table_math[(paramChar - '∀')] != 0) {
/*  59 */           return true;
/*     */         }
/*  61 */       } else if ((paramChar >= 'Α') && (paramChar <= 'ϖ') && 
/*  62 */         (table_greek[(paramChar - 'Α')] != 0)) {
/*  63 */         return true;
/*     */       }
/*     */       
/*  66 */       return false;
/*     */     }
/*     */     
/*     */     protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer) {
/*  70 */       char[] arrayOfChar = paramCharBuffer.array();
/*  71 */       int i = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
/*  72 */       int j = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
/*  73 */       assert (i <= j);
/*  74 */       i = i <= j ? i : j;
/*  75 */       byte[] arrayOfByte = paramByteBuffer.array();
/*  76 */       int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/*  77 */       int m = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
/*  78 */       assert (k <= m);
/*  79 */       k = k <= m ? k : m;
/*     */       try
/*     */       {
/*  82 */         while (i < j) {
/*  83 */           char c = arrayOfChar[i];
/*  84 */           CoderResult localCoderResult2; if (m - k < 1)
/*  85 */             return CoderResult.OVERFLOW;
/*  86 */           if (!canEncode(c))
/*  87 */             return CoderResult.unmappableForLength(1);
/*  88 */           i++;
/*  89 */           if ((c >= '∀') && (c <= '⋯')) {
/*  90 */             arrayOfByte[(k++)] = table_math[(c - '∀')];
/*  91 */           } else if ((c >= 'Α') && (c <= 'ϖ')) {
/*  92 */             arrayOfByte[(k++)] = table_greek[(c - 'Α')];
/*     */           }
/*     */         }
/*  95 */         return CoderResult.UNDERFLOW;
/*     */       } finally {
/*  97 */         paramCharBuffer.position(i - paramCharBuffer.arrayOffset());
/*  98 */         paramByteBuffer.position(k - paramByteBuffer.arrayOffset());
/*     */       }
/*     */     }
/*     */     
/* 102 */     private static byte[] table_math = { 34, 0, 100, 36, 0, -58, 68, -47, -50, -49, 0, 0, 0, 39, 0, 80, 0, -27, 45, 0, 0, -92, 0, 42, -80, -73, -42, 0, 0, -75, -91, 0, 0, 0, 0, -67, 0, 0, 0, -39, -38, -57, -56, -14, 0, 0, 0, 0, 0, 0, 0, 0, 92, 0, 0, 0, 0, 0, 0, 0, 126, 0, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, -69, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -71, -70, 0, 0, -93, -77, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -52, -55, -53, 0, -51, -54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -59, 0, -60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -32, -41, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -68 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 165 */     private static byte[] table_greek = { 65, 66, 71, 68, 69, 90, 72, 81, 73, 75, 76, 77, 78, 88, 79, 80, 82, 0, 83, 84, 85, 70, 67, 89, 87, 0, 0, 0, 0, 0, 0, 0, 97, 98, 103, 100, 101, 122, 104, 113, 105, 107, 108, 109, 110, 120, 111, 112, 114, 86, 115, 116, 117, 102, 99, 121, 119, 0, 0, 0, 0, 0, 0, 0, 74, -95, 0, 0, 106, 118 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isLegalReplacement(byte[] paramArrayOfByte)
/*     */     {
/* 188 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\Symbol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */