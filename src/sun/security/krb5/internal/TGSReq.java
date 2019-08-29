/*    */ package sun.security.krb5.internal;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import sun.security.krb5.Asn1Exception;
/*    */ import sun.security.krb5.KrbException;
/*    */ import sun.security.util.DerValue;
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
/*    */ public class TGSReq
/*    */   extends KDCReq
/*    */ {
/*    */   public TGSReq(PAData[] paramArrayOfPAData, KDCReqBody paramKDCReqBody)
/*    */     throws IOException
/*    */   {
/* 40 */     super(paramArrayOfPAData, paramKDCReqBody, 12);
/*    */   }
/*    */   
/*    */   public TGSReq(byte[] paramArrayOfByte) throws Asn1Exception, IOException, KrbException
/*    */   {
/* 45 */     init(new DerValue(paramArrayOfByte));
/*    */   }
/*    */   
/*    */   public TGSReq(DerValue paramDerValue) throws Asn1Exception, IOException, KrbException
/*    */   {
/* 50 */     init(paramDerValue);
/*    */   }
/*    */   
/*    */   private void init(DerValue paramDerValue) throws Asn1Exception, IOException, KrbException
/*    */   {
/* 55 */     init(paramDerValue, 12);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\TGSReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */