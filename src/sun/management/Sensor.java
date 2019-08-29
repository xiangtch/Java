/*     */ package sun.management;
/*     */ 
/*     */ import java.lang.management.MemoryUsage;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Sensor
/*     */ {
/*     */   private Object lock;
/*     */   private String name;
/*     */   private long count;
/*     */   private boolean on;
/*     */   
/*     */   public Sensor(String paramString)
/*     */   {
/*  62 */     this.name = paramString;
/*  63 */     this.count = 0L;
/*  64 */     this.on = false;
/*  65 */     this.lock = new Object();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/*  74 */     return this.name;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public long getCount()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 69	sun/management/Sensor:lock	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 67	sun/management/Sensor:count	J
/*     */     //   11: aload_1
/*     */     //   12: monitorexit
/*     */     //   13: lreturn
/*     */     //   14: astore_2
/*     */     //   15: aload_1
/*     */     //   16: monitorexit
/*     */     //   17: aload_2
/*     */     //   18: athrow
/*     */     // Line number table:
/*     */     //   Java source line #83	-> byte code offset #0
/*     */     //   Java source line #84	-> byte code offset #7
/*     */     //   Java source line #85	-> byte code offset #14
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	19	0	this	Sensor
/*     */     //   5	11	1	Ljava/lang/Object;	Object
/*     */     //   14	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	13	14	finally
/*     */     //   14	17	14	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean isOn()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 69	sun/management/Sensor:lock	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 68	sun/management/Sensor:on	Z
/*     */     //   11: aload_1
/*     */     //   12: monitorexit
/*     */     //   13: ireturn
/*     */     //   14: astore_2
/*     */     //   15: aload_1
/*     */     //   16: monitorexit
/*     */     //   17: aload_2
/*     */     //   18: athrow
/*     */     // Line number table:
/*     */     //   Java source line #96	-> byte code offset #0
/*     */     //   Java source line #97	-> byte code offset #7
/*     */     //   Java source line #98	-> byte code offset #14
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	19	0	this	Sensor
/*     */     //   5	11	1	Ljava/lang/Object;	Object
/*     */     //   14	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	13	14	finally
/*     */     //   14	17	14	finally
/*     */   }
/*     */   
/*     */   public void trigger()
/*     */   {
/* 106 */     synchronized (this.lock) {
/* 107 */       this.on = true;
/* 108 */       this.count += 1L;
/*     */     }
/* 110 */     triggerAction();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trigger(int paramInt)
/*     */   {
/* 118 */     synchronized (this.lock) {
/* 119 */       this.on = true;
/* 120 */       this.count += paramInt;
/*     */     }
/*     */     
/* 123 */     triggerAction();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trigger(int paramInt, MemoryUsage paramMemoryUsage)
/*     */   {
/* 132 */     synchronized (this.lock) {
/* 133 */       this.on = true;
/* 134 */       this.count += paramInt;
/*     */     }
/*     */     
/* 137 */     triggerAction(paramMemoryUsage);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/* 144 */     synchronized (this.lock) {
/* 145 */       this.on = false;
/*     */     }
/* 147 */     clearAction();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clear(int paramInt)
/*     */   {
/* 156 */     synchronized (this.lock) {
/* 157 */       this.on = false;
/* 158 */       this.count += paramInt;
/*     */     }
/* 160 */     clearAction();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 164 */     return 
/*     */     
/* 166 */       "Sensor - " + getName() + (isOn() ? " on " : " off ") + " count = " + getCount();
/*     */   }
/*     */   
/*     */   abstract void triggerAction();
/*     */   
/*     */   abstract void triggerAction(MemoryUsage paramMemoryUsage);
/*     */   
/*     */   abstract void clearAction();
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\Sensor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */