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

package ch.chuv.lren.woken.dao

import cats._
import cats.implicits._
import ch.chuv.lren.woken.core.model.VariablesMeta

import scala.collection.concurrent.TrieMap
import scala.language.higherKinds

/**
  * The interface to Metadata database
  */
trait MetadataRepository[F[_]] extends Repository {

  def variablesMeta: VariablesMetaRepository[F]

}

trait VariablesMetaRepository[F[_]] extends Repository {

  def put(variablesMeta: VariablesMeta): F[VariablesMeta]

  def get(targetFeaturesTable: String): F[Option[VariablesMeta]]

}

class MetadataInMemoryRepository[F[_]: Applicative] extends MetadataRepository[F] {

  override val variablesMeta: VariablesMetaRepository[F] = new VariablesMetaRepository[F] {

    private val cache = new TrieMap[String, VariablesMeta]

    override def put(variablesMeta: VariablesMeta): F[VariablesMeta] = {
      cache.put(variablesMeta.targetFeaturesTable, variablesMeta)
      variablesMeta.pure[F]
    }

    override def get(targetFeaturesTable: String): F[Option[VariablesMeta]] =
      cache.get(targetFeaturesTable).pure[F]

  }

}
