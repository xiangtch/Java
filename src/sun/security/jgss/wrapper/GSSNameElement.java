/*     */ package sun.security.jgss.wrapper;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.Provider;
/*     */ import javax.security.auth.kerberos.ServicePermission;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.GSSName;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.jgss.GSSExceptionImpl;
/*     */ import sun.security.jgss.GSSUtil;
/*     */ import sun.security.jgss.spi.GSSNameSpi;
/*     */ import sun.security.krb5.Realm;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GSSNameElement
/*     */   implements GSSNameSpi
/*     */ {
/*  53 */   long pName = 0L;
/*     */   
/*     */   private String printableName;
/*     */   private Oid printableType;
/*     */   private GSSLibStub cStub;
/*  58 */   static final GSSNameElement DEF_ACCEPTOR = new GSSNameElement();
/*     */   
/*     */   private static Oid getNativeNameType(Oid paramOid, GSSLibStub paramGSSLibStub) {
/*  61 */     if (GSSUtil.NT_GSS_KRB5_PRINCIPAL.equals(paramOid)) {
/*  62 */       Oid[] arrayOfOid = null;
/*     */       try {
/*  64 */         arrayOfOid = paramGSSLibStub.inquireNamesForMech();
/*     */       } catch (GSSException localGSSException1) {
/*  66 */         if ((localGSSException1.getMajor() == 2) && 
/*  67 */           (GSSUtil.isSpNegoMech(paramGSSLibStub.getMech())))
/*     */         {
/*     */           try
/*     */           {
/*  71 */             paramGSSLibStub = GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID);
/*  72 */             arrayOfOid = paramGSSLibStub.inquireNamesForMech();
/*     */           }
/*     */           catch (GSSException localGSSException2) {
/*  75 */             SunNativeProvider.debug(
/*  76 */               "Name type list unavailable: " + localGSSException2.getMajorString());
/*     */           }
/*     */         } else {
/*  79 */           SunNativeProvider.debug(
/*  80 */             "Name type list unavailable: " + localGSSException1.getMajorString());
/*     */         }
/*     */       }
/*  83 */       if (arrayOfOid != null) {
/*  84 */         for (int i = 0; i < arrayOfOid.length; i++) {
/*  85 */           if (arrayOfOid[i].equals(paramOid)) { return paramOid;
/*     */           }
/*     */         }
/*  88 */         SunNativeProvider.debug("Override " + paramOid + " with mechanism default(null)");
/*     */         
/*  90 */         return null;
/*     */       }
/*     */     }
/*  93 */     return paramOid;
/*     */   }
/*     */   
/*     */   private GSSNameElement() {
/*  97 */     this.printableName = "<DEFAULT ACCEPTOR>";
/*     */   }
/*     */   
/*     */   GSSNameElement(long paramLong, GSSLibStub paramGSSLibStub) throws GSSException {
/* 101 */     assert (paramGSSLibStub != null);
/* 102 */     if (paramLong == 0L) {
/* 103 */       throw new GSSException(3);
/*     */     }
/*     */     
/* 106 */     this.pName = paramLong;
/* 107 */     this.cStub = paramGSSLibStub;
/* 108 */     setPrintables();
/*     */   }
/*     */   
/*     */   GSSNameElement(byte[] paramArrayOfByte, Oid paramOid, GSSLibStub paramGSSLibStub) throws GSSException
/*     */   {
/* 113 */     assert (paramGSSLibStub != null);
/* 114 */     if (paramArrayOfByte == null) {
/* 115 */       throw new GSSException(3);
/*     */     }
/* 117 */     this.cStub = paramGSSLibStub;
/* 118 */     byte[] arrayOfByte = paramArrayOfByte;
/*     */     Object localObject2;
/* 120 */     if (paramOid != null)
/*     */     {
/*     */ 
/* 123 */       paramOid = getNativeNameType(paramOid, paramGSSLibStub);
/*     */       
/* 125 */       if (GSSName.NT_EXPORT_NAME.equals(paramOid))
/*     */       {
/*     */ 
/*     */ 
/* 129 */         localObject1 = null;
/* 130 */         localObject2 = new DerOutputStream();
/* 131 */         Oid localOid = this.cStub.getMech();
/*     */         try {
/* 133 */           ((DerOutputStream)localObject2).putOID(new ObjectIdentifier(localOid.toString()));
/*     */         } catch (IOException localIOException) {
/* 135 */           throw new GSSExceptionImpl(11, localIOException);
/*     */         }
/* 137 */         localObject1 = ((DerOutputStream)localObject2).toByteArray();
/* 138 */         arrayOfByte = new byte[4 + localObject1.length + 4 + paramArrayOfByte.length];
/* 139 */         int j = 0;
/* 140 */         arrayOfByte[(j++)] = 4;
/* 141 */         arrayOfByte[(j++)] = 1;
/* 142 */         arrayOfByte[(j++)] = ((byte)(localObject1.length >>> 8));
/* 143 */         arrayOfByte[(j++)] = ((byte)localObject1.length);
/* 144 */         System.arraycopy(localObject1, 0, arrayOfByte, j, localObject1.length);
/* 145 */         j += localObject1.length;
/* 146 */         arrayOfByte[(j++)] = ((byte)(paramArrayOfByte.length >>> 24));
/* 147 */         arrayOfByte[(j++)] = ((byte)(paramArrayOfByte.length >>> 16));
/* 148 */         arrayOfByte[(j++)] = ((byte)(paramArrayOfByte.length >>> 8));
/* 149 */         arrayOfByte[(j++)] = ((byte)paramArrayOfByte.length);
/* 150 */         System.arraycopy(paramArrayOfByte, 0, arrayOfByte, j, paramArrayOfByte.length);
/*     */       }
/*     */     }
/* 153 */     this.pName = this.cStub.importName(arrayOfByte, paramOid);
/* 154 */     setPrintables();
/*     */     
/* 156 */     Object localObject1 = System.getSecurityManager();
/* 157 */     if ((localObject1 != null) && (!Realm.AUTODEDUCEREALM)) {
/* 158 */       localObject2 = getKrbName();
/* 159 */       int i = ((String)localObject2).lastIndexOf('@');
/* 160 */       if (i != -1) {
/* 161 */         String str = ((String)localObject2).substring(i);
/*     */         
/* 163 */         if (((paramOid != null) && 
/* 164 */           (!paramOid.equals(GSSUtil.NT_GSS_KRB5_PRINCIPAL))) || 
/* 165 */           (!new String(paramArrayOfByte).endsWith(str)))
/*     */         {
/*     */           try
/*     */           {
/* 169 */             ((SecurityManager)localObject1).checkPermission(new ServicePermission(str, "-"));
/*     */           }
/*     */           catch (SecurityException localSecurityException) {
/* 172 */             throw new GSSException(11);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 178 */     SunNativeProvider.debug("Imported " + this.printableName + " w/ type " + this.printableType);
/*     */   }
/*     */   
/*     */   private void setPrintables() throws GSSException
/*     */   {
/* 183 */     Object[] arrayOfObject = null;
/* 184 */     arrayOfObject = this.cStub.displayName(this.pName);
/* 185 */     assert ((arrayOfObject != null) && (arrayOfObject.length == 2));
/* 186 */     this.printableName = ((String)arrayOfObject[0]);
/* 187 */     assert (this.printableName != null);
/* 188 */     this.printableType = ((Oid)arrayOfObject[1]);
/* 189 */     if (this.printableType == null) {
/* 190 */       this.printableType = GSSName.NT_USER_NAME;
/*     */     }
/*     */   }
/*     */   
/*     */   public String getKrbName() throws GSSException
/*     */   {
/* 196 */     long l = 0L;
/* 197 */     GSSLibStub localGSSLibStub = this.cStub;
/* 198 */     if (!GSSUtil.isKerberosMech(this.cStub.getMech())) {
/* 199 */       localGSSLibStub = GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID);
/*     */     }
/* 201 */     l = localGSSLibStub.canonicalizeName(this.pName);
/* 202 */     Object[] arrayOfObject = localGSSLibStub.displayName(l);
/* 203 */     localGSSLibStub.releaseName(l);
/* 204 */     SunNativeProvider.debug("Got kerberized name: " + arrayOfObject[0]);
/* 205 */     return (String)arrayOfObject[0];
/*     */   }
/*     */   
/*     */   public Provider getProvider() {
/* 209 */     return SunNativeProvider.INSTANCE;
/*     */   }
/*     */   
/*     */   public boolean equals(GSSNameSpi paramGSSNameSpi) throws GSSException {
/* 213 */     if (!(paramGSSNameSpi instanceof GSSNameElement)) {
/* 214 */       return false;
/*     */     }
/* 216 */     return this.cStub.compareName(this.pName, ((GSSNameElement)paramGSSNameSpi).pName);
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 220 */     if (!(paramObject instanceof GSSNameElement)) {
/* 221 */       return false;
/*     */     }
/*     */     try {
/* 224 */       return equals((GSSNameElement)paramObject);
/*     */     } catch (GSSException localGSSException) {}
/* 226 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 231 */     return new Long(this.pName).hashCode();
/*     */   }
/*     */   
/*     */   public byte[] export() throws GSSException {
/* 235 */     byte[] arrayOfByte1 = this.cStub.exportName(this.pName);
/*     */     
/*     */ 
/*     */ 
/* 239 */     int i = 0;
/* 240 */     if ((arrayOfByte1[(i++)] != 4) || (arrayOfByte1[(i++)] != 1))
/*     */     {
/* 242 */       throw new GSSException(3);
/*     */     }
/* 244 */     int j = (0xFF & arrayOfByte1[(i++)]) << 8 | 0xFF & arrayOfByte1[(i++)];
/*     */     
/* 246 */     ObjectIdentifier localObjectIdentifier = null;
/*     */     try {
/* 248 */       DerInputStream localDerInputStream = new DerInputStream(arrayOfByte1, i, j);
/*     */       
/* 250 */       localObjectIdentifier = new ObjectIdentifier(localDerInputStream);
/*     */     } catch (IOException localIOException) {
/* 252 */       throw new GSSExceptionImpl(3, localIOException);
/*     */     }
/* 254 */     Oid localOid = new Oid(localObjectIdentifier.toString());
/* 255 */     assert (localOid.equals(getMechanism()));
/* 256 */     i += j;
/* 257 */     int k = (0xFF & arrayOfByte1[(i++)]) << 24 | (0xFF & arrayOfByte1[(i++)]) << 16 | (0xFF & arrayOfByte1[(i++)]) << 8 | 0xFF & arrayOfByte1[(i++)];
/*     */     
/*     */ 
/*     */ 
/* 261 */     if (k < 0) {
/* 262 */       throw new GSSException(3);
/*     */     }
/* 264 */     byte[] arrayOfByte2 = new byte[k];
/* 265 */     System.arraycopy(arrayOfByte1, i, arrayOfByte2, 0, k);
/* 266 */     return arrayOfByte2;
/*     */   }
/*     */   
/*     */   public Oid getMechanism() {
/* 270 */     return this.cStub.getMech();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 274 */     return this.printableName;
/*     */   }
/*     */   
/*     */   public Oid getStringNameType() {
/* 278 */     return this.printableType;
/*     */   }
/*     */   
/*     */   public boolean isAnonymousName() {
/* 282 */     return GSSName.NT_ANONYMOUS.equals(this.printableType);
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 286 */     if (this.pName != 0L) {
/* 287 */       this.cStub.releaseName(this.pName);
/* 288 */       this.pName = 0L;
/*     */     }
/*     */   }
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 293 */     dispose();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\wrapper\GSSNameElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */