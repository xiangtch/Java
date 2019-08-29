/*     */ package sun.misc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PushbackInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BASE64Decoder
/*     */   extends CharacterDecoder
/*     */ {
/*     */   protected int bytesPerAtom()
/*     */   {
/*  65 */     return 4;
/*     */   }
/*     */   
/*     */   protected int bytesPerLine()
/*     */   {
/*  70 */     return 72;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  77 */   private static final char[] pem_array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  89 */   private static final byte[] pem_convert_array = new byte['Ā'];
/*     */   
/*     */   static {
/*  92 */     for (int i = 0; i < 255; i++) {
/*  93 */       pem_convert_array[i] = -1;
/*     */     }
/*  95 */     for (i = 0; i < pem_array.length; i++) {
/*  96 */       pem_convert_array[pem_array[i]] = ((byte)i);
/*     */     }
/*     */   }
/*     */   
/* 100 */   byte[] decode_buffer = new byte[4];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void decodeAtom(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/* 110 */     int j = -1;int k = -1;int m = -1;int n = -1;
/*     */     
/* 112 */     if (paramInt < 2) {
/* 113 */       throw new CEFormatException("BASE64Decoder: Not enough bytes for an atom.");
/*     */     }
/*     */     do {
/* 116 */       i = paramPushbackInputStream.read();
/* 117 */       if (i == -1) {
/* 118 */         throw new CEStreamExhausted();
/*     */       }
/* 120 */     } while ((i == 10) || (i == 13));
/* 121 */     this.decode_buffer[0] = ((byte)i);
/*     */     
/* 123 */     int i = readFully(paramPushbackInputStream, this.decode_buffer, 1, paramInt - 1);
/* 124 */     if (i == -1) {
/* 125 */       throw new CEStreamExhausted();
/*     */     }
/*     */     
/* 128 */     if ((paramInt > 3) && (this.decode_buffer[3] == 61)) {
/* 129 */       paramInt = 3;
/*     */     }
/* 131 */     if ((paramInt > 2) && (this.decode_buffer[2] == 61)) {
/* 132 */       paramInt = 2;
/*     */     }
/* 134 */     switch (paramInt) {
/*     */     case 4: 
/* 136 */       n = pem_convert_array[(this.decode_buffer[3] & 0xFF)];
/*     */     
/*     */     case 3: 
/* 139 */       m = pem_convert_array[(this.decode_buffer[2] & 0xFF)];
/*     */     
/*     */     case 2: 
/* 142 */       k = pem_convert_array[(this.decode_buffer[1] & 0xFF)];
/* 143 */       j = pem_convert_array[(this.decode_buffer[0] & 0xFF)];
/*     */     }
/*     */     
/*     */     
/* 147 */     switch (paramInt) {
/*     */     case 2: 
/* 149 */       paramOutputStream.write((byte)(j << 2 & 0xFC | k >>> 4 & 0x3));
/* 150 */       break;
/*     */     case 3: 
/* 152 */       paramOutputStream.write((byte)(j << 2 & 0xFC | k >>> 4 & 0x3));
/* 153 */       paramOutputStream.write((byte)(k << 4 & 0xF0 | m >>> 2 & 0xF));
/* 154 */       break;
/*     */     case 4: 
/* 156 */       paramOutputStream.write((byte)(j << 2 & 0xFC | k >>> 4 & 0x3));
/* 157 */       paramOutputStream.write((byte)(k << 4 & 0xF0 | m >>> 2 & 0xF));
/* 158 */       paramOutputStream.write((byte)(m << 6 & 0xC0 | n & 0x3F));
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\BASE64Decoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */