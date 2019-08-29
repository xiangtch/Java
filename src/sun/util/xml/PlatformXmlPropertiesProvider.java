/*     */ package sun.util.xml;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.StringReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.IllegalCharsetNameException;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.util.InvalidPropertiesFormatException;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ import sun.util.spi.XmlPropertiesProvider;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlatformXmlPropertiesProvider
/*     */   extends XmlPropertiesProvider
/*     */ {
/*     */   private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
/*     */   private static final String PROPS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>";
/*     */   private static final String EXTERNAL_XML_VERSION = "1.0";
/*     */   
/*     */   public void load(Properties paramProperties, InputStream paramInputStream)
/*     */     throws IOException, InvalidPropertiesFormatException
/*     */   {
/*  76 */     Document localDocument = null;
/*     */     try {
/*  78 */       localDocument = getLoadingDoc(paramInputStream);
/*     */     } catch (SAXException localSAXException) {
/*  80 */       throw new InvalidPropertiesFormatException(localSAXException);
/*     */     }
/*  82 */     Element localElement = localDocument.getDocumentElement();
/*  83 */     String str = localElement.getAttribute("version");
/*  84 */     if (str.compareTo("1.0") > 0) {
/*  85 */       throw new InvalidPropertiesFormatException("Exported Properties file format version " + str + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK.");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  90 */     importProperties(paramProperties, localElement);
/*     */   }
/*     */   
/*     */   static Document getLoadingDoc(InputStream paramInputStream)
/*     */     throws SAXException, IOException
/*     */   {
/*  96 */     DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/*  97 */     localDocumentBuilderFactory.setIgnoringElementContentWhitespace(true);
/*  98 */     localDocumentBuilderFactory.setValidating(true);
/*  99 */     localDocumentBuilderFactory.setCoalescing(true);
/* 100 */     localDocumentBuilderFactory.setIgnoringComments(true);
/*     */     try {
/* 102 */       DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/* 103 */       localDocumentBuilder.setEntityResolver(new Resolver(null));
/* 104 */       localDocumentBuilder.setErrorHandler(new EH(null));
/* 105 */       InputSource localInputSource = new InputSource(paramInputStream);
/* 106 */       return localDocumentBuilder.parse(localInputSource);
/*     */     } catch (ParserConfigurationException localParserConfigurationException) {
/* 108 */       throw new Error(localParserConfigurationException);
/*     */     }
/*     */   }
/*     */   
/*     */   static void importProperties(Properties paramProperties, Element paramElement) {
/* 113 */     NodeList localNodeList = paramElement.getChildNodes();
/* 114 */     int i = localNodeList.getLength();
/*     */     
/* 116 */     int j = (i > 0) && (localNodeList.item(0).getNodeName().equals("comment")) ? 1 : 0;
/* 117 */     for (int k = j; k < i; k++) {
/* 118 */       Element localElement = (Element)localNodeList.item(k);
/* 119 */       if (localElement.hasAttribute("key")) {
/* 120 */         Node localNode = localElement.getFirstChild();
/* 121 */         String str = localNode == null ? "" : localNode.getNodeValue();
/* 122 */         paramProperties.setProperty(localElement.getAttribute("key"), str);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void store(Properties paramProperties, OutputStream paramOutputStream, String paramString1, String paramString2)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 135 */       Charset.forName(paramString2);
/*     */     } catch (IllegalCharsetNameException|UnsupportedCharsetException localIllegalCharsetNameException) {
/* 137 */       throw new UnsupportedEncodingException(paramString2);
/*     */     }
/* 139 */     DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/* 140 */     DocumentBuilder localDocumentBuilder = null;
/*     */     try {
/* 142 */       localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/*     */     } catch (ParserConfigurationException localParserConfigurationException) {
/* 144 */       if (!$assertionsDisabled) throw new AssertionError();
/*     */     }
/* 146 */     Document localDocument = localDocumentBuilder.newDocument();
/*     */     
/* 148 */     Element localElement1 = (Element)localDocument.appendChild(localDocument.createElement("properties"));
/*     */     
/* 150 */     if (paramString1 != null) {
/* 151 */       Element localElement2 = (Element)localElement1.appendChild(localDocument
/* 152 */         .createElement("comment"));
/* 153 */       localElement2.appendChild(localDocument.createTextNode(paramString1));
/*     */     }
/*     */     
/* 156 */     synchronized (paramProperties) {
/* 157 */       for (Map.Entry localEntry : paramProperties.entrySet()) {
/* 158 */         Object localObject1 = localEntry.getKey();
/* 159 */         Object localObject2 = localEntry.getValue();
/* 160 */         if (((localObject1 instanceof String)) && ((localObject2 instanceof String))) {
/* 161 */           Element localElement3 = (Element)localElement1.appendChild(localDocument
/* 162 */             .createElement("entry"));
/* 163 */           localElement3.setAttribute("key", (String)localObject1);
/* 164 */           localElement3.appendChild(localDocument.createTextNode((String)localObject2));
/*     */         }
/*     */       }
/*     */     }
/* 168 */     emitDocument(localDocument, paramOutputStream, paramString2);
/*     */   }
/*     */   
/*     */   static void emitDocument(Document paramDocument, OutputStream paramOutputStream, String paramString)
/*     */     throws IOException
/*     */   {
/* 174 */     TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
/* 175 */     Transformer localTransformer = null;
/*     */     try {
/* 177 */       localTransformer = localTransformerFactory.newTransformer();
/* 178 */       localTransformer.setOutputProperty("doctype-system", "http://java.sun.com/dtd/properties.dtd");
/* 179 */       localTransformer.setOutputProperty("indent", "yes");
/* 180 */       localTransformer.setOutputProperty("method", "xml");
/* 181 */       localTransformer.setOutputProperty("encoding", paramString);
/*     */     } catch (TransformerConfigurationException localTransformerConfigurationException) {
/* 183 */       if (!$assertionsDisabled) throw new AssertionError();
/*     */     }
/* 185 */     DOMSource localDOMSource = new DOMSource(paramDocument);
/* 186 */     StreamResult localStreamResult = new StreamResult(paramOutputStream);
/*     */     try {
/* 188 */       localTransformer.transform(localDOMSource, localStreamResult);
/*     */     } catch (TransformerException localTransformerException) {
/* 190 */       throw new IOException(localTransformerException);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Resolver implements EntityResolver
/*     */   {
/*     */     public InputSource resolveEntity(String paramString1, String paramString2) throws SAXException
/*     */     {
/* 198 */       if (paramString2.equals("http://java.sun.com/dtd/properties.dtd"))
/*     */       {
/* 200 */         InputSource localInputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>"));
/* 201 */         localInputSource.setSystemId("http://java.sun.com/dtd/properties.dtd");
/* 202 */         return localInputSource;
/*     */       }
/* 204 */       throw new SAXException("Invalid system identifier: " + paramString2);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class EH implements ErrorHandler {
/*     */     public void error(SAXParseException paramSAXParseException) throws SAXException {
/* 210 */       throw paramSAXParseException;
/*     */     }
/*     */     
/* 213 */     public void fatalError(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
/*     */     
/*     */     public void warning(SAXParseException paramSAXParseException) throws SAXException {
/* 216 */       throw paramSAXParseException;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\xml\PlatformXmlPropertiesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */