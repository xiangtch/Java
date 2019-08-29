/*     */ package sun.security.jgss.krb5;
/*     */ 
/*     */ import java.security.Provider;
/*     */ import javax.security.auth.DestroyFailedException;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.jgss.spi.GSSCredentialSpi;
/*     */ import sun.security.jgss.spi.GSSNameSpi;
/*     */ import sun.security.krb5.internal.Ticket;
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
/*     */ public class Krb5ProxyCredential
/*     */   implements Krb5CredElement
/*     */ {
/*     */   public final Krb5InitCredential self;
/*     */   private final Krb5NameElement client;
/*     */   public final Ticket tkt;
/*     */   
/*     */   Krb5ProxyCredential(Krb5InitCredential paramKrb5InitCredential, Krb5NameElement paramKrb5NameElement, Ticket paramTicket)
/*     */   {
/*  54 */     this.self = paramKrb5InitCredential;
/*  55 */     this.tkt = paramTicket;
/*  56 */     this.client = paramKrb5NameElement;
/*     */   }
/*     */   
/*     */   public final Krb5NameElement getName()
/*     */     throws GSSException
/*     */   {
/*  62 */     return this.client;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInitLifetime()
/*     */     throws GSSException
/*     */   {
/*  69 */     return this.self.getInitLifetime();
/*     */   }
/*     */   
/*     */   public int getAcceptLifetime() throws GSSException
/*     */   {
/*  74 */     return 0;
/*     */   }
/*     */   
/*     */   public boolean isInitiatorCredential() throws GSSException
/*     */   {
/*  79 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isAcceptorCredential() throws GSSException
/*     */   {
/*  84 */     return false;
/*     */   }
/*     */   
/*     */   public final Oid getMechanism()
/*     */   {
/*  89 */     return Krb5MechFactory.GSS_KRB5_MECH_OID;
/*     */   }
/*     */   
/*     */   public final Provider getProvider()
/*     */   {
/*  94 */     return Krb5MechFactory.PROVIDER;
/*     */   }
/*     */   
/*     */   public void dispose() throws GSSException
/*     */   {
/*     */     try {
/* 100 */       this.self.destroy();
/*     */     }
/*     */     catch (DestroyFailedException localDestroyFailedException)
/*     */     {
/* 104 */       GSSException localGSSException = new GSSException(11, -1, "Could not destroy credentials - " + localDestroyFailedException.getMessage());
/* 105 */       localGSSException.initCause(localDestroyFailedException);
/*     */     }
/*     */   }
/*     */   
/*     */   public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi)
/*     */     throws GSSException
/*     */   {
/* 112 */     throw new GSSException(11, -1, "Only an initiate credentials can impersonate");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\krb5\Krb5ProxyCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */