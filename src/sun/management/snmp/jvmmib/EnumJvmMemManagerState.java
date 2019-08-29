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
/*    */ 
/*    */ public class EnumJvmMemManagerState
/*    */   extends Enumerated
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = 8249515157795166343L;
/* 48 */   protected static Hashtable<Integer, String> intTable = new Hashtable();
/*    */   
/* 50 */   protected static Hashtable<String, Integer> stringTable = new Hashtable();
/*    */   
/*    */   static {
/* 53 */     intTable.put(new Integer(2), "valid");
/* 54 */     intTable.put(new Integer(1), "invalid");
/* 55 */     stringTable.put("valid", new Integer(2));
/* 56 */     stringTable.put("invalid", new Integer(1));
/*    */   }
/*    */   
/*    */   public EnumJvmMemManagerState(int paramInt) throws IllegalArgumentException {
/* 60 */     super(paramInt);
/*    */   }
/*    */   
/*    */   public EnumJvmMemManagerState(Integer paramInteger) throws IllegalArgumentException {
/* 64 */     super(paramInteger);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public EnumJvmMemManagerState(String paramString)
/*    */     throws IllegalArgumentException
/*    */   {
/* 72 */     super(paramString);
/*    */   }
/*    */   
/*    */   protected Hashtable<Integer, String> getIntTable() {
/* 76 */     return intTable;
/*    */   }
/*    */   
/*    */   protected Hashtable<String, Integer> getStringTable() {
/* 80 */     return stringTable;
/*    */   }
/*    */   
/*    */   public EnumJvmMemManagerState()
/*    */     throws IllegalArgumentException
/*    */   {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\EnumJvmMemManagerState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */