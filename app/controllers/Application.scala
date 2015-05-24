package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def main = Action {
    Ok(views.html.main("visualization"))
  }
  
  def lex(source: String) = Action {
//    val result = LexParser.lex(source)
//    printf(result)
    Ok(source)
  }
  
  def parse(source: String) = Action {
    Ok(source)
  }
  
  def ir(source: String) = Action {
    Ok(source)
  }
  
  def target(source: String) = Action {
    Ok(source)
  }
  
  def exec(source: String) = Action {
    Ok(source)
  }
}