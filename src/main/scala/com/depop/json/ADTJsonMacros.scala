/*
 * Copyright 2015 Depop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.depop.json

import spray.json.JsonWriter

import scala.language.experimental.macros
import scala.reflect.macros.blackbox._

trait ADTJsonMacros {

  def jsonWriterFromSubTypesMacro[T: c.WeakTypeTag](c: Context): c.Expr[JsonWriter[T]] = {
    import c.universe._

    // the type representation of T
    val baseType = weakTypeOf[T]

    // all subclasses for the type T which are "known", ie: extending a sealed trait
    val subclasses = baseType.typeSymbol.asClass.knownDirectSubclasses

    // the writers for all of the subclasses
    val writers = subclasses.map { subclass =>
      // function taking an instance of the subclass and returning that instance as json
      val q"{ case $writer }" =
        q""" {
          case item: $subclass =>
            val jsonWriter = implicitly[spray.json.JsonWriter[$subclass]]
            jsonWriter.write(item)
          }
        """
      writer
    }

    val jsonWriterType = weakTypeOf[JsonWriter[T]]

    // the JsonWriter for the parent type which matches against each of the subclass writers
    val jsonWriterFound =
      q"""
      new $jsonWriterType {
        def write(item: $baseType): spray.json.JsValue = {
          item match { case ..$writers }
        }
      }
      """

    c.Expr[JsonWriter[T]](jsonWriterFound)
  }

}
