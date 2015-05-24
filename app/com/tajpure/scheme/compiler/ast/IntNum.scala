package com.tajpure.scheme.compiler.ast

import com.tajpure.scheme.compiler.Scope
import com.tajpure.scheme.compiler.value.Value
import com.tajpure.scheme.compiler.value.IntValue
import com.tajpure.scheme.compiler.value.Type
import org.jllvm.value.user.constant.ConstantInteger

class IntNum(val content: String, _file: String, _start: Int, _end: Int, _row: Int, _col: Int)
  extends Number(_file, _start, _end, _row, _col) {

  val value: Long = 
    if (content.startsWith("+")) {
      content.substring(1).toLong
    } 
    else if (content.startsWith("-")) {
      - (content.substring(1).toLong)
    }
    else {
      content.toLong
    }
  
  def interp(s: Scope): Value = {
    new IntValue(value)
  }

  def typecheck(s: Scope): Value = {
    Type.INT
  }

  def codegen(s: Scope): org.jllvm.value.Value = {
    ConstantInteger.constI32(value)
  }

  override def toString(): String = {
    "Int=>" + content
  }

}