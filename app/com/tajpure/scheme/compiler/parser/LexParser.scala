package com.tajpure.scheme.compiler.parser

import com.tajpure.scheme.compiler.Constants
import com.tajpure.scheme.compiler.ast.Bool
import com.tajpure.scheme.compiler.ast.Delimeter
import com.tajpure.scheme.compiler.ast.FloatNum
import com.tajpure.scheme.compiler.ast.IntNum
import com.tajpure.scheme.compiler.ast.Name
import com.tajpure.scheme.compiler.ast.Node
import com.tajpure.scheme.compiler.ast.Str
import com.tajpure.scheme.compiler.ast.Symbol
import com.tajpure.scheme.compiler.exception.ParserException
import com.tajpure.scheme.compiler.util.FileUtils
import com.tajpure.scheme.compiler.util.Log
import com.tajpure.scheme.compiler.ast.CharNum

class LexParser(_source:String, _path: String) {

  def this(_path: String) {
    this(null, _path)
  }
  
  var offset: Int = 0
  
  var row: Int = 0
  
  var col: Int = 0

  val source: String = _source match {
    case null => FileUtils.read(_path)
    case "" => FileUtils.read(_path)
    case default => _source
  }
  
  val file: String = FileUtils.unifyPath(_path)

  if (source == null) {
    Log.error("failed to read the file:" + file)
  }
  
  Delimeter.addDelimiterPair(Constants.PAREN_BEGIN, Constants.PAREN_END)
  
  def forward() {
    if (source.charAt(offset) != '\n') {
      col += 1
    } else {
      row += 1
      col = 0
    }
    offset += 1
  }

  def skip(n: Int) {
    (1 to n).foreach( _ => forward())
  }

  def skipSpacesAndTab(): Boolean = {
    if (offset < source.length && (source.charAt(offset) == ' ' || source.charAt(offset) == '\t')) {
      skip(1)
      skipSpacesAndTab()
      true
    } 
    else {
      false  
    }
  }

  def skipComments(): Boolean = {
    if (source.startsWith(Constants.COMMENTS, offset)) {
      while (offset < source.length && source.charAt(offset) != '\n') {
        skip(1)
      }
      if (offset < source.length) {
         forward();
      }
      true
    }
    else {
      false
    }
  }
  
  def skipEnter(): Boolean = {
    if (offset < source.length && (source.charAt(offset) == '\r' || source.charAt(offset) == '\n')) {
      forward()
      true
    } else {
      false
    }
  }

  def skipSpacesAndComments() {
    if (skipSpacesAndTab() || skipComments() || skipEnter()) {
      skipSpacesAndComments()
    }
  }

  def scanString(): Node = {
    val start: Int = offset
    val startRow: Int = row
    val startCol: Int = col
    skip(Constants.STRING_BEGIN.length())

    def loop() {
      if (offset >= source.length() || source.charAt(offset) == '\n') {
        throw new ParserException("string format error:", startRow, startCol, offset);
      } 
      else if (source.startsWith(Constants.STRING_END, offset)) {
        skip(Constants.STRING_END.length());
      } 
      else {
        forward()
        loop()
      }
    }
    loop()
    
    val end: Int = offset
    val content: String = source.substring(start + Constants.STRING_BEGIN.length(), end - Constants.STRING_END.length())
    new Str(content, file, start, end, row, col)
  }
  
  def scanChar(): Node = {
    val start: Int = offset
    val startRow: Int = row
    val startCol: Int = col
    skip(Constants.CHAR_PREFIX.length())
    
    if (source.charAt(offset) == ' ') {
      skip(1)
    }
    else if (Character.isLetter(source.charAt(offset))) {
      def loop() {
        if (Character.isLetter(source.charAt(offset))) {
          skip(1)
          loop()
        }
      }
      loop()
    }
    else {
      throw new ParserException("character can't be null", startRow, startCol, offset)
    }
    
    val end: Int = offset
    val content: String = source.substring(start, end)
    new CharNum(content, file, start, end, row, col)
  }

