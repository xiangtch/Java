/*     */ package sun.net.www.protocol.http;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.ListIterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AuthCacheImpl
/*     */   implements AuthCache
/*     */ {
/*     */   HashMap<String, LinkedList<AuthCacheValue>> hashtable;
/*     */   
/*     */   public AuthCacheImpl()
/*     */   {
/*  40 */     this.hashtable = new HashMap();
/*     */   }
/*     */   
/*     */   public void setMap(HashMap<String, LinkedList<AuthCacheValue>> paramHashMap) {
/*  44 */     this.hashtable = paramHashMap;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void put(String paramString, AuthCacheValue paramAuthCacheValue)
/*     */   {
/*  51 */     LinkedList localLinkedList = (LinkedList)this.hashtable.get(paramString);
/*  52 */     String str = paramAuthCacheValue.getPath();
/*  53 */     if (localLinkedList == null) {
/*  54 */       localLinkedList = new LinkedList();
/*  55 */       this.hashtable.put(paramString, localLinkedList);
/*     */     }
/*     */     
/*  58 */     ListIterator localListIterator = localLinkedList.listIterator();
/*  59 */     while (localListIterator.hasNext()) {
/*  60 */       AuthenticationInfo localAuthenticationInfo = (AuthenticationInfo)localListIterator.next();
/*  61 */       if ((localAuthenticationInfo.path == null) || (localAuthenticationInfo.path.startsWith(str))) {
/*  62 */         localListIterator.remove();
/*     */       }
/*     */     }
/*  65 */     localListIterator.add(paramAuthCacheValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized AuthCacheValue get(String paramString1, String paramString2)
/*     */   {
/*  72 */     Object localObject = null;
/*  73 */     LinkedList localLinkedList = (LinkedList)this.hashtable.get(paramString1);
/*  74 */     if ((localLinkedList == null) || (localLinkedList.size() == 0)) {
/*  75 */       return null;
/*     */     }
/*  77 */     if (paramString2 == null)
/*     */     {
/*  79 */       return (AuthenticationInfo)localLinkedList.get(0);
/*     */     }
/*  81 */     ListIterator localListIterator = localLinkedList.listIterator();
/*  82 */     while (localListIterator.hasNext()) {
/*  83 */       AuthenticationInfo localAuthenticationInfo = (AuthenticationInfo)localListIterator.next();
/*  84 */       if (paramString2.startsWith(localAuthenticationInfo.path)) {
/*  85 */         return localAuthenticationInfo;
/*     */       }
/*     */     }
/*  88 */     return null;
/*     */   }
/*     */   
/*     */   public synchronized void remove(String paramString, AuthCacheValue paramAuthCacheValue) {
/*  92 */     LinkedList localLinkedList = (LinkedList)this.hashtable.get(paramString);
/*  93 */     if (localLinkedList == null) {
/*  94 */       return;
/*     */     }
/*  96 */     if (paramAuthCacheValue == null) {
/*  97 */       localLinkedList.clear();
/*  98 */       return;
/*     */     }
/* 100 */     ListIterator localListIterator = localLinkedList.listIterator();
/* 101 */     while (localListIterator.hasNext()) {
/* 102 */       AuthenticationInfo localAuthenticationInfo = (AuthenticationInfo)localListIterator.next();
/* 103 */       if (paramAuthCacheValue.equals(localAuthenticationInfo)) {
/* 104 */         localListIterator.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\http\AuthCacheImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */