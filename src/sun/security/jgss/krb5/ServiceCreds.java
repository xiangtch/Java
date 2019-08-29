/*     */ package sun.security.jgss.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.Subject;
/*     */ import javax.security.auth.kerberos.KerberosKey;
/*     */ import javax.security.auth.kerberos.KerberosPrincipal;
/*     */ import javax.security.auth.kerberos.KerberosTicket;
/*     */ import sun.security.krb5.Credentials;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ServiceCreds
/*     */ {
/*     */   private KerberosPrincipal kp;
/*     */   private Set<KerberosPrincipal> allPrincs;
/*     */   private List<javax.security.auth.kerberos.KeyTab> ktabs;
/*     */   private List<KerberosKey> kk;
/*     */   private KerberosTicket tgt;
/*     */   private boolean destroyed;
/*     */   
/*     */   public static ServiceCreds getInstance(Subject paramSubject, String paramString)
/*     */   {
/*  90 */     ServiceCreds localServiceCreds = new ServiceCreds();
/*     */     
/*     */ 
/*  93 */     localServiceCreds.allPrincs = paramSubject.getPrincipals(KerberosPrincipal.class);
/*     */     
/*     */ 
/*  96 */     for (Iterator localIterator = SubjectComber.findMany(paramSubject, paramString, null, KerberosKey.class).iterator(); localIterator.hasNext();) { localObject = (KerberosKey)localIterator.next();
/*     */       
/*  98 */       localServiceCreds.allPrincs.add(((KerberosKey)localObject).getPrincipal());
/*     */     }
/*     */     Object localObject;
/* 101 */     if (paramString != null) {
/* 102 */       localServiceCreds.kp = new KerberosPrincipal(paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/* 107 */     else if (localServiceCreds.allPrincs.size() == 1) {
/* 108 */       int i = 0;
/* 109 */       for (localObject = SubjectComber.findMany(paramSubject, null, null, javax.security.auth.kerberos.KeyTab.class).iterator(); ((Iterator)localObject).hasNext();) { javax.security.auth.kerberos.KeyTab localKeyTab = (javax.security.auth.kerberos.KeyTab)((Iterator)localObject).next();
/*     */         
/* 111 */         if (!localKeyTab.isBound()) {
/* 112 */           i = 1;
/* 113 */           break;
/*     */         }
/*     */       }
/* 116 */       if (i == 0) {
/* 117 */         localServiceCreds.kp = ((KerberosPrincipal)localServiceCreds.allPrincs.iterator().next());
/* 118 */         paramString = localServiceCreds.kp.getName();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 123 */     localServiceCreds.ktabs = SubjectComber.findMany(paramSubject, paramString, null, javax.security.auth.kerberos.KeyTab.class);
/*     */     
/* 125 */     localServiceCreds.kk = SubjectComber.findMany(paramSubject, paramString, null, KerberosKey.class);
/*     */     
/* 127 */     localServiceCreds.tgt = ((KerberosTicket)SubjectComber.find(paramSubject, null, paramString, KerberosTicket.class));
/*     */     
/* 129 */     if ((localServiceCreds.ktabs.isEmpty()) && (localServiceCreds.kk.isEmpty()) && (localServiceCreds.tgt == null)) {
/* 130 */       return null;
/*     */     }
/*     */     
/* 133 */     localServiceCreds.destroyed = false;
/*     */     
/* 135 */     return localServiceCreds;
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/* 140 */     if (this.destroyed) {
/* 141 */       throw new IllegalStateException("This object is destroyed");
/*     */     }
/* 143 */     return this.kp == null ? null : this.kp.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public KerberosKey[] getKKeys()
/*     */   {
/* 153 */     if (this.destroyed) {
/* 154 */       throw new IllegalStateException("This object is destroyed");
/*     */     }
/* 156 */     KerberosPrincipal localKerberosPrincipal = this.kp;
/* 157 */     if ((localKerberosPrincipal == null) && (!this.allPrincs.isEmpty())) {
/* 158 */       localKerberosPrincipal = (KerberosPrincipal)this.allPrincs.iterator().next();
/*     */     }
/* 160 */     if (localKerberosPrincipal == null) {
/* 161 */       for (javax.security.auth.kerberos.KeyTab localKeyTab : this.ktabs)
/*     */       {
/*     */ 
/* 164 */         PrincipalName localPrincipalName = Krb5Util.snapshotFromJavaxKeyTab(localKeyTab).getOneName();
/* 165 */         if (localPrincipalName != null) {
/* 166 */           localKerberosPrincipal = new KerberosPrincipal(localPrincipalName.getName());
/* 167 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 171 */     if (localKerberosPrincipal != null) {
/* 172 */       return getKKeys(localKerberosPrincipal);
/*     */     }
/* 174 */     return new KerberosKey[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public KerberosKey[] getKKeys(KerberosPrincipal paramKerberosPrincipal)
/*     */   {
/* 184 */     if (this.destroyed) {
/* 185 */       throw new IllegalStateException("This object is destroyed");
/*     */     }
/* 187 */     ArrayList localArrayList = new ArrayList();
/* 188 */     if ((this.kp != null) && (!paramKerberosPrincipal.equals(this.kp))) {
/* 189 */       return new KerberosKey[0];
/*     */     }
/* 191 */     for (Iterator localIterator = this.kk.iterator(); localIterator.hasNext();) { localObject = (KerberosKey)localIterator.next();
/* 192 */       if (((KerberosKey)localObject).getPrincipal().equals(paramKerberosPrincipal))
/* 193 */         localArrayList.add(localObject);
/*     */     }
/*     */     Object localObject;
/* 196 */     for (localIterator = this.ktabs.iterator(); localIterator.hasNext();) { localObject = (javax.security.auth.kerberos.KeyTab)localIterator.next();
/* 197 */       if ((((javax.security.auth.kerberos.KeyTab)localObject).getPrincipal() != null) || (!((javax.security.auth.kerberos.KeyTab)localObject).isBound()) || 
/*     */       
/*     */ 
/* 200 */         (this.allPrincs.contains(paramKerberosPrincipal)))
/*     */       {
/*     */ 
/*     */ 
/* 204 */         for (KerberosKey localKerberosKey : ((javax.security.auth.kerberos.KeyTab)localObject).getKeys(paramKerberosPrincipal))
/* 205 */           localArrayList.add(localKerberosKey);
/*     */       }
/*     */     }
/* 208 */     return (KerberosKey[])localArrayList.toArray(new KerberosKey[localArrayList.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public EncryptionKey[] getEKeys(PrincipalName paramPrincipalName)
/*     */   {
/* 217 */     if (this.destroyed) {
/* 218 */       throw new IllegalStateException("This object is destroyed");
/*     */     }
/* 220 */     KerberosKey[] arrayOfKerberosKey = getKKeys(new KerberosPrincipal(paramPrincipalName.getName()));
/* 221 */     if (arrayOfKerberosKey.length == 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 228 */       arrayOfKerberosKey = getKKeys();
/*     */     }
/* 230 */     EncryptionKey[] arrayOfEncryptionKey = new EncryptionKey[arrayOfKerberosKey.length];
/* 231 */     for (int i = 0; i < arrayOfEncryptionKey.length; i++)
/*     */     {
/*     */ 
/* 234 */       arrayOfEncryptionKey[i] = new EncryptionKey(arrayOfKerberosKey[i].getEncoded(), arrayOfKerberosKey[i].getKeyType(), new Integer(arrayOfKerberosKey[i].getVersionNumber()));
/*     */     }
/* 236 */     return arrayOfEncryptionKey;
/*     */   }
/*     */   
/*     */   public Credentials getInitCred() {
/* 240 */     if (this.destroyed) {
/* 241 */       throw new IllegalStateException("This object is destroyed");
/*     */     }
/* 243 */     if (this.tgt == null) {
/* 244 */       return null;
/*     */     }
/*     */     try {
/* 247 */       return Krb5Util.ticketToCreds(this.tgt);
/*     */     } catch (KrbException|IOException localKrbException) {}
/* 249 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 256 */     this.destroyed = true;
/* 257 */     this.kp = null;
/* 258 */     this.ktabs.clear();
/* 259 */     this.kk.clear();
/* 260 */     this.tgt = null;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\krb5\ServiceCreds.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */