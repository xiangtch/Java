/*     */ package sun.security.jgss.wrapper;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.Provider;
/*     */ import java.util.Vector;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.jgss.GSSCaller;
/*     */ import sun.security.jgss.GSSExceptionImpl;
/*     */ import sun.security.jgss.GSSUtil;
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
/*     */ public final class NativeGSSFactory
/*     */   implements MechanismFactory
/*     */ {
/*  45 */   GSSLibStub cStub = null;
/*     */   private final GSSCaller caller;
/*     */   
/*     */   private GSSCredElement getCredFromSubject(GSSNameElement paramGSSNameElement, boolean paramBoolean)
/*     */     throws GSSException
/*     */   {
/*  51 */     Oid localOid = this.cStub.getMech();
/*     */     
/*  53 */     Vector localVector = GSSUtil.searchSubject(paramGSSNameElement, localOid, paramBoolean, GSSCredElement.class);
/*     */     
/*     */ 
/*  56 */     if ((localVector != null) && (localVector.isEmpty()) && 
/*  57 */       (GSSUtil.useSubjectCredsOnly(this.caller))) {
/*  58 */       throw new GSSException(13);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  63 */     GSSCredElement localGSSCredElement = (localVector == null) || (localVector.isEmpty()) ? null : (GSSCredElement)localVector.firstElement();
/*     */     
/*  65 */     if (localGSSCredElement != null) {
/*  66 */       localGSSCredElement.doServicePermCheck();
/*     */     }
/*  68 */     return localGSSCredElement;
/*     */   }
/*     */   
/*     */   public NativeGSSFactory(GSSCaller paramGSSCaller) {
/*  72 */     this.caller = paramGSSCaller;
/*     */   }
/*     */   
/*     */   public void setMech(Oid paramOid)
/*     */     throws GSSException
/*     */   {
/*  78 */     this.cStub = GSSLibStub.getInstance(paramOid);
/*     */   }
/*     */   
/*     */   public GSSNameSpi getNameElement(String paramString, Oid paramOid) throws GSSException
/*     */   {
/*     */     try
/*     */     {
/*  85 */       byte[] arrayOfByte = paramString == null ? null : paramString.getBytes("UTF-8");
/*  86 */       return new GSSNameElement(arrayOfByte, paramOid, this.cStub);
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*  89 */       throw new GSSExceptionImpl(11, localUnsupportedEncodingException);
/*     */     }
/*     */   }
/*     */   
/*     */   public GSSNameSpi getNameElement(byte[] paramArrayOfByte, Oid paramOid) throws GSSException
/*     */   {
/*  95 */     return new GSSNameElement(paramArrayOfByte, paramOid, this.cStub);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public GSSCredentialSpi getCredentialElement(GSSNameSpi paramGSSNameSpi, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws GSSException
/*     */   {
/* 103 */     GSSNameElement localGSSNameElement = null;
/* 104 */     if ((paramGSSNameSpi != null) && (!(paramGSSNameSpi instanceof GSSNameElement)))
/*     */     {
/* 106 */       localGSSNameElement = (GSSNameElement)getNameElement(paramGSSNameSpi.toString(), paramGSSNameSpi.getStringNameType()); } else {
/* 107 */       localGSSNameElement = (GSSNameElement)paramGSSNameSpi;
/*     */     }
/* 109 */     if (paramInt3 == 0)
/*     */     {
/*     */ 
/* 112 */       paramInt3 = 1;
/*     */     }
/*     */     
/*     */ 
/* 116 */     GSSCredElement localGSSCredElement = getCredFromSubject(localGSSNameElement, paramInt3 == 1);
/*     */     
/* 118 */     if (localGSSCredElement == null)
/*     */     {
/* 120 */       if (paramInt3 == 1) {
/* 121 */         localGSSCredElement = new GSSCredElement(localGSSNameElement, paramInt1, paramInt3, this.cStub);
/*     */       }
/* 123 */       else if (paramInt3 == 2) {
/* 124 */         if (localGSSNameElement == null) {
/* 125 */           localGSSNameElement = GSSNameElement.DEF_ACCEPTOR;
/*     */         }
/* 127 */         localGSSCredElement = new GSSCredElement(localGSSNameElement, paramInt2, paramInt3, this.cStub);
/*     */       }
/*     */       else {
/* 130 */         throw new GSSException(11, -1, "Unknown usage mode requested");
/*     */       }
/*     */     }
/*     */     
/* 134 */     return localGSSCredElement;
/*     */   }
/*     */   
/*     */ 
/*     */   public GSSContextSpi getMechanismContext(GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt)
/*     */     throws GSSException
/*     */   {
/* 141 */     if (paramGSSNameSpi == null)
/* 142 */       throw new GSSException(3);
/* 143 */     if (!(paramGSSNameSpi instanceof GSSNameElement))
/*     */     {
/* 145 */       paramGSSNameSpi = (GSSNameElement)getNameElement(paramGSSNameSpi.toString(), paramGSSNameSpi.getStringNameType());
/*     */     }
/* 147 */     if (paramGSSCredentialSpi == null) {
/* 148 */       paramGSSCredentialSpi = getCredFromSubject(null, true);
/* 149 */     } else if (!(paramGSSCredentialSpi instanceof GSSCredElement)) {
/* 150 */       throw new GSSException(13);
/*     */     }
/* 152 */     return new NativeGSSContext((GSSNameElement)paramGSSNameSpi, (GSSCredElement)paramGSSCredentialSpi, paramInt, this.cStub);
/*     */   }
/*     */   
/*     */ 
/*     */   public GSSContextSpi getMechanismContext(GSSCredentialSpi paramGSSCredentialSpi)
/*     */     throws GSSException
/*     */   {
/* 159 */     if (paramGSSCredentialSpi == null) {
/* 160 */       paramGSSCredentialSpi = getCredFromSubject(null, false);
/* 161 */     } else if (!(paramGSSCredentialSpi instanceof GSSCredElement)) {
/* 162 */       throw new GSSException(13);
/*     */     }
/* 164 */     return new NativeGSSContext((GSSCredElement)paramGSSCredentialSpi, this.cStub);
/*     */   }
/*     */   
/*     */   public GSSContextSpi getMechanismContext(byte[] paramArrayOfByte) throws GSSException
/*     */   {
/* 169 */     return this.cStub.importContext(paramArrayOfByte);
/*     */   }
/*     */   
/*     */   public final Oid getMechanismOid() {
/* 173 */     return this.cStub.getMech();
/*     */   }
/*     */   
/*     */   public Provider getProvider() {
/* 177 */     return SunNativeProvider.INSTANCE;
/*     */   }
/*     */   
/*     */   public Oid[] getNameTypes() throws GSSException {
/* 181 */     return this.cStub.inquireNamesForMech();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\wrapper\NativeGSSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */