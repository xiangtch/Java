/*     */ package sun.security.pkcs;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.KeyRep;
/*     */ import java.security.KeyRep.Type;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.Provider;
/*     */ import java.security.Security;
/*     */ import java.security.spec.InvalidKeySpecException;
/*     */ import java.security.spec.PKCS8EncodedKeySpec;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PKCS8Key
/*     */   implements PrivateKey
/*     */ {
/*     */   private static final long serialVersionUID = -3836890099307167124L;
/*     */   protected AlgorithmId algid;
/*     */   protected byte[] key;
/*     */   protected byte[] encodedKey;
/*  68 */   public static final BigInteger version = BigInteger.ZERO;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS8Key() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private PKCS8Key(AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte)
/*     */     throws InvalidKeyException
/*     */   {
/*  84 */     this.algid = paramAlgorithmId;
/*  85 */     this.key = paramArrayOfByte;
/*  86 */     encode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PKCS8Key parse(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/*  95 */     PrivateKey localPrivateKey = parseKey(paramDerValue);
/*  96 */     if ((localPrivateKey instanceof PKCS8Key)) {
/*  97 */       return (PKCS8Key)localPrivateKey;
/*     */     }
/*  99 */     throw new IOException("Provider did not return PKCS8Key");
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
/*     */   public static PrivateKey parseKey(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/* 121 */     if (paramDerValue.tag != 48) {
/* 122 */       throw new IOException("corrupt private key");
/*     */     }
/* 124 */     BigInteger localBigInteger = paramDerValue.data.getBigInteger();
/* 125 */     if (!version.equals(localBigInteger))
/*     */     {
/*     */ 
/*     */ 
/* 129 */       throw new IOException("version mismatch: (supported: " + Debug.toHexString(version) + ", parsed: " + Debug.toHexString(localBigInteger));
/*     */     }
/*     */     
/* 132 */     AlgorithmId localAlgorithmId = AlgorithmId.parse(paramDerValue.data.getDerValue());
/*     */     PrivateKey localPrivateKey;
/*     */     try {
/* 135 */       localPrivateKey = buildPKCS8Key(localAlgorithmId, paramDerValue.data.getOctetString());
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {
/* 138 */       throw new IOException("corrupt private key");
/*     */     }
/*     */     
/* 141 */     if (paramDerValue.data.available() != 0)
/* 142 */       throw new IOException("excess private key");
/* 143 */     return localPrivateKey;
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
/*     */   protected void parseKeyBits()
/*     */     throws IOException, InvalidKeyException
/*     */   {
/* 161 */     encode();
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
/*     */   static PrivateKey buildPKCS8Key(AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte)
/*     */     throws IOException, InvalidKeyException
/*     */   {
/* 177 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 178 */     encode(localDerOutputStream, paramAlgorithmId, paramArrayOfByte);
/*     */     
/* 180 */     PKCS8EncodedKeySpec localPKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(localDerOutputStream.toByteArray());
/*     */     
/*     */     try
/*     */     {
/* 184 */       KeyFactory localKeyFactory = KeyFactory.getInstance(paramAlgorithmId.getName());
/*     */       
/*     */ 
/* 187 */       return localKeyFactory.generatePrivate(localPKCS8EncodedKeySpec);
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}catch (InvalidKeySpecException localInvalidKeySpecException) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 197 */     String str = "";
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 203 */       Provider localProvider = Security.getProvider("SUN");
/* 204 */       if (localProvider == null)
/* 205 */         throw new InstantiationException();
/* 206 */       str = localProvider.getProperty("PrivateKey.PKCS#8." + paramAlgorithmId
/* 207 */         .getName());
/* 208 */       if (str == null) {
/* 209 */         throw new InstantiationException();
/*     */       }
/*     */       
/* 212 */       Class localClass = null;
/*     */       Object localObject2;
/* 214 */       try { localClass = Class.forName(str);
/*     */       } catch (ClassNotFoundException localClassNotFoundException2) {
/* 216 */         localObject2 = ClassLoader.getSystemClassLoader();
/* 217 */         if (localObject2 != null) {
/* 218 */           localClass = ((ClassLoader)localObject2).loadClass(str);
/*     */         }
/*     */       }
/*     */       
/* 222 */       Object localObject1 = null;
/*     */       
/*     */ 
/* 225 */       if (localClass != null)
/* 226 */         localObject1 = localClass.newInstance();
/* 227 */       if ((localObject1 instanceof PKCS8Key)) {
/* 228 */         localObject2 = (PKCS8Key)localObject1;
/* 229 */         ((PKCS8Key)localObject2).algid = paramAlgorithmId;
/* 230 */         ((PKCS8Key)localObject2).key = paramArrayOfByte;
/* 231 */         ((PKCS8Key)localObject2).parseKeyBits();
/* 232 */         return (PrivateKey)localObject2;
/*     */       }
/*     */       
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException1) {}catch (InstantiationException localInstantiationException) {}catch (IllegalAccessException localIllegalAccessException)
/*     */     {
/* 238 */       throw new IOException(str + " [internal error]");
/*     */     }
/*     */     
/* 241 */     PKCS8Key localPKCS8Key = new PKCS8Key();
/* 242 */     localPKCS8Key.algid = paramAlgorithmId;
/* 243 */     localPKCS8Key.key = paramArrayOfByte;
/* 244 */     return localPKCS8Key;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAlgorithm()
/*     */   {
/* 251 */     return this.algid.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public AlgorithmId getAlgorithmId()
/*     */   {
/* 257 */     return this.algid;
/*     */   }
/*     */   
/*     */ 
/*     */   public final void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 264 */     encode(paramDerOutputStream, this.algid, this.key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized byte[] getEncoded()
/*     */   {
/* 271 */     byte[] arrayOfByte = null;
/*     */     try {
/* 273 */       arrayOfByte = encode();
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {}
/* 276 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getFormat()
/*     */   {
/* 283 */     return "PKCS#8";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] encode()
/*     */     throws InvalidKeyException
/*     */   {
/* 292 */     if (this.encodedKey == null)
/*     */     {
/*     */       try
/*     */       {
/* 296 */         DerOutputStream localDerOutputStream = new DerOutputStream();
/* 297 */         encode(localDerOutputStream);
/* 298 */         this.encodedKey = localDerOutputStream.toByteArray();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 302 */         throw new InvalidKeyException("IOException : " + localIOException.getMessage());
/*     */       }
/*     */     }
/* 305 */     return (byte[])this.encodedKey.clone();
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
/*     */   public void decode(InputStream paramInputStream)
/*     */     throws InvalidKeyException
/*     */   {
/*     */     try
/*     */     {
/* 329 */       DerValue localDerValue = new DerValue(paramInputStream);
/* 330 */       if (localDerValue.tag != 48) {
/* 331 */         throw new InvalidKeyException("invalid key format");
/*     */       }
/*     */       
/* 334 */       BigInteger localBigInteger = localDerValue.data.getBigInteger();
/* 335 */       if (!localBigInteger.equals(version))
/*     */       {
/*     */ 
/*     */ 
/* 339 */         throw new IOException("version mismatch: (supported: " + Debug.toHexString(version) + ", parsed: " + Debug.toHexString(localBigInteger));
/*     */       }
/* 341 */       this.algid = AlgorithmId.parse(localDerValue.data.getDerValue());
/* 342 */       this.key = localDerValue.data.getOctetString();
/* 343 */       parseKeyBits();
/*     */       
/* 345 */       if (localDerValue.data.available() != 0) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 354 */       return;
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 352 */       throw new InvalidKeyException("IOException : " + localIOException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */   public void decode(byte[] paramArrayOfByte) throws InvalidKeyException {
/* 357 */     decode(new ByteArrayInputStream(paramArrayOfByte));
/*     */   }
/*     */   
/*     */   protected Object writeReplace() throws ObjectStreamException {
/* 361 */     return new KeyRep(Type.PRIVATE,
/* 362 */       getAlgorithm(), 
/* 363 */       getFormat(), 
/* 364 */       getEncoded());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 375 */       decode(paramObjectInputStream);
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {
/* 378 */       localInvalidKeyException.printStackTrace();
/*     */       
/* 380 */       throw new IOException("deserialized key is invalid: " + localInvalidKeyException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static void encode(DerOutputStream paramDerOutputStream, AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 389 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 390 */     localDerOutputStream.putInteger(version);
/* 391 */     paramAlgorithmId.encode(localDerOutputStream);
/* 392 */     localDerOutputStream.putOctetString(paramArrayOfByte);
/* 393 */     paramDerOutputStream.write((byte)48, localDerOutputStream);
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 407 */     if (this == paramObject) {
/* 408 */       return true;
/*     */     }
/*     */     
/* 411 */     if ((paramObject instanceof Key))
/*     */     {
/*     */       byte[] arrayOfByte1;
/*     */       
/* 415 */       if (this.encodedKey != null) {
/* 416 */         arrayOfByte1 = this.encodedKey;
/*     */       } else {
/* 418 */         arrayOfByte1 = getEncoded();
/*     */       }
/*     */       
/*     */ 
/* 422 */       byte[] arrayOfByte2 = ((Key)paramObject).getEncoded();
/*     */       
/*     */ 
/* 425 */       return MessageDigest.isEqual(arrayOfByte1, arrayOfByte2);
/*     */     }
/* 427 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 435 */     int i = 0;
/* 436 */     byte[] arrayOfByte = getEncoded();
/*     */     
/* 438 */     for (int j = 1; j < arrayOfByte.length; j++) {
/* 439 */       i += arrayOfByte[j] * j;
/*     */     }
/* 441 */     return i;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\pkcs\PKCS8Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */