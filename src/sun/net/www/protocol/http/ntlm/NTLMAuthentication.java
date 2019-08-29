/*     */ package sun.net.www.protocol.http.ntlm;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.net.www.HeaderParser;
/*     */ import sun.net.www.protocol.http.AuthScheme;
/*     */ import sun.net.www.protocol.http.AuthenticationInfo;
/*     */ import sun.net.www.protocol.http.HttpURLConnection;
/*     */ import sun.security.action.GetPropertyAction;
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
/*     */ public class NTLMAuthentication
/*     */   extends AuthenticationInfo
/*     */ {
/*     */   private static final long serialVersionUID = 100L;
/*  49 */   private static final NTLMAuthenticationCallback NTLMAuthCallback = ;
/*     */   
/*     */ 
/*     */ 
/*     */   private String hostname;
/*     */   
/*     */ 
/*  56 */   private static String defaultDomain = (String)AccessController.doPrivileged(new GetPropertyAction("http.auth.ntlm.domain", "domain"));
/*     */   private static final boolean ntlmCache;
/*     */   
/*  59 */   static { String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.ntlm.cache", "true"));
/*     */     
/*  61 */     ntlmCache = Boolean.parseBoolean(str);
/*     */   }
/*     */   
/*     */   private void init0()
/*     */   {
/*  66 */     this.hostname = ((String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run() {
/*     */         String str;
/*     */         try {
/*  71 */           str = InetAddress.getLocalHost().getHostName().toUpperCase();
/*     */         } catch (UnknownHostException localUnknownHostException) {
/*  73 */           str = "localhost";
/*     */         }
/*  75 */         return str;
/*     */       }
/*  77 */     }));
/*  78 */     int i = this.hostname.indexOf('.');
/*  79 */     if (i != -1) {
/*  80 */       this.hostname = this.hostname.substring(0, i);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   String username;
/*     */   
/*     */ 
/*     */   String ntdomain;
/*     */   
/*     */   String password;
/*     */   
/*     */   public NTLMAuthentication(boolean paramBoolean, URL paramURL, PasswordAuthentication paramPasswordAuthentication)
/*     */   {
/*  95 */     super(paramBoolean ? 'p' : 's', AuthScheme.NTLM, paramURL, "");
/*     */     
/*     */ 
/*     */ 
/*  99 */     init(paramPasswordAuthentication);
/*     */   }
/*     */   
/*     */   private void init(PasswordAuthentication paramPasswordAuthentication) {
/* 103 */     this.pw = paramPasswordAuthentication;
/* 104 */     if (paramPasswordAuthentication != null) {
/* 105 */       String str = paramPasswordAuthentication.getUserName();
/* 106 */       int i = str.indexOf('\\');
/* 107 */       if (i == -1) {
/* 108 */         this.username = str;
/* 109 */         this.ntdomain = defaultDomain;
/*     */       } else {
/* 111 */         this.ntdomain = str.substring(0, i).toUpperCase();
/* 112 */         this.username = str.substring(i + 1);
/*     */       }
/* 114 */       this.password = new String(paramPasswordAuthentication.getPassword());
/*     */     }
/*     */     else {
/* 117 */       this.username = null;
/* 118 */       this.ntdomain = null;
/* 119 */       this.password = null;
/*     */     }
/* 121 */     init0();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public NTLMAuthentication(boolean paramBoolean, String paramString, int paramInt, PasswordAuthentication paramPasswordAuthentication)
/*     */   {
/* 129 */     super(paramBoolean ? 'p' : 's', AuthScheme.NTLM, paramString, paramInt, "");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 134 */     init(paramPasswordAuthentication);
/*     */   }
/*     */   
/*     */   protected boolean useAuthCache()
/*     */   {
/* 139 */     return (ntlmCache) && (super.useAuthCache());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean supportsPreemptiveAuthorization()
/*     */   {
/* 147 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean supportsTransparentAuth()
/*     */   {
/* 154 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isTrustedSite(URL paramURL)
/*     */   {
/* 162 */     return NTLMAuthCallback.isTrustedSite(paramURL);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHeaderValue(URL paramURL, String paramString)
/*     */   {
/* 170 */     throw new RuntimeException("getHeaderValue not supported");
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
/*     */   public boolean isAuthorizationStale(String paramString)
/*     */   {
/* 183 */     return false;
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
/*     */   public synchronized boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString)
/*     */   {
/*     */     try
/*     */     {
/* 198 */       NTLMAuthSequence localNTLMAuthSequence = (NTLMAuthSequence)paramHttpURLConnection.authObj();
/* 199 */       if (localNTLMAuthSequence == null) {
/* 200 */         localNTLMAuthSequence = new NTLMAuthSequence(this.username, this.password, this.ntdomain);
/* 201 */         paramHttpURLConnection.authObj(localNTLMAuthSequence);
/*     */       }
/* 203 */       String str = "NTLM " + localNTLMAuthSequence.getAuthHeader(paramString.length() > 6 ? paramString.substring(5) : null);
/* 204 */       paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), str);
/* 205 */       if (localNTLMAuthSequence.isComplete()) {
/* 206 */         paramHttpURLConnection.authObj(null);
/*     */       }
/* 208 */       return true;
/*     */     } catch (IOException localIOException) {
/* 210 */       paramHttpURLConnection.authObj(null); }
/* 211 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\http\ntlm\NTLMAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */