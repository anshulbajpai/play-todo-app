package controllers

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status.OK
import play.api.libs.ws.WSClient
import play.api.test.Injecting

class HomeIntegrationSpec extends WordSpec with MustMatchers with GuiceOneServerPerSuite with WsScalaTestClient with Injecting with ScalaFutures with IntegrationPatience {

  private implicit lazy val wsClient = inject[WSClient]

  "GET /" must {
    "render the index page" in {
      val response = wsUrl("/").get().futureValue
      response.status mustBe OK
      response.body must include ("Welcome to Play")
    }
  }

}
