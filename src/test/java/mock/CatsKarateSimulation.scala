package mock

import com.intuit.karate.gatling.PreDef.{pauseFor, _}
import io.gatling.core.Predef._

import scala.concurrent.duration._

class CatsKarateSimulation extends Simulation {

  MockUtils.startServer()

  val protocol = karateProtocol(
    "/cats/{id}" -> Nil,
    "/cats" -> pauseFor("get" -> 15, "post" -> 25)
  )

  protocol.nameResolver = (req, ctx) => req.getHeader("karate-name")

  val create = scenario("create").exec(karateFeature("classpath:mock/cats-create.feature"))
  val delete = scenario("delete").exec(karateFeature("classpath:mock/cats-delete.feature@name=delete"))
  val custom = scenario("custom").exec(karateFeature("classpath:mock/custom-rpc.feature"))

  setUp(
    create.inject(rampUsers(10) over (5 seconds)).protocols(protocol),
    delete.inject(rampUsers(5) over (5 seconds)).protocols(protocol),
    custom.inject(rampUsers(10) over (5 seconds)).protocols(protocol)
  )

}
