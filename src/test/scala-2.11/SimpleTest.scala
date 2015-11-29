import org.scalatest.FunSuite

class SimpleTest extends FunSuite {
  test("just checking") {
    assert(true)
  }

  test("just checking fail") {
    fail("Just checking, fail")
  }
}
