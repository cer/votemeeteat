package net.chrisrichardson.vme.vmemanagement.services

import net.chrisrichardson.vme.common.domain.Location

case class VmeRecord(id : String = null, name : String, phoneNumber : String, location : Location)