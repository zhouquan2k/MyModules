// Generated from /Users/zhouquan/Workspace/MyModules/System/src/main/resources/VueParser.g4 by ANTLR 4.13.1

package com.progartisan.module.uiartisan.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link VueParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface VueParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link VueParser#document}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDocument(VueParser.DocumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#prolog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProlog(VueParser.PrologContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#content}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContent(VueParser.ContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElement(VueParser.ElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReference(VueParser.ReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribute(VueParser.AttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#attributeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeName(VueParser.AttributeNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#sharpName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSharpName(VueParser.SharpNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#eventName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventName(VueParser.EventNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#attributeValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeValue(VueParser.AttributeValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#sharpValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSharpValue(VueParser.SharpValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#eventValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventValue(VueParser.EventValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#chardata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChardata(VueParser.ChardataContext ctx);
	/**
	 * Visit a parse tree produced by {@link VueParser#misc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMisc(VueParser.MiscContext ctx);
}