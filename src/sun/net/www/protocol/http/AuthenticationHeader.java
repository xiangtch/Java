/*     */ package sun.net.www.protocol.http;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import sun.net.www.HeaderParser;
/*     */ import sun.net.www.MessageHeader;
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
/*     */ public class AuthenticationHeader
/*     */ {
/*     */   MessageHeader rsp;
/*     */   HeaderParser preferred;
/*     */   String preferred_r;
/*     */   private final HttpCallerInfo hci;
/*  90 */   boolean dontUseNegotiate = false;
/*  91 */   static String authPref = null;
/*     */   String hdrname;
/*     */   
/*  94 */   public String toString() { return "AuthenticationHeader: prefer " + this.preferred_r; }
/*     */   
/*     */   static
/*     */   {
/*  98 */     authPref = (String)AccessController.doPrivileged(new GetPropertyAction("http.auth.preference"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 107 */     if (authPref != null) {
/* 108 */       authPref = authPref.toLowerCase();
/* 109 */       if ((authPref.equals("spnego")) || (authPref.equals("kerberos"))) {
/* 110 */         authPref = "negotiate";
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
/*     */   public AuthenticationHeader(String paramString, MessageHeader paramMessageHeader, HttpCallerInfo paramHttpCallerInfo, boolean paramBoolean)
/*     */   {
/* 123 */     this(paramString, paramMessageHeader, paramHttpCallerInfo, paramBoolean, Collections.emptySet());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   HashMap<String, SchemeMapValue> schemes;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AuthenticationHeader(String paramString, MessageHeader paramMessageHeader, HttpCallerInfo paramHttpCallerInfo, boolean paramBoolean, Set<String> paramSet)
/*     */   {
/* 138 */     this.hci = paramHttpCallerInfo;
/* 139 */     this.dontUseNegotiate = paramBoolean;
/* 140 */     this.rsp = paramMessageHeader;
/* 141 */     this.hdrname = paramString;
/* 142 */     this.schemes = new HashMap();
/* 143 */     parse(paramSet);
/*     */   }
/*     */   
/*     */ 
/* 147 */   public HttpCallerInfo getHttpCallerInfo() { return this.hci; }
/*     */   
/*     */   static class SchemeMapValue { String raw;
/*     */     
/* 151 */     SchemeMapValue(HeaderParser paramHeaderParser, String paramString) { this.raw = paramString;this.parser = paramHeaderParser;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     HeaderParser parser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void parse(Set<String> paramSet)
/*     */   {
/* 164 */     Iterator localIterator1 = this.rsp.multiValueIterator(this.hdrname);
/* 165 */     Object localObject2; while (localIterator1.hasNext()) {
/* 166 */       localObject1 = (String)localIterator1.next();
/*     */       
/* 168 */       localObject2 = new HeaderParser((String)localObject1);
/* 169 */       Iterator localIterator2 = ((HeaderParser)localObject2).keys();
/*     */       
/* 171 */       int i = 0; HeaderParser localHeaderParser; String str; for (int j = -1; localIterator2.hasNext(); i++) {
/* 172 */         localIterator2.next();
/* 173 */         if (((HeaderParser)localObject2).findValue(i) == null) {
/* 174 */           if (j != -1) {
/* 175 */             localHeaderParser = ((HeaderParser)localObject2).subsequence(j, i);
/* 176 */             str = localHeaderParser.findKey(0);
/* 177 */             if (!paramSet.contains(str))
/* 178 */               this.schemes.put(str, new SchemeMapValue(localHeaderParser, (String)localObject1));
/*     */           }
/* 180 */           j = i;
/*     */         }
/*     */       }
/* 183 */       if (i > j) {
/* 184 */         localHeaderParser = ((HeaderParser)localObject2).subsequence(j, i);
/* 185 */         str = localHeaderParser.findKey(0);
/* 186 */         if (!paramSet.contains(str)) {
/* 187 */           this.schemes.put(str, new SchemeMapValue(localHeaderParser, (String)localObject1));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 194 */     Object localObject1 = null;
/* 195 */     if ((authPref == null) || ((localObject1 = (SchemeMapValue)this.schemes.get(authPref)) == null))
/*     */     {
/* 197 */       if ((localObject1 == null) && (!this.dontUseNegotiate)) {
/* 198 */         localObject2 = (SchemeMapValue)this.schemes.get("negotiate");
/* 199 */         if (localObject2 != null) {
/* 200 */           if ((this.hci == null) || (!NegotiateAuthentication.isSupported(new HttpCallerInfo(this.hci, "Negotiate")))) {
/* 201 */             localObject2 = null;
/*     */           }
/* 203 */           localObject1 = localObject2;
/*     */         }
/*     */       }
/*     */       
/* 207 */       if ((localObject1 == null) && (!this.dontUseNegotiate)) {
/* 208 */         localObject2 = (SchemeMapValue)this.schemes.get("kerberos");
/* 209 */         if (localObject2 != null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 220 */           if ((this.hci == null) || (!NegotiateAuthentication.isSupported(new HttpCallerInfo(this.hci, "Kerberos")))) {
/* 221 */             localObject2 = null;
/*     */           }
/* 223 */           localObject1 = localObject2;
/*     */         }
/*     */       }
/*     */       
/* 227 */       if ((localObject1 == null) && 
/* 228 */         ((localObject1 = (SchemeMapValue)this.schemes.get("digest")) == null) && (
/* 229 */         (!NTLMAuthenticationProxy.supported) || 
/* 230 */         ((localObject1 = (SchemeMapValue)this.schemes.get("ntlm")) == null))) {
/* 231 */         localObject1 = (SchemeMapValue)this.schemes.get("basic");
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 236 */     else if ((this.dontUseNegotiate) && (authPref.equals("negotiate"))) {
/* 237 */       localObject1 = null;
/*     */     }
/*     */     
/*     */ 
/* 241 */     if (localObject1 != null) {
/* 242 */       this.preferred = ((SchemeMapValue)localObject1).parser;
/* 243 */       this.preferred_r = ((SchemeMapValue)localObject1).raw;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HeaderParser headerParser()
/*     */   {
/* 253 */     return this.preferred;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String scheme()
/*     */   {
/* 260 */     if (this.preferred != null) {
/* 261 */       return this.preferred.findKey(0);
/*     */     }
/* 263 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String raw()
/*     */   {
/* 270 */     return this.preferred_r;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isPresent()
/*     */   {
/* 277 */     return this.preferred != null;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\http\AuthenticationHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */