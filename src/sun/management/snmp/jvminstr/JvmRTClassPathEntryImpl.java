/*    */ package sun.management.snmp.jvminstr;
/*    */ 
/*    */ import com.sun.jmx.snmp.SnmpStatusException;
/*    */ import java.io.Serializable;
/*    */ import sun.management.snmp.jvmmib.JvmRTClassPathEntryMBean;
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
/*    */ public class JvmRTClassPathEntryImpl
/*    */   implements JvmRTClassPathEntryMBean, Serializable
/*    */ {
/*    */   static final long serialVersionUID = 8524792845083365742L;
/*    */   private final String item;
/*    */   private final int index;
/*    */   
/*    */   public JvmRTClassPathEntryImpl(String paramString, int paramInt)
/*    */   {
/* 55 */     this.item = validPathElementTC(paramString);
/* 56 */     this.index = paramInt;
/*    */   }
/*    */   
/*    */   private String validPathElementTC(String paramString) {
/* 60 */     return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(paramString);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getJvmRTClassPathItem()
/*    */     throws SnmpStatusException
/*    */   {
/* 67 */     return this.item;
/*    */   }
/*    */   
/*    */ 
/*    */   public Integer getJvmRTClassPathIndex()
/*    */     throws SnmpStatusException
/*    */   {
/* 74 */     return new Integer(this.index);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmRTClassPathEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */