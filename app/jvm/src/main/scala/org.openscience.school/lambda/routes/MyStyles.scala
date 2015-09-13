package org.openscience.school.lambda.routes

import scala.language.postfixOps

import scalacss.Defaults._

object MyStyles extends StyleSheet.Standalone {
  import dsl._

  "#logo"-(
    maxHeight(40 vh),
    backgroundColor(lightgoldenrodyellow)
    )

}
