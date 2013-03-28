package net.chrisrichardson.vme.common.domain

object PhoneNumber {

    def isValidNumber(number : String) = number.length > 9 && !number.substring(5, 8).equals("555")

}