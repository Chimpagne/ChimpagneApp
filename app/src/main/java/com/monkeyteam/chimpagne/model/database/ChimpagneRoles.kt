package com.monkeyteam.chimpagne.model.database

typealias ChimpagneRole = Int
object ChimpagneRoles {
  val OWNER : ChimpagneRole = 0
  val STAFF : ChimpagneRole = 1
  val GUEST : ChimpagneRole = 2
}