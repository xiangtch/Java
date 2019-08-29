/*     */ package sun.security.util;
/*     */ 
/*     */ import java.util.ListResourceBundle;
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
/*     */ public class Resources_zh_CN
/*     */   extends ListResourceBundle
/*     */ {
/*  35 */   private static final Object[][] contents = { { "invalid.null.input.s.", "无效的空输入" }, { "actions.can.only.be.read.", "操作只能为 '读取'" }, { "permission.name.name.syntax.invalid.", "权限名称 [{0}] 语法无效: " }, { "Credential.Class.not.followed.by.a.Principal.Class.and.Name", "身份证明类后面未跟随主用户类及名称" }, { "Principal.Class.not.followed.by.a.Principal.Name", "主用户类后面未跟随主用户名称" }, { "Principal.Name.must.be.surrounded.by.quotes", "主用户名称必须放在引号内" }, { "Principal.Name.missing.end.quote", "主用户名称缺少右引号" }, { "PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value", "如果主用户名称不是通配符 (*) 值, 那么 PrivateCredentialPermission 主用户类不能是通配符 (*) 值" }, { "CredOwner.Principal.Class.class.Principal.Name.name", "CredOwner:\n\t主用户类 = {0}\n\t主用户名称 = {1}" }, { "provided.null.name", "提供的名称为空值" }, { "provided.null.keyword.map", "提供的关键字映射为空值" }, { "provided.null.OID.map", "提供的 OID 映射为空值" }, { "NEWLINE", "\n" }, { "invalid.null.AccessControlContext.provided", "提供了无效的空 AccessControlContext" }, { "invalid.null.action.provided", "提供了无效的空操作" }, { "invalid.null.Class.provided", "提供了无效的空类" }, { "Subject.", "主体: \n" }, { ".Principal.", "\t主用户: " }, { ".Public.Credential.", "\t公共身份证明: " }, { ".Private.Credentials.inaccessible.", "\t无法访问专用身份证明\n" }, { ".Private.Credential.", "\t专用身份证明: " }, { ".Private.Credential.inaccessible.", "\t无法访问专用身份证明\n" }, { "Subject.is.read.only", "主体为只读" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set", "正在尝试将一个非 java.security.Principal 实例的对象添加到主体的主用户集中" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.class", "正在尝试添加一个非{0}实例的对象" }, { "LoginModuleControlFlag.", "LoginModuleControlFlag: " }, { "Invalid.null.input.name", "无效空输入: 名称" }, { "No.LoginModules.configured.for.name", "没有为{0}配置 LoginModules" }, { "invalid.null.Subject.provided", "提供了无效的空主体" }, { "invalid.null.CallbackHandler.provided", "提供了无效的空 CallbackHandler" }, { "null.subject.logout.called.before.login", "空主体 - 在登录之前调用了注销" }, { "unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor", "无法实例化 LoginModule, {0}, 因为它未提供一个无参数构造器" }, { "unable.to.instantiate.LoginModule", "无法实例化 LoginModule" }, { "unable.to.instantiate.LoginModule.", "无法实例化 LoginModule: " }, { "unable.to.find.LoginModule.class.", "无法找到 LoginModule 类: " }, { "unable.to.access.LoginModule.", "无法访问 LoginModule: " }, { "Login.Failure.all.modules.ignored", "登录失败: 忽略所有模块" }, { "java.security.policy.error.parsing.policy.message", "java.security.policy: 解析{0}时出错:\n\t{1}" }, { "java.security.policy.error.adding.Permission.perm.message", "java.security.policy: 添加权限{0}时出错:\n\t{1}" }, { "java.security.policy.error.adding.Entry.message", "java.security.policy: 添加条目时出错:\n\t{0}" }, { "alias.name.not.provided.pe.name.", "未提供别名 ({0})" }, { "unable.to.perform.substitution.on.alias.suffix", "无法在别名 {0} 上执行替代" }, { "substitution.value.prefix.unsupported", "替代值{0}不受支持" }, { "LPARAM", "(" }, { "RPARAM", ")" }, { "type.can.t.be.null", "类型不能为空值" }, { "keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore", "不指定密钥库时无法指定 keystorePasswordURL" }, { "expected.keystore.type", "应为密钥库类型" }, { "expected.keystore.provider", "应为密钥库提供方" }, { "multiple.Codebase.expressions", "多个代码库表达式" }, { "multiple.SignedBy.expressions", "多个 SignedBy 表达式" }, { "duplicate.keystore.domain.name", "密钥库域名重复: {0}" }, { "duplicate.keystore.name", "密钥库名称重复: {0}" }, { "SignedBy.has.empty.alias", "SignedBy 有空别名" }, { "can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name", "没有通配符名称, 无法使用通配符类指定主用户" }, { "expected.codeBase.or.SignedBy.or.Principal", "应为 codeBase, SignedBy 或主用户" }, { "expected.permission.entry", "应为权限条目" }, { "number.", "编号 " }, { "expected.expect.read.end.of.file.", "应为 [{0}], 读取的是 [文件结尾]" }, { "expected.read.end.of.file.", "应为 [;], 读取的是 [文件结尾]" }, { "line.number.msg", "列{0}: {1}" }, { "line.number.expected.expect.found.actual.", "行号 {0}: 应为 [{1}], 找到 [{2}]" }, { "null.principalClass.or.principalName", "principalClass 或 principalName 为空值" }, { "PKCS11.Token.providerName.Password.", "PKCS11 标记 [{0}] 口令: " }, { "unable.to.instantiate.Subject.based.policy", "无法实例化基于主题的策略" } };
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
/*     */   public Object[][] getContents()
/*     */   {
/* 169 */     return contents;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\Resources_zh_CN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */