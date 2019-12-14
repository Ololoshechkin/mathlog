// Generated from antlr/generated/Expression.g4 by ANTLR 4.7.1
package antlr.generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExpressionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExpressionVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader(ExpressionParser.HeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#assumptionsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssumptionsList(ExpressionParser.AssumptionsListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(ExpressionParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#disjunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDisjunction(ExpressionParser.DisjunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#conjunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConjunction(ExpressionParser.ConjunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#negation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegation(ExpressionParser.NegationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#parenthesis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesis(ExpressionParser.ParenthesisContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#pureNegation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPureNegation(ExpressionParser.PureNegationContext ctx);
}