package hw2

import grammar.*
import hw1.checkAxiom
import java.io.BufferedWriter
import java.io.File

data class Proof(
        val header: Header,
        val proof: List<Node>
) {
    fun printHeader(output: BufferedWriter) {
        output.append(
                header.assumptions
                        .joinToString(",", "", "|-", transform = text)
                        + header.expression.text() + "\n"
        )
    }

    fun printProof(output: BufferedWriter) {
        proof.forEach { output.append(it.text() + '\n') }
    }


    fun debug(sufix: String) {
        File("files/output$sufix.txt").bufferedWriter().use { out ->
            printHeader(out)
            proof.map(text).forEach {
                out.append(it)
                out.newLine()
            }
        }
    }

}

infix fun Node.impl(other: Node) = Implication(this, other)
infix fun Node.or(other: Node) = Disjunction(this, other)
infix fun Node.and(other: Node) = Conjunction(this, other)
operator fun Node.not() = Negation(this)

var __id__ = 0

fun Proof.transform(): Proof {

//    debug("${__id__++}")

    var A: Node = object : Node(0) {}
    var B: Node
    var expected: Node = A

    val newAssumptions = hashSetOf<Node>()
    val axioms = object : AbstractSet<Node>() {
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

    val previousFormulas = hashSetOf<Node>()
    val reasons = hashMapOf<Node, HashSet<Node>>()

    fun processLine(expr: Node): Boolean {
        val aExpr = A impl expr
        if (when (expr) {
                    A -> {
//                        println("expr : ${expr.text()} == ${A.text()}")
                        proof(
                                A impl (A impl A),
                                A impl (A impl A),
                                (A impl (A impl A)) impl ((A impl ((A impl A) impl A)) impl aExpr),
                                (A impl ((A impl A) impl A)) impl aExpr,
                                (A impl ((A impl A) impl A)),
                                aExpr
                        )
                    }
                    in newAssumptions, in axioms -> {
//                        println("expr : ${expr.text()} - ass(${expr in newAssumptions})/ax(${expr in axioms})")
                        proof(
                                expr,
                                expr impl aExpr,
                                aExpr
                        )
                    }
                    else -> {
//                        println("expr : ${expr.text()} - common case")
                        val reason = reasons[expr]!!.find { it in previousFormulas }!!
                        val impl = reason impl expr
                        proof(
                                (A impl reason) impl ((A impl impl) impl aExpr),
                                (A impl impl) impl aExpr,
                                aExpr
                        )
                    }
                }) {
            return true
        }
//        println("previousFormulas.add")
        previousFormulas.add(expr)
        if (expr is Implication) {
//            println("expr is Implication")
            reasons.putIfAbsent(expr.right, hashSetOf())
            reasons[expr.right]!!.add(expr.left)
        }
        return false
    }

    fun processHeader() {
        B = header.expression
        A = header.assumptions.last()
        expected = A impl B
        newAssumptions.addAll(header.assumptions.dropLast(1))
    }

    processHeader()
    for (line in proof)
        if (processLine(line))
            break

    return Proof(
            Header(
                    newAssumptions.toList(),
                    expected
            ),
            newProof
    )
//            .also {
//                it.debug("${__id__}-trans")
//            }

}