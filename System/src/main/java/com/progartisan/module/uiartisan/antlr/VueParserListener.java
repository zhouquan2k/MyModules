// Generated from /Users/zhouquan/Workspace/MyModules/System/src/main/resources/VueParser.g4 by ANTLR 4.13.1

package com.progartisan.module.uiartisan.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link VueParser}.
 */
public interface VueParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link VueParser#document}.
	 * @param ctx the parse tree
	 */
	void enterDocument(VueParser.DocumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#document}.
	 * @param ctx the parse tree
	 */
	void exitDocument(VueParser.DocumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#prolog}.
	 * @param ctx the parse tree
	 */
	void enterProlog(VueParser.PrologContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#prolog}.
	 * @param ctx the parse tree
	 */
	void exitProlog(VueParser.PrologContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#content}.
	 * @param ctx the parse tree
	 */
	void enterContent(VueParser.ContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#content}.
	 * @param ctx the parse tree
	 */
	void exitContent(VueParser.ContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(VueParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(VueParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#reference}.
	 * @param ctx the parse tree
	 */
	void enterReference(VueParser.ReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#reference}.
	 * @param ctx the parse tree
	 */
	void exitReference(VueParser.ReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(VueParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(VueParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#attributeName}.
	 * @param ctx the parse tree
	 */
	void enterAttributeName(VueParser.AttributeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#attributeName}.
	 * @param ctx the parse tree
	 */
	void exitAttributeName(VueParser.AttributeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#sharpName}.
	 * @param ctx the parse tree
	 */
	void enterSharpName(VueParser.SharpNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#sharpName}.
	 * @param ctx the parse tree
	 */
	void exitSharpName(VueParser.SharpNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#eventName}.
	 * @param ctx the parse tree
	 */
	void enterEventName(VueParser.EventNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#eventName}.
	 * @param ctx the parse tree
	 */
	void exitEventName(VueParser.EventNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void enterAttributeValue(VueParser.AttributeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void exitAttributeValue(VueParser.AttributeValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#sharpValue}.
	 * @param ctx the parse tree
	 */
	void enterSharpValue(VueParser.SharpValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#sharpValue}.
	 * @param ctx the parse tree
	 */
	void exitSharpValue(VueParser.SharpValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#eventValue}.
	 * @param ctx the parse tree
	 */
	void enterEventValue(VueParser.EventValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#eventValue}.
	 * @param ctx the parse tree
	 */
	void exitEventValue(VueParser.EventValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#chardata}.
	 * @param ctx the parse tree
	 */
	void enterChardata(VueParser.ChardataContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#chardata}.
	 * @param ctx the parse tree
	 */
	void exitChardata(VueParser.ChardataContext ctx);
	/**
	 * Enter a parse tree produced by {@link VueParser#misc}.
	 * @param ctx the parse tree
	 */
	void enterMisc(VueParser.MiscContext ctx);
	/**
	 * Exit a parse tree produced by {@link VueParser#misc}.
	 * @param ctx the parse tree
	 */
	void exitMisc(VueParser.MiscContext ctx);
}