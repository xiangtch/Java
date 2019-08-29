/*     */ package sun.security.jgss;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Provider;
/*     */ import org.ietf.jgss.GSSContext;
/*     */ import org.ietf.jgss.GSSCredential;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.GSSManager;
/*     */ import org.ietf.jgss.GSSName;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.jgss.spi.GSSContextSpi;
/*     */ import sun.security.jgss.spi.GSSCredentialSpi;
/*     */ import sun.security.jgss.spi.GSSNameSpi;
/*     */ import sun.security.jgss.spi.MechanismFactory;
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
/*     */ public class GSSManagerImpl
/*     */   extends GSSManager
/*     */ {
/*     */   private static final String USE_NATIVE_PROP = "sun.security.jgss.native";
/*  47 */   private static final Boolean USE_NATIVE = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Boolean run() {
/*  49 */       String str = System.getProperty("os.name");
/*  50 */       if ((str.startsWith("SunOS")) || 
/*  51 */         (str.contains("OS X")) || 
/*  52 */         (str.startsWith("Linux"))) {
/*  53 */         return new Boolean(
/*  54 */           System.getProperty("sun.security.jgss.native"));
/*     */       }
/*  56 */       return Boolean.FALSE;
/*     */     }
/*  47 */   });
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ProviderList list;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GSSManagerImpl(GSSCaller paramGSSCaller, boolean paramBoolean)
/*     */   {
/*  66 */     this.list = new ProviderList(paramGSSCaller, paramBoolean);
/*     */   }
/*     */   
/*     */   public GSSManagerImpl(GSSCaller paramGSSCaller)
/*     */   {
/*  71 */     this.list = new ProviderList(paramGSSCaller, USE_NATIVE.booleanValue());
/*     */   }
/*     */   
/*     */   public GSSManagerImpl() {
/*  75 */     this.list = new ProviderList(GSSCaller.CALLER_UNKNOWN, USE_NATIVE.booleanValue());
/*     */   }
/*     */   
/*     */   public Oid[] getMechs() {
/*  79 */     return this.list.getMechs();
/*     */   }
/*     */   
/*     */   public Oid[] getNamesForMech(Oid paramOid) throws GSSException
/*     */   {
/*  84 */     MechanismFactory localMechanismFactory = this.list.getMechFactory(paramOid);
/*  85 */     return (Oid[])localMechanismFactory.getNameTypes().clone();
/*     */   }
/*     */   
/*     */   public Oid[] getMechsForName(Oid paramOid) {
/*  89 */     Oid[] arrayOfOid1 = this.list.getMechs();
/*  90 */     Object localObject = new Oid[arrayOfOid1.length];
/*  91 */     int i = 0;
/*     */     
/*     */ 
/*  94 */     if (paramOid.equals(GSSNameImpl.oldHostbasedServiceName)) {
/*  95 */       paramOid = GSSName.NT_HOSTBASED_SERVICE;
/*     */     }
/*     */     
/*     */ 
/*  99 */     for (int j = 0; j < arrayOfOid1.length; j++)
/*     */     {
/* 101 */       Oid localOid = arrayOfOid1[j];
/*     */       try {
/* 103 */         Oid[] arrayOfOid3 = getNamesForMech(localOid);
/*     */         
/* 105 */         if (paramOid.containedIn(arrayOfOid3)) {
/* 106 */           localObject[(i++)] = localOid;
/*     */         }
/*     */       }
/*     */       catch (GSSException localGSSException) {
/* 110 */         GSSUtil.debug("Skip " + localOid + ": error retrieving supported name types");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 116 */     if (i < localObject.length) {
/* 117 */       Oid[] arrayOfOid2 = new Oid[i];
/* 118 */       for (int k = 0; k < i; k++)
/* 119 */         arrayOfOid2[k] = localObject[k];
/* 120 */       localObject = arrayOfOid2;
/*     */     }
/*     */     
/* 123 */     return (Oid[])localObject;
/*     */   }
/*     */   
/*     */   public GSSName createName(String paramString, Oid paramOid) throws GSSException
/*     */   {
/* 128 */     return new GSSNameImpl(this, paramString, paramOid);
/*     */   }
/*     */   
/*     */   public GSSName createName(byte[] paramArrayOfByte, Oid paramOid) throws GSSException
/*     */   {
/* 133 */     return new GSSNameImpl(this, paramArrayOfByte, paramOid);
/*     */   }
/*     */   
/*     */   public GSSName createName(String paramString, Oid paramOid1, Oid paramOid2) throws GSSException
/*     */   {
/* 138 */     return new GSSNameImpl(this, paramString, paramOid1, paramOid2);
/*     */   }
/*     */   
/*     */   public GSSName createName(byte[] paramArrayOfByte, Oid paramOid1, Oid paramOid2) throws GSSException
/*     */   {
/* 143 */     return new GSSNameImpl(this, paramArrayOfByte, paramOid1, paramOid2);
/*     */   }
/*     */   
/*     */   public GSSCredential createCredential(int paramInt) throws GSSException
/*     */   {
/* 148 */     return new GSSCredentialImpl(this, paramInt);
/*     */   }
/*     */   
/*     */   public GSSCredential createCredential(GSSName paramGSSName, int paramInt1, Oid paramOid, int paramInt2)
/*     */     throws GSSException
/*     */   {
/* 154 */     return new GSSCredentialImpl(this, paramGSSName, paramInt1, paramOid, paramInt2);
/*     */   }
/*     */   
/*     */   public GSSCredential createCredential(GSSName paramGSSName, int paramInt1, Oid[] paramArrayOfOid, int paramInt2)
/*     */     throws GSSException
/*     */   {
/* 160 */     return new GSSCredentialImpl(this, paramGSSName, paramInt1, paramArrayOfOid, paramInt2);
/*     */   }
/*     */   
/*     */   public GSSContext createContext(GSSName paramGSSName, Oid paramOid, GSSCredential paramGSSCredential, int paramInt)
/*     */     throws GSSException
/*     */   {
/* 166 */     return new GSSContextImpl(this, paramGSSName, paramOid, paramGSSCredential, paramInt);
/*     */   }
/*     */   
/*     */   public GSSContext createContext(GSSCredential paramGSSCredential) throws GSSException
/*     */   {
/* 171 */     return new GSSContextImpl(this, paramGSSCredential);
/*     */   }
/*     */   
/*     */   public GSSContext createContext(byte[] paramArrayOfByte) throws GSSException
/*     */   {
/* 176 */     return new GSSContextImpl(this, paramArrayOfByte);
/*     */   }
/*     */   
/*     */   public void addProviderAtFront(Provider paramProvider, Oid paramOid) throws GSSException
/*     */   {
/* 181 */     this.list.addProviderAtFront(paramProvider, paramOid);
/*     */   }
/*     */   
/*     */   public void addProviderAtEnd(Provider paramProvider, Oid paramOid) throws GSSException
/*     */   {
/* 186 */     this.list.addProviderAtEnd(paramProvider, paramOid);
/*     */   }
/*     */   
/*     */   public GSSCredentialSpi getCredentialElement(GSSNameSpi paramGSSNameSpi, int paramInt1, int paramInt2, Oid paramOid, int paramInt3)
/*     */     throws GSSException
/*     */   {
/* 192 */     MechanismFactory localMechanismFactory = this.list.getMechFactory(paramOid);
/* 193 */     return localMechanismFactory.getCredentialElement(paramGSSNameSpi, paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public GSSNameSpi getNameElement(String paramString, Oid paramOid1, Oid paramOid2)
/*     */     throws GSSException
/*     */   {
/* 202 */     MechanismFactory localMechanismFactory = this.list.getMechFactory(paramOid2);
/* 203 */     return localMechanismFactory.getNameElement(paramString, paramOid1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public GSSNameSpi getNameElement(byte[] paramArrayOfByte, Oid paramOid1, Oid paramOid2)
/*     */     throws GSSException
/*     */   {
/* 211 */     MechanismFactory localMechanismFactory = this.list.getMechFactory(paramOid2);
/* 212 */     return localMechanismFactory.getNameElement(paramArrayOfByte, paramOid1);
/*     */   }
/*     */   
/*     */ 
/*     */   GSSContextSpi getMechanismContext(GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt, Oid paramOid)
/*     */     throws GSSException
/*     */   {
/* 219 */     Provider localProvider = null;
/* 220 */     if (paramGSSCredentialSpi != null) {
/* 221 */       localProvider = paramGSSCredentialSpi.getProvider();
/*     */     }
/* 223 */     MechanismFactory localMechanismFactory = this.list.getMechFactory(paramOid, localProvider);
/* 224 */     return localMechanismFactory.getMechanismContext(paramGSSNameSpi, paramGSSCredentialSpi, paramInt);
/*     */   }
/*     */   
/*     */   GSSContextSpi getMechanismContext(GSSCredentialSpi paramGSSCredentialSpi, Oid paramOid)
/*     */     throws GSSException
/*     */   {
/* 230 */     Provider localProvider = null;
/* 231 */     if (paramGSSCredentialSpi != null) {
/* 232 */       localProvider = paramGSSCredentialSpi.getProvider();
/*     */     }
/* 234 */     MechanismFactory localMechanismFactory = this.list.getMechFactory(paramOid, localProvider);
/* 235 */     return localMechanismFactory.getMechanismContext(paramGSSCredentialSpi);
/*     */   }
/*     */   
/*     */   GSSContextSpi getMechanismContext(byte[] paramArrayOfByte) throws GSSException
/*     */   {
/* 240 */     if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
/* 241 */       throw new GSSException(12);
/*     */     }
/* 243 */     GSSContextSpi localGSSContextSpi = null;
/*     */     
/*     */ 
/*     */ 
/* 247 */     Oid[] arrayOfOid = this.list.getMechs();
/* 248 */     for (int i = 0; i < arrayOfOid.length; i++) {
/* 249 */       MechanismFactory localMechanismFactory = this.list.getMechFactory(arrayOfOid[i]);
/* 250 */       if (localMechanismFactory.getProvider().getName().equals("SunNativeGSS")) {
/* 251 */         localGSSContextSpi = localMechanismFactory.getMechanismContext(paramArrayOfByte);
/* 252 */         if (localGSSContextSpi != null) break;
/*     */       }
/*     */     }
/* 255 */     if (localGSSContextSpi == null) {
/* 256 */       throw new GSSException(16);
/*     */     }
/* 258 */     return localGSSContextSpi;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\GSSManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */