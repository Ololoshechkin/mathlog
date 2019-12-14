package hw1

import grammar.*

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

val indexByNode = hashMapOf<Node, Int>()
val indexByImplicationNode = hashMapOf<Node, Int>()
val indexByAssumptionNode = hashMapOf<Node, Int>()

val implications = hashMapOf<Node, HashSet<Implication>>()


val axioms = listOf(
        Implication(Letter("A"), Implication(Letter("B"), Letter("A"))),
        Implication(Implication(Letter("A"), Letter("B")), Implication(Implication(Letter("A"), Implication(Letter("B"), Letter("C"))), Implication(Letter("A"), Letter("C")))),
        Implication(Letter("A"), Implication(Letter("B"), Conjunction(Letter("A"), Letter("B")))),
        Implication(Conjunction(Letter("A"), Letter("B")), Letter("A")),
        Implication(Conjunction(Letter("A"), Letter("B")), Letter("B")),
        Implication(Letter("A"), Disjunction(Letter("A"), Letter("B"))),
        Implication(Letter("B"), Disjunction(Letter("A"), Letter("B"))),
        Implication(Implication(Letter("A"), Letter("C")), Implication(Implication(Letter("B"), Letter("C")), Implication(Disjunction(Letter("A"), Letter("B")), Letter("C")))),
        Implication(Implication(Letter("A"), Letter("B")), Implication(Implication(Letter("A"), Negation(Letter("B"))), Negation(Letter("A")))),
        Implication(Negation(Negation(Letter("A"))), Letter("A"))
)

val letterToNode = hashMapOf<String, Node>()
fun isomorphismCheck(node: Node, expectedNode: Node): Boolean = when (expectedNode) {
    is Letter -> {
        val letter = expectedNode.letter
        if (letterToNode.containsKey(letter))
            letterToNode[letter] == node
        else {
            letterToNode[letter] = node
            true
        }
    }
    is Binary -> node is Binary
            && node.opcode == expectedNode.opcode
            && isomorphismCheck(node.left, expectedNode.left)
            && isomorphismCheck(node.right, expectedNode.right)
    is Negation -> node is Negation && isomorphismCheck(node.child, expectedNode.child)
    else -> false
}

fun checkAxiom(expr: Node): AnnotationInfo? {
    for (i in 0 until axioms.size) {
        letterToNode.clear()
        if (isomorphismCheck(expr, axioms[i]))
            return Axiom(i + 1)
    }
    return null
}


fun checkModusPonens(expr: Node): AnnotationInfo? {
    if (implications.containsKey(expr)) {
        for (impl in implications[expr]!!) {
            val reason = impl.left
            val trueIndex = indexByNode[reason]
            if (trueIndex != null)
                return MP(indexByImplicationNode[impl]!!, trueIndex)
        }
    }
    return null
}

fun checkAssumption(expr: Node) = indexByAssumptionNode[expr]?.let(::Assumption)

fun setTrue(expr: Node, index: Int) {
    indexByNode.putIfAbsent(expr, index)
}

fun tryAddToImplications(expr: Node, index: Int) {
    if (expr is Implication) {
        val consequence = expr.right
        implications.putIfAbsent(consequence, hashSetOf())
        implications[consequence]!!.add(expr)
        indexByImplicationNode[expr] = index
    }
}

fun annotateLine(line: String, index: Int): AnnotationInfo {
    val expr = line.parse()
    val result = checkAxiom(expr) ?: checkAssumption(expr) ?: checkModusPonens(expr)
    setTrue(expr, index)
    tryAddToImplications(expr, index)
    return result ?: None()
}

fun processHeader(header: String) {
    val (assumptions, _) = getHeader(header)
    assumptions.forEachIndexed { id, ass ->
        indexByAssumptionNode[ass] = id + 1
    }
}