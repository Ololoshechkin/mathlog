// Generated from antlr/generated/Expression.g4 by ANTLR 4.7.1
package antlr.generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExpressionParser}.
 */
public interface ExpressionListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#header}.
	 * @param ctx the parse tree
	 */
	void enterHeader(ExpressionParser.HeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#header}.
	 * @param ctx the parse tree
	 */
	void exitHeader(ExpressionParser.HeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#assumptionsList}.
	 * @param ctx the parse tree
	 */
	void enterAssumptionsList(ExpressionParser.AssumptionsListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#assumptionsList}.
	 * @param ctx the parse tree
	 */
	void exitAssumptionsList(ExpressionParser.AssumptionsListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ExpressionParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ExpressionParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#disjunction}.
	 * @param ctx the parse tree
	 */
	void enterDisjunction(ExpressionParser.DisjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#disjunction}.
	 * @param ctx the parse tree
	 */
	void exitDisjunction(ExpressionParser.DisjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void enterConjunction(ExpressionParser.ConjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void exitConjunction(ExpressionParser.ConjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#negation}.
	 * @param ctx the parse tree
	 */
	void enterNegation(ExpressionParser.NegationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#negation}.
	 * @param ctx the parse tree
	 */
	void exitNegation(ExpressionParser.NegationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#parenthesis}.
	 * @param ctx the parse tree
	 */
	void enterParenthesis(ExpressionParser.ParenthesisContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#parenthesis}.
	 * @param ctx the parse tree
	 */
	void exitParenthesis(ExpressionParser.ParenthesisContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#pureNegation}.
	 * @param ctx the parse tree
	 */
	void enterPureNegation(ExpressionParser.PureNegationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#pureNegation}.
	 * @param ctx the parse tree
	 */
	void exitPureNegation(ExpressionParser.PureNegationContext ctx);
}