Download
--------
https://dl.bintray.com/agustafson/maven/

Generating a json writer using a function for generating json field name
====================================================================
Write this:
```
SprayJsonMacros.jsonWriter[Car](toSnakeCase).write(Car(2, 2.4))
SprayJsonMacros.jsonWriter[Airplane](toSnakeCase).write(Airplane(4))
```

To get this output:
```
JsObject("number_of_doors" -> JsNumber(2), "engine_capacity" -> JsNumber(2.4))
JsObject("number_of_engines" -> JsNumber(4))
```

Using some function:
`def toSnakeCase: String => String`


Generating a json writer for a parent by using child types
==========================================================
Given I have this:
```
sealed trait Vehicle
 case class Car(numberOfDoors: Int, engineCapacity: Double) extends Vehicle 
case class Airplane(numberOfEngines: Int) extends Vehicle
```

And I have an implicit `JsonWriter[Car]` and `JsonWriter[Airplane]` in scope

Then you can simply write:
```
val vehicleJsonWriter: JsonWriter[Vehicle] = SprayJsonMacros.jsonWriterFromSubTypes[Vehicle]
```
