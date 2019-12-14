
import antlr.generated.ExpressionLexer
import antlr.generated.ExpressionParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import java.io.File

sealed class AnnotationInfo {
    abstract val number: Int
}

class Assumption(override val number: Int) : AnnotationInfo() {
    override fun toString() = "(Предп. $number)"
}

class Axiom(override val number: Int) : AnnotationInfo() {
    override fun toString() = "(Сх. акс. $number)"
}

class MP(override val number: Int, val number2: Int) : AnnotationInfo() {
    override fun toString() = "(M.P. $number, $number2)"
}

class None : AnnotationInfo() {
    override val number = -1
    override fun toString() = "(Не доказано)"
}

class NodeWithIndex(val node: NodeHash, val index: Int) {
    override fun hashCode() = node
    override fun equals(other: Any?) = other is NodeWithIndex && node == other.node
}

typealias Reason = NodeWithIndex

val assumptions = hashSetOf<NodeWithIndex>()
val trueExpressions = hashSetOf<NodeWithIndex>()
val implications = hashMapOf<NodeHash, HashSet<Reason>>()

var expectedFormula: Int = 0

fun String.parser() = ExpressionParser(CommonTokenStream(ExpressionLexer(ANTLRInputStream(this))))

val axioms = File("axioms.txt")
        .readLines()
        .map { it.parser().expression() }

fun processHeader(header: ExpressionParser.HeaderContext) {
    header.assumptionsList()
            ?.expression()
            ?.map(ParseTree::calcHash)
            ?.forEachIndexed { id, ass ->
                assumptions.add(NodeWithIndex(ass, id))
            }

    expectedFormula = header.expression().calcHash()
}

val operators = listOf("!", "->", "|", "&")

fun skipWrappers(expr: ParseTree): ParseTree =
        if (expr.childCount == 1 && expr !is TerminalNode)
            skipWrappers(expr.getChild(0))
        else
            if (expr is ExpressionParser.ParenthesisContext)
                skipWrappers(expr.expression())
            else
                expr

private fun checkAxiom(expr: ExpressionParser.ExpressionContext): AnnotationInfo? {
    val letterToNode = hashMapOf<Char, NodeHash>()
    fun isomorfismCheck(nodeWrapped: ParseTree, expectedNodeWrapped: ParseTree, i: Int): Boolean {
        val node = skipWrappers(nodeWrapped)
        val expectedNode = skipWrappers(expectedNodeWrapped)
        return when (expectedNode) {
            is TerminalNode -> {
                if (node is TerminalNode && expectedNode.text in operators)
                    node.text == expectedNode.text
                else {
                    val letter = expectedNode.text[0]
                    val nodeHash = node.calcHash()
                    if (letterToNode.containsKey(letter))
                        letterToNode[letter] == nodeHash
                    else {
                        letterToNode[letter] = nodeHash
                        true
                    }
                }
            }
            else -> {
                if (expectedNode.childCount != node.childCount) false
                else
                    (0 until node.childCount)
                            .all { isomorfismCheck(node.getChild(it), expectedNode.getChild(it), i) }

            }
        }
    }
    for (i in 0 until axioms.size) {
        letterToNode.clear()
        if (isomorfismCheck(expr, axioms[i], i))
            return Axiom(i + 1)
    }
    return null
}

fun checkModusPonens(expr: ExpressionParser.ExpressionContext): AnnotationInfo? = expr.calcHash().let { nodeHash ->
    if (implications.containsKey(nodeHash)) {
        for (impl in implications[nodeHash]!!) {
            val reason = trueExpressions.find { it == impl }
            if (reason != null)
                return@let MP(impl.index, reason.index)
        }
        return@let null
    }
    return@let null
}

fun checkAssumption(expr: ExpressionParser.ExpressionContext) = assumptions
        .find { it.node == expr.calcHash() }
        ?.let { Assumption(it.index + 1) }

fun setTrue(expr: ExpressionParser.ExpressionContext, index: Int) =
        trueExpressions.add(NodeWithIndex(expr.calcHash(), index))

fun tryAddToImplications(exprWithPar: ExpressionParser.ExpressionContext, index: Int) {
    val expr = skipWrappers(exprWithPar)
    if (expr is ExpressionParser.ExpressionContext && expr.IMPL() != null) {
        val exprHash = expr.getChild(2).calcHash()
        implications.putIfAbsent(exprHash, hashSetOf())
        implications[exprHash]!!.add(Reason(expr.getChild(0).calcHash(), index))
    }
}

fun annotateLine(line: String, index: Int): AnnotationInfo {
    val expr = line.parser().expression()
    val result = checkAssumption(expr) ?: checkAxiom(expr) ?: checkModusPonens(expr)
    setTrue(expr, index)
    tryAddToImplications(expr, index)
    return result ?: None()
}
