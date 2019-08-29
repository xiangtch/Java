/*   */ package sun.launcher.resources;
/*   */ 
/*   */ import java.util.ListResourceBundle;
/*   */ 
/*   */ public final class launcher_zh_CN extends ListResourceBundle {
/*   */   protected final Object[][] getContents() {
/* 7 */     return new Object[][] { { "java.launcher.X.macosx.usage", "\n以下选项为 Mac OS X 特定的选项:\n    -XstartOnFirstThread\n                      在第一个 (AppKit) 线程上运行 main() 方法\n    -Xdock:name=<应用程序名称>\"\n                      覆盖停靠栏中显示的默认应用程序名称\n    -Xdock:icon=<图标文件的路径>\n                      覆盖停靠栏中显示的默认图标\n\n" }, { "java.launcher.X.usage", "    -Xmixed           混合模式执行 (默认)\n    -Xint             仅解释模式执行\n    -Xbootclasspath:<用 {0} 分隔的目录和 zip/jar 文件>\n                      设置搜索路径以引导类和资源\n    -Xbootclasspath/a:<用 {0} 分隔的目录和 zip/jar 文件>\n                      附加在引导类路径末尾\n    -Xbootclasspath/p:<用 {0} 分隔的目录和 zip/jar 文件>\n                      置于引导类路径之前\n    -Xdiag            显示附加诊断消息\n    -Xnoclassgc       禁用类垃圾收集\n    -Xincgc           启用增量垃圾收集\n    -Xloggc:<file>    将 GC 状态记录在文件中 (带时间戳)\n    -Xbatch           禁用后台编译\n    -Xms<size>        设置初始 Java 堆大小\n    -Xmx<size>        设置最大 Java 堆大小\n    -Xss<size>        设置 Java 线程堆栈大小\n    -Xprof            输出 cpu 配置文件数据\n    -Xfuture          启用最严格的检查, 预期将来的默认值\n    -Xrs              减少 Java/VM 对操作系统信号的使用 (请参阅文档)\n    -Xcheck:jni       对 JNI 函数执行其他检查\n    -Xshare:off       不尝试使用共享类数据\n    -Xshare:auto      在可能的情况下使用共享类数据 (默认)\n    -Xshare:on        要求使用共享类数据, 否则将失败。\n    -XshowSettings    显示所有设置并继续\n    -XshowSettings:all\n                      显示所有设置并继续\n    -XshowSettings:vm 显示所有与 vm 相关的设置并继续\n    -XshowSettings:properties\n                      显示所有属性设置并继续\n    -XshowSettings:locale\n                      显示所有与区域设置相关的设置并继续\n\n-X 选项是非标准选项, 如有更改, 恕不另行通知。\n" }, { "java.launcher.cls.error1", "错误: 找不到或无法加载主类 {0}" }, { "java.launcher.cls.error2", "错误: main 方法不是类 {1} 中的{0}, 请将 main 方法定义为:\n   public static void main(String[] args)" }, { "java.launcher.cls.error3", "错误: main 方法必须返回类 {0} 中的空类型值, 请\n将 main 方法定义为:\n   public static void main(String[] args)" }, { "java.launcher.cls.error4", "错误: 在类 {0} 中找不到 main 方法, 请将 main 方法定义为:\n   public static void main(String[] args)\n否则 JavaFX 应用程序类必须扩展{1}" }, { "java.launcher.cls.error5", "错误: 缺少 JavaFX 运行时组件, 需要使用该组件来运行此应用程序" }, { "java.launcher.ergo.message1", "                  默认 VM 是 {0}" }, { "java.launcher.ergo.message2", "                  因为您是在服务器类计算机上运行。\n" }, { "java.launcher.init.error", "初始化错误" }, { "java.launcher.jar.error1", "错误: 尝试打开文件{0}时出现意外错误" }, { "java.launcher.jar.error2", "在{0}中找不到清单" }, { "java.launcher.jar.error3", "{0}中没有主清单属性" }, { "java.launcher.javafx.error1", "错误: JavaFX launchApplication 方法具有错误的签名, 必须\n将方法声明为静态方法并返回空类型的值" }, { "java.launcher.opt.datamodel", "    -d{0}\t  使用 {0} 位数据模型 (如果可用)\n" }, { "java.launcher.opt.footer", "    -cp <目录和 zip/jar 文件的类搜索路径>\n    -classpath <目录和 zip/jar 文件的类搜索路径>\n                  用 {0} 分隔的目录, JAR 档案\n                  和 ZIP 档案列表, 用于搜索类文件。\n    -D<名称>=<值>\n                  设置系统属性\n    -verbose:[class|gc|jni]\n                  启用详细输出\n    -version      输出产品版本并退出\n    -version:<值>\n                  警告: 此功能已过时, 将在\n                  未来发行版中删除。\n                  需要指定的版本才能运行\n    -showversion  输出产品版本并继续\n    -jre-restrict-search | -no-jre-restrict-search\n                  警告: 此功能已过时, 将在\n                  未来发行版中删除。\n                  在版本搜索中包括/排除用户专用 JRE\n    -? -help      输出此帮助消息\n    -X            输出非标准选项的帮助\n    -ea[:<packagename>...|:<classname>]\n    -enableassertions[:<packagename>...|:<classname>]\n                  按指定的粒度启用断言\n    -da[:<packagename>...|:<classname>]\n    -disableassertions[:<packagename>...|:<classname>]\n                  禁用具有指定粒度的断言\n    -esa | -enablesystemassertions\n                  启用系统断言\n    -dsa | -disablesystemassertions\n                  禁用系统断言\n    -agentlib:<libname>[=<选项>]\n                  加载本机代理库 <libname>, 例如 -agentlib:hprof\n                  另请参阅 -agentlib:jdwp=help 和 -agentlib:hprof=help\n    -agentpath:<pathname>[=<选项>]\n                  按完整路径名加载本机代理库\n    -javaagent:<jarpath>[=<选项>]\n                  加载 Java 编程语言代理, 请参阅 java.lang.instrument\n    -splash:<imagepath>\n                  使用指定的图像显示启动屏幕\n有关详细信息, 请参阅 http://www.oracle.com/technetwork/java/javase/documentation/index.html。" }, { "java.launcher.opt.header", "用法: {0} [-options] class [args...]\n           (执行类)\n   或  {0} [-options] -jar jarfile [args...]\n           (执行 jar 文件)\n其中选项包括:\n" }, { "java.launcher.opt.hotspot", "    {0}\t  是 \"{1}\" VM 的同义词 [已过时]\n" }, { "java.launcher.opt.vmselect", "    {0}\t  选择 \"{1}\" VM\n" } };
/*   */   }
/*   */ }


/* Location:              E:\java_source\rt.jar!\sun\launcher\resources\launcher_zh_CN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */