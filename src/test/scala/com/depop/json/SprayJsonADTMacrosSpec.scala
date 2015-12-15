/*
 * Copyright 2015 Andrew Gustafson
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

import com.depop.json.model._
import org.specs2.mutable.Specification
import spray.json.DefaultJsonProtocol._
import spray.json._

class SprayJsonADTMacrosSpec extends Specification {
  "spray json macro" should {
    "write json for an ADT" in {
      implicit val carJsonFormat: JsonFormat[Car] = jsonFormat2(Car.apply)
      implicit val airplaneJsonFormat: JsonFormat[Airplane] = jsonFormat1(Airplane.apply)

      val vehicleJsonWriter = SprayJsonMacros.jsonWriterFromSubTypes[Vehicle]
      vehicleJsonWriter.write(Car(2, 2.4)) ==== JsObject("numberOfDoors" -> JsNumber(2), "engineCapacity" -> JsNumber(2.4))
      vehicleJsonWriter.write(Airplane(4)) ==== JsObject("numberOfEngines" -> JsNumber(4))
    }
  }
}
