/*
 * Copyright (C) 2017  LREN CHUV for Human Brain Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.chuv.lren.woken.backends.exareme

import akka.actor.{ Actor, ActorLogging, Cancellable, Props, Status }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

// see https://github.com/calvinlfer/Akka-HTTP-Akka-Streams-Akka-Actors-Integration/blob/master/src/main/scala/com/experiments/integration/actors/JokeFetcher.scala

class ExaremeService extends Actor with ActorLogging {

  import ExaremeService._

  implicit val timeout      = Timeout(5 seconds)
  implicit val materializer = ActorMaterializer()(context.system)
  implicit val ec           = context.system.dispatcher

  val http                             = Http(context.system)
  val url                              = "http://api.icndb.com/jokes/random?escape=javascript"
  var cancellable: Option[Cancellable] = None

  override def preStart(): Unit = {
    log.debug("Starting up Joke Fetcher")
    cancellable = Some(
      context.system.scheduler
        .schedule(initialDelay = 1 second, interval = 1 second, receiver = self, FetchResult)
    )
  }

  override def postStop(): Unit =
    cancellable.foreach(c => c.cancel())

  override def receive: Receive = {
    case FetchResult =>
      val futureResponse = http.singleRequest(HttpRequest(GET, url)) flatMap { httpResponse =>
        httpResponse.status match {
          case OK =>
            //val futureResult = Unmarshal(httpResponse).to[Result]
            //futureResult.map(x => Some(x))
            Future.successful(None)
          case _ =>
            log.error("Non 200 OK response code, error obtaining jokes")
            Future.successful(None)
        }
      }
      futureResponse pipeTo self

    case Status.Failure(throwable) =>
      log.error("Could not obtain jokes", throwable)

    case Some(Result(_, joke)) =>
    //context.system.eventStream.publish(JokeEvent(joke.id, joke.joke))
  }
}

object ExaremeService {

  sealed trait Command

  case object FetchResult extends Command

  // Type safe HTTP response
  case class Joke(id: Int, joke: String, categories: List[String])

  case class Result(response: String, joke: Joke)

  def props = Props[ExaremeService]
}
