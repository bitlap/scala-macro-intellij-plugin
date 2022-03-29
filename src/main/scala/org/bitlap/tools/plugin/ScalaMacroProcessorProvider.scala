package org.bitlap.tools.plugin

import com.intellij.openapi.Disposable
import org.bitlap.tools.plugin.processor.Processor
import org.bitlap.tools.plugin.processor.clazz.{ApplyProcessor, BuilderProcessor, ConstructorProcessor, EqualsAndHashCodeProcessor, JavaCompatibleProcessor, JsonProcessor, LogProcessor}
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.{ScClass, ScObject, ScTypeDefinition}

import scala.collection.mutable

/**
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2021/7/1
 */
class ScalaMacroProcessorProvider extends Disposable {

  private val processors = mutable.Map[String, Processor]()
  regist(ScalaMacroNames.BUILDER, new BuilderProcessor())
  regist(ScalaMacroNames.LOG, new LogProcessor())
  regist(ScalaMacroNames.JSON, new JsonProcessor())
  regist(ScalaMacroNames.APPLY, new ApplyProcessor())
  regist(ScalaMacroNames.CONSTRUCTOR, new ConstructorProcessor())
  regist(ScalaMacroNames.EQUALS_HASHCODE, new EqualsAndHashCodeProcessor())
  regist(ScalaMacroNames.JAVA_COMPATIBLE, new JavaCompatibleProcessor())

  override def dispose(): Unit = {}

  def regist(name: String, p: Processor): ScalaMacroProcessorProvider = {
    if (p != null) {
      this.processors += name.trim -> p
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
