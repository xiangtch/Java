/*     */ package sun.awt.windows;
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
/*     */ public final class WingDings
/*     */   extends Charset
/*     */ {
/*     */   public WingDings()
/*     */   {
/*  34 */     super("WingDings", null);
/*     */   }
/*     */   
/*     */   public CharsetEncoder newEncoder()
/*     */   {
/*  39 */     return new Encoder(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CharsetDecoder newDecoder()
/*     */   {
/*  47 */     throw new Error("Decoder isn't implemented for WingDings Charset");
/*     */   }
/*     */   
/*     */   public boolean contains(Charset paramCharset)
/*     */   {
/*  52 */     return paramCharset instanceof WingDings;
/*     */   }
/*     */   
/*     */   private static class Encoder extends CharsetEncoder {
/*     */     public Encoder(Charset paramCharset) {
/*  57 */       super(1.0F, 1.0F);
/*     */     }
/*     */     
/*     */     public boolean canEncode(char paramChar)
/*     */     {
/*  62 */       if ((paramChar >= '✁') && (paramChar <= '➾')) {
/*  63 */         if (table[(paramChar - '✀')] != 0) {
/*  64 */           return true;
/*     */         }
/*  66 */         return false;
/*     */       }
/*  68 */       return false;
/*     */     }
/*     */     
/*     */     protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */     {
/*  73 */       char[] arrayOfChar = paramCharBuffer.array();
/*  74 */       int i = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
/*  75 */       int j = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
/*  76 */       assert (i <= j);
/*  77 */       i = i <= j ? i : j;
/*  78 */       byte[] arrayOfByte = paramByteBuffer.array();
/*  79 */       int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/*  80 */       int m = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
/*  81 */       assert (k <= m);
/*  82 */       k = k <= m ? k : m;
/*     */       try
/*     */       {
/*  85 */         while (i < j) {
/*  86 */           char c = arrayOfChar[i];
/*  87 */           CoderResult localCoderResult2; if (m - k < 1)
/*  88 */             return CoderResult.OVERFLOW;
/*  89 */           if (!canEncode(c))
/*  90 */             return CoderResult.unmappableForLength(1);
/*  91 */           i++;
/*  92 */           arrayOfByte[(k++)] = table[(c - '✀')];
/*     */         }
/*  94 */         return CoderResult.UNDERFLOW;
/*     */       } finally {
/*  96 */         paramCharBuffer.position(i - paramCharBuffer.arrayOffset());
/*  97 */         paramByteBuffer.position(k - paramByteBuffer.arrayOffset());
/*     */       }
/*     */     }
/*     */     
/* 101 */     private static byte[] table = { 0, 35, 34, 0, 0, 0, 41, 62, 81, 42, 0, 0, 65, 63, 0, 0, 0, 0, 0, -4, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 86, 0, 88, 89, 0, 0, 0, 0, 0, 0, 0, 0, -75, 0, 0, 0, 0, 0, -74, 0, 0, 0, -83, -81, -84, 0, 0, 0, 0, 0, 0, 0, 0, 124, 123, 0, 0, 0, 84, 0, 0, 0, 0, 0, 0, 0, 0, -90, 0, 0, 0, 113, 114, 0, 0, 0, 117, 0, 0, 0, 0, 0, 0, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -24, -40, 0, 0, -60, -58, 0, 0, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, -36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 166 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WingDings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */