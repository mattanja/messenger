package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import com.typesafe.plugin._
import play.api.Play.current
import org.specs2.specification.Scope
import play.api.db.DBApi
import play.api.db.DB
import play.api.libs.ws.WS



/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
object IntegrationSpec extends Specification{
  sequential
  
   def testServer = TestServer(15155,FakeApplication(additionalConfiguration = inMemoryDatabase()))
   
   "run in a server" in new WithServer(app = FakeApplication(additionalConfiguration = inMemoryDatabase())) {
  await(WS.url("http://localhost:"+port).get).status must equalTo(OK)
}
   
   
   "app" should { "render the index page" in   running(testServer, HTMLUNIT){
       browser =>{
          
          val home = route(FakeRequest(GET, "/")).get
           status(home) must equalTo(200)
          contentType(home) must beSome
        
      }
    }
   }
   
  
   
  //"run in a browser" in new WithBrowser {
  //browser.goTo("/")
 // browser.$("#title").getTexts().get(0) must equalTo("Hello Guest")
    
 // browser.$("a").click()
    
  //browser.url must equalTo("/")
  //browser.$("#title").getTexts().get(0) must equalTo("Hello Coco")
//} 
  
  /*"app" should { "work from within a browser" in   running(testServer, HTMLUNIT) {
       browser =>{
         browser.goTo("http://localhost:15155/login")
         browser.pageSource must contain("Sign in")
    }
   }
   }*/
}
