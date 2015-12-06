package com.ronyhe.sweeper.view

import org.scalatest.FunSuite

class SWTTest extends FunSuite {
  test("SWT is properly included") {
    assertCompiles("import org.eclipse.swt")
  }
}
