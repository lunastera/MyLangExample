package com.github.sobreera.myParser

sealed class AST

data class Program(val program: List<AST>)
object Skip : AST()

data class Variable(val name: String) : AST()

data class StringNode(val value: String) : AST()
data class Block(val statements: List<AST>) : AST()

open class FunctionDeclaration(open val name: String, val parameters: List<AST>, val body: Block) : AST()
data class UnresolvedFunction(override val name: String, val paramSize: Int) : FunctionDeclaration(name, (0..paramSize).map { Skip }, Block(
    emptyList()))
data class FunctionCall(val functionDeclaration: FunctionDeclaration, val parameters: List<AST>) : AST()