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
/*     */ public class Resources_fr
/*     */   extends ListResourceBundle
/*     */ {
/*  35 */   private static final Object[][] contents = { { "invalid.null.input.s.", "entrées NULL non valides" }, { "actions.can.only.be.read.", "les actions sont accessibles en lecture uniquement" }, { "permission.name.name.syntax.invalid.", "syntaxe de nom de droit [{0}] non valide : " }, { "Credential.Class.not.followed.by.a.Principal.Class.and.Name", "Classe Credential non suivie d'une classe et d'un nom de principal" }, { "Principal.Class.not.followed.by.a.Principal.Name", "Classe de principal non suivie d'un nom de principal" }, { "Principal.Name.must.be.surrounded.by.quotes", "Le nom de principal doit être indiqué entre guillemets" }, { "Principal.Name.missing.end.quote", "Guillemet fermant manquant pour le nom de principal" }, { "PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value", "La classe de principal PrivateCredentialPermission ne peut pas être une valeur générique (*) si le nom de principal n'est pas une valeur générique (*)" }, { "CredOwner.Principal.Class.class.Principal.Name.name", "CredOwner :\n\tClasse de principal = {0}\n\tNom de principal = {1}" }, { "provided.null.name", "nom NULL fourni" }, { "provided.null.keyword.map", "mappage de mots-clés NULL fourni" }, { "provided.null.OID.map", "mappage OID NULL fourni" }, { "NEWLINE", "\n" }, { "invalid.null.AccessControlContext.provided", "AccessControlContext NULL fourni non valide" }, { "invalid.null.action.provided", "action NULL fournie non valide" }, { "invalid.null.Class.provided", "classe NULL fournie non valide" }, { "Subject.", "Objet :\n" }, { ".Principal.", "\tPrincipal : " }, { ".Public.Credential.", "\tInformations d'identification publiques : " }, { ".Private.Credentials.inaccessible.", "\tInformations d'identification privées inaccessibles\n" }, { ".Private.Credential.", "\tInformations d'identification privées : " }, { ".Private.Credential.inaccessible.", "\tInformations d'identification privées inaccessibles\n" }, { "Subject.is.read.only", "Sujet en lecture seule" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set", "tentative d'ajout d'un objet qui n'est pas une instance de java.security.Principal dans un ensemble de principaux du sujet" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.class", "tentative d''ajout d''un objet qui n''est pas une instance de {0}" }, { "LoginModuleControlFlag.", "LoginModuleControlFlag : " }, { "Invalid.null.input.name", "Entrée NULL non valide : nom" }, { "No.LoginModules.configured.for.name", "Aucun LoginModule configuré pour {0}" }, { "invalid.null.Subject.provided", "sujet NULL fourni non valide" }, { "invalid.null.CallbackHandler.provided", "CallbackHandler NULL fourni non valide" }, { "null.subject.logout.called.before.login", "sujet NULL - Tentative de déconnexion avant la connexion" }, { "unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor", "impossible d''instancier LoginModule {0} car il ne fournit pas de constructeur sans argument" }, { "unable.to.instantiate.LoginModule", "impossible d'instancier LoginModule" }, { "unable.to.instantiate.LoginModule.", "impossible d'instancier LoginModule : " }, { "unable.to.find.LoginModule.class.", "classe LoginModule introuvable : " }, { "unable.to.access.LoginModule.", "impossible d'accéder à LoginModule : " }, { "Login.Failure.all.modules.ignored", "Echec de connexion : tous les modules ont été ignorés" }, { "java.security.policy.error.parsing.policy.message", "java.security.policy : erreur d''analyse de {0} :\n\t{1}" }, { "java.security.policy.error.adding.Permission.perm.message", "java.security.policy : erreur d''ajout de droit, {0} :\n\t{1}" }, { "java.security.policy.error.adding.Entry.message", "java.security.policy : erreur d''ajout d''entrée :\n\t{0}" }, { "alias.name.not.provided.pe.name.", "nom d''alias non fourni ({0})" }, { "unable.to.perform.substitution.on.alias.suffix", "impossible d''effectuer une substitution pour l''alias, {0}" }, { "substitution.value.prefix.unsupported", "valeur de substitution, {0}, non prise en charge" }, { "LPARAM", "(" }, { "RPARAM", ")" }, { "type.can.t.be.null", "le type ne peut être NULL" }, { "keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore", "Impossible de spécifier keystorePasswordURL sans indiquer aussi le fichier de clés" }, { "expected.keystore.type", "type de fichier de clés attendu" }, { "expected.keystore.provider", "fournisseur de fichier de clés attendu" }, { "multiple.Codebase.expressions", "expressions Codebase multiples" }, { "multiple.SignedBy.expressions", "expressions SignedBy multiples" }, { "duplicate.keystore.domain.name", "nom de domaine de fichier de clés en double : {0}" }, { "duplicate.keystore.name", "nom de fichier de clés en double : {0}" }, { "SignedBy.has.empty.alias", "SignedBy possède un alias vide" }, { "can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name", "impossible de spécifier le principal avec une classe générique sans nom générique" }, { "expected.codeBase.or.SignedBy.or.Principal", "codeBase, SignedBy ou Principal attendu" }, { "expected.permission.entry", "entrée de droit attendue" }, { "number.", "nombre " }, { "expected.expect.read.end.of.file.", "attendu [{0}], lu [fin de fichier]" }, { "expected.read.end.of.file.", "attendu [;], lu [fin de fichier]" }, { "line.number.msg", "ligne {0} : {1}" }, { "line.number.expected.expect.found.actual.", "ligne {0} : attendu [{1}], trouvé [{2}]" }, { "null.principalClass.or.principalName", "principalClass ou principalName NULL" }, { "PKCS11.Token.providerName.Password.", "Mot de passe PKCS11 Token [{0}] : " }, { "unable.to.instantiate.Subject.based.policy", "impossible d'instancier les règles basées sur le sujet" } };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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


/* Location:              E:\java_source\rt.jar!\sun\security\util\Resources_fr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */