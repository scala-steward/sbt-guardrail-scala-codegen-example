package helloworld

import scala.concurrent.Future
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object Hello {
  def main(args: Array[String]) = {
    import com.example.clients.petstore.user.UserClient
    import scala.concurrent.ExecutionContext.Implicits.global

    val server = buildServer()

    implicit val actorSys = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val userClient = UserClient.httpClient(server)
    val result = userClient.getUserByName("billg")

    System.out.println(result)
  }

  private def buildServer(): HttpRequest => Future[HttpResponse] = {
    import com.example.servers.petstore.user._
    import com.example.servers.petstore.{definitions => sdefs}
    import akka.http.scaladsl.server.Route
    import akka.http.scaladsl.settings.RoutingSettings

    implicit val actorSys = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val routingSettings = RoutingSettings(actorSys)

    Route.asyncHandler(
      UserResource.routes(new DummyUserHandler())
    )
  }
}

class DummyUserHandler
  extends com.example.servers.petstore.user.UserHandler {

  import com.example.servers.petstore.user._
  import com.example.servers.petstore.definitions._
  import scala.collection._
  import scala.concurrent.ExecutionContext.Implicits.global

  override def createUser(respond: UserResource.createUserResponse.type)(body: User): scala.concurrent.Future[UserResource.createUserResponse] = ???
  override def createUsersWithArrayInput(respond: UserResource.createUsersWithArrayInputResponse.type)(body: Vector[User]): scala.concurrent.Future[UserResource.createUsersWithArrayInputResponse] = ???
  override def createUsersWithListInput(respond: UserResource.createUsersWithListInputResponse.type)(body: Vector[User]): scala.concurrent.Future[UserResource.createUsersWithListInputResponse] = ???
  override def loginUser(respond: UserResource.loginUserResponse.type)(username: String, password: String): scala.concurrent.Future[UserResource.loginUserResponse] = ???
  override def logoutUser(respond: UserResource.logoutUserResponse.type)(): scala.concurrent.Future[UserResource.logoutUserResponse] = ???
  override def getUserByName(respond: UserResource.getUserByNameResponse.type)(username: String): scala.concurrent.Future[UserResource.getUserByNameResponse] = {
    val user = new User(
      id = Some(1234),
      username = Some(username),
      firstName = Some("First"),
      lastName = Some("Last"),
      email = Some(username + "@example.com"))
    Future { UserResource.getUserByNameResponseOK(user) }
  }
  override def updateUser(respond: UserResource.updateUserResponse.type)(username: String, body: User): scala.concurrent.Future[UserResource.updateUserResponse] = ???
  override def deleteUser(respond: UserResource.deleteUserResponse.type)(username: String): scala.concurrent.Future[UserResource.deleteUserResponse] = ???
}
