/*     */ package sun.net.spi;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.ProxySelector;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.URI;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringJoiner;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import sun.net.NetProperties;
/*     */ import sun.net.SocksProxy;
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
/*     */ public class DefaultProxySelector
/*     */   extends ProxySelector
/*     */ {
/*  75 */   static final String[][] props = { { "http", "http.proxy", "proxy", "socksProxy" }, { "https", "https.proxy", "proxy", "socksProxy" }, { "ftp", "ftp.proxy", "ftpProxy", "proxy", "socksProxy" }, { "gopher", "gopherProxy", "socksProxy" }, { "socket", "socksProxy" } };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String SOCKS_PROXY_VERSION = "socksProxyVersion";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  88 */   private static boolean hasSystemProxies = false;
/*     */   
/*     */   static
/*     */   {
/*  92 */     Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */ 
/*  95 */       public Boolean run() { return NetProperties.getBoolean("java.net.useSystemProxies"); }
/*     */     });
/*  97 */     if ((localBoolean != null) && (localBoolean.booleanValue())) {
/*  98 */       AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Void run() {
/* 101 */           System.loadLibrary("net");
/* 102 */           return null;
/*     */         }
/* 104 */       });
/* 105 */       hasSystemProxies = init();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static class NonProxyInfo
/*     */   {
/*     */     static final String defStringVal = "localhost|127.*|[::1]|0.0.0.0|[::0]";
/*     */     
/*     */ 
/*     */     String hostsSource;
/*     */     
/*     */ 
/*     */     Pattern pattern;
/*     */     
/*     */ 
/*     */     final String property;
/*     */     
/*     */     final String defaultVal;
/*     */     
/* 126 */     static NonProxyInfo ftpNonProxyInfo = new NonProxyInfo("ftp.nonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
/* 127 */     static NonProxyInfo httpNonProxyInfo = new NonProxyInfo("http.nonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
/* 128 */     static NonProxyInfo socksNonProxyInfo = new NonProxyInfo("socksNonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
/*     */     
/*     */     NonProxyInfo(String paramString1, String paramString2, Pattern paramPattern, String paramString3) {
/* 131 */       this.property = paramString1;
/* 132 */       this.hostsSource = paramString2;
/* 133 */       this.pattern = paramPattern;
/* 134 */       this.defaultVal = paramString3;
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
/*     */   public List<Proxy> select(URI paramURI)
/*     */   {
/* 147 */     if (paramURI == null) {
/* 148 */       throw new IllegalArgumentException("URI can't be null.");
/*     */     }
/* 150 */     String str1 = paramURI.getScheme();
/* 151 */     Object localObject1 = paramURI.getHost();
/*     */     
/* 153 */     if (localObject1 == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 161 */       localObject2 = paramURI.getAuthority();
/* 162 */       if (localObject2 != null)
/*     */       {
/* 164 */         int i = ((String)localObject2).indexOf('@');
/* 165 */         if (i >= 0) {
/* 166 */           localObject2 = ((String)localObject2).substring(i + 1);
/*     */         }
/* 168 */         i = ((String)localObject2).lastIndexOf(':');
/* 169 */         if (i >= 0) {
/* 170 */           localObject2 = ((String)localObject2).substring(0, i);
/*     */         }
/* 172 */         localObject1 = localObject2;
/*     */       }
/*     */     }
/*     */     
/* 176 */     if ((str1 == null) || (localObject1 == null)) {
/* 177 */       throw new IllegalArgumentException("protocol = " + str1 + " host = " + (String)localObject1);
/*     */     }
/* 179 */     Object localObject2 = new ArrayList(1);
/*     */     
/* 181 */     NonProxyInfo localNonProxyInfo1 = null;
/*     */     
/* 183 */     if ("http".equalsIgnoreCase(str1)) {
/* 184 */       localNonProxyInfo1 = NonProxyInfo.httpNonProxyInfo;
/* 185 */     } else if ("https".equalsIgnoreCase(str1))
/*     */     {
/*     */ 
/* 188 */       localNonProxyInfo1 = NonProxyInfo.httpNonProxyInfo;
/* 189 */     } else if ("ftp".equalsIgnoreCase(str1)) {
/* 190 */       localNonProxyInfo1 = NonProxyInfo.ftpNonProxyInfo;
/* 191 */     } else if ("socket".equalsIgnoreCase(str1)) {
/* 192 */       localNonProxyInfo1 = NonProxyInfo.socksNonProxyInfo;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 198 */     final String str2 = str1;
/* 199 */     final NonProxyInfo localNonProxyInfo2 = localNonProxyInfo1;
/* 200 */     final String str3 = ((String)localObject1).toLowerCase();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 208 */     Proxy localProxy = (Proxy)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Proxy run()
/*     */       {
/* 212 */         String str1 = null;
/* 213 */         int j = 0;
/* 214 */         String str2 = null;
/* 215 */         InetSocketAddress localInetSocketAddress = null;
/*     */         
/*     */ 
/* 218 */         for (int i = 0; i < DefaultProxySelector.props.length; i++) {
/* 219 */           if (DefaultProxySelector.props[i][0].equalsIgnoreCase(str2)) {
/* 220 */             for (Object localObject1 = 1; localObject1 < DefaultProxySelector.props[i].length; localObject1++)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 225 */               str1 = NetProperties.get(DefaultProxySelector.props[i][localObject1] + "Host");
/* 226 */               if ((str1 != null) && (str1.length() != 0))
/*     */                 break;
/*     */             }
/* 229 */             if ((str1 == null) || (str1.length() == 0))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */               if (DefaultProxySelector.hasSystemProxies) {
/*     */                 String str3;
/* 238 */                 if (str2.equalsIgnoreCase("socket")) {
/* 239 */                   str3 = "socks";
/*     */                 } else
/* 241 */                   str3 = str2;
/* 242 */                 Proxy localProxy = DefaultProxySelector.this.getSystemProxy(str3, str3);
/* 243 */                 if (localProxy != null) {
/* 244 */                   return localProxy;
/*     */                 }
/*     */               }
/* 247 */               return Proxy.NO_PROXY;
/*     */             }
/*     */             
/*     */ 
/* 251 */             if (localNonProxyInfo2 != null) {
/* 252 */               str2 = NetProperties.get(localNonProxyInfo2.property);
/* 253 */               synchronized (localNonProxyInfo2) {
/* 254 */                 if (str2 == null) {
/* 255 */                   if (localNonProxyInfo2.defaultVal != null) {
/* 256 */                     str2 = localNonProxyInfo2.defaultVal;
/*     */                   } else {
/* 258 */                     localNonProxyInfo2.hostsSource = null;
/* 259 */                     localNonProxyInfo2.pattern = null;
/*     */                   }
/* 261 */                 } else if (str2.length() != 0)
/*     */                 {
/*     */ 
/*     */ 
/* 265 */                   str2 = str2 + "|localhost|127.*|[::1]|0.0.0.0|[::0]";
/*     */                 }
/*     */                 
/* 268 */                 if ((str2 != null) && 
/* 269 */                   (!str2.equals(localNonProxyInfo2.hostsSource))) {
/* 270 */                   localNonProxyInfo2.pattern = DefaultProxySelector.toPattern(str2);
/* 271 */                   localNonProxyInfo2.hostsSource = str2;
/*     */                 }
/*     */                 
/* 274 */                 if (DefaultProxySelector.shouldNotUseProxyFor(localNonProxyInfo2.pattern, str3)) {
/* 275 */                   return Proxy.NO_PROXY;
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 281 */             j = NetProperties.getInteger(DefaultProxySelector.props[i][localObject1] + "Port", 0).intValue();
/* 282 */             if ((j == 0) && (localObject1 < DefaultProxySelector.props[i].length - 1))
/*     */             {
/*     */ 
/*     */ 
/* 286 */               for (Object localObject2 = 1; localObject2 < DefaultProxySelector.props[i].length - 1; localObject2++) {
/* 287 */                 if ((localObject2 != localObject1) && (j == 0)) {
/* 288 */                   j = NetProperties.getInteger(DefaultProxySelector.props[i][localObject2] + "Port", 0).intValue();
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 293 */             if (j == 0) {
/* 294 */               if (localObject1 == DefaultProxySelector.props[i].length - 1) {
/* 295 */                 j = DefaultProxySelector.this.defaultPort("socket");
/*     */               } else {
/* 297 */                 j = DefaultProxySelector.this.defaultPort(str2);
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 302 */             localInetSocketAddress = InetSocketAddress.createUnresolved(str1, j);
/*     */             
/* 304 */             if (localObject1 == DefaultProxySelector.props[i].length - 1) {
/* 305 */               int k = NetProperties.getInteger("socksProxyVersion", 5).intValue();
/* 306 */               return SocksProxy.create(localInetSocketAddress, k);
/*     */             }
/* 308 */             return new Proxy(Type.HTTP, localInetSocketAddress);
/*     */           }
/*     */         }
/*     */         
/* 312 */         return Proxy.NO_PROXY;
/*     */       }
/* 314 */     });
/* 315 */     ((List)localObject2).add(localProxy);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 321 */     return (List<Proxy>)localObject2;
/*     */   }
/*     */   
/*     */   public void connectFailed(URI paramURI, SocketAddress paramSocketAddress, IOException paramIOException) {
/* 325 */     if ((paramURI == null) || (paramSocketAddress == null) || (paramIOException == null)) {
/* 326 */       throw new IllegalArgumentException("Arguments can't be null.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private int defaultPort(String paramString)
/*     */   {
/* 333 */     if ("http".equalsIgnoreCase(paramString))
/* 334 */       return 80;
/* 335 */     if ("https".equalsIgnoreCase(paramString))
/* 336 */       return 443;
/* 337 */     if ("ftp".equalsIgnoreCase(paramString))
/* 338 */       return 80;
/* 339 */     if ("socket".equalsIgnoreCase(paramString))
/* 340 */       return 1080;
/* 341 */     if ("gopher".equalsIgnoreCase(paramString)) {
/* 342 */       return 80;
/*     */     }
/* 344 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static boolean shouldNotUseProxyFor(Pattern paramPattern, String paramString)
/*     */   {
/* 356 */     if ((paramPattern == null) || (paramString.isEmpty()))
/* 357 */       return false;
/* 358 */     boolean bool = paramPattern.matcher(paramString).matches();
/* 359 */     return bool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Pattern toPattern(String paramString)
/*     */   {
/* 368 */     int i = 1;
/* 369 */     StringJoiner localStringJoiner = new StringJoiner("|");
/* 370 */     for (String str1 : paramString.split("\\|"))
/* 371 */       if (!str1.isEmpty())
/*     */       {
/* 373 */         i = 0;
/* 374 */         String str2 = disjunctToRegex(str1.toLowerCase());
/* 375 */         localStringJoiner.add(str2);
/*     */       }
/* 377 */     return i != 0 ? null : Pattern.compile(localStringJoiner.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static String disjunctToRegex(String paramString)
/*     */   {
/*     */     String str;
/*     */     
/* 386 */     if (paramString.startsWith("*")) {
/* 387 */       str = ".*" + Pattern.quote(paramString.substring(1));
/* 388 */     } else if (paramString.endsWith("*")) {
/* 389 */       str = Pattern.quote(paramString.substring(0, paramString.length() - 1)) + ".*";
/*     */     } else {
/* 391 */       str = Pattern.quote(paramString);
/*     */     }
/* 393 */     return str;
/*     */   }
/*     */   
/*     */   private static native boolean init();
/*     */   
/*     */   private synchronized native Proxy getSystemProxy(String paramString1, String paramString2);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\spi\DefaultProxySelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */