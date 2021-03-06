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

package ch.chuv.lren.woken.core.model

object Shapes {

  sealed trait Shape {
    def mime: String
    def values: Set[String]
    def contains(s: String): Boolean = values.contains(s)
  }

  object error extends Shape {
    val error  = "error"
    val mime   = "text/plain+error"
    val values = Set(error, mime)
  }

  object pfa extends Shape {
    val pfa    = "pfa"
    val json   = "pfa_json"
    val mime   = "application/pfa+json"
    val values = Set(pfa, json, mime)
  }

  object pfaYaml extends Shape {
    val yaml   = "pfa_yaml"
    val mime   = "application/pfa+yaml"
    val values = Set(yaml, mime)
  }

  object pfaExperiment extends Shape {
    val json   = "pfa_experiment_json"
    val mime   = "application/vnd.hbp.mip.experiment.pfa+json"
    val values = Set(json, mime)
  }

  object dataResource extends Shape {
    val json   = "data_resource_json"
    val mime   = "application/vnd.dataresource+json"
    val values = Set(json, mime)
  }

  object html extends Shape {
    val html   = "html"
    val mime   = "text/html"
    val values = Set(html, mime)
  }

  object svg extends Shape {
    val svg       = "svg"
    val svg_image = "svg_image"
    val mime      = "image/svg+xml"
    val values    = Set(svg, svg_image, mime)
  }

  object png extends Shape {
    val png       = "png"
    val png_image = "png_image"
    val mime      = "image/png;base64"
    val values    = Set(png, png_image, mime)
  }

  object highcharts extends Shape {
    val highcharts = "highcharts"
    val json       = "highcharts_json"
    val mime       = "application/highcharts+json"
    val values     = Set(highcharts, json, mime)
  }

  object visjs extends Shape {
    val visjs  = "visjs"
    val js     = "visjs_javascript"
    val mime   = "application/visjs+javascript"
    val values = Set(visjs, js, mime)
  }

  object plotly extends Shape {
    val plotly = "plotly"
    val json   = "plotly_json"
    val mime   = "application/plotly+json"
    val values = Set(plotly, json, mime)
  }

  // Generic Json, for other types of visualisations
  object json extends Shape {
    val json   = "json"
    val mime   = "application/json"
    val values = Set(json, mime)
  }

  object compound extends Shape {
    val compound = "compound"
    val mime     = "application/vnd.hbp.mip.compound+json"
    val values   = Set(compound, mime)
  }

  /** Results stored as PFA documents in the database */
  val pfaResults: Set[Shape] = Set(pfa, pfaYaml, pfaExperiment)

  /** Results stored as Json documents in the database */
  val visualisationJsonResults: Set[Shape] = Set(highcharts, plotly, json, dataResource, compound)

  /** Results stored as generic documents (strings) in the database */
  val visualisationOtherResults: Set[Shape] = Set(html, svg, png, visjs)

  val allResults
    : Set[Shape] = pfaResults ++ visualisationJsonResults ++ visualisationOtherResults + error

  def fromString(s: String): Option[Shape] =
    allResults.find(_.contains(s))

}
