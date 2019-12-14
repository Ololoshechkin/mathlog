
import antlr.generated.ExpressionParser
import antlr.generated.ExpressionVisitor
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode

typealias NodeHash = Int

object SimplifiedText : ExpressionVisitor<String> {

    override fun visitAssumptionsList(ctx: ExpressionParser.AssumptionsListContext?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitHeader(ctx: ExpressionParser.HeaderContext?) = TODO("no")

    private fun visitBinary(ctx: ParseTree) = listOf(1, 0, 2)
            .map { ctx.getChild(it).accept(this@SimplifiedText) }
            .joinToString(", ", "(", ")")

    private fun visitBinaryOrGetDown(ctx: ParseTree) = when (ctx.childCount) {
        1 -> ctx.getChild(0).accept(this)
        else -> visitBinary(ctx)
    }

    override fun visitExpression(ctx: ExpressionParser.ExpressionContext?) = visitBinaryOrGetDown(ctx!!)

    override fun visitDisjunction(ctx: ExpressionParser.DisjunctionContext?) = visitBinaryOrGetDown(ctx!!)

    override fun visitConjunction(ctx: ExpressionParser.ConjunctionContext?) = visitBinaryOrGetDown(ctx!!)

    override fun visitTerminal(node: TerminalNode?) = node!!.text

    override fun visitNegation(ctx: ExpressionParser.NegationContext?) = when (ctx!!.getChild(0)) {
        is TerminalNode ->  ctx.text
        is ExpressionParser.ParenthesisContext -> visitParenthesis(ctx.parenthesis())
        else -> visitPureNegation(ctx.pureNegation())
    }

    override fun visitParenthesis(ctx: ExpressionParser.ParenthesisContext?) =
            visitExpression(ctx?.expression())

    override fun visitPureNegation(ctx: ExpressionParser.PureNegationContext?) =
            "(${ctx!!.NOT().text}${ctx.negation().accept(this)})"

    override fun visit(tree: ParseTree?) = tree?.accept(this)

    override fun visitChildren(node: RuleNode?) = ""
    override fun visitErrorNode(node: ErrorNode?) = ""

}

fun ParseTree.calcHash(): NodeHash = this.accept(SimplifiedText).hashCode()
