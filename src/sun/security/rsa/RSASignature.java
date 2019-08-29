/*     */ package sun.security.rsa;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.InvalidParameterException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.ProviderException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.SignatureException;
/*     */ import java.security.SignatureSpi;
/*     */ import java.security.interfaces.RSAKey;
/*     */ import java.security.interfaces.RSAPrivateKey;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import javax.crypto.BadPaddingException;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
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
/*     */ 
/*     */ 
/*     */ public abstract class RSASignature
/*     */   extends SignatureSpi
/*     */ {
/*     */   private static final int baseLength = 8;
/*     */   private final ObjectIdentifier digestOID;
/*     */   private final int encodedLength;
/*     */   private final MessageDigest md;
/*     */   private boolean digestReset;
/*     */   private RSAPrivateKey privateKey;
/*     */   private RSAPublicKey publicKey;
/*     */   private RSAPadding padding;
/*     */   
/*     */   RSASignature(String paramString, ObjectIdentifier paramObjectIdentifier, int paramInt)
/*     */   {
/*  77 */     this.digestOID = paramObjectIdentifier;
/*     */     try {
/*  79 */       this.md = MessageDigest.getInstance(paramString);
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/*  81 */       throw new ProviderException(localNoSuchAlgorithmException);
/*     */     }
/*  83 */     this.digestReset = true;
/*  84 */     this.encodedLength = (8 + paramInt + this.md.getDigestLength());
/*     */   }
/*     */   
/*     */   protected void engineInitVerify(PublicKey paramPublicKey)
/*     */     throws InvalidKeyException
/*     */   {
/*  90 */     RSAPublicKey localRSAPublicKey = (RSAPublicKey)RSAKeyFactory.toRSAKey(paramPublicKey);
/*  91 */     this.privateKey = null;
/*  92 */     this.publicKey = localRSAPublicKey;
/*  93 */     initCommon(localRSAPublicKey, null);
/*     */   }
/*     */   
/*     */   protected void engineInitSign(PrivateKey paramPrivateKey)
/*     */     throws InvalidKeyException
/*     */   {
/*  99 */     engineInitSign(paramPrivateKey, null);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom)
/*     */     throws InvalidKeyException
/*     */   {
/* 106 */     RSAPrivateKey localRSAPrivateKey = (RSAPrivateKey)RSAKeyFactory.toRSAKey(paramPrivateKey);
/* 107 */     this.privateKey = localRSAPrivateKey;
/* 108 */     this.publicKey = null;
/* 109 */     initCommon(localRSAPrivateKey, paramSecureRandom);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void initCommon(RSAKey paramRSAKey, SecureRandom paramSecureRandom)
/*     */     throws InvalidKeyException
/*     */   {
/* 117 */     resetDigest();
/* 118 */     int i = RSACore.getByteLength(paramRSAKey);
/*     */     try
/*     */     {
/* 121 */       this.padding = RSAPadding.getInstance(1, i, paramSecureRandom);
/*     */     } catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException) {
/* 123 */       throw new InvalidKeyException(localInvalidAlgorithmParameterException.getMessage());
/*     */     }
/* 125 */     int j = this.padding.getMaxDataSize();
/* 126 */     if (this.encodedLength > j) {
/* 127 */       throw new InvalidKeyException("Key is too short for this signature algorithm");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void resetDigest()
/*     */   {
/* 136 */     if (!this.digestReset) {
/* 137 */       this.md.reset();
/* 138 */       this.digestReset = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private byte[] getDigestValue()
/*     */   {
/* 146 */     this.digestReset = true;
/* 147 */     return this.md.digest();
/*     */   }
/*     */   
/*     */   protected void engineUpdate(byte paramByte) throws SignatureException
/*     */   {
/* 152 */     this.md.update(paramByte);
/* 153 */     this.digestReset = false;
/*     */   }
/*     */   
/*     */   protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws SignatureException
/*     */   {
/* 159 */     this.md.update(paramArrayOfByte, paramInt1, paramInt2);
/* 160 */     this.digestReset = false;
/*     */   }
/*     */   
/*     */   protected void engineUpdate(ByteBuffer paramByteBuffer)
/*     */   {
/* 165 */     this.md.update(paramByteBuffer);
/* 166 */     this.digestReset = false;
/*     */   }
/*     */   
/*     */   protected byte[] engineSign() throws SignatureException
/*     */   {
/* 171 */     byte[] arrayOfByte1 = getDigestValue();
/*     */     try {
/* 173 */       byte[] arrayOfByte2 = encodeSignature(this.digestOID, arrayOfByte1);
/* 174 */       byte[] arrayOfByte3 = this.padding.pad(arrayOfByte2);
/* 175 */       return RSACore.rsa(arrayOfByte3, this.privateKey, true);
/*     */     }
/*     */     catch (GeneralSecurityException localGeneralSecurityException) {
/* 178 */       throw new SignatureException("Could not sign data", localGeneralSecurityException);
/*     */     } catch (IOException localIOException) {
/* 180 */       throw new SignatureException("Could not encode data", localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean engineVerify(byte[] paramArrayOfByte) throws SignatureException
/*     */   {
/* 186 */     if (paramArrayOfByte.length != RSACore.getByteLength(this.publicKey))
/*     */     {
/*     */ 
/* 189 */       throw new SignatureException("Signature length not correct: got " + paramArrayOfByte.length + " but was expecting " + RSACore.getByteLength(this.publicKey));
/*     */     }
/* 191 */     byte[] arrayOfByte1 = getDigestValue();
/*     */     try {
/* 193 */       byte[] arrayOfByte2 = RSACore.rsa(paramArrayOfByte, this.publicKey);
/* 194 */       byte[] arrayOfByte3 = this.padding.unpad(arrayOfByte2);
/* 195 */       byte[] arrayOfByte4 = decodeSignature(this.digestOID, arrayOfByte3);
/* 196 */       return MessageDigest.isEqual(arrayOfByte1, arrayOfByte4);
/*     */ 
/*     */     }
/*     */     catch (BadPaddingException localBadPaddingException)
/*     */     {
/*     */ 
/* 202 */       return false;
/*     */     } catch (IOException localIOException) {
/* 204 */       throw new SignatureException("Signature encoding error", localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] encodeSignature(ObjectIdentifier paramObjectIdentifier, byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 214 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 215 */     new AlgorithmId(paramObjectIdentifier).encode(localDerOutputStream);
/* 216 */     localDerOutputStream.putOctetString(paramArrayOfByte);
/*     */     
/* 218 */     DerValue localDerValue = new DerValue((byte)48, localDerOutputStream.toByteArray());
/* 219 */     return localDerValue.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] decodeSignature(ObjectIdentifier paramObjectIdentifier, byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 229 */     DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte, 0, paramArrayOfByte.length, false);
/* 230 */     DerValue[] arrayOfDerValue = localDerInputStream.getSequence(2);
/* 231 */     if ((arrayOfDerValue.length != 2) || (localDerInputStream.available() != 0)) {
/* 232 */       throw new IOException("SEQUENCE length error");
/*     */     }
/* 234 */     AlgorithmId localAlgorithmId = AlgorithmId.parse(arrayOfDerValue[0]);
/* 235 */     if (!localAlgorithmId.getOID().equals(paramObjectIdentifier))
/*     */     {
/* 237 */       throw new IOException("ObjectIdentifier mismatch: " + localAlgorithmId.getOID());
/*     */     }
/* 239 */     if (localAlgorithmId.getEncodedParams() != null) {
/* 240 */       throw new IOException("Unexpected AlgorithmId parameters");
/*     */     }
/* 242 */     byte[] arrayOfByte = arrayOfDerValue[1].getOctetString();
/* 243 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected void engineSetParameter(String paramString, Object paramObject)
/*     */     throws InvalidParameterException
/*     */   {
/* 250 */     throw new UnsupportedOperationException("setParameter() not supported");
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected Object engineGetParameter(String paramString)
/*     */     throws InvalidParameterException
/*     */   {
/* 257 */     throw new UnsupportedOperationException("getParameter() not supported");
/*     */   }
/*     */   
/*     */   public static final class MD2withRSA extends RSASignature
/*     */   {
/*     */     public MD2withRSA() {
/* 263 */       super(AlgorithmId.MD2_oid, 10);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class MD5withRSA extends RSASignature
/*     */   {
/*     */     public MD5withRSA() {
/* 270 */       super(AlgorithmId.MD5_oid, 10);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class SHA1withRSA extends RSASignature
/*     */   {
/*     */     public SHA1withRSA() {
/* 277 */       super(AlgorithmId.SHA_oid, 7);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class SHA224withRSA extends RSASignature
/*     */   {
/*     */     public SHA224withRSA() {
/* 284 */       super(AlgorithmId.SHA224_oid, 11);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class SHA256withRSA extends RSASignature
/*     */   {
/*     */     public SHA256withRSA() {
/* 291 */       super(AlgorithmId.SHA256_oid, 11);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class SHA384withRSA extends RSASignature
/*     */   {
/*     */     public SHA384withRSA() {
/* 298 */       super(AlgorithmId.SHA384_oid, 11);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class SHA512withRSA extends RSASignature
/*     */   {
/*     */     public SHA512withRSA() {
/* 305 */       super(AlgorithmId.SHA512_oid, 11);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\rsa\RSASignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */