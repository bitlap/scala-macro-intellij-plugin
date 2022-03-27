package org.bitlap.tools.plugin.processor.clazz

import org.bitlap.tools.plugin.processor.ProcessType.ProcessType
import org.bitlap.tools.plugin.processor.{AbsProcessor, ProcessType}
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.{ScClass, ScTypeDefinition}

/**
 * Desc: Processor for annotation javaCompatible
 */
class JavaCompatibleProcessor extends AbsProcessor {

  override def needCompanion: Boolean = true

  override def process(source: ScTypeDefinition, typ: ProcessType): Seq[String] = {
    typ match {
      case ProcessType.Method =>
        source match {
          case _: ScClass =>
            // source.getConstructors.filter(_.hasParameters)
            Seq("def this() = ???")
          case _ => Nil
        }
      case _ => Nil
    }
  }
}
