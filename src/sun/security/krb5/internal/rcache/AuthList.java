/*     */ package sun.security.krb5.internal.rcache;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.ListIterator;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.KrbApErrException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AuthList
/*     */ {
/*     */   private final LinkedList<AuthTimeWithHash> entries;
/*     */   private final int lifespan;
/*     */   
/*     */   public AuthList(int paramInt)
/*     */   {
/*  62 */     this.lifespan = paramInt;
/*  63 */     this.entries = new LinkedList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(AuthTimeWithHash paramAuthTimeWithHash, KerberosTime paramKerberosTime)
/*     */     throws KrbApErrException
/*     */   {
/*  73 */     if (this.entries.isEmpty()) {
/*  74 */       this.entries.addFirst(paramAuthTimeWithHash);
/*     */     } else {
/*  76 */       AuthTimeWithHash localAuthTimeWithHash1 = (AuthTimeWithHash)this.entries.getFirst();
/*  77 */       int i = localAuthTimeWithHash1.compareTo(paramAuthTimeWithHash);
/*  78 */       if (i < 0)
/*     */       {
/*     */ 
/*  81 */         this.entries.addFirst(paramAuthTimeWithHash);
/*  82 */       } else { if (i == 0) {
/*  83 */           throw new KrbApErrException(34);
/*     */         }
/*     */         
/*  86 */         localListIterator = this.entries.listIterator(1);
/*  87 */         int j = 0;
/*  88 */         while (localListIterator.hasNext()) {
/*  89 */           localAuthTimeWithHash1 = (AuthTimeWithHash)localListIterator.next();
/*  90 */           i = localAuthTimeWithHash1.compareTo(paramAuthTimeWithHash);
/*  91 */           if (i < 0)
/*     */           {
/*  93 */             this.entries.add(this.entries.indexOf(localAuthTimeWithHash1), paramAuthTimeWithHash);
/*  94 */             j = 1;
/*     */           }
/*  96 */           else if (i == 0) {
/*  97 */             throw new KrbApErrException(34);
/*     */           }
/*     */         }
/* 100 */         if (j == 0)
/*     */         {
/* 102 */           this.entries.addLast(paramAuthTimeWithHash);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 108 */     long l = paramKerberosTime.getSeconds() - this.lifespan;
/* 109 */     ListIterator localListIterator = this.entries.listIterator(0);
/* 110 */     AuthTimeWithHash localAuthTimeWithHash2 = null;
/* 111 */     int k = -1;
/* 112 */     while (localListIterator.hasNext())
/*     */     {
/* 114 */       localAuthTimeWithHash2 = (AuthTimeWithHash)localListIterator.next();
/* 115 */       if (localAuthTimeWithHash2.ctime < l) {
/* 116 */         k = this.entries.indexOf(localAuthTimeWithHash2);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 121 */     if (k > -1) {
/*     */       do
/*     */       {
/* 124 */         this.entries.removeLast();
/* 125 */       } while (this.entries.size() > k);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 130 */     return this.entries.isEmpty();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 134 */     StringBuilder localStringBuilder = new StringBuilder();
/* 135 */     Iterator localIterator = this.entries.descendingIterator();
/* 136 */     int i = this.entries.size();
/* 137 */     while (localIterator.hasNext()) {
/* 138 */       AuthTimeWithHash localAuthTimeWithHash = (AuthTimeWithHash)localIterator.next();
/* 139 */       localStringBuilder.append('#').append(i--).append(": ")
/* 140 */         .append(localAuthTimeWithHash.toString()).append('\n');
/*     */     }
/* 142 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\rcache\AuthList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */