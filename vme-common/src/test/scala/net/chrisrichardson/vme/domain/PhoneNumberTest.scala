package net.chrisrichardson.vme.domain

import junit.framework.Assert
import org.junit.Test
import net.chrisrichardson.vme.common.domain.PhoneNumber

class PhoneNumberTest  {

  @Test
  def testIsValid {
    Assert.assertTrue(PhoneNumber.isValidNumber("+14153331616"))
  }

  @Test
  def testIsInvalid {
    Assert.assertFalse(PhoneNumber.isValidNumber("+14155551616"))
  }
}