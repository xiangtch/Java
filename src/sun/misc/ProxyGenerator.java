/*      */ package sun.misc;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Method;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.OpenOption;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.Paths;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Map;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ProxyGenerator
/*      */ {
/*      */   private static final int CLASSFILE_MAJOR_VERSION = 49;
/*      */   private static final int CLASSFILE_MINOR_VERSION = 0;
/*      */   private static final int CONSTANT_UTF8 = 1;
/*      */   private static final int CONSTANT_UNICODE = 2;
/*      */   private static final int CONSTANT_INTEGER = 3;
/*      */   private static final int CONSTANT_FLOAT = 4;
/*      */   private static final int CONSTANT_LONG = 5;
/*      */   private static final int CONSTANT_DOUBLE = 6;
/*      */   private static final int CONSTANT_CLASS = 7;
/*      */   private static final int CONSTANT_STRING = 8;
/*      */   private static final int CONSTANT_FIELD = 9;
/*      */   private static final int CONSTANT_METHOD = 10;
/*      */   private static final int CONSTANT_INTERFACEMETHOD = 11;
/*      */   private static final int CONSTANT_NAMEANDTYPE = 12;
/*      */   private static final int ACC_PUBLIC = 1;
/*      */   private static final int ACC_PRIVATE = 2;
/*      */   private static final int ACC_STATIC = 8;
/*      */   private static final int ACC_FINAL = 16;
/*      */   private static final int ACC_SUPER = 32;
/*      */   private static final int opc_aconst_null = 1;
/*      */   private static final int opc_iconst_0 = 3;
/*      */   private static final int opc_bipush = 16;
/*      */   private static final int opc_sipush = 17;
/*      */   private static final int opc_ldc = 18;
/*      */   private static final int opc_ldc_w = 19;
/*      */   private static final int opc_iload = 21;
/*      */   private static final int opc_lload = 22;
/*      */   private static final int opc_fload = 23;
/*      */   private static final int opc_dload = 24;
/*      */   private static final int opc_aload = 25;
/*      */   private static final int opc_iload_0 = 26;
/*      */   private static final int opc_lload_0 = 30;
/*      */   private static final int opc_fload_0 = 34;
/*      */   private static final int opc_dload_0 = 38;
/*      */   private static final int opc_aload_0 = 42;
/*      */   private static final int opc_astore = 58;
/*      */   private static final int opc_astore_0 = 75;
/*      */   private static final int opc_aastore = 83;
/*      */   private static final int opc_pop = 87;
/*      */   private static final int opc_dup = 89;
/*      */   private static final int opc_ireturn = 172;
/*      */   private static final int opc_lreturn = 173;
/*      */   private static final int opc_freturn = 174;
/*      */   private static final int opc_dreturn = 175;
/*      */   private static final int opc_areturn = 176;
/*      */   private static final int opc_return = 177;
/*      */   private static final int opc_getstatic = 178;
/*      */   private static final int opc_putstatic = 179;
/*      */   private static final int opc_getfield = 180;
/*      */   private static final int opc_invokevirtual = 182;
/*      */   private static final int opc_invokespecial = 183;
/*      */   private static final int opc_invokestatic = 184;
/*      */   private static final int opc_invokeinterface = 185;
/*      */   private static final int opc_new = 187;
/*      */   private static final int opc_anewarray = 189;
/*      */   private static final int opc_athrow = 191;
/*      */   private static final int opc_checkcast = 192;
/*      */   private static final int opc_wide = 196;
/*      */   private static final String superclassName = "java/lang/reflect/Proxy";
/*      */   private static final String handlerFieldName = "h";
/*      */   private static final boolean saveGeneratedFiles;
/*      */   private static Method hashCodeMethod;
/*      */   private static Method equalsMethod;
/*      */   private static Method toStringMethod;
/*      */   private String className;
/*      */   private Class<?>[] interfaces;
/*      */   private int accessFlags;
/*      */   
/*      */   public static byte[] generateProxyClass(String paramString, Class<?>[] paramArrayOfClass)
/*      */   {
/*  324 */     return generateProxyClass(paramString, paramArrayOfClass, 49);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] generateProxyClass(String paramString, Class<?>[] paramArrayOfClass, int paramInt)
/*      */   {
/*  338 */     ProxyGenerator localProxyGenerator = new ProxyGenerator(paramString, paramArrayOfClass, paramInt);
/*  339 */     final byte[] arrayOfByte = localProxyGenerator.generateClassFile();
/*      */     
/*  341 */     if (saveGeneratedFiles) {
/*  342 */       AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Void run() {
/*      */           try {
/*  346 */             int i = this.val$name.lastIndexOf('.');
/*      */             Path localPath1;
/*  348 */             if (i > 0) {
/*  349 */               Path localPath2 = Paths.get(this.val$name.substring(0, i).replace('.', File.separatorChar), new String[0]);
/*  350 */               Files.createDirectories(localPath2, new FileAttribute[0]);
/*  351 */               localPath1 = localPath2.resolve(this.val$name.substring(i + 1, this.val$name.length()) + ".class");
/*      */             } else {
/*  353 */               localPath1 = Paths.get(this.val$name + ".class", new String[0]);
/*      */             }
/*  355 */             Files.write(localPath1, arrayOfByte, new OpenOption[0]);
/*  356 */             return null;
/*      */           } catch (IOException localIOException) {
/*  358 */             throw new InternalError("I/O exception saving generated file: " + localIOException);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  365 */     return arrayOfByte;
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*  317 */     saveGeneratedFiles = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.misc.ProxyGenerator.saveGeneratedFiles"))).booleanValue();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  374 */       hashCodeMethod = Object.class.getMethod("hashCode", new Class[0]);
/*      */       
/*  376 */       equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
/*  377 */       toStringMethod = Object.class.getMethod("toString", new Class[0]);
/*      */     } catch (NoSuchMethodException localNoSuchMethodException) {
/*  379 */       throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  393 */   private ConstantPool cp = new ConstantPool(null);
/*      */   
/*      */ 
/*  396 */   private List<FieldInfo> fields = new ArrayList();
/*      */   
/*      */ 
/*  399 */   private List<MethodInfo> methods = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  405 */   private Map<String, List<ProxyMethod>> proxyMethods = new HashMap();
/*      */   
/*      */ 
/*  408 */   private int proxyMethodCount = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private ProxyGenerator(String paramString, Class<?>[] paramArrayOfClass, int paramInt)
/*      */   {
/*  418 */     this.className = paramString;
/*  419 */     this.interfaces = paramArrayOfClass;
/*  420 */     this.accessFlags = paramInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] generateClassFile()
/*      */   {
/*  441 */     addProxyMethod(hashCodeMethod, Object.class);
/*  442 */     addProxyMethod(equalsMethod, Object.class);
/*  443 */     addProxyMethod(toStringMethod, Object.class);
/*      */     
/*      */ 
/*      */ 
/*      */     Object localObject4;
/*      */     
/*      */ 
/*  450 */     for (localObject4 : this.interfaces) {
/*  451 */       for (Method localMethod : ((Class)localObject4).getMethods()) {
/*  452 */         addProxyMethod(localMethod, (Class)localObject4);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  460 */     for (??? = this.proxyMethods.values().iterator(); ((Iterator)???).hasNext();) { localList = (List)((Iterator)???).next();
/*  461 */       checkReturnTypes(localList);
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*      */       List localList;
/*      */       
/*  469 */       this.methods.add(generateConstructor());
/*      */       
/*  471 */       for (??? = this.proxyMethods.values().iterator(); ((Iterator)???).hasNext();) { localList = (List)((Iterator)???).next();
/*  472 */         for (localIterator = localList.iterator(); localIterator.hasNext();) { localObject4 = (ProxyMethod)localIterator.next();
/*      */           
/*      */ 
/*  475 */           this.fields.add(new FieldInfo(((ProxyMethod)localObject4).methodFieldName, "Ljava/lang/reflect/Method;", 10));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  480 */           this.methods.add(((ProxyMethod)localObject4).generateMethod());
/*      */         }
/*      */       }
/*      */       Iterator localIterator;
/*  484 */       this.methods.add(generateStaticInitializer());
/*      */     }
/*      */     catch (IOException localIOException1) {
/*  487 */       throw new InternalError("unexpected I/O Exception", localIOException1);
/*      */     }
/*      */     
/*  490 */     if (this.methods.size() > 65535) {
/*  491 */       throw new IllegalArgumentException("method limit exceeded");
/*      */     }
/*  493 */     if (this.fields.size() > 65535) {
/*  494 */       throw new IllegalArgumentException("field limit exceeded");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  505 */     this.cp.getClass(dotToSlash(this.className));
/*  506 */     this.cp.getClass("java/lang/reflect/Proxy");
/*  507 */     for (localObject4 : this.interfaces) {
/*  508 */       this.cp.getClass(dotToSlash(((Class)localObject4).getName()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  515 */     this.cp.setReadOnly();
/*      */     
/*  517 */     ??? = new ByteArrayOutputStream();
/*  518 */     DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream)???);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  526 */       localDataOutputStream.writeInt(-889275714);
/*      */       
/*  528 */       localDataOutputStream.writeShort(0);
/*      */       
/*  530 */       localDataOutputStream.writeShort(49);
/*      */       
/*  532 */       this.cp.write(localDataOutputStream);
/*      */       
/*      */ 
/*  535 */       localDataOutputStream.writeShort(this.accessFlags);
/*      */       
/*  537 */       localDataOutputStream.writeShort(this.cp.getClass(dotToSlash(this.className)));
/*      */       
/*  539 */       localDataOutputStream.writeShort(this.cp.getClass("java/lang/reflect/Proxy"));
/*      */       
/*      */ 
/*  542 */       localDataOutputStream.writeShort(this.interfaces.length);
/*      */       
/*  544 */       for (Object localObject6 : this.interfaces) {
/*  545 */         localDataOutputStream.writeShort(this.cp.getClass(
/*  546 */           dotToSlash(((Class)localObject6).getName())));
/*      */       }
/*      */       
/*      */ 
/*  550 */       localDataOutputStream.writeShort(this.fields.size());
/*      */       
/*  552 */       for (??? = this.fields.iterator(); ((Iterator)???).hasNext();) { localObject5 = (FieldInfo)((Iterator)???).next();
/*  553 */         ((FieldInfo)localObject5).write(localDataOutputStream);
/*      */       }
/*      */       
/*      */       Object localObject5;
/*  557 */       localDataOutputStream.writeShort(this.methods.size());
/*      */       
/*  559 */       for (??? = this.methods.iterator(); ((Iterator)???).hasNext();) { localObject5 = (MethodInfo)((Iterator)???).next();
/*  560 */         ((MethodInfo)localObject5).write(localDataOutputStream);
/*      */       }
/*      */       
/*      */ 
/*  564 */       localDataOutputStream.writeShort(0);
/*      */     }
/*      */     catch (IOException localIOException2) {
/*  567 */       throw new InternalError("unexpected I/O Exception", localIOException2);
/*      */     }
/*      */     
/*  570 */     return ((ByteArrayOutputStream)???).toByteArray();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addProxyMethod(Method paramMethod, Class<?> paramClass)
/*      */   {
/*  587 */     String str1 = paramMethod.getName();
/*  588 */     Class[] arrayOfClass1 = paramMethod.getParameterTypes();
/*  589 */     Class localClass = paramMethod.getReturnType();
/*  590 */     Class[] arrayOfClass2 = paramMethod.getExceptionTypes();
/*      */     
/*  592 */     String str2 = str1 + getParameterDescriptors(arrayOfClass1);
/*  593 */     Object localObject = (List)this.proxyMethods.get(str2);
/*  594 */     if (localObject != null) {
/*  595 */       for (ProxyMethod localProxyMethod : (List)localObject) {
/*  596 */         if (localClass == localProxyMethod.returnType)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  603 */           ArrayList localArrayList = new ArrayList();
/*  604 */           collectCompatibleTypes(arrayOfClass2, localProxyMethod.exceptionTypes, localArrayList);
/*      */           
/*  606 */           collectCompatibleTypes(localProxyMethod.exceptionTypes, arrayOfClass2, localArrayList);
/*      */           
/*  608 */           localProxyMethod.exceptionTypes = new Class[localArrayList.size()];
/*      */           
/*  610 */           localProxyMethod.exceptionTypes = ((Class[])localArrayList.toArray(localProxyMethod.exceptionTypes));
/*  611 */           return;
/*      */         }
/*      */       }
/*      */     } else {
/*  615 */       localObject = new ArrayList(3);
/*  616 */       this.proxyMethods.put(str2, localObject);
/*      */     }
/*  618 */     ((List)localObject).add(new ProxyMethod(str1, arrayOfClass1, localClass, arrayOfClass2, paramClass, null));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void checkReturnTypes(List<ProxyMethod> paramList)
/*      */   {
/*  637 */     if (paramList.size() < 2) {
/*  638 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  645 */     LinkedList localLinkedList = new LinkedList();
/*      */     
/*      */ 
/*  648 */     for (Object localObject = paramList.iterator(); ((Iterator)localObject).hasNext();) { ProxyMethod localProxyMethod = (ProxyMethod)((Iterator)localObject).next();
/*  649 */       Class localClass1 = localProxyMethod.returnType;
/*  650 */       if (localClass1.isPrimitive())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  656 */         throw new IllegalArgumentException("methods with same signature " + getFriendlyMethodSignature(localProxyMethod.methodName, localProxyMethod.parameterTypes) + " but incompatible return types: " + localClass1.getName() + " and others");
/*      */       }
/*  658 */       int i = 0;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  664 */       ListIterator localListIterator = localLinkedList.listIterator();
/*  665 */       for (;;) { if (!localListIterator.hasNext()) break label214;
/*  666 */         Class localClass2 = (Class)localListIterator.next();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  672 */         if (localClass1.isAssignableFrom(localClass2)) {
/*  673 */           if (($assertionsDisabled) || (i == 0)) break; throw new AssertionError();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  683 */         if (localClass2.isAssignableFrom(localClass1))
/*      */         {
/*  685 */           if (i == 0) {
/*  686 */             localListIterator.set(localClass1);
/*  687 */             i = 1;
/*      */           } else {
/*  689 */             localListIterator.remove();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  699 */       if (i == 0) {
/*  700 */         localLinkedList.add(localClass1);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     label214:
/*      */     
/*      */ 
/*  708 */     if (localLinkedList.size() > 1) {
/*  709 */       localObject = (ProxyMethod)paramList.get(0);
/*      */       
/*      */ 
/*  712 */       throw new IllegalArgumentException("methods with same signature " + getFriendlyMethodSignature(((ProxyMethod)localObject).methodName, ((ProxyMethod)localObject).parameterTypes) + " but incompatible return types: " + localLinkedList);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class FieldInfo
/*      */   {
/*      */     public int accessFlags;
/*      */     
/*      */     public String name;
/*      */     
/*      */     public String descriptor;
/*      */     
/*      */ 
/*      */     public FieldInfo(String paramString1, String paramString2, int paramInt)
/*      */     {
/*  728 */       this.name = paramString1;
/*  729 */       this.descriptor = paramString2;
/*  730 */       this.accessFlags = paramInt;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  736 */       ProxyGenerator.this.cp.getUtf8(paramString1);
/*  737 */       ProxyGenerator.this.cp.getUtf8(paramString2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void write(DataOutputStream paramDataOutputStream)
/*      */       throws IOException
/*      */     {
/*  746 */       paramDataOutputStream.writeShort(this.accessFlags);
/*      */       
/*  748 */       paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.name));
/*      */       
/*  750 */       paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.descriptor));
/*      */       
/*  752 */       paramDataOutputStream.writeShort(0);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class ExceptionTableEntry
/*      */   {
/*      */     public short startPc;
/*      */     
/*      */     public short endPc;
/*      */     
/*      */     public short handlerPc;
/*      */     
/*      */     public short catchType;
/*      */     
/*      */ 
/*      */     public ExceptionTableEntry(short paramShort1, short paramShort2, short paramShort3, short paramShort4)
/*      */     {
/*  770 */       this.startPc = paramShort1;
/*  771 */       this.endPc = paramShort2;
/*  772 */       this.handlerPc = paramShort3;
/*  773 */       this.catchType = paramShort4;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class MethodInfo
/*      */   {
/*      */     public int accessFlags;
/*      */     
/*      */     public String name;
/*      */     
/*      */     public String descriptor;
/*      */     
/*      */     public short maxStack;
/*      */     public short maxLocals;
/*  788 */     public ByteArrayOutputStream code = new ByteArrayOutputStream();
/*  789 */     public List<ExceptionTableEntry> exceptionTable = new ArrayList();
/*      */     public short[] declaredExceptions;
/*      */     
/*      */     public MethodInfo(String paramString1, String paramString2, int paramInt)
/*      */     {
/*  794 */       this.name = paramString1;
/*  795 */       this.descriptor = paramString2;
/*  796 */       this.accessFlags = paramInt;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  802 */       ProxyGenerator.this.cp.getUtf8(paramString1);
/*  803 */       ProxyGenerator.this.cp.getUtf8(paramString2);
/*  804 */       ProxyGenerator.this.cp.getUtf8("Code");
/*  805 */       ProxyGenerator.this.cp.getUtf8("Exceptions");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void write(DataOutputStream paramDataOutputStream)
/*      */       throws IOException
/*      */     {
/*  814 */       paramDataOutputStream.writeShort(this.accessFlags);
/*      */       
/*  816 */       paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.name));
/*      */       
/*  818 */       paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8(this.descriptor));
/*      */       
/*  820 */       paramDataOutputStream.writeShort(2);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  825 */       paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8("Code"));
/*      */       
/*  827 */       paramDataOutputStream.writeInt(12 + this.code.size() + 8 * this.exceptionTable.size());
/*      */       
/*  829 */       paramDataOutputStream.writeShort(this.maxStack);
/*      */       
/*  831 */       paramDataOutputStream.writeShort(this.maxLocals);
/*      */       
/*  833 */       paramDataOutputStream.writeInt(this.code.size());
/*      */       
/*  835 */       this.code.writeTo(paramDataOutputStream);
/*      */       
/*  837 */       paramDataOutputStream.writeShort(this.exceptionTable.size());
/*  838 */       for (Object localObject = this.exceptionTable.iterator(); ((Iterator)localObject).hasNext();) { ExceptionTableEntry localExceptionTableEntry = (ExceptionTableEntry)((Iterator)localObject).next();
/*      */         
/*  840 */         paramDataOutputStream.writeShort(localExceptionTableEntry.startPc);
/*      */         
/*  842 */         paramDataOutputStream.writeShort(localExceptionTableEntry.endPc);
/*      */         
/*  844 */         paramDataOutputStream.writeShort(localExceptionTableEntry.handlerPc);
/*      */         
/*  846 */         paramDataOutputStream.writeShort(localExceptionTableEntry.catchType);
/*      */       }
/*      */       
/*  849 */       paramDataOutputStream.writeShort(0);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  854 */       paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getUtf8("Exceptions"));
/*      */       
/*  856 */       paramDataOutputStream.writeInt(2 + 2 * this.declaredExceptions.length);
/*      */       
/*  858 */       paramDataOutputStream.writeShort(this.declaredExceptions.length);
/*      */       
/*  860 */       for (int k : this.declaredExceptions) {
/*  861 */         paramDataOutputStream.writeShort(k);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class ProxyMethod
/*      */   {
/*      */     public String methodName;
/*      */     
/*      */     public Class<?>[] parameterTypes;
/*      */     
/*      */     public Class<?> returnType;
/*      */     
/*      */     public Class<?>[] exceptionTypes;
/*      */     
/*      */     public Class<?> fromClass;
/*      */     
/*      */     public String methodFieldName;
/*      */     
/*      */ 
/*      */     private ProxyMethod(Class<?>[] paramArrayOfClass1, Class<?> paramClass1, Class<?>[] paramArrayOfClass2, Class<?> paramClass2)
/*      */     {
/*  885 */       this.methodName = paramArrayOfClass1;
/*  886 */       this.parameterTypes = paramClass1;
/*  887 */       this.returnType = paramArrayOfClass2;
/*  888 */       this.exceptionTypes = paramClass2;
/*  889 */       Class localClass; this.fromClass = localClass;
/*  890 */       this.methodFieldName = ("m" + ProxyGenerator.access$408(ProxyGenerator.this));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private MethodInfo generateMethod()
/*      */       throws IOException
/*      */     {
/*  898 */       String str = ProxyGenerator.getMethodDescriptor(this.parameterTypes, this.returnType);
/*  899 */       MethodInfo localMethodInfo = new MethodInfo(ProxyGenerator.this, this.methodName, str, 17);
/*      */       
/*      */ 
/*  902 */       int[] arrayOfInt = new int[this.parameterTypes.length];
/*  903 */       int i = 1;
/*  904 */       for (int j = 0; j < arrayOfInt.length; j++) {
/*  905 */         arrayOfInt[j] = i;
/*  906 */         i += ProxyGenerator.getWordsPerType(this.parameterTypes[j]);
/*      */       }
/*  908 */       j = i;
/*  909 */       short s2 = 0;
/*      */       
/*  911 */       DataOutputStream localDataOutputStream = new DataOutputStream(localMethodInfo.code);
/*      */       
/*  913 */       ProxyGenerator.this.code_aload(0, localDataOutputStream);
/*      */       
/*  915 */       localDataOutputStream.writeByte(180);
/*  916 */       localDataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef("java/lang/reflect/Proxy", "h", "Ljava/lang/reflect/InvocationHandler;"));
/*      */       
/*      */ 
/*      */ 
/*  920 */       ProxyGenerator.this.code_aload(0, localDataOutputStream);
/*      */       
/*  922 */       localDataOutputStream.writeByte(178);
/*  923 */       localDataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef(
/*  924 */         ProxyGenerator.dotToSlash(ProxyGenerator.this.className), this.methodFieldName, "Ljava/lang/reflect/Method;"));
/*      */       
/*      */ 
/*  927 */       if (this.parameterTypes.length > 0)
/*      */       {
/*  929 */         ProxyGenerator.this.code_ipush(this.parameterTypes.length, localDataOutputStream);
/*      */         
/*  931 */         localDataOutputStream.writeByte(189);
/*  932 */         localDataOutputStream.writeShort(ProxyGenerator.this.cp.getClass("java/lang/Object"));
/*      */         
/*  934 */         for (int k = 0; k < this.parameterTypes.length; k++)
/*      */         {
/*  936 */           localDataOutputStream.writeByte(89);
/*      */           
/*  938 */           ProxyGenerator.this.code_ipush(k, localDataOutputStream);
/*      */           
/*  940 */           codeWrapArgument(this.parameterTypes[k], arrayOfInt[k], localDataOutputStream);
/*      */           
/*  942 */           localDataOutputStream.writeByte(83);
/*      */         }
/*      */       }
/*      */       else {
/*  946 */         localDataOutputStream.writeByte(1);
/*      */       }
/*      */       
/*  949 */       localDataOutputStream.writeByte(185);
/*  950 */       localDataOutputStream.writeShort(ProxyGenerator.this.cp.getInterfaceMethodRef("java/lang/reflect/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  955 */       localDataOutputStream.writeByte(4);
/*  956 */       localDataOutputStream.writeByte(0);
/*      */       
/*  958 */       if (this.returnType == Void.TYPE)
/*      */       {
/*  960 */         localDataOutputStream.writeByte(87);
/*      */         
/*  962 */         localDataOutputStream.writeByte(177);
/*      */       }
/*      */       else
/*      */       {
/*  966 */         codeUnwrapReturnValue(this.returnType, localDataOutputStream);
/*      */       }
/*      */       short s1;
/*  969 */       short s3 = s1 = (short)localMethodInfo.code.size();
/*      */       
/*  971 */       List localList = ProxyGenerator.computeUniqueCatchList(this.exceptionTypes);
/*  972 */       if (localList.size() > 0)
/*      */       {
/*  974 */         for (Class localClass : localList) {
/*  975 */           localMethodInfo.exceptionTable.add(new ExceptionTableEntry(s2, s3, s1,
/*      */           
/*  977 */             ProxyGenerator.this.cp.getClass(ProxyGenerator.dotToSlash(localClass.getName()))));
/*      */         }
/*      */         
/*  980 */         localDataOutputStream.writeByte(191);
/*      */         
/*  982 */         s1 = (short)localMethodInfo.code.size();
/*      */         
/*  984 */         localMethodInfo.exceptionTable.add(new ExceptionTableEntry(s2, s3, s1,
/*  985 */           ProxyGenerator.this.cp.getClass("java/lang/Throwable")));
/*      */         
/*  987 */         ProxyGenerator.this.code_astore(j, localDataOutputStream);
/*      */         
/*  989 */         localDataOutputStream.writeByte(187);
/*  990 */         localDataOutputStream.writeShort(ProxyGenerator.this.cp.getClass("java/lang/reflect/UndeclaredThrowableException"));
/*      */         
/*      */ 
/*  993 */         localDataOutputStream.writeByte(89);
/*      */         
/*  995 */         ProxyGenerator.this.code_aload(j, localDataOutputStream);
/*      */         
/*  997 */         localDataOutputStream.writeByte(183);
/*      */         
/*  999 */         localDataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef("java/lang/reflect/UndeclaredThrowableException", "<init>", "(Ljava/lang/Throwable;)V"));
/*      */         
/*      */ 
/*      */ 
/* 1003 */         localDataOutputStream.writeByte(191);
/*      */       }
/*      */       
/* 1006 */       if (localMethodInfo.code.size() > 65535) {
/* 1007 */         throw new IllegalArgumentException("code size limit exceeded");
/*      */       }
/*      */       
/* 1010 */       localMethodInfo.maxStack = 10;
/* 1011 */       localMethodInfo.maxLocals = ((short)(j + 1));
/* 1012 */       localMethodInfo.declaredExceptions = new short[this.exceptionTypes.length];
/* 1013 */       for (int m = 0; m < this.exceptionTypes.length; m++) {
/* 1014 */         localMethodInfo.declaredExceptions[m] = ProxyGenerator.this.cp.getClass(
/* 1015 */           ProxyGenerator.dotToSlash(this.exceptionTypes[m].getName()));
/*      */       }
/*      */       
/* 1018 */       return localMethodInfo;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void codeWrapArgument(Class<?> paramClass, int paramInt, DataOutputStream paramDataOutputStream)
/*      */       throws IOException
/*      */     {
/* 1032 */       if (paramClass.isPrimitive()) {
/* 1033 */         PrimitiveTypeInfo localPrimitiveTypeInfo = PrimitiveTypeInfo.get(paramClass);
/*      */         
/* 1035 */         if ((paramClass == Integer.TYPE) || (paramClass == Boolean.TYPE) || (paramClass == Byte.TYPE) || (paramClass == Character.TYPE) || (paramClass == Short.TYPE))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1041 */           ProxyGenerator.this.code_iload(paramInt, paramDataOutputStream);
/* 1042 */         } else if (paramClass == Long.TYPE) {
/* 1043 */           ProxyGenerator.this.code_lload(paramInt, paramDataOutputStream);
/* 1044 */         } else if (paramClass == Float.TYPE) {
/* 1045 */           ProxyGenerator.this.code_fload(paramInt, paramDataOutputStream);
/* 1046 */         } else if (paramClass == Double.TYPE) {
/* 1047 */           ProxyGenerator.this.code_dload(paramInt, paramDataOutputStream);
/*      */         } else {
/* 1049 */           throw new AssertionError();
/*      */         }
/*      */         
/* 1052 */         paramDataOutputStream.writeByte(184);
/* 1053 */         paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef(localPrimitiveTypeInfo.wrapperClassName, "valueOf", localPrimitiveTypeInfo.wrapperValueOfDesc));
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1059 */         ProxyGenerator.this.code_aload(paramInt, paramDataOutputStream);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void codeUnwrapReturnValue(Class<?> paramClass, DataOutputStream paramDataOutputStream)
/*      */       throws IOException
/*      */     {
/* 1072 */       if (paramClass.isPrimitive()) {
/* 1073 */         PrimitiveTypeInfo localPrimitiveTypeInfo = PrimitiveTypeInfo.get(paramClass);
/*      */         
/* 1075 */         paramDataOutputStream.writeByte(192);
/* 1076 */         paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getClass(localPrimitiveTypeInfo.wrapperClassName));
/*      */         
/* 1078 */         paramDataOutputStream.writeByte(182);
/* 1079 */         paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef(localPrimitiveTypeInfo.wrapperClassName, localPrimitiveTypeInfo.unwrapMethodName, localPrimitiveTypeInfo.unwrapMethodDesc));
/*      */         
/*      */ 
/*      */ 
/* 1083 */         if ((paramClass == Integer.TYPE) || (paramClass == Boolean.TYPE) || (paramClass == Byte.TYPE) || (paramClass == Character.TYPE) || (paramClass == Short.TYPE))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1089 */           paramDataOutputStream.writeByte(172);
/* 1090 */         } else if (paramClass == Long.TYPE) {
/* 1091 */           paramDataOutputStream.writeByte(173);
/* 1092 */         } else if (paramClass == Float.TYPE) {
/* 1093 */           paramDataOutputStream.writeByte(174);
/* 1094 */         } else if (paramClass == Double.TYPE) {
/* 1095 */           paramDataOutputStream.writeByte(175);
/*      */         } else {
/* 1097 */           throw new AssertionError();
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1102 */         paramDataOutputStream.writeByte(192);
/* 1103 */         paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getClass(ProxyGenerator.dotToSlash(paramClass.getName())));
/*      */         
/* 1105 */         paramDataOutputStream.writeByte(176);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void codeFieldInitialization(DataOutputStream paramDataOutputStream)
/*      */       throws IOException
/*      */     {
/* 1117 */       ProxyGenerator.this.codeClassForName(this.fromClass, paramDataOutputStream);
/*      */       
/* 1119 */       ProxyGenerator.this.code_ldc(ProxyGenerator.this.cp.getString(this.methodName), paramDataOutputStream);
/*      */       
/* 1121 */       ProxyGenerator.this.code_ipush(this.parameterTypes.length, paramDataOutputStream);
/*      */       
/* 1123 */       paramDataOutputStream.writeByte(189);
/* 1124 */       paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getClass("java/lang/Class"));
/*      */       
/* 1126 */       for (int i = 0; i < this.parameterTypes.length; i++)
/*      */       {
/* 1128 */         paramDataOutputStream.writeByte(89);
/*      */         
/* 1130 */         ProxyGenerator.this.code_ipush(i, paramDataOutputStream);
/*      */         
/* 1132 */         if (this.parameterTypes[i].isPrimitive())
/*      */         {
/* 1134 */           PrimitiveTypeInfo localPrimitiveTypeInfo = PrimitiveTypeInfo.get(this.parameterTypes[i]);
/*      */           
/* 1136 */           paramDataOutputStream.writeByte(178);
/* 1137 */           paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef(localPrimitiveTypeInfo.wrapperClassName, "TYPE", "Ljava/lang/Class;"));
/*      */         }
/*      */         else
/*      */         {
/* 1141 */           ProxyGenerator.this.codeClassForName(this.parameterTypes[i], paramDataOutputStream);
/*      */         }
/*      */         
/* 1144 */         paramDataOutputStream.writeByte(83);
/*      */       }
/*      */       
/* 1147 */       paramDataOutputStream.writeByte(182);
/* 1148 */       paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getMethodRef("java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;"));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1154 */       paramDataOutputStream.writeByte(179);
/* 1155 */       paramDataOutputStream.writeShort(ProxyGenerator.this.cp.getFieldRef(
/* 1156 */         ProxyGenerator.dotToSlash(ProxyGenerator.this.className), this.methodFieldName, "Ljava/lang/reflect/Method;"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private MethodInfo generateConstructor()
/*      */     throws IOException
/*      */   {
/* 1165 */     MethodInfo localMethodInfo = new MethodInfo("<init>", "(Ljava/lang/reflect/InvocationHandler;)V", 1);
/*      */     
/*      */ 
/*      */ 
/* 1169 */     DataOutputStream localDataOutputStream = new DataOutputStream(localMethodInfo.code);
/*      */     
/* 1171 */     code_aload(0, localDataOutputStream);
/*      */     
/* 1173 */     code_aload(1, localDataOutputStream);
/*      */     
/* 1175 */     localDataOutputStream.writeByte(183);
/* 1176 */     localDataOutputStream.writeShort(this.cp.getMethodRef("java/lang/reflect/Proxy", "<init>", "(Ljava/lang/reflect/InvocationHandler;)V"));
/*      */     
/*      */ 
/*      */ 
/* 1180 */     localDataOutputStream.writeByte(177);
/*      */     
/* 1182 */     localMethodInfo.maxStack = 10;
/* 1183 */     localMethodInfo.maxLocals = 2;
/* 1184 */     localMethodInfo.declaredExceptions = new short[0];
/*      */     
/* 1186 */     return localMethodInfo;
/*      */   }
/*      */   
/*      */ 
/*      */   private MethodInfo generateStaticInitializer()
/*      */     throws IOException
/*      */   {
/* 1193 */     MethodInfo localMethodInfo = new MethodInfo("<clinit>", "()V", 8);
/*      */     
/*      */ 
/* 1196 */     int i = 1;
/* 1197 */     short s2 = 0;
/*      */     
/* 1199 */     DataOutputStream localDataOutputStream = new DataOutputStream(localMethodInfo.code);
/*      */     
/* 1201 */     for (List localList : this.proxyMethods.values()) {
/* 1202 */       for (ProxyMethod localProxyMethod : localList) {
/* 1203 */         localProxyMethod.codeFieldInitialization(localDataOutputStream);
/*      */       }
/*      */     }
/*      */     
/* 1207 */     localDataOutputStream.writeByte(177);
/*      */     
/* 1209 */     short s3 = s1 = (short)localMethodInfo.code.size();
/*      */     
/* 1211 */     localMethodInfo.exceptionTable.add(new ExceptionTableEntry(s2, s3, s1, this.cp
/*      */     
/* 1213 */       .getClass("java/lang/NoSuchMethodException")));
/*      */     
/* 1215 */     code_astore(i, localDataOutputStream);
/*      */     
/* 1217 */     localDataOutputStream.writeByte(187);
/* 1218 */     localDataOutputStream.writeShort(this.cp.getClass("java/lang/NoSuchMethodError"));
/*      */     
/* 1220 */     localDataOutputStream.writeByte(89);
/*      */     
/* 1222 */     code_aload(i, localDataOutputStream);
/*      */     
/* 1224 */     localDataOutputStream.writeByte(182);
/* 1225 */     localDataOutputStream.writeShort(this.cp.getMethodRef("java/lang/Throwable", "getMessage", "()Ljava/lang/String;"));
/*      */     
/*      */ 
/* 1228 */     localDataOutputStream.writeByte(183);
/* 1229 */     localDataOutputStream.writeShort(this.cp.getMethodRef("java/lang/NoSuchMethodError", "<init>", "(Ljava/lang/String;)V"));
/*      */     
/*      */ 
/* 1232 */     localDataOutputStream.writeByte(191);
/*      */     
/* 1234 */     short s1 = (short)localMethodInfo.code.size();
/*      */     
/* 1236 */     localMethodInfo.exceptionTable.add(new ExceptionTableEntry(s2, s3, s1, this.cp
/*      */     
/* 1238 */       .getClass("java/lang/ClassNotFoundException")));
/*      */     
/* 1240 */     code_astore(i, localDataOutputStream);
/*      */     
/* 1242 */     localDataOutputStream.writeByte(187);
/* 1243 */     localDataOutputStream.writeShort(this.cp.getClass("java/lang/NoClassDefFoundError"));
/*      */     
/* 1245 */     localDataOutputStream.writeByte(89);
/*      */     
/* 1247 */     code_aload(i, localDataOutputStream);
/*      */     
/* 1249 */     localDataOutputStream.writeByte(182);
/* 1250 */     localDataOutputStream.writeShort(this.cp.getMethodRef("java/lang/Throwable", "getMessage", "()Ljava/lang/String;"));
/*      */     
/*      */ 
/* 1253 */     localDataOutputStream.writeByte(183);
/* 1254 */     localDataOutputStream.writeShort(this.cp.getMethodRef("java/lang/NoClassDefFoundError", "<init>", "(Ljava/lang/String;)V"));
/*      */     
/*      */ 
/*      */ 
/* 1258 */     localDataOutputStream.writeByte(191);
/*      */     
/* 1260 */     if (localMethodInfo.code.size() > 65535) {
/* 1261 */       throw new IllegalArgumentException("code size limit exceeded");
/*      */     }
/*      */     
/* 1264 */     localMethodInfo.maxStack = 10;
/* 1265 */     localMethodInfo.maxLocals = ((short)(i + 1));
/* 1266 */     localMethodInfo.declaredExceptions = new short[0];
/*      */     
/* 1268 */     return localMethodInfo;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void code_iload(int paramInt, DataOutputStream paramDataOutputStream)
/*      */     throws IOException
/*      */   {
/* 1285 */     codeLocalLoadStore(paramInt, 21, 26, paramDataOutputStream);
/*      */   }
/*      */   
/*      */   private void code_lload(int paramInt, DataOutputStream paramDataOutputStream)
/*      */     throws IOException
/*      */   {
/* 1291 */     codeLocalLoadStore(paramInt, 22, 30, paramDataOutputStream);
/*      */   }
/*      */   
/*      */   private void code_fload(int paramInt, DataOutputStream paramDataOutputStream)
/*      */     throws IOException
/*      */   {
/* 1297 */     codeLocalLoadStore(paramInt, 23, 34, paramDataOutputStream);
/*      */   }
/*      */   
/*      */   private void code_dload(int paramInt, DataOutputStream paramDataOutputStream)
/*      */     throws IOException
/*      */   {
/* 1303 */     codeLocalLoadStore(paramInt, 24, 38, paramDataOutputStream);
/*      */   }
/*      */   
/*      */   private void code_aload(int paramInt, DataOutputStream paramDataOutputStream)
/*      */     throws IOException
/*      */   {
/* 1309 */     codeLocalLoadStore(paramInt, 25, 42, paramDataOutputStream);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void code_astore(int paramInt, DataOutputStream paramDataOutputStream)
/*      */     throws IOException
/*      */   {
/* 1339 */     codeLocalLoadStore(paramInt, 58, 75, paramDataOutputStream);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void codeLocalLoadStore(int paramInt1, int paramInt2, int paramInt3, DataOutputStream paramDataOutputStream)
/*      */     throws IOException
/*      */   {
/* 1355 */     assert ((paramInt1 >= 0) && (paramInt1 <= 65535));
/* 1356 */     if (paramInt1 <= 3) {
/* 1357 */       paramDataOutputStream.writeByte(paramInt3 + paramInt1);
/* 1358 */     } else if (paramInt1 <= 255) {
/* 1359 */       paramDataOutputStream.writeByte(paramInt2);
/* 1360 */       paramDataOutputStream.writeByte(paramInt1 & 0xFF);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/* 1366 */       paramDataOutputStream.writeByte(196);
/* 1367 */       paramDataOutputStream.writeByte(paramInt2);
/* 1368 */       paramDataOutputStream.writeShort(paramInt1 & 0xFFFF);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void code_ldc(int paramInt, DataOutputStream paramDataOutputStream)
/*      */     throws IOException
/*      */   {
/* 1380 */     assert ((paramInt >= 0) && (paramInt <= 65535));
/* 1381 */     if (paramInt <= 255) {
/* 1382 */       paramDataOutputStream.writeByte(18);
/* 1383 */       paramDataOutputStream.writeByte(paramInt & 0xFF);
/*      */     } else {
/* 1385 */       paramDataOutputStream.writeByte(19);
/* 1386 */       paramDataOutputStream.writeShort(paramInt & 0xFFFF);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void code_ipush(int paramInt, DataOutputStream paramDataOutputStream)
/*      */     throws IOException
/*      */   {
/* 1399 */     if ((paramInt >= -1) && (paramInt <= 5)) {
/* 1400 */       paramDataOutputStream.writeByte(3 + paramInt);
/* 1401 */     } else if ((paramInt >= -128) && (paramInt <= 127)) {
/* 1402 */       paramDataOutputStream.writeByte(16);
/* 1403 */       paramDataOutputStream.writeByte(paramInt & 0xFF);
/* 1404 */     } else if ((paramInt >= 32768) && (paramInt <= 32767)) {
/* 1405 */       paramDataOutputStream.writeByte(17);
/* 1406 */       paramDataOutputStream.writeShort(paramInt & 0xFFFF);
/*      */     } else {
/* 1408 */       throw new AssertionError();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void codeClassForName(Class<?> paramClass, DataOutputStream paramDataOutputStream)
/*      */     throws IOException
/*      */   {
/* 1421 */     code_ldc(this.cp.getString(paramClass.getName()), paramDataOutputStream);
/*      */     
/* 1423 */     paramDataOutputStream.writeByte(184);
/* 1424 */     paramDataOutputStream.writeShort(this.cp.getMethodRef("java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String dotToSlash(String paramString)
/*      */   {
/* 1442 */     return paramString.replace('.', '/');
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getMethodDescriptor(Class<?>[] paramArrayOfClass, Class<?> paramClass)
/*      */   {
/* 1452 */     return 
/* 1453 */       getParameterDescriptors(paramArrayOfClass) + (paramClass == Void.TYPE ? "V" : getFieldType(paramClass));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getParameterDescriptors(Class<?>[] paramArrayOfClass)
/*      */   {
/* 1464 */     StringBuilder localStringBuilder = new StringBuilder("(");
/* 1465 */     for (int i = 0; i < paramArrayOfClass.length; i++) {
/* 1466 */       localStringBuilder.append(getFieldType(paramArrayOfClass[i]));
/*      */     }
/* 1468 */     localStringBuilder.append(')');
/* 1469 */     return localStringBuilder.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getFieldType(Class<?> paramClass)
/*      */   {
/* 1478 */     if (paramClass.isPrimitive())
/* 1479 */       return PrimitiveTypeInfo.get(paramClass).baseTypeString;
/* 1480 */     if (paramClass.isArray())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1488 */       return paramClass.getName().replace('.', '/');
/*      */     }
/* 1490 */     return "L" + dotToSlash(paramClass.getName()) + ";";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getFriendlyMethodSignature(String paramString, Class<?>[] paramArrayOfClass)
/*      */   {
/* 1501 */     StringBuilder localStringBuilder = new StringBuilder(paramString);
/* 1502 */     localStringBuilder.append('(');
/* 1503 */     for (int i = 0; i < paramArrayOfClass.length; i++) {
/* 1504 */       if (i > 0) {
/* 1505 */         localStringBuilder.append(',');
/*      */       }
/* 1507 */       Object localObject = paramArrayOfClass[i];
/* 1508 */       int j = 0;
/* 1509 */       while (((Class)localObject).isArray()) {
/* 1510 */         localObject = ((Class)localObject).getComponentType();
/* 1511 */         j++;
/*      */       }
/* 1513 */       localStringBuilder.append(((Class)localObject).getName());
/* 1514 */       while (j-- > 0) {
/* 1515 */         localStringBuilder.append("[]");
/*      */       }
/*      */     }
/* 1518 */     localStringBuilder.append(')');
/* 1519 */     return localStringBuilder.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int getWordsPerType(Class<?> paramClass)
/*      */   {
/* 1532 */     if ((paramClass == Long.TYPE) || (paramClass == Double.TYPE)) {
/* 1533 */       return 2;
/*      */     }
/* 1535 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void collectCompatibleTypes(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, List<Class<?>> paramList)
/*      */   {
/* 1552 */     for (Class<?> localClass1 : paramArrayOfClass1) {
/* 1553 */       if (!paramList.contains(localClass1)) {
/* 1554 */         for (Class<?> localClass2 : paramArrayOfClass2) {
/* 1555 */           if (localClass2.isAssignableFrom(localClass1)) {
/* 1556 */             paramList.add(localClass1);
/* 1557 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static List<Class<?>> computeUniqueCatchList(Class<?>[] paramArrayOfClass)
/*      */   {
/* 1586 */     ArrayList localArrayList = new ArrayList();
/*      */     
/*      */ 
/* 1589 */     localArrayList.add(Error.class);
/* 1590 */     localArrayList.add(RuntimeException.class);
/*      */     
/*      */     label155:
/* 1593 */     for (Class<?> localClass : paramArrayOfClass) {
/* 1594 */       if (localClass.isAssignableFrom(Throwable.class))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1600 */         localArrayList.clear();
/* 1601 */         break; }
/* 1602 */       if (Throwable.class.isAssignableFrom(localClass))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1612 */         for (int k = 0; k < localArrayList.size();) {
/* 1613 */           Class localClass1 = (Class)localArrayList.get(k);
/* 1614 */           if (localClass1.isAssignableFrom(localClass)) {
/*      */             break label155;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1620 */           if (localClass.isAssignableFrom(localClass1))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1625 */             localArrayList.remove(k);
/*      */           } else {
/* 1627 */             k++;
/*      */           }
/*      */         }
/*      */         
/* 1631 */         localArrayList.add(localClass);
/*      */       } }
/* 1633 */     return localArrayList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class PrimitiveTypeInfo
/*      */   {
/*      */     public String baseTypeString;
/*      */     
/*      */ 
/*      */     public String wrapperClassName;
/*      */     
/*      */ 
/*      */     public String wrapperValueOfDesc;
/*      */     
/*      */ 
/*      */     public String unwrapMethodName;
/*      */     
/*      */     public String unwrapMethodDesc;
/*      */     
/*      */     private static Map<Class<?>, PrimitiveTypeInfo> table;
/*      */     
/*      */ 
/*      */     static
/*      */     {
/* 1658 */       table = new HashMap();
/*      */       
/* 1660 */       add(Byte.TYPE, Byte.class);
/* 1661 */       add(Character.TYPE, Character.class);
/* 1662 */       add(Double.TYPE, Double.class);
/* 1663 */       add(Float.TYPE, Float.class);
/* 1664 */       add(Integer.TYPE, Integer.class);
/* 1665 */       add(Long.TYPE, Long.class);
/* 1666 */       add(Short.TYPE, Short.class);
/* 1667 */       add(Boolean.TYPE, Boolean.class);
/*      */     }
/*      */     
/*      */     private static void add(Class<?> paramClass1, Class<?> paramClass2) {
/* 1671 */       table.put(paramClass1, new PrimitiveTypeInfo(paramClass1, paramClass2));
/*      */     }
/*      */     
/*      */     private PrimitiveTypeInfo(Class<?> paramClass1, Class<?> paramClass2)
/*      */     {
/* 1676 */       assert (paramClass1.isPrimitive());
/*      */       
/*      */ 
/*      */ 
/* 1680 */       this.baseTypeString = Array.newInstance(paramClass1, 0).getClass().getName().substring(1);
/* 1681 */       this.wrapperClassName = ProxyGenerator.dotToSlash(paramClass2.getName());
/* 1682 */       this.wrapperValueOfDesc = ("(" + this.baseTypeString + ")L" + this.wrapperClassName + ";");
/*      */       
/* 1684 */       this.unwrapMethodName = (paramClass1.getName() + "Value");
/* 1685 */       this.unwrapMethodDesc = ("()" + this.baseTypeString);
/*      */     }
/*      */     
/*      */     public static PrimitiveTypeInfo get(Class<?> paramClass) {
/* 1689 */       return (PrimitiveTypeInfo)table.get(paramClass);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class ConstantPool
/*      */   {
/* 1719 */     private List<Entry> pool = new ArrayList(32);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1727 */     private Map<Object, Short> map = new HashMap(16);
/*      */     
/*      */ 
/* 1730 */     private boolean readOnly = false;
/*      */     
/*      */ 
/*      */ 
/*      */     public short getUtf8(String paramString)
/*      */     {
/* 1736 */       if (paramString == null) {
/* 1737 */         throw new NullPointerException();
/*      */       }
/* 1739 */       return getValue(paramString);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public short getInteger(int paramInt)
/*      */     {
/* 1746 */       return getValue(new Integer(paramInt));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public short getFloat(float paramFloat)
/*      */     {
/* 1753 */       return getValue(new Float(paramFloat));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public short getClass(String paramString)
/*      */     {
/* 1760 */       short s = getUtf8(paramString);
/* 1761 */       return getIndirect(new IndirectEntry(7, s));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public short getString(String paramString)
/*      */     {
/* 1769 */       short s = getUtf8(paramString);
/* 1770 */       return getIndirect(new IndirectEntry(8, s));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public short getFieldRef(String paramString1, String paramString2, String paramString3)
/*      */     {
/* 1780 */       short s1 = getClass(paramString1);
/* 1781 */       short s2 = getNameAndType(paramString2, paramString3);
/* 1782 */       return getIndirect(new IndirectEntry(9, s1, s2));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public short getMethodRef(String paramString1, String paramString2, String paramString3)
/*      */     {
/* 1792 */       short s1 = getClass(paramString1);
/* 1793 */       short s2 = getNameAndType(paramString2, paramString3);
/* 1794 */       return getIndirect(new IndirectEntry(10, s1, s2));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public short getInterfaceMethodRef(String paramString1, String paramString2, String paramString3)
/*      */     {
/* 1804 */       short s1 = getClass(paramString1);
/* 1805 */       short s2 = getNameAndType(paramString2, paramString3);
/* 1806 */       return getIndirect(new IndirectEntry(11, s1, s2));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public short getNameAndType(String paramString1, String paramString2)
/*      */     {
/* 1814 */       short s1 = getUtf8(paramString1);
/* 1815 */       short s2 = getUtf8(paramString2);
/* 1816 */       return getIndirect(new IndirectEntry(12, s1, s2));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setReadOnly()
/*      */     {
/* 1828 */       this.readOnly = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void write(OutputStream paramOutputStream)
/*      */       throws IOException
/*      */     {
/* 1840 */       DataOutputStream localDataOutputStream = new DataOutputStream(paramOutputStream);
/*      */       
/*      */ 
/* 1843 */       localDataOutputStream.writeShort(this.pool.size() + 1);
/*      */       
/* 1845 */       for (Entry localEntry : this.pool) {
/* 1846 */         localEntry.write(localDataOutputStream);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private short addEntry(Entry paramEntry)
/*      */     {
/* 1854 */       this.pool.add(paramEntry);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1860 */       if (this.pool.size() >= 65535) {
/* 1861 */         throw new IllegalArgumentException("constant pool size limit exceeded");
/*      */       }
/*      */       
/* 1864 */       return (short)this.pool.size();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private short getValue(Object paramObject)
/*      */     {
/* 1879 */       Short localShort = (Short)this.map.get(paramObject);
/* 1880 */       if (localShort != null) {
/* 1881 */         return localShort.shortValue();
/*      */       }
/* 1883 */       if (this.readOnly) {
/* 1884 */         throw new InternalError("late constant pool addition: " + paramObject);
/*      */       }
/*      */       
/* 1887 */       short s = addEntry(new ValueEntry(paramObject));
/* 1888 */       this.map.put(paramObject, new Short(s));
/* 1889 */       return s;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private short getIndirect(IndirectEntry paramIndirectEntry)
/*      */     {
/* 1898 */       Short localShort = (Short)this.map.get(paramIndirectEntry);
/* 1899 */       if (localShort != null) {
/* 1900 */         return localShort.shortValue();
/*      */       }
/* 1902 */       if (this.readOnly) {
/* 1903 */         throw new InternalError("late constant pool addition");
/*      */       }
/* 1905 */       short s = addEntry(paramIndirectEntry);
/* 1906 */       this.map.put(paramIndirectEntry, new Short(s));
/* 1907 */       return s;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private static class ValueEntry
/*      */       extends Entry
/*      */     {
/*      */       private Object value;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public ValueEntry(Object paramObject)
/*      */       {
/* 1932 */         super();
/* 1933 */         this.value = paramObject;
/*      */       }
/*      */       
/*      */       public void write(DataOutputStream paramDataOutputStream) throws IOException {
/* 1937 */         if ((this.value instanceof String)) {
/* 1938 */           paramDataOutputStream.writeByte(1);
/* 1939 */           paramDataOutputStream.writeUTF((String)this.value);
/* 1940 */         } else if ((this.value instanceof Integer)) {
/* 1941 */           paramDataOutputStream.writeByte(3);
/* 1942 */           paramDataOutputStream.writeInt(((Integer)this.value).intValue());
/* 1943 */         } else if ((this.value instanceof Float)) {
/* 1944 */           paramDataOutputStream.writeByte(4);
/* 1945 */           paramDataOutputStream.writeFloat(((Float)this.value).floatValue());
/* 1946 */         } else if ((this.value instanceof Long)) {
/* 1947 */           paramDataOutputStream.writeByte(5);
/* 1948 */           paramDataOutputStream.writeLong(((Long)this.value).longValue());
/* 1949 */         } else if ((this.value instanceof Double)) {
/* 1950 */           paramDataOutputStream.writeDouble(6.0D);
/* 1951 */           paramDataOutputStream.writeDouble(((Double)this.value).doubleValue());
/*      */         } else {
/* 1953 */           throw new InternalError("bogus value entry: " + this.value);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private static class IndirectEntry
/*      */       extends Entry
/*      */     {
/*      */       private int tag;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       private short index0;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       private short index1;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public IndirectEntry(int paramInt, short paramShort)
/*      */       {
/* 1982 */         super();
/* 1983 */         this.tag = paramInt;
/* 1984 */         this.index0 = paramShort;
/* 1985 */         this.index1 = 0;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public IndirectEntry(int paramInt, short paramShort1, short paramShort2)
/*      */       {
/* 1992 */         super();
/* 1993 */         this.tag = paramInt;
/* 1994 */         this.index0 = paramShort1;
/* 1995 */         this.index1 = paramShort2;
/*      */       }
/*      */       
/*      */       public void write(DataOutputStream paramDataOutputStream) throws IOException {
/* 1999 */         paramDataOutputStream.writeByte(this.tag);
/* 2000 */         paramDataOutputStream.writeShort(this.index0);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2005 */         if ((this.tag == 9) || (this.tag == 10) || (this.tag == 11) || (this.tag == 12))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2010 */           paramDataOutputStream.writeShort(this.index1);
/*      */         }
/*      */       }
/*      */       
/*      */       public int hashCode() {
/* 2015 */         return this.tag + this.index0 + this.index1;
/*      */       }
/*      */       
/*      */       public boolean equals(Object paramObject) {
/* 2019 */         if ((paramObject instanceof IndirectEntry)) {
/* 2020 */           IndirectEntry localIndirectEntry = (IndirectEntry)paramObject;
/* 2021 */           if ((this.tag == localIndirectEntry.tag) && (this.index0 == localIndirectEntry.index0) && (this.index1 == localIndirectEntry.index1))
/*      */           {
/*      */ 
/* 2024 */             return true;
/*      */           }
/*      */         }
/* 2027 */         return false;
/*      */       }
/*      */     }
/*      */     
/*      */     private static abstract class Entry
/*      */     {
/*      */       public abstract void write(DataOutputStream paramDataOutputStream)
/*      */         throws IOException;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\ProxyGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */