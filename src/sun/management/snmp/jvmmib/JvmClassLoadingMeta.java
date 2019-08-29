/*     */ package sun.management.snmp.jvmmib;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpCounter64;
/*     */ import com.sun.jmx.snmp.SnmpGauge;
/*     */ import com.sun.jmx.snmp.SnmpInt;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.SnmpValue;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibGroup;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibTable;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.io.Serializable;
/*     */ import javax.management.MBeanServer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JvmClassLoadingMeta
/*     */   extends SnmpMibGroup
/*     */   implements Serializable, SnmpStandardMetaServer
/*     */ {
/*     */   static final long serialVersionUID = 5722857476941218568L;
/*     */   protected JvmClassLoadingMBean node;
/*     */   
/*     */   public JvmClassLoadingMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/*  78 */     this.objectserver = paramSnmpStandardObjectServer;
/*     */     try {
/*  80 */       registerObject(4L);
/*  81 */       registerObject(3L);
/*  82 */       registerObject(2L);
/*  83 */       registerObject(1L);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/*  85 */       throw new RuntimeException(localIllegalAccessException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue get(long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/*  94 */     switch ((int)paramLong) {
/*     */     case 4: 
/*  96 */       return new SnmpInt(this.node.getJvmClassesVerboseLevel());
/*     */     
/*     */     case 3: 
/*  99 */       return new SnmpCounter64(this.node.getJvmClassesUnloadedCount());
/*     */     
/*     */     case 2: 
/* 102 */       return new SnmpCounter64(this.node.getJvmClassesTotalLoadedCount());
/*     */     
/*     */     case 1: 
/* 105 */       return new SnmpGauge(this.node.getJvmClassesLoadedCount());
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 110 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 118 */     switch ((int)paramLong) {
/*     */     case 4: 
/* 120 */       if ((paramSnmpValue instanceof SnmpInt)) {
/*     */         try {
/* 122 */           this.node.setJvmClassesVerboseLevel(new EnumJvmClassesVerboseLevel(((SnmpInt)paramSnmpValue).toInteger()));
/*     */         } catch (IllegalArgumentException localIllegalArgumentException) {
/* 124 */           throw new SnmpStatusException(10);
/*     */         }
/* 126 */         return new SnmpInt(this.node.getJvmClassesVerboseLevel());
/*     */       }
/* 128 */       throw new SnmpStatusException(7);
/*     */     
/*     */ 
/*     */     case 3: 
/* 132 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 135 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 138 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 143 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 151 */     switch ((int)paramLong) {
/*     */     case 4: 
/* 153 */       if ((paramSnmpValue instanceof SnmpInt)) {
/*     */         try {
/* 155 */           this.node.checkJvmClassesVerboseLevel(new EnumJvmClassesVerboseLevel(((SnmpInt)paramSnmpValue).toInteger()));
/*     */         } catch (IllegalArgumentException localIllegalArgumentException) {
/* 157 */           throw new SnmpStatusException(10);
/*     */         }
/*     */       } else {
/* 160 */         throw new SnmpStatusException(7);
/*     */       }
/*     */       
/*     */       break;
/*     */     case 3: 
/* 165 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 168 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 171 */       throw new SnmpStatusException(17);
/*     */     
/*     */     default: 
/* 174 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setInstance(JvmClassLoadingMBean paramJvmClassLoadingMBean)
/*     */   {
/* 182 */     this.node = paramJvmClassLoadingMBean;
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
/* 195 */     this.objectserver.get(this, paramSnmpMibSubRequest, paramInt);
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
/* 208 */     this.objectserver.set(this, paramSnmpMibSubRequest, paramInt);
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
/* 221 */     this.objectserver.check(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isVariable(long paramLong)
/*     */   {
/* 229 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/* 234 */       return true;
/*     */     }
/*     */     
/*     */     
/* 238 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReadable(long paramLong)
/*     */   {
/* 246 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/* 251 */       return true;
/*     */     }
/*     */     
/*     */     
/* 255 */     return false;
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
/* 267 */     switch ((int)paramLong) {
/*     */     case 2: 
/*     */     case 3: 
/* 270 */       if (paramInt == 0) { return true;
/*     */       }
/*     */       break;
/*     */     }
/*     */     
/* 275 */     return super.skipVariable(paramLong, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAttributeName(long paramLong)
/*     */     throws SnmpStatusException
/*     */   {
/* 283 */     switch ((int)paramLong) {
/*     */     case 4: 
/* 285 */       return "JvmClassesVerboseLevel";
/*     */     
/*     */     case 3: 
/* 288 */       return "JvmClassesUnloadedCount";
/*     */     
/*     */     case 2: 
/* 291 */       return "JvmClassesTotalLoadedCount";
/*     */     
/*     */     case 1: 
/* 294 */       return "JvmClassesLoadedCount";
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 299 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTable(long paramLong)
/*     */   {
/* 307 */     switch ((int)paramLong)
/*     */     {
/*     */     }
/*     */     
/* 311 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpMibTable getTable(long paramLong)
/*     */   {
/* 318 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 328 */   protected SnmpStandardObjectServer objectserver = null;
/*     */   
/*     */   public void registerTableNodes(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmClassLoadingMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */