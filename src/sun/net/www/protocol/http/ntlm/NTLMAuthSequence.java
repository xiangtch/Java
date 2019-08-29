/*    */ package sun.net.www.protocol.http.ntlm;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Base64;
/*    */ import java.util.Base64.Decoder;
/*    */ import java.util.Base64.Encoder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NTLMAuthSequence
/*    */ {
/*    */   private String username;
/*    */   private String password;
/*    */   private String ntdomain;
/*    */   private int state;
/*    */   private long crdHandle;
/*    */   private long ctxHandle;
/*    */   Status status;
/*    */   
/*    */   static
/*    */   {
/* 47 */     initFirst(Status.class);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   NTLMAuthSequence(String paramString1, String paramString2, String paramString3)
/*    */     throws IOException
/*    */   {
/* 62 */     this.username = paramString1;
/* 63 */     this.password = paramString2;
/* 64 */     this.ntdomain = paramString3;
/* 65 */     this.status = new Status();
/* 66 */     this.state = 0;
/* 67 */     this.crdHandle = getCredentialsHandle(paramString1, paramString3, paramString2);
/* 68 */     if (this.crdHandle == 0L) {
/* 69 */       throw new IOException("could not get credentials handle");
/*    */     }
/*    */   }
/*    */   
/*    */   public String getAuthHeader(String paramString) throws IOException {
/* 74 */     byte[] arrayOfByte1 = null;
/*    */     
/* 76 */     assert (!this.status.sequenceComplete);
/*    */     
/* 78 */     if (paramString != null)
/* 79 */       arrayOfByte1 = Base64.getDecoder().decode(paramString);
/* 80 */     byte[] arrayOfByte2 = getNextToken(this.crdHandle, arrayOfByte1, this.status);
/* 81 */     if (arrayOfByte2 == null)
/* 82 */       throw new IOException("Internal authentication error");
/* 83 */     return Base64.getEncoder().encodeToString(arrayOfByte2);
/*    */   }
/*    */   
/*    */   public boolean isComplete() {
/* 87 */     return this.status.sequenceComplete;
/*    */   }
/*    */   
/*    */   private static native void initFirst(Class<Status> paramClass);
/*    */   
/*    */   private native long getCredentialsHandle(String paramString1, String paramString2, String paramString3);
/*    */   
/*    */   private native byte[] getNextToken(long paramLong, byte[] paramArrayOfByte, Status paramStatus);
/*    */   
/*    */   class Status
/*    */   {
/*    */     boolean sequenceComplete;
/*    */     
/*    */     Status() {}
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\http\ntlm\NTLMAuthSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */