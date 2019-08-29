/*     */ package sun.net.httpserver;
/*     */ 
/*     */ import com.sun.net.httpserver.Headers;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class UnmodifiableHeaders
/*     */   extends Headers
/*     */ {
/*     */   Headers map;
/*     */   
/*     */   UnmodifiableHeaders(Headers paramHeaders)
/*     */   {
/*  36 */     this.map = paramHeaders;
/*     */   }
/*     */   
/*  39 */   public int size() { return this.map.size(); }
/*     */   
/*  41 */   public boolean isEmpty() { return this.map.isEmpty(); }
/*     */   
/*     */   public boolean containsKey(Object paramObject) {
/*  44 */     return this.map.containsKey(paramObject);
/*     */   }
/*     */   
/*     */   public boolean containsValue(Object paramObject) {
/*  48 */     return this.map.containsValue(paramObject);
/*     */   }
/*     */   
/*     */   public List<String> get(Object paramObject) {
/*  52 */     return this.map.get(paramObject);
/*     */   }
/*     */   
/*     */   public String getFirst(String paramString) {
/*  56 */     return this.map.getFirst(paramString);
/*     */   }
/*     */   
/*     */   public List<String> put(String paramString, List<String> paramList)
/*     */   {
/*  61 */     return this.map.put(paramString, paramList);
/*     */   }
/*     */   
/*     */   public void add(String paramString1, String paramString2) {
/*  65 */     throw new UnsupportedOperationException("unsupported operation");
/*     */   }
/*     */   
/*     */   public void set(String paramString1, String paramString2) {
/*  69 */     throw new UnsupportedOperationException("unsupported operation");
/*     */   }
/*     */   
/*     */   public List<String> remove(Object paramObject) {
/*  73 */     throw new UnsupportedOperationException("unsupported operation");
/*     */   }
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends List<String>> paramMap) {
/*  77 */     throw new UnsupportedOperationException("unsupported operation");
/*     */   }
/*     */   
/*     */   public void clear() {
/*  81 */     throw new UnsupportedOperationException("unsupported operation");
/*     */   }
/*     */   
/*     */   public Set<String> keySet() {
/*  85 */     return Collections.unmodifiableSet(this.map.keySet());
/*     */   }
/*     */   
/*     */   public Collection<List<String>> values() {
/*  89 */     return Collections.unmodifiableCollection(this.map.values());
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<Entry<String, List<String>>> entrySet()
/*     */   {
/*  95 */     return Collections.unmodifiableSet(this.map.entrySet());
/*     */   }
/*     */   
/*  98 */   public boolean equals(Object paramObject) { return this.map.equals(paramObject); }
/*     */   
/* 100 */   public int hashCode() { return this.map.hashCode(); }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\httpserver\UnmodifiableHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */