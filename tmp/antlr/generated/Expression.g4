grammar Expression;

header : assumptionsList? '|-' expression;

assumptionsList: expression (',' expression)*;

expression
    : disjunction
    | disjunction IMPL expression
    ;
disjunction
    : conjunction
    | disjunction OR conjunction
    ;
conjunction
    : negation
    | conjunction AND negation
    ;
negation
    : VARIABLE
    | pureNegation
    | parenthesis
    ;
parenthesis: '(' expression ')';
pureNegation: NOT negation;

IMPL : '->';
OR : '|';
AND : '&';
NOT : '!';
VARIABLE : [A-Z]([A-Z0-9])*;

WS: [ \n\t\r]+ -> skip;
