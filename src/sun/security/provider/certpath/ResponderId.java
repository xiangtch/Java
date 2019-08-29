/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.PublicKey;
/*     */ import java.util.Arrays;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.x509.KeyIdentifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ResponderId
/*     */ {
/*     */   private Type type;
/*     */   private X500Principal responderName;
/*     */   private KeyIdentifier responderKeyId;
/*     */   private byte[] encodedRid;
/*     */   
/*     */   public static enum Type
/*     */   {
/*  68 */     BY_NAME(1, "byName"), 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  75 */     BY_KEY(2, "byKey");
/*     */     
/*     */     private final int tagNumber;
/*     */     private final String ridTypeName;
/*     */     
/*     */     private Type(int paramInt, String paramString) {
/*  81 */       this.tagNumber = paramInt;
/*  82 */       this.ridTypeName = paramString;
/*     */     }
/*     */     
/*     */     public int value() {
/*  86 */       return this.tagNumber;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/*  91 */       return this.ridTypeName;
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
/*     */   public ResponderId(X500Principal paramX500Principal)
/*     */     throws IOException
/*     */   {
/* 111 */     this.responderName = paramX500Principal;
/* 112 */     this.responderKeyId = null;
/* 113 */     this.encodedRid = principalToBytes();
/* 114 */     this.type = Type.BY_NAME;
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
/*     */   public ResponderId(PublicKey paramPublicKey)
/*     */     throws IOException
/*     */   {
/* 128 */     this.responderKeyId = new KeyIdentifier(paramPublicKey);
/* 129 */     this.responderName = null;
/* 130 */     this.encodedRid = keyIdToBytes();
/* 131 */     this.type = Type.BY_KEY;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResponderId(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 142 */     DerValue localDerValue = new DerValue(paramArrayOfByte);
/*     */     
/* 144 */     if ((localDerValue.isContextSpecific((byte)Type.BY_NAME.value())) && 
/* 145 */       (localDerValue.isConstructed()))
/*     */     {
/*     */ 
/* 148 */       this.responderName = new X500Principal(localDerValue.getDataBytes());
/* 149 */       this.encodedRid = principalToBytes();
/* 150 */       this.type = Type.BY_NAME;
/* 151 */     } else if ((localDerValue.isContextSpecific((byte)Type.BY_KEY.value())) && 
/* 152 */       (localDerValue.isConstructed()))
/*     */     {
/*     */ 
/*     */ 
/* 156 */       this.responderKeyId = new KeyIdentifier(new DerValue(localDerValue.getDataBytes()));
/* 157 */       this.encodedRid = keyIdToBytes();
/* 158 */       this.type = Type.BY_KEY;
/*     */     } else {
/* 160 */       throw new IOException("Invalid ResponderId content");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncoded()
/*     */   {
/* 171 */     return (byte[])this.encodedRid.clone();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Type getType()
/*     */   {
/* 181 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int length()
/*     */   {
/* 191 */     return this.encodedRid.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X500Principal getResponderName()
/*     */   {
/* 202 */     return this.responderName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public KeyIdentifier getKeyIdentifier()
/*     */   {
/* 213 */     return this.responderKeyId;
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 229 */     if (paramObject == null) {
/* 230 */       return false;
/*     */     }
/*     */     
/* 233 */     if (this == paramObject) {
/* 234 */       return true;
/*     */     }
/*     */     
/* 237 */     if ((paramObject instanceof ResponderId)) {
/* 238 */       ResponderId localResponderId = (ResponderId)paramObject;
/* 239 */       return Arrays.equals(this.encodedRid, localResponderId.getEncoded());
/*     */     }
/*     */     
/* 242 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 252 */     return Arrays.hashCode(this.encodedRid);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 262 */     StringBuilder localStringBuilder = new StringBuilder();
/* 263 */     switch (this.type) {
/*     */     case BY_NAME: 
/* 265 */       localStringBuilder.append(this.type).append(": ").append(this.responderName);
/* 266 */       break;
/*     */     case BY_KEY: 
/* 268 */       localStringBuilder.append(this.type).append(": ");
/* 269 */       for (byte b : this.responderKeyId.getIdentifier()) {
/* 270 */         localStringBuilder.append(String.format("%02X", new Object[] { Byte.valueOf(b) }));
/*     */       }
/* 272 */       break;
/*     */     default: 
/* 274 */       localStringBuilder.append("Unknown ResponderId Type: ").append(this.type);
/*     */     }
/* 276 */     return localStringBuilder.toString();
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
/*     */   private byte[] principalToBytes()
/*     */     throws IOException
/*     */   {
/* 290 */     DerValue localDerValue = new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)Type.BY_NAME.value()), this.responderName.getEncoded());
/* 291 */     return localDerValue.toByteArray();
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
/*     */   private byte[] keyIdToBytes()
/*     */     throws IOException
/*     */   {
/* 305 */     DerValue localDerValue1 = new DerValue((byte)4, this.responderKeyId.getIdentifier());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 310 */     DerValue localDerValue2 = new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)Type.BY_KEY.value()), localDerValue1.toByteArray());
/*     */     
/* 312 */     return localDerValue2.toByteArray();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\ResponderId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */