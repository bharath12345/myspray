package in.bharathwrites.boot

import org.fusesource.scalate.{Binding, TemplateEngine}
import org.fusesource.scalate.util.{Resource, FileResourceLoader}

/**
 * Created by bharadwaj on 27/12/13.
 */
class templating {

  val engine = new TemplateEngine
  engine.allowReload =  false
  engine.allowCaching =  false
  engine.resourceLoader = new FileResourceLoader {
    override def resource(uri: String): Option[Resource] =
      Some(Resource.fromText(uri, "Some text"))
  }

  engine.bindings = List(Binding("name", "(String,String)"))

  val template = engine.load("/path/to/template.ssp", List(Binding("city", "String")))

  val output = engine.layout("/foo/bar.scaml", Map("name" -> ("Hiram", "Chirino"), "city" -> "Tampa"))

}
