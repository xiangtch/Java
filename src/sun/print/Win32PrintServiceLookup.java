/*     */ package sun.print;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import javax.print.DocFlavor;
/*     */ import javax.print.MultiDocPrintService;
/*     */ import javax.print.PrintService;
/*     */ import javax.print.PrintServiceLookup;
/*     */ import javax.print.attribute.Attribute;
/*     */ import javax.print.attribute.AttributeSet;
/*     */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*     */ import javax.print.attribute.HashPrintServiceAttributeSet;
/*     */ import javax.print.attribute.PrintRequestAttribute;
/*     */ import javax.print.attribute.PrintRequestAttributeSet;
/*     */ import javax.print.attribute.PrintServiceAttribute;
/*     */ import javax.print.attribute.PrintServiceAttributeSet;
/*     */ import javax.print.attribute.standard.PrinterName;
/*     */ import sun.security.action.GetPropertyAction;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Win32PrintServiceLookup
/*     */   extends PrintServiceLookup
/*     */ {
/*     */   private String defaultPrinter;
/*     */   private PrintService defaultPrintService;
/*     */   private String[] printers;
/*     */   private PrintService[] printServices;
/*     */   private static Win32PrintServiceLookup win32PrintLUS;
/*     */   
/*     */   static
/*     */   {
/*  58 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/*  61 */         System.loadLibrary("awt");
/*  62 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Win32PrintServiceLookup getWin32PrintLUS()
/*     */   {
/*  77 */     if (win32PrintLUS == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  82 */       PrintServiceLookup.lookupDefaultPrintService();
/*     */     }
/*  84 */     return win32PrintLUS;
/*     */   }
/*     */   
/*     */   public Win32PrintServiceLookup()
/*     */   {
/*  89 */     if (win32PrintLUS == null) {
/*  90 */       win32PrintLUS = this;
/*     */       
/*  92 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
/*     */       
/*     */ 
/*     */ 
/*  96 */       if ((str != null) && (str.startsWith("Windows 98"))) {
/*  97 */         return;
/*     */       }
/*     */       
/* 100 */       PrinterChangeListener localPrinterChangeListener = new PrinterChangeListener();
/* 101 */       localPrinterChangeListener.setDaemon(true);
/* 102 */       localPrinterChangeListener.start();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized PrintService[] getPrintServices()
/*     */   {
/* 112 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 113 */     if (localSecurityManager != null) {
/* 114 */       localSecurityManager.checkPrintJobAccess();
/*     */     }
/* 116 */     if (this.printServices == null) {
/* 117 */       refreshServices();
/*     */     }
/* 119 */     return this.printServices;
/*     */   }
/*     */   
/*     */   private synchronized void refreshServices() {
/* 123 */     this.printers = getAllPrinterNames();
/* 124 */     if (this.printers == null)
/*     */     {
/*     */ 
/* 127 */       this.printServices = new PrintService[0];
/* 128 */       return;
/*     */     }
/*     */     
/* 131 */     PrintService[] arrayOfPrintService = new PrintService[this.printers.length];
/* 132 */     PrintService localPrintService = getDefaultPrintService();
/* 133 */     for (int i = 0; i < this.printers.length; i++) {
/* 134 */       if ((localPrintService != null) && 
/* 135 */         (this.printers[i].equals(localPrintService.getName()))) {
/* 136 */         arrayOfPrintService[i] = localPrintService;
/*     */       }
/* 138 */       else if (this.printServices == null) {
/* 139 */         arrayOfPrintService[i] = new Win32PrintService(this.printers[i]);
/*     */       }
/*     */       else {
/* 142 */         for (int j = 0; j < this.printServices.length; j++) {
/* 143 */           if ((this.printServices[j] != null) && 
/* 144 */             (this.printers[i].equals(this.printServices[j].getName()))) {
/* 145 */             arrayOfPrintService[i] = this.printServices[j];
/* 146 */             this.printServices[j] = null;
/* 147 */             break;
/*     */           }
/*     */         }
/* 150 */         if (j == this.printServices.length) {
/* 151 */           arrayOfPrintService[i] = new Win32PrintService(this.printers[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 158 */     if (this.printServices != null) {
/* 159 */       for (i = 0; i < this.printServices.length; i++) {
/* 160 */         if (((this.printServices[i] instanceof Win32PrintService)) && 
/* 161 */           (!this.printServices[i].equals(this.defaultPrintService))) {
/* 162 */           ((Win32PrintService)this.printServices[i]).invalidateService();
/*     */         }
/*     */       }
/*     */     }
/* 166 */     this.printServices = arrayOfPrintService;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized PrintService getPrintServiceByName(String paramString)
/*     */   {
/* 172 */     if ((paramString == null) || (paramString.equals(""))) {
/* 173 */       return null;
/*     */     }
/*     */     
/* 176 */     PrintService[] arrayOfPrintService = getPrintServices();
/* 177 */     for (int i = 0; i < arrayOfPrintService.length; i++) {
/* 178 */       if (arrayOfPrintService[i].getName().equals(paramString)) {
/* 179 */         return arrayOfPrintService[i];
/*     */       }
/*     */     }
/* 182 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   boolean matchingService(PrintService paramPrintService, PrintServiceAttributeSet paramPrintServiceAttributeSet)
/*     */   {
/* 188 */     if (paramPrintServiceAttributeSet != null) {
/* 189 */       Attribute[] arrayOfAttribute = paramPrintServiceAttributeSet.toArray();
/*     */       
/* 191 */       for (int i = 0; i < arrayOfAttribute.length; i++)
/*     */       {
/* 193 */         PrintServiceAttribute localPrintServiceAttribute = paramPrintService.getAttribute(arrayOfAttribute[i].getCategory());
/* 194 */         if ((localPrintServiceAttribute == null) || (!localPrintServiceAttribute.equals(arrayOfAttribute[i]))) {
/* 195 */           return false;
/*     */         }
/*     */       }
/*     */     }
/* 199 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public PrintService[] getPrintServices(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
/*     */   {
/* 205 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 206 */     if (localSecurityManager != null) {
/* 207 */       localSecurityManager.checkPrintJobAccess();
/*     */     }
/* 209 */     HashPrintRequestAttributeSet localHashPrintRequestAttributeSet = null;
/* 210 */     HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = null;
/*     */     
/* 212 */     if ((paramAttributeSet != null) && (!paramAttributeSet.isEmpty()))
/*     */     {
/* 214 */       localHashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
/* 215 */       localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
/*     */       
/* 217 */       localObject1 = paramAttributeSet.toArray();
/* 218 */       for (int i = 0; i < localObject1.length; i++) {
/* 219 */         if ((localObject1[i] instanceof PrintRequestAttribute)) {
/* 220 */           localHashPrintRequestAttributeSet.add(localObject1[i]);
/* 221 */         } else if ((localObject1[i] instanceof PrintServiceAttribute)) {
/* 222 */           localHashPrintServiceAttributeSet.add(localObject1[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 232 */     Object localObject1 = null;
/* 233 */     if ((localHashPrintServiceAttributeSet != null) && (localHashPrintServiceAttributeSet.get(PrinterName.class) != null)) {
/* 234 */       localObject2 = (PrinterName)localHashPrintServiceAttributeSet.get(PrinterName.class);
/* 235 */       PrintService localPrintService = getPrintServiceByName(((PrinterName)localObject2).getValue());
/* 236 */       if ((localPrintService == null) || (!matchingService(localPrintService, localHashPrintServiceAttributeSet))) {
/* 237 */         localObject1 = new PrintService[0];
/*     */       } else {
/* 239 */         localObject1 = new PrintService[1];
/* 240 */         localObject1[0] = localPrintService;
/*     */       }
/*     */     } else {
/* 243 */       localObject1 = getPrintServices();
/*     */     }
/*     */     
/* 246 */     if (localObject1.length == 0) {
/* 247 */       return (PrintService[])localObject1;
/*     */     }
/* 249 */     Object localObject2 = new ArrayList();
/* 250 */     for (int j = 0; j < localObject1.length; j++) {
/*     */       try
/*     */       {
/* 253 */         if (localObject1[j].getUnsupportedAttributes(paramDocFlavor, localHashPrintRequestAttributeSet) == null) {
/* 254 */           ((ArrayList)localObject2).add(localObject1[j]);
/*     */         }
/*     */       }
/*     */       catch (IllegalArgumentException localIllegalArgumentException) {}
/*     */     }
/* 259 */     localObject1 = new PrintService[((ArrayList)localObject2).size()];
/* 260 */     return (PrintService[])((ArrayList)localObject2).toArray((Object[])localObject1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] paramArrayOfDocFlavor, AttributeSet paramAttributeSet)
/*     */   {
/* 270 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 271 */     if (localSecurityManager != null) {
/* 272 */       localSecurityManager.checkPrintJobAccess();
/*     */     }
/* 274 */     return new MultiDocPrintService[0];
/*     */   }
/*     */   
/*     */   public synchronized PrintService getDefaultPrintService()
/*     */   {
/* 279 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 280 */     if (localSecurityManager != null) {
/* 281 */       localSecurityManager.checkPrintJobAccess();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 287 */     this.defaultPrinter = getDefaultPrinterName();
/* 288 */     if (this.defaultPrinter == null) {
/* 289 */       return null;
/*     */     }
/*     */     
/* 292 */     if ((this.defaultPrintService != null) && 
/* 293 */       (this.defaultPrintService.getName().equals(this.defaultPrinter)))
/*     */     {
/* 295 */       return this.defaultPrintService;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 301 */     this.defaultPrintService = null;
/*     */     
/* 303 */     if (this.printServices != null) {
/* 304 */       for (int i = 0; i < this.printServices.length; i++) {
/* 305 */         if (this.defaultPrinter.equals(this.printServices[i].getName())) {
/* 306 */           this.defaultPrintService = this.printServices[i];
/* 307 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 312 */     if (this.defaultPrintService == null) {
/* 313 */       this.defaultPrintService = new Win32PrintService(this.defaultPrinter);
/*     */     }
/* 315 */     return this.defaultPrintService; }
/*     */   
/*     */   private native String getDefaultPrinterName();
/*     */   
/*     */   private native String[] getAllPrinterNames();
/*     */   
/* 321 */   class PrinterChangeListener extends Thread { PrinterChangeListener() { this.chgObj = Win32PrintServiceLookup.this.notifyFirstPrinterChange(null); }
/*     */     
/*     */     long chgObj;
/*     */     public void run() {
/* 325 */       if (this.chgObj != -1L)
/*     */       {
/*     */         for (;;) {
/* 328 */           if (Win32PrintServiceLookup.this.notifyPrinterChange(this.chgObj) != 0)
/*     */             try {
/* 330 */               Win32PrintServiceLookup.this.refreshServices();
/*     */             } catch (SecurityException localSecurityException) {
/*     */               return;
/*     */             }
/*     */         }
/* 335 */         Win32PrintServiceLookup.this.notifyClosePrinterChange(this.chgObj);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private native long notifyFirstPrinterChange(String paramString);
/*     */   
/*     */   private native void notifyClosePrinterChange(long paramLong);
/*     */   
/*     */   private native int notifyPrinterChange(long paramLong);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\print\Win32PrintServiceLookup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */