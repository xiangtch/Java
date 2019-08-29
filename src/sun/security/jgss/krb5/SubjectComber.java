/*     */ package sun.security.jgss.krb5;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.DestroyFailedException;
/*     */ import javax.security.auth.Subject;
/*     */ import javax.security.auth.kerberos.KerberosKey;
/*     */ import javax.security.auth.kerberos.KerberosPrincipal;
/*     */ import javax.security.auth.kerberos.KerberosTicket;
/*     */ import javax.security.auth.kerberos.KeyTab;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SubjectComber
/*     */ {
/*  49 */   private static final boolean DEBUG = Krb5Util.DEBUG;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static <T> T find(Subject paramSubject, String paramString1, String paramString2, Class<T> paramClass)
/*     */   {
/*  61 */     return (T)paramClass.cast(findAux(paramSubject, paramString1, paramString2, paramClass, true));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static <T> List<T> findMany(Subject paramSubject, String paramString1, String paramString2, Class<T> paramClass)
/*     */   {
/*  69 */     return (List)findAux(paramSubject, paramString1, paramString2, paramClass, false);
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
/*     */   private static <T> Object findAux(Subject paramSubject, String paramString1, String paramString2, Class<T> paramClass, boolean paramBoolean)
/*     */   {
/*  83 */     if (paramSubject == null) {
/*  84 */       return null;
/*     */     }
/*  86 */     ArrayList localArrayList = paramBoolean ? null : new ArrayList();
/*     */     Object localObject1;
/*  88 */     Object localObject2; Object localObject3; Object localObject5; if (paramClass == KeyTab.class)
/*     */     {
/*  90 */       localObject1 = paramSubject.getPrivateCredentials(KeyTab.class).iterator();
/*  91 */       while (((Iterator)localObject1).hasNext()) {
/*  92 */         localObject2 = (KeyTab)((Iterator)localObject1).next();
/*  93 */         if ((paramString1 != null) && (((KeyTab)localObject2).isBound())) {
/*  94 */           localObject3 = ((KeyTab)localObject2).getPrincipal();
/*  95 */           if (localObject3 != null) {
/*  96 */             if (paramString1.equals(((KerberosPrincipal)localObject3).getName())) {}
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/* 102 */             int i = 0;
/*     */             
/* 104 */             for (localObject5 = paramSubject.getPrincipals(KerberosPrincipal.class).iterator(); ((Iterator)localObject5).hasNext();) { KerberosPrincipal localKerberosPrincipal = (KerberosPrincipal)((Iterator)localObject5).next();
/* 105 */               if (localKerberosPrincipal.getName().equals(paramString1)) {
/* 106 */                 i = 1;
/* 107 */                 break;
/*     */               }
/*     */             }
/* 110 */             if (i == 0)
/*     */               continue;
/*     */           }
/*     */         }
/* 114 */         if (DEBUG) {
/* 115 */           System.out.println("Found " + paramClass.getSimpleName() + " " + localObject2);
/*     */         }
/*     */         
/* 118 */         if (paramBoolean) {
/* 119 */           return localObject2;
/*     */         }
/* 121 */         localArrayList.add(paramClass.cast(localObject2));
/*     */       }
/*     */     }
/* 124 */     else if (paramClass == KerberosKey.class)
/*     */     {
/*     */ 
/* 127 */       localObject1 = paramSubject.getPrivateCredentials(KerberosKey.class).iterator();
/* 128 */       while (((Iterator)localObject1).hasNext()) {
/* 129 */         localObject2 = (KerberosKey)((Iterator)localObject1).next();
/* 130 */         localObject3 = ((KerberosKey)localObject2).getPrincipal().getName();
/* 131 */         if ((paramString1 == null) || (paramString1.equals(localObject3))) {
/* 132 */           if (DEBUG) {
/* 133 */             System.out.println("Found " + paramClass
/* 134 */               .getSimpleName() + " for " + (String)localObject3);
/*     */           }
/* 136 */           if (paramBoolean) {
/* 137 */             return localObject2;
/*     */           }
/* 139 */           localArrayList.add(paramClass.cast(localObject2));
/*     */         }
/*     */       }
/*     */     }
/* 143 */     else if (paramClass == KerberosTicket.class)
/*     */     {
/*     */ 
/* 146 */       localObject1 = paramSubject.getPrivateCredentials();
/* 147 */       synchronized (localObject1) {
/* 148 */         localObject3 = ((Set)localObject1).iterator();
/* 149 */         while (((Iterator)localObject3).hasNext()) {
/* 150 */           Object localObject4 = ((Iterator)localObject3).next();
/* 151 */           if ((localObject4 instanceof KerberosTicket))
/*     */           {
/* 153 */             localObject5 = (KerberosTicket)localObject4;
/* 154 */             if (DEBUG) {
/* 155 */               System.out.println("Found ticket for " + ((KerberosTicket)localObject5)
/* 156 */                 .getClient() + " to go to " + ((KerberosTicket)localObject5)
/*     */                 
/* 158 */                 .getServer() + " expiring on " + ((KerberosTicket)localObject5)
/*     */                 
/* 160 */                 .getEndTime());
/*     */             }
/* 162 */             if (!((KerberosTicket)localObject5).isCurrent())
/*     */             {
/*     */ 
/*     */ 
/* 166 */               if (!paramSubject.isReadOnly()) {
/* 167 */                 ((Iterator)localObject3).remove();
/*     */                 try {
/* 169 */                   ((KerberosTicket)localObject5).destroy();
/* 170 */                   if (DEBUG) {
/* 171 */                     System.out.println("Removed and destroyed the expired Ticket \n" + localObject5);
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 catch (DestroyFailedException localDestroyFailedException)
/*     */                 {
/* 177 */                   if (DEBUG) {
/* 178 */                     System.out.println("Expired ticket not detroyed successfully. " + localDestroyFailedException);
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 
/*     */               }
/*     */             }
/* 185 */             else if ((paramString1 == null) || 
/* 186 */               (((KerberosTicket)localObject5).getServer().getName().equals(paramString1)))
/*     */             {
/* 188 */               if ((paramString2 == null) || 
/* 189 */                 (paramString2.equals(((KerberosTicket)localObject5)
/* 190 */                 .getClient().getName()))) {
/* 191 */                 if (paramBoolean) {
/* 192 */                   return localObject5;
/*     */                 }
/*     */                 
/*     */ 
/* 196 */                 if (paramString2 == null)
/*     */                 {
/* 198 */                   paramString2 = ((KerberosTicket)localObject5).getClient().getName();
/*     */                 }
/* 200 */                 if (paramString1 == null)
/*     */                 {
/* 202 */                   paramString1 = ((KerberosTicket)localObject5).getServer().getName();
/*     */                 }
/* 204 */                 localArrayList.add(paramClass.cast(localObject5));
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 213 */     return localArrayList;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\krb5\SubjectComber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */