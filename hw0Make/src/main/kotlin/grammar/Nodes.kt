package grammar

private val hashes = hashMapOf<ArrayList<Int>, Int>()

open class Node(val hash: Int) {

    override fun hashCode() = hash

    override fun equals(other: Any?) = other is Node && hash == other.hash

}

fun calcHash(arr: ArrayList<Int>): Int {
    hashes.putIfAbsent(arr, hashes.size)
    return hashes[arr]!!
}

sealed class Binary(val opcode: Int, val left: Node, val right: Node) : Node(
        hash= calcHash(arrayListOf(opcode, left.hash, right.hash))
)
class Disjunction(left: Node, right: Node) : Binary(1, left, right)
class Conjunction(left: Node, right: Node) : Binary(2, left, right)
class Implication(left: Node, right: Node) : Binary(3, left, right)
class Negation(val child: Node) : Node(hash= calcHash(arrayListOf(4, child.hash)))
class Letter(val letter: String) : Node(hash= calcHash(arrayListOf(letter.hashCode())))