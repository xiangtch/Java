/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BuildStep
/*     */ {
/*     */   private Vertex vertex;
/*     */   private X509Certificate cert;
/*     */   private Throwable throwable;
/*     */   private int result;
/*     */   public static final int POSSIBLE = 1;
/*     */   public static final int BACK = 2;
/*     */   public static final int FOLLOW = 3;
/*     */   public static final int FAIL = 4;
/*     */   public static final int SUCCEED = 5;
/*     */   
/*     */   public BuildStep(Vertex paramVertex, int paramInt)
/*     */   {
/*  85 */     this.vertex = paramVertex;
/*  86 */     if (this.vertex != null) {
/*  87 */       this.cert = this.vertex.getCertificate();
/*  88 */       this.throwable = this.vertex.getThrowable();
/*     */     }
/*  90 */     this.result = paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Vertex getVertex()
/*     */   {
/*  99 */     return this.vertex;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate getCertificate()
/*     */   {
/* 108 */     return this.cert;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getIssuerName()
/*     */   {
/* 118 */     return getIssuerName(null);
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
/*     */   public String getIssuerName(String paramString)
/*     */   {
/* 132 */     return this.cert == null ? paramString : this.cert
/* 133 */       .getIssuerX500Principal().toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getSubjectName()
/*     */   {
/* 143 */     return getSubjectName(null);
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
/*     */   public String getSubjectName(String paramString)
/*     */   {
/* 159 */     return this.cert == null ? paramString : this.cert
/* 160 */       .getSubjectX500Principal().toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Throwable getThrowable()
/*     */   {
/* 169 */     return this.throwable;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getResult()
/*     */   {
/* 179 */     return this.result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String resultToString(int paramInt)
/*     */   {
/* 190 */     String str = "";
/* 191 */     switch (paramInt) {
/*     */     case 1: 
/* 193 */       str = "Certificate to be tried.\n";
/* 194 */       break;
/*     */     case 2: 
/* 196 */       str = "Certificate backed out since path does not satisfy build requirements.\n";
/*     */       
/* 198 */       break;
/*     */     case 3: 
/* 200 */       str = "Certificate satisfies conditions.\n";
/* 201 */       break;
/*     */     case 4: 
/* 203 */       str = "Certificate backed out since path does not satisfy conditions.\n";
/*     */       
/* 205 */       break;
/*     */     case 5: 
/* 207 */       str = "Certificate satisfies conditions.\n";
/* 208 */       break;
/*     */     default: 
/* 210 */       str = "Internal error: Invalid step result value.\n";
/*     */     }
/* 212 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 223 */     String str = "Internal Error\n";
/* 224 */     switch (this.result) {
/*     */     case 2: 
/*     */     case 4: 
/* 227 */       str = resultToString(this.result);
/* 228 */       str = str + this.vertex.throwableToString();
/* 229 */       break;
/*     */     case 1: 
/*     */     case 3: 
/*     */     case 5: 
/* 233 */       str = resultToString(this.result);
/* 234 */       break;
/*     */     default: 
/* 236 */       str = "Internal Error: Invalid step result\n";
/*     */     }
/* 238 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String verboseToString()
/*     */   {
/* 249 */     String str = resultToString(getResult());
/* 250 */     switch (this.result) {
/*     */     case 2: 
/*     */     case 4: 
/* 253 */       str = str + this.vertex.throwableToString();
/* 254 */       break;
/*     */     case 3: 
/*     */     case 5: 
/* 257 */       str = str + this.vertex.moreToString();
/* 258 */       break;
/*     */     case 1: 
/*     */       break;
/*     */     }
/*     */     
/*     */     
/* 264 */     str = str + "Certificate contains:\n" + this.vertex.certToString();
/* 265 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String fullToString()
/*     */   {
/* 275 */     return resultToString(getResult()) + this.vertex.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\BuildStep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */