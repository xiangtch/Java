/*    */ package sun.management.snmp.jvminstr;
/*    */ 
/*    */ import com.sun.jmx.snmp.SnmpStatusException;
/*    */ import java.io.Serializable;
/*    */ import sun.management.snmp.jvmmib.JvmRTBootClassPathEntryMBean;
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
/*    */ public class JvmRTBootClassPathEntryImpl
/*    */   implements JvmRTBootClassPathEntryMBean, Serializable
/*    */ {
/*    */   static final long serialVersionUID = -2282652055235913013L;
/*    */   private final String item;
/*    */   private final int index;
/*    */   
/*    */   public JvmRTBootClassPathEntryImpl(String paramString, int paramInt)
/*    */   {
/* 56 */     this.item = validPathElementTC(paramString);
/* 57 */     this.index = paramInt;
/*    */   }
/*    */   
/*    */   private String validPathElementTC(String paramString) {
/* 61 */     return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(paramString);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getJvmRTBootClassPathItem()
/*    */     throws SnmpStatusException
/*    */   {
/* 68 */     return this.item;
/*    */   }
/*    */   
/*    */ 
/*    */   public Integer getJvmRTBootClassPathIndex()
/*    */     throws SnmpStatusException
/*    */   {
/* 75 */     return new Integer(this.index);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmRTBootClassPathEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */