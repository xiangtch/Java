/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.util.Arrays;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.BitArray;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IPAddressName
/*     */   implements GeneralNameInterface
/*     */ {
/*     */   private byte[] address;
/*     */   private boolean isIPv4;
/*     */   private String name;
/*     */   private static final int MASKSIZE = 16;
/*     */   
/*     */   public IPAddressName(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/*  82 */     this(paramDerValue.getOctetString());
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
/*     */   public IPAddressName(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/*  97 */     if ((paramArrayOfByte.length == 4) || (paramArrayOfByte.length == 8)) {
/*  98 */       this.isIPv4 = true;
/*  99 */     } else if ((paramArrayOfByte.length == 16) || (paramArrayOfByte.length == 32)) {
/* 100 */       this.isIPv4 = false;
/*     */     } else {
/* 102 */       throw new IOException("Invalid IPAddressName");
/*     */     }
/* 104 */     this.address = paramArrayOfByte;
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
/*     */   public IPAddressName(String paramString)
/*     */     throws IOException
/*     */   {
/* 128 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 129 */       throw new IOException("IPAddress cannot be null or empty");
/*     */     }
/* 131 */     if (paramString.charAt(paramString.length() - 1) == '/') {
/* 132 */       throw new IOException("Invalid IPAddress: " + paramString);
/*     */     }
/*     */     
/* 135 */     if (paramString.indexOf(':') >= 0)
/*     */     {
/*     */ 
/*     */ 
/* 139 */       parseIPv6(paramString);
/* 140 */       this.isIPv4 = false;
/* 141 */     } else if (paramString.indexOf('.') >= 0)
/*     */     {
/* 143 */       parseIPv4(paramString);
/* 144 */       this.isIPv4 = true;
/*     */     } else {
/* 146 */       throw new IOException("Invalid IPAddress: " + paramString);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void parseIPv4(String paramString)
/*     */     throws IOException
/*     */   {
/* 159 */     int i = paramString.indexOf('/');
/* 160 */     if (i == -1) {
/* 161 */       this.address = InetAddress.getByName(paramString).getAddress();
/*     */     } else {
/* 163 */       this.address = new byte[8];
/*     */       
/*     */ 
/*     */ 
/* 167 */       byte[] arrayOfByte1 = InetAddress.getByName(paramString.substring(i + 1)).getAddress();
/*     */       
/*     */ 
/*     */ 
/* 171 */       byte[] arrayOfByte2 = InetAddress.getByName(paramString.substring(0, i)).getAddress();
/*     */       
/* 173 */       System.arraycopy(arrayOfByte2, 0, this.address, 0, 4);
/* 174 */       System.arraycopy(arrayOfByte1, 0, this.address, 4, 4);
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
/*     */   private void parseIPv6(String paramString)
/*     */     throws IOException
/*     */   {
/* 189 */     int i = paramString.indexOf('/');
/* 190 */     if (i == -1) {
/* 191 */       this.address = InetAddress.getByName(paramString).getAddress();
/*     */     } else {
/* 193 */       this.address = new byte[32];
/*     */       
/* 195 */       byte[] arrayOfByte1 = InetAddress.getByName(paramString.substring(0, i)).getAddress();
/* 196 */       System.arraycopy(arrayOfByte1, 0, this.address, 0, 16);
/*     */       
/*     */ 
/* 199 */       int j = Integer.parseInt(paramString.substring(i + 1));
/* 200 */       if ((j < 0) || (j > 128)) {
/* 201 */         throw new IOException("IPv6Address prefix length (" + j + ") in out of valid range [0,128]");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 206 */       BitArray localBitArray = new BitArray(128);
/*     */       
/*     */ 
/* 209 */       for (int k = 0; k < j; k++)
/* 210 */         localBitArray.set(k, true);
/* 211 */       byte[] arrayOfByte2 = localBitArray.toByteArray();
/*     */       
/*     */ 
/* 214 */       for (int m = 0; m < 16; m++) {
/* 215 */         this.address[(16 + m)] = arrayOfByte2[m];
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/* 223 */     return 7;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 233 */     paramDerOutputStream.putOctetString(this.address);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 241 */       return "IPAddress: " + getName();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 244 */       HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/* 245 */       return "IPAddress: " + localHexDumpEncoder.encodeBuffer(this.address);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */     throws IOException
/*     */   {
/* 257 */     if (this.name != null)
/* 258 */       return this.name;
/*     */     byte[] arrayOfByte1;
/* 260 */     byte[] arrayOfByte2; if (this.isIPv4)
/*     */     {
/* 262 */       arrayOfByte1 = new byte[4];
/* 263 */       System.arraycopy(this.address, 0, arrayOfByte1, 0, 4);
/* 264 */       this.name = InetAddress.getByAddress(arrayOfByte1).getHostAddress();
/* 265 */       if (this.address.length == 8) {
/* 266 */         arrayOfByte2 = new byte[4];
/* 267 */         System.arraycopy(this.address, 4, arrayOfByte2, 0, 4);
/*     */         
/* 269 */         this.name = (this.name + "/" + InetAddress.getByAddress(arrayOfByte2).getHostAddress());
/*     */       }
/*     */     }
/*     */     else {
/* 273 */       arrayOfByte1 = new byte[16];
/* 274 */       System.arraycopy(this.address, 0, arrayOfByte1, 0, 16);
/* 275 */       this.name = InetAddress.getByAddress(arrayOfByte1).getHostAddress();
/* 276 */       if (this.address.length == 32)
/*     */       {
/*     */ 
/*     */ 
/* 280 */         arrayOfByte2 = new byte[16];
/* 281 */         for (int i = 16; i < 32; i++)
/* 282 */           arrayOfByte2[(i - 16)] = this.address[i];
/* 283 */         BitArray localBitArray = new BitArray(128, arrayOfByte2);
/*     */         
/* 285 */         for (int j = 0; 
/* 286 */             j < 128; j++) {
/* 287 */           if (!localBitArray.get(j))
/*     */             break;
/*     */         }
/* 290 */         this.name = (this.name + "/" + j);
/* 292 */         for (; 
/* 292 */             j < 128; j++) {
/* 293 */           if (localBitArray.get(j)) {
/* 294 */             throw new IOException("Invalid IPv6 subdomain - set bit " + j + " not contiguous");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 300 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getBytes()
/*     */   {
/* 307 */     return (byte[])this.address.clone();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 316 */     if (this == paramObject) {
/* 317 */       return true;
/*     */     }
/* 319 */     if (!(paramObject instanceof IPAddressName)) {
/* 320 */       return false;
/*     */     }
/* 322 */     IPAddressName localIPAddressName = (IPAddressName)paramObject;
/* 323 */     byte[] arrayOfByte = localIPAddressName.address;
/*     */     
/* 325 */     if (arrayOfByte.length != this.address.length) {
/* 326 */       return false;
/*     */     }
/* 328 */     if ((this.address.length == 8) || (this.address.length == 32))
/*     */     {
/*     */ 
/* 331 */       int i = this.address.length / 2;
/* 332 */       for (int j = 0; j < i; j++) {
/* 333 */         int k = (byte)(this.address[j] & this.address[(j + i)]);
/* 334 */         int m = (byte)(arrayOfByte[j] & arrayOfByte[(j + i)]);
/* 335 */         if (k != m) {
/* 336 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 340 */       for (j = i; j < this.address.length; j++)
/* 341 */         if (this.address[j] != arrayOfByte[j])
/* 342 */           return false;
/* 343 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 347 */     return Arrays.equals(arrayOfByte, this.address);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 357 */     int i = 0;
/*     */     
/* 359 */     for (int j = 0; j < this.address.length; j++) {
/* 360 */       i += this.address[j] * j;
/*     */     }
/* 362 */     return i;
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
/*     */   public int constrains(GeneralNameInterface paramGeneralNameInterface)
/*     */     throws UnsupportedOperationException
/*     */   {
/*     */     int i;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 397 */     if (paramGeneralNameInterface == null) {
/* 398 */       i = -1;
/* 399 */     } else if (paramGeneralNameInterface.getType() != 7) {
/* 400 */       i = -1;
/* 401 */     } else if (((IPAddressName)paramGeneralNameInterface).equals(this)) {
/* 402 */       i = 0;
/*     */     } else {
/* 404 */       IPAddressName localIPAddressName = (IPAddressName)paramGeneralNameInterface;
/* 405 */       byte[] arrayOfByte = localIPAddressName.address;
/* 406 */       if ((arrayOfByte.length == 4) && (this.address.length == 4))
/*     */       {
/* 408 */         i = 3; } else { int j;
/* 409 */         int k; if (((arrayOfByte.length == 8) && (this.address.length == 8)) || ((arrayOfByte.length == 32) && (this.address.length == 32)))
/*     */         {
/*     */ 
/*     */ 
/* 413 */           j = 1;
/* 414 */           k = 1;
/* 415 */           int m = 0;
/* 416 */           int n = 0;
/* 417 */           int i1 = this.address.length / 2;
/* 418 */           for (int i2 = 0; i2 < i1; i2++) {
/* 419 */             if ((byte)(this.address[i2] & this.address[(i2 + i1)]) != this.address[i2])
/* 420 */               m = 1;
/* 421 */             if ((byte)(arrayOfByte[i2] & arrayOfByte[(i2 + i1)]) != arrayOfByte[i2])
/* 422 */               n = 1;
/* 423 */             if (((byte)(this.address[(i2 + i1)] & arrayOfByte[(i2 + i1)]) != this.address[(i2 + i1)]) || ((byte)(this.address[i2] & this.address[(i2 + i1)]) != (byte)(arrayOfByte[i2] & this.address[(i2 + i1)])))
/*     */             {
/* 425 */               j = 0;
/*     */             }
/* 427 */             if (((byte)(arrayOfByte[(i2 + i1)] & this.address[(i2 + i1)]) != arrayOfByte[(i2 + i1)]) || ((byte)(arrayOfByte[i2] & arrayOfByte[(i2 + i1)]) != (byte)(this.address[i2] & arrayOfByte[(i2 + i1)])))
/*     */             {
/* 429 */               k = 0;
/*     */             }
/*     */           }
/* 432 */           if ((m != 0) || (n != 0)) {
/* 433 */             if ((m != 0) && (n != 0)) {
/* 434 */               i = 0;
/* 435 */             } else if (m != 0) {
/* 436 */               i = 2;
/*     */             } else
/* 438 */               i = 1;
/* 439 */           } else if (j != 0) {
/* 440 */             i = 1;
/* 441 */           } else if (k != 0) {
/* 442 */             i = 2;
/*     */           } else
/* 444 */             i = 3;
/* 445 */         } else if ((arrayOfByte.length == 8) || (arrayOfByte.length == 32))
/*     */         {
/* 447 */           j = 0;
/* 448 */           k = arrayOfByte.length / 2;
/* 449 */           for (; j < k; j++)
/*     */           {
/*     */ 
/* 452 */             if ((this.address[j] & arrayOfByte[(j + k)]) != arrayOfByte[j])
/*     */               break;
/*     */           }
/* 455 */           if (j == k) {
/* 456 */             i = 2;
/*     */           } else
/* 458 */             i = 3;
/* 459 */         } else if ((this.address.length == 8) || (this.address.length == 32))
/*     */         {
/* 461 */           j = 0;
/* 462 */           k = this.address.length / 2;
/* 463 */           for (; j < k; j++)
/*     */           {
/* 465 */             if ((arrayOfByte[j] & this.address[(j + k)]) != this.address[j])
/*     */               break;
/*     */           }
/* 468 */           if (j == k) {
/* 469 */             i = 1;
/*     */           } else
/* 471 */             i = 3;
/*     */         } else {
/* 473 */           i = 3;
/*     */         }
/*     */       } }
/* 476 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int subtreeDepth()
/*     */     throws UnsupportedOperationException
/*     */   {
/* 488 */     throw new UnsupportedOperationException("subtreeDepth() not defined for IPAddressName");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\IPAddressName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */