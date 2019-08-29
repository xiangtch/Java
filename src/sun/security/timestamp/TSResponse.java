/*     */ package sun.security.timestamp;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import sun.security.pkcs.ContentInfo;
/*     */ import sun.security.pkcs.PKCS7;
/*     */ import sun.security.util.BitArray;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerInputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TSResponse
/*     */ {
/*     */   public static final int GRANTED = 0;
/*     */   public static final int GRANTED_WITH_MODS = 1;
/*     */   public static final int REJECTION = 2;
/*     */   public static final int WAITING = 3;
/*     */   public static final int REVOCATION_WARNING = 4;
/*     */   public static final int REVOCATION_NOTIFICATION = 5;
/*     */   public static final int BAD_ALG = 0;
/*     */   public static final int BAD_REQUEST = 2;
/*     */   public static final int BAD_DATA_FORMAT = 5;
/*     */   public static final int TIME_NOT_AVAILABLE = 14;
/*     */   public static final int UNACCEPTED_POLICY = 15;
/*     */   public static final int UNACCEPTED_EXTENSION = 16;
/*     */   public static final int ADD_INFO_NOT_AVAILABLE = 17;
/*     */   public static final int SYSTEM_FAILURE = 25;
/* 179 */   private static final Debug debug = Debug.getInstance("ts");
/*     */   
/*     */   private int status;
/*     */   
/* 183 */   private String[] statusString = null;
/*     */   
/* 185 */   private boolean[] failureInfo = null;
/*     */   
/* 187 */   private byte[] encodedTsToken = null;
/*     */   
/* 189 */   private PKCS7 tsToken = null;
/*     */   
/*     */ 
/*     */ 
/*     */   private TimestampToken tstInfo;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   TSResponse(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 201 */     parse(paramArrayOfByte);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getStatusCode()
/*     */   {
/* 208 */     return this.status;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] getStatusMessages()
/*     */   {
/* 217 */     return this.statusString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean[] getFailureInfo()
/*     */   {
/* 226 */     return this.failureInfo;
/*     */   }
/*     */   
/*     */   public String getStatusCodeAsText()
/*     */   {
/* 231 */     switch (this.status) {
/*     */     case 0: 
/* 233 */       return "the timestamp request was granted.";
/*     */     
/*     */     case 1: 
/* 236 */       return "the timestamp request was granted with some modifications.";
/*     */     
/*     */ 
/*     */     case 2: 
/* 240 */       return "the timestamp request was rejected.";
/*     */     
/*     */     case 3: 
/* 243 */       return "the timestamp request has not yet been processed.";
/*     */     
/*     */     case 4: 
/* 246 */       return "warning: a certificate revocation is imminent.";
/*     */     
/*     */     case 5: 
/* 249 */       return "notification: a certificate revocation has occurred.";
/*     */     }
/*     */     
/* 252 */     return "unknown status code " + this.status + ".";
/*     */   }
/*     */   
/*     */   private boolean isSet(int paramInt)
/*     */   {
/* 257 */     return this.failureInfo[paramInt];
/*     */   }
/*     */   
/*     */   public String getFailureCodeAsText()
/*     */   {
/* 262 */     if (this.failureInfo == null) {
/* 263 */       return "";
/*     */     }
/*     */     try
/*     */     {
/* 267 */       if (isSet(0))
/* 268 */         return "Unrecognized or unsupported algorithm identifier.";
/* 269 */       if (isSet(2)) {
/* 270 */         return "The requested transaction is not permitted or supported.";
/*     */       }
/* 272 */       if (isSet(5))
/* 273 */         return "The data submitted has the wrong format.";
/* 274 */       if (isSet(14))
/* 275 */         return "The TSA's time source is not available.";
/* 276 */       if (isSet(15))
/* 277 */         return "The requested TSA policy is not supported by the TSA.";
/* 278 */       if (isSet(16))
/* 279 */         return "The requested extension is not supported by the TSA.";
/* 280 */       if (isSet(17)) {
/* 281 */         return "The additional information requested could not be understood or is not available.";
/*     */       }
/* 283 */       if (isSet(25)) {
/* 284 */         return "The request cannot be handled due to system failure.";
/*     */       }
/*     */     } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
/* 287 */     return "unknown failure code";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS7 getToken()
/*     */   {
/* 296 */     return this.tsToken;
/*     */   }
/*     */   
/*     */   public TimestampToken getTimestampToken() {
/* 300 */     return this.tstInfo;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncodedToken()
/*     */   {
/* 309 */     return this.encodedTsToken;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void parse(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 322 */     DerValue localDerValue1 = new DerValue(paramArrayOfByte);
/* 323 */     if (localDerValue1.tag != 48) {
/* 324 */       throw new IOException("Bad encoding for timestamp response");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 329 */     DerValue localDerValue2 = localDerValue1.data.getDerValue();
/* 330 */     this.status = localDerValue2.data.getInteger();
/* 331 */     if (debug != null) {
/* 332 */       debug.println("timestamp response: status=" + this.status);
/*     */     }
/*     */     
/* 335 */     if (localDerValue2.data.available() > 0) {
/* 336 */       int i = (byte)localDerValue2.data.peekByte();
/* 337 */       if (i == 48) {
/* 338 */         DerValue[] arrayOfDerValue = localDerValue2.data.getSequence(1);
/* 339 */         this.statusString = new String[arrayOfDerValue.length];
/* 340 */         for (int j = 0; j < arrayOfDerValue.length; j++) {
/* 341 */           this.statusString[j] = arrayOfDerValue[j].getUTF8String();
/* 342 */           if (debug != null) {
/* 343 */             debug.println("timestamp response: statusString=" + this.statusString[j]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 350 */     if (localDerValue2.data.available() > 0)
/*     */     {
/* 352 */       this.failureInfo = localDerValue2.data.getUnalignedBitString().toBooleanArray();
/*     */     }
/*     */     
/*     */ 
/* 356 */     if (localDerValue1.data.available() > 0) {
/* 357 */       DerValue localDerValue3 = localDerValue1.data.getDerValue();
/* 358 */       this.encodedTsToken = localDerValue3.toByteArray();
/* 359 */       this.tsToken = new PKCS7(this.encodedTsToken);
/* 360 */       this.tstInfo = new TimestampToken(this.tsToken.getContentInfo().getData());
/*     */     }
/*     */     
/*     */ 
/* 364 */     if ((this.status == 0) || (this.status == 1)) {
/* 365 */       if (this.tsToken == null) {
/* 366 */         throw new TimestampException("Bad encoding for timestamp response: expected a timeStampToken element to be present");
/*     */       }
/*     */       
/*     */     }
/* 370 */     else if (this.tsToken != null) {
/* 371 */       throw new TimestampException("Bad encoding for timestamp response: expected no timeStampToken element to be present");
/*     */     }
/*     */   }
/*     */   
/*     */   static final class TimestampException extends IOException
/*     */   {
/*     */     private static final long serialVersionUID = -1631631794891940953L;
/*     */     
/*     */     TimestampException(String paramString)
/*     */     {
/* 381 */       super();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\timestamp\TSResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */