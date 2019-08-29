/*     */ package sun.security.krb5.internal.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteOrder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KrbDataInputStream
/*     */   extends BufferedInputStream
/*     */ {
/*  46 */   private boolean bigEndian = true;
/*     */   
/*     */   public void setNativeByteOrder()
/*     */   {
/*  50 */     if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
/*  51 */       this.bigEndian = true;
/*     */     } else
/*  53 */       this.bigEndian = false;
/*     */   }
/*     */   
/*     */   public KrbDataInputStream(InputStream paramInputStream) {
/*  57 */     super(paramInputStream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final int readLength4()
/*     */     throws IOException
/*     */   {
/*  68 */     int i = read(4);
/*  69 */     if (i < 0) {
/*  70 */       throw new IOException("Invalid encoding");
/*     */     }
/*  72 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(int paramInt)
/*     */     throws IOException
/*     */   {
/*  82 */     byte[] arrayOfByte = new byte[paramInt];
/*  83 */     if (read(arrayOfByte, 0, paramInt) != paramInt) {
/*  84 */       throw new IOException("Premature end of stream reached");
/*     */     }
/*  86 */     int i = 0;
/*  87 */     for (int j = 0; j < paramInt; j++) {
/*  88 */       if (this.bigEndian) {
/*  89 */         i |= (arrayOfByte[j] & 0xFF) << (paramInt - j - 1) * 8;
/*     */       } else {
/*  91 */         i |= (arrayOfByte[j] & 0xFF) << j * 8;
/*     */       }
/*     */     }
/*  94 */     return i;
/*     */   }
/*     */   
/*     */   public int readVersion() throws IOException
/*     */   {
/*  99 */     int i = (read() & 0xFF) << 8;
/* 100 */     return i | read() & 0xFF;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\util\KrbDataInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */