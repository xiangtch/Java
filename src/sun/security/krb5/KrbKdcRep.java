/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import sun.security.krb5.internal.EncKDCRepPart;
/*     */ import sun.security.krb5.internal.HostAddresses;
/*     */ import sun.security.krb5.internal.KDCOptions;
/*     */ import sun.security.krb5.internal.KDCRep;
/*     */ import sun.security.krb5.internal.KDCReq;
/*     */ import sun.security.krb5.internal.KDCReqBody;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.KrbApErrException;
/*     */ import sun.security.krb5.internal.TicketFlags;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class KrbKdcRep
/*     */ {
/*     */   static void check(boolean paramBoolean, KDCReq paramKDCReq, KDCRep paramKDCRep)
/*     */     throws KrbApErrException
/*     */   {
/*  43 */     if ((paramBoolean) && (!paramKDCReq.reqBody.cname.equals(paramKDCRep.cname))) {
/*  44 */       paramKDCRep.encKDCRepPart.key.destroy();
/*  45 */       throw new KrbApErrException(41);
/*     */     }
/*     */     
/*  48 */     if (!paramKDCReq.reqBody.sname.equals(paramKDCRep.encKDCRepPart.sname)) {
/*  49 */       paramKDCRep.encKDCRepPart.key.destroy();
/*  50 */       throw new KrbApErrException(41);
/*     */     }
/*     */     
/*  53 */     if (paramKDCReq.reqBody.getNonce() != paramKDCRep.encKDCRepPart.nonce) {
/*  54 */       paramKDCRep.encKDCRepPart.key.destroy();
/*  55 */       throw new KrbApErrException(41);
/*     */     }
/*     */     
/*  58 */     if ((paramKDCReq.reqBody.addresses != null) && (paramKDCRep.encKDCRepPart.caddr != null))
/*     */     {
/*  60 */       if (!paramKDCReq.reqBody.addresses.equals(paramKDCRep.encKDCRepPart.caddr)) {
/*  61 */         paramKDCRep.encKDCRepPart.key.destroy();
/*  62 */         throw new KrbApErrException(41);
/*     */       }
/*     */     }
/*     */     
/*  66 */     for (int i = 2; i < 6; i++)
/*     */     {
/*  68 */       if (paramKDCReq.reqBody.kdcOptions.get(i) != paramKDCRep.encKDCRepPart.flags.get(i)) {
/*  69 */         if (Krb5.DEBUG) {
/*  70 */           System.out.println("> KrbKdcRep.check: at #" + i + ". request for " + paramKDCReq.reqBody.kdcOptions
/*  71 */             .get(i) + ", received " + paramKDCRep.encKDCRepPart.flags
/*  72 */             .get(i));
/*     */         }
/*  74 */         throw new KrbApErrException(41);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  81 */     if (paramKDCReq.reqBody.kdcOptions.get(8) != paramKDCRep.encKDCRepPart.flags.get(8)) {
/*  82 */       throw new KrbApErrException(41);
/*     */     }
/*  84 */     if ((paramKDCReq.reqBody.from == null) || (paramKDCReq.reqBody.from.isZero()))
/*     */     {
/*  86 */       if ((paramKDCRep.encKDCRepPart.starttime != null) && 
/*  87 */         (!paramKDCRep.encKDCRepPart.starttime.inClockSkew())) {
/*  88 */         paramKDCRep.encKDCRepPart.key.destroy();
/*  89 */         throw new KrbApErrException(37);
/*     */       }
/*     */     }
/*  92 */     if ((paramKDCReq.reqBody.from != null) && (!paramKDCReq.reqBody.from.isZero()))
/*     */     {
/*  94 */       if ((paramKDCRep.encKDCRepPart.starttime != null) && 
/*  95 */         (!paramKDCReq.reqBody.from.equals(paramKDCRep.encKDCRepPart.starttime))) {
/*  96 */         paramKDCRep.encKDCRepPart.key.destroy();
/*  97 */         throw new KrbApErrException(41);
/*     */       }
/*     */     }
/* 100 */     if ((!paramKDCReq.reqBody.till.isZero()) && 
/* 101 */       (paramKDCRep.encKDCRepPart.endtime.greaterThan(paramKDCReq.reqBody.till))) {
/* 102 */       paramKDCRep.encKDCRepPart.key.destroy();
/* 103 */       throw new KrbApErrException(41);
/*     */     }
/*     */     
/* 106 */     if ((paramKDCReq.reqBody.kdcOptions.get(8)) && 
/* 107 */       (paramKDCReq.reqBody.rtime != null) && (!paramKDCReq.reqBody.rtime.isZero()))
/*     */     {
/* 109 */       if ((paramKDCRep.encKDCRepPart.renewTill == null) || 
/* 110 */         (paramKDCRep.encKDCRepPart.renewTill.greaterThan(paramKDCReq.reqBody.rtime)))
/*     */       {
/* 112 */         paramKDCRep.encKDCRepPart.key.destroy();
/* 113 */         throw new KrbApErrException(41);
/*     */       }
/*     */     }
/* 116 */     if ((paramKDCReq.reqBody.kdcOptions.get(27)) && 
/* 117 */       (paramKDCRep.encKDCRepPart.flags.get(8)) && 
/* 118 */       (!paramKDCReq.reqBody.till.isZero()))
/*     */     {
/* 120 */       if ((paramKDCRep.encKDCRepPart.renewTill == null) || 
/* 121 */         (paramKDCRep.encKDCRepPart.renewTill.greaterThan(paramKDCReq.reqBody.till)))
/*     */       {
/* 123 */         paramKDCRep.encKDCRepPart.key.destroy();
/* 124 */         throw new KrbApErrException(41);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\KrbKdcRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */