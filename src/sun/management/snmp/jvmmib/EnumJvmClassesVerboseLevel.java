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
/*    */ public class EnumJvmClassesVerboseLevel
/*    */   extends Enumerated
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = -620710366914810374L;
/* 47 */   protected static Hashtable<Integer, String> intTable = new Hashtable();
/*    */   
/* 49 */   protected static Hashtable<String, Integer> stringTable = new Hashtable();
/*    */   
/*    */   static {
/* 52 */     intTable.put(new Integer(2), "verbose");
/* 53 */     intTable.put(new Integer(1), "silent");
/* 54 */     stringTable.put("verbose", new Integer(2));
/* 55 */     stringTable.put("silent", new Integer(1));
/*    */   }
/*    */   
/*    */   public EnumJvmClassesVerboseLevel(int paramInt) throws IllegalArgumentException {
/* 59 */     super(paramInt);
/*    */   }
/*    */   
/*    */   public EnumJvmClassesVerboseLevel(Integer paramInteger) throws IllegalArgumentException {
/* 63 */     super(paramInteger);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public EnumJvmClassesVerboseLevel(String paramString)
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
/*    */   public EnumJvmClassesVerboseLevel()
/*    */     throws IllegalArgumentException
/*    */   {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\EnumJvmClassesVerboseLevel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */