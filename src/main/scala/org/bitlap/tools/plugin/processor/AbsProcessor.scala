package org.bitlap.tools.plugin.processor

import org.jetbrains.plugins.scala.lang.psi.api.base.ScMethodLike
import org.jetbrains.plugins.scala.lang.psi.api.statements.params.ScClassParameter
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.ScClass

/**
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2021/6/30
 */
abstract class AbsProcessor extends Processor {

  override def needCompanion: Boolean = false

  // Parameter
  case class Parameter(name: String, typ: String, isVar: Boolean = false)

  /**
   * get constructor parameters
   *
   * @return name and type
   */
  protected def getConstructorParameters(clazz: ScClass, withSecond: Boolean = true): Seq[Parameter] = {
    this.getConstructorCurryingParameters(clazz, withSecond, withCurrying = false).head
  }

  /**
   * get constructor parameters with currying
   *
   * @return
   *   if `withCurrying` = true, return (name: type, name: type)(name: type)...
   *   else return (name: type, name: type, name: type, ...)
   */
  protected def getConstructorCurryingParameters(clazz: ScClass, withSecond: Boolean = true, withCurrying: Boolean = true): Seq[Seq[Parameter]] = {
    val constructors = if (withSecond) {
      clazz.constructors.map(Some(_))
    } else {
      Seq(clazz.constructor.map(_.asInstanceOf[ScMethodLike]))
    }
    if (withCurrying) {
      constructors.flatten.flatMap { c =>
        c.effectiveParameterClauses.map(_.effectiveParameters.collect {
          case p: ScClassParameter =>
            Parameter(p.name, p.`type`().toOption.map(_.toString).getOrElse("Unit"), p.isVar)
        })
      }
    } else {
      Seq(
        constructors.flatten.flatMap(_.getParameterList.getParameters)
          .collect {
            case p: ScClassParameter =>
              Parameter(p.name, p.`type`().toOption.map(_.toString).getOrElse("Unit"), p.isVar)
          }
      )
    }
  }

  /**
   * get type param string
   *
   * @param returnType if it is return type
   * @return
   *   if `returnType` is false, just return typeParamString with bound like [T <: Any, U]
   *   else return typeParamString without bound like [T, U]
   */
  protected def getTypeParamString(clazz: ScClass, returnType: Boolean = false): String = {
    if (!returnType || clazz.typeParamString.isEmpty) {
      clazz.typeParamString
    } else {
      clazz.typeParameters.map(_.name).mkString("[", ", ", "]")
    }
  }
}
