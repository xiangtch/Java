/*     */ package sun.security.tools.keytool;
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
/*  35 */   private static final Object[][] contents = { { "NEWLINE", "\n" }, { "STAR", "*******************************************" }, { "STARNN", "*******************************************\n\n" }, { ".OPTION.", " [OPTION]..." }, { "Options.", "Opciones:" }, { "Use.keytool.help.for.all.available.commands", "Utilice\"keytool -help\" para todos los comandos disponibles" }, { "Key.and.Certificate.Management.Tool", "Herramienta de Gestión de Certificados y Claves" }, { "Commands.", "Comandos:" }, { "Use.keytool.command.name.help.for.usage.of.command.name", "Utilice \"keytool -command_name -help\" para la sintaxis de nombre_comando" }, { "Generates.a.certificate.request", "Genera una solicitud de certificado" }, { "Changes.an.entry.s.alias", "Cambia un alias de entrada" }, { "Deletes.an.entry", "Suprime una entrada" }, { "Exports.certificate", "Exporta el certificado" }, { "Generates.a.key.pair", "Genera un par de claves" }, { "Generates.a.secret.key", "Genera un clave secreta" }, { "Generates.certificate.from.a.certificate.request", "Genera un certificado a partir de una solicitud de certificado" }, { "Generates.CRL", "Genera CRL" }, { "Generated.keyAlgName.secret.key", "Clave secreta {0} generada" }, { "Generated.keysize.bit.keyAlgName.secret.key", "Clave secreta {1} de {0} bits generada" }, { "Imports.entries.from.a.JDK.1.1.x.style.identity.database", "Importa entradas desde una base de datos de identidades JDK 1.1.x-style" }, { "Imports.a.certificate.or.a.certificate.chain", "Importa un certificado o una cadena de certificados" }, { "Imports.a.password", "Importa una contraseña" }, { "Imports.one.or.all.entries.from.another.keystore", "Importa una o todas las entradas desde otro almacén de claves" }, { "Clones.a.key.entry", "Clona una entrada de clave" }, { "Changes.the.key.password.of.an.entry", "Cambia la contraseña de clave de una entrada" }, { "Lists.entries.in.a.keystore", "Enumera las entradas de un almacén de claves" }, { "Prints.the.content.of.a.certificate", "Imprime el contenido de un certificado" }, { "Prints.the.content.of.a.certificate.request", "Imprime el contenido de una solicitud de certificado" }, { "Prints.the.content.of.a.CRL.file", "Imprime el contenido de un archivo CRL" }, { "Generates.a.self.signed.certificate", "Genera un certificado autofirmado" }, { "Changes.the.store.password.of.a.keystore", "Cambia la contraseña de almacén de un almacén de claves" }, { "alias.name.of.the.entry.to.process", "nombre de alias de la entrada que se va a procesar" }, { "destination.alias", "alias de destino" }, { "destination.key.password", "contraseña de clave de destino" }, { "destination.keystore.name", "nombre de almacén de claves de destino" }, { "destination.keystore.password.protected", "almacén de claves de destino protegido por contraseña" }, { "destination.keystore.provider.name", "nombre de proveedor de almacén de claves de destino" }, { "destination.keystore.password", "contraseña de almacén de claves de destino" }, { "destination.keystore.type", "tipo de almacén de claves de destino" }, { "distinguished.name", "nombre distintivo" }, { "X.509.extension", "extensión X.509" }, { "output.file.name", "nombre de archivo de salida" }, { "input.file.name", "nombre de archivo de entrada" }, { "key.algorithm.name", "nombre de algoritmo de clave" }, { "key.password", "contraseña de clave" }, { "key.bit.size", "tamaño de bit de clave" }, { "keystore.name", "nombre de almacén de claves" }, { "new.password", "nueva contraseña" }, { "do.not.prompt", "no solicitar" }, { "password.through.protected.mechanism", "contraseña a través de mecanismo protegido" }, { "provider.argument", "argumento del proveedor" }, { "provider.class.name", "nombre de clase del proveedor" }, { "provider.name", "nombre del proveedor" }, { "provider.classpath", "classpath de proveedor" }, { "output.in.RFC.style", "salida en estilo RFC" }, { "signature.algorithm.name", "nombre de algoritmo de firma" }, { "source.alias", "alias de origen" }, { "source.key.password", "contraseña de clave de origen" }, { "source.keystore.name", "nombre de almacén de claves de origen" }, { "source.keystore.password.protected", "almacén de claves de origen protegido por contraseña" }, { "source.keystore.provider.name", "nombre de proveedor de almacén de claves de origen" }, { "source.keystore.password", "contraseña de almacén de claves de origen" }, { "source.keystore.type", "tipo de almacén de claves de origen" }, { "SSL.server.host.and.port", "puerto y host del servidor SSL" }, { "signed.jar.file", "archivo jar firmado" }, { "certificate.validity.start.date.time", "fecha/hora de inicio de validez del certificado" }, { "keystore.password", "contraseña de almacén de claves" }, { "keystore.type", "tipo de almacén de claves" }, { "trust.certificates.from.cacerts", "certificados de protección de cacerts" }, { "verbose.output", "salida detallada" }, { "validity.number.of.days", "número de validez de días" }, { "Serial.ID.of.cert.to.revoke", "identificador de serie del certificado que se va a revocar" }, { "keytool.error.", "error de herramienta de claves: " }, { "Illegal.option.", "Opción no permitida:  " }, { "Illegal.value.", "Valor no permitido: " }, { "Unknown.password.type.", "Tipo de contraseña desconocido: " }, { "Cannot.find.environment.variable.", "No se ha encontrado la variable del entorno: " }, { "Cannot.find.file.", "No se ha encontrado el archivo: " }, { "Command.option.flag.needs.an.argument.", "La opción de comando {0} necesita un argumento." }, { "Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value.", "Advertencia: los almacenes de claves en formato PKCS12 no admiten contraseñas de clave y almacenamiento distintas. Se ignorará el valor especificado por el usuario, {0}." }, { ".keystore.must.be.NONE.if.storetype.is.{0}", "-keystore debe ser NONE si -storetype es {0}" }, { "Too.many.retries.program.terminated", "Ha habido demasiados intentos, se ha cerrado el programa" }, { ".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}", "Los comandos -storepasswd y -keypasswd no están soportados si -storetype es {0}" }, { ".keypasswd.commands.not.supported.if.storetype.is.PKCS12", "Los comandos -keypasswd no están soportados si -storetype es PKCS12" }, { ".keypass.and.new.can.not.be.specified.if.storetype.is.{0}", "-keypass y -new no se pueden especificar si -storetype es {0}" }, { "if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified", "si se especifica -protected, no deben especificarse -storepass, -keypass ni -new" }, { "if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified", "Si se especifica -srcprotected, no se puede especificar -srcstorepass ni -srckeypass" }, { "if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified", "Si keystore no está protegido por contraseña, no se deben especificar -storepass, -keypass ni -new" }, { "if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified", "Si el almacén de claves de origen no está protegido por contraseña, no se deben especificar -srcstorepass ni -srckeypass" }, { "Illegal.startdate.value", "Valor de fecha de inicio no permitido" }, { "Validity.must.be.greater.than.zero", "La validez debe ser mayor que cero" }, { "provName.not.a.provider", "{0} no es un proveedor" }, { "Usage.error.no.command.provided", "Error de sintaxis: no se ha proporcionado ningún comando" }, { "Source.keystore.file.exists.but.is.empty.", "El archivo de almacén de claves de origen existe, pero está vacío: " }, { "Please.specify.srckeystore", "Especifique -srckeystore" }, { "Must.not.specify.both.v.and.rfc.with.list.command", "No se deben especificar -v y -rfc simultáneamente con el comando 'list'" }, { "Key.password.must.be.at.least.6.characters", "La contraseña de clave debe tener al menos 6 caracteres" }, { "New.password.must.be.at.least.6.characters", "La nueva contraseña debe tener al menos 6 caracteres" }, { "Keystore.file.exists.but.is.empty.", "El archivo de almacén de claves existe, pero está vacío: " }, { "Keystore.file.does.not.exist.", "El archivo de almacén de claves no existe: " }, { "Must.specify.destination.alias", "Se debe especificar un alias de destino" }, { "Must.specify.alias", "Se debe especificar un alias" }, { "Keystore.password.must.be.at.least.6.characters", "La contraseña del almacén de claves debe tener al menos 6 caracteres" }, { "Enter.the.password.to.be.stored.", "Introduzca la contraseña que se va a almacenar:  " }, { "Enter.keystore.password.", "Introduzca la contraseña del almacén de claves:  " }, { "Enter.source.keystore.password.", "Introduzca la contraseña de almacén de claves de origen:  " }, { "Enter.destination.keystore.password.", "Introduzca la contraseña de almacén de claves de destino:  " }, { "Keystore.password.is.too.short.must.be.at.least.6.characters", "La contraseña del almacén de claves es demasiado corta, debe tener al menos 6 caracteres" }, { "Unknown.Entry.Type", "Tipo de Entrada Desconocido" }, { "Too.many.failures.Alias.not.changed", "Demasiados fallos. No se ha cambiado el alias" }, { "Entry.for.alias.alias.successfully.imported.", "La entrada del alias {0} se ha importado correctamente." }, { "Entry.for.alias.alias.not.imported.", "La entrada del alias {0} no se ha importado." }, { "Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported.", "Problema al importar la entrada del alias {0}: {1}.\nNo se ha importado la entrada del alias {0}." }, { "Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled", "Comando de importación completado: {0} entradas importadas correctamente, {1} entradas incorrectas o canceladas" }, { "Warning.Overwriting.existing.alias.alias.in.destination.keystore", "Advertencia: se sobrescribirá el alias {0} en el almacén de claves de destino" }, { "Existing.entry.alias.alias.exists.overwrite.no.", "El alias de entrada existente {0} ya existe, ¿desea sobrescribirlo? [no]:  " }, { "Too.many.failures.try.later", "Demasiados fallos; inténtelo más adelante" }, { "Certification.request.stored.in.file.filename.", "Solicitud de certificación almacenada en el archivo <{0}>" }, { "Submit.this.to.your.CA", "Enviar a la CA" }, { "if.alias.not.specified.destalias.and.srckeypass.must.not.be.specified", "si no se especifica el alias, no se debe especificar destalias ni srckeypass" }, { "The.destination.pkcs12.keystore.has.different.storepass.and.keypass.Please.retry.with.destkeypass.specified.", "El almacén de claves pkcs12 de destino tiene storepass y keypass diferentes. Vuelva a intentarlo con -destkeypass especificado." }, { "Certificate.stored.in.file.filename.", "Certificado almacenado en el archivo <{0}>" }, { "Certificate.reply.was.installed.in.keystore", "Se ha instalado la respuesta del certificado en el almacén de claves" }, { "Certificate.reply.was.not.installed.in.keystore", "No se ha instalado la respuesta del certificado en el almacén de claves" }, { "Certificate.was.added.to.keystore", "Se ha agregado el certificado al almacén de claves" }, { "Certificate.was.not.added.to.keystore", "No se ha agregado el certificado al almacén de claves" }, { ".Storing.ksfname.", "[Almacenando {0}]" }, { "alias.has.no.public.key.certificate.", "{0} no tiene clave pública (certificado)" }, { "Cannot.derive.signature.algorithm", "No se puede derivar el algoritmo de firma" }, { "Alias.alias.does.not.exist", "El alias <{0}> no existe" }, { "Alias.alias.has.no.certificate", "El alias <{0}> no tiene certificado" }, { "Key.pair.not.generated.alias.alias.already.exists", "No se ha generado el par de claves, el alias <{0}> ya existe" }, { "Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for", "Generando par de claves {1} de {0} bits para certificado autofirmado ({2}) con una validez de {3} días\n\tpara: {4}" }, { "Enter.key.password.for.alias.", "Introduzca la contraseña de clave para <{0}>" }, { ".RETURN.if.same.as.keystore.password.", "\t(INTRO si es la misma contraseña que la del almacén de claves):  " }, { "Key.password.is.too.short.must.be.at.least.6.characters", "La contraseña de clave es demasiado corta; debe tener al menos 6 caracteres" }, { "Too.many.failures.key.not.added.to.keystore", "Demasiados fallos; no se ha agregado la clave al almacén de claves" }, { "Destination.alias.dest.already.exists", "El alias de destino <{0}> ya existe" }, { "Password.is.too.short.must.be.at.least.6.characters", "La contraseña es demasiado corta; debe tener al menos 6 caracteres" }, { "Too.many.failures.Key.entry.not.cloned", "Demasiados fallos. No se ha clonado la entrada de clave" }, { "key.password.for.alias.", "contraseña de clave para <{0}>" }, { "Keystore.entry.for.id.getName.already.exists", "La entrada de almacén de claves para <{0}> ya existe" }, { "Creating.keystore.entry.for.id.getName.", "Creando entrada de almacén de claves para <{0}> ..." }, { "No.entries.from.identity.database.added", "No se han agregado entradas de la base de datos de identidades" }, { "Alias.name.alias", "Nombre de Alias: {0}" }, { "Creation.date.keyStore.getCreationDate.alias.", "Fecha de Creación: {0,date}" }, { "alias.keyStore.getCreationDate.alias.", "{0}, {1,date}, " }, { "alias.", "{0}, " }, { "Entry.type.type.", "Tipo de Entrada: {0}" }, { "Certificate.chain.length.", "Longitud de la Cadena de Certificado: " }, { "Certificate.i.1.", "Certificado[{0,number,integer}]:" }, { "Certificate.fingerprint.SHA1.", "Huella Digital de Certificado (SHA1): " }, { "Keystore.type.", "Tipo de Almacén de Claves: " }, { "Keystore.provider.", "Proveedor de Almacén de Claves: " }, { "Your.keystore.contains.keyStore.size.entry", "Su almacén de claves contiene {0,number,integer} entrada" }, { "Your.keystore.contains.keyStore.size.entries", "Su almacén de claves contiene {0,number,integer} entradas" }, { "Failed.to.parse.input", "Fallo al analizar la entrada" }, { "Empty.input", "Entrada vacía" }, { "Not.X.509.certificate", "No es un certificado X.509" }, { "alias.has.no.public.key", "{0} no tiene clave pública" }, { "alias.has.no.X.509.certificate", "{0} no tiene certificado X.509" }, { "New.certificate.self.signed.", "Nuevo Certificado (Autofirmado):" }, { "Reply.has.no.certificates", "La respuesta no tiene certificados" }, { "Certificate.not.imported.alias.alias.already.exists", "Certificado no importado, el alias <{0}> ya existe" }, { "Input.not.an.X.509.certificate", "La entrada no es un certificado X.509" }, { "Certificate.already.exists.in.keystore.under.alias.trustalias.", "El certificado ya existe en el almacén de claves con el alias <{0}>" }, { "Do.you.still.want.to.add.it.no.", "¿Aún desea agregarlo? [no]:  " }, { "Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias.", "El certificado ya existe en el almacén de claves de la CA del sistema, con el alias <{0}>" }, { "Do.you.still.want.to.add.it.to.your.own.keystore.no.", "¿Aún desea agregarlo a su propio almacén de claves? [no]:  " }, { "Trust.this.certificate.no.", "¿Confiar en este certificado? [no]:  " }, { "YES", "SÍ" }, { "New.prompt.", "Nuevo {0}: " }, { "Passwords.must.differ", "Las contraseñas deben ser distintas" }, { "Re.enter.new.prompt.", "Vuelva a escribir el nuevo {0}: " }, { "Re.enter.password.", "Vuelva a introducir la contraseña: " }, { "Re.enter.new.password.", "Volver a escribir la contraseña nueva: " }, { "They.don.t.match.Try.again", "No coinciden. Inténtelo de nuevo" }, { "Enter.prompt.alias.name.", "Escriba el nombre de alias de {0}:  " }, { "Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry.", "Indique el nuevo nombre de alias\t(INTRO para cancelar la importación de esta entrada):  " }, { "Enter.alias.name.", "Introduzca el nombre de alias:  " }, { ".RETURN.if.same.as.for.otherAlias.", "\t(INTRO si es el mismo que para <{0}>)" }, { "What.is.your.first.and.last.name.", "¿Cuáles son su nombre y su apellido?" }, { "What.is.the.name.of.your.organizational.unit.", "¿Cuál es el nombre de su unidad de organización?" }, { "What.is.the.name.of.your.organization.", "¿Cuál es el nombre de su organización?" }, { "What.is.the.name.of.your.City.or.Locality.", "¿Cuál es el nombre de su ciudad o localidad?" }, { "What.is.the.name.of.your.State.or.Province.", "¿Cuál es el nombre de su estado o provincia?" }, { "What.is.the.two.letter.country.code.for.this.unit.", "¿Cuál es el código de país de dos letras de la unidad?" }, { "Is.name.correct.", "¿Es correcto {0}?" }, { "no", "no" }, { "yes", "sí" }, { "y", "s" }, { ".defaultValue.", "  [{0}]:  " }, { "Alias.alias.has.no.key", "El alias <{0}> no tiene clave" }, { "Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key", "El alias <{0}> hace referencia a un tipo de entrada que no es una clave privada. El comando -keyclone sólo permite la clonación de entradas de claves privadas" }, { ".WARNING.WARNING.WARNING.", "*****************  WARNING WARNING WARNING  *****************" }, { "Signer.d.", "#%d de Firmante:" }, { "Timestamp.", "Registro de Hora:" }, { "Signature.", "Firma:" }, { "CRLs.", "CRL:" }, { "Certificate.owner.", "Propietario del Certificado: " }, { "Not.a.signed.jar.file", "No es un archivo jar firmado" }, { "No.certificate.from.the.SSL.server", "Ningún certificado del servidor SSL" }, { ".The.integrity.of.the.information.stored.in.your.keystore.", "* La integridad de la información almacenada en el almacén de claves  *\n* NO se ha comprobado.  Para comprobar dicha integridad, *\n* debe proporcionar la contraseña del almacén de claves.                  *" }, { ".The.integrity.of.the.information.stored.in.the.srckeystore.", "* La integridad de la información almacenada en srckeystore*\n* NO se ha comprobado.  Para comprobar dicha integridad, *\n* debe proporcionar la contraseña de srckeystore.                *" }, { "Certificate.reply.does.not.contain.public.key.for.alias.", "La respuesta de certificado no contiene una clave pública para <{0}>" }, { "Incomplete.certificate.chain.in.reply", "Cadena de certificado incompleta en la respuesta" }, { "Certificate.chain.in.reply.does.not.verify.", "La cadena de certificado de la respuesta no verifica: " }, { "Top.level.certificate.in.reply.", "Certificado de nivel superior en la respuesta:\n" }, { ".is.not.trusted.", "... no es de confianza. " }, { "Install.reply.anyway.no.", "¿Instalar respuesta de todos modos? [no]:  " }, { "NO", "NO" }, { "Public.keys.in.reply.and.keystore.don.t.match", "Las claves públicas en la respuesta y en el almacén de claves no coinciden" }, { "Certificate.reply.and.certificate.in.keystore.are.identical", "La respuesta del certificado y el certificado en el almacén de claves son idénticos" }, { "Failed.to.establish.chain.from.reply", "No se ha podido definir una cadena a partir de la respuesta" }, { "n", "n" }, { "Wrong.answer.try.again", "Respuesta incorrecta, vuelva a intentarlo" }, { "Secret.key.not.generated.alias.alias.already.exists", "No se ha generado la clave secreta, el alias <{0}> ya existe" }, { "Please.provide.keysize.for.secret.key.generation", "Proporcione el valor de -keysize para la generación de claves secretas" }, { "warning.not.verified.make.sure.keystore.is.correct", "ADVERTENCIA: no se ha verificado. Asegúrese de que el valor de -keystore es correcto." }, { "Extensions.", "Extensiones: " }, { ".Empty.value.", "(Valor vacío)" }, { "Extension.Request.", "Solicitud de Extensión:" }, { "Unknown.keyUsage.type.", "Tipo de uso de clave desconocido: " }, { "Unknown.extendedkeyUsage.type.", "Tipo de uso de clave extendida desconocido: " }, { "Unknown.AccessDescription.type.", "Tipo de descripción de acceso desconocido: " }, { "Unrecognized.GeneralName.type.", "Tipo de nombre general no reconocido: " }, { "This.extension.cannot.be.marked.as.critical.", "Esta extensión no se puede marcar como crítica. " }, { "Odd.number.of.hex.digits.found.", "Se ha encontrado un número impar de dígitos hexadecimales: " }, { "Unknown.extension.type.", "Tipo de extensión desconocida: " }, { "command.{0}.is.ambiguous.", "El comando {0} es ambiguo:" }, { "the.certificate.request", "La solicitud de certificado" }, { "the.issuer", "El emisor" }, { "the.generated.certificate", "El certificado generado" }, { "the.generated.crl", "La CRL generada" }, { "the.generated.certificate.request", "La solicitud de certificado generada" }, { "the.certificate", "El certificado" }, { "the.crl", "La CRL" }, { "the.tsa.certificate", "El certificado de TSA" }, { "the.input", "La entrada" }, { "reply", "Responder" }, { "one.in.many", "%1$s #%2$d de %3$d" }, { "alias.in.cacerts", "Emisor <%s> en cacerts" }, { "alias.in.keystore", "Emisor <%s>" }, { "with.weak", "%s (débil)" }, { "key.bit", "Clave %2$s de %1$d bits" }, { "key.bit.weak", "Clave %2$s de %1$d bits (débil)" }, { ".PATTERN.printX509Cert.with.weak", "Propietario: {0}\nEmisor: {1}\nNúmero de serie: {2}\nVálido desde: {3} hasta: {4}\nHuellas digitales del certificado:\n\t MD5: {5}\n\t SHA1: {6}\n\t SHA256: {7}\nNombre del algoritmo de firma: {8}\nAlgoritmo de clave pública de asunto: {9}\nVersión: {10}" }, { "PKCS.10.with.weak", "Solicitud de certificado PKCS #10 (Versión 1.0)\nAsunto: %1$s\nFormato: %2$s\nClave pública: %3$s\nAlgoritmo de firma: %4$s\n" }, { "verified.by.s.in.s.weak", "Verificado por %1$s en %2$s con %3$s" }, { "whose.sigalg.risk", "%1$s utiliza el algoritmo de firma %2$s, lo que se considera un riesgo de seguridad." }, { "whose.key.risk", "%1$s utiliza %2$s, lo que se considera riesgo de seguridad." }, { "jks.storetype.warning", "El almacén de claves %1$s utiliza un formato propietario. Se recomienda migrar a PKCS12, que es un formato estándar del sector que utiliza \"keytool -importkeystore -srckeystore %2$s -destkeystore %2$s -deststoretype pkcs12\"." }, { "migrate.keystore.warning", "Se ha migrado \"%1$s\" a %4$s. Se ha realizado la copia de seguridad del almacén de claves %2$s como \"%3$s\"." }, { "backup.keystore.warning", "La copia de seguridad del almacén de claves \"%1$s\" se ha realizado como \"%3$s\"..." }, { "importing.keystore.status", "Importando el almacén de claves de %1$s a %2$s..." } };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 467 */     return contents;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\tools\keytool\Resources_es.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */