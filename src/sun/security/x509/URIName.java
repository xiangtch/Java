/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URIName
/*     */   implements GeneralNameInterface
/*     */ {
/*     */   private URI uri;
/*     */   private String host;
/*     */   private DNSName hostDNS;
/*     */   private IPAddressName hostIP;
/*     */   
/*     */   public URIName(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/*  96 */     this(paramDerValue.getIA5String());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public URIName(String paramString)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 107 */       this.uri = new URI(paramString);
/*     */     } catch (URISyntaxException localURISyntaxException) {
/* 109 */       throw new IOException("invalid URI name:" + paramString, localURISyntaxException);
/*     */     }
/* 111 */     if (this.uri.getScheme() == null) {
/* 112 */       throw new IOException("URI name must include scheme:" + paramString);
/*     */     }
/*     */     
/* 115 */     this.host = this.uri.getHost();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 120 */     if (this.host != null) {
/* 121 */       if (this.host.charAt(0) == '[')
/*     */       {
/* 123 */         String str = this.host.substring(1, this.host.length() - 1);
/*     */         try {
/* 125 */           this.hostIP = new IPAddressName(str);
/*     */         } catch (IOException localIOException2) {
/* 127 */           throw new IOException("invalid URI name (host portion is not a valid IPv6 address):" + paramString);
/*     */         }
/*     */       }
/*     */       else {
/*     */         try {
/* 132 */           this.hostDNS = new DNSName(this.host);
/*     */         }
/*     */         catch (IOException localIOException1)
/*     */         {
/*     */           try {
/* 137 */             this.hostIP = new IPAddressName(this.host);
/*     */           } catch (Exception localException) {
/* 139 */             throw new IOException("invalid URI name (host portion is not a valid DNS name, IPv4 address, or IPv6 address):" + paramString);
/*     */           }
/*     */         }
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static URIName nameConstraint(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/* 158 */     String str1 = paramDerValue.getIA5String();
/*     */     URI localURI;
/* 160 */     try { localURI = new URI(str1);
/*     */     } catch (URISyntaxException localURISyntaxException) {
/* 162 */       throw new IOException("invalid URI name constraint:" + str1, localURISyntaxException);
/*     */     }
/* 164 */     if (localURI.getScheme() == null) {
/* 165 */       String str2 = localURI.getSchemeSpecificPart();
/*     */       try {
/*     */         DNSName localDNSName;
/* 168 */         if (str2.startsWith(".")) {
/* 169 */           localDNSName = new DNSName(str2.substring(1));
/*     */         } else {
/* 171 */           localDNSName = new DNSName(str2);
/*     */         }
/* 173 */         return new URIName(localURI, str2, localDNSName);
/*     */       } catch (IOException localIOException) {
/* 175 */         throw new IOException("invalid URI name constraint:" + str1, localIOException);
/*     */       }
/*     */     }
/* 178 */     throw new IOException("invalid URI name constraint (should not include scheme):" + str1);
/*     */   }
/*     */   
/*     */ 
/*     */   URIName(URI paramURI, String paramString, DNSName paramDNSName)
/*     */   {
/* 184 */     this.uri = paramURI;
/* 185 */     this.host = paramString;
/* 186 */     this.hostDNS = paramDNSName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getType()
/*     */   {
/* 193 */     return 6;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 203 */     paramDerOutputStream.putIA5String(this.uri.toASCIIString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 210 */     return "URIName: " + this.uri.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 219 */     if (this == paramObject) {
/* 220 */       return true;
/*     */     }
/*     */     
/* 223 */     if (!(paramObject instanceof URIName)) {
/* 224 */       return false;
/*     */     }
/*     */     
/* 227 */     URIName localURIName = (URIName)paramObject;
/*     */     
/* 229 */     return this.uri.equals(localURIName.getURI());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public URI getURI()
/*     */   {
/* 236 */     return this.uri;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 243 */     return this.uri.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getScheme()
/*     */   {
/* 252 */     return this.uri.getScheme();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHost()
/*     */   {
/* 261 */     return this.host;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getHostObject()
/*     */   {
/* 272 */     if (this.hostIP != null) {
/* 273 */       return this.hostIP;
/*     */     }
/* 275 */     return this.hostDNS;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 285 */     return this.uri.hashCode();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int constrains(GeneralNameInterface paramGeneralNameInterface)
/*     */     throws UnsupportedOperationException
/*     */   {
/*     */     int i;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 320 */     if (paramGeneralNameInterface == null) {
/* 321 */       i = -1;
/* 322 */     } else if (paramGeneralNameInterface.getType() != 6) {
/* 323 */       i = -1;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 329 */       String str = ((URIName)paramGeneralNameInterface).getHost();
/*     */       
/*     */ 
/* 332 */       if (str.equalsIgnoreCase(this.host)) {
/* 333 */         i = 0;
/*     */       } else {
/* 335 */         Object localObject = ((URIName)paramGeneralNameInterface).getHostObject();
/*     */         
/* 337 */         if ((this.hostDNS == null) || (!(localObject instanceof DNSName)))
/*     */         {
/*     */ 
/* 340 */           i = 3;
/*     */         }
/*     */         else {
/* 343 */           int j = this.host.charAt(0) == '.' ? 1 : 0;
/* 344 */           int k = str.charAt(0) == '.' ? 1 : 0;
/* 345 */           DNSName localDNSName = (DNSName)localObject;
/*     */           
/*     */ 
/* 348 */           i = this.hostDNS.constrains(localDNSName);
/*     */           
/*     */ 
/* 351 */           if ((j == 0) && (k == 0) && ((i == 2) || (i == 1)))
/*     */           {
/*     */ 
/* 354 */             i = 3;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 361 */           if ((j != k) && (i == 0))
/*     */           {
/* 363 */             if (j != 0) {
/* 364 */               i = 2;
/*     */             } else {
/* 366 */               i = 1;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 372 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int subtreeDepth()
/*     */     throws UnsupportedOperationException
/*     */   {
/* 384 */     DNSName localDNSName = null;
/*     */     try {
/* 386 */       localDNSName = new DNSName(this.host);
/*     */     } catch (IOException localIOException) {
/* 388 */       throw new UnsupportedOperationException(localIOException.getMessage());
/*     */     }
/* 390 */     return localDNSName.subtreeDepth();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\URIName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */