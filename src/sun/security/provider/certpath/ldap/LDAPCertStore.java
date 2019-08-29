/*      */ package sun.security.provider.certpath.ldap;
/*      */ 
/*      */ import com.sun.jndi.ldap.LdapReferralException;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URI;
/*      */ import java.security.AccessController;
/*      */ import java.security.InvalidAlgorithmParameterException;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.PublicKey;
/*      */ import java.security.cert.CRL;
/*      */ import java.security.cert.CRLException;
/*      */ import java.security.cert.CRLSelector;
/*      */ import java.security.cert.CertSelector;
/*      */ import java.security.cert.CertStore;
/*      */ import java.security.cert.CertStoreException;
/*      */ import java.security.cert.CertStoreParameters;
/*      */ import java.security.cert.CertStoreSpi;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateFactory;
/*      */ import java.security.cert.LDAPCertStoreParameters;
/*      */ import java.security.cert.X509CRL;
/*      */ import java.security.cert.X509CRLSelector;
/*      */ import java.security.cert.X509CertSelector;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.naming.CompositeName;
/*      */ import javax.naming.InvalidNameException;
/*      */ import javax.naming.NameNotFoundException;
/*      */ import javax.naming.NamingEnumeration;
/*      */ import javax.naming.NamingException;
/*      */ import javax.naming.directory.Attribute;
/*      */ import javax.naming.directory.Attributes;
/*      */ import javax.naming.directory.BasicAttributes;
/*      */ import javax.naming.directory.DirContext;
/*      */ import javax.naming.directory.InitialDirContext;
/*      */ import javax.naming.ldap.LdapContext;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.misc.HexDumpEncoder;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.security.provider.certpath.X509CertificatePair;
/*      */ import sun.security.util.Cache;
/*      */ import sun.security.util.Debug;
/*      */ import sun.security.x509.X500Name;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class LDAPCertStore
/*      */   extends CertStoreSpi
/*      */ {
/*  113 */   private static final Debug debug = Debug.getInstance("certpath");
/*      */   
/*      */   private static final boolean DEBUG = false;
/*      */   
/*      */   private static final String USER_CERT = "userCertificate;binary";
/*      */   
/*      */   private static final String CA_CERT = "cACertificate;binary";
/*      */   
/*      */   private static final String CROSS_CERT = "crossCertificatePair;binary";
/*      */   
/*      */   private static final String CRL = "certificateRevocationList;binary";
/*      */   
/*      */   private static final String ARL = "authorityRevocationList;binary";
/*      */   
/*      */   private static final String DELTA_CRL = "deltaRevocationList;binary";
/*  128 */   private static final String[] STRING0 = new String[0];
/*      */   
/*  130 */   private static final byte[][] BB0 = new byte[0][];
/*      */   
/*  132 */   private static final Attributes EMPTY_ATTRIBUTES = new BasicAttributes();
/*      */   
/*      */ 
/*      */   private static final int DEFAULT_CACHE_SIZE = 750;
/*      */   
/*      */   private static final int DEFAULT_CACHE_LIFETIME = 30;
/*      */   
/*      */   private static final int LIFETIME;
/*      */   
/*      */   private static final String PROP_LIFETIME = "sun.security.certpath.ldap.cache.lifetime";
/*      */   
/*      */   private static final String PROP_DISABLE_APP_RESOURCE_FILES = "sun.security.certpath.ldap.disable.app.resource.files";
/*      */   
/*      */   private CertificateFactory cf;
/*      */   
/*      */   private DirContext ctx;
/*      */   
/*      */ 
/*      */   static
/*      */   {
/*  152 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.certpath.ldap.cache.lifetime"));
/*      */     
/*  154 */     if (str != null) {
/*  155 */       LIFETIME = Integer.parseInt(str);
/*      */     } else {
/*  157 */       LIFETIME = 30;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  174 */   private boolean prefetchCRLs = false;
/*      */   
/*      */   private final Cache<String, byte[][]> valueCache;
/*      */   
/*  178 */   private int cacheHits = 0;
/*  179 */   private int cacheMisses = 0;
/*  180 */   private int requests = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LDAPCertStore(CertStoreParameters paramCertStoreParameters)
/*      */     throws InvalidAlgorithmParameterException
/*      */   {
/*  193 */     super(paramCertStoreParameters);
/*  194 */     if (!(paramCertStoreParameters instanceof LDAPCertStoreParameters)) {
/*  195 */       throw new InvalidAlgorithmParameterException("parameters must be LDAPCertStoreParameters");
/*      */     }
/*      */     
/*  198 */     LDAPCertStoreParameters localLDAPCertStoreParameters = (LDAPCertStoreParameters)paramCertStoreParameters;
/*      */     
/*      */ 
/*  201 */     createInitialDirContext(localLDAPCertStoreParameters.getServerName(), localLDAPCertStoreParameters.getPort());
/*      */     
/*      */     try
/*      */     {
/*  205 */       this.cf = CertificateFactory.getInstance("X.509");
/*      */     } catch (CertificateException localCertificateException) {
/*  207 */       throw new InvalidAlgorithmParameterException("unable to create CertificateFactory for X.509");
/*      */     }
/*      */     
/*  210 */     if (LIFETIME == 0) {
/*  211 */       this.valueCache = Cache.newNullCache();
/*  212 */     } else if (LIFETIME < 0) {
/*  213 */       this.valueCache = Cache.newSoftMemoryCache(750);
/*      */     } else {
/*  215 */       this.valueCache = Cache.newSoftMemoryCache(750, LIFETIME);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  224 */   private static final Cache<LDAPCertStoreParameters, CertStore> certStoreCache = Cache.newSoftMemoryCache(185);
/*      */   
/*      */   static synchronized CertStore getInstance(LDAPCertStoreParameters paramLDAPCertStoreParameters) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
/*      */   {
/*  228 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  229 */     if (localSecurityManager != null) {
/*  230 */       localSecurityManager.checkConnect(paramLDAPCertStoreParameters.getServerName(), paramLDAPCertStoreParameters.getPort());
/*      */     }
/*      */     
/*  233 */     CertStore localCertStore = (CertStore)certStoreCache.get(paramLDAPCertStoreParameters);
/*  234 */     if (localCertStore == null) {
/*  235 */       localCertStore = CertStore.getInstance("LDAP", paramLDAPCertStoreParameters);
/*  236 */       certStoreCache.put(paramLDAPCertStoreParameters, localCertStore);
/*      */     }
/*  238 */     else if (debug != null) {
/*  239 */       debug.println("LDAPCertStore.getInstance: cache hit");
/*      */     }
/*      */     
/*  242 */     return localCertStore;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void createInitialDirContext(String paramString, int paramInt)
/*      */     throws InvalidAlgorithmParameterException
/*      */   {
/*  254 */     String str = "ldap://" + paramString + ":" + paramInt;
/*  255 */     Hashtable localHashtable1 = new Hashtable();
/*  256 */     localHashtable1.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
/*      */     
/*  258 */     localHashtable1.put("java.naming.provider.url", str);
/*      */     
/*      */ 
/*  261 */     boolean bool = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.certpath.ldap.disable.app.resource.files"))).booleanValue();
/*      */     
/*  263 */     if (bool) {
/*  264 */       if (debug != null) {
/*  265 */         debug.println("LDAPCertStore disabling app resource files");
/*      */       }
/*  267 */       localHashtable1.put("com.sun.naming.disable.app.resource.files", "true");
/*      */     }
/*      */     try
/*      */     {
/*  271 */       this.ctx = new InitialDirContext(localHashtable1);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  276 */       Hashtable localHashtable2 = this.ctx.getEnvironment();
/*  277 */       if (localHashtable2.get("java.naming.referral") == null) {
/*  278 */         this.ctx.addToEnvironment("java.naming.referral", "throw");
/*      */       }
/*      */     } catch (NamingException localNamingException) {
/*  281 */       if (debug != null) {
/*  282 */         debug.println("LDAPCertStore.engineInit about to throw InvalidAlgorithmParameterException");
/*      */         
/*  284 */         localNamingException.printStackTrace();
/*      */       }
/*  286 */       InvalidAlgorithmParameterException localInvalidAlgorithmParameterException = new InvalidAlgorithmParameterException("unable to create InitialDirContext using supplied parameters");
/*      */       
/*  288 */       localInvalidAlgorithmParameterException.initCause(localNamingException);
/*  289 */       throw ((InvalidAlgorithmParameterException)localInvalidAlgorithmParameterException);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private class LDAPRequest
/*      */   {
/*      */     private final String name;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private Map<String, byte[][]> valueMap;
/*      */     
/*      */ 
/*      */ 
/*      */     private final List<String> requestedAttributes;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     LDAPRequest(String paramString)
/*      */       throws CertStoreException
/*      */     {
/*  316 */       this.name = checkName(paramString);
/*  317 */       this.requestedAttributes = new ArrayList(5);
/*      */     }
/*      */     
/*      */     private String checkName(String paramString) throws CertStoreException {
/*  321 */       if (paramString == null) {
/*  322 */         throw new CertStoreException("Name absent");
/*      */       }
/*      */       try {
/*  325 */         if (new CompositeName(paramString).size() > 1) {
/*  326 */           throw new CertStoreException("Invalid name: " + paramString);
/*      */         }
/*      */       } catch (InvalidNameException localInvalidNameException) {
/*  329 */         throw new CertStoreException("Invalid name: " + paramString, localInvalidNameException);
/*      */       }
/*  331 */       return paramString;
/*      */     }
/*      */     
/*      */     String getName() {
/*  335 */       return this.name;
/*      */     }
/*      */     
/*      */     void addRequestedAttribute(String paramString) {
/*  339 */       if (this.valueMap != null) {
/*  340 */         throw new IllegalStateException("Request already sent");
/*      */       }
/*  342 */       this.requestedAttributes.add(paramString);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     byte[][] getValues(String paramString)
/*      */       throws NamingException
/*      */     {
/*  357 */       String str = this.name + "|" + paramString;
/*  358 */       byte[][] arrayOfByte = (byte[][])LDAPCertStore.this.valueCache.get(str);
/*  359 */       if (arrayOfByte != null) {
/*  360 */         LDAPCertStore.access$108(LDAPCertStore.this);
/*  361 */         return arrayOfByte;
/*      */       }
/*  363 */       LDAPCertStore.access$208(LDAPCertStore.this);
/*  364 */       Map localMap = getValueMap();
/*  365 */       arrayOfByte = (byte[][])localMap.get(paramString);
/*  366 */       return arrayOfByte;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private Map<String, byte[][]> getValueMap()
/*      */       throws NamingException
/*      */     {
/*  384 */       if (this.valueMap != null) {
/*  385 */         return this.valueMap;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  394 */       this.valueMap = new HashMap(8);
/*  395 */       String[] arrayOfString = (String[])this.requestedAttributes.toArray(LDAPCertStore.STRING0);
/*      */       Attributes localAttributes;
/*      */       Object localObject3;
/*  398 */       Object localObject2; try { localAttributes = LDAPCertStore.this.ctx.getAttributes(this.name, arrayOfString);
/*      */       }
/*      */       catch (LdapReferralException localLdapReferralException1) {
/*      */         for (;;) {
/*      */           try {
/*  403 */             String str = (String)localLdapReferralException1.getReferralInfo();
/*  404 */             URI localURI = new URI(str);
/*  405 */             if (!localURI.getScheme().equalsIgnoreCase("ldap")) {
/*  406 */               throw new IllegalArgumentException("Not LDAP");
/*      */             }
/*  408 */             localObject3 = localURI.getPath();
/*  409 */             if ((localObject3 != null) && (((String)localObject3).charAt(0) == '/')) {
/*  410 */               localObject3 = ((String)localObject3).substring(1);
/*      */             }
/*  412 */             checkName((String)localObject3);
/*      */           }
/*      */           catch (Exception localException) {
/*  415 */             throw new NamingException("Cannot follow referral to " + localLdapReferralException1.getReferralInfo());
/*      */           }
/*      */           
/*  418 */           localObject2 = (LdapContext)localLdapReferralException1.getReferralContext();
/*      */           
/*      */           try
/*      */           {
/*  422 */             localAttributes = ((LdapContext)localObject2).getAttributes(this.name, arrayOfString);
/*      */           }
/*      */           catch (LdapReferralException localLdapReferralException2) {
/*  425 */             Object localObject1 = localLdapReferralException2;
/*      */             
/*      */ 
/*      */ 
/*  429 */             ((LdapContext)localObject2).close(); } finally { ((LdapContext)localObject2).close();
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (NameNotFoundException localNameNotFoundException)
/*      */       {
/*  435 */         localAttributes = LDAPCertStore.EMPTY_ATTRIBUTES;
/*      */       }
/*  437 */       for (Iterator localIterator = this.requestedAttributes.iterator(); localIterator.hasNext();) { localObject2 = (String)localIterator.next();
/*  438 */         Attribute localAttribute = localAttributes.get((String)localObject2);
/*  439 */         localObject3 = getAttributeValues(localAttribute);
/*  440 */         cacheAttribute((String)localObject2, (byte[][])localObject3);
/*  441 */         this.valueMap.put(localObject2, localObject3);
/*      */       }
/*  443 */       return this.valueMap;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void cacheAttribute(String paramString, byte[][] paramArrayOfByte)
/*      */     {
/*  450 */       String str = this.name + "|" + paramString;
/*  451 */       LDAPCertStore.this.valueCache.put(str, paramArrayOfByte);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private byte[][] getAttributeValues(Attribute paramAttribute)
/*      */       throws NamingException
/*      */     {
/*      */       byte[][] arrayOfByte;
/*      */       
/*      */ 
/*  462 */       if (paramAttribute == null) {
/*  463 */         arrayOfByte = LDAPCertStore.BB0;
/*      */       } else {
/*  465 */         arrayOfByte = new byte[paramAttribute.size()][];
/*  466 */         int i = 0;
/*  467 */         NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
/*  468 */         while (localNamingEnumeration.hasMore()) {
/*  469 */           Object localObject = localNamingEnumeration.next();
/*  470 */           if ((LDAPCertStore.debug != null) && 
/*  471 */             ((localObject instanceof String))) {
/*  472 */             LDAPCertStore.debug.println("LDAPCertStore.getAttrValues() enum.next is a string!: " + localObject);
/*      */           }
/*      */           
/*      */ 
/*  476 */           byte[] arrayOfByte1 = (byte[])localObject;
/*  477 */           arrayOfByte[(i++)] = arrayOfByte1;
/*      */         }
/*      */       }
/*  480 */       return arrayOfByte;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Collection<X509Certificate> getCertificates(LDAPRequest paramLDAPRequest, String paramString, X509CertSelector paramX509CertSelector)
/*      */     throws CertStoreException
/*      */   {
/*      */     byte[][] arrayOfByte;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  502 */       arrayOfByte = paramLDAPRequest.getValues(paramString);
/*      */     } catch (NamingException localNamingException) {
/*  504 */       throw new CertStoreException(localNamingException);
/*      */     }
/*      */     
/*  507 */     int i = arrayOfByte.length;
/*  508 */     if (i == 0) {
/*  509 */       return Collections.emptySet();
/*      */     }
/*      */     
/*  512 */     ArrayList localArrayList = new ArrayList(i);
/*      */     
/*  514 */     for (int j = 0; j < i; j++) {
/*  515 */       ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte[j]);
/*      */       try {
/*  517 */         Certificate localCertificate = this.cf.generateCertificate(localByteArrayInputStream);
/*  518 */         if (paramX509CertSelector.match(localCertificate)) {
/*  519 */           localArrayList.add((X509Certificate)localCertificate);
/*      */         }
/*      */       } catch (CertificateException localCertificateException) {
/*  522 */         if (debug != null) {
/*  523 */           debug.println("LDAPCertStore.getCertificates() encountered exception while parsing cert, skipping the bad data: ");
/*      */           
/*  525 */           HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*  526 */           debug.println("[ " + localHexDumpEncoder
/*  527 */             .encodeBuffer(arrayOfByte[j]) + " ]");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  532 */     return localArrayList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Collection<X509CertificatePair> getCertPairs(LDAPRequest paramLDAPRequest, String paramString)
/*      */     throws CertStoreException
/*      */   {
/*      */     byte[][] arrayOfByte;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  550 */       arrayOfByte = paramLDAPRequest.getValues(paramString);
/*      */     } catch (NamingException localNamingException) {
/*  552 */       throw new CertStoreException(localNamingException);
/*      */     }
/*      */     
/*  555 */     int i = arrayOfByte.length;
/*  556 */     if (i == 0) {
/*  557 */       return Collections.emptySet();
/*      */     }
/*      */     
/*  560 */     ArrayList localArrayList = new ArrayList(i);
/*      */     
/*  562 */     for (int j = 0; j < i; j++) {
/*      */       try
/*      */       {
/*  565 */         X509CertificatePair localX509CertificatePair = X509CertificatePair.generateCertificatePair(arrayOfByte[j]);
/*  566 */         localArrayList.add(localX509CertificatePair);
/*      */       } catch (CertificateException localCertificateException) {
/*  568 */         if (debug != null) {
/*  569 */           debug.println("LDAPCertStore.getCertPairs() encountered exception while parsing cert, skipping the bad data: ");
/*      */           
/*      */ 
/*  572 */           HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*  573 */           debug.println("[ " + localHexDumpEncoder
/*  574 */             .encodeBuffer(arrayOfByte[j]) + " ]");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  579 */     return localArrayList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Collection<X509Certificate> getMatchingCrossCerts(LDAPRequest paramLDAPRequest, X509CertSelector paramX509CertSelector1, X509CertSelector paramX509CertSelector2)
/*      */     throws CertStoreException
/*      */   {
/*  604 */     Collection localCollection = getCertPairs(paramLDAPRequest, "crossCertificatePair;binary");
/*      */     
/*      */ 
/*  607 */     ArrayList localArrayList = new ArrayList();
/*  608 */     for (X509CertificatePair localX509CertificatePair : localCollection) {
/*      */       X509Certificate localX509Certificate;
/*  610 */       if (paramX509CertSelector1 != null) {
/*  611 */         localX509Certificate = localX509CertificatePair.getForward();
/*  612 */         if ((localX509Certificate != null) && (paramX509CertSelector1.match(localX509Certificate))) {
/*  613 */           localArrayList.add(localX509Certificate);
/*      */         }
/*      */       }
/*  616 */       if (paramX509CertSelector2 != null) {
/*  617 */         localX509Certificate = localX509CertificatePair.getReverse();
/*  618 */         if ((localX509Certificate != null) && (paramX509CertSelector2.match(localX509Certificate))) {
/*  619 */           localArrayList.add(localX509Certificate);
/*      */         }
/*      */       }
/*      */     }
/*  623 */     return localArrayList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector)
/*      */     throws CertStoreException
/*      */   {
/*  649 */     if (debug != null) {
/*  650 */       debug.println("LDAPCertStore.engineGetCertificates() selector: " + 
/*  651 */         String.valueOf(paramCertSelector));
/*      */     }
/*      */     
/*  654 */     if (paramCertSelector == null) {
/*  655 */       paramCertSelector = new X509CertSelector();
/*      */     }
/*  657 */     if (!(paramCertSelector instanceof X509CertSelector)) {
/*  658 */       throw new CertStoreException("LDAPCertStore needs an X509CertSelector to find certs");
/*      */     }
/*      */     
/*  661 */     X509CertSelector localX509CertSelector = (X509CertSelector)paramCertSelector;
/*  662 */     int i = localX509CertSelector.getBasicConstraints();
/*  663 */     String str1 = localX509CertSelector.getSubjectAsString();
/*  664 */     String str2 = localX509CertSelector.getIssuerAsString();
/*  665 */     HashSet localHashSet = new HashSet();
/*  666 */     if (debug != null) {
/*  667 */       debug.println("LDAPCertStore.engineGetCertificates() basicConstraints: " + i);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     LDAPRequest localLDAPRequest;
/*      */     
/*      */ 
/*      */ 
/*  676 */     if (str1 != null) {
/*  677 */       if (debug != null) {
/*  678 */         debug.println("LDAPCertStore.engineGetCertificates() subject is not null");
/*      */       }
/*      */       
/*  681 */       localLDAPRequest = new LDAPRequest(str1);
/*  682 */       if (i > -2) {
/*  683 */         localLDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
/*  684 */         localLDAPRequest.addRequestedAttribute("cACertificate;binary");
/*  685 */         localLDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
/*  686 */         if (this.prefetchCRLs) {
/*  687 */           localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
/*      */         }
/*      */       }
/*  690 */       if (i < 0) {
/*  691 */         localLDAPRequest.addRequestedAttribute("userCertificate;binary");
/*      */       }
/*      */       
/*  694 */       if (i > -2) {
/*  695 */         localHashSet.addAll(getMatchingCrossCerts(localLDAPRequest, localX509CertSelector, null));
/*  696 */         if (debug != null) {
/*  697 */           debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(subject,xsel,null),certs.size(): " + localHashSet
/*      */           
/*  699 */             .size());
/*      */         }
/*  701 */         localHashSet.addAll(getCertificates(localLDAPRequest, "cACertificate;binary", localX509CertSelector));
/*  702 */         if (debug != null) {
/*  703 */           debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,CA_CERT,xsel),certs.size(): " + localHashSet
/*      */           
/*  705 */             .size());
/*      */         }
/*      */       }
/*  708 */       if (i < 0) {
/*  709 */         localHashSet.addAll(getCertificates(localLDAPRequest, "userCertificate;binary", localX509CertSelector));
/*  710 */         if (debug != null) {
/*  711 */           debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,USER_CERT, xsel),certs.size(): " + localHashSet
/*      */           
/*  713 */             .size());
/*      */         }
/*      */       }
/*      */     } else {
/*  717 */       if (debug != null)
/*      */       {
/*  719 */         debug.println("LDAPCertStore.engineGetCertificates() subject is null");
/*      */       }
/*  721 */       if (i == -2) {
/*  722 */         throw new CertStoreException("need subject to find EE certs");
/*      */       }
/*  724 */       if (str2 == null) {
/*  725 */         throw new CertStoreException("need subject or issuer to find certs");
/*      */       }
/*      */     }
/*  728 */     if (debug != null) {
/*  729 */       debug.println("LDAPCertStore.engineGetCertificates() about to getMatchingCrossCerts...");
/*      */     }
/*      */     
/*  732 */     if ((str2 != null) && (i > -2)) {
/*  733 */       localLDAPRequest = new LDAPRequest(str2);
/*  734 */       localLDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
/*  735 */       localLDAPRequest.addRequestedAttribute("cACertificate;binary");
/*  736 */       localLDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
/*  737 */       if (this.prefetchCRLs) {
/*  738 */         localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
/*      */       }
/*      */       
/*  741 */       localHashSet.addAll(getMatchingCrossCerts(localLDAPRequest, null, localX509CertSelector));
/*  742 */       if (debug != null) {
/*  743 */         debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(issuer,null,xsel),certs.size(): " + localHashSet
/*      */         
/*  745 */           .size());
/*      */       }
/*  747 */       localHashSet.addAll(getCertificates(localLDAPRequest, "cACertificate;binary", localX509CertSelector));
/*  748 */       if (debug != null) {
/*  749 */         debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(issuer,CA_CERT,xsel),certs.size(): " + localHashSet
/*      */         
/*  751 */           .size());
/*      */       }
/*      */     }
/*  754 */     if (debug != null) {
/*  755 */       debug.println("LDAPCertStore.engineGetCertificates() returning certs");
/*      */     }
/*  757 */     return localHashSet;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Collection<X509CRL> getCRLs(LDAPRequest paramLDAPRequest, String paramString, X509CRLSelector paramX509CRLSelector)
/*      */     throws CertStoreException
/*      */   {
/*      */     byte[][] arrayOfByte;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  777 */       arrayOfByte = paramLDAPRequest.getValues(paramString);
/*      */     } catch (NamingException localNamingException) {
/*  779 */       throw new CertStoreException(localNamingException);
/*      */     }
/*      */     
/*  782 */     int i = arrayOfByte.length;
/*  783 */     if (i == 0) {
/*  784 */       return Collections.emptySet();
/*      */     }
/*      */     
/*  787 */     ArrayList localArrayList = new ArrayList(i);
/*      */     
/*  789 */     for (int j = 0; j < i; j++) {
/*      */       try {
/*  791 */         CRL localCRL = this.cf.generateCRL(new ByteArrayInputStream(arrayOfByte[j]));
/*  792 */         if (paramX509CRLSelector.match(localCRL)) {
/*  793 */           localArrayList.add((X509CRL)localCRL);
/*      */         }
/*      */       } catch (CRLException localCRLException) {
/*  796 */         if (debug != null) {
/*  797 */           debug.println("LDAPCertStore.getCRLs() encountered exception while parsing CRL, skipping the bad data: ");
/*      */           
/*  799 */           HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*  800 */           debug.println("[ " + localHexDumpEncoder.encodeBuffer(arrayOfByte[j]) + " ]");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  805 */     return localArrayList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector)
/*      */     throws CertStoreException
/*      */   {
/*  831 */     if (debug != null) {
/*  832 */       debug.println("LDAPCertStore.engineGetCRLs() selector: " + paramCRLSelector);
/*      */     }
/*      */     
/*      */ 
/*  836 */     if (paramCRLSelector == null) {
/*  837 */       paramCRLSelector = new X509CRLSelector();
/*      */     }
/*  839 */     if (!(paramCRLSelector instanceof X509CRLSelector)) {
/*  840 */       throw new CertStoreException("need X509CRLSelector to find CRLs");
/*      */     }
/*  842 */     X509CRLSelector localX509CRLSelector = (X509CRLSelector)paramCRLSelector;
/*  843 */     HashSet localHashSet = new HashSet();
/*      */     
/*      */ 
/*      */ 
/*  847 */     X509Certificate localX509Certificate = localX509CRLSelector.getCertificateChecking();
/*  848 */     Object localObject1; if (localX509Certificate != null) {
/*  849 */       localObject1 = new HashSet();
/*  850 */       localObject2 = localX509Certificate.getIssuerX500Principal();
/*  851 */       ((Collection)localObject1).add(((X500Principal)localObject2).getName("RFC2253"));
/*      */     }
/*      */     else
/*      */     {
/*  855 */       localObject1 = localX509CRLSelector.getIssuerNames();
/*  856 */       if (localObject1 == null) {
/*  857 */         throw new CertStoreException("need issuerNames or certChecking to find CRLs");
/*      */       }
/*      */     }
/*      */     
/*  861 */     for (Object localObject2 = ((Collection)localObject1).iterator(); ((Iterator)localObject2).hasNext();) { Object localObject3 = ((Iterator)localObject2).next();
/*      */       String str;
/*  863 */       if ((localObject3 instanceof byte[])) {
/*      */         try {
/*  865 */           X500Principal localX500Principal = new X500Principal((byte[])localObject3);
/*  866 */           str = localX500Principal.getName("RFC2253");
/*      */ 
/*      */         }
/*      */         catch (IllegalArgumentException localIllegalArgumentException) {}
/*      */       } else {
/*  871 */         str = (String)localObject3;
/*      */       }
/*      */       
/*  874 */       Object localObject4 = Collections.emptySet();
/*  875 */       LDAPRequest localLDAPRequest; if ((localX509Certificate == null) || (localX509Certificate.getBasicConstraints() != -1)) {
/*  876 */         localLDAPRequest = new LDAPRequest(str);
/*  877 */         localLDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
/*  878 */         localLDAPRequest.addRequestedAttribute("cACertificate;binary");
/*  879 */         localLDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
/*  880 */         if (this.prefetchCRLs) {
/*  881 */           localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
/*      */         }
/*      */         try {
/*  884 */           localObject4 = getCRLs(localLDAPRequest, "authorityRevocationList;binary", localX509CRLSelector);
/*  885 */           if (((Collection)localObject4).isEmpty())
/*      */           {
/*      */ 
/*  888 */             this.prefetchCRLs = true;
/*      */           } else {
/*  890 */             localHashSet.addAll((Collection)localObject4);
/*      */           }
/*      */         } catch (CertStoreException localCertStoreException) {
/*  893 */           if (debug != null) {
/*  894 */             debug.println("LDAPCertStore.engineGetCRLs non-fatal error retrieving ARLs:" + localCertStoreException);
/*      */             
/*  896 */             localCertStoreException.printStackTrace();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  903 */       if ((((Collection)localObject4).isEmpty()) || (localX509Certificate == null)) {
/*  904 */         localLDAPRequest = new LDAPRequest(str);
/*  905 */         localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
/*  906 */         localObject4 = getCRLs(localLDAPRequest, "certificateRevocationList;binary", localX509CRLSelector);
/*  907 */         localHashSet.addAll((Collection)localObject4);
/*      */       }
/*      */     }
/*  910 */     return localHashSet;
/*      */   }
/*      */   
/*      */   static LDAPCertStoreParameters getParameters(URI paramURI)
/*      */   {
/*  915 */     String str = paramURI.getHost();
/*  916 */     if (str == null) {
/*  917 */       return new SunLDAPCertStoreParameters();
/*      */     }
/*  919 */     int i = paramURI.getPort();
/*  920 */     return i == -1 ? new SunLDAPCertStoreParameters(str) : new SunLDAPCertStoreParameters(str, i);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class SunLDAPCertStoreParameters
/*      */     extends LDAPCertStoreParameters
/*      */   {
/*  934 */     private volatile int hashCode = 0;
/*      */     
/*      */     SunLDAPCertStoreParameters(String paramString, int paramInt) {
/*  937 */       super(paramInt);
/*      */     }
/*      */     
/*  940 */     SunLDAPCertStoreParameters(String paramString) { super(); }
/*      */     
/*      */     SunLDAPCertStoreParameters() {}
/*      */     
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*  946 */       if (!(paramObject instanceof LDAPCertStoreParameters)) {
/*  947 */         return false;
/*      */       }
/*  949 */       LDAPCertStoreParameters localLDAPCertStoreParameters = (LDAPCertStoreParameters)paramObject;
/*  950 */       return (getPort() == localLDAPCertStoreParameters.getPort()) && 
/*  951 */         (getServerName().equalsIgnoreCase(localLDAPCertStoreParameters.getServerName()));
/*      */     }
/*      */     
/*  954 */     public int hashCode() { if (this.hashCode == 0) {
/*  955 */         int i = 17;
/*  956 */         i = 37 * i + getPort();
/*      */         
/*  958 */         i = 37 * i + getServerName().toLowerCase(Locale.ENGLISH).hashCode();
/*  959 */         this.hashCode = i;
/*      */       }
/*  961 */       return this.hashCode;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static class LDAPCertSelector
/*      */     extends X509CertSelector
/*      */   {
/*      */     private X500Principal certSubject;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private X509CertSelector selector;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private X500Principal subject;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     LDAPCertSelector(X509CertSelector paramX509CertSelector, X500Principal paramX500Principal, String paramString)
/*      */       throws IOException
/*      */     {
/*  995 */       this.selector = (paramX509CertSelector == null ? new X509CertSelector() : paramX509CertSelector);
/*  996 */       this.certSubject = paramX500Principal;
/*  997 */       this.subject = new X500Name(paramString).asX500Principal();
/*      */     }
/*      */     
/*      */ 
/*      */     public X509Certificate getCertificate()
/*      */     {
/* 1003 */       return this.selector.getCertificate();
/*      */     }
/*      */     
/* 1006 */     public BigInteger getSerialNumber() { return this.selector.getSerialNumber(); }
/*      */     
/*      */     public X500Principal getIssuer() {
/* 1009 */       return this.selector.getIssuer();
/*      */     }
/*      */     
/* 1012 */     public String getIssuerAsString() { return this.selector.getIssuerAsString(); }
/*      */     
/*      */     public byte[] getIssuerAsBytes() throws IOException {
/* 1015 */       return this.selector.getIssuerAsBytes();
/*      */     }
/*      */     
/*      */     public X500Principal getSubject() {
/* 1019 */       return this.subject;
/*      */     }
/*      */     
/*      */     public String getSubjectAsString() {
/* 1023 */       return this.subject.getName();
/*      */     }
/*      */     
/*      */     public byte[] getSubjectAsBytes() throws IOException {
/* 1027 */       return this.subject.getEncoded();
/*      */     }
/*      */     
/* 1030 */     public byte[] getSubjectKeyIdentifier() { return this.selector.getSubjectKeyIdentifier(); }
/*      */     
/*      */     public byte[] getAuthorityKeyIdentifier() {
/* 1033 */       return this.selector.getAuthorityKeyIdentifier();
/*      */     }
/*      */     
/* 1036 */     public Date getCertificateValid() { return this.selector.getCertificateValid(); }
/*      */     
/*      */     public Date getPrivateKeyValid() {
/* 1039 */       return this.selector.getPrivateKeyValid();
/*      */     }
/*      */     
/* 1042 */     public String getSubjectPublicKeyAlgID() { return this.selector.getSubjectPublicKeyAlgID(); }
/*      */     
/*      */     public PublicKey getSubjectPublicKey() {
/* 1045 */       return this.selector.getSubjectPublicKey();
/*      */     }
/*      */     
/* 1048 */     public boolean[] getKeyUsage() { return this.selector.getKeyUsage(); }
/*      */     
/*      */     public Set<String> getExtendedKeyUsage() {
/* 1051 */       return this.selector.getExtendedKeyUsage();
/*      */     }
/*      */     
/* 1054 */     public boolean getMatchAllSubjectAltNames() { return this.selector.getMatchAllSubjectAltNames(); }
/*      */     
/*      */     public Collection<List<?>> getSubjectAlternativeNames() {
/* 1057 */       return this.selector.getSubjectAlternativeNames();
/*      */     }
/*      */     
/* 1060 */     public byte[] getNameConstraints() { return this.selector.getNameConstraints(); }
/*      */     
/*      */     public int getBasicConstraints() {
/* 1063 */       return this.selector.getBasicConstraints();
/*      */     }
/*      */     
/* 1066 */     public Set<String> getPolicy() { return this.selector.getPolicy(); }
/*      */     
/*      */     public Collection<List<?>> getPathToNames() {
/* 1069 */       return this.selector.getPathToNames();
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean match(Certificate paramCertificate)
/*      */     {
/* 1075 */       this.selector.setSubject(this.certSubject);
/* 1076 */       boolean bool = this.selector.match(paramCertificate);
/* 1077 */       this.selector.setSubject(this.subject);
/* 1078 */       return bool;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static class LDAPCRLSelector
/*      */     extends X509CRLSelector
/*      */   {
/*      */     private X509CRLSelector selector;
/*      */     
/*      */ 
/*      */     private Collection<X500Principal> certIssuers;
/*      */     
/*      */ 
/*      */     private Collection<X500Principal> issuers;
/*      */     
/*      */ 
/*      */     private HashSet<Object> issuerNames;
/*      */     
/*      */ 
/*      */ 
/*      */     LDAPCRLSelector(X509CRLSelector paramX509CRLSelector, Collection<X500Principal> paramCollection, String paramString)
/*      */       throws IOException
/*      */     {
/* 1104 */       this.selector = (paramX509CRLSelector == null ? new X509CRLSelector() : paramX509CRLSelector);
/* 1105 */       this.certIssuers = paramCollection;
/* 1106 */       this.issuerNames = new HashSet();
/* 1107 */       this.issuerNames.add(paramString);
/* 1108 */       this.issuers = new HashSet();
/* 1109 */       this.issuers.add(new X500Name(paramString).asX500Principal());
/*      */     }
/*      */     
/*      */ 
/*      */     public Collection<X500Principal> getIssuers()
/*      */     {
/* 1115 */       return Collections.unmodifiableCollection(this.issuers);
/*      */     }
/*      */     
/*      */     public Collection<Object> getIssuerNames() {
/* 1119 */       return Collections.unmodifiableCollection(this.issuerNames);
/*      */     }
/*      */     
/* 1122 */     public BigInteger getMinCRL() { return this.selector.getMinCRL(); }
/*      */     
/*      */     public BigInteger getMaxCRL() {
/* 1125 */       return this.selector.getMaxCRL();
/*      */     }
/*      */     
/* 1128 */     public Date getDateAndTime() { return this.selector.getDateAndTime(); }
/*      */     
/*      */     public X509Certificate getCertificateChecking() {
/* 1131 */       return this.selector.getCertificateChecking();
/*      */     }
/*      */     
/*      */     public boolean match(CRL paramCRL)
/*      */     {
/* 1136 */       this.selector.setIssuers(this.certIssuers);
/* 1137 */       boolean bool = this.selector.match(paramCRL);
/* 1138 */       this.selector.setIssuers(this.issuers);
/* 1139 */       return bool;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\ldap\LDAPCertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */