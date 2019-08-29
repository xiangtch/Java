/*    */ package sun.management.snmp.jvmmib;
/*    */ 
/*    */ import com.sun.jmx.snmp.Enumerated;
/*    */ import java.io.Serializable;
/*    */ import java.util.Hashtable;
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
/*    */ public class EnumJvmThreadContentionMonitoring
/*    */   extends Enumerated
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = -6411827583604137210L;
/* 47 */   protected static Hashtable<Integer, String> intTable = new Hashtable();
/*    */   
/* 49 */   protected static Hashtable<String, Integer> stringTable = new Hashtable();
/*    */   
/*    */   static {
/* 52 */     intTable.put(new Integer(3), "enabled");
/* 53 */     intTable.put(new Integer(4), "disabled");
/* 54 */     intTable.put(new Integer(1), "unsupported");
/* 55 */     stringTable.put("enabled", new Integer(3));
/* 56 */     stringTable.put("disabled", new Integer(4));
/* 57 */     stringTable.put("unsupported", new Integer(1));
/*    */   }
/*    */   
/*    */   public EnumJvmThreadContentionMonitoring(int paramInt) throws IllegalArgumentException {
/* 61 */     super(paramInt);
/*    */   }
/*    */   
/*    */   public EnumJvmThreadContentionMonitoring(Integer paramInteger) throws IllegalArgumentException {
/* 65 */     super(paramInteger);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public EnumJvmThreadContentionMonitoring(String paramString)
/*    */     throws IllegalArgumentException
/*    */   {
/* 73 */     super(paramString);
/*    */   }
/*    */   
/*    */   protected Hashtable<Integer, String> getIntTable() {
/* 77 */     return intTable;
/*    */   }
/*    */   
/*    */   protected Hashtable<String, Integer> getStringTable() {
/* 81 */     return stringTable;
/*    */   }
/*    */   
/*    */   public EnumJvmThreadContentionMonitoring()
/*    */     throws IllegalArgumentException
/*    */   {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\EnumJvmThreadContentionMonitoring.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */