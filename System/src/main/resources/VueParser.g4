parser grammar VueParser;

@header {
package com.progartisan.module.uiartisan;
}

options {
    tokenVocab = XMLLexer;
}

document
    : prolog? misc* element misc* EOF
    ;

prolog
    : XMLDeclOpen attribute* SPECIAL_CLOSE
    ;

content
    : chardata? ((element | reference | CDATA | PI | COMMENT) chardata?)*
    ;

element
    : '<' Name attribute* '>' content '<' '/' Name '>'
    | '<' Name attribute* '/>'
    ;

reference
    : EntityRef
    | CharRef
    ;



attribute
    : attributeName (EQUALS attributeValue)?
    | SHARP sharpName (EQUALS sharpValue)?
    | AT_SIGN eventName EQUALS eventValue
    ;

attributeName: Name ;
sharpName: Name ;
eventName: Name ;
attributeValue: STRING ;
sharpValue: STRING ;
eventValue: STRING ;

// Our STRING is AttValue in spec

/** ``All text that is not markup constitutes the character data of
 *  the document.''
 */
chardata
    : TEXT
    | SEA_WS
    ;

misc
    : COMMENT
    | PI
    | SEA_WS
    ;