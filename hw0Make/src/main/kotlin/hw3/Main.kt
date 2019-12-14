package hw3

import grammar.*
import hw2.*
import java.io.BufferedWriter
import java.io.File

fun <T> merge(vararg lists: List<T>): List<T> {
    val result = arrayListOf<T>()
    for (list in lists)
        result.addAll(list)
    return result
}

fun Node.substitute(vararg supstitutions: Pair<String, Node>) =
        getNodeMethod(
                implication = ::Implication,
                disjunction = ::Disjunction,
                conjunction = ::Conjunction,
                negation = ::Negation
        ) { letter ->
            supstitutions.find { it.first == letter }!!.second
        }.let {
            this.it()
        }

fun List<Node>.substituteAll(vararg supsts: Pair<String, Node>) = this.map { it.substitute(*supsts) }

fun File.readProof() = this.readLines().map(String::parse)

val controposition = File("textProofs/Contraposition.txt").readProof()
val addDoubleNot = File("textProofs/AddDoubleNot.txt").readProof()
val noNoNotOr = File("textProofs/NoNoNotOr.txt").readProof()

val noNoNotAnd = File("textProofs/NoNoNotAnd.txt").readProof()
val yesNoNotAnd = File("textProofs/YesNoNotAnd.txt").readProof()
val noYesNotAnd = File("textProofs/NoYesNotAnd.txt").readProof()

val yesYesCons = File("textProofs/YesYesCons.txt").readProof()
val noYesCons = File("textProofs/NoYesCons.txt").readProof()
val noNoCons = File("textProofs/NoNoCons.txt").readProof()

val yesNoNotImpl = File("textProofs/YesNoNotCons.txt").readProof()

val aOrNotA = File("textProofs/TertiumNonDatum.txt").readProof()
var proofANA: List<Node> = arrayListOf()

// (!(a->!a)->(a->!a)) -> !(a -> !a) -> !!(a -> !a)
// !(a -> !a) -> !a
fun proofAOrNotA(a: Letter): List<Node> {
    val f1 = a impl (a or !a)
    val f2 = (!a) impl (a or !a)
//    println("f1 = ${f1.text()}")
//    println("f2 = ${f2.text()}")
//    println("f1 == f2 = ${f1 == f2}")
//    println("f1.hash() == f2.hash() = ${f1.hashCode() == f2.hashCode()}")
//    println()
    return merge(
            listOf(
                    f1,
                    f2
            ),
            controposition.substituteAll("A" to a, "B" to (a or !a)),
            controposition.substituteAll("A" to !a, "B" to (a or !a)),
            listOf(
                    !(a or !a) impl !a,
                    !(a or !a) impl !!a
            ),
            aOrNotA.substituteAll("A" to a)
    )
}

val maskToProof = hashMapOf<Int, Proof>()

data class LineProof(val estimation: Boolean, val proof: List<Node>) {
    val expression get() = proof.last()

    companion object {
        val noproof = LineProof(false, listOf())
    }
}

fun Node.withEstimation(est: Boolean) = if (est) this else !this

fun Node.estimated() = withEstimation(this.estimate())

val estimate = getNodeMethod(
        implication = { a, b -> !a || b },
        disjunction = Boolean::or,
        conjunction = Boolean::and,
        negation = Boolean::not,
        letter = { values[it]!! }
)

fun proofBin(
        bin: Binary,
        formula: (a: Boolean, b: Boolean) -> Boolean,
        formulaProofGen: (Boolean, Boolean) -> List<Node>
): LineProof {
    val leftProof = dfsProof(bin.left)
    val rightProof = dfsProof(bin.right)
    val estimation = formula(leftProof.estimation, rightProof.estimation)
    return LineProof(
            estimation = estimation,
            proof = merge(
                    leftProof.proof,
                    rightProof.proof,
                    formulaProofGen(
                            leftProof.estimation,
                            rightProof.estimation
                    ).substituteAll(
                            "A" to bin.left,
                            "B" to bin.right
                    )
            )
    )
}

fun dfsProof(phi: Node): LineProof = when (phi) {
    is Implication -> proofBin(phi, { a, b -> !a || b }, { a, b ->
        if (a && b)
            yesYesCons
        else if (!a && !b)
            noNoCons
        else if (!a && b)
            noYesCons
        else yesNoNotImpl
    })
    is Conjunction -> proofBin(phi, Boolean::and, { a, b ->
        if (a && b)
            listOf(
                    Letter("A") impl (Letter("B") impl (Letter("A") and Letter("B"))),
                    Letter("B") impl (Letter("A") and Letter("B")),
                    Letter("A") and Letter("B")
            )
        else if (!a && b)
            noYesNotAnd
        else if (!b && a)
            yesNoNotAnd
        else
            noNoNotAnd
    })
    is Disjunction -> proofBin(phi, Boolean::or, { a, b ->
        if (a || b)
            listOf(
                    (if (a) Letter("A") else Letter("B")) impl (Letter("A") or Letter("B")),
                    Letter("A") or Letter("B")
            )
        else
            noNoNotOr
    })
    is Negation -> {
        val childProof = dfsProof(phi.child)
        LineProof(
                estimation = !childProof.estimation,
                proof = merge(
                        childProof.proof,
                        addDoubleNot
                                .substituteAll("A" to childProof.expression)
                )
        )
//        if (childProof.expression is Negation)
//            childProof
//        else
//            LineProof(
//                    estimation = !childProof.estimation,
//                    proof = merge(
//                            childProof.proof,
//                            addDoubleNot
//                                    .substituteAll("A" to childProof.expression)
//                    )
//            )
    }
    is Letter -> {
        if (values[phi.letter]!!)
            LineProof(true, listOf(Letter(phi.letter)))
        else
            LineProof(false, listOf(!Letter(phi.letter)))
    }
    else -> LineProof.noproof
}

