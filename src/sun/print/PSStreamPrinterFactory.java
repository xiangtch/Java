/*    */ package sun.print;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ import javax.print.DocFlavor;
/*    */ import javax.print.DocFlavor.BYTE_ARRAY;
/*    */ import javax.print.DocFlavor.INPUT_STREAM;
/*    */ import javax.print.DocFlavor.SERVICE_FORMATTED;
/*    */ import javax.print.DocFlavor.URL;
/*    */ import javax.print.StreamPrintService;
/*    */ import javax.print.StreamPrintServiceFactory;
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
/*    */ public class PSStreamPrinterFactory
/*    */   extends StreamPrintServiceFactory
/*    */ {
/*    */   static final String psMimeType = "application/postscript";
/* 38 */   static final DocFlavor[] supportedDocFlavors = { SERVICE_FORMATTED.PAGEABLE, SERVICE_FORMATTED.PRINTABLE, BYTE_ARRAY.GIF, INPUT_STREAM.GIF, URL.GIF, BYTE_ARRAY.JPEG, INPUT_STREAM.JPEG, URL.JPEG, BYTE_ARRAY.PNG, INPUT_STREAM.PNG, URL.PNG };
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
/*    */   public String getOutputFormat()
/*    */   {
/* 53 */     return "application/postscript";
/*    */   }
/*    */   
/*    */   public DocFlavor[] getSupportedDocFlavors() {
/* 57 */     return getFlavors();
/*    */   }
/*    */   
/*    */   static DocFlavor[] getFlavors() {
/* 61 */     DocFlavor[] arrayOfDocFlavor = new DocFlavor[supportedDocFlavors.length];
/* 62 */     System.arraycopy(supportedDocFlavors, 0, arrayOfDocFlavor, 0, arrayOfDocFlavor.length);
/* 63 */     return arrayOfDocFlavor;
/*    */   }
/*    */   
/*    */   public StreamPrintService getPrintService(OutputStream paramOutputStream) {
/* 67 */     return new PSStreamPrintService(paramOutputStream);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\print\PSStreamPrinterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */