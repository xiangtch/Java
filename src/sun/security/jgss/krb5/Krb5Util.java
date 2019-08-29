/*     */ package sun.security.jgss.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.util.Set;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.security.auth.Subject;
/*     */ import javax.security.auth.kerberos.KerberosPrincipal;
/*     */ import javax.security.auth.kerberos.KerberosTicket;
/*     */ import javax.security.auth.login.LoginException;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.jgss.GSSCaller;
/*     */ import sun.security.jgss.GSSUtil;
/*     */ import sun.security.krb5.Credentials;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.JavaxSecurityAuthKerberosAccess;
/*     */ import sun.security.krb5.KerberosSecrets;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Krb5Util
/*     */ {
/*  53 */   static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.debug")))
/*     */   
/*  55 */     .booleanValue();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static KerberosTicket getTicketFromSubjectAndTgs(GSSCaller paramGSSCaller, String paramString1, String paramString2, String paramString3, AccessControlContext paramAccessControlContext)
/*     */     throws LoginException, KrbException, IOException
/*     */   {
/*  81 */     Subject localSubject1 = Subject.getSubject(paramAccessControlContext);
/*  82 */     KerberosTicket localKerberosTicket1 = (KerberosTicket)SubjectComber.find(localSubject1, paramString2, paramString1, KerberosTicket.class);
/*     */     
/*     */ 
/*  85 */     if (localKerberosTicket1 != null) {
/*  86 */       return localKerberosTicket1;
/*     */     }
/*     */     
/*  89 */     Subject localSubject2 = null;
/*  90 */     if (!GSSUtil.useSubjectCredsOnly(paramGSSCaller)) {
/*     */       try
/*     */       {
/*  93 */         localSubject2 = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
/*  94 */         localKerberosTicket1 = (KerberosTicket)SubjectComber.find(localSubject2, paramString2, paramString1, KerberosTicket.class);
/*     */         
/*  96 */         if (localKerberosTicket1 != null) {
/*  97 */           return localKerberosTicket1;
/*     */         }
/*     */       }
/*     */       catch (LoginException localLoginException) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 109 */     KerberosTicket localKerberosTicket2 = (KerberosTicket)SubjectComber.find(localSubject1, paramString3, paramString1, KerberosTicket.class);
/*     */     
/*     */     int i;
/*     */     
/* 113 */     if ((localKerberosTicket2 == null) && (localSubject2 != null))
/*     */     {
/* 115 */       localKerberosTicket2 = (KerberosTicket)SubjectComber.find(localSubject2, paramString3, paramString1, KerberosTicket.class);
/*     */       
/* 117 */       i = 0;
/*     */     } else {
/* 119 */       i = 1;
/*     */     }
/*     */     
/*     */ 
/* 123 */     if (localKerberosTicket2 != null) {
/* 124 */       Credentials localCredentials1 = ticketToCreds(localKerberosTicket2);
/* 125 */       Credentials localCredentials2 = Credentials.acquireServiceCreds(paramString2, localCredentials1);
/*     */       
/* 127 */       if (localCredentials2 != null) {
/* 128 */         localKerberosTicket1 = credsToTicket(localCredentials2);
/*     */         
/*     */ 
/* 131 */         if ((i != 0) && (localSubject1 != null) && (!localSubject1.isReadOnly())) {
/* 132 */           localSubject1.getPrivateCredentials().add(localKerberosTicket1);
/*     */         }
/*     */       }
/*     */     }
/* 136 */     return localKerberosTicket1;
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
/*     */   static KerberosTicket getTicket(GSSCaller paramGSSCaller, String paramString1, String paramString2, AccessControlContext paramAccessControlContext)
/*     */     throws LoginException
/*     */   {
/* 151 */     Subject localSubject1 = Subject.getSubject(paramAccessControlContext);
/*     */     
/* 153 */     KerberosTicket localKerberosTicket = (KerberosTicket)SubjectComber.find(localSubject1, paramString2, paramString1, KerberosTicket.class);
/*     */     
/*     */ 
/*     */ 
/* 157 */     if ((localKerberosTicket == null) && (!GSSUtil.useSubjectCredsOnly(paramGSSCaller))) {
/* 158 */       Subject localSubject2 = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
/* 159 */       localKerberosTicket = (KerberosTicket)SubjectComber.find(localSubject2, paramString2, paramString1, KerberosTicket.class);
/*     */     }
/*     */     
/* 162 */     return localKerberosTicket;
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
/*     */   public static Subject getSubject(GSSCaller paramGSSCaller, AccessControlContext paramAccessControlContext)
/*     */     throws LoginException
/*     */   {
/* 180 */     Subject localSubject = Subject.getSubject(paramAccessControlContext);
/*     */     
/*     */ 
/* 183 */     if ((localSubject == null) && (!GSSUtil.useSubjectCredsOnly(paramGSSCaller))) {
/* 184 */       localSubject = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
/*     */     }
/* 186 */     return localSubject;
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
/*     */   public static ServiceCreds getServiceCreds(GSSCaller paramGSSCaller, String paramString, AccessControlContext paramAccessControlContext)
/*     */     throws LoginException
/*     */   {
/* 200 */     Subject localSubject1 = Subject.getSubject(paramAccessControlContext);
/* 201 */     ServiceCreds localServiceCreds = null;
/* 202 */     if (localSubject1 != null) {
/* 203 */       localServiceCreds = ServiceCreds.getInstance(localSubject1, paramString);
/*     */     }
/* 205 */     if ((localServiceCreds == null) && (!GSSUtil.useSubjectCredsOnly(paramGSSCaller))) {
/* 206 */       Subject localSubject2 = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
/* 207 */       localServiceCreds = ServiceCreds.getInstance(localSubject2, paramString);
/*     */     }
/* 209 */     return localServiceCreds;
/*     */   }
/*     */   
/*     */   public static KerberosTicket credsToTicket(Credentials paramCredentials) {
/* 213 */     EncryptionKey localEncryptionKey = paramCredentials.getSessionKey();
/* 214 */     return new KerberosTicket(paramCredentials
/* 215 */       .getEncoded(), new KerberosPrincipal(paramCredentials
/* 216 */       .getClient().getName()), new KerberosPrincipal(paramCredentials
/* 217 */       .getServer().getName(), 2), localEncryptionKey
/*     */       
/* 219 */       .getBytes(), localEncryptionKey
/* 220 */       .getEType(), paramCredentials
/* 221 */       .getFlags(), paramCredentials
/* 222 */       .getAuthTime(), paramCredentials
/* 223 */       .getStartTime(), paramCredentials
/* 224 */       .getEndTime(), paramCredentials
/* 225 */       .getRenewTill(), paramCredentials
/* 226 */       .getClientAddresses());
/*     */   }
/*     */   
/*     */   public static Credentials ticketToCreds(KerberosTicket paramKerberosTicket) throws KrbException, IOException
/*     */   {
/* 231 */     return new Credentials(paramKerberosTicket
/* 232 */       .getEncoded(), paramKerberosTicket
/* 233 */       .getClient().getName(), paramKerberosTicket
/* 234 */       .getServer().getName(), paramKerberosTicket
/* 235 */       .getSessionKey().getEncoded(), paramKerberosTicket
/* 236 */       .getSessionKeyType(), paramKerberosTicket
/* 237 */       .getFlags(), paramKerberosTicket
/* 238 */       .getAuthTime(), paramKerberosTicket
/* 239 */       .getStartTime(), paramKerberosTicket
/* 240 */       .getEndTime(), paramKerberosTicket
/* 241 */       .getRenewTill(), paramKerberosTicket
/* 242 */       .getClientAddresses());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static sun.security.krb5.internal.ktab.KeyTab snapshotFromJavaxKeyTab(javax.security.auth.kerberos.KeyTab paramKeyTab)
/*     */   {
/* 252 */     return 
/* 253 */       KerberosSecrets.getJavaxSecurityAuthKerberosAccess().keyTabTakeSnapshot(paramKeyTab);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EncryptionKey[] keysFromJavaxKeyTab(javax.security.auth.kerberos.KeyTab paramKeyTab, PrincipalName paramPrincipalName)
/*     */   {
/* 264 */     return snapshotFromJavaxKeyTab(paramKeyTab).readServiceKeys(paramPrincipalName);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\krb5\Krb5Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */