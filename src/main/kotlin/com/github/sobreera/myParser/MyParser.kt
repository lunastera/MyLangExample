package com.github.sobreera.myParser

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.parser.Parser

object MyParser : Grammar<Program>() {
    private val STRING by token("\".*\"")

    private val FUNC by token("func\\b")
    private val LBRC by token("\\{")
    private val RBRC by token("}")
    private val LPAR by token("\\(")
    private val RPAR by token("\\)")

    private val ID by token("[a-zA-Z]\\w*")

    private val COMMA by token(",")
    private val SEMI by token(";")
    private val NL by token("\\n", ignore = true)
    private val WS by token("\\s", ignore = true)

    private val semi by SEMI or NL

    private val stringLiteral by STRING use { StringNode(text.removeSurrounding("\"", "\"")) }
    private val literal: Parser<AST>  by stringLiteral

    private val expression by literal or parser(::block) or parser(::functionCall)
    private val statement by expression * -semi
    private val statements: Parser<List<AST>?> by zeroOrMore(statement)
    private val block by -LBRC * statements * -RBRC map { Block(it ?: emptyList()) }

    private val functionParameters by -LPAR * separatedTerms(ID, COMMA, acceptZero = true) * -RPAR map {
        it.map { v -> Variable(v.text) }
    }
    private val functionCall: Parser<FunctionCall> by ID * -LPAR * separatedTerms(expression, COMMA, acceptZero = true) * -RPAR map { (name, args) ->
        FunctionCall(UnresolvedFunction(name.text, args.size), args)
    }
    private val function by -FUNC * ID * functionParameters * block map { (name, param, block) ->
        FunctionDeclaration(name.text, param, block)
    }

    private val topLevelObject by function
    private val program: Parser<Program> by oneOrMore(topLevelObject) map { Program(it) }

    override val rootParser: Parser<Program> by program
}