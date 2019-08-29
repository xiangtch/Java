/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.SecureRandomSpi;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SecureRandom
/*     */   extends SecureRandomSpi
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3581829991155417889L;
/*     */   private static final int DIGEST_SIZE = 20;
/*     */   private transient MessageDigest digest;
/*     */   private byte[] state;
/*     */   private byte[] remainder;
/*     */   private int remCount;
/*     */   
/*     */   public SecureRandom()
/*     */   {
/*  79 */     init(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SecureRandom(byte[] paramArrayOfByte)
/*     */   {
/*  89 */     init(paramArrayOfByte);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void init(byte[] paramArrayOfByte)
/*     */   {
/*     */     try
/*     */     {
/* 102 */       this.digest = MessageDigest.getInstance("SHA", "SUN");
/*     */     }
/*     */     catch (NoSuchProviderException|NoSuchAlgorithmException localNoSuchProviderException) {
/*     */       try {
/* 106 */         this.digest = MessageDigest.getInstance("SHA");
/*     */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 108 */         throw new InternalError("internal error: SHA-1 not available.", localNoSuchAlgorithmException);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 113 */     if (paramArrayOfByte != null) {
/* 114 */       engineSetSeed(paramArrayOfByte);
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] engineGenerateSeed(int paramInt)
/*     */   {
/* 138 */     byte[] arrayOfByte = new byte[paramInt];
/* 139 */     SeedGenerator.generateSeed(arrayOfByte);
/* 140 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void engineSetSeed(byte[] paramArrayOfByte)
/*     */   {
/* 152 */     if (this.state != null) {
/* 153 */       this.digest.update(this.state);
/* 154 */       for (int i = 0; i < this.state.length; i++) {
/* 155 */         this.state[i] = 0;
/*     */       }
/*     */     }
/* 158 */     this.state = this.digest.digest(paramArrayOfByte);
/*     */   }
/*     */   
/*     */   private static void updateState(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
/* 162 */     int i = 1;
/*     */     
/*     */ 
/* 165 */     int m = 0;
/*     */     
/*     */ 
/* 168 */     for (int n = 0; n < paramArrayOfByte1.length; n++)
/*     */     {
/* 170 */       int j = paramArrayOfByte1[n] + paramArrayOfByte2[n] + i;
/*     */       
/* 172 */       int k = (byte)j;
/*     */       
/* 174 */       m |= (paramArrayOfByte1[n] != k ? 1 : 0);
/* 175 */       paramArrayOfByte1[n] = k;
/*     */       
/* 177 */       i = j >> 8;
/*     */     }
/*     */     
/*     */ 
/* 181 */     if (m == 0) {
/* 182 */       int tmp74_73 = 0;paramArrayOfByte1[tmp74_73] = ((byte)(paramArrayOfByte1[tmp74_73] + 1));
/*     */     }
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
/*     */ 
/*     */   private static class SeederHolder
/*     */   {
/* 201 */     private static final SecureRandom seeder = new SecureRandom(SeedGenerator.getSystemEntropy(), null);
/* 202 */     static { byte[] arrayOfByte = new byte[20];
/* 203 */       SeedGenerator.generateSeed(arrayOfByte);
/* 204 */       seeder.engineSetSeed(arrayOfByte);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void engineNextBytes(byte[] paramArrayOfByte)
/*     */   {
/* 215 */     int i = 0;
/*     */     
/* 217 */     byte[] arrayOfByte1 = this.remainder;
/*     */     
/* 219 */     if (this.state == null) {
/* 220 */       byte[] arrayOfByte2 = new byte[20];
/* 221 */       SeederHolder.seeder.engineNextBytes(arrayOfByte2);
/* 222 */       this.state = this.digest.digest(arrayOfByte2);
/*     */     }
/*     */     
/*     */ 
/* 226 */     int k = this.remCount;
/* 227 */     int j; int m; if (k > 0)
/*     */     {
/* 229 */       j = paramArrayOfByte.length - i < 20 - k ? paramArrayOfByte.length - i : 20 - k;
/*     */       
/*     */ 
/* 232 */       for (m = 0; m < j; m++) {
/* 233 */         paramArrayOfByte[m] = arrayOfByte1[k];
/* 234 */         arrayOfByte1[(k++)] = 0;
/*     */       }
/* 236 */       this.remCount += j;
/* 237 */       i += j;
/*     */     }
/*     */     
/*     */ 
/* 241 */     while (i < paramArrayOfByte.length)
/*     */     {
/* 243 */       this.digest.update(this.state);
/* 244 */       arrayOfByte1 = this.digest.digest();
/* 245 */       updateState(this.state, arrayOfByte1);
/*     */       
/*     */ 
/* 248 */       j = paramArrayOfByte.length - i > 20 ? 20 : paramArrayOfByte.length - i;
/*     */       
/*     */ 
/* 251 */       for (m = 0; m < j; m++) {
/* 252 */         paramArrayOfByte[(i++)] = arrayOfByte1[m];
/* 253 */         arrayOfByte1[m] = 0;
/*     */       }
/* 255 */       this.remCount += j;
/*     */     }
/*     */     
/*     */ 
/* 259 */     this.remainder = arrayOfByte1;
/* 260 */     this.remCount %= 20;
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
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 276 */     paramObjectInputStream.defaultReadObject();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 283 */       this.digest = MessageDigest.getInstance("SHA", "SUN");
/*     */     }
/*     */     catch (NoSuchProviderException|NoSuchAlgorithmException localNoSuchProviderException) {
/*     */       try {
/* 287 */         this.digest = MessageDigest.getInstance("SHA");
/*     */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 289 */         throw new InternalError("internal error: SHA-1 not available.", localNoSuchAlgorithmException);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\SecureRandom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */