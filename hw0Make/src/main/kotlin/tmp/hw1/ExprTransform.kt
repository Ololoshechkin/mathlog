package tmp.hw1

import grammar.Implication
import grammar.Node
import grammar.parse
import hw1.None
import hw1.checkAxiom
import java.io.BufferedWriter

var A = object : Node(0) {}
var B = A
var expected = A

val assumptions = hashSetOf<Node>()
val axioms = object: AbstractSet<Node>() {
    override val size = hw1.axioms.size
    override fun iterator() = TODO("not impl")
    override fun contains(element: Node): Boolean = checkAxiom(element) != null
}

val newProof = arrayListOf<Node>()
val alreadyInProof = hashSetOf<Node>()
fun proof(vararg formulas: Node): Boolean {
    for (f in formulas) {
        if (f !in alreadyInProof) {
            newProof.add(f)
            alreadyInProof.add(f)
            if (f == expected)
                return true
        }
    }
    return false
}

infix fun Node.impl(other: Node) = Implication(this, other)
val previousFormulas = hashSetOf<Node>()
val reasons = hashMapOf<Node, HashSet<Node>>()

fun processLine(line: String): Boolean {
    val expr = line.parse()
    val aExpr = A impl expr
    if (when (expr) {
                A -> proof(
                        A impl (A impl A),
                        (A impl (A impl A)) impl ((A impl ((A impl A) impl A)) impl aExpr),
                        (A impl ((A impl A) impl A)) impl aExpr,
                        (A impl ((A impl A) impl A)),
                        aExpr
                )
                in assumptions, in axioms -> proof(
                        expr,
                        expr impl aExpr,
                        aExpr
                )
                else -> {
                    val reason = reasons[expr]!!.find { it in previousFormulas }!!
                    val impl = reason impl expr
                    proof(
                            (A impl reason) impl ((A impl impl) impl aExpr),
                            (A impl impl) impl aExpr,
                            aExpr
                    )
                }
            })
        return true
    previousFormulas.add(expr)
    if (expr is Implication) {
        reasons.putIfAbsent(expr.right, hashSetOf())
        reasons[expr.right]!!.add(expr.left)
    }
    return false
}

fun processHeader(header: String, output: BufferedWriter): Boolean {
    val assumpsEnd = header.indexOf("|-")
    val assumps = header
            .substring(0, assumpsEnd)
            .split(',')
    val assumptionsList =
            assumps
                    .dropLast(1)
                    .asSequence()
                    .map(String::parse)
    A = assumps.last().parse()
    B = header.substring(assumpsEnd + 2, header.length).parse()
    expected = A impl B
    output.append(assumptionsList.map(Node::text).joinToString(",", "", "|-") + expected.text + "\n")
    for (ass in assumptionsList) {
        if (proof(ass))
            return true
        if (ass is Implication) {
            reasons.putIfAbsent(ass.right, hashSetOf())
            reasons[ass.right]!!.add(ass.left)
        }
    }
    assumptionsList.forEach { assumptions.add(it) }
    return false
}