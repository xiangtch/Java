/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.KeyFactorySpi;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.interfaces.DSAParams;
/*     */ import java.security.spec.DSAPrivateKeySpec;
/*     */ import java.security.spec.DSAPublicKeySpec;
/*     */ import java.security.spec.InvalidKeySpecException;
/*     */ import java.security.spec.KeySpec;
/*     */ import java.security.spec.PKCS8EncodedKeySpec;
/*     */ import java.security.spec.X509EncodedKeySpec;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DSAKeyFactory
/*     */   extends KeyFactorySpi
/*     */ {
/*     */   static final boolean SERIAL_INTEROP;
/*     */   private static final String SERIAL_PROP = "sun.security.key.serial.interop";
/*     */   
/*     */   static
/*     */   {
/*  74 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.key.serial.interop", null));
/*  75 */     SERIAL_INTEROP = "true".equalsIgnoreCase(str);
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
/*     */   protected PublicKey engineGeneratePublic(KeySpec paramKeySpec)
/*     */     throws InvalidKeySpecException
/*     */   {
/*     */     try
/*     */     {
/*  92 */       if ((paramKeySpec instanceof DSAPublicKeySpec)) {
/*  93 */         DSAPublicKeySpec localDSAPublicKeySpec = (DSAPublicKeySpec)paramKeySpec;
/*  94 */         if (SERIAL_INTEROP) {
/*  95 */           return new DSAPublicKey(localDSAPublicKeySpec.getY(), localDSAPublicKeySpec
/*  96 */             .getP(), localDSAPublicKeySpec
/*  97 */             .getQ(), localDSAPublicKeySpec
/*  98 */             .getG());
/*     */         }
/* 100 */         return new DSAPublicKeyImpl(localDSAPublicKeySpec.getY(), localDSAPublicKeySpec
/* 101 */           .getP(), localDSAPublicKeySpec
/* 102 */           .getQ(), localDSAPublicKeySpec
/* 103 */           .getG());
/*     */       }
/* 105 */       if ((paramKeySpec instanceof X509EncodedKeySpec)) {
/* 106 */         if (SERIAL_INTEROP) {
/* 107 */           return new DSAPublicKey(((X509EncodedKeySpec)paramKeySpec)
/* 108 */             .getEncoded());
/*     */         }
/* 110 */         return new DSAPublicKeyImpl(((X509EncodedKeySpec)paramKeySpec)
/* 111 */           .getEncoded());
/*     */       }
/*     */       
/* 114 */       throw new InvalidKeySpecException("Inappropriate key specification");
/*     */ 
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException)
/*     */     {
/* 119 */       throw new InvalidKeySpecException("Inappropriate key specification: " + localInvalidKeyException.getMessage());
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
/*     */   protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec)
/*     */     throws InvalidKeySpecException
/*     */   {
/*     */     try
/*     */     {
/* 137 */       if ((paramKeySpec instanceof DSAPrivateKeySpec)) {
/* 138 */         DSAPrivateKeySpec localDSAPrivateKeySpec = (DSAPrivateKeySpec)paramKeySpec;
/* 139 */         return new DSAPrivateKey(localDSAPrivateKeySpec.getX(), localDSAPrivateKeySpec
/* 140 */           .getP(), localDSAPrivateKeySpec
/* 141 */           .getQ(), localDSAPrivateKeySpec
/* 142 */           .getG());
/*     */       }
/* 144 */       if ((paramKeySpec instanceof PKCS8EncodedKeySpec)) {
/* 145 */         return new DSAPrivateKey(((PKCS8EncodedKeySpec)paramKeySpec)
/* 146 */           .getEncoded());
/*     */       }
/*     */       
/* 149 */       throw new InvalidKeySpecException("Inappropriate key specification");
/*     */ 
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException)
/*     */     {
/* 154 */       throw new InvalidKeySpecException("Inappropriate key specification: " + localInvalidKeyException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected <T extends KeySpec> T engineGetKeySpec(Key paramKey, Class<T> paramClass)
/*     */     throws InvalidKeySpecException
/*     */   {
/*     */     try
/*     */     {
/*     */       Class localClass1;
/*     */       
/*     */ 
/*     */ 
/*     */       Class localClass2;
/*     */       
/*     */ 
/*     */ 
/*     */       Object localObject;
/*     */       
/*     */ 
/*     */ 
/*     */       DSAParams localDSAParams;
/*     */       
/*     */ 
/*     */ 
/* 182 */       if ((paramKey instanceof java.security.interfaces.DSAPublicKey))
/*     */       {
/*     */ 
/*     */ 
/* 186 */         localClass1 = Class.forName("java.security.spec.DSAPublicKeySpec");
/*     */         
/* 188 */         localClass2 = Class.forName("java.security.spec.X509EncodedKeySpec");
/*     */         
/* 190 */         if (localClass1.isAssignableFrom(paramClass)) {
/* 191 */           localObject = (java.security.interfaces.DSAPublicKey)paramKey;
/*     */           
/* 193 */           localDSAParams = ((java.security.interfaces.DSAPublicKey)localObject).getParams();
/* 194 */           return (KeySpec)paramClass.cast(new DSAPublicKeySpec(((java.security.interfaces.DSAPublicKey)localObject).getY(), localDSAParams
/* 195 */             .getP(), localDSAParams
/* 196 */             .getQ(), localDSAParams
/* 197 */             .getG()));
/*     */         }
/* 199 */         if (localClass2.isAssignableFrom(paramClass)) {
/* 200 */           return (KeySpec)paramClass.cast(new X509EncodedKeySpec(paramKey.getEncoded()));
/*     */         }
/*     */         
/* 203 */         throw new InvalidKeySpecException("Inappropriate key specification");
/*     */       }
/*     */       
/*     */ 
/* 207 */       if ((paramKey instanceof java.security.interfaces.DSAPrivateKey))
/*     */       {
/*     */ 
/*     */ 
/* 211 */         localClass1 = Class.forName("java.security.spec.DSAPrivateKeySpec");
/*     */         
/* 213 */         localClass2 = Class.forName("java.security.spec.PKCS8EncodedKeySpec");
/*     */         
/* 215 */         if (localClass1.isAssignableFrom(paramClass)) {
/* 216 */           localObject = (java.security.interfaces.DSAPrivateKey)paramKey;
/*     */           
/* 218 */           localDSAParams = ((java.security.interfaces.DSAPrivateKey)localObject).getParams();
/* 219 */           return (KeySpec)paramClass.cast(new DSAPrivateKeySpec(((java.security.interfaces.DSAPrivateKey)localObject).getX(), localDSAParams
/* 220 */             .getP(), localDSAParams
/* 221 */             .getQ(), localDSAParams
/* 222 */             .getG()));
/*     */         }
/* 224 */         if (localClass2.isAssignableFrom(paramClass)) {
/* 225 */           return (KeySpec)paramClass.cast(new PKCS8EncodedKeySpec(paramKey.getEncoded()));
/*     */         }
/*     */         
/* 228 */         throw new InvalidKeySpecException("Inappropriate key specification");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 233 */       throw new InvalidKeySpecException("Inappropriate key type");
/*     */ 
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException)
/*     */     {
/* 238 */       throw new InvalidKeySpecException("Unsupported key specification: " + localClassNotFoundException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Key engineTranslateKey(Key paramKey)
/*     */     throws InvalidKeyException
/*     */   {
/*     */     try
/*     */     {
/*     */       Object localObject;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 257 */       if ((paramKey instanceof java.security.interfaces.DSAPublicKey))
/*     */       {
/* 259 */         if ((paramKey instanceof DSAPublicKey)) {
/* 260 */           return paramKey;
/*     */         }
/*     */         
/*     */ 
/* 264 */         localObject = (DSAPublicKeySpec)engineGetKeySpec(paramKey, DSAPublicKeySpec.class);
/*     */         
/* 266 */         return engineGeneratePublic((KeySpec)localObject);
/*     */       }
/* 268 */       if ((paramKey instanceof java.security.interfaces.DSAPrivateKey))
/*     */       {
/* 270 */         if ((paramKey instanceof DSAPrivateKey)) {
/* 271 */           return paramKey;
/*     */         }
/*     */         
/*     */ 
/* 275 */         localObject = (DSAPrivateKeySpec)engineGetKeySpec(paramKey, DSAPrivateKeySpec.class);
/*     */         
/* 277 */         return engineGeneratePrivate((KeySpec)localObject);
/*     */       }
/*     */       
/* 280 */       throw new InvalidKeyException("Wrong algorithm type");
/*     */ 
/*     */     }
/*     */     catch (InvalidKeySpecException localInvalidKeySpecException)
/*     */     {
/* 285 */       throw new InvalidKeyException("Cannot translate key: " + localInvalidKeySpecException.getMessage());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\DSAKeyFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */