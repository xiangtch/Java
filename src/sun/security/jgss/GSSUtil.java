/*     */ package sun.security.jgss;
/*     */ 
/*     */ import com.sun.security.auth.callback.TextCallbackHandler;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.Security;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.security.auth.Subject;
/*     */ import javax.security.auth.callback.CallbackHandler;
/*     */ import javax.security.auth.kerberos.KerberosKey;
/*     */ import javax.security.auth.kerberos.KerberosPrincipal;
/*     */ import javax.security.auth.kerberos.KerberosTicket;
/*     */ import javax.security.auth.login.LoginContext;
/*     */ import javax.security.auth.login.LoginException;
/*     */ import org.ietf.jgss.GSSCredential;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.GSSName;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.net.www.protocol.http.spnego.NegotiateCallbackHandler;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.jgss.krb5.Krb5NameElement;
/*     */ import sun.security.jgss.spi.GSSCredentialSpi;
/*     */ import sun.security.jgss.spi.GSSNameSpi;
/*     */ import sun.security.jgss.spnego.SpNegoCredElement;
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
/*     */ public class GSSUtil
/*     */ {
/*  59 */   public static final Oid GSS_KRB5_MECH_OID = createOid("1.2.840.113554.1.2.2");
/*     */   
/*  61 */   public static final Oid GSS_KRB5_MECH_OID2 = createOid("1.3.5.1.5.2");
/*     */   
/*  63 */   public static final Oid GSS_KRB5_MECH_OID_MS = createOid("1.2.840.48018.1.2.2");
/*     */   
/*     */ 
/*  66 */   public static final Oid GSS_SPNEGO_MECH_OID = createOid("1.3.6.1.5.5.2");
/*     */   
/*     */ 
/*  69 */   public static final Oid NT_GSS_KRB5_PRINCIPAL = createOid("1.2.840.113554.1.2.2.1");
/*     */   
/*     */ 
/*     */ 
/*     */   private static final String DEFAULT_HANDLER = "auth.login.defaultCallbackHandler";
/*     */   
/*     */ 
/*     */ 
/*  77 */   static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.jgss.debug")))
/*  78 */     .booleanValue();
/*     */   
/*     */   static void debug(String paramString)
/*     */   {
/*  82 */     if (DEBUG) {
/*  83 */       assert (paramString != null);
/*  84 */       System.out.println(paramString);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static Oid createOid(String paramString)
/*     */   {
/*     */     try
/*     */     {
/*  93 */       return new Oid(paramString);
/*     */     } catch (GSSException localGSSException) {
/*  95 */       debug("Ignored invalid OID: " + paramString); }
/*  96 */     return null;
/*     */   }
/*     */   
/*     */   public static boolean isSpNegoMech(Oid paramOid)
/*     */   {
/* 101 */     return GSS_SPNEGO_MECH_OID.equals(paramOid);
/*     */   }
/*     */   
/*     */   public static boolean isKerberosMech(Oid paramOid) {
/* 105 */     return (GSS_KRB5_MECH_OID.equals(paramOid)) || 
/* 106 */       (GSS_KRB5_MECH_OID2.equals(paramOid)) || 
/* 107 */       (GSS_KRB5_MECH_OID_MS.equals(paramOid));
/*     */   }
/*     */   
/*     */   public static String getMechStr(Oid paramOid)
/*     */   {
/* 112 */     if (isSpNegoMech(paramOid))
/* 113 */       return "SPNEGO";
/* 114 */     if (isKerberosMech(paramOid)) {
/* 115 */       return "Kerberos V5";
/*     */     }
/* 117 */     return paramOid.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Subject getSubject(GSSName paramGSSName, GSSCredential paramGSSCredential)
/*     */   {
/* 129 */     HashSet localHashSet1 = null;
/* 130 */     HashSet localHashSet2 = new HashSet();
/*     */     
/* 132 */     Set localSet = null;
/*     */     
/* 134 */     HashSet localHashSet3 = new HashSet();
/*     */     
/*     */ 
/* 137 */     if ((paramGSSName instanceof GSSNameImpl)) {
/*     */       try
/*     */       {
/* 140 */         GSSNameSpi localGSSNameSpi = ((GSSNameImpl)paramGSSName).getElement(GSS_KRB5_MECH_OID);
/* 141 */         String str = localGSSNameSpi.toString();
/* 142 */         if ((localGSSNameSpi instanceof Krb5NameElement))
/*     */         {
/* 144 */           str = ((Krb5NameElement)localGSSNameSpi).getKrb5PrincipalName().getName();
/*     */         }
/* 146 */         KerberosPrincipal localKerberosPrincipal = new KerberosPrincipal(str);
/* 147 */         localHashSet3.add(localKerberosPrincipal);
/*     */       } catch (GSSException localGSSException) {
/* 149 */         debug("Skipped name " + paramGSSName + " due to " + localGSSException);
/*     */       }
/*     */     }
/*     */     
/* 153 */     if ((paramGSSCredential instanceof GSSCredentialImpl)) {
/* 154 */       localSet = ((GSSCredentialImpl)paramGSSCredential).getElements();
/* 155 */       localHashSet1 = new HashSet(localSet.size());
/* 156 */       populateCredentials(localHashSet1, localSet);
/*     */     } else {
/* 158 */       localHashSet1 = new HashSet();
/*     */     }
/* 160 */     debug("Created Subject with the following");
/* 161 */     debug("principals=" + localHashSet3);
/* 162 */     debug("public creds=" + localHashSet2);
/* 163 */     debug("private creds=" + localHashSet1);
/*     */     
/* 165 */     return new Subject(false, localHashSet3, localHashSet2, localHashSet1);
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
/*     */ 
/*     */   private static void populateCredentials(Set<Object> paramSet, Set<?> paramSet1)
/*     */   {
/* 184 */     Iterator localIterator = paramSet1.iterator();
/* 185 */     while (localIterator.hasNext())
/*     */     {
/* 187 */       Object localObject1 = localIterator.next();
/*     */       
/*     */ 
/* 190 */       if ((localObject1 instanceof SpNegoCredElement)) {
/* 191 */         localObject1 = ((SpNegoCredElement)localObject1).getInternalCred();
/*     */       }
/*     */       Object localObject2;
/* 194 */       if ((localObject1 instanceof KerberosTicket))
/*     */       {
/* 196 */         if (!localObject1.getClass().getName().equals("javax.security.auth.kerberos.KerberosTicket")) {
/* 197 */           localObject2 = (KerberosTicket)localObject1;
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
/* 208 */           localObject1 = new KerberosTicket(((KerberosTicket)localObject2).getEncoded(), ((KerberosTicket)localObject2).getClient(), ((KerberosTicket)localObject2).getServer(), ((KerberosTicket)localObject2).getSessionKey().getEncoded(), ((KerberosTicket)localObject2).getSessionKeyType(), ((KerberosTicket)localObject2).getFlags(), ((KerberosTicket)localObject2).getAuthTime(), ((KerberosTicket)localObject2).getStartTime(), ((KerberosTicket)localObject2).getEndTime(), ((KerberosTicket)localObject2).getRenewTill(), ((KerberosTicket)localObject2).getClientAddresses());
/*     */         }
/* 210 */         paramSet.add(localObject1);
/* 211 */       } else if ((localObject1 instanceof KerberosKey))
/*     */       {
/* 213 */         if (!localObject1.getClass().getName().equals("javax.security.auth.kerberos.KerberosKey")) {
/* 214 */           localObject2 = (KerberosKey)localObject1;
/*     */           
/*     */ 
/*     */ 
/* 218 */           localObject1 = new KerberosKey(((KerberosKey)localObject2).getPrincipal(), ((KerberosKey)localObject2).getEncoded(), ((KerberosKey)localObject2).getKeyType(), ((KerberosKey)localObject2).getVersionNumber());
/*     */         }
/* 220 */         paramSet.add(localObject1);
/*     */       }
/*     */       else {
/* 223 */         debug("Skipped cred element: " + localObject1);
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
/*     */ 
/*     */   public static Subject login(GSSCaller paramGSSCaller, Oid paramOid)
/*     */     throws LoginException
/*     */   {
/* 238 */     Object localObject1 = null;
/* 239 */     if ((paramGSSCaller instanceof HttpCaller))
/*     */     {
/* 241 */       localObject1 = new NegotiateCallbackHandler(((HttpCaller)paramGSSCaller).info());
/*     */     }
/*     */     else {
/* 244 */       localObject2 = Security.getProperty("auth.login.defaultCallbackHandler");
/*     */       
/* 246 */       if ((localObject2 != null) && (((String)localObject2).length() != 0)) {
/* 247 */         localObject1 = null;
/*     */       } else {
/* 249 */         localObject1 = new TextCallbackHandler();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 256 */     Object localObject2 = new LoginContext("", null, (CallbackHandler)localObject1, new LoginConfigImpl(paramGSSCaller, paramOid));
/*     */     
/* 258 */     ((LoginContext)localObject2).login();
/* 259 */     return ((LoginContext)localObject2).getSubject();
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
/*     */   public static boolean useSubjectCredsOnly(GSSCaller paramGSSCaller)
/*     */   {
/* 273 */     String str = GetPropertyAction.privilegedGetProperty("javax.security.auth.useSubjectCredsOnly");
/*     */     
/*     */ 
/*     */ 
/* 277 */     if ((paramGSSCaller instanceof HttpCaller))
/*     */     {
/* 279 */       return "true".equalsIgnoreCase(str);
/*     */     }
/*     */     
/* 282 */     return !"false".equalsIgnoreCase(str);
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
/*     */   public static boolean useMSInterop()
/*     */   {
/* 298 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.spnego.msinterop", "true"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 305 */     return !str.equalsIgnoreCase("false");
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
/*     */   public static <T extends GSSCredentialSpi> Vector<T> searchSubject(final GSSNameSpi paramGSSNameSpi, final Oid paramOid, final boolean paramBoolean, final Class<? extends T> paramClass)
/*     */   {
/* 320 */     debug(
/*     */     
/*     */ 
/* 323 */       "Search Subject for " + getMechStr(paramOid) + (paramBoolean ? " INIT" : " ACCEPT") + " cred (" + (paramGSSNameSpi == null ? "<<DEF>>" : paramGSSNameSpi.toString()) + ", " + paramClass.getName() + ")");
/* 324 */     AccessControlContext localAccessControlContext = AccessController.getContext();
/*     */     
/*     */     try
/*     */     {
/* 328 */       (Vector)AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*     */         public Vector<T> run() throws Exception {
/* 330 */           Subject localSubject = Subject.getSubject(this.val$acc);
/* 331 */           Vector localVector = null;
/* 332 */           if (localSubject != null) {
/* 333 */             localVector = new Vector();
/*     */             
/*     */ 
/* 336 */             Iterator localIterator = localSubject.getPrivateCredentials(GSSCredentialImpl.class).iterator();
/* 337 */             while (localIterator.hasNext()) {
/* 338 */               GSSCredentialImpl localGSSCredentialImpl = (GSSCredentialImpl)localIterator.next();
/* 339 */               GSSUtil.debug("...Found cred" + localGSSCredentialImpl);
/*     */               try
/*     */               {
/* 342 */                 GSSCredentialSpi localGSSCredentialSpi = localGSSCredentialImpl.getElement(paramOid, paramBoolean);
/* 343 */                 GSSUtil.debug("......Found element: " + localGSSCredentialSpi);
/* 344 */                 if ((localGSSCredentialSpi.getClass().equals(paramClass)) && ((paramGSSNameSpi == null) || 
/*     */                 
/* 346 */                   (paramGSSNameSpi.equals(localGSSCredentialSpi.getName())))) {
/* 347 */                   localVector.add(paramClass.cast(localGSSCredentialSpi));
/*     */                 } else {
/* 349 */                   GSSUtil.debug("......Discard element");
/*     */                 }
/*     */               } catch (GSSException localGSSException) {
/* 352 */                 GSSUtil.debug("...Discard cred (" + localGSSException + ")");
/*     */               }
/*     */             }
/* 355 */           } else { GSSUtil.debug("No Subject"); }
/* 356 */           return localVector;
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException) {
/* 361 */       debug("Unexpected exception when searching Subject:");
/* 362 */       if (DEBUG) localPrivilegedActionException.printStackTrace(); }
/* 363 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\GSSUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */