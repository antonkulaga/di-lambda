package org.openscience.school.lambda.routes

import scala.language.postfixOps

import scalacss.Defaults._

object MyStyles extends StyleSheet.Standalone {
  import dsl._

  "#logo"-(
    maxHeight(40 vh),
    backgroundColor(lightgoldenrodyellow)
    )

  ".ui.inverted.table tr th" -(
      fontSize(20 pt) important,
      fontWeight.bold

  )
  ".ui.inverted.table tr td" -(
    fontSize(20 pt) important,
    fontWeight.bold

    )
}
