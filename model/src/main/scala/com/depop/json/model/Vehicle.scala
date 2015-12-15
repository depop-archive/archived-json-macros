package com.depop.json.model

sealed trait Vehicle

case class Car(numberOfDoors: Int, engineCapacity: Double) extends Vehicle

case class Airplane(numberOfEngines: Int) extends Vehicle
