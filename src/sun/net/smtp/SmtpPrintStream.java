/*     */ package sun.net.smtp;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SmtpPrintStream
/*     */   extends PrintStream
/*     */ {
/*     */   private SmtpClient target;
/* 225 */   private int lastc = 10;
/*     */   
/*     */   SmtpPrintStream(OutputStream paramOutputStream, SmtpClient paramSmtpClient) throws UnsupportedEncodingException {
/* 228 */     super(paramOutputStream, false, paramSmtpClient.getEncoding());
/* 229 */     this.target = paramSmtpClient;
/*     */   }
/*     */   
/*     */   public void close() {
/* 233 */     if (this.target == null)
/* 234 */       return;
/* 235 */     if (this.lastc != 10) {
/* 236 */       write(10);
/*     */     }
/*     */     try {
/* 239 */       this.target.issueCommand(".\r\n", 250);
/* 240 */       this.target.message = null;
/* 241 */       this.out = null;
/* 242 */       this.target = null;
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */   }
/*     */   
/*     */   public void write(int paramInt)
/*     */   {
/*     */     try {
/* 250 */       if ((this.lastc == 10) && (paramInt == 46)) {
/* 251 */         this.out.write(46);
/*     */       }
/*     */       
/*     */ 
/* 255 */       if ((paramInt == 10) && (this.lastc != 13)) {
/* 256 */         this.out.write(13);
/*     */       }
/* 258 */       this.out.write(paramInt);
/* 259 */       this.lastc = paramInt;
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */   }
/*     */   
/*     */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
/*     */     try {
/* 266 */       int i = this.lastc;
/* 267 */       for (;;) { paramInt2--; if (paramInt2 < 0) break;
/* 268 */         int j = paramArrayOfByte[(paramInt1++)];
/*     */         
/*     */ 
/* 271 */         if ((i == 10) && (j == 46)) {
/* 272 */           this.out.write(46);
/*     */         }
/*     */         
/* 275 */         if ((j == 10) && (i != 13)) {
/* 276 */           this.out.write(13);
/*     */         }
/* 278 */         this.out.write(j);
/* 279 */         i = j;
/*     */       }
/* 281 */       this.lastc = i;
/*     */     } catch (IOException localIOException) {}
/*     */   }
/*     */   
/*     */   public void print(String paramString) {
/* 286 */     int i = paramString.length();
/* 287 */     for (int j = 0; j < i; j++) {
/* 288 */       write(paramString.charAt(j));
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\smtp\SmtpPrintStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */