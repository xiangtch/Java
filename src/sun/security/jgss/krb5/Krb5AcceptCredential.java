/*     */ package sun.security.jgss.krb5;
/*     */ 
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.Provider;
/*     */ import javax.security.auth.DestroyFailedException;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.jgss.GSSCaller;
/*     */ import sun.security.jgss.spi.GSSCredentialSpi;
/*     */ import sun.security.jgss.spi.GSSNameSpi;
/*     */ import sun.security.krb5.Credentials;
/*     */ import sun.security.krb5.EncryptionKey;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Krb5AcceptCredential
/*     */   implements Krb5CredElement
/*     */ {
/*     */   private final Krb5NameElement name;
/*     */   private final ServiceCreds screds;
/*     */   
/*     */   private Krb5AcceptCredential(Krb5NameElement paramKrb5NameElement, ServiceCreds paramServiceCreds)
/*     */   {
/*  58 */     this.name = paramKrb5NameElement;
/*  59 */     this.screds = paramServiceCreds;
/*     */   }
/*     */   
/*     */ 
/*     */   static Krb5AcceptCredential getInstance(GSSCaller paramGSSCaller, Krb5NameElement paramKrb5NameElement)
/*     */     throws GSSException
/*     */   {
/*  66 */     final String str1 = paramKrb5NameElement == null ? null : paramKrb5NameElement.getKrb5PrincipalName().getName();
/*  67 */     final AccessControlContext localAccessControlContext = AccessController.getContext();
/*     */     
/*  69 */     ServiceCreds localServiceCreds = null;
/*     */     try {
/*  71 */       localServiceCreds = (ServiceCreds)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public ServiceCreds run() throws Exception {
/*  74 */           return Krb5Util.getServiceCreds(this.val$caller == GSSCaller.CALLER_UNKNOWN ? GSSCaller.CALLER_ACCEPT : this.val$caller, str1, localAccessControlContext);
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException) {
/*  79 */       GSSException localGSSException = new GSSException(13, -1, "Attempt to obtain new ACCEPT credentials failed!");
/*     */       
/*     */ 
/*  82 */       localGSSException.initCause(localPrivilegedActionException.getException());
/*  83 */       throw localGSSException;
/*     */     }
/*     */     
/*  86 */     if (localServiceCreds == null) {
/*  87 */       throw new GSSException(13, -1, "Failed to find any Kerberos credentails");
/*     */     }
/*     */     
/*  90 */     if (paramKrb5NameElement == null) {
/*  91 */       String str2 = localServiceCreds.getName();
/*  92 */       if (str2 != null) {
/*  93 */         paramKrb5NameElement = Krb5NameElement.getInstance(str2, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  98 */     return new Krb5AcceptCredential(paramKrb5NameElement, localServiceCreds);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final GSSNameSpi getName()
/*     */     throws GSSException
/*     */   {
/* 109 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getInitLifetime()
/*     */     throws GSSException
/*     */   {
/* 119 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getAcceptLifetime()
/*     */     throws GSSException
/*     */   {
/* 129 */     return Integer.MAX_VALUE;
/*     */   }
/*     */   
/*     */   public boolean isInitiatorCredential() throws GSSException {
/* 133 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isAcceptorCredential() throws GSSException {
/* 137 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final Oid getMechanism()
/*     */   {
/* 148 */     return Krb5MechFactory.GSS_KRB5_MECH_OID;
/*     */   }
/*     */   
/*     */   public final Provider getProvider() {
/* 152 */     return Krb5MechFactory.PROVIDER;
/*     */   }
/*     */   
/*     */   public EncryptionKey[] getKrb5EncryptionKeys(PrincipalName paramPrincipalName) {
/* 156 */     return this.screds.getEKeys(paramPrincipalName);
/*     */   }
/*     */   
/*     */   public void dispose()
/*     */     throws GSSException
/*     */   {
/*     */     try
/*     */     {
/* 164 */       destroy();
/*     */     }
/*     */     catch (DestroyFailedException localDestroyFailedException)
/*     */     {
/* 168 */       GSSException localGSSException = new GSSException(11, -1, "Could not destroy credentials - " + localDestroyFailedException.getMessage());
/* 169 */       localGSSException.initCause(localDestroyFailedException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */     throws DestroyFailedException
/*     */   {
/* 178 */     this.screds.destroy();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi)
/*     */     throws GSSException
/*     */   {
/* 188 */     Credentials localCredentials = this.screds.getInitCred();
/* 189 */     if (localCredentials != null) {
/* 190 */       return 
/* 191 */         Krb5InitCredential.getInstance(this.name, localCredentials).impersonate(paramGSSNameSpi);
/*     */     }
/* 193 */     throw new GSSException(11, -1, "Only an initiate credentials can impersonate");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\krb5\Krb5AcceptCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */