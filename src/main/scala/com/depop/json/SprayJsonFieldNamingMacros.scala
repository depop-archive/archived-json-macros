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

import scala.reflect.macros.blackbox._

trait SprayJsonFieldNamingMacros {

  def jsonWriterMacro[T : c.WeakTypeTag](c: Context)(fieldNameWriter: c.Expr[String => String]): c.Expr[JsonWriter[T]] = {
    import c.universe._

    // the type representation of T
    val baseType = weakTypeOf[T]

    // fields within the type T
    val fields = baseType.decls.collect {
      case methodSymbol: MethodSymbol if methodSymbol.isCaseAccessor =>
        methodSymbol
    }

    // ensure all JsonWriters exist for field return types
    fields.map(_.returnType).toSet.foreach { fieldType: Type =>
      q"""implicitly[spray.json.JsonWriter[$fieldType]]"""
    }

    // pairs of String -> JsValue which represent json field name -> json field value
    val pairs = fields.toList.map { field =>
      val classFieldName = field.name.decodedName.toTermName
      val jsonFieldName = q"$fieldNameWriter(${classFieldName.toString})"
      q"($jsonFieldName, item.$classFieldName.toJson)"
    }

    // create the JsonWriter
    val jsonWriterType = weakTypeOf[JsonWriter[T]]
    val jsonWriter =
      q"""
        new $jsonWriterType {
          def write(item: $baseType): spray.json.JsValue = {
            JsObject($pairs)
          }
        }
      """

    c.Expr[JsonWriter[T]](jsonWriter)
  }

}
