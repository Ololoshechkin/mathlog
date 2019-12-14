package tmp.hw1

import grammar.*
import java.io.File

val Node.text: String
    get() = when (this) {
        is Implication -> "(${left.text}->${right.text})"
        is Conjunction -> "(${left.text}&${right.text})"
        is Disjunction -> "(${left.text}|${right.text})"
        is Negation -> "(!${child.text})"
        is Letter -> letter
        else -> ""
    }

fun main(args: Array<String>) {
    File("files/output1.txt").bufferedReader().useLines {
        File("output.txt").bufferedWriter().use { output ->
            val lines = it.filterNot(String::isEmpty)

            for ((i, line) in lines.withIndex()) {
                if (i == 0) {
                    if (processHeader(line, output)) {
                        break
                    }
                } else {
                    if (processLine(line)) {
                        break
                    }
                }
            }
            newProof.map(Node::text).forEach { output.append(it + '\n') }
        }
    }
}