package com.tajpure.scheme.compiler

import com.tajpure.scheme.compiler.util.FileUtils

object Main extends App {
  
  override 
  def main(args: Array[String]): Unit = {
    val compiler: Compiler = new Compiler("./src/test/resources/scheme/double.scm")
    compiler.compile0()
  }
  
  def codegen(source: String): String = {
//    val path = "./demo.scm"
//    FileUtils.save(path, source)
//    val compiler: Compiler = new Compiler(path)
//    compiler.compile0()
    FileUtils.read("./demo.ll")
  }
  
  def bitcode(source: String): String = {
//    val path = "./demo.scm"
//    FileUtils.save(path, source)
//    val compiler: Compiler = new Compiler(path)
//    compiler.compile0()
    FileUtils.read("./demo.bc")
  }
  
}