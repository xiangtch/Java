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
/*    */ public class EnumJvmMemPoolCollectThreshdSupport
/*    */   extends Enumerated
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = 8610091819732806282L;
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
/*    */   public EnumJvmMemPoolCollectThreshdSupport(int paramInt) throws IllegalArgumentException {
/* 59 */     super(paramInt);
/*    */   }
/*    */   
/*    */   public EnumJvmMemPoolCollectThreshdSupport(Integer paramInteger) throws IllegalArgumentException {
/* 63 */     super(paramInteger);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public EnumJvmMemPoolCollectThreshdSupport(String paramString)
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
/*    */   public EnumJvmMemPoolCollectThreshdSupport()
/*    */     throws IllegalArgumentException
/*    */   {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\EnumJvmMemPoolCollectThreshdSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */