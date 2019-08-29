/*     */ package sun.security.jgss;
/*     */ 
/*     */ import com.sun.security.jgss.ExtendedGSSCredential;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import org.ietf.jgss.GSSCredential;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.GSSName;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.jgss.spi.GSSCredentialSpi;
/*     */ import sun.security.jgss.spi.GSSNameSpi;
/*     */ import sun.security.jgss.spnego.SpNegoCredElement;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GSSCredentialImpl
/*     */   implements ExtendedGSSCredential
/*     */ {
/*  36 */   private GSSManagerImpl gssManager = null;
/*  37 */   private boolean destroyed = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  45 */   private Hashtable<SearchKey, GSSCredentialSpi> hashtable = null;
/*     */   
/*     */ 
/*  48 */   private GSSCredentialSpi tempCred = null;
/*     */   
/*     */   GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, int paramInt) throws GSSException
/*     */   {
/*  52 */     this(paramGSSManagerImpl, null, 0, (Oid[])null, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */   GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, GSSName paramGSSName, int paramInt1, Oid paramOid, int paramInt2)
/*     */     throws GSSException
/*     */   {
/*  59 */     if (paramOid == null) { paramOid = ProviderList.DEFAULT_MECH_OID;
/*     */     }
/*  61 */     init(paramGSSManagerImpl);
/*  62 */     add(paramGSSName, paramInt1, paramInt1, paramOid, paramInt2);
/*     */   }
/*     */   
/*     */   GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, GSSName paramGSSName, int paramInt1, Oid[] paramArrayOfOid, int paramInt2)
/*     */     throws GSSException
/*     */   {
/*  68 */     init(paramGSSManagerImpl);
/*  69 */     int i = 0;
/*  70 */     if (paramArrayOfOid == null) {
/*  71 */       paramArrayOfOid = paramGSSManagerImpl.getMechs();
/*  72 */       i = 1;
/*     */     }
/*     */     
/*  75 */     for (int j = 0; j < paramArrayOfOid.length; j++) {
/*     */       try {
/*  77 */         add(paramGSSName, paramInt1, paramInt1, paramArrayOfOid[j], paramInt2);
/*     */       } catch (GSSException localGSSException) {
/*  79 */         if (i != 0)
/*     */         {
/*  81 */           GSSUtil.debug("Ignore " + localGSSException + " while acquring cred for " + paramArrayOfOid[j]);
/*     */         }
/*     */         else
/*  84 */           throw localGSSException;
/*     */       }
/*     */     }
/*  87 */     if ((this.hashtable.size() == 0) || (paramInt2 != getUsage())) {
/*  88 */       throw new GSSException(13);
/*     */     }
/*     */   }
/*     */   
/*     */   public GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, GSSCredentialSpi paramGSSCredentialSpi)
/*     */     throws GSSException
/*     */   {
/*  95 */     init(paramGSSManagerImpl);
/*  96 */     int i = 2;
/*  97 */     if (paramGSSCredentialSpi.isInitiatorCredential()) {
/*  98 */       if (paramGSSCredentialSpi.isAcceptorCredential()) {
/*  99 */         i = 0;
/*     */       } else {
/* 101 */         i = 1;
/*     */       }
/*     */     }
/* 104 */     SearchKey localSearchKey = new SearchKey(paramGSSCredentialSpi.getMechanism(), i);
/*     */     
/* 106 */     this.tempCred = paramGSSCredentialSpi;
/* 107 */     this.hashtable.put(localSearchKey, this.tempCred);
/*     */     
/* 109 */     if (!GSSUtil.isSpNegoMech(paramGSSCredentialSpi.getMechanism())) {
/* 110 */       localSearchKey = new SearchKey(GSSUtil.GSS_SPNEGO_MECH_OID, i);
/* 111 */       this.hashtable.put(localSearchKey, new SpNegoCredElement(paramGSSCredentialSpi));
/*     */     }
/*     */   }
/*     */   
/*     */   void init(GSSManagerImpl paramGSSManagerImpl) {
/* 116 */     this.gssManager = paramGSSManagerImpl;
/*     */     
/* 118 */     this.hashtable = new Hashtable(paramGSSManagerImpl.getMechs().length);
/*     */   }
/*     */   
/*     */   public void dispose() throws GSSException {
/* 122 */     if (!this.destroyed)
/*     */     {
/* 124 */       Enumeration localEnumeration = this.hashtable.elements();
/* 125 */       while (localEnumeration.hasMoreElements()) {
/* 126 */         GSSCredentialSpi localGSSCredentialSpi = (GSSCredentialSpi)localEnumeration.nextElement();
/* 127 */         localGSSCredentialSpi.dispose();
/*     */       }
/* 129 */       this.destroyed = true;
/*     */     }
/*     */   }
/*     */   
/*     */   public GSSCredential impersonate(GSSName paramGSSName) throws GSSException {
/* 134 */     if (this.destroyed) {
/* 135 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/* 138 */     Oid localOid = this.tempCred.getMechanism();
/*     */     
/* 140 */     GSSNameSpi localGSSNameSpi = paramGSSName == null ? null : ((GSSNameImpl)paramGSSName).getElement(localOid);
/* 141 */     GSSCredentialSpi localGSSCredentialSpi = this.tempCred.impersonate(localGSSNameSpi);
/* 142 */     return localGSSCredentialSpi == null ? null : new GSSCredentialImpl(this.gssManager, localGSSCredentialSpi);
/*     */   }
/*     */   
/*     */   public GSSName getName() throws GSSException
/*     */   {
/* 147 */     if (this.destroyed) {
/* 148 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/* 151 */     return GSSNameImpl.wrapElement(this.gssManager, this.tempCred.getName());
/*     */   }
/*     */   
/*     */   public GSSName getName(Oid paramOid) throws GSSException
/*     */   {
/* 156 */     if (this.destroyed) {
/* 157 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/*     */ 
/* 161 */     SearchKey localSearchKey = null;
/* 162 */     GSSCredentialSpi localGSSCredentialSpi = null;
/*     */     
/* 164 */     if (paramOid == null) { paramOid = ProviderList.DEFAULT_MECH_OID;
/*     */     }
/* 166 */     localSearchKey = new SearchKey(paramOid, 1);
/* 167 */     localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */     
/* 169 */     if (localGSSCredentialSpi == null) {
/* 170 */       localSearchKey = new SearchKey(paramOid, 2);
/* 171 */       localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */     }
/*     */     
/* 174 */     if (localGSSCredentialSpi == null) {
/* 175 */       localSearchKey = new SearchKey(paramOid, 0);
/* 176 */       localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */     }
/*     */     
/* 179 */     if (localGSSCredentialSpi == null) {
/* 180 */       throw new GSSExceptionImpl(2, paramOid);
/*     */     }
/*     */     
/* 183 */     return GSSNameImpl.wrapElement(this.gssManager, localGSSCredentialSpi.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getRemainingLifetime()
/*     */     throws GSSException
/*     */   {
/* 195 */     if (this.destroyed) {
/* 196 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 202 */     int i = 0;int j = 0;int k = 0;
/* 203 */     int m = Integer.MAX_VALUE;
/*     */     
/* 205 */     Enumeration localEnumeration = this.hashtable.keys();
/* 206 */     while (localEnumeration.hasMoreElements()) {
/* 207 */       SearchKey localSearchKey = (SearchKey)localEnumeration.nextElement();
/* 208 */       GSSCredentialSpi localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/* 209 */       if (localSearchKey.getUsage() == 1) {
/* 210 */         i = localGSSCredentialSpi.getInitLifetime();
/* 211 */       } else if (localSearchKey.getUsage() == 2) {
/* 212 */         i = localGSSCredentialSpi.getAcceptLifetime();
/*     */       } else {
/* 214 */         j = localGSSCredentialSpi.getInitLifetime();
/* 215 */         k = localGSSCredentialSpi.getAcceptLifetime();
/* 216 */         i = j < k ? j : k;
/*     */       }
/*     */       
/*     */ 
/* 220 */       if (m > i) {
/* 221 */         m = i;
/*     */       }
/*     */     }
/* 224 */     return m;
/*     */   }
/*     */   
/*     */   public int getRemainingInitLifetime(Oid paramOid) throws GSSException
/*     */   {
/* 229 */     if (this.destroyed) {
/* 230 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/*     */ 
/* 234 */     GSSCredentialSpi localGSSCredentialSpi = null;
/* 235 */     SearchKey localSearchKey = null;
/* 236 */     int i = 0;
/* 237 */     int j = 0;
/*     */     
/* 239 */     if (paramOid == null) { paramOid = ProviderList.DEFAULT_MECH_OID;
/*     */     }
/* 241 */     localSearchKey = new SearchKey(paramOid, 1);
/* 242 */     localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */     
/* 244 */     if (localGSSCredentialSpi != null) {
/* 245 */       i = 1;
/* 246 */       if (j < localGSSCredentialSpi.getInitLifetime()) {
/* 247 */         j = localGSSCredentialSpi.getInitLifetime();
/*     */       }
/*     */     }
/* 250 */     localSearchKey = new SearchKey(paramOid, 0);
/* 251 */     localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */     
/* 253 */     if (localGSSCredentialSpi != null) {
/* 254 */       i = 1;
/* 255 */       if (j < localGSSCredentialSpi.getInitLifetime()) {
/* 256 */         j = localGSSCredentialSpi.getInitLifetime();
/*     */       }
/*     */     }
/* 259 */     if (i == 0) {
/* 260 */       throw new GSSExceptionImpl(2, paramOid);
/*     */     }
/*     */     
/* 263 */     return j;
/*     */   }
/*     */   
/*     */   public int getRemainingAcceptLifetime(Oid paramOid)
/*     */     throws GSSException
/*     */   {
/* 269 */     if (this.destroyed) {
/* 270 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/*     */ 
/* 274 */     GSSCredentialSpi localGSSCredentialSpi = null;
/* 275 */     SearchKey localSearchKey = null;
/* 276 */     int i = 0;
/* 277 */     int j = 0;
/*     */     
/* 279 */     if (paramOid == null) { paramOid = ProviderList.DEFAULT_MECH_OID;
/*     */     }
/* 281 */     localSearchKey = new SearchKey(paramOid, 2);
/* 282 */     localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */     
/* 284 */     if (localGSSCredentialSpi != null) {
/* 285 */       i = 1;
/* 286 */       if (j < localGSSCredentialSpi.getAcceptLifetime()) {
/* 287 */         j = localGSSCredentialSpi.getAcceptLifetime();
/*     */       }
/*     */     }
/* 290 */     localSearchKey = new SearchKey(paramOid, 0);
/* 291 */     localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */     
/* 293 */     if (localGSSCredentialSpi != null) {
/* 294 */       i = 1;
/* 295 */       if (j < localGSSCredentialSpi.getAcceptLifetime()) {
/* 296 */         j = localGSSCredentialSpi.getAcceptLifetime();
/*     */       }
/*     */     }
/* 299 */     if (i == 0) {
/* 300 */       throw new GSSExceptionImpl(2, paramOid);
/*     */     }
/*     */     
/* 303 */     return j;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getUsage()
/*     */     throws GSSException
/*     */   {
/* 315 */     if (this.destroyed) {
/* 316 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 321 */     int i = 0;
/* 322 */     int j = 0;
/*     */     
/* 324 */     Enumeration localEnumeration = this.hashtable.keys();
/* 325 */     while (localEnumeration.hasMoreElements()) {
/* 326 */       SearchKey localSearchKey = (SearchKey)localEnumeration.nextElement();
/* 327 */       if (localSearchKey.getUsage() == 1) {
/* 328 */         i = 1;
/* 329 */       } else if (localSearchKey.getUsage() == 2) {
/* 330 */         j = 1;
/*     */       } else
/* 332 */         return 0;
/*     */     }
/* 334 */     if (i != 0) {
/* 335 */       if (j != 0) {
/* 336 */         return 0;
/*     */       }
/* 338 */       return 1;
/*     */     }
/* 340 */     return 2;
/*     */   }
/*     */   
/*     */   public int getUsage(Oid paramOid) throws GSSException
/*     */   {
/* 345 */     if (this.destroyed) {
/* 346 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/*     */ 
/* 350 */     GSSCredentialSpi localGSSCredentialSpi = null;
/* 351 */     SearchKey localSearchKey = null;
/* 352 */     int i = 0;
/* 353 */     int j = 0;
/*     */     
/* 355 */     if (paramOid == null) { paramOid = ProviderList.DEFAULT_MECH_OID;
/*     */     }
/* 357 */     localSearchKey = new SearchKey(paramOid, 1);
/* 358 */     localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */     
/* 360 */     if (localGSSCredentialSpi != null) {
/* 361 */       i = 1;
/*     */     }
/*     */     
/* 364 */     localSearchKey = new SearchKey(paramOid, 2);
/* 365 */     localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */     
/* 367 */     if (localGSSCredentialSpi != null) {
/* 368 */       j = 1;
/*     */     }
/*     */     
/* 371 */     localSearchKey = new SearchKey(paramOid, 0);
/* 372 */     localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */     
/* 374 */     if (localGSSCredentialSpi != null) {
/* 375 */       i = 1;
/* 376 */       j = 1;
/*     */     }
/*     */     
/* 379 */     if ((i != 0) && (j != 0))
/* 380 */       return 0;
/* 381 */     if (i != 0)
/* 382 */       return 1;
/* 383 */     if (j != 0) {
/* 384 */       return 2;
/*     */     }
/* 386 */     throw new GSSExceptionImpl(2, paramOid);
/*     */   }
/*     */   
/*     */   public Oid[] getMechs()
/*     */     throws GSSException
/*     */   {
/* 392 */     if (this.destroyed) {
/* 393 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/* 396 */     Vector localVector = new Vector(this.hashtable.size());
/*     */     
/* 398 */     Enumeration localEnumeration = this.hashtable.keys();
/* 399 */     while (localEnumeration.hasMoreElements()) {
/* 400 */       SearchKey localSearchKey = (SearchKey)localEnumeration.nextElement();
/* 401 */       localVector.addElement(localSearchKey.getMech());
/*     */     }
/* 403 */     return (Oid[])localVector.toArray(new Oid[0]);
/*     */   }
/*     */   
/*     */   public void add(GSSName paramGSSName, int paramInt1, int paramInt2, Oid paramOid, int paramInt3)
/*     */     throws GSSException
/*     */   {
/* 409 */     if (this.destroyed) {
/* 410 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/* 413 */     if (paramOid == null) { paramOid = ProviderList.DEFAULT_MECH_OID;
/*     */     }
/* 415 */     SearchKey localSearchKey = new SearchKey(paramOid, paramInt3);
/* 416 */     if (this.hashtable.containsKey(localSearchKey))
/*     */     {
/*     */ 
/* 419 */       throw new GSSExceptionImpl(17, "Duplicate element found: " + getElementStr(paramOid, paramInt3));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 425 */     GSSNameSpi localGSSNameSpi = paramGSSName == null ? null : ((GSSNameImpl)paramGSSName).getElement(paramOid);
/*     */     
/* 427 */     this.tempCred = this.gssManager.getCredentialElement(localGSSNameSpi, paramInt1, paramInt2, paramOid, paramInt3);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 446 */     if (this.tempCred != null) {
/* 447 */       if ((paramInt3 == 0) && (
/* 448 */         (!this.tempCred.isAcceptorCredential()) || 
/* 449 */         (!this.tempCred.isInitiatorCredential())))
/*     */       {
/*     */         int i;
/*     */         
/*     */         int j;
/* 454 */         if (!this.tempCred.isInitiatorCredential()) {
/* 455 */           i = 2;
/* 456 */           j = 1;
/*     */         } else {
/* 458 */           i = 1;
/* 459 */           j = 2;
/*     */         }
/*     */         
/* 462 */         localSearchKey = new SearchKey(paramOid, i);
/* 463 */         this.hashtable.put(localSearchKey, this.tempCred);
/*     */         
/* 465 */         this.tempCred = this.gssManager.getCredentialElement(localGSSNameSpi, paramInt1, paramInt2, paramOid, j);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 471 */         localSearchKey = new SearchKey(paramOid, j);
/* 472 */         this.hashtable.put(localSearchKey, this.tempCred);
/*     */       } else {
/* 474 */         this.hashtable.put(localSearchKey, this.tempCred);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 481 */     if (this.destroyed) {
/* 482 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/*     */ 
/* 486 */     if (this == paramObject) {
/* 487 */       return true;
/*     */     }
/*     */     
/* 490 */     if (!(paramObject instanceof GSSCredentialImpl)) {
/* 491 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 510 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 521 */     if (this.destroyed) {
/* 522 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 532 */     return 1;
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
/*     */   public GSSCredentialSpi getElement(Oid paramOid, boolean paramBoolean)
/*     */     throws GSSException
/*     */   {
/* 548 */     if (this.destroyed) {
/* 549 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/*     */     SearchKey localSearchKey;
/*     */     
/*     */     GSSCredentialSpi localGSSCredentialSpi;
/*     */     
/* 556 */     if (paramOid == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 561 */       paramOid = ProviderList.DEFAULT_MECH_OID;
/* 562 */       localSearchKey = new SearchKey(paramOid, paramBoolean ? 1 : 2);
/*     */       
/* 564 */       localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/* 565 */       if (localGSSCredentialSpi == null) {
/* 566 */         localSearchKey = new SearchKey(paramOid, 0);
/* 567 */         localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/* 568 */         if (localGSSCredentialSpi == null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 573 */           Object[] arrayOfObject = this.hashtable.entrySet().toArray();
/* 574 */           for (int i = 0; i < arrayOfObject.length; i++)
/*     */           {
/* 576 */             localGSSCredentialSpi = (GSSCredentialSpi)((Map.Entry)arrayOfObject[i]).getValue();
/* 577 */             if (localGSSCredentialSpi.isInitiatorCredential() == paramBoolean) {
/*     */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     } else {
/* 584 */       if (paramBoolean) {
/* 585 */         localSearchKey = new SearchKey(paramOid, 1);
/*     */       } else {
/* 587 */         localSearchKey = new SearchKey(paramOid, 2);
/*     */       }
/* 589 */       localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */       
/* 591 */       if (localGSSCredentialSpi == null) {
/* 592 */         localSearchKey = new SearchKey(paramOid, 0);
/* 593 */         localGSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(localSearchKey);
/*     */       }
/*     */     }
/*     */     
/* 597 */     if (localGSSCredentialSpi == null)
/*     */     {
/*     */ 
/* 600 */       throw new GSSExceptionImpl(13, "No credential found for: " + getElementStr(paramOid, paramBoolean ? 1 : 2));
/*     */     }
/* 602 */     return localGSSCredentialSpi;
/*     */   }
/*     */   
/*     */   Set<GSSCredentialSpi> getElements()
/*     */   {
/* 607 */     HashSet localHashSet = new HashSet(this.hashtable.size());
/* 608 */     Enumeration localEnumeration = this.hashtable.elements();
/* 609 */     while (localEnumeration.hasMoreElements()) {
/* 610 */       GSSCredentialSpi localGSSCredentialSpi = (GSSCredentialSpi)localEnumeration.nextElement();
/* 611 */       localHashSet.add(localGSSCredentialSpi);
/*     */     }
/* 613 */     return localHashSet;
/*     */   }
/*     */   
/*     */   private static String getElementStr(Oid paramOid, int paramInt) {
/* 617 */     String str = paramOid.toString();
/* 618 */     if (paramInt == 1)
/*     */     {
/* 620 */       str = str.concat(" usage: Initiate");
/* 621 */     } else if (paramInt == 2)
/*     */     {
/* 623 */       str = str.concat(" usage: Accept");
/*     */     }
/*     */     else {
/* 626 */       str = str.concat(" usage: Initiate and Accept");
/*     */     }
/* 628 */     return str;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 633 */     if (this.destroyed) {
/* 634 */       throw new IllegalStateException("This credential is no longer valid");
/*     */     }
/*     */     
/*     */ 
/* 638 */     GSSCredentialSpi localGSSCredentialSpi = null;
/* 639 */     StringBuffer localStringBuffer = new StringBuffer("[GSSCredential: ");
/* 640 */     Object[] arrayOfObject = this.hashtable.entrySet().toArray();
/* 641 */     for (int i = 0; i < arrayOfObject.length; i++) {
/*     */       try {
/* 643 */         localStringBuffer.append('\n');
/*     */         
/* 645 */         localGSSCredentialSpi = (GSSCredentialSpi)((Map.Entry)arrayOfObject[i]).getValue();
/* 646 */         localStringBuffer.append(localGSSCredentialSpi.getName());
/* 647 */         localStringBuffer.append(' ');
/* 648 */         localStringBuffer.append(localGSSCredentialSpi.getMechanism());
/* 649 */         localStringBuffer.append(localGSSCredentialSpi.isInitiatorCredential() ? " Initiate" : "");
/*     */         
/* 651 */         localStringBuffer.append(localGSSCredentialSpi.isAcceptorCredential() ? " Accept" : "");
/*     */         
/* 653 */         localStringBuffer.append(" [");
/* 654 */         localStringBuffer.append(localGSSCredentialSpi.getClass());
/* 655 */         localStringBuffer.append(']');
/*     */       }
/*     */       catch (GSSException localGSSException) {}
/*     */     }
/*     */     
/* 660 */     localStringBuffer.append(']');
/* 661 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */   static class SearchKey {
/* 665 */     private Oid mechOid = null;
/* 666 */     private int usage = 0;
/*     */     
/*     */     public SearchKey(Oid paramOid, int paramInt) {
/* 669 */       this.mechOid = paramOid;
/* 670 */       this.usage = paramInt;
/*     */     }
/*     */     
/* 673 */     public Oid getMech() { return this.mechOid; }
/*     */     
/*     */ 
/* 676 */     public int getUsage() { return this.usage; }
/*     */     
/*     */     public boolean equals(Object paramObject) {
/* 679 */       if (!(paramObject instanceof SearchKey))
/* 680 */         return false;
/* 681 */       SearchKey localSearchKey = (SearchKey)paramObject;
/* 682 */       return (this.mechOid.equals(localSearchKey.mechOid)) && (this.usage == localSearchKey.usage);
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 686 */       return this.mechOid.hashCode();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\GSSCredentialImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */