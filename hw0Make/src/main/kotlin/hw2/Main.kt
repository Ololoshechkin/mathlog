package hw2

import grammar.*
import java.io.File

val text = getNodeMethod(
        implication = { l, r -> "($l->$r)" },
        conjunction = { l, r -> "($l&$r)" },
        disjunction = { l, r -> "($l|$r)" },
        negation = { c -> "(!$c)" },
        letter = { t -> "($t)" }
)

fun main(args: Array<String>) {
    File("input.txt").bufferedReader().useLines {
        File("output.txt").bufferedWriter().use { output ->
            val lines = it.filterNot(String::isEmpty)
            var header: Header? = null
            val proof = arrayListOf<Node>()
            for ((i, line) in lines.withIndex()) {
                if (i == 0) {
                    header = getHeader(line, "|-")
                } else {
                    proof.add(line.parse())
                }
            }
            header!!
            val newProof = Proof(header, proof).transform()
            newProof.printHeader(output)
            newProof.printProof(output)
        }
    }
}