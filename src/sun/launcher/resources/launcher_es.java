/*   */ package sun.launcher.resources;
/*   */ 
/*   */ import java.util.ListResourceBundle;
/*   */ 
/*   */ public final class launcher_es extends ListResourceBundle {
/*   */   protected final Object[][] getContents() {
/* 7 */     return new Object[][] { { "java.launcher.X.macosx.usage", "\nLas siguientes opciones son específicas para Mac OS X:\n    -XstartOnFirstThread\n                      ejecuta el método main() del primer thread (AppKit)\n    -Xdock:name=<nombre de aplicación>\"\n                      sustituye al nombre por defecto de la aplicación que se muestra en el Dock\n    -Xdock:icon=<ruta de acceso a archivo de icono>\n                      sustituye al icono por defecto que se muestra en el Dock\n\n" }, { "java.launcher.X.usage", "    -Xmixed           ejecución de modo mixto (por defecto)\n    -Xint             sólo ejecución de modo interpretado\n    -Xbootclasspath:<directorios y archivos zip/jar separados por {0}>\n                      definir la ruta de acceso de búsqueda para los recursos y clases de inicialización de datos\n    -Xbootclasspath/a:<directorios y archivos zip/jar separados por {0}>\n                      agregar al final de la ruta de acceso de la clase de inicialización de datos\n    -Xbootclasspath/p:<directorios y archivos zip/jar separados por {0}>\n                      anteponer a la ruta de acceso de la clase de inicialización de datos\n    -Xdiag            mostrar mensajes de diagnóstico adicionales\n    -Xnoclassgc       desactivar la recolección de basura de clases\n    -Xincgc           activar la recolección de basura de clases\n    -Xloggc:<archivo> registrar el estado de GC en un archivo con registros de hora\n    -Xbatch           desactivar compilación en segundo plano\n    -Xms<tamaño>      definir tamaño de pila Java inicial\n    -Xmx<tamaño>      definir tamaño de pila Java máximo\n    -Xss<tamaño>      definir tamaño de la pila del thread de Java\n    -Xprof            datos de salida de creación de perfil de CPU\n    -Xfuture          activar las comprobaciones más estrictas, anticipándose al futuro valor por defecto\n    -Xrs              reducir el uso de señales de sistema operativo por parte de Java/VM (consulte la documentación)\n    -Xcheck:jni       realizar comprobaciones adicionales para las funciones de JNI\n    -Xshare:off       no intentar usar datos de clase compartidos\n    -Xshare:auto      usar datos de clase compartidos si es posible (valor por defecto)\n    -Xshare:on        es obligatorio el uso de datos de clase compartidos, de lo contrario se emitirá un fallo.\n    -XshowSettings    mostrar todos los valores y continuar\n    -XshowSettings:all\n                      mostrar todos los valores y continuar\n    -XshowSettings:vm mostrar todos los valores de la VM y continuar\n    -XshowSettings:properties\n                      mostrar todos los valores de las propiedades y continuar\n    -XshowSettings:locale\n                      mostrar todos los valores relacionados con la configuración regional y continuar\n\nLas opciones -X no son estándar, por lo que podrían cambiarse sin previo aviso.\n" }, { "java.launcher.cls.error1", "Error: no se ha encontrado o cargado la clase principal {0}" }, { "java.launcher.cls.error2", "Error: el método principal no es {0} en la clase {1}, defina el método principal del siguiente modo:\n   public static void main(String[] args)" }, { "java.launcher.cls.error3", "Error: el método principal debe devolver un valor del tipo void en la clase {0}, \ndefina el método principal del siguiente modo:\n   public static void main(String[] args)" }, { "java.launcher.cls.error4", "Error: no se ha encontrado el método principal en la clase {0}, defina el método principal del siguiente modo:\\n   public static void main(String[] args)\\nde lo contrario, se deberá ampliar una clase de aplicación JavaFX {1}" }, { "java.launcher.cls.error5", "Error: faltan los componentes de JavaFX runtime y son necesarios para ejecutar esta aplicación" }, { "java.launcher.ergo.message1", "                  La VM por defecto es {0}" }, { "java.launcher.ergo.message2", "                  porque la ejecución se está llevando a cabo en una máquina de clase de servidor.\n" }, { "java.launcher.init.error", "error de inicialización" }, { "java.launcher.jar.error1", "Error: se ha producido un error inesperado al intentar abrir el archivo {0}" }, { "java.launcher.jar.error2", "no se ha encontrado el manifiesto en {0}" }, { "java.launcher.jar.error3", "no hay ningún atributo de manifiesto principal en {0}" }, { "java.launcher.javafx.error1", "Error: el método launchApplication de JavaFX tiene una firma que no es correcta.\\nSe debe declarar estático y devolver un valor de tipo nulo" }, { "java.launcher.opt.datamodel", "    -d{0}\t  usar un modelo de datos de {0} bits, si está disponible\n" }, { "java.launcher.opt.footer", "    -cp <ruta de acceso de búsqueda de clases de los directorios y los archivos zip/jar>\n    -classpath <ruta de acceso de búsqueda de clases de los directorios y los archivos zip/jar>\n                  Lista separada por {0} de directorios, archivos JAR\n                  y archivos ZIP para buscar archivos de clase.\n    -D<nombre>=<valor>\n                  definir una propiedad del sistema\n    -verbose:[class|gc|jni]\n                  activar la salida verbose\n    -version      imprimir la versión del producto y salir\n    -version:<valor>\n                  Advertencia: Esta función está en desuso y se eliminará\n                  en una versión futura.\n                  es necesario que se ejecute la versión especificada\n    -showversion  imprimir la versión del producto y continuar\n    -jre-restrict-search | -no-jre-restrict-search\n                  Advertencia: Esta función está en desuso y se eliminará\n                  en una versión futura.\n                  incluir/excluir JRE privados de usuario en la búsqueda de versión\n    -? -help      imprimir este mensaje de ayuda\n    -X            imprimir la ayuda sobre las opciones que no sean estándar\n    -ea[:<nombre paquete>...|:<nombre clase>]\n    -enableassertions[:<nombre paquete>...|:<nombre clase>]\n                  activar afirmaciones con la granularidad especificada\n    -da[:<nombre paquete>...|:<nombre clase>]\n    -disableassertions[:<nombre paquete>...|:<nombre clase>]\n                  desactivar afirmaciones con la granularidad especificada\n    -esa | -enablesystemassertions\n                  activar afirmaciones del sistema\n    -dsa | -disablesystemassertions\n                  desactivar afirmaciones del sistema\n    -agentlib:<nombre bib>[=<opciones>]\n                  cargar la biblioteca de agente nativa <nombre bib>, como -agentlib:hprof\n                  véase también -agentlib:jdwp=help y -agentlib:hprof=help\n    -agentpath:<nombre ruta acceso>[=<opciones>]\n                  cargar biblioteca de agente nativa con el nombre de la ruta de acceso completa\n    -javaagent:<ruta acceso jar>[=<opciones>]\n                  cargar agente de lenguaje de programación Java, véase java.lang.instrument\n    -splash:<ruta acceso imagen>\n                  mostrar una pantalla de presentación con la imagen especificada\nConsulte http://www.oracle.com/technetwork/java/javase/documentation/index.html para obtener más información." }, { "java.launcher.opt.header", "Sintaxis: {0} [-options] class [args...]\n           (para ejecutar una clase)\n   o  {0} [-options] -jar jarfile [args...]\n           (para ejecutar un archivo jar)\ndonde las opciones incluyen:\n" }, { "java.launcher.opt.hotspot", "    {0}\t  es un sinónimo de la VM \"{1}\" [en desuso]\n" }, { "java.launcher.opt.vmselect", "    {0}\t  para seleccionar la VM \"{1}\"\n" } };
/*   */   }
/*   */ }


/* Location:              E:\java_source\rt.jar!\sun\launcher\resources\launcher_es.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */