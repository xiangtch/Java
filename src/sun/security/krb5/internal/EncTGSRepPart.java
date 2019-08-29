/*    */ package sun.security.krb5.internal;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import sun.security.krb5.Asn1Exception;
/*    */ import sun.security.krb5.EncryptionKey;
/*    */ import sun.security.krb5.KrbException;
/*    */ import sun.security.krb5.PrincipalName;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EncTGSRepPart
/*    */   extends EncKDCRepPart
/*    */ {
/*    */   public EncTGSRepPart(EncryptionKey paramEncryptionKey, LastReq paramLastReq, int paramInt, KerberosTime paramKerberosTime1, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, KerberosTime paramKerberosTime5, PrincipalName paramPrincipalName, HostAddresses paramHostAddresses)
/*    */   {
/* 50 */     super(paramEncryptionKey, paramLastReq, paramInt, paramKerberosTime1, paramTicketFlags, paramKerberosTime2, paramKerberosTime3, paramKerberosTime4, paramKerberosTime5, paramPrincipalName, paramHostAddresses, 26);
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
/*    */ 
/*    */ 
/*    */   public EncTGSRepPart(byte[] paramArrayOfByte)
/*    */     throws Asn1Exception, IOException, KrbException
/*    */   {
/* 67 */     init(new DerValue(paramArrayOfByte));
/*    */   }
/*    */   
/*    */   public EncTGSRepPart(DerValue paramDerValue) throws Asn1Exception, IOException, KrbException
/*    */   {
/* 72 */     init(paramDerValue);
/*    */   }
/*    */   
/*    */   private void init(DerValue paramDerValue) throws Asn1Exception, IOException, KrbException
/*    */   {
/* 77 */     init(paramDerValue, 26);
/*    */   }
/*    */   
/*    */   public byte[] asn1Encode() throws Asn1Exception, IOException
/*    */   {
/* 82 */     return asn1Encode(26);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\EncTGSRepPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */