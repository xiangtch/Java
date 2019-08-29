/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.AlgorithmParameters;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Provider;
/*     */ import java.security.spec.ECField;
/*     */ import java.security.spec.ECGenParameterSpec;
/*     */ import java.security.spec.ECParameterSpec;
/*     */ import java.security.spec.ECPoint;
/*     */ import java.security.spec.EllipticCurve;
/*     */ import java.security.spec.InvalidParameterSpecException;
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
/*     */ public class ECUtil
/*     */ {
/*     */   public static ECPoint decodePoint(byte[] paramArrayOfByte, EllipticCurve paramEllipticCurve)
/*     */     throws IOException
/*     */   {
/*  47 */     if ((paramArrayOfByte.length == 0) || (paramArrayOfByte[0] != 4)) {
/*  48 */       throw new IOException("Only uncompressed point format supported");
/*     */     }
/*     */     
/*     */ 
/*  52 */     int i = (paramArrayOfByte.length - 1) / 2;
/*  53 */     if (i != paramEllipticCurve.getField().getFieldSize() + 7 >> 3) {
/*  54 */       throw new IOException("Point does not match field size");
/*     */     }
/*     */     
/*  57 */     byte[] arrayOfByte1 = Arrays.copyOfRange(paramArrayOfByte, 1, 1 + i);
/*  58 */     byte[] arrayOfByte2 = Arrays.copyOfRange(paramArrayOfByte, i + 1, i + 1 + i);
/*     */     
/*  60 */     return new ECPoint(new BigInteger(1, arrayOfByte1), new BigInteger(1, arrayOfByte2));
/*     */   }
/*     */   
/*     */ 
/*     */   public static byte[] encodePoint(ECPoint paramECPoint, EllipticCurve paramEllipticCurve)
/*     */   {
/*  66 */     int i = paramEllipticCurve.getField().getFieldSize() + 7 >> 3;
/*  67 */     byte[] arrayOfByte1 = trimZeroes(paramECPoint.getAffineX().toByteArray());
/*  68 */     byte[] arrayOfByte2 = trimZeroes(paramECPoint.getAffineY().toByteArray());
/*  69 */     if ((arrayOfByte1.length > i) || (arrayOfByte2.length > i)) {
/*  70 */       throw new RuntimeException("Point coordinates do not match field size");
/*     */     }
/*     */     
/*  73 */     byte[] arrayOfByte3 = new byte[1 + (i << 1)];
/*  74 */     arrayOfByte3[0] = 4;
/*  75 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte3, i - arrayOfByte1.length + 1, arrayOfByte1.length);
/*  76 */     System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte3.length - arrayOfByte2.length, arrayOfByte2.length);
/*  77 */     return arrayOfByte3;
/*     */   }
/*     */   
/*     */   public static byte[] trimZeroes(byte[] paramArrayOfByte) {
/*  81 */     int i = 0;
/*  82 */     while ((i < paramArrayOfByte.length - 1) && (paramArrayOfByte[i] == 0)) {
/*  83 */       i++;
/*     */     }
/*  85 */     if (i == 0) {
/*  86 */       return paramArrayOfByte;
/*     */     }
/*     */     
/*  89 */     return Arrays.copyOfRange(paramArrayOfByte, i, paramArrayOfByte.length);
/*     */   }
/*     */   
/*     */   private static AlgorithmParameters getECParameters(Provider paramProvider) {
/*     */     try {
/*  94 */       if (paramProvider != null) {
/*  95 */         return AlgorithmParameters.getInstance("EC", paramProvider);
/*     */       }
/*     */       
/*  98 */       return AlgorithmParameters.getInstance("EC");
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 100 */       throw new RuntimeException(localNoSuchAlgorithmException);
/*     */     }
/*     */   }
/*     */   
/*     */   public static byte[] encodeECParameterSpec(Provider paramProvider, ECParameterSpec paramECParameterSpec)
/*     */   {
/* 106 */     AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
/*     */     try
/*     */     {
/* 109 */       localAlgorithmParameters.init(paramECParameterSpec);
/*     */     } catch (InvalidParameterSpecException localInvalidParameterSpecException) {
/* 111 */       throw new RuntimeException("Not a known named curve: " + paramECParameterSpec);
/*     */     }
/*     */     try
/*     */     {
/* 115 */       return localAlgorithmParameters.getEncoded();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 118 */       throw new RuntimeException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   public static ECParameterSpec getECParameterSpec(Provider paramProvider, ECParameterSpec paramECParameterSpec)
/*     */   {
/* 124 */     AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
/*     */     try
/*     */     {
/* 127 */       localAlgorithmParameters.init(paramECParameterSpec);
/* 128 */       return (ECParameterSpec)localAlgorithmParameters.getParameterSpec(ECParameterSpec.class);
/*     */     } catch (InvalidParameterSpecException localInvalidParameterSpecException) {}
/* 130 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static ECParameterSpec getECParameterSpec(Provider paramProvider, byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 137 */     AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
/*     */     
/* 139 */     localAlgorithmParameters.init(paramArrayOfByte);
/*     */     try
/*     */     {
/* 142 */       return (ECParameterSpec)localAlgorithmParameters.getParameterSpec(ECParameterSpec.class);
/*     */     } catch (InvalidParameterSpecException localInvalidParameterSpecException) {}
/* 144 */     return null;
/*     */   }
/*     */   
/*     */   public static ECParameterSpec getECParameterSpec(Provider paramProvider, String paramString)
/*     */   {
/* 149 */     AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
/*     */     try
/*     */     {
/* 152 */       localAlgorithmParameters.init(new ECGenParameterSpec(paramString));
/* 153 */       return (ECParameterSpec)localAlgorithmParameters.getParameterSpec(ECParameterSpec.class);
/*     */     } catch (InvalidParameterSpecException localInvalidParameterSpecException) {}
/* 155 */     return null;
/*     */   }
/*     */   
/*     */   public static ECParameterSpec getECParameterSpec(Provider paramProvider, int paramInt)
/*     */   {
/* 160 */     AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
/*     */     try
/*     */     {
/* 163 */       localAlgorithmParameters.init(new ECKeySizeParameterSpec(paramInt));
/* 164 */       return (ECParameterSpec)localAlgorithmParameters.getParameterSpec(ECParameterSpec.class);
/*     */     } catch (InvalidParameterSpecException localInvalidParameterSpecException) {}
/* 166 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getCurveName(Provider paramProvider, ECParameterSpec paramECParameterSpec)
/*     */   {
/* 173 */     AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
/*     */     ECGenParameterSpec localECGenParameterSpec;
/*     */     try {
/* 176 */       localAlgorithmParameters.init(paramECParameterSpec);
/* 177 */       localECGenParameterSpec = (ECGenParameterSpec)localAlgorithmParameters.getParameterSpec(ECGenParameterSpec.class);
/*     */     } catch (InvalidParameterSpecException localInvalidParameterSpecException) {
/* 179 */       return null;
/*     */     }
/*     */     
/* 182 */     if (localECGenParameterSpec == null) {
/* 183 */       return null;
/*     */     }
/*     */     
/* 186 */     return localECGenParameterSpec.getName();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\ECUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */