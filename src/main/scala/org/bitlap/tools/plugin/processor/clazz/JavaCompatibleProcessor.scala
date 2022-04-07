package org.bitlap.tools.plugin.processor.clazz

import org.bitlap.tools.plugin.processor.ProcessType.ProcessType
import org.bitlap.tools.plugin.processor.{ AbsProcessor, ProcessType }
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.{ ScClass, ScTypeDefinition }

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
            val params = super.getConstructorCurryingParameters(source.asInstanceOf[ScClass]).flatten
            val assignMethods = params.flatMap { term =>
              val mName = term.name.head.toUpper + term.name.tail
              Seq(
                if (term.isVar) Some(s"def set$mName(${term.name}: ${term.typ}) = this") else None,
                Some(s"def get$mName(): ${term.typ} = this.${term.name}"),
              ).collect { case Some(value) if value.nonEmpty => value }
            }
            Seq("def this() = ???") ++ assignMethods
          case _ => Nil
        }
      case _ => Nil
    }
  }
}
