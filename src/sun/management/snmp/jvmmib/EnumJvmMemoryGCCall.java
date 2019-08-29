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
/*    */ public class EnumJvmMemoryGCCall
/*    */   extends Enumerated
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = -2869147994287351375L;
/* 47 */   protected static Hashtable<Integer, String> intTable = new Hashtable();
/*    */   
/* 49 */   protected static Hashtable<String, Integer> stringTable = new Hashtable();
/*    */   
/*    */   static {
/* 52 */     intTable.put(new Integer(2), "supported");
/* 53 */     intTable.put(new Integer(5), "failed");
/* 54 */     intTable.put(new Integer(4), "started");
/* 55 */     intTable.put(new Integer(1), "unsupported");
/* 56 */     intTable.put(new Integer(3), "start");
/* 57 */     stringTable.put("supported", new Integer(2));
/* 58 */     stringTable.put("failed", new Integer(5));
/* 59 */     stringTable.put("started", new Integer(4));
/* 60 */     stringTable.put("unsupported", new Integer(1));
/* 61 */     stringTable.put("start", new Integer(3));
/*    */   }
/*    */   
/*    */   public EnumJvmMemoryGCCall(int paramInt) throws IllegalArgumentException {
/* 65 */     super(paramInt);
/*    */   }
/*    */   
/*    */   public EnumJvmMemoryGCCall(Integer paramInteger) throws IllegalArgumentException {
/* 69 */     super(paramInteger);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public EnumJvmMemoryGCCall(String paramString)
/*    */     throws IllegalArgumentException
/*    */   {
/* 77 */     super(paramString);
/*    */   }
/*    */   
/*    */   protected Hashtable<Integer, String> getIntTable() {
/* 81 */     return intTable;
/*    */   }
/*    */   
/*    */   protected Hashtable<String, Integer> getStringTable() {
/* 85 */     return stringTable;
/*    */   }
/*    */   
/*    */   public EnumJvmMemoryGCCall()
/*    */     throws IllegalArgumentException
/*    */   {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\EnumJvmMemoryGCCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */