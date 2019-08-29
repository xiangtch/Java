/*     */ package sun.security.jca;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Provider;
/*     */ import java.security.Provider.Service;
/*     */ import java.security.Security;
/*     */ import java.util.AbstractList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import sun.security.util.Debug;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ProviderList
/*     */ {
/*  59 */   static final Debug debug = Debug.getInstance("jca", "ProviderList");
/*     */   
/*  61 */   private static final ProviderConfig[] PC0 = new ProviderConfig[0];
/*     */   
/*  63 */   private static final Provider[] P0 = new Provider[0];
/*     */   
/*     */ 
/*  66 */   static final ProviderList EMPTY = new ProviderList(PC0, true);
/*     */   
/*     */ 
/*     */ 
/*  70 */   private static final Provider EMPTY_PROVIDER = new Provider("##Empty##", 1.0D, "initialization in progress")
/*     */   {
/*     */     private static final long serialVersionUID = 1151354171352296389L;
/*     */     
/*     */     public Service getService(String paramAnonymousString1, String paramAnonymousString2) {
/*  75 */       return null;
/*     */     }
/*     */   };
/*     */   private final ProviderConfig[] configs;
/*     */   private volatile boolean allLoaded;
/*     */   
/*     */   static ProviderList fromSecurityProperties()
/*     */   {
/*  83 */     (ProviderList)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public ProviderList run() {
/*  86 */         return new ProviderList(null);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static ProviderList add(ProviderList paramProviderList, Provider paramProvider) {
/*  92 */     return insertAt(paramProviderList, paramProvider, -1);
/*     */   }
/*     */   
/*     */   public static ProviderList insertAt(ProviderList paramProviderList, Provider paramProvider, int paramInt)
/*     */   {
/*  97 */     if (paramProviderList.getProvider(paramProvider.getName()) != null) {
/*  98 */       return paramProviderList;
/*     */     }
/*     */     
/* 101 */     ArrayList localArrayList = new ArrayList(Arrays.asList(paramProviderList.configs));
/* 102 */     int i = localArrayList.size();
/* 103 */     if ((paramInt < 0) || (paramInt > i)) {
/* 104 */       paramInt = i;
/*     */     }
/* 106 */     localArrayList.add(paramInt, new ProviderConfig(paramProvider));
/* 107 */     return new ProviderList((ProviderConfig[])localArrayList.toArray(PC0), true);
/*     */   }
/*     */   
/*     */   public static ProviderList remove(ProviderList paramProviderList, String paramString)
/*     */   {
/* 112 */     if (paramProviderList.getProvider(paramString) == null) {
/* 113 */       return paramProviderList;
/*     */     }
/*     */     
/* 116 */     ProviderConfig[] arrayOfProviderConfig1 = new ProviderConfig[paramProviderList.size() - 1];
/* 117 */     int i = 0;
/* 118 */     for (ProviderConfig localProviderConfig : paramProviderList.configs) {
/* 119 */       if (!localProviderConfig.getProvider().getName().equals(paramString)) {
/* 120 */         arrayOfProviderConfig1[(i++)] = localProviderConfig;
/*     */       }
/*     */     }
/* 123 */     return new ProviderList(arrayOfProviderConfig1, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public static ProviderList newList(Provider... paramVarArgs)
/*     */   {
/* 129 */     ProviderConfig[] arrayOfProviderConfig = new ProviderConfig[paramVarArgs.length];
/* 130 */     for (int i = 0; i < paramVarArgs.length; i++) {
/* 131 */       arrayOfProviderConfig[i] = new ProviderConfig(paramVarArgs[i]);
/*     */     }
/* 133 */     return new ProviderList(arrayOfProviderConfig, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 143 */   private final List<Provider> userList = new AbstractList() {
/*     */     public int size() {
/* 145 */       return ProviderList.this.configs.length;
/*     */     }
/*     */     
/* 148 */     public Provider get(int paramAnonymousInt) { return ProviderList.this.getProvider(paramAnonymousInt); }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private ProviderList(ProviderConfig[] paramArrayOfProviderConfig, boolean paramBoolean)
/*     */   {
/* 156 */     this.configs = paramArrayOfProviderConfig;
/* 157 */     this.allLoaded = paramBoolean;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private ProviderList()
/*     */   {
/* 164 */     ArrayList localArrayList = new ArrayList();
/* 165 */     for (int i = 1;; i++) {
/* 166 */       String str1 = Security.getProperty("security.provider." + i);
/* 167 */       if (str1 == null) {
/*     */         break;
/*     */       }
/* 170 */       str1 = str1.trim();
/* 171 */       if (str1.length() == 0) {
/* 172 */         System.err.println("invalid entry for security.provider." + i);
/*     */         
/* 174 */         break;
/*     */       }
/* 176 */       int j = str1.indexOf(' ');
/*     */       ProviderConfig localProviderConfig;
/* 178 */       if (j == -1) {
/* 179 */         localProviderConfig = new ProviderConfig(str1);
/*     */       } else {
/* 181 */         String str2 = str1.substring(0, j);
/* 182 */         String str3 = str1.substring(j + 1).trim();
/* 183 */         localProviderConfig = new ProviderConfig(str2, str3);
/*     */       }
/*     */       
/*     */ 
/* 187 */       if (!localArrayList.contains(localProviderConfig)) {
/* 188 */         localArrayList.add(localProviderConfig);
/*     */       }
/*     */     }
/* 191 */     this.configs = ((ProviderConfig[])localArrayList.toArray(PC0));
/* 192 */     if (debug != null) {
/* 193 */       debug.println("provider configuration: " + localArrayList);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   ProviderList getJarList(String[] paramArrayOfString)
/*     */   {
/* 204 */     ArrayList localArrayList = new ArrayList();
/* 205 */     for (String str : paramArrayOfString) {
/* 206 */       Object localObject2 = new ProviderConfig(str);
/* 207 */       for (ProviderConfig localProviderConfig : this.configs)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */         if (localProviderConfig.equals(localObject2)) {
/* 214 */           localObject2 = localProviderConfig;
/* 215 */           break;
/*     */         }
/*     */       }
/* 218 */       localArrayList.add(localObject2);
/*     */     }
/* 220 */     ??? = (ProviderConfig[])localArrayList.toArray(PC0);
/* 221 */     return new ProviderList((ProviderConfig[])???, false);
/*     */   }
/*     */   
/*     */   public int size() {
/* 225 */     return this.configs.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   Provider getProvider(int paramInt)
/*     */   {
/* 233 */     Provider localProvider = this.configs[paramInt].getProvider();
/* 234 */     return localProvider != null ? localProvider : EMPTY_PROVIDER;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<Provider> providers()
/*     */   {
/* 243 */     return this.userList;
/*     */   }
/*     */   
/*     */   private ProviderConfig getProviderConfig(String paramString) {
/* 247 */     int i = getIndex(paramString);
/* 248 */     return i != -1 ? this.configs[i] : null;
/*     */   }
/*     */   
/*     */   public Provider getProvider(String paramString)
/*     */   {
/* 253 */     ProviderConfig localProviderConfig = getProviderConfig(paramString);
/* 254 */     return localProviderConfig == null ? null : localProviderConfig.getProvider();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getIndex(String paramString)
/*     */   {
/* 262 */     for (int i = 0; i < this.configs.length; i++) {
/* 263 */       Provider localProvider = getProvider(i);
/* 264 */       if (localProvider.getName().equals(paramString)) {
/* 265 */         return i;
/*     */       }
/*     */     }
/* 268 */     return -1;
/*     */   }
/*     */   
/*     */   private int loadAll()
/*     */   {
/* 273 */     if (this.allLoaded) {
/* 274 */       return this.configs.length;
/*     */     }
/* 276 */     if (debug != null) {
/* 277 */       debug.println("Loading all providers");
/* 278 */       new Exception("Call trace").printStackTrace();
/*     */     }
/* 280 */     int i = 0;
/* 281 */     for (int j = 0; j < this.configs.length; j++) {
/* 282 */       Provider localProvider = this.configs[j].getProvider();
/* 283 */       if (localProvider != null) {
/* 284 */         i++;
/*     */       }
/*     */     }
/* 287 */     if (i == this.configs.length) {
/* 288 */       this.allLoaded = true;
/*     */     }
/* 290 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   ProviderList removeInvalid()
/*     */   {
/* 299 */     int i = loadAll();
/* 300 */     if (i == this.configs.length) {
/* 301 */       return this;
/*     */     }
/* 303 */     ProviderConfig[] arrayOfProviderConfig = new ProviderConfig[i];
/* 304 */     int j = 0; for (int k = 0; j < this.configs.length; j++) {
/* 305 */       ProviderConfig localProviderConfig = this.configs[j];
/* 306 */       if (localProviderConfig.isLoaded()) {
/* 307 */         arrayOfProviderConfig[(k++)] = localProviderConfig;
/*     */       }
/*     */     }
/* 310 */     return new ProviderList(arrayOfProviderConfig, true);
/*     */   }
/*     */   
/*     */   public Provider[] toArray()
/*     */   {
/* 315 */     return (Provider[])providers().toArray(P0);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 320 */     return Arrays.asList(this.configs).toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Service getService(String paramString1, String paramString2)
/*     */   {
/* 330 */     for (int i = 0; i < this.configs.length; i++) {
/* 331 */       Provider localProvider = getProvider(i);
/* 332 */       Service localService = localProvider.getService(paramString1, paramString2);
/* 333 */       if (localService != null) {
/* 334 */         return localService;
/*     */       }
/*     */     }
/* 337 */     return null;
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
/*     */   public List<Service> getServices(String paramString1, String paramString2)
/*     */   {
/* 350 */     return new ServiceList(paramString1, paramString2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public List<Service> getServices(String paramString, List<String> paramList)
/*     */   {
/* 360 */     ArrayList localArrayList = new ArrayList();
/* 361 */     for (String str : paramList) {
/* 362 */       localArrayList.add(new ServiceId(paramString, str));
/*     */     }
/* 364 */     return getServices(localArrayList);
/*     */   }
/*     */   
/*     */   public List<Service> getServices(List<ServiceId> paramList) {
/* 368 */     return new ServiceList(paramList);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final class ServiceList
/*     */     extends AbstractList<Service>
/*     */   {
/*     */     private final String type;
/*     */     
/*     */ 
/*     */ 
/*     */     private final String algorithm;
/*     */     
/*     */ 
/*     */ 
/*     */     private final List<ServiceId> ids;
/*     */     
/*     */ 
/*     */     private Service firstService;
/*     */     
/*     */ 
/*     */     private List<Service> services;
/*     */     
/*     */ 
/*     */     private int providerIndex;
/*     */     
/*     */ 
/*     */ 
/*     */     ServiceList(String paramString1, String paramString2)
/*     */     {
/* 400 */       this.type = paramString1;
/* 401 */       this.algorithm = paramString2;
/* 402 */       this.ids = null;
/*     */     }
/*     */     
/*     */     ServiceList() {
/* 406 */       this.type = null;
/* 407 */       this.algorithm = null;
/* 408 */       List localList; this.ids = localList;
/*     */     }
/*     */     
/*     */     private void addService(Service paramService) {
/* 412 */       if (this.firstService == null) {
/* 413 */         this.firstService = paramService;
/*     */       } else {
/* 415 */         if (this.services == null) {
/* 416 */           this.services = new ArrayList(4);
/* 417 */           this.services.add(this.firstService);
/*     */         }
/* 419 */         this.services.add(paramService);
/*     */       }
/*     */     }
/*     */     
/*     */     private Service tryGet(int paramInt) { Provider localProvider;
/*     */       Object localObject;
/* 425 */       for (;;) { if ((paramInt == 0) && (this.firstService != null))
/* 426 */           return this.firstService;
/* 427 */         if ((this.services != null) && (this.services.size() > paramInt)) {
/* 428 */           return (Service)this.services.get(paramInt);
/*     */         }
/* 430 */         if (this.providerIndex >= ProviderList.this.configs.length) {
/* 431 */           return null;
/*     */         }
/*     */         
/* 434 */         localProvider = ProviderList.this.getProvider(this.providerIndex++);
/* 435 */         if (this.type != null)
/*     */         {
/* 437 */           localObject = localProvider.getService(this.type, this.algorithm);
/* 438 */           if (localObject != null) {
/* 439 */             addService((Service)localObject);
/*     */           }
/*     */         }
/*     */         else {
/* 443 */           for (localObject = this.ids.iterator(); ((Iterator)localObject).hasNext();) { ServiceId localServiceId = (ServiceId)((Iterator)localObject).next();
/* 444 */             Service localService = localProvider.getService(localServiceId.type, localServiceId.algorithm);
/* 445 */             if (localService != null) {
/* 446 */               addService(localService);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public Service get(int paramInt) {
/* 454 */       Service localService = tryGet(paramInt);
/* 455 */       if (localService == null) {
/* 456 */         throw new IndexOutOfBoundsException();
/*     */       }
/* 458 */       return localService;
/*     */     }
/*     */     
/*     */     public int size() {
/*     */       int i;
/* 463 */       if (this.services != null) {
/* 464 */         i = this.services.size();
/*     */       } else {
/* 466 */         i = this.firstService != null ? 1 : 0;
/*     */       }
/* 468 */       while (tryGet(i) != null) {
/* 469 */         i++;
/*     */       }
/* 471 */       return i;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 478 */       return tryGet(0) == null;
/*     */     }
/*     */     
/*     */     public Iterator<Service> iterator() {
/* 482 */       new Iterator() {
/*     */         int index;
/*     */         
/*     */         public boolean hasNext() {
/* 486 */           return ServiceList.this.tryGet(this.index) != null;
/*     */         }
/*     */         
/*     */         public Service next() {
/* 490 */           Service localService = ServiceList.this.tryGet(this.index);
/* 491 */           if (localService == null) {
/* 492 */             throw new NoSuchElementException();
/*     */           }
/* 494 */           this.index += 1;
/* 495 */           return localService;
/*     */         }
/*     */         
/*     */         public void remove() {
/* 499 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jca\ProviderList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */