/*    */ package sun.management.snmp.jvminstr;
/*    */ 
/*    */ import com.sun.jmx.snmp.SnmpStatusException;
/*    */ import java.io.Serializable;
/*    */ import sun.management.snmp.jvmmib.JvmRTInputArgsEntryMBean;
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
/*    */ public class JvmRTInputArgsEntryImpl
/*    */   implements JvmRTInputArgsEntryMBean, Serializable
/*    */ {
/*    */   static final long serialVersionUID = 1000306518436503395L;
/*    */   private final String item;
/*    */   private final int index;
/*    */   
/*    */   public JvmRTInputArgsEntryImpl(String paramString, int paramInt)
/*    */   {
/* 55 */     this.item = validArgValueTC(paramString);
/* 56 */     this.index = paramInt;
/*    */   }
/*    */   
/*    */   private String validArgValueTC(String paramString) {
/* 60 */     return JVM_MANAGEMENT_MIB_IMPL.validArgValueTC(paramString);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getJvmRTInputArgsItem()
/*    */     throws SnmpStatusException
/*    */   {
/* 67 */     return this.item;
/*    */   }
/*    */   
/*    */ 
/*    */   public Integer getJvmRTInputArgsIndex()
/*    */     throws SnmpStatusException
/*    */   {
/* 74 */     return new Integer(this.index);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmRTInputArgsEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */