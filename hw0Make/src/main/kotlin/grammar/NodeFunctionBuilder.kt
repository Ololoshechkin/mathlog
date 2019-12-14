package grammar

fun <T> getNodeMethod(
        implication: (T, T) -> T,
        disjunction: (T, T) -> T,
        conjunction: (T, T) -> T,
        negation: (T) -> T,
        letter: (String) -> T
): Node.() -> T {
    fun Node._f_(): T = when (this) {
        is Implication -> implication(left._f_(), right._f_())
        is Disjunction -> disjunction(left._f_(), right._f_())
        is Conjunction -> conjunction(left._f_(), right._f_())
        is Negation -> negation(child._f_())
        is Letter -> letter(this.letter)
        else -> TODO()
    }
    return Node::_f_
}