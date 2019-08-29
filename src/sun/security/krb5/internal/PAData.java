/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.krb5.Asn1Exception;
/*     */ import sun.security.krb5.internal.util.KerberosString;
/*     */ import sun.security.util.DerInputStream;
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
/*     */ public class PAData
/*     */ {
/*     */   private int pADataType;
/*  59 */   private byte[] pADataValue = null;
/*     */   private static final byte TAG_PATYPE = 1;
/*     */   private static final byte TAG_PAVALUE = 2;
/*     */   
/*     */   private PAData() {}
/*     */   
/*     */   public PAData(int paramInt, byte[] paramArrayOfByte)
/*     */   {
/*  67 */     this.pADataType = paramInt;
/*  68 */     if (paramArrayOfByte != null) {
/*  69 */       this.pADataValue = ((byte[])paramArrayOfByte.clone());
/*     */     }
/*     */   }
/*     */   
/*     */   public Object clone() {
/*  74 */     PAData localPAData = new PAData();
/*  75 */     localPAData.pADataType = this.pADataType;
/*  76 */     if (this.pADataValue != null) {
/*  77 */       localPAData.pADataValue = new byte[this.pADataValue.length];
/*  78 */       System.arraycopy(this.pADataValue, 0, localPAData.pADataValue, 0, this.pADataValue.length);
/*     */     }
/*     */     
/*  81 */     return localPAData;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PAData(DerValue paramDerValue)
/*     */     throws Asn1Exception, IOException
/*     */   {
/*  91 */     DerValue localDerValue = null;
/*  92 */     if (paramDerValue.getTag() != 48) {
/*  93 */       throw new Asn1Exception(906);
/*     */     }
/*  95 */     localDerValue = paramDerValue.getData().getDerValue();
/*  96 */     if ((localDerValue.getTag() & 0x1F) == 1) {
/*  97 */       this.pADataType = localDerValue.getData().getBigInteger().intValue();
/*     */     }
/*     */     else
/* 100 */       throw new Asn1Exception(906);
/* 101 */     localDerValue = paramDerValue.getData().getDerValue();
/* 102 */     if ((localDerValue.getTag() & 0x1F) == 2) {
/* 103 */       this.pADataValue = localDerValue.getData().getOctetString();
/*     */     }
/* 105 */     if (paramDerValue.getData().available() > 0) {
/* 106 */       throw new Asn1Exception(906);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] asn1Encode()
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 118 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 119 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*     */     
/* 121 */     localDerOutputStream2.putInteger(this.pADataType);
/* 122 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
/* 123 */     localDerOutputStream2 = new DerOutputStream();
/* 124 */     localDerOutputStream2.putOctetString(this.pADataValue);
/* 125 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
/*     */     
/* 127 */     localDerOutputStream2 = new DerOutputStream();
/* 128 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/* 129 */     return localDerOutputStream2.toByteArray();
/*     */   }
/*     */   
/*     */   public int getType()
/*     */   {
/* 134 */     return this.pADataType;
/*     */   }
/*     */   
/*     */   public byte[] getValue() {
/* 138 */     return this.pADataValue == null ? null : (byte[])this.pADataValue.clone();
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
/*     */   public static int getPreferredEType(PAData[] paramArrayOfPAData, int paramInt)
/*     */     throws IOException, Asn1Exception
/*     */   {
/* 154 */     if (paramArrayOfPAData == null) { return paramInt;
/*     */     }
/* 156 */     DerValue localDerValue1 = null;DerValue localDerValue2 = null;
/* 157 */     for (Object localObject3 : paramArrayOfPAData) {
/* 158 */       if (((PAData)localObject3).getValue() != null)
/* 159 */         switch (((PAData)localObject3).getType()) {
/*     */         case 11: 
/* 161 */           localDerValue1 = new DerValue(((PAData)localObject3).getValue());
/* 162 */           break;
/*     */         case 19: 
/* 164 */           localDerValue2 = new DerValue(((PAData)localObject3).getValue());
/*     */         }
/*     */     }
/*     */     Object localObject2;
/* 168 */     if (localDerValue2 != null) {
/* 169 */       while (localDerValue2.data.available() > 0) {
/* 170 */         ??? = localDerValue2.data.getDerValue();
/* 171 */         localObject2 = new ETypeInfo2((DerValue)???);
/* 172 */         if (((ETypeInfo2)localObject2).getParams() == null)
/*     */         {
/* 174 */           return ((ETypeInfo2)localObject2).getEType();
/*     */         }
/*     */       }
/*     */     }
/* 178 */     if ((localDerValue1 != null) && 
/* 179 */       (localDerValue1.data.available() > 0)) {
/* 180 */       ??? = localDerValue1.data.getDerValue();
/* 181 */       localObject2 = new ETypeInfo((DerValue)???);
/* 182 */       return ((ETypeInfo)localObject2).getEType();
/*     */     }
/*     */     
/* 185 */     return paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class SaltAndParams
/*     */   {
/*     */     public final String salt;
/*     */     
/*     */     public final byte[] params;
/*     */     
/*     */     public SaltAndParams(String paramString, byte[] paramArrayOfByte)
/*     */     {
/* 197 */       if ((paramString != null) && (paramString.isEmpty())) paramString = null;
/* 198 */       this.salt = paramString;
/* 199 */       this.params = paramArrayOfByte;
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
/*     */   public static SaltAndParams getSaltAndParams(int paramInt, PAData[] paramArrayOfPAData)
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 215 */     if (paramArrayOfPAData == null) { return null;
/*     */     }
/* 217 */     DerValue localDerValue1 = null;DerValue localDerValue2 = null;
/* 218 */     String str = null;
/*     */     
/* 220 */     for (Object localObject3 : paramArrayOfPAData) {
/* 221 */       if (((PAData)localObject3).getValue() != null)
/* 222 */         switch (((PAData)localObject3).getType()) {
/*     */         case 3: 
/* 224 */           str = new String(((PAData)localObject3).getValue(), KerberosString.MSNAME ? "UTF8" : "8859_1");
/*     */           
/* 226 */           break;
/*     */         case 11: 
/* 228 */           localDerValue1 = new DerValue(((PAData)localObject3).getValue());
/* 229 */           break;
/*     */         case 19: 
/* 231 */           localDerValue2 = new DerValue(((PAData)localObject3).getValue());
/*     */         }
/*     */     }
/*     */     Object localObject2;
/* 235 */     if (localDerValue2 != null) {
/* 236 */       while (localDerValue2.data.available() > 0) {
/* 237 */         ??? = localDerValue2.data.getDerValue();
/* 238 */         localObject2 = new ETypeInfo2((DerValue)???);
/* 239 */         if ((((ETypeInfo2)localObject2).getParams() == null) && (((ETypeInfo2)localObject2).getEType() == paramInt))
/*     */         {
/* 241 */           return new SaltAndParams(((ETypeInfo2)localObject2).getSalt(), ((ETypeInfo2)localObject2).getParams());
/*     */         }
/*     */       }
/*     */     }
/* 245 */     if (localDerValue1 != null) {
/* 246 */       while (localDerValue1.data.available() > 0) {
/* 247 */         ??? = localDerValue1.data.getDerValue();
/* 248 */         localObject2 = new ETypeInfo((DerValue)???);
/* 249 */         if (((ETypeInfo)localObject2).getEType() == paramInt) {
/* 250 */           return new SaltAndParams(((ETypeInfo)localObject2).getSalt(), null);
/*     */         }
/*     */       }
/*     */     }
/* 254 */     if (str != null) {
/* 255 */       return new SaltAndParams(str, null);
/*     */     }
/* 257 */     return null;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 262 */     StringBuilder localStringBuilder = new StringBuilder();
/* 263 */     localStringBuilder.append(">>>Pre-Authentication Data:\n\t PA-DATA type = ")
/* 264 */       .append(this.pADataType).append('\n');
/*     */     DerValue localDerValue3;
/* 266 */     Object localObject; switch (this.pADataType) {
/*     */     case 2: 
/* 268 */       localStringBuilder.append("\t PA-ENC-TIMESTAMP");
/* 269 */       break;
/*     */     case 11: 
/* 271 */       if (this.pADataValue != null) {
/*     */         try {
/* 273 */           DerValue localDerValue1 = new DerValue(this.pADataValue);
/* 274 */           while (localDerValue1.data.available() > 0) {
/* 275 */             localDerValue3 = localDerValue1.data.getDerValue();
/* 276 */             localObject = new ETypeInfo(localDerValue3);
/* 277 */             localStringBuilder.append("\t PA-ETYPE-INFO etype = ")
/* 278 */               .append(((ETypeInfo)localObject).getEType())
/* 279 */               .append(", salt = ")
/* 280 */               .append(((ETypeInfo)localObject).getSalt())
/* 281 */               .append('\n');
/*     */           }
/*     */         } catch (IOException|Asn1Exception localIOException1) {
/* 284 */           localStringBuilder.append("\t <Unparseable PA-ETYPE-INFO>\n");
/*     */         }
/*     */       }
/*     */       break;
/*     */     case 19: 
/* 289 */       if (this.pADataValue != null) {
/*     */         try {
/* 291 */           DerValue localDerValue2 = new DerValue(this.pADataValue);
/* 292 */           while (localDerValue2.data.available() > 0) {
/* 293 */             localDerValue3 = localDerValue2.data.getDerValue();
/* 294 */             localObject = new ETypeInfo2(localDerValue3);
/* 295 */             localStringBuilder.append("\t PA-ETYPE-INFO2 etype = ")
/* 296 */               .append(((ETypeInfo2)localObject).getEType())
/* 297 */               .append(", salt = ")
/* 298 */               .append(((ETypeInfo2)localObject).getSalt())
/* 299 */               .append(", s2kparams = ");
/* 300 */             byte[] arrayOfByte = ((ETypeInfo2)localObject).getParams();
/* 301 */             if (arrayOfByte == null) {
/* 302 */               localStringBuilder.append("null\n");
/* 303 */             } else if (arrayOfByte.length == 0) {
/* 304 */               localStringBuilder.append("empty\n");
/*     */             } else {
/* 306 */               localStringBuilder.append(new HexDumpEncoder()
/* 307 */                 .encodeBuffer(arrayOfByte));
/*     */             }
/*     */           }
/*     */         } catch (IOException|Asn1Exception localIOException2) {
/* 311 */           localStringBuilder.append("\t <Unparseable PA-ETYPE-INFO>\n");
/*     */         }
/*     */       }
/*     */       break;
/*     */     case 129: 
/* 316 */       localStringBuilder.append("\t PA-FOR-USER\n");
/* 317 */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 322 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\PAData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */