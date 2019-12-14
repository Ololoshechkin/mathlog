package hw3

import grammar.*

val vars = arrayListOf<String>()

val values = hashMapOf<String, Boolean>()

fun Int.fillValues(length: Int = vars.size): List<Node> {
    val assumptions = arrayListOf<Node>()
    for (i in 0 until vars.size)
        values[vars[i]] = when (i) {
            in (0 until length) -> {
                val result = (((this shr i) and 1) == 1)
                assumptions.add(if (result) Letter(vars[i]) else Negation(Letter(vars[i])))
                result
            }
            else -> false
        }
    return assumptions
}

val calc = getNodeMethod(
        implication = { a, b -> !a || b },
        disjunction = Boolean::or,
        conjunction = Boolean::and,
        negation = Boolean::not,
        letter = { values[it]!! }
)
