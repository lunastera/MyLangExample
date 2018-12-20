import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.sobreera.myParser.Compiler
import com.github.sobreera.myParser.FunctionDeclaration
import com.github.sobreera.myParser.MyParser

fun main(args: Array<String>) {
    val CODE = """
    func main() {
        puts("Hello, World")
    }
    """.trimIndent()
    val result = MyParser.parseToEnd(CODE)
    println(result)

    Compiler.compile(result)
}