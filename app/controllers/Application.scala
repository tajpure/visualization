package controllers

import play.api._
import play.api.mvc._
import com.tajpure.scheme.compiler._
import com.tajpure.scheme.compiler.parser._

object Application extends Controller {
  
  def main = Action {
    Ok(views.html.main("visualization"))
  }
  
  def lex(source: String) = Action {
    val res = LexParser.lex(source)
    Ok(res)
  }
  
  def parse(source: String) = Action {
    val res = Parser.parseSource(source)
    Ok(res)
  }
  
  def ir(source: String) = Action {
    var res = Main.codegen(source)
    Ok(res)
  }
  
  def target(source: String) = Action {
     var res = Main.bitcode(source)
    Ok(res)
  }
}