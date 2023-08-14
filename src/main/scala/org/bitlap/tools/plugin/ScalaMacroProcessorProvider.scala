package org.bitlap.tools.plugin

import com.intellij.openapi.Disposable
import org.bitlap.tools.plugin.processor.Processor
import org.bitlap.tools.plugin.processor.clazz._
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef._

import scala.collection.mutable

/**
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2021/7/1
 */
class ScalaMacroProcessorProvider extends Disposable {

  private lazy val processors = mutable.Map[String, Processor]()
  register(ScalaMacroNames.BUILDER, new BuilderProcessor())
    .register(ScalaMacroNames.APPLY, new ApplyProcessor())
    .register(ScalaMacroNames.CONSTRUCTOR, new ConstructorProcessor())
    .register(ScalaMacroNames.EQUALS_HASHCODE, new EqualsAndHashCodeProcessor())
    .register(ScalaMacroNames.JAVA_COMPATIBLE, new JavaCompatibleProcessor())

  override def dispose(): Unit = ()

  def register(name: String, processor: Processor): ScalaMacroProcessorProvider = {
    if (processor != null) {
      this.processors += name.trim -> processor
    }
    this
  }

  def findProcessors(source: ScTypeDefinition): Seq[Processor] = {
    processors.filter { p =>
      source match {
        case obj: ScObject  => obj.hasAnnotation(p._1) || obj.fakeCompanionClassOrCompanionClass.hasAnnotation(p._1)
        case clazz: ScClass => clazz.hasAnnotation(p._1)
        case _              => source.hasAnnotation(p._1)
      }
    }.values.toSeq
  }
}
