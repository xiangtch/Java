/*     */ package sun.util.resources;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import sun.util.ResourceBundleEnumeration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class OpenListResourceBundle
/*     */   extends ResourceBundle
/*     */ {
/*     */   protected Object handleGetObject(String paramString)
/*     */   {
/*  72 */     if (paramString == null) {
/*  73 */       throw new NullPointerException();
/*     */     }
/*     */     
/*  76 */     loadLookupTablesIfNecessary();
/*  77 */     return this.lookup.get(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getKeys()
/*     */   {
/*  85 */     ResourceBundle localResourceBundle = this.parent;
/*  86 */     return new ResourceBundleEnumeration(handleKeySet(), localResourceBundle != null ? localResourceBundle
/*  87 */       .getKeys() : null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Set<String> handleKeySet()
/*     */   {
/*  96 */     loadLookupTablesIfNecessary();
/*  97 */     return this.lookup.keySet();
/*     */   }
/*     */   
/*     */   public Set<String> keySet()
/*     */   {
/* 102 */     if (this.keyset != null) {
/* 103 */       return this.keyset;
/*     */     }
/* 105 */     Set localSet = createSet();
/* 106 */     localSet.addAll(handleKeySet());
/* 107 */     if (this.parent != null) {
/* 108 */       localSet.addAll(this.parent.keySet());
/*     */     }
/* 110 */     synchronized (this) {
/* 111 */       if (this.keyset == null) {
/* 112 */         this.keyset = localSet;
/*     */       }
/*     */     }
/* 115 */     return this.keyset;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract Object[][] getContents();
/*     */   
/*     */ 
/*     */ 
/*     */   void loadLookupTablesIfNecessary()
/*     */   {
/* 127 */     if (this.lookup == null) {
/* 128 */       loadLookup();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void loadLookup()
/*     */   {
/* 137 */     Object[][] arrayOfObject = getContents();
/* 138 */     Map localMap = createMap(arrayOfObject.length);
/* 139 */     for (int i = 0; i < arrayOfObject.length; i++)
/*     */     {
/* 141 */       String str = (String)arrayOfObject[i][0];
/* 142 */       Object localObject1 = arrayOfObject[i][1];
/* 143 */       if ((str == null) || (localObject1 == null)) {
/* 144 */         throw new NullPointerException();
/*     */       }
/* 146 */       localMap.put(str, localObject1);
/*     */     }
/* 148 */     synchronized (this) {
/* 149 */       if (this.lookup == null) {
/* 150 */         this.lookup = localMap;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected <K, V> Map<K, V> createMap(int paramInt)
/*     */   {
/* 160 */     return new HashMap(paramInt);
/*     */   }
/*     */   
/*     */   protected <E> Set<E> createSet() {
/* 164 */     return new HashSet();
/*     */   }
/*     */   
/* 167 */   private volatile Map<String, Object> lookup = null;
/*     */   private volatile Set<String> keyset;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\resources\OpenListResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */