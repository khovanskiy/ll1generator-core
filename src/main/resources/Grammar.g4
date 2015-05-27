grammar Grammar;

@header {
package com.khovanskiy.ll1generator.core;
}

gram : header? members? rule1* ;

header : '@header' JAVA_CODE # headerLabel ;

members : '@members' JAVA_CODE # membersLabel ;

rule1 : NON_TERM_NAME inherited? ('returns' synthesized)? ':' non_term_prod ('|' non_term_prod)* ';' # nonTerminalLabel
      | TERM_NAME ':' term_prod ('|' term_prod)* ';' # terminalLabel ;

inherited : JAVA_CODE ;

synthesized : NON_TERM_NAME | TERM_NAME | MIXED_CASE ;

non_term_prod_helper : NON_TERM_NAME | TERM_NAME ;

non_term_prod : non_term_prod_helper* JAVA_CODE? ;
term_prod : STRING+ ;

NON_TERM_NAME : [a-z] [a-z_0-9]* ;
TERM_NAME : [A-Z] [A-Z_0-9]* ;
MIXED_CASE : [A-Za-z] [a-z_A-A0-9]* ;
JAVA_CODE : '{' (~[{}]+ JAVA_CODE?)* '}' ;
STRING : '\'' (~'\'' | '\\\'')* '\'' ;

WS : [ \t\r\n]+ -> skip ;
