package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import com.typesafe.plugin._
import play.api.Play.current

import org.specs2.specification.Scope
import play.api.db.DBApi
import play.api.db.DB



/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
object IntegrationSpec extends Specification{
      
   object FakeApp extends FakeApplication(additionalConfiguration=inMemoryDatabase())
   def testServer = TestServer(3333,  FakeApplication(additionalConfiguration = inMemoryDatabase()))
   
   "app" should { "work from within a browser" in   running(testServer, HTMLUNIT){
       browser =>{
         browser.goTo("http://localhost:3333/")
         browser.pageSource must contain("Sign in")
         
       }
   }
   }
   
   "app" should { "render the index page" in   running(testServer, HTMLUNIT){
       browser =>{
          
          val home = route(FakeRequest(GET, "/")).get
           status(home) must equalTo(303)
          contentType(home) must beNone
        
      }
    }
   }
   
  
  
}

