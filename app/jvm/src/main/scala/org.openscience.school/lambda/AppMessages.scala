package org.openscience.school.lambda

import com.typesafe.config.Config

object AppMessages {

  case class Start(config: Config)
  case object Stop

}