fun proofFormulaWithFixedValues(assumptionsMask: Int, phi: Node): Proof {
//    println("maskToProof = $assumptionsMask")
    if (!maskToProof.containsKey(assumptionsMask)) {
//        println("maskToProof is unique")
        val assumptions = assumptionsMask.fillValues()
        maskToProof[assumptionsMask] = Proof(
                Header(assumptions, phi),
                merge(dfsProof(phi).proof)
        )
    }
    return maskToProof[assumptionsMask]!!
}

fun proofFormula(phi: Node)
        : Proof {
    for (maskSize in vars.size - 1 downTo 0) {
        for (maskPrefix in 0 until (1 shl maskSize)) {
            val lastVarMask = (1 shl maskSize)
            // [x1]^x1...[x[k-1]]^x[k-1] |- xk -> phi
            val proofTrue = proofFormulaWithFixedValues(
                    maskPrefix + lastVarMask,//merge(assumptions, listOf(lastVar)),
                    phi
            ).also {
                //                println("assumptions : ${it.header.assumptions.map { it.text() }.joinToString(",  ")}")
//                println("expression : ${it.header.expression.text()}")
//                println("proof  : \n${it.proof.map { it.text() }.joinToString("\n", "\n")}")
            }.transform()
            // [x1]^x1...[x[k-1]]^x[k-1] |- !xk ->phi
            val proofFalse = proofFormulaWithFixedValues(
                    maskPrefix,//merge(assumptions, listOf(Negation(lastVar))),
                    phi
            ).transform()
            val assumptions = maskPrefix.fillValues(length = maskSize)
            val lastVar = Letter(vars[maskSize])
            // xk -> phi
            // !xk ->phi
            // (xk -> phi) -> (!xk ->phi) -> (xk || !xk ->phi)
            // (!xk ->phi) -> (xk || !xk ->phi)
            // xk || !xk -> phi
            maskToProof[maskPrefix] = Proof(
                    Header(
                            assumptions = assumptions,
                            expression = phi
                    ),
                    proof = merge(
                            proofFalse.proof,
                            proofTrue.proof,
                            proofANA,
                            listOf(
                                    (lastVar impl phi) impl ((!lastVar impl phi) impl ((lastVar or !lastVar) impl phi)),
                                    (!lastVar impl phi) impl ((lastVar or !lastVar) impl phi),
                                    (lastVar or !lastVar) impl phi,
                                    phi
                            )
                    )
            )
        }
    }
    return maskToProof[0]!!
}

fun solve(header: String, output: BufferedWriter) {
    val (assumptions, expression) = getHeader(header, "|=")
    val nothing = { _: Unit, _: Unit -> }
    val addToVars = getNodeMethod(nothing, nothing, nothing, {}, {
        if (it !in vars)
            vars.add(it)
    })
    assumptions.forEach(addToVars)
    expression.addToVars()
    for (mask in 0 until (1 shl vars.size)) {
        mask.fillValues()
        if (assumptions.all(calc)) {
            if (!expression.calc()) {
                output.append("Высказывание ложно при ${
                values.map { "${it.key}=${if (it.value) "И" else "Л"}" }.joinToString(", ")
                }\n")
                return
            }
        }
    }
    output.append(
            assumptions
                    .joinToString(",", "", "|-", transform = text)
                    + expression.text()
                    + "\n"
    )
    var phi = expression
    for (g in assumptions.reversed())
        phi = g impl phi
    proofANA = merge(*(vars.map(::Letter).map(::proofAOrNotA).toTypedArray()))
    proofFormula(phi).printProof(output)
    for (g in assumptions.reversed())
        output.append("${g.text()}\n")
    for (i in 0 until assumptions.size) {
        phi as Implication
        phi = phi.right
        output.append("${phi.text()}\n")
    }
}

fun main(args: Array<String>) {
//    val a = Letter("B")
//    Proof(Header(listOf(a), a or !a), proofAOrNotA(a)).transform().proof.map(text).forEach(::println)
//    return
    File("output.txt").bufferedWriter().use { output ->
        solve(
                File("input.txt")
                        .bufferedReader()
                        .useLines {
                            it
                                    .filterNot(String::isEmpty)
                                    .first()
                        },
                output
        )
    }
}

// B->((W)->((A)->(B)))  ????
// !B
// !W
// A
// A = true
// B = false
//
// (!A->(B->(W->(A->B))))->((A|!A)->(B->(W->(A->B))))
// (!lastVar impl phi) impl ((lastVar or !lastVar) impl phi),