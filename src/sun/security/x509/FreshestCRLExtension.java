/*    */ package sun.security.x509;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.util.List;
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
/*    */ public class FreshestCRLExtension
/*    */   extends CRLDistributionPointsExtension
/*    */ {
/*    */   public static final String NAME = "FreshestCRL";
/*    */   
/*    */   public FreshestCRLExtension(List<DistributionPoint> paramList)
/*    */     throws IOException
/*    */   {
/* 74 */     super(PKIXExtensions.FreshestCRL_Id, false, paramList, "FreshestCRL");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public FreshestCRLExtension(Boolean paramBoolean, Object paramObject)
/*    */     throws IOException
/*    */   {
/* 86 */     super(PKIXExtensions.FreshestCRL_Id, Boolean.valueOf(paramBoolean.booleanValue()), paramObject, "FreshestCRL");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void encode(OutputStream paramOutputStream)
/*    */     throws IOException
/*    */   {
/* 97 */     super.encode(paramOutputStream, PKIXExtensions.FreshestCRL_Id, false);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\FreshestCRLExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */