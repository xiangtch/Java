/*    */ package sun.security.provider;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class NativeSeedGenerator
/*    */   extends SeedGenerator
/*    */ {
/*    */   NativeSeedGenerator(String paramString)
/*    */     throws IOException
/*    */   {
/* 46 */     if (!nativeGenerateSeed(new byte[2])) {
/* 47 */       throw new IOException("Required native CryptoAPI features not  available on this machine");
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private static native boolean nativeGenerateSeed(byte[] paramArrayOfByte);
/*    */   
/*    */ 
/*    */ 
/*    */   void getSeedBytes(byte[] paramArrayOfByte)
/*    */   {
/* 60 */     if (!nativeGenerateSeed(paramArrayOfByte))
/*    */     {
/* 62 */       throw new InternalError("Unexpected CryptoAPI failure generating seed");
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\NativeSeedGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */