/*    */ package sun.management.snmp.jvminstr;
/*    */ 
/*    */ import com.sun.jmx.snmp.SnmpStatusException;
/*    */ import java.io.Serializable;
/*    */ import sun.management.snmp.jvmmib.JvmRTLibraryPathEntryMBean;
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
/*    */ public class JvmRTLibraryPathEntryImpl
/*    */   implements JvmRTLibraryPathEntryMBean, Serializable
/*    */ {
/*    */   static final long serialVersionUID = -3322438153507369765L;
/*    */   private final String item;
/*    */   private final int index;
/*    */   
/*    */   public JvmRTLibraryPathEntryImpl(String paramString, int paramInt)
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
/*    */   public String getJvmRTLibraryPathItem()
/*    */     throws SnmpStatusException
/*    */   {
/* 67 */     return this.item;
/*    */   }
/*    */   
/*    */ 
/*    */   public Integer getJvmRTLibraryPathIndex()
/*    */     throws SnmpStatusException
/*    */   {
/* 74 */     return new Integer(this.index);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmRTLibraryPathEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */