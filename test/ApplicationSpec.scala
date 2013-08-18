package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import controllers.Application
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent._
import models._
/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {

  class TestController() extends Controller with Application

  "render login template" in {
    val controller = new TestController
    val html = views.html.login(controller.loginForm)(Flash())
    contentType(html) must equalTo("text/html")
    contentAsString(html) must contain("Sign in")
  }



  "Application" should {

    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone
      }
    }
  }
}