/*     */ package sun.management.snmp.jvmmib;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpCounter64;
/*     */ import com.sun.jmx.snmp.SnmpInt;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.SnmpString;
/*     */ import com.sun.jmx.snmp.SnmpValue;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibEntry;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibNode;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JvmMemPoolEntryMeta
/*     */   extends SnmpMibEntry
/*     */   implements Serializable, SnmpStandardMetaServer
/*     */ {
/*     */   static final long serialVersionUID = 7220682779249102830L;
/*     */   protected JvmMemPoolEntryMBean node;
/*     */   
/*     */   public JvmMemPoolEntryMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/*  79 */     this.objectserver = paramSnmpStandardObjectServer;
/*  80 */     this.varList = new int[20];
/*  81 */     this.varList[0] = 33;
/*  82 */     this.varList[1] = 32;
/*  83 */     this.varList[2] = 31;
/*  84 */     this.varList[3] = 133;
/*  85 */     this.varList[4] = 132;
/*  86 */     this.varList[5] = 131;
/*  87 */     this.varList[6] = 13;
/*  88 */     this.varList[7] = 12;
/*  89 */     this.varList[8] = 11;
/*  90 */     this.varList[9] = 10;
/*  91 */     this.varList[10] = 112;
/*  92 */     this.varList[11] = 111;
/*  93 */     this.varList[12] = 110;
/*  94 */     this.varList[13] = 5;
/*  95 */     this.varList[14] = 4;
/*  96 */     this.varList[15] = 3;
/*  97 */     this.varList[16] = 2;
/*  98 */     this.varList[17] = 23;
/*  99 */     this.varList[18] = 22;
/* 100 */     this.varList[19] = 21;
/* 101 */     SnmpMibNode.sort(this.varList);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue get(long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 109 */     switch ((int)paramLong) {
/*     */     case 33: 
/* 111 */       return new SnmpCounter64(this.node.getJvmMemPoolCollectMaxSize());
/*     */     
/*     */     case 32: 
/* 114 */       return new SnmpCounter64(this.node.getJvmMemPoolCollectCommitted());
/*     */     
/*     */     case 31: 
/* 117 */       return new SnmpCounter64(this.node.getJvmMemPoolCollectUsed());
/*     */     
/*     */     case 133: 
/* 120 */       return new SnmpInt(this.node.getJvmMemPoolCollectThreshdSupport());
/*     */     
/*     */     case 132: 
/* 123 */       return new SnmpCounter64(this.node.getJvmMemPoolCollectThreshdCount());
/*     */     
/*     */     case 131: 
/* 126 */       return new SnmpCounter64(this.node.getJvmMemPoolCollectThreshold());
/*     */     
/*     */     case 13: 
/* 129 */       return new SnmpCounter64(this.node.getJvmMemPoolMaxSize());
/*     */     
/*     */     case 12: 
/* 132 */       return new SnmpCounter64(this.node.getJvmMemPoolCommitted());
/*     */     
/*     */     case 11: 
/* 135 */       return new SnmpCounter64(this.node.getJvmMemPoolUsed());
/*     */     
/*     */     case 10: 
/* 138 */       return new SnmpCounter64(this.node.getJvmMemPoolInitSize());
/*     */     
/*     */     case 112: 
/* 141 */       return new SnmpInt(this.node.getJvmMemPoolThreshdSupport());
/*     */     
/*     */     case 111: 
/* 144 */       return new SnmpCounter64(this.node.getJvmMemPoolThreshdCount());
/*     */     
/*     */     case 110: 
/* 147 */       return new SnmpCounter64(this.node.getJvmMemPoolThreshold());
/*     */     
/*     */     case 5: 
/* 150 */       return new SnmpCounter64(this.node.getJvmMemPoolPeakReset());
/*     */     
/*     */     case 4: 
/* 153 */       return new SnmpInt(this.node.getJvmMemPoolState());
/*     */     
/*     */     case 3: 
/* 156 */       return new SnmpInt(this.node.getJvmMemPoolType());
/*     */     
/*     */     case 2: 
/* 159 */       return new SnmpString(this.node.getJvmMemPoolName());
/*     */     
/*     */     case 23: 
/* 162 */       return new SnmpCounter64(this.node.getJvmMemPoolPeakMaxSize());
/*     */     
/*     */     case 1: 
/* 165 */       throw new SnmpStatusException(224);
/*     */     case 22: 
/* 167 */       return new SnmpCounter64(this.node.getJvmMemPoolPeakCommitted());
/*     */     
/*     */     case 21: 
/* 170 */       return new SnmpCounter64(this.node.getJvmMemPoolPeakUsed());
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 175 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 183 */     switch ((int)paramLong) {
/*     */     case 33: 
/* 185 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 32: 
/* 188 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 31: 
/* 191 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 133: 
/* 194 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 132: 
/* 197 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 131: 
/* 200 */       if ((paramSnmpValue instanceof SnmpCounter64)) {
/* 201 */         this.node.setJvmMemPoolCollectThreshold(((SnmpCounter64)paramSnmpValue).toLong());
/* 202 */         return new SnmpCounter64(this.node.getJvmMemPoolCollectThreshold());
/*     */       }
/* 204 */       throw new SnmpStatusException(7);
/*     */     
/*     */ 
/*     */     case 13: 
/* 208 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 12: 
/* 211 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 11: 
/* 214 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 10: 
/* 217 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 112: 
/* 220 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 111: 
/* 223 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 110: 
/* 226 */       if ((paramSnmpValue instanceof SnmpCounter64)) {
/* 227 */         this.node.setJvmMemPoolThreshold(((SnmpCounter64)paramSnmpValue).toLong());
/* 228 */         return new SnmpCounter64(this.node.getJvmMemPoolThreshold());
/*     */       }
/* 230 */       throw new SnmpStatusException(7);
/*     */     
/*     */ 
/*     */     case 5: 
/* 234 */       if ((paramSnmpValue instanceof SnmpCounter64)) {
/* 235 */         this.node.setJvmMemPoolPeakReset(((SnmpCounter64)paramSnmpValue).toLong());
/* 236 */         return new SnmpCounter64(this.node.getJvmMemPoolPeakReset());
/*     */       }
/* 238 */       throw new SnmpStatusException(7);
/*     */     
/*     */ 
/*     */     case 4: 
/* 242 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 3: 
/* 245 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 248 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 23: 
/* 251 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 254 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 22: 
/* 257 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 21: 
/* 260 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 265 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 273 */     switch ((int)paramLong) {
/*     */     case 33: 
/* 275 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 32: 
/* 278 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 31: 
/* 281 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 133: 
/* 284 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 132: 
/* 287 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 131: 
/* 290 */       if ((paramSnmpValue instanceof SnmpCounter64)) {
/* 291 */         this.node.checkJvmMemPoolCollectThreshold(((SnmpCounter64)paramSnmpValue).toLong());
/*     */       } else {
/* 293 */         throw new SnmpStatusException(7);
/*     */       }
/*     */       
/*     */       break;
/*     */     case 13: 
/* 298 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 12: 
/* 301 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 11: 
/* 304 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 10: 
/* 307 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 112: 
/* 310 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 111: 
/* 313 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 110: 
/* 316 */       if ((paramSnmpValue instanceof SnmpCounter64)) {
/* 317 */         this.node.checkJvmMemPoolThreshold(((SnmpCounter64)paramSnmpValue).toLong());
/*     */       } else {
/* 319 */         throw new SnmpStatusException(7);
/*     */       }
/*     */       
/*     */       break;
/*     */     case 5: 
/* 324 */       if ((paramSnmpValue instanceof SnmpCounter64)) {
/* 325 */         this.node.checkJvmMemPoolPeakReset(((SnmpCounter64)paramSnmpValue).toLong());
/*     */       } else {
/* 327 */         throw new SnmpStatusException(7);
/*     */       }
/*     */       
/*     */       break;
/*     */     case 4: 
/* 332 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 3: 
/* 335 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 338 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 23: 
/* 341 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 344 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 22: 
/* 347 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 21: 
/* 350 */       throw new SnmpStatusException(17);
/*     */     
/*     */     default: 
/* 353 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setInstance(JvmMemPoolEntryMBean paramJvmMemPoolEntryMBean)
/*     */   {
/* 361 */     this.node = paramJvmMemPoolEntryMBean;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
/*     */     throws SnmpStatusException
/*     */   {
/* 374 */     this.objectserver.get(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
/*     */     throws SnmpStatusException
/*     */   {
/* 387 */     this.objectserver.set(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
/*     */     throws SnmpStatusException
/*     */   {
/* 400 */     this.objectserver.check(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isVariable(long paramLong)
/*     */   {
/* 408 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/*     */     case 5: 
/*     */     case 10: 
/*     */     case 11: 
/*     */     case 12: 
/*     */     case 13: 
/*     */     case 21: 
/*     */     case 22: 
/*     */     case 23: 
/*     */     case 31: 
/*     */     case 32: 
/*     */     case 33: 
/*     */     case 110: 
/*     */     case 111: 
/*     */     case 112: 
/*     */     case 131: 
/*     */     case 132: 
/*     */     case 133: 
/* 430 */       return true;
/*     */     }
/*     */     
/*     */     
/* 434 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReadable(long paramLong)
/*     */   {
/* 442 */     switch ((int)paramLong) {
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/*     */     case 5: 
/*     */     case 10: 
/*     */     case 11: 
/*     */     case 12: 
/*     */     case 13: 
/*     */     case 21: 
/*     */     case 22: 
/*     */     case 23: 
/*     */     case 31: 
/*     */     case 32: 
/*     */     case 33: 
/*     */     case 110: 
/*     */     case 111: 
/*     */     case 112: 
/*     */     case 131: 
/*     */     case 132: 
/*     */     case 133: 
/* 463 */       return true;
/*     */     }
/*     */     
/*     */     
/* 467 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean skipVariable(long paramLong, Object paramObject, int paramInt)
/*     */   {
/* 479 */     switch ((int)paramLong) {
/*     */     case 5: 
/*     */     case 10: 
/*     */     case 11: 
/*     */     case 12: 
/*     */     case 13: 
/*     */     case 23: 
/*     */     case 31: 
/*     */     case 32: 
/*     */     case 33: 
/*     */     case 110: 
/*     */     case 111: 
/*     */     case 131: 
/*     */     case 132: 
/* 493 */       if (paramInt == 0) return true;
/*     */       break;
/*     */     case 1: 
/* 496 */       return true;
/*     */     case 21: 
/*     */     case 22: 
/* 499 */       if (paramInt == 0) { return true;
/*     */       }
/*     */       break;
/*     */     }
/*     */     
/* 504 */     return super.skipVariable(paramLong, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAttributeName(long paramLong)
/*     */     throws SnmpStatusException
/*     */   {
/* 512 */     switch ((int)paramLong) {
/*     */     case 33: 
/* 514 */       return "JvmMemPoolCollectMaxSize";
/*     */     
/*     */     case 32: 
/* 517 */       return "JvmMemPoolCollectCommitted";
/*     */     
/*     */     case 31: 
/* 520 */       return "JvmMemPoolCollectUsed";
/*     */     
/*     */     case 133: 
/* 523 */       return "JvmMemPoolCollectThreshdSupport";
/*     */     
/*     */     case 132: 
/* 526 */       return "JvmMemPoolCollectThreshdCount";
/*     */     
/*     */     case 131: 
/* 529 */       return "JvmMemPoolCollectThreshold";
/*     */     
/*     */     case 13: 
/* 532 */       return "JvmMemPoolMaxSize";
/*     */     
/*     */     case 12: 
/* 535 */       return "JvmMemPoolCommitted";
/*     */     
/*     */     case 11: 
/* 538 */       return "JvmMemPoolUsed";
/*     */     
/*     */     case 10: 
/* 541 */       return "JvmMemPoolInitSize";
/*     */     
/*     */     case 112: 
/* 544 */       return "JvmMemPoolThreshdSupport";
/*     */     
/*     */     case 111: 
/* 547 */       return "JvmMemPoolThreshdCount";
/*     */     
/*     */     case 110: 
/* 550 */       return "JvmMemPoolThreshold";
/*     */     
/*     */     case 5: 
/* 553 */       return "JvmMemPoolPeakReset";
/*     */     
/*     */     case 4: 
/* 556 */       return "JvmMemPoolState";
/*     */     
/*     */     case 3: 
/* 559 */       return "JvmMemPoolType";
/*     */     
/*     */     case 2: 
/* 562 */       return "JvmMemPoolName";
/*     */     
/*     */     case 23: 
/* 565 */       return "JvmMemPoolPeakMaxSize";
/*     */     
/*     */     case 1: 
/* 568 */       return "JvmMemPoolIndex";
/*     */     
/*     */     case 22: 
/* 571 */       return "JvmMemPoolPeakCommitted";
/*     */     
/*     */     case 21: 
/* 574 */       return "JvmMemPoolPeakUsed";
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 579 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/* 583 */   protected SnmpStandardObjectServer objectserver = null;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmMemPoolEntryMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */