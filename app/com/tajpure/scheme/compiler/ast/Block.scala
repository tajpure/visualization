package com.tajpure.scheme.compiler.ast

import com.tajpure.scheme.compiler.Scope
import com.tajpure.scheme.compiler.value.Value

import org.jllvm.value.user.instruction.ReturnInstruction

class Block(val statements: List[Node], _file: String, _start: Int, _end: Int, _row: Int, _col: Int)
  extends Node(_file: String, _start: Int, _end: Int, _row: Int, _col: Int) {
  
  def this(_statements: List[Node], node: Node) = 
    this(_statements, node.file, node.start, node.end, node.row, node.col)

  def interp(s: Scope): Value = {
    val curScope: Scope = new Scope(s)
    s.setInnerScope(curScope)
    statements.map {
      node =>
        node.interp(curScope)
    }.last
  }

  def typecheck(s: Scope): Value = {
    val curScope: Scope = new Scope(s)
    s.setInnerScope(curScope)
    statements.map {
      node =>
        node.typecheck(curScope)
    }.last
  }
  
  def codegen(s: Scope): org.jllvm.value.Value = {
    val curScope: Scope = new Scope(s)
    s.setInnerScope(curScope)
    statements.map {
      node =>
        node.codegen(curScope)
    }.last
  }
  
  override
  def toString(): String = {
    "Block:(\n" + statements.foldLeft("")((content: String, node: Node) => "  " + content + node.toString()) + "\n)"
  }

}

object Block extends App {
  val statements: List[Int] = List(1,23,3,45,6)
  println(statements.foldLeft("")((content: String, node: Int) => content + node.toString()))
  var i = 1
  statements.map { r => i = r }
  println(i)
}