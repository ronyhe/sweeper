package com.ronyhe.sweeper.view

import org.eclipse.swt.widgets.Display
import org.scalatest.FunSuite

class SWTTest extends FunSuite {
  test("SWT is properly included and is able to load its libraries") {
    val display = new Display()
    display.dispose()
  }
}
