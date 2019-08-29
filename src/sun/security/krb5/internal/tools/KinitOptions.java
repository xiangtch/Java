/*     */ package sun.security.krb5.internal.tools;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.RealmException;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.ccache.CCacheInputStream;
/*     */ import sun.security.krb5.internal.ccache.FileCredentialsCache;
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
/*     */ class KinitOptions
/*     */ {
/*  52 */   public boolean validate = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  57 */   public short forwardable = -1;
/*  58 */   public short proxiable = -1;
/*  59 */   public boolean renew = false;
/*     */   public KerberosTime lifetime;
/*     */   public KerberosTime renewable_lifetime;
/*     */   public String target_service;
/*     */   public String keytab_file;
/*     */   public String cachename;
/*     */   private PrincipalName principal;
/*     */   public String realm;
/*  67 */   char[] password = null;
/*     */   public boolean keytab;
/*  69 */   private boolean DEBUG = Krb5.DEBUG;
/*  70 */   private boolean includeAddresses = true;
/*  71 */   private boolean useKeytab = false;
/*     */   private String ktabName;
/*     */   
/*     */   public KinitOptions()
/*     */     throws RuntimeException, RealmException
/*     */   {
/*  77 */     this.cachename = FileCredentialsCache.getDefaultCacheName();
/*  78 */     if (this.cachename == null) {
/*  79 */       throw new RuntimeException("default cache name error");
/*     */     }
/*  81 */     this.principal = getDefaultPrincipal();
/*     */   }
/*     */   
/*     */   public void setKDCRealm(String paramString) throws RealmException {
/*  85 */     this.realm = paramString;
/*     */   }
/*     */   
/*     */   public String getKDCRealm() {
/*  89 */     if ((this.realm == null) && 
/*  90 */       (this.principal != null)) {
/*  91 */       return this.principal.getRealmString();
/*     */     }
/*     */     
/*  94 */     return null;
/*     */   }
/*     */   
/*     */   public KinitOptions(String[] paramArrayOfString)
/*     */     throws KrbException, RuntimeException, IOException
/*     */   {
/* 100 */     String str = null;
/*     */     
/* 102 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 103 */       if (paramArrayOfString[i].equals("-f")) {
/* 104 */         this.forwardable = 1;
/* 105 */       } else if (paramArrayOfString[i].equals("-p")) {
/* 106 */         this.proxiable = 1;
/* 107 */       } else if (paramArrayOfString[i].equals("-c"))
/*     */       {
/* 109 */         if (paramArrayOfString[(i + 1)].startsWith("-")) {
/* 110 */           throw new IllegalArgumentException("input format  not correct:  -c  option must be followed by the cache name");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 116 */         this.cachename = paramArrayOfString[(++i)];
/* 117 */         if ((this.cachename.length() >= 5) && 
/* 118 */           (this.cachename.substring(0, 5).equalsIgnoreCase("FILE:"))) {
/* 119 */           this.cachename = this.cachename.substring(5);
/*     */         }
/* 121 */       } else if (paramArrayOfString[i].equals("-A")) {
/* 122 */         this.includeAddresses = false;
/* 123 */       } else if (paramArrayOfString[i].equals("-k")) {
/* 124 */         this.useKeytab = true;
/* 125 */       } else if (paramArrayOfString[i].equals("-t")) {
/* 126 */         if (this.ktabName != null) {
/* 127 */           throw new IllegalArgumentException("-t option/keytab file name repeated");
/*     */         }
/* 129 */         if (i + 1 < paramArrayOfString.length) {
/* 130 */           this.ktabName = paramArrayOfString[(++i)];
/*     */         } else {
/* 132 */           throw new IllegalArgumentException("-t option requires keytab file name");
/*     */         }
/*     */         
/*     */ 
/* 136 */         this.useKeytab = true;
/* 137 */       } else if (paramArrayOfString[i].equalsIgnoreCase("-help")) {
/* 138 */         printHelp();
/* 139 */         System.exit(0);
/* 140 */       } else if (str == null) {
/* 141 */         str = paramArrayOfString[i];
/*     */         try {
/* 143 */           this.principal = new PrincipalName(str);
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/* 147 */           throw new IllegalArgumentException("invalid Principal name: " + str + localException.getMessage());
/*     */         }
/* 149 */       } else if (this.password == null)
/*     */       {
/* 151 */         this.password = paramArrayOfString[i].toCharArray();
/*     */       } else {
/* 153 */         throw new IllegalArgumentException("too many parameters");
/*     */       }
/*     */     }
/*     */     
/* 157 */     if (this.cachename == null) {
/* 158 */       this.cachename = FileCredentialsCache.getDefaultCacheName();
/* 159 */       if (this.cachename == null) {
/* 160 */         throw new RuntimeException("default cache name error");
/*     */       }
/*     */     }
/* 163 */     if (this.principal == null) {
/* 164 */       this.principal = getDefaultPrincipal();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   PrincipalName getDefaultPrincipal()
/*     */   {
/*     */     try
/*     */     {
/* 174 */       CCacheInputStream localCCacheInputStream = new CCacheInputStream(new FileInputStream(this.cachename));
/*     */       
/*     */       int i;
/* 177 */       if ((i = localCCacheInputStream.readVersion()) == 1284)
/*     */       {
/* 179 */         localCCacheInputStream.readTag();
/*     */       }
/* 181 */       else if ((i == 1281) || (i == 1282))
/*     */       {
/* 183 */         localCCacheInputStream.setNativeByteOrder();
/*     */       }
/*     */       
/* 186 */       PrincipalName localPrincipalName2 = localCCacheInputStream.readPrincipal(i);
/* 187 */       localCCacheInputStream.close();
/* 188 */       if (this.DEBUG) {
/* 189 */         System.out.println(">>>KinitOptions principal name from the cache is :" + localPrincipalName2);
/*     */       }
/*     */       
/* 192 */       return localPrincipalName2;
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 196 */       if (this.DEBUG) {
/* 197 */         localIOException.printStackTrace();
/*     */       }
/*     */     } catch (RealmException localRealmException1) {
/* 200 */       if (this.DEBUG) {
/* 201 */         localRealmException1.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 205 */     String str = System.getProperty("user.name");
/* 206 */     if (this.DEBUG) {
/* 207 */       System.out.println(">>>KinitOptions default username is :" + str);
/*     */     }
/*     */     try
/*     */     {
/* 211 */       return new PrincipalName(str);
/*     */     }
/*     */     catch (RealmException localRealmException2)
/*     */     {
/* 215 */       if (this.DEBUG) {
/* 216 */         System.out.println("Exception in getting principal name " + localRealmException2
/* 217 */           .getMessage());
/* 218 */         localRealmException2.printStackTrace();
/*     */       }
/*     */     }
/* 221 */     return null;
/*     */   }
/*     */   
/*     */   void printHelp()
/*     */   {
/* 226 */     System.out.println("Usage: kinit [-A] [-f] [-p] [-c cachename] [[-k [-t keytab_file_name]] [principal] [password]");
/*     */     
/*     */ 
/*     */ 
/* 230 */     System.out.println("\tavailable options to Kerberos 5 ticket request:");
/*     */     
/* 232 */     System.out.println("\t    -A   do not include addresses");
/* 233 */     System.out.println("\t    -f   forwardable");
/* 234 */     System.out.println("\t    -p   proxiable");
/* 235 */     System.out.println("\t    -c   cache name (i.e., FILE:\\d:\\myProfiles\\mykrb5cache)");
/*     */     
/* 237 */     System.out.println("\t    -k   use keytab");
/* 238 */     System.out.println("\t    -t   keytab file name");
/* 239 */     System.out.println("\t    principal   the principal name (i.e., qweadf@ATHENA.MIT.EDU qweadf)");
/*     */     
/* 241 */     System.out.println("\t    password   the principal's Kerberos password");
/*     */   }
/*     */   
/*     */   public boolean getAddressOption()
/*     */   {
/* 246 */     return this.includeAddresses;
/*     */   }
/*     */   
/*     */   public boolean useKeytabFile() {
/* 250 */     return this.useKeytab;
/*     */   }
/*     */   
/*     */   public String keytabFileName() {
/* 254 */     return this.ktabName;
/*     */   }
/*     */   
/*     */   public PrincipalName getPrincipal() {
/* 258 */     return this.principal;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\tools\KinitOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */