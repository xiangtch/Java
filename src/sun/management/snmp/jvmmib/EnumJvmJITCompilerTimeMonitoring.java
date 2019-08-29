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
/*    */ public class EnumJvmJITCompilerTimeMonitoring
/*    */   extends Enumerated
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = 3953565918146461236L;
/* 47 */   protected static Hashtable<Integer, String> intTable = new Hashtable();
/*    */   
/* 49 */   protected static Hashtable<String, Integer> stringTable = new Hashtable();
/*    */   
/*    */   static {
/* 52 */     intTable.put(new Integer(2), "supported");
/* 53 */     intTable.put(new Integer(1), "unsupported");
/* 54 */     stringTable.put("supported", new Integer(2));
/* 55 */     stringTable.put("unsupported", new Integer(1));
/*    */   }
/*    */   
/*    */   public EnumJvmJITCompilerTimeMonitoring(int paramInt) throws IllegalArgumentException {
/* 59 */     super(paramInt);
/*    */   }
/*    */   
/*    */   public EnumJvmJITCompilerTimeMonitoring(Integer paramInteger) throws IllegalArgumentException {
/* 63 */     super(paramInteger);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public EnumJvmJITCompilerTimeMonitoring(String paramString)
/*    */     throws IllegalArgumentException
/*    */   {
/* 71 */     super(paramString);
/*    */   }
/*    */   
/*    */   protected Hashtable<Integer, String> getIntTable() {
/* 75 */     return intTable;
/*    */   }
/*    */   
/*    */   protected Hashtable<String, Integer> getStringTable() {
/* 79 */     return stringTable;
/*    */   }
/*    */   
/*    */   public EnumJvmJITCompilerTimeMonitoring()
/*    */     throws IllegalArgumentException
/*    */   {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\EnumJvmJITCompilerTimeMonitoring.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */