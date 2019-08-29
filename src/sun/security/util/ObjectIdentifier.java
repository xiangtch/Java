/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ObjectIdentifier
/*     */   implements Serializable
/*     */ {
/*  60 */   private byte[] encoding = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private volatile transient String stringForm;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 8697030238860181294L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 100 */   private Object components = null;
/*     */   
/*     */ 
/*     */ 
/* 104 */   private int componentLen = -1;
/*     */   
/*     */ 
/* 107 */   private transient boolean componentsCalculated = false;
/*     */   
/*     */   private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException
/*     */   {
/* 111 */     paramObjectInputStream.defaultReadObject();
/*     */     
/* 113 */     if (this.encoding == null) {
/* 114 */       int[] arrayOfInt = (int[])this.components;
/* 115 */       if (this.componentLen > arrayOfInt.length) {
/* 116 */         this.componentLen = arrayOfInt.length;
/*     */       }
/* 118 */       init(arrayOfInt, this.componentLen);
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException
/*     */   {
/* 124 */     if (!this.componentsCalculated) {
/* 125 */       int[] arrayOfInt = toIntArray();
/* 126 */       if (arrayOfInt != null) {
/* 127 */         this.components = arrayOfInt;
/* 128 */         this.componentLen = arrayOfInt.length;
/*     */       } else {
/* 130 */         this.components = HugeOidNotSupportedByOldJDK.theOne;
/*     */       }
/* 132 */       this.componentsCalculated = true;
/*     */     }
/* 134 */     paramObjectOutputStream.defaultWriteObject();
/*     */   }
/*     */   
/*     */   static class HugeOidNotSupportedByOldJDK implements Serializable {
/*     */     private static final long serialVersionUID = 1L;
/* 139 */     static HugeOidNotSupportedByOldJDK theOne = new HugeOidNotSupportedByOldJDK();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ObjectIdentifier(String paramString)
/*     */     throws IOException
/*     */   {
/* 148 */     int i = 46;
/* 149 */     int j = 0;
/* 150 */     int k = 0;
/*     */     
/* 152 */     int m = 0;
/* 153 */     byte[] arrayOfByte = new byte[paramString.length()];
/* 154 */     int n = 0;
/* 155 */     int i1 = 0;
/*     */     try
/*     */     {
/* 158 */       String str = null;
/*     */       do {
/* 160 */         int i2 = 0;
/* 161 */         k = paramString.indexOf(i, j);
/* 162 */         if (k == -1) {
/* 163 */           str = paramString.substring(j);
/* 164 */           i2 = paramString.length() - j;
/*     */         } else {
/* 166 */           str = paramString.substring(j, k);
/* 167 */           i2 = k - j;
/*     */         }
/*     */         
/* 170 */         if (i2 > 9) {
/* 171 */           BigInteger localBigInteger = new BigInteger(str);
/* 172 */           if (i1 == 0) {
/* 173 */             checkFirstComponent(localBigInteger);
/* 174 */             n = localBigInteger.intValue();
/*     */           } else {
/* 176 */             if (i1 == 1) {
/* 177 */               checkSecondComponent(n, localBigInteger);
/* 178 */               localBigInteger = localBigInteger.add(BigInteger.valueOf(40 * n));
/*     */             } else {
/* 180 */               checkOtherComponent(i1, localBigInteger);
/*     */             }
/* 182 */             m += pack7Oid(localBigInteger, arrayOfByte, m);
/*     */           }
/*     */         } else {
/* 185 */           int i3 = Integer.parseInt(str);
/* 186 */           if (i1 == 0) {
/* 187 */             checkFirstComponent(i3);
/* 188 */             n = i3;
/*     */           } else {
/* 190 */             if (i1 == 1) {
/* 191 */               checkSecondComponent(n, i3);
/* 192 */               i3 += 40 * n;
/*     */             } else {
/* 194 */               checkOtherComponent(i1, i3);
/*     */             }
/* 196 */             m += pack7Oid(i3, arrayOfByte, m);
/*     */           }
/*     */         }
/* 199 */         j = k + 1;
/* 200 */         i1++;
/* 201 */       } while (k != -1);
/*     */       
/* 203 */       checkCount(i1);
/* 204 */       this.encoding = new byte[m];
/* 205 */       System.arraycopy(arrayOfByte, 0, this.encoding, 0, m);
/* 206 */       this.stringForm = paramString;
/*     */     } catch (IOException localIOException) {
/* 208 */       throw localIOException;
/*     */     }
/*     */     catch (Exception localException) {
/* 211 */       throw new IOException("ObjectIdentifier() -- Invalid format: " + localException.toString(), localException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ObjectIdentifier(int[] paramArrayOfInt)
/*     */     throws IOException
/*     */   {
/* 221 */     checkCount(paramArrayOfInt.length);
/* 222 */     checkFirstComponent(paramArrayOfInt[0]);
/* 223 */     checkSecondComponent(paramArrayOfInt[0], paramArrayOfInt[1]);
/* 224 */     for (int i = 2; i < paramArrayOfInt.length; i++)
/* 225 */       checkOtherComponent(i, paramArrayOfInt[i]);
/* 226 */     init(paramArrayOfInt, paramArrayOfInt.length);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ObjectIdentifier(DerInputStream paramDerInputStream)
/*     */     throws IOException
/*     */   {
/* 255 */     int i = (byte)paramDerInputStream.getByte();
/* 256 */     if (i != 6) {
/* 257 */       throw new IOException("ObjectIdentifier() -- data isn't an object ID (tag = " + i + ")");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 262 */     int j = paramDerInputStream.getLength();
/* 263 */     if (j > paramDerInputStream.available())
/*     */     {
/*     */ 
/* 266 */       throw new IOException("ObjectIdentifier() -- length exceedsdata available.  Length: " + j + ", Available: " + paramDerInputStream.available());
/*     */     }
/* 268 */     this.encoding = new byte[j];
/* 269 */     paramDerInputStream.getBytes(this.encoding);
/* 270 */     check(this.encoding);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   ObjectIdentifier(DerInputBuffer paramDerInputBuffer)
/*     */     throws IOException
/*     */   {
/* 280 */     DerInputStream localDerInputStream = new DerInputStream(paramDerInputBuffer);
/* 281 */     this.encoding = new byte[localDerInputStream.available()];
/* 282 */     localDerInputStream.getBytes(this.encoding);
/* 283 */     check(this.encoding);
/*     */   }
/*     */   
/*     */   private void init(int[] paramArrayOfInt, int paramInt) {
/* 287 */     int i = 0;
/* 288 */     byte[] arrayOfByte = new byte[paramInt * 5 + 1];
/*     */     
/* 290 */     if (paramArrayOfInt[1] < Integer.MAX_VALUE - paramArrayOfInt[0] * 40) {
/* 291 */       i += pack7Oid(paramArrayOfInt[0] * 40 + paramArrayOfInt[1], arrayOfByte, i);
/*     */     } else {
/* 293 */       BigInteger localBigInteger = BigInteger.valueOf(paramArrayOfInt[1]);
/* 294 */       localBigInteger = localBigInteger.add(BigInteger.valueOf(paramArrayOfInt[0] * 40));
/* 295 */       i += pack7Oid(localBigInteger, arrayOfByte, i);
/*     */     }
/*     */     
/* 298 */     for (int j = 2; j < paramInt; j++) {
/* 299 */       i += pack7Oid(paramArrayOfInt[j], arrayOfByte, i);
/*     */     }
/* 301 */     this.encoding = new byte[i];
/* 302 */     System.arraycopy(arrayOfByte, 0, this.encoding, 0, i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ObjectIdentifier newInternal(int[] paramArrayOfInt)
/*     */   {
/*     */     try
/*     */     {
/* 316 */       return new ObjectIdentifier(paramArrayOfInt);
/*     */     } catch (IOException localIOException) {
/* 318 */       throw new RuntimeException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 328 */     paramDerOutputStream.write((byte)6, this.encoding);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public boolean equals(ObjectIdentifier paramObjectIdentifier)
/*     */   {
/* 336 */     return equals(paramObjectIdentifier);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 346 */     if (this == paramObject) {
/* 347 */       return true;
/*     */     }
/* 349 */     if (!(paramObject instanceof ObjectIdentifier)) {
/* 350 */       return false;
/*     */     }
/* 352 */     ObjectIdentifier localObjectIdentifier = (ObjectIdentifier)paramObject;
/* 353 */     return Arrays.equals(this.encoding, localObjectIdentifier.encoding);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 358 */     return Arrays.hashCode(this.encoding);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int[] toIntArray()
/*     */   {
/* 368 */     int i = this.encoding.length;
/* 369 */     int[] arrayOfInt = new int[20];
/* 370 */     int j = 0;
/* 371 */     BigInteger localBigInteger1 = 0;
/* 372 */     for (BigInteger localBigInteger2 = 0; localBigInteger2 < i; localBigInteger2++) {
/* 373 */       if ((this.encoding[localBigInteger2] & 0x80) == 0) {
/*     */         BigInteger localBigInteger4;
/* 375 */         if (localBigInteger2 - localBigInteger1 + 1 > 4) {
/* 376 */           BigInteger localBigInteger3 = new BigInteger(pack(this.encoding, localBigInteger1, localBigInteger2 - localBigInteger1 + 1, 7, 8));
/* 377 */           if (localBigInteger1 == 0) {
/* 378 */             arrayOfInt[(j++)] = 2;
/* 379 */             localBigInteger4 = localBigInteger3.subtract(BigInteger.valueOf(80L));
/* 380 */             if (localBigInteger4.compareTo(BigInteger.valueOf(2147483647L)) == 1) {
/* 381 */               return null;
/*     */             }
/* 383 */             arrayOfInt[(j++)] = localBigInteger4.intValue();
/*     */           }
/*     */           else {
/* 386 */             if (localBigInteger3.compareTo(BigInteger.valueOf(2147483647L)) == 1) {
/* 387 */               return null;
/*     */             }
/* 389 */             arrayOfInt[(j++)] = localBigInteger3.intValue();
/*     */           }
/*     */         }
/*     */         else {
/* 393 */           int k = 0;
/* 394 */           for (localBigInteger4 = localBigInteger1; localBigInteger4 <= localBigInteger2; localBigInteger4++) {
/* 395 */             k <<= 7;
/* 396 */             int m = this.encoding[localBigInteger4];
/* 397 */             k |= m & 0x7F;
/*     */           }
/* 399 */           if (localBigInteger1 == 0) {
/* 400 */             if (k < 80) {
/* 401 */               arrayOfInt[(j++)] = (k / 40);
/* 402 */               arrayOfInt[(j++)] = (k % 40);
/*     */             } else {
/* 404 */               arrayOfInt[(j++)] = 2;
/* 405 */               arrayOfInt[(j++)] = (k - 80);
/*     */             }
/*     */           } else {
/* 408 */             arrayOfInt[(j++)] = k;
/*     */           }
/*     */         }
/* 411 */         localBigInteger1 = localBigInteger2 + 1;
/*     */       }
/* 413 */       if (j >= arrayOfInt.length) {
/* 414 */         arrayOfInt = Arrays.copyOf(arrayOfInt, j + 10);
/*     */       }
/*     */     }
/* 417 */     return Arrays.copyOf(arrayOfInt, j);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 428 */     String str = this.stringForm;
/* 429 */     if (str == null) {
/* 430 */       int i = this.encoding.length;
/* 431 */       StringBuffer localStringBuffer = new StringBuffer(i * 4);
/*     */       
/* 433 */       int j = 0;
/* 434 */       for (int k = 0; k < i; k++) {
/* 435 */         if ((this.encoding[k] & 0x80) == 0)
/*     */         {
/* 437 */           if (j != 0) {
/* 438 */             localStringBuffer.append('.');
/*     */           }
/* 440 */           if (k - j + 1 > 4) {
/* 441 */             BigInteger localBigInteger = new BigInteger(pack(this.encoding, j, k - j + 1, 7, 8));
/* 442 */             if (j == 0)
/*     */             {
/*     */ 
/* 445 */               localStringBuffer.append("2.");
/* 446 */               localStringBuffer.append(localBigInteger.subtract(BigInteger.valueOf(80L)));
/*     */             } else {
/* 448 */               localStringBuffer.append(localBigInteger);
/*     */             }
/*     */           } else {
/* 451 */             int m = 0;
/* 452 */             for (int n = j; n <= k; n++) {
/* 453 */               m <<= 7;
/* 454 */               int i1 = this.encoding[n];
/* 455 */               m |= i1 & 0x7F;
/*     */             }
/* 457 */             if (j == 0) {
/* 458 */               if (m < 80) {
/* 459 */                 localStringBuffer.append(m / 40);
/* 460 */                 localStringBuffer.append('.');
/* 461 */                 localStringBuffer.append(m % 40);
/*     */               } else {
/* 463 */                 localStringBuffer.append("2.");
/* 464 */                 localStringBuffer.append(m - 80);
/*     */               }
/*     */             } else {
/* 467 */               localStringBuffer.append(m);
/*     */             }
/*     */           }
/* 470 */           j = k + 1;
/*     */         }
/*     */       }
/* 473 */       str = localStringBuffer.toString();
/* 474 */       this.stringForm = str;
/*     */     }
/* 476 */     return str;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static byte[] pack(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 505 */     assert ((paramInt3 > 0) && (paramInt3 <= 8)) : "input NUB must be between 1 and 8";
/* 506 */     assert ((paramInt4 > 0) && (paramInt4 <= 8)) : "output NUB must be between 1 and 8";
/*     */     
/* 508 */     if (paramInt3 == paramInt4) {
/* 509 */       return (byte[])paramArrayOfByte.clone();
/*     */     }
/*     */     
/* 512 */     int i = paramInt2 * paramInt3;
/* 513 */     byte[] arrayOfByte = new byte[(i + paramInt4 - 1) / paramInt4];
/*     */     
/*     */ 
/* 516 */     int j = 0;
/*     */     
/*     */ 
/* 519 */     int k = (i + paramInt4 - 1) / paramInt4 * paramInt4 - i;
/*     */     
/* 521 */     while (j < i) {
/* 522 */       int m = paramInt3 - j % paramInt3;
/* 523 */       if (m > paramInt4 - k % paramInt4) {
/* 524 */         m = paramInt4 - k % paramInt4;
/*     */       }
/*     */       
/* 527 */       int tmp153_152 = (k / paramInt4); byte[] tmp153_146 = arrayOfByte;tmp153_146[tmp153_152] = ((byte)(tmp153_146[tmp153_152] | (paramArrayOfByte[(paramInt1 + j / paramInt3)] + 256 >> paramInt3 - j % paramInt3 - m & (1 << m) - 1) << paramInt4 - k % paramInt4 - m));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 532 */       j += m;
/* 533 */       k += m;
/*     */     }
/* 535 */     return arrayOfByte;
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
/*     */   private static int pack7Oid(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
/*     */   {
/* 548 */     byte[] arrayOfByte = pack(paramArrayOfByte1, paramInt1, paramInt2, 8, 7);
/* 549 */     int i = arrayOfByte.length - 1;
/* 550 */     for (int j = arrayOfByte.length - 2; j >= 0; j--) {
/* 551 */       if (arrayOfByte[j] != 0) {
/* 552 */         i = j;
/*     */       }
/* 554 */       int tmp47_45 = j; byte[] tmp47_43 = arrayOfByte;tmp47_43[tmp47_45] = ((byte)(tmp47_43[tmp47_45] | 0x80));
/*     */     }
/* 556 */     System.arraycopy(arrayOfByte, i, paramArrayOfByte2, paramInt3, arrayOfByte.length - i);
/* 557 */     return arrayOfByte.length - i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int pack8(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
/*     */   {
/* 568 */     byte[] arrayOfByte = pack(paramArrayOfByte1, paramInt1, paramInt2, 7, 8);
/* 569 */     int i = arrayOfByte.length - 1;
/* 570 */     for (int j = arrayOfByte.length - 2; j >= 0; j--) {
/* 571 */       if (arrayOfByte[j] != 0) {
/* 572 */         i = j;
/*     */       }
/*     */     }
/* 575 */     System.arraycopy(arrayOfByte, i, paramArrayOfByte2, paramInt3, arrayOfByte.length - i);
/* 576 */     return arrayOfByte.length - i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static int pack7Oid(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*     */   {
/* 583 */     byte[] arrayOfByte = new byte[4];
/* 584 */     arrayOfByte[0] = ((byte)(paramInt1 >> 24));
/* 585 */     arrayOfByte[1] = ((byte)(paramInt1 >> 16));
/* 586 */     arrayOfByte[2] = ((byte)(paramInt1 >> 8));
/* 587 */     arrayOfByte[3] = ((byte)paramInt1);
/* 588 */     return pack7Oid(arrayOfByte, 0, 4, paramArrayOfByte, paramInt2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static int pack7Oid(BigInteger paramBigInteger, byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 595 */     byte[] arrayOfByte = paramBigInteger.toByteArray();
/* 596 */     return pack7Oid(arrayOfByte, 0, arrayOfByte.length, paramArrayOfByte, paramInt);
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
/*     */   private static void check(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 612 */     int i = paramArrayOfByte.length;
/* 613 */     if ((i < 1) || ((paramArrayOfByte[(i - 1)] & 0x80) != 0))
/*     */     {
/* 615 */       throw new IOException("ObjectIdentifier() -- Invalid DER encoding, not ended");
/*     */     }
/*     */     
/* 618 */     for (int j = 0; j < i; j++)
/*     */     {
/* 620 */       if ((paramArrayOfByte[j] == Byte.MIN_VALUE) && ((j == 0) || ((paramArrayOfByte[(j - 1)] & 0x80) == 0)))
/*     */       {
/* 622 */         throw new IOException("ObjectIdentifier() -- Invalid DER encoding, useless extra octet detected");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkCount(int paramInt) throws IOException {
/* 628 */     if (paramInt < 2) {
/* 629 */       throw new IOException("ObjectIdentifier() -- Must be at least two oid components ");
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkFirstComponent(int paramInt) throws IOException {
/* 634 */     if ((paramInt < 0) || (paramInt > 2)) {
/* 635 */       throw new IOException("ObjectIdentifier() -- First oid component is invalid ");
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkFirstComponent(BigInteger paramBigInteger) throws IOException {
/* 640 */     if ((paramBigInteger.signum() == -1) || 
/* 641 */       (paramBigInteger.compareTo(BigInteger.valueOf(2L)) == 1)) {
/* 642 */       throw new IOException("ObjectIdentifier() -- First oid component is invalid ");
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkSecondComponent(int paramInt1, int paramInt2) throws IOException {
/* 647 */     if ((paramInt2 < 0) || ((paramInt1 != 2) && (paramInt2 > 39))) {
/* 648 */       throw new IOException("ObjectIdentifier() -- Second oid component is invalid ");
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkSecondComponent(int paramInt, BigInteger paramBigInteger) throws IOException {
/* 653 */     if (paramBigInteger.signum() != -1) { if (paramInt != 2)
/*     */       {
/* 655 */         if (paramBigInteger.compareTo(BigInteger.valueOf(39L)) != 1) {} }
/* 656 */     } else throw new IOException("ObjectIdentifier() -- Second oid component is invalid ");
/*     */   }
/*     */   
/*     */   private static void checkOtherComponent(int paramInt1, int paramInt2) throws IOException
/*     */   {
/* 661 */     if (paramInt2 < 0) {
/* 662 */       throw new IOException("ObjectIdentifier() -- oid component #" + (paramInt1 + 1) + " must be non-negative ");
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkOtherComponent(int paramInt, BigInteger paramBigInteger) throws IOException {
/* 667 */     if (paramBigInteger.signum() == -1) {
/* 668 */       throw new IOException("ObjectIdentifier() -- oid component #" + (paramInt + 1) + " must be non-negative ");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\ObjectIdentifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */