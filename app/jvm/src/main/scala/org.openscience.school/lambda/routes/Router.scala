package org.openscience.school.lambda.routes

import akka.http.extensions.security.LoginInfo
import akka.http.extensions.stubs._
import akka.http.scaladsl.server.{Route, Directives}


class Router extends Directives {
  val sessionController: SessionController = new InMemorySessionController
  val loginController: InMemoryLoginController = new InMemoryLoginController()
  loginController.addUser(LoginInfo("admin", "test2test", "test@email"))

  def routes: Route = new Head().routes ~ new Pages().routes

}