  def isNumberOrChar(ch: Char): Boolean = {
    Character.isLetterOrDigit(ch) || ch == '.' || ch == '+' || ch == '-'
  }

  def scanNumber(): Node = {
    val start: Int = offset
    val startRow: Int = row
    val startCol: Int = col
    var isInt: Boolean = true

    def loop() {
      if (offset > source.length()) {
        throw new ParserException("number format error", startRow, startCol, offset)
      }
      else if (offset == source.length()) {
        // avoid string index out of range
      }
      else if (isNumberOrChar(source.charAt(offset))) {
          if (source.charAt(offset) == '.') {
            isInt = false
          }
        forward()
        loop()
      }
    }
    loop()
    
    val end: Int = offset
    val content: String = source.substring(start, end)
    if (isInt) {
      new IntNum(content, file, start, end, startRow, startCol)
    } 
    else {
      new FloatNum(content, file, start, end, startRow, startCol)
    }
  }
  
  def scanSymbol(): Node = {
    val start: Int = offset
    val startRow: Int = row
    val startCol: Int = col
    
    def loop() {
      if (offset < source.length && (source.charAt(offset) == Constants.QUOTE)) {
        skip(1)
        loop()
      }
      else if (offset < source.length && source.startsWith(Constants._QUOTE, offset)) {
        skip(Constants._QUOTE.length())
        loop()
      }
    }
    loop()
    
    val end: Int = offset
    val content: String = source.substring(start, end)
    new Symbol(content, file, start, end, row, col)
  }
  
  def isIdentifierChar(ch: Char): Boolean = {
    Character.isLetterOrDigit(ch) || Constants.IDENT_CHARS.contains(ch)
  }

  def scanName(): Node = {
    val start: Int = offset
    val startRow: Int = row
    val startCol: Int = col

    def loop() {
      if (offset < source.length && isIdentifierChar(source.charAt(offset))) {
        forward()
        loop()
      }
    }
    loop()

    val content = source.substring(start, offset)
    new Name(content, file, start, offset, startRow, startCol)
  }

  @throws(classOf[ParserException])
  def nextToken(): Node = {

    skipSpacesAndComments()

    if (offset >= source.length()) {
      null
    } 
    else {
      val cur = source.charAt(offset)
      if (Delimeter.isDelimiter(cur)) {
        val ret: Node = new Delimeter(Character.toString(cur), file, offset, offset + 1, row, col)
        forward()
        ret
      } 
      else if (source.startsWith(Constants.STRING_BEGIN, offset)) {
        scanString()
      } 
      else if (source.charAt(offset) == Constants.QUOTE || source.startsWith(Constants._QUOTE, offset)) {
        scanSymbol()
      }
      else if (Character.isDigit(source.charAt(offset)) ||
        ((source.charAt(offset) == '+' || source.charAt(offset) == '-') 
        && offset + 1 < source.length() && Character.isDigit(source.charAt(offset + 1)))) {
        scanNumber()
      } 
      else if (source.startsWith(Constants.CHAR_PREFIX, offset)) {
        scanChar()
      }
      else if (isIdentifierChar(source.charAt(offset))) {
        scanName()
      }
      else {
        throw new ParserException("unrecognized syntax: " + source.substring(offset, offset + 1),
                  row, col, offset)
      }
    }
  }
  
}

object LexParser extends App {
  
  def lex(source: String): String = {
     val lexer: LexParser = new LexParser(source, "/visual")
     
     println(source)
     var tokens: List[Node] = List[Node]()
    
     var n: Node = lexer.nextToken()
    
     def loop() {
       if (n != null) {
         tokens  = tokens :+ n
         try {
           n = lexer.nextToken()
           loop()
         } 
         catch {
           case pe: ParserException => Log.error(pe.toString())
           case e: Exception => Log.error(e.toString())
         }
       }
     }
     loop()
     tokens.foldLeft("")((result, token) => {
       result + token + "\n"
     })
  }
  
  println(lex("(define x 1)"))
  
}