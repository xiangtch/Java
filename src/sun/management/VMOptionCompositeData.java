/*     */ package sun.management;
/*     */ 
/*     */ import com.sun.management.VMOption;
/*     */ import com.sun.management.VMOption.Origin;
/*     */ import javax.management.openmbean.CompositeData;
/*     */ import javax.management.openmbean.CompositeDataSupport;
/*     */ import javax.management.openmbean.CompositeType;
/*     */ import javax.management.openmbean.OpenDataException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VMOptionCompositeData
/*     */   extends LazyCompositeData
/*     */ {
/*     */   private final VMOption option;
/*     */   private static final CompositeType vmOptionCompositeType;
/*     */   private static final String NAME = "name";
/*     */   private static final String VALUE = "value";
/*     */   private static final String WRITEABLE = "writeable";
/*     */   private static final String ORIGIN = "origin";
/*     */   
/*     */   private VMOptionCompositeData(VMOption paramVMOption)
/*     */   {
/*  44 */     this.option = paramVMOption;
/*     */   }
/*     */   
/*     */   public VMOption getVMOption() {
/*  48 */     return this.option;
/*     */   }
/*     */   
/*     */   public static CompositeData toCompositeData(VMOption paramVMOption) {
/*  52 */     VMOptionCompositeData localVMOptionCompositeData = new VMOptionCompositeData(paramVMOption);
/*  53 */     return localVMOptionCompositeData.getCompositeData();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CompositeData getCompositeData()
/*     */   {
/*  63 */     Object[] arrayOfObject = { this.option.getName(), this.option.getValue(), new Boolean(this.option.isWriteable()), this.option.getOrigin().toString() };
/*     */     
/*     */     try
/*     */     {
/*  67 */       return new CompositeDataSupport(vmOptionCompositeType, vmOptionItemNames, arrayOfObject);
/*     */ 
/*     */     }
/*     */     catch (OpenDataException localOpenDataException)
/*     */     {
/*  72 */       throw new AssertionError(localOpenDataException);
/*     */     }
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  80 */       vmOptionCompositeType = (CompositeType)MappedMXBeanType.toOpenType(VMOption.class);
/*     */     }
/*     */     catch (OpenDataException localOpenDataException) {
/*  83 */       throw new AssertionError(localOpenDataException);
/*     */     }
/*     */   }
/*     */   
/*     */   static CompositeType getVMOptionCompositeType() {
/*  88 */     return vmOptionCompositeType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  96 */   private static final String[] vmOptionItemNames = { "name", "value", "writeable", "origin" };
/*     */   
/*     */ 
/*     */   private static final long serialVersionUID = -2395573975093578470L;
/*     */   
/*     */ 
/*     */   public static String getName(CompositeData paramCompositeData)
/*     */   {
/* 104 */     return getString(paramCompositeData, "name");
/*     */   }
/*     */   
/* 107 */   public static String getValue(CompositeData paramCompositeData) { return getString(paramCompositeData, "value"); }
/*     */   
/*     */   public static VMOption.Origin getOrigin(CompositeData paramCompositeData) {
/* 110 */     String str = getString(paramCompositeData, "origin");
/* 111 */     return (VMOption.Origin)Enum.valueOf(VMOption.Origin.class, str);
/*     */   }
/*     */   
/* 114 */   public static boolean isWriteable(CompositeData paramCompositeData) { return getBoolean(paramCompositeData, "writeable"); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void validateCompositeData(CompositeData paramCompositeData)
/*     */   {
/* 122 */     if (paramCompositeData == null) {
/* 123 */       throw new NullPointerException("Null CompositeData");
/*     */     }
/*     */     
/* 126 */     if (!isTypeMatched(vmOptionCompositeType, paramCompositeData.getCompositeType())) {
/* 127 */       throw new IllegalArgumentException("Unexpected composite type for VMOption");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\VMOptionCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */