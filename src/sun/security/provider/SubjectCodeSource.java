/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.CodeSource;
/*     */ import java.security.Principal;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.ListIterator;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.Subject;
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
/*     */ class SubjectCodeSource
/*     */   extends CodeSource
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 6039418085604715275L;
/*  52 */   private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public ResourceBundle run()
/*     */     {
/*  54 */       return 
/*  55 */         ResourceBundle.getBundle("sun.security.util.AuthResources");
/*     */     }
/*  52 */   });
/*     */   
/*     */ 
/*     */   private Subject subject;
/*     */   
/*     */ 
/*     */   private LinkedList<PolicyParser.PrincipalEntry> principals;
/*     */   
/*     */ 
/*  61 */   private static final Class<?>[] PARAMS = { String.class };
/*     */   
/*  63 */   private static final Debug debug = Debug.getInstance("auth", "\t[Auth Access]");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ClassLoader sysClassLoader;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   SubjectCodeSource(Subject paramSubject, LinkedList<PolicyParser.PrincipalEntry> paramLinkedList, URL paramURL, Certificate[] paramArrayOfCertificate)
/*     */   {
/*  93 */     super(paramURL, paramArrayOfCertificate);
/*  94 */     this.subject = paramSubject;
/*  95 */     this.principals = (paramLinkedList == null ? new LinkedList() : new LinkedList(paramLinkedList));
/*     */     
/*     */ 
/*     */ 
/*  99 */     this.sysClassLoader = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public ClassLoader run() {
/* 101 */         return ClassLoader.getSystemClassLoader();
/*     */       }
/*     */     }));
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
/*     */   LinkedList<PolicyParser.PrincipalEntry> getPrincipals()
/*     */   {
/* 118 */     return this.principals;
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
/*     */   Subject getSubject()
/*     */   {
/* 133 */     return this.subject;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean implies(CodeSource paramCodeSource)
/*     */   {
/* 170 */     LinkedList localLinkedList = null;
/*     */     
/* 172 */     if ((paramCodeSource == null) || (!(paramCodeSource instanceof SubjectCodeSource)) || 
/*     */     
/* 174 */       (!super.implies(paramCodeSource)))
/*     */     {
/* 176 */       if (debug != null)
/* 177 */         debug.println("\tSubjectCodeSource.implies: FAILURE 1");
/* 178 */       return false;
/*     */     }
/*     */     
/* 181 */     SubjectCodeSource localSubjectCodeSource = (SubjectCodeSource)paramCodeSource;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 187 */     if (this.principals == null) {
/* 188 */       if (debug != null)
/* 189 */         debug.println("\tSubjectCodeSource.implies: PASS 1");
/* 190 */       return true;
/*     */     }
/*     */     
/* 193 */     if ((localSubjectCodeSource.getSubject() == null) || 
/* 194 */       (localSubjectCodeSource.getSubject().getPrincipals().size() == 0)) {
/* 195 */       if (debug != null)
/* 196 */         debug.println("\tSubjectCodeSource.implies: FAILURE 2");
/* 197 */       return false;
/*     */     }
/*     */     
/* 200 */     ListIterator localListIterator = this.principals.listIterator(0);
/* 201 */     while (localListIterator.hasNext()) {
/* 202 */       PolicyParser.PrincipalEntry localPrincipalEntry1 = (PolicyParser.PrincipalEntry)localListIterator.next();
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 207 */         Class localClass = Class.forName(localPrincipalEntry1.principalClass, true, this.sysClassLoader);
/*     */         
/* 209 */         if (!Principal.class.isAssignableFrom(localClass))
/*     */         {
/* 211 */           throw new ClassCastException(localPrincipalEntry1.principalClass + " is not a Principal");
/*     */         }
/*     */         
/* 214 */         localObject = localClass.getConstructor(PARAMS);
/* 215 */         localPrincipal = (Principal)((Constructor)localObject).newInstance(new Object[] { localPrincipalEntry1.principalName });
/*     */         
/*     */ 
/* 218 */         if (!localPrincipal.implies(localSubjectCodeSource.getSubject())) {
/* 219 */           if (debug != null)
/* 220 */             debug.println("\tSubjectCodeSource.implies: FAILURE 3");
/* 221 */           return false;
/*     */         }
/* 223 */         if (debug != null)
/* 224 */           debug.println("\tSubjectCodeSource.implies: PASS 2");
/* 225 */         return true;
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */         Object localObject;
/*     */         Principal localPrincipal;
/* 231 */         if (localLinkedList == null)
/*     */         {
/* 233 */           if (localSubjectCodeSource.getSubject() == null) {
/* 234 */             if (debug != null) {
/* 235 */               debug.println("\tSubjectCodeSource.implies: FAILURE 4");
/*     */             }
/* 237 */             return false;
/*     */           }
/*     */           
/* 240 */           localObject = localSubjectCodeSource.getSubject().getPrincipals().iterator();
/*     */           
/* 242 */           localLinkedList = new LinkedList();
/* 243 */           while (((Iterator)localObject).hasNext()) {
/* 244 */             localPrincipal = (Principal)((Iterator)localObject).next();
/*     */             
/* 246 */             PolicyParser.PrincipalEntry localPrincipalEntry2 = new PolicyParser.PrincipalEntry(localPrincipal.getClass().getName(), localPrincipal.getName());
/* 247 */             localLinkedList.add(localPrincipalEntry2);
/*     */           }
/*     */         }
/*     */         
/* 251 */         if (!subjectListImpliesPrincipalEntry(localLinkedList, localPrincipalEntry1)) {
/* 252 */           if (debug != null)
/* 253 */             debug.println("\tSubjectCodeSource.implies: FAILURE 5");
/* 254 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 259 */     if (debug != null)
/* 260 */       debug.println("\tSubjectCodeSource.implies: PASS 3");
/* 261 */     return true;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean subjectListImpliesPrincipalEntry(LinkedList<PolicyParser.PrincipalEntry> paramLinkedList, PolicyParser.PrincipalEntry paramPrincipalEntry)
/*     */   {
/* 288 */     ListIterator localListIterator = paramLinkedList.listIterator(0);
/* 289 */     while (localListIterator.hasNext()) {
/* 290 */       PolicyParser.PrincipalEntry localPrincipalEntry = (PolicyParser.PrincipalEntry)localListIterator.next();
/*     */       
/*     */ 
/* 293 */       if ((paramPrincipalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS")) || 
/* 294 */         (paramPrincipalEntry.getPrincipalClass().equals(localPrincipalEntry.getPrincipalClass())))
/*     */       {
/*     */ 
/* 297 */         if ((paramPrincipalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME")) || 
/* 298 */           (paramPrincipalEntry.getPrincipalName().equals(localPrincipalEntry.getPrincipalName())))
/* 299 */           return true;
/*     */       }
/*     */     }
/* 302 */     return false;
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
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 323 */     if (paramObject == this) {
/* 324 */       return true;
/*     */     }
/* 326 */     if (!super.equals(paramObject)) {
/* 327 */       return false;
/*     */     }
/* 329 */     if (!(paramObject instanceof SubjectCodeSource)) {
/* 330 */       return false;
/*     */     }
/* 332 */     SubjectCodeSource localSubjectCodeSource = (SubjectCodeSource)paramObject;
/*     */     
/*     */     try
/*     */     {
/* 336 */       if (getSubject() != localSubjectCodeSource.getSubject())
/* 337 */         return false;
/*     */     } catch (SecurityException localSecurityException) {
/* 339 */       return false;
/*     */     }
/*     */     
/* 342 */     if (((this.principals == null) && (localSubjectCodeSource.principals != null)) || ((this.principals != null) && (localSubjectCodeSource.principals == null)))
/*     */     {
/* 344 */       return false;
/*     */     }
/* 346 */     if ((this.principals != null) && (localSubjectCodeSource.principals != null) && (
/* 347 */       (!this.principals.containsAll(localSubjectCodeSource.principals)) || 
/* 348 */       (!localSubjectCodeSource.principals.containsAll(this.principals))))
/*     */     {
/* 350 */       return false;
/*     */     }
/*     */     
/* 353 */     return true;
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
/* 364 */     return super.hashCode();
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
/* 375 */     String str = super.toString();
/* 376 */     final Object localObject; if (getSubject() != null) {
/* 377 */       if (debug != null) {
/* 378 */         localObject = getSubject();
/*     */         
/*     */ 
/* 381 */         str = str + "\n" + (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public String run()
/*     */           {
/* 383 */             return localObject.toString();
/*     */           }
/*     */         });
/*     */       } else {
/* 387 */         str = str + "\n" + getSubject().toString();
/*     */       }
/*     */     }
/* 390 */     if (this.principals != null) {
/* 391 */       localObject = this.principals.listIterator();
/* 392 */       while (((ListIterator)localObject).hasNext()) {
/* 393 */         PolicyParser.PrincipalEntry localPrincipalEntry = (PolicyParser.PrincipalEntry)((ListIterator)localObject).next();
/*     */         
/*     */ 
/* 396 */         str = str + rb.getString("NEWLINE") + localPrincipalEntry.getPrincipalClass() + " " + localPrincipalEntry.getPrincipalName();
/*     */       }
/*     */     }
/* 399 */     return str;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\SubjectCodeSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */