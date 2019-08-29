/*     */ package sun.security.jgss.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.Provider;
/*     */ import java.util.Date;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.security.auth.DestroyFailedException;
/*     */ import javax.security.auth.kerberos.KerberosPrincipal;
/*     */ import javax.security.auth.kerberos.KerberosTicket;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.jgss.GSSCaller;
/*     */ import sun.security.jgss.spi.GSSCredentialSpi;
/*     */ import sun.security.jgss.spi.GSSNameSpi;
/*     */ import sun.security.krb5.Credentials;
/*     */ import sun.security.krb5.EncryptionKey;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Krb5InitCredential
/*     */   extends KerberosTicket
/*     */   implements Krb5CredElement
/*     */ {
/*     */   private static final long serialVersionUID = 7723415700837898232L;
/*     */   private Krb5NameElement name;
/*     */   private Credentials krb5Credentials;
/*     */   
/*     */   private Krb5InitCredential(Krb5NameElement paramKrb5NameElement, byte[] paramArrayOfByte1, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress)
/*     */     throws GSSException
/*     */   {
/*  72 */     super(paramArrayOfByte1, paramKerberosPrincipal1, paramKerberosPrincipal2, paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  84 */     this.name = paramKrb5NameElement;
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/*  90 */       this.krb5Credentials = new Credentials(paramArrayOfByte1, paramKerberosPrincipal1.getName(), paramKerberosPrincipal2.getName(), paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (KrbException localKrbException)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 101 */       throw new GSSException(13, -1, localKrbException.getMessage());
/*     */     }
/*     */     catch (IOException localIOException) {
/* 104 */       throw new GSSException(13, -1, localIOException.getMessage());
/*     */     }
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
/*     */   private Krb5InitCredential(Krb5NameElement paramKrb5NameElement, Credentials paramCredentials, byte[] paramArrayOfByte1, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress)
/*     */     throws GSSException
/*     */   {
/* 123 */     super(paramArrayOfByte1, paramKerberosPrincipal1, paramKerberosPrincipal2, paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 135 */     this.name = paramKrb5NameElement;
/*     */     
/*     */ 
/* 138 */     this.krb5Credentials = paramCredentials;
/*     */   }
/*     */   
/*     */ 
/*     */   static Krb5InitCredential getInstance(GSSCaller paramGSSCaller, Krb5NameElement paramKrb5NameElement, int paramInt)
/*     */     throws GSSException
/*     */   {
/* 145 */     KerberosTicket localKerberosTicket = getTgt(paramGSSCaller, paramKrb5NameElement, paramInt);
/* 146 */     if (localKerberosTicket == null) {
/* 147 */       throw new GSSException(13, -1, "Failed to find any Kerberos tgt");
/*     */     }
/*     */     
/* 150 */     if (paramKrb5NameElement == null) {
/* 151 */       String str = localKerberosTicket.getClient().getName();
/* 152 */       paramKrb5NameElement = Krb5NameElement.getInstance(str, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
/*     */     }
/*     */     
/*     */ 
/* 156 */     return new Krb5InitCredential(paramKrb5NameElement, localKerberosTicket
/* 157 */       .getEncoded(), localKerberosTicket
/* 158 */       .getClient(), localKerberosTicket
/* 159 */       .getServer(), localKerberosTicket
/* 160 */       .getSessionKey().getEncoded(), localKerberosTicket
/* 161 */       .getSessionKeyType(), localKerberosTicket
/* 162 */       .getFlags(), localKerberosTicket
/* 163 */       .getAuthTime(), localKerberosTicket
/* 164 */       .getStartTime(), localKerberosTicket
/* 165 */       .getEndTime(), localKerberosTicket
/* 166 */       .getRenewTill(), localKerberosTicket
/* 167 */       .getClientAddresses());
/*     */   }
/*     */   
/*     */ 
/*     */   static Krb5InitCredential getInstance(Krb5NameElement paramKrb5NameElement, Credentials paramCredentials)
/*     */     throws GSSException
/*     */   {
/* 174 */     EncryptionKey localEncryptionKey = paramCredentials.getSessionKey();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 181 */     PrincipalName localPrincipalName1 = paramCredentials.getClient();
/* 182 */     PrincipalName localPrincipalName2 = paramCredentials.getServer();
/*     */     
/* 184 */     KerberosPrincipal localKerberosPrincipal1 = null;
/* 185 */     KerberosPrincipal localKerberosPrincipal2 = null;
/*     */     
/* 187 */     Krb5NameElement localKrb5NameElement = null;
/*     */     
/* 189 */     if (localPrincipalName1 != null) {
/* 190 */       String str = localPrincipalName1.getName();
/* 191 */       localKrb5NameElement = Krb5NameElement.getInstance(str, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
/*     */       
/* 193 */       localKerberosPrincipal1 = new KerberosPrincipal(str);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 198 */     if (localPrincipalName2 != null)
/*     */     {
/* 200 */       localKerberosPrincipal2 = new KerberosPrincipal(localPrincipalName2.getName(), 2);
/*     */     }
/*     */     
/*     */ 
/* 204 */     return new Krb5InitCredential(localKrb5NameElement, paramCredentials, paramCredentials
/*     */     
/* 206 */       .getEncoded(), localKerberosPrincipal1, localKerberosPrincipal2, localEncryptionKey
/*     */       
/*     */ 
/* 209 */       .getBytes(), localEncryptionKey
/* 210 */       .getEType(), paramCredentials
/* 211 */       .getFlags(), paramCredentials
/* 212 */       .getAuthTime(), paramCredentials
/* 213 */       .getStartTime(), paramCredentials
/* 214 */       .getEndTime(), paramCredentials
/* 215 */       .getRenewTill(), paramCredentials
/* 216 */       .getClientAddresses());
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
/* 227 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getInitLifetime()
/*     */     throws GSSException
/*     */   {
/* 237 */     int i = 0;
/* 238 */     Date localDate = getEndTime();
/* 239 */     if (localDate == null) {
/* 240 */       return 0;
/*     */     }
/* 242 */     i = (int)(localDate.getTime() - new Date().getTime());
/*     */     
/* 244 */     return i / 1000;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getAcceptLifetime()
/*     */     throws GSSException
/*     */   {
/* 254 */     return 0;
/*     */   }
/*     */   
/*     */   public boolean isInitiatorCredential() throws GSSException {
/* 258 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isAcceptorCredential() throws GSSException {
/* 262 */     return false;
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
/* 273 */     return Krb5MechFactory.GSS_KRB5_MECH_OID;
/*     */   }
/*     */   
/*     */   public final Provider getProvider() {
/* 277 */     return Krb5MechFactory.PROVIDER;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Credentials getKrb5Credentials()
/*     */   {
/* 286 */     return this.krb5Credentials;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispose()
/*     */     throws GSSException
/*     */   {
/*     */     try
/*     */     {
/* 299 */       destroy();
/*     */     }
/*     */     catch (DestroyFailedException localDestroyFailedException)
/*     */     {
/* 303 */       GSSException localGSSException = new GSSException(11, -1, "Could not destroy credentials - " + localDestroyFailedException.getMessage());
/* 304 */       localGSSException.initCause(localDestroyFailedException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static KerberosTicket getTgt(GSSCaller paramGSSCaller, Krb5NameElement paramKrb5NameElement, int paramInt)
/*     */     throws GSSException
/*     */   {
/*     */     final String str;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 321 */     if (paramKrb5NameElement != null) {
/* 322 */       str = paramKrb5NameElement.getKrb5PrincipalName().getName();
/*     */     } else {
/* 324 */       str = null;
/*     */     }
/*     */     
/* 327 */     final AccessControlContext localAccessControlContext = AccessController.getContext();
/*     */     try
/*     */     {
/* 330 */       GSSCaller localGSSCaller = paramGSSCaller == GSSCaller.CALLER_UNKNOWN ? GSSCaller.CALLER_INITIATE : paramGSSCaller;
/*     */       
/*     */ 
/* 333 */       (KerberosTicket)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public KerberosTicket run()
/*     */           throws Exception
/*     */         {
/* 338 */           return Krb5Util.getTicket(this.val$realCaller, str, null, localAccessControlContext);
/*     */         }
/*     */         
/*     */ 
/*     */       });
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException)
/*     */     {
/* 346 */       GSSException localGSSException = new GSSException(13, -1, "Attempt to obtain new INITIATE credentials failed! (" + localPrivilegedActionException.getMessage() + ")");
/* 347 */       localGSSException.initCause(localPrivilegedActionException.getException());
/* 348 */       throw localGSSException;
/*     */     }
/*     */   }
/*     */   
/*     */   public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi) throws GSSException
/*     */   {
/*     */     try {
/* 355 */       Krb5NameElement localKrb5NameElement = (Krb5NameElement)paramGSSNameSpi;
/* 356 */       localObject = Credentials.acquireS4U2selfCreds(localKrb5NameElement
/* 357 */         .getKrb5PrincipalName(), this.krb5Credentials);
/* 358 */       return new Krb5ProxyCredential(this, localKrb5NameElement, ((Credentials)localObject).getTicket());
/*     */     } catch (IOException|KrbException localIOException) {
/* 360 */       Object localObject = new GSSException(11, -1, "Attempt to obtain S4U2self credentials failed!");
/*     */       
/*     */ 
/* 363 */       ((GSSException)localObject).initCause(localIOException);
/* 364 */       throw ((Throwable)localObject);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\krb5\Krb5InitCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */