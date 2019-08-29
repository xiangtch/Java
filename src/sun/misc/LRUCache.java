/*    */ package sun.misc;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class LRUCache<N, V>
/*    */ {
/* 35 */   private V[] oa = null;
/*    */   private final int size;
/*    */   
/*    */   public LRUCache(int paramInt) {
/* 39 */     this.size = paramInt;
/*    */   }
/*    */   
/*    */   protected abstract V create(N paramN);
/*    */   
/*    */   protected abstract boolean hasName(V paramV, N paramN);
/*    */   
/*    */   public static void moveToFront(Object[] paramArrayOfObject, int paramInt) {
/* 47 */     Object localObject = paramArrayOfObject[paramInt];
/* 48 */     for (int i = paramInt; i > 0; i--)
/* 49 */       paramArrayOfObject[i] = paramArrayOfObject[(i - 1)];
/* 50 */     paramArrayOfObject[0] = localObject;
/*    */   }
/*    */   
/*    */   public V forName(N paramN) {
/* 54 */     if (this.oa == null)
/*    */     {
/* 56 */       Object[] arrayOfObject = (Object[])new Object[this.size];
/* 57 */       this.oa = arrayOfObject;
/*    */     } else {
/* 59 */       for (int i = 0; i < this.oa.length; i++) {
/* 60 */         Object localObject2 = this.oa[i];
/* 61 */         if (localObject2 != null)
/*    */         {
/* 63 */           if (hasName(localObject2, paramN)) {
/* 64 */             if (i > 0)
/* 65 */               moveToFront(this.oa, i);
/* 66 */             return (V)localObject2;
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */     
/* 72 */     Object localObject1 = create(paramN);
/* 73 */     this.oa[(this.oa.length - 1)] = localObject1;
/* 74 */     moveToFront(this.oa, this.oa.length - 1);
/* 75 */     return (V)localObject1;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\LRUCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */