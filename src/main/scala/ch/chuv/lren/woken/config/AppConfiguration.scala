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

package ch.chuv.lren.woken.config

import com.typesafe.config.Config
import ch.chuv.lren.woken.cromwell.core.ConfigUtil._
import cats.data.Validated._
import cats.implicits._
import ch.chuv.lren.woken.messages.remoting.BasicAuthentication

case class MasterRouterConfig(miningActorsLimit: Int, experimentActorsLimit: Int)

case class AppConfiguration(
    clusterSystemName: String,
    dockerBridgeNetwork: Option[String],
    networkInterface: String,
    webServicesPort: Int,
    webServicesHttps: Boolean,
    disableWorkers: Boolean,
    jobServiceName: String,
    basicAuth: BasicAuthentication,
    masterRouterConfig: MasterRouterConfig
)

object AppConfiguration {

  def read(config: Config, path: List[String] = List("app")): Validation[AppConfiguration] = {
    val appConfig = config.validateConfig(path.mkString("."))

    appConfig.andThen { app =>
      val clusterSystemName   = app.validateString("clusterSystemName")
      val dockerBridgeNetwork = app.validateOptionalString("dockerBridgeNetwork")
      val networkInterface    = app.validateString("networkInterface")
      val port                = app.validateInt("webServicesPort")
      val jobServiceName      = app.validateString("jobServiceName")

      val https: Validation[Boolean] = app.validateBoolean("webServicesHttps").orElse(lift(true))
      val disableWorkers: Validation[Boolean] =
        app.validateBoolean("disableWorkers").orElse(lift(false))

      val basicAuth: Validation[BasicAuthentication] = app.validateConfig("basicAuth").andThen {
        c =>
          val user     = c.validateString("user")
          val password = c.validateString("password")
          (user, password) mapN BasicAuthentication.apply
      }

      val masterRouterConfig: Validation[MasterRouterConfig] =
        app.validateConfig("master.router.actors").andThen { c =>
          val miningActorsLimit     = c.validateInt("mining.limit")
          val experimentActorsLimit = c.validateInt("experiment.limit")
          (miningActorsLimit, experimentActorsLimit) mapN MasterRouterConfig.apply
        }

      (clusterSystemName,
       dockerBridgeNetwork,
       networkInterface,
       port,
       https,
       disableWorkers,
       jobServiceName,
       basicAuth,
       masterRouterConfig) mapN AppConfiguration.apply
    }
  }

}
