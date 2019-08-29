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
/*     */ public class Resources_es
/*     */   extends ListResourceBundle
/*     */ {
/*  35 */   private static final Object[][] contents = { { "invalid.null.input.s.", "entradas nulas no válidas" }, { "actions.can.only.be.read.", "las acciones sólo pueden 'leerse'" }, { "permission.name.name.syntax.invalid.", "sintaxis de nombre de permiso [{0}] no válida: " }, { "Credential.Class.not.followed.by.a.Principal.Class.and.Name", "La clase de credencial no va seguida de una clase y nombre de principal" }, { "Principal.Class.not.followed.by.a.Principal.Name", "La clase de principal no va seguida de un nombre de principal" }, { "Principal.Name.must.be.surrounded.by.quotes", "El nombre de principal debe ir entre comillas" }, { "Principal.Name.missing.end.quote", "Faltan las comillas finales en el nombre de principal" }, { "PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value", "La clase de principal PrivateCredentialPermission no puede ser un valor comodín (*) si el nombre de principal no lo es también" }, { "CredOwner.Principal.Class.class.Principal.Name.name", "CredOwner:\n\tClase de Principal = {0}\n\tNombre de Principal = {1}" }, { "provided.null.name", "se ha proporcionado un nombre nulo" }, { "provided.null.keyword.map", "mapa de palabras clave proporcionado nulo" }, { "provided.null.OID.map", "mapa de OID proporcionado nulo" }, { "NEWLINE", "\n" }, { "invalid.null.AccessControlContext.provided", "se ha proporcionado un AccessControlContext nulo no válido" }, { "invalid.null.action.provided", "se ha proporcionado una acción nula no válida" }, { "invalid.null.Class.provided", "se ha proporcionado una clase nula no válida" }, { "Subject.", "Asunto:\n" }, { ".Principal.", "\tPrincipal: " }, { ".Public.Credential.", "\tCredencial Pública: " }, { ".Private.Credentials.inaccessible.", "\tCredenciales Privadas Inaccesibles\n" }, { ".Private.Credential.", "\tCredencial Privada: " }, { ".Private.Credential.inaccessible.", "\tCredencial Privada Inaccesible\n" }, { "Subject.is.read.only", "El asunto es de sólo lectura" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set", "intentando agregar un objeto que no es una instancia de java.security.Principal al juego principal de un asunto" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.class", "intentando agregar un objeto que no es una instancia de {0}" }, { "LoginModuleControlFlag.", "LoginModuleControlFlag: " }, { "Invalid.null.input.name", "Entrada nula no válida: nombre" }, { "No.LoginModules.configured.for.name", "No se han configurado LoginModules para {0}" }, { "invalid.null.Subject.provided", "se ha proporcionado un asunto nulo no válido" }, { "invalid.null.CallbackHandler.provided", "se ha proporcionado CallbackHandler nulo no válido" }, { "null.subject.logout.called.before.login", "asunto nulo - se ha llamado al cierre de sesión antes del inicio de sesión" }, { "unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor", "no se ha podido instanciar LoginModule, {0}, porque no incluye un constructor sin argumentos" }, { "unable.to.instantiate.LoginModule", "no se ha podido instanciar LoginModule" }, { "unable.to.instantiate.LoginModule.", "no se ha podido instanciar LoginModule: " }, { "unable.to.find.LoginModule.class.", "no se ha encontrado la clase LoginModule: " }, { "unable.to.access.LoginModule.", "no se ha podido acceder a LoginModule: " }, { "Login.Failure.all.modules.ignored", "Fallo en inicio de sesión: se han ignorado todos los módulos" }, { "java.security.policy.error.parsing.policy.message", "java.security.policy: error de análisis de {0}:\n\t{1}" }, { "java.security.policy.error.adding.Permission.perm.message", "java.security.policy: error al agregar un permiso, {0}:\n\t{1}" }, { "java.security.policy.error.adding.Entry.message", "java.security.policy: error al agregar una entrada:\n\t{0}" }, { "alias.name.not.provided.pe.name.", "no se ha proporcionado el nombre de alias ({0})" }, { "unable.to.perform.substitution.on.alias.suffix", "no se puede realizar la sustitución en el alias, {0}" }, { "substitution.value.prefix.unsupported", "valor de sustitución, {0}, no soportado" }, { "LPARAM", "(" }, { "RPARAM", ")" }, { "type.can.t.be.null", "el tipo no puede ser nulo" }, { "keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore", "keystorePasswordURL no puede especificarse sin especificar también el almacén de claves" }, { "expected.keystore.type", "se esperaba un tipo de almacén de claves" }, { "expected.keystore.provider", "se esperaba un proveedor de almacén de claves" }, { "multiple.Codebase.expressions", "expresiones múltiples de CodeBase" }, { "multiple.SignedBy.expressions", "expresiones múltiples de SignedBy" }, { "duplicate.keystore.domain.name", "nombre de dominio de almacén de claves duplicado: {0}" }, { "duplicate.keystore.name", "nombre de almacén de claves duplicado: {0}" }, { "SignedBy.has.empty.alias", "SignedBy tiene un alias vacío" }, { "can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name", "no se puede especificar Principal con una clase de comodín sin un nombre de comodín" }, { "expected.codeBase.or.SignedBy.or.Principal", "se esperaba codeBase o SignedBy o Principal" }, { "expected.permission.entry", "se esperaba una entrada de permiso" }, { "number.", "número " }, { "expected.expect.read.end.of.file.", "se esperaba [{0}], se ha leído [final de archivo]" }, { "expected.read.end.of.file.", "se esperaba [;], se ha leído [final de archivo]" }, { "line.number.msg", "línea {0}: {1}" }, { "line.number.expected.expect.found.actual.", "línea {0}: se esperaba [{1}], se ha encontrado [{2}]" }, { "null.principalClass.or.principalName", "principalClass o principalName nulos" }, { "PKCS11.Token.providerName.Password.", "Contraseña del Token PKCS11 [{0}]: " }, { "unable.to.instantiate.Subject.based.policy", "no se ha podido instanciar una política basada en asunto" } };
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


/* Location:              E:\java_source\rt.jar!\sun\security\util\Resources_es.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */