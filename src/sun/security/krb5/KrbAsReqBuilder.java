/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import javax.security.auth.kerberos.KeyTab;
/*     */ import sun.security.jgss.krb5.Krb5Util;
/*     */ import sun.security.krb5.internal.HostAddresses;
/*     */ import sun.security.krb5.internal.KDCOptions;
/*     */ import sun.security.krb5.internal.KRBError;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.PAData;
/*     */ import sun.security.krb5.internal.PAData.SaltAndParams;
/*     */ import sun.security.krb5.internal.crypto.EType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class KrbAsReqBuilder
/*     */ {
/*     */   private KDCOptions options;
/*     */   private PrincipalName cname;
/*     */   private PrincipalName sname;
/*     */   private KerberosTime from;
/*     */   private KerberosTime till;
/*     */   private KerberosTime rtime;
/*     */   private HostAddresses addresses;
/*     */   private final char[] password;
/*     */   private final KeyTab ktab;
/*     */   private PAData[] paList;
/*     */   private KrbAsReq req;
/*     */   private KrbAsRep rep;
/*     */   private State state;
/*     */   
/*     */   private static enum State
/*     */   {
/*  93 */     INIT, 
/*  94 */     REQ_OK, 
/*  95 */     DESTROYED;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */   
/*     */   private void init(PrincipalName paramPrincipalName) throws KrbException
/*     */   {
/* 102 */     this.cname = paramPrincipalName;
/* 103 */     this.state = State.INIT;
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
/*     */   public KrbAsReqBuilder(PrincipalName paramPrincipalName, KeyTab paramKeyTab)
/*     */     throws KrbException
/*     */   {
/* 119 */     init(paramPrincipalName);
/* 120 */     this.ktab = paramKeyTab;
/* 121 */     this.password = null;
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
/*     */   public KrbAsReqBuilder(PrincipalName paramPrincipalName, char[] paramArrayOfChar)
/*     */     throws KrbException
/*     */   {
/* 137 */     init(paramPrincipalName);
/* 138 */     this.password = ((char[])paramArrayOfChar.clone());
/* 139 */     this.ktab = null;
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
/*     */   public EncryptionKey[] getKeys(boolean paramBoolean)
/*     */     throws KrbException
/*     */   {
/* 155 */     checkState(paramBoolean ? State.REQ_OK : State.INIT, "Cannot get keys");
/* 156 */     if (this.password != null) {
/* 157 */       int[] arrayOfInt = EType.getDefaults("default_tkt_enctypes");
/* 158 */       EncryptionKey[] arrayOfEncryptionKey = new EncryptionKey[arrayOfInt.length];
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 176 */       String str = null;
/*     */       try {
/* 178 */         for (int i = 0; i < arrayOfInt.length; i++)
/*     */         {
/*     */ 
/* 181 */           localObject = PAData.getSaltAndParams(arrayOfInt[i], this.paList);
/* 182 */           if (localObject != null)
/*     */           {
/*     */ 
/* 185 */             if ((arrayOfInt[i] != 23) && (((SaltAndParams)localObject).salt != null))
/*     */             {
/* 187 */               str = ((SaltAndParams)localObject).salt;
/*     */             }
/* 189 */             arrayOfEncryptionKey[i] = EncryptionKey.acquireSecretKey(this.cname, this.password, arrayOfInt[i], (SaltAndParams)localObject);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 196 */         if (str == null) str = this.cname.getSalt();
/* 197 */         for (i = 0; i < arrayOfInt.length; i++)
/*     */         {
/* 199 */           if (arrayOfEncryptionKey[i] == null) {
/* 200 */             arrayOfEncryptionKey[i] = EncryptionKey.acquireSecretKey(this.password, str, arrayOfInt[i], null);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 207 */         Object localObject = new KrbException(909);
/* 208 */         ((KrbException)localObject).initCause(localIOException);
/* 209 */         throw ((Throwable)localObject);
/*     */       }
/* 211 */       return arrayOfEncryptionKey;
/*     */     }
/* 213 */     throw new IllegalStateException("Required password not provided");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setOptions(KDCOptions paramKDCOptions)
/*     */   {
/* 223 */     checkState(State.INIT, "Cannot specify options");
/* 224 */     this.options = paramKDCOptions;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTarget(PrincipalName paramPrincipalName)
/*     */   {
/* 233 */     checkState(State.INIT, "Cannot specify target");
/* 234 */     this.sname = paramPrincipalName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAddresses(HostAddresses paramHostAddresses)
/*     */   {
/* 243 */     checkState(State.INIT, "Cannot specify addresses");
/* 244 */     this.addresses = paramHostAddresses;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private KrbAsReq build(EncryptionKey paramEncryptionKey)
/*     */     throws KrbException, IOException
/*     */   {
/*     */     int[] arrayOfInt;
/*     */     
/*     */ 
/*     */ 
/* 257 */     if (this.password != null) {
/* 258 */       arrayOfInt = EType.getDefaults("default_tkt_enctypes");
/*     */     } else {
/* 260 */       EncryptionKey[] arrayOfEncryptionKey1 = Krb5Util.keysFromJavaxKeyTab(this.ktab, this.cname);
/* 261 */       arrayOfInt = EType.getDefaults("default_tkt_enctypes", arrayOfEncryptionKey1);
/*     */       
/* 263 */       for (EncryptionKey localEncryptionKey : arrayOfEncryptionKey1) localEncryptionKey.destroy();
/*     */     }
/* 265 */     return new KrbAsReq(paramEncryptionKey, this.options, this.cname, this.sname, this.from, this.till, this.rtime, arrayOfInt, this.addresses);
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
/*     */   private KrbAsReqBuilder resolve()
/*     */     throws KrbException, Asn1Exception, IOException
/*     */   {
/* 284 */     if (this.ktab != null) {
/* 285 */       this.rep.decryptUsingKeyTab(this.ktab, this.req, this.cname);
/*     */     } else {
/* 287 */       this.rep.decryptUsingPassword(this.password, this.req, this.cname);
/*     */     }
/* 289 */     if (this.rep.getPA() != null) {
/* 290 */       if ((this.paList == null) || (this.paList.length == 0)) {
/* 291 */         this.paList = this.rep.getPA();
/*     */       } else {
/* 293 */         int i = this.rep.getPA().length;
/* 294 */         if (i > 0) {
/* 295 */           int j = this.paList.length;
/* 296 */           this.paList = ((PAData[])Arrays.copyOf(this.paList, this.paList.length + i));
/* 297 */           System.arraycopy(this.rep.getPA(), 0, this.paList, j, i);
/*     */         }
/*     */       }
/*     */     }
/* 301 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private KrbAsReqBuilder send()
/*     */     throws KrbException, IOException
/*     */   {
/* 310 */     int i = 0;
/* 311 */     KdcComm localKdcComm = new KdcComm(this.cname.getRealmAsString());
/* 312 */     EncryptionKey localEncryptionKey1 = null;
/*     */     for (;;) {
/*     */       try {
/* 315 */         this.req = build(localEncryptionKey1);
/* 316 */         this.rep = new KrbAsRep(localKdcComm.send(this.req.encoding()));
/* 317 */         return this;
/*     */       } catch (KrbException localKrbException) {
/* 319 */         if ((i == 0) && (
/* 320 */           (localKrbException.returnCode() == 24) || 
/* 321 */           (localKrbException.returnCode() == 25))) {
/* 322 */           if (Krb5.DEBUG) {
/* 323 */             System.out.println("KrbAsReqBuilder: PREAUTH FAILED/REQ, re-send AS-REQ");
/*     */           }
/*     */           
/* 326 */           i = 1;
/* 327 */           KRBError localKRBError = localKrbException.getError();
/* 328 */           int j = PAData.getPreferredEType(localKRBError.getPA(), 
/* 329 */             EType.getDefaults("default_tkt_enctypes")[0]);
/* 330 */           if (this.password == null) {
/* 331 */             EncryptionKey[] arrayOfEncryptionKey1 = Krb5Util.keysFromJavaxKeyTab(this.ktab, this.cname);
/* 332 */             localEncryptionKey1 = EncryptionKey.findKey(j, arrayOfEncryptionKey1);
/* 333 */             if (localEncryptionKey1 != null) localEncryptionKey1 = (EncryptionKey)localEncryptionKey1.clone();
/* 334 */             EncryptionKey[] arrayOfEncryptionKey2 = arrayOfEncryptionKey1;int k = arrayOfEncryptionKey2.length;int m = 0; if (m < k) { EncryptionKey localEncryptionKey2 = arrayOfEncryptionKey2[m];localEncryptionKey2.destroy();m++; continue;
/*     */             }
/* 336 */           } else { localEncryptionKey1 = EncryptionKey.acquireSecretKey(this.cname, this.password, j, 
/*     */             
/*     */ 
/* 339 */               PAData.getSaltAndParams(j, localKRBError
/* 340 */               .getPA()));
/*     */           }
/* 342 */           this.paList = localKRBError.getPA();
/*     */         } else {
/* 344 */           throw localKrbException;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public KrbAsReqBuilder action()
/*     */     throws KrbException, Asn1Exception, IOException
/*     */   {
/* 359 */     checkState(State.INIT, "Cannot call action");
/* 360 */     this.state = State.REQ_OK;
/* 361 */     return send().resolve();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Credentials getCreds()
/*     */   {
/* 368 */     checkState(State.REQ_OK, "Cannot retrieve creds");
/* 369 */     return this.rep.getCreds();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public sun.security.krb5.internal.ccache.Credentials getCCreds()
/*     */   {
/* 376 */     checkState(State.REQ_OK, "Cannot retrieve CCreds");
/* 377 */     return this.rep.getCCreds();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 384 */     this.state = State.DESTROYED;
/* 385 */     if (this.password != null) {
/* 386 */       Arrays.fill(this.password, '\000');
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkState(State paramState, String paramString)
/*     */   {
/* 397 */     if (this.state != paramState) {
/* 398 */       throw new IllegalStateException(paramString + " at " + paramState + " state");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\KrbAsReqBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */