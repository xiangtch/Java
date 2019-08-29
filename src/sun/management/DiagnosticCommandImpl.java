/*     */ package sun.management;
/*     */ 
/*     */ import com.sun.management.DiagnosticCommandMBean;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.security.Permission;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeList;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.Descriptor;
/*     */ import javax.management.ImmutableDescriptor;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.ListenerNotFoundException;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanNotificationInfo;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ import javax.management.MBeanParameterInfo;
/*     */ import javax.management.MalformedObjectNameException;
/*     */ import javax.management.Notification;
/*     */ import javax.management.NotificationFilter;
/*     */ import javax.management.NotificationListener;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.ReflectionException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class DiagnosticCommandImpl
/*     */   extends NotificationEmitterSupport
/*     */   implements DiagnosticCommandMBean
/*     */ {
/*     */   private final VMManagement jvm;
/*  44 */   private volatile Map<String, Wrapper> wrappers = null;
/*  45 */   private static final String strClassName = "".getClass().getName();
/*  46 */   private static final String strArrayClassName = String[].class.getName();
/*     */   private final boolean isSupported;
/*     */   private static final String notifName = "javax.management.Notification";
/*     */   
/*     */   public Object getAttribute(String paramString) throws AttributeNotFoundException, MBeanException, ReflectionException
/*     */   {
/*  52 */     throw new AttributeNotFoundException(paramString);
/*     */   }
/*     */   
/*     */   public void setAttribute(Attribute paramAttribute)
/*     */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*     */   {
/*  58 */     throw new AttributeNotFoundException(paramAttribute.getName());
/*     */   }
/*     */   
/*     */   public AttributeList getAttributes(String[] paramArrayOfString)
/*     */   {
/*  63 */     return new AttributeList();
/*     */   }
/*     */   
/*     */   public AttributeList setAttributes(AttributeList paramAttributeList)
/*     */   {
/*  68 */     return new AttributeList();
/*     */   }
/*     */   
/*     */   private class Wrapper
/*     */   {
/*     */     String name;
/*     */     String cmd;
/*     */     DiagnosticCommandInfo info;
/*     */     Permission permission;
/*     */     
/*     */     Wrapper(String paramString1, String paramString2, DiagnosticCommandInfo paramDiagnosticCommandInfo) throws InstantiationException
/*     */     {
/*  80 */       this.name = paramString1;
/*  81 */       this.cmd = paramString2;
/*  82 */       this.info = paramDiagnosticCommandInfo;
/*  83 */       this.permission = null;
/*  84 */       Object localObject = null;
/*  85 */       if (paramDiagnosticCommandInfo.getPermissionClass() != null) {
/*     */         try {
/*  87 */           Class localClass = Class.forName(paramDiagnosticCommandInfo.getPermissionClass());
/*  88 */           if (paramDiagnosticCommandInfo.getPermissionAction() == null) {
/*     */             try {
/*  90 */               Constructor localConstructor1 = localClass.getConstructor(new Class[] { String.class });
/*  91 */               this.permission = ((Permission)localConstructor1.newInstance(new Object[] { paramDiagnosticCommandInfo.getPermissionName() }));
/*     */ 
/*     */             }
/*     */             catch (InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException localInstantiationException2)
/*     */             {
/*  96 */               localObject = localInstantiationException2;
/*     */             }
/*     */           }
/*  99 */           if (this.permission == null) {
/*     */             try {
/* 101 */               Constructor localConstructor2 = localClass.getConstructor(new Class[] { String.class, String.class });
/* 102 */               this.permission = ((Permission)localConstructor2.newInstance(new Object[] {paramDiagnosticCommandInfo
/* 103 */                 .getPermissionName(), paramDiagnosticCommandInfo
/* 104 */                 .getPermissionAction() }));
/*     */             }
/*     */             catch (InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException localInstantiationException3)
/*     */             {
/* 108 */               localObject = localInstantiationException3;
/*     */             }
/*     */           }
/*     */         } catch (ClassNotFoundException localClassNotFoundException) {}
/* 112 */         if (this.permission == null) {
/* 113 */           InstantiationException localInstantiationException1 = new InstantiationException("Unable to instantiate required permission");
/*     */           
/* 115 */           localInstantiationException1.initCause((Throwable)localObject);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public String execute(String[] paramArrayOfString) {
/* 121 */       if (this.permission != null) {
/* 122 */         localObject = System.getSecurityManager();
/* 123 */         if (localObject != null) {
/* 124 */           ((SecurityManager)localObject).checkPermission(this.permission);
/*     */         }
/*     */       }
/* 127 */       if (paramArrayOfString == null) {
/* 128 */         return DiagnosticCommandImpl.this.executeDiagnosticCommand(this.cmd);
/*     */       }
/* 130 */       Object localObject = new StringBuilder();
/* 131 */       ((StringBuilder)localObject).append(this.cmd);
/* 132 */       for (int i = 0; i < paramArrayOfString.length; i++) {
/* 133 */         if (paramArrayOfString[i] == null) {
/* 134 */           throw new IllegalArgumentException("Invalid null argument");
/*     */         }
/* 136 */         ((StringBuilder)localObject).append(" ");
/* 137 */         ((StringBuilder)localObject).append(paramArrayOfString[i]);
/*     */       }
/* 139 */       return DiagnosticCommandImpl.this.executeDiagnosticCommand(((StringBuilder)localObject).toString());
/*     */     }
/*     */   }
/*     */   
/*     */   DiagnosticCommandImpl(VMManagement paramVMManagement)
/*     */   {
/* 145 */     this.jvm = paramVMManagement;
/* 146 */     this.isSupported = paramVMManagement.isRemoteDiagnosticCommandsSupported();
/*     */   }
/*     */   
/*     */   private static class OperationInfoComparator implements Comparator<MBeanOperationInfo>
/*     */   {
/*     */     public int compare(MBeanOperationInfo paramMBeanOperationInfo1, MBeanOperationInfo paramMBeanOperationInfo2) {
/* 152 */       return paramMBeanOperationInfo1.getName().compareTo(paramMBeanOperationInfo2.getName());
/*     */     }
/*     */   }
/*     */   
/*     */   public MBeanInfo getMBeanInfo()
/*     */   {
/* 158 */     TreeSet localTreeSet = new TreeSet(new OperationInfoComparator(null));
/*     */     Object localObject1;
/* 160 */     if (!this.isSupported) {
/* 161 */       localObject1 = Collections.EMPTY_MAP;
/*     */     } else {
/*     */       try {
/* 164 */         String[] arrayOfString = getDiagnosticCommands();
/* 165 */         localObject2 = getDiagnosticCommandInfo(arrayOfString);
/* 166 */         MBeanParameterInfo[] arrayOfMBeanParameterInfo = { new MBeanParameterInfo("arguments", strArrayClassName, "Array of Diagnostic Commands Arguments and Options") };
/*     */         
/*     */ 
/*     */ 
/* 170 */         localObject1 = new HashMap();
/* 171 */         for (int i = 0; i < arrayOfString.length; i++) {
/* 172 */           String str = transform(arrayOfString[i]);
/*     */           try {
/* 174 */             Wrapper localWrapper = new Wrapper(str, arrayOfString[i], localObject2[i]);
/* 175 */             ((Map)localObject1).put(str, localWrapper);
/* 176 */             localTreeSet.add(new MBeanOperationInfo(localWrapper.name, localWrapper.info
/*     */             
/* 178 */               .getDescription(), 
/* 179 */               (localWrapper.info.getArgumentsInfo() == null) || 
/* 180 */               (localWrapper.info.getArgumentsInfo().isEmpty()) ? null : arrayOfMBeanParameterInfo, strClassName, 2, 
/*     */               
/*     */ 
/*     */ 
/* 184 */               commandDescriptor(localWrapper)));
/*     */ 
/*     */           }
/*     */           catch (InstantiationException localInstantiationException) {}
/*     */         }
/*     */       }
/*     */       catch (IllegalArgumentException|UnsupportedOperationException localIllegalArgumentException)
/*     */       {
/* 192 */         localObject1 = Collections.EMPTY_MAP;
/*     */       }
/*     */     }
/* 195 */     this.wrappers = Collections.unmodifiableMap((Map)localObject1);
/* 196 */     HashMap localHashMap = new HashMap();
/* 197 */     localHashMap.put("immutableInfo", "false");
/* 198 */     localHashMap.put("interfaceClassName", "com.sun.management.DiagnosticCommandMBean");
/* 199 */     localHashMap.put("mxbean", "false");
/* 200 */     Object localObject2 = new ImmutableDescriptor(localHashMap);
/* 201 */     return new MBeanInfo(
/* 202 */       getClass().getName(), "Diagnostic Commands", null, null, 
/*     */       
/*     */ 
/*     */ 
/* 206 */       (MBeanOperationInfo[])localTreeSet.toArray(new MBeanOperationInfo[localTreeSet.size()]), 
/* 207 */       getNotificationInfo(), (Descriptor)localObject2);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*     */     throws MBeanException, ReflectionException
/*     */   {
/* 214 */     if (!this.isSupported) {
/* 215 */       throw new UnsupportedOperationException();
/*     */     }
/* 217 */     if (this.wrappers == null) {
/* 218 */       getMBeanInfo();
/*     */     }
/* 220 */     Wrapper localWrapper = (Wrapper)this.wrappers.get(paramString);
/* 221 */     if (localWrapper != null) {
/* 222 */       if ((localWrapper.info.getArgumentsInfo().isEmpty()) && ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0)) && ((paramArrayOfString == null) || (paramArrayOfString.length == 0)))
/*     */       {
/*     */ 
/* 225 */         return localWrapper.execute(null); }
/* 226 */       if ((paramArrayOfObject != null) && (paramArrayOfObject.length == 1) && (paramArrayOfString != null) && (paramArrayOfString.length == 1) && (paramArrayOfString[0] != null))
/*     */       {
/*     */ 
/* 229 */         if (paramArrayOfString[0].compareTo(strArrayClassName) == 0)
/* 230 */           return localWrapper.execute((String[])paramArrayOfObject[0]);
/*     */       }
/*     */     }
/* 233 */     throw new ReflectionException(new NoSuchMethodException(paramString));
/*     */   }
/*     */   
/*     */   private static String transform(String paramString) {
/* 237 */     StringBuilder localStringBuilder = new StringBuilder();
/* 238 */     int i = 1;
/* 239 */     int j = 0;
/* 240 */     for (int k = 0; k < paramString.length(); k++) {
/* 241 */       char c = paramString.charAt(k);
/* 242 */       if ((c == '.') || (c == '_')) {
/* 243 */         i = 0;
/* 244 */         j = 1;
/*     */       }
/* 246 */       else if (j != 0) {
/* 247 */         j = 0;
/* 248 */         localStringBuilder.append(Character.toUpperCase(c));
/* 249 */       } else if (i != 0) {
/* 250 */         localStringBuilder.append(Character.toLowerCase(c));
/*     */       } else {
/* 252 */         localStringBuilder.append(c);
/*     */       }
/*     */     }
/*     */     
/* 256 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */   private Descriptor commandDescriptor(Wrapper paramWrapper) throws IllegalArgumentException {
/* 260 */     HashMap localHashMap1 = new HashMap();
/* 261 */     localHashMap1.put("dcmd.name", paramWrapper.info.getName());
/* 262 */     localHashMap1.put("dcmd.description", paramWrapper.info.getDescription());
/* 263 */     localHashMap1.put("dcmd.vmImpact", paramWrapper.info.getImpact());
/* 264 */     localHashMap1.put("dcmd.permissionClass", paramWrapper.info.getPermissionClass());
/* 265 */     localHashMap1.put("dcmd.permissionName", paramWrapper.info.getPermissionName());
/* 266 */     localHashMap1.put("dcmd.permissionAction", paramWrapper.info.getPermissionAction());
/* 267 */     localHashMap1.put("dcmd.enabled", Boolean.valueOf(paramWrapper.info.isEnabled()));
/* 268 */     StringBuilder localStringBuilder = new StringBuilder();
/* 269 */     localStringBuilder.append("help ");
/* 270 */     localStringBuilder.append(paramWrapper.info.getName());
/* 271 */     localHashMap1.put("dcmd.help", executeDiagnosticCommand(localStringBuilder.toString()));
/* 272 */     if ((paramWrapper.info.getArgumentsInfo() != null) && (!paramWrapper.info.getArgumentsInfo().isEmpty())) {
/* 273 */       HashMap localHashMap2 = new HashMap();
/* 274 */       for (DiagnosticCommandArgumentInfo localDiagnosticCommandArgumentInfo : paramWrapper.info.getArgumentsInfo()) {
/* 275 */         HashMap localHashMap3 = new HashMap();
/* 276 */         localHashMap3.put("dcmd.arg.name", localDiagnosticCommandArgumentInfo.getName());
/* 277 */         localHashMap3.put("dcmd.arg.type", localDiagnosticCommandArgumentInfo.getType());
/* 278 */         localHashMap3.put("dcmd.arg.description", localDiagnosticCommandArgumentInfo.getDescription());
/* 279 */         localHashMap3.put("dcmd.arg.isMandatory", Boolean.valueOf(localDiagnosticCommandArgumentInfo.isMandatory()));
/* 280 */         localHashMap3.put("dcmd.arg.isMultiple", Boolean.valueOf(localDiagnosticCommandArgumentInfo.isMultiple()));
/* 281 */         boolean bool = localDiagnosticCommandArgumentInfo.isOption();
/* 282 */         localHashMap3.put("dcmd.arg.isOption", Boolean.valueOf(bool));
/* 283 */         if (!bool) {
/* 284 */           localHashMap3.put("dcmd.arg.position", Integer.valueOf(localDiagnosticCommandArgumentInfo.getPosition()));
/*     */         } else {
/* 286 */           localHashMap3.put("dcmd.arg.position", Integer.valueOf(-1));
/*     */         }
/* 288 */         localHashMap2.put(localDiagnosticCommandArgumentInfo.getName(), new ImmutableDescriptor(localHashMap3));
/*     */       }
/* 290 */       localHashMap1.put("dcmd.arguments", new ImmutableDescriptor(localHashMap2));
/*     */     }
/* 292 */     return new ImmutableDescriptor(localHashMap1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 298 */   private static final String[] diagFramNotifTypes = { "jmx.mbean.info.changed" };
/*     */   
/*     */ 
/*     */ 
/* 302 */   private MBeanNotificationInfo[] notifInfo = null;
/*     */   
/*     */   public MBeanNotificationInfo[] getNotificationInfo()
/*     */   {
/* 306 */     synchronized (this) {
/* 307 */       if (this.notifInfo == null) {
/* 308 */         this.notifInfo = new MBeanNotificationInfo[1];
/* 309 */         this.notifInfo[0] = new MBeanNotificationInfo(diagFramNotifTypes, "javax.management.Notification", "Diagnostic Framework Notification");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 315 */     return (MBeanNotificationInfo[])this.notifInfo.clone();
/*     */   }
/*     */   
/* 318 */   private static long seqNumber = 0L;
/*     */   
/* 320 */   private static long getNextSeqNumber() { return ++seqNumber; }
/*     */   
/*     */ 
/*     */   private void createDiagnosticFrameworkNotification()
/*     */   {
/* 325 */     if (!hasListeners()) {
/* 326 */       return;
/*     */     }
/* 328 */     ObjectName localObjectName = null;
/*     */     try {
/* 330 */       localObjectName = ObjectName.getInstance("com.sun.management:type=DiagnosticCommand");
/*     */     }
/*     */     catch (MalformedObjectNameException localMalformedObjectNameException) {}
/*     */     
/* 334 */     Notification localNotification = new Notification("jmx.mbean.info.changed", localObjectName, getNextSeqNumber());
/* 335 */     localNotification.setUserData(getMBeanInfo());
/* 336 */     sendNotification(localNotification);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*     */   {
/* 343 */     boolean bool1 = hasListeners();
/* 344 */     super.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
/* 345 */     boolean bool2 = hasListeners();
/* 346 */     if ((!bool1) && (bool2)) {
/* 347 */       setNotificationEnabled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void removeNotificationListener(NotificationListener paramNotificationListener)
/*     */     throws ListenerNotFoundException
/*     */   {
/* 354 */     boolean bool1 = hasListeners();
/* 355 */     super.removeNotificationListener(paramNotificationListener);
/* 356 */     boolean bool2 = hasListeners();
/* 357 */     if ((bool1) && (!bool2)) {
/* 358 */       setNotificationEnabled(false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*     */     throws ListenerNotFoundException
/*     */   {
/* 367 */     boolean bool1 = hasListeners();
/* 368 */     super.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
/* 369 */     boolean bool2 = hasListeners();
/* 370 */     if ((bool1) && (!bool2)) {
/* 371 */       setNotificationEnabled(false);
/*     */     }
/*     */   }
/*     */   
/*     */   private native void setNotificationEnabled(boolean paramBoolean);
/*     */   
/*     */   private native String[] getDiagnosticCommands();
/*     */   
/*     */   private native DiagnosticCommandInfo[] getDiagnosticCommandInfo(String[] paramArrayOfString);
/*     */   
/*     */   private native String executeDiagnosticCommand(String paramString);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\DiagnosticCommandImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */