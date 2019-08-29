/*     */ package sun.net;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TelnetInputStream
/*     */   extends FilterInputStream
/*     */ {
/*  76 */   boolean stickyCRLF = false;
/*  77 */   boolean seenCR = false;
/*     */   
/*  79 */   public boolean binaryMode = false;
/*     */   
/*     */   public TelnetInputStream(InputStream paramInputStream, boolean paramBoolean) {
/*  82 */     super(paramInputStream);
/*  83 */     this.binaryMode = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setStickyCRLF(boolean paramBoolean) {
/*  87 */     this.stickyCRLF = paramBoolean;
/*     */   }
/*     */   
/*     */   public int read() throws IOException {
/*  91 */     if (this.binaryMode) {
/*  92 */       return super.read();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */     if (this.seenCR) {
/* 102 */       this.seenCR = false;
/* 103 */       return 10;
/*     */     }
/*     */     int i;
/* 106 */     if ((i = super.read()) == 13) {
/* 107 */       switch (i = super.read()) {
/*     */       case -1: 
/*     */       default: 
/* 110 */         throw new TelnetProtocolException("misplaced CR in input");
/*     */       
/*     */       case 0: 
/* 113 */         return 13;
/*     */       }
/*     */       
/* 116 */       if (this.stickyCRLF) {
/* 117 */         this.seenCR = true;
/* 118 */         return 13;
/*     */       }
/* 120 */       return 10;
/*     */     }
/*     */     
/*     */ 
/* 124 */     return i;
/*     */   }
/*     */   
/*     */   public int read(byte[] paramArrayOfByte) throws IOException
/*     */   {
/* 129 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 137 */     if (this.binaryMode) {
/* 138 */       return super.read(paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/*     */     
/* 141 */     int j = paramInt1;
/*     */     for (;;) {
/* 143 */       paramInt2--; if (paramInt2 < 0) break;
/* 144 */       int i = read();
/* 145 */       if (i == -1)
/*     */         break;
/* 147 */       paramArrayOfByte[(paramInt1++)] = ((byte)i);
/*     */     }
/* 149 */     return paramInt1 > j ? paramInt1 - j : -1;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\TelnetInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */