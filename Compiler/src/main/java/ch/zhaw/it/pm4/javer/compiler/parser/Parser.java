package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.CaseLabelAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.LiteralCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.*;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenType;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;
import ch.zhaw.it.pm4.misc.JaverLogger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public class Parser {

    private final List<Token> tokens;
    private final DiagnosticBag diagnosticBag;
    private int currentPosition = 0;

    private static final Set<TokenType> EOF = EnumSet.of(TokenType.SPECIAL_END_OF_FILE);

    private static final Set<TokenType> FIRST_TOP_LEVEL = EnumSet.of(
            TokenType.TYPE_ENUM, TokenType.TYPE_STRUCT, TokenType.KEYWORD_FUNCTION
    );

    private static final Set<TokenType> FIRST_PRIMITIVE_TYPE = EnumSet.of(
            TokenType.TYPE_INTEGER, TokenType.TYPE_DOUBLE, TokenType.TYPE_BOOLEAN,
            TokenType.TYPE_STRING, TokenType.TYPE_CHARACTER
    );

    private static final Set<TokenType> FIRST_TYPE = EnumSet.of(
            TokenType.TYPE_INTEGER, TokenType.TYPE_DOUBLE, TokenType.TYPE_BOOLEAN,
            TokenType.TYPE_STRING, TokenType.TYPE_CHARACTER, TokenType.TYPE_STRUCT, TokenType.TYPE_ENUM
    );

    private static final Set<TokenType> FIRST_RETURN_TYPE = enumSet(FIRST_TYPE, TokenType.TYPE_VOID);

    private static final Set<TokenType> FIRST_ASSIGN_OPERATOR = EnumSet.of(
            TokenType.OPERATOR_ASSIGN, TokenType.OPERATOR_PLUS_ASSIGN, TokenType.OPERATOR_MINUS_ASSIGN,
            TokenType.OPERATOR_MULTIPLY_ASSIGN, TokenType.OPERATOR_DIVIDE_ASSIGN, TokenType.OPERATOR_MODULO_ASSIGN,
            TokenType.OPERATOR_BITWISE_OR_ASSIGN, TokenType.OPERATOR_BITWISE_AND_ASSIGN,
            TokenType.OPERATOR_BITWISE_XOR_ASSIGN, TokenType.OPERATOR_BITSHIFT_LEFT_ASSIGN,
            TokenType.OPERATOR_BITSHIFT_RIGHT_ASSIGN
    );

    private static final Set<TokenType> FIRST_EXPRESSION = EnumSet.of(
            TokenType.OPERATOR_MINUS, TokenType.OPERATOR_DECREMENT, TokenType.OPERATOR_LOGICAL_NOT,
            TokenType.SYMBOL_LEFT_PARENTHESIS, TokenType.OPERATOR_PLUS, TokenType.OPERATOR_INCREMENT,
            TokenType.OPERATOR_BITWISE_NOT, TokenType.LITERAL_BOOLEAN, TokenType.KEYWORD_CALL,
            TokenType.LITERAL_CHAR, TokenType.LITERAL_DOUBLE, TokenType.ID_IDENTIFIER,
            TokenType.LITERAL_INTEGER, TokenType.KEYWORD_NEW, TokenType.LITERAL_NULL, TokenType.LITERAL_STRING
    );

    private static final Set<TokenType> FIRST_INITIALIZER = enumSet(FIRST_EXPRESSION, TokenType.SYMBOL_LEFT_BRACE);

    private static final Set<TokenType> FIRST_STATEMENT = enumSet(FIRST_EXPRESSION,
            TokenType.SYMBOL_LEFT_BRACE, TokenType.KEYWORD_IF, TokenType.KEYWORD_WHILE,
            TokenType.KEYWORD_DO, TokenType.KEYWORD_FOR, TokenType.KEYWORD_SWITCH,
            TokenType.KEYWORD_BREAK, TokenType.KEYWORD_CONTINUE, TokenType.KEYWORD_RETURN, TokenType.KEYWORD_LET
    );

    private static final Set<TokenType> FIRST_CASE = EnumSet.of(TokenType.KEYWORD_CASE, TokenType.KEYWORD_DEFAULT);

    private static final Set<TokenType> FIRST_CASE_LABEL = EnumSet.of(
            TokenType.LITERAL_BOOLEAN, TokenType.LITERAL_CHAR, TokenType.LITERAL_DOUBLE,
            TokenType.ID_IDENTIFIER, TokenType.LITERAL_INTEGER, TokenType.LITERAL_NULL, TokenType.LITERAL_STRING
    );

    private static final Set<TokenType> FOLLOW_TOP_LEVEL = enumSet(FIRST_TOP_LEVEL, TokenType.SPECIAL_END_OF_FILE);
    private static final Set<TokenType> FOLLOW_DECLARATION = FOLLOW_TOP_LEVEL;
    private static final Set<TokenType> FOLLOW_TYPE = EnumSet.of(TokenType.ID_IDENTIFIER);
    private static final Set<TokenType> FOLLOW_RETURN_TYPE = EnumSet.of(TokenType.ID_IDENTIFIER);
    private static final Set<TokenType> FOLLOW_PARAM = EnumSet.of(TokenType.SYMBOL_COMMA, TokenType.SYMBOL_RIGHT_PARENTHESIS);
    private static final Set<TokenType> FOLLOW_STRUCT_FIELD = enumSet(FIRST_TYPE, TokenType.SYMBOL_RIGHT_BRACE);
    private static final Set<TokenType> FOLLOW_ENUM_ITEM = EnumSet.of(TokenType.SYMBOL_COMMA, TokenType.SYMBOL_RIGHT_BRACE);
    private static final Set<TokenType> FOLLOW_BLOCK = enumSet(FIRST_STATEMENT, TokenType.SYMBOL_RIGHT_BRACE, TokenType.KEYWORD_ELSE, TokenType.KEYWORD_CASE, TokenType.KEYWORD_DEFAULT, TokenType.SPECIAL_END_OF_FILE);
    private static final Set<TokenType> FOLLOW_STATEMENT = enumSet(FIRST_STATEMENT, TokenType.SYMBOL_RIGHT_BRACE, TokenType.KEYWORD_ELSE, TokenType.KEYWORD_CASE, TokenType.KEYWORD_DEFAULT, TokenType.SPECIAL_END_OF_FILE);
    private static final Set<TokenType> FOLLOW_EXPRESSION = EnumSet.of(TokenType.SYMBOL_COMMA, TokenType.SYMBOL_SEMICOLON, TokenType.SYMBOL_COLON, TokenType.SYMBOL_RIGHT_PARENTHESIS, TokenType.SYMBOL_RIGHT_BRACKET, TokenType.SYMBOL_RIGHT_BRACE);
    private static final Set<TokenType> FOLLOW_CASE_LABEL = EnumSet.of(TokenType.SYMBOL_COMMA, TokenType.SYMBOL_COLON);
    private static final Set<TokenType> FOLLOW_CASE = EnumSet.of(TokenType.KEYWORD_CASE, TokenType.KEYWORD_DEFAULT, TokenType.SYMBOL_RIGHT_BRACE);
    private static final Set<TokenType> FOLLOW_ARRAY_INIT = enumSet(FOLLOW_EXPRESSION, TokenType.SYMBOL_COMMA);

    private static Set<TokenType> enumSet(Set<TokenType> base, TokenType... additional) {
        Set<TokenType> result = EnumSet.copyOf(base);
        for (TokenType tokenType : additional) result.add(tokenType);
        return result;
    }

    private static Set<TokenType> enumSet(TokenType first, TokenType... rest) {
        Set<TokenType> result = EnumSet.of(first);
        for (TokenType tokenType : rest) result.add(tokenType);
        return result;
    }

    public Parser(List<Token> tokens, DiagnosticBag diagnosticBag) {
        this.tokens = tokens;
        this.diagnosticBag = diagnosticBag;
    }

    public CompilationUnit parse() {
        return parseCompilationUnit();
    }

    private Token currentToken() { return peek(0); }
    private Token lookahead() { return peek(1); }

    private Token peek(int offset) {
        int index = currentPosition + offset;
        if (index < 0) index = 0;
        return index >= tokens.size() ? tokens.getLast() : tokens.get(index);
    }

    private boolean matchCurrentToken(TokenType tokenType) { return currentToken().getTokenType() == tokenType; }
    private boolean matchNextToken(TokenType tokenType) { return lookahead().getTokenType() == tokenType; }
    private boolean currentIs(Set<TokenType> tokenTypes) { return tokenTypes.contains(currentToken().getTokenType()); }
    private boolean isAtEnd() { return matchCurrentToken(TokenType.SPECIAL_END_OF_FILE); }

    private void consumeToken() {
        if (currentPosition < tokens.size()) currentPosition++;
    }

    private Token expectTokenType(TokenType expected) {
        Token token = currentToken();
        if (token.getTokenType() == expected) {
            consumeToken();
            return token;
        }
        reportExpectedToken(expected);
        return new Token(expected, expected.toString(), token.getPosition());
    }

    private Token expectTokenTypes(Set<TokenType> expected) {
        Token token = currentToken();
        if (expected.contains(token.getTokenType())) {
            consumeToken();
            return token;
        }
        reportExpectedTokens(expected);
        TokenType dummy = expected.iterator().next();
        return new Token(dummy, dummy.toString(), token.getPosition());
    }

    private boolean skipErrors(Set<TokenType> first, Set<TokenType> follow, boolean epsilonAllowed) {
        if (currentIs(first)) return true;
        if (epsilonAllowed && currentIs(follow)) return false;
        reportExpectedTokens(first);
        Set<TokenType> sync = enumSet(first, TokenType.SPECIAL_END_OF_FILE);
        sync.addAll(follow);
        while (!currentIs(sync) && !isAtEnd()) consumeToken();
        if (epsilonAllowed && currentIs(follow)) return false;
        return currentIs(first);
    }

    private void reportExpectedToken(TokenType expected) {
        reportExpectedTokens(EnumSet.of(expected));
    }

    private void reportExpectedTokens(Set<TokenType> expected) {
        Token current = currentToken();
        SourceLocation location = current.getPosition();
        String message = String.format("Expected token %s but found %s.", expected, current.getTokenType());
        JaverLogger.error(message);
        diagnosticBag.add(location, Severity.ERROR, message);
    }

    private void reportExpectedTokens(List<TokenType> expected) {
        reportExpectedTokens(EnumSet.copyOf(expected));
    }

    private CompilationUnit parseCompilationUnit() {
        List<DeclarationAstNode> declarations = new ArrayList<>();
        while (!isAtEnd()) {
            if (!skipErrors(FIRST_TOP_LEVEL, EOF, true)) break;
            declarations.add(parseDeclaration());
        }
        expectTokenType(TokenType.SPECIAL_END_OF_FILE);
        return new CompilationUnit(declarations);
    }

    private DeclarationAstNode parseDeclaration() {
        if (!skipErrors(FIRST_TOP_LEVEL, FOLLOW_DECLARATION, false)) return new StructDeclaration("<error>", List.of());
        return switch (currentToken().getTokenType()) {
            case TYPE_ENUM -> parseEnumDeclaration();
            case KEYWORD_FUNCTION -> parseFunctionDeclaration();
            case TYPE_STRUCT -> parseStructDeclaration();
            default -> new StructDeclaration("<error>", List.of());
        };
    }

    private EnumDeclaration parseEnumDeclaration() {
        expectTokenType(TokenType.TYPE_ENUM);
        String name = expectTokenType(TokenType.ID_IDENTIFIER).getValue();
        expectTokenType(TokenType.SYMBOL_LEFT_BRACE);
        List<EnumItem> items = parseEnumItems();
        expectTokenType(TokenType.SYMBOL_RIGHT_BRACE);
        return new EnumDeclaration(name, items);
    }

    private List<EnumItem> parseEnumItems() {
        List<EnumItem> items = new ArrayList<>();
        Set<TokenType> first = EnumSet.of(TokenType.ID_IDENTIFIER);
        while (!matchCurrentToken(TokenType.SYMBOL_RIGHT_BRACE) && !isAtEnd()) {
            if (!skipErrors(first, EnumSet.of(TokenType.SYMBOL_RIGHT_BRACE), true)) break;
            items.add(parseEnumItem());
            if (!matchCurrentToken(TokenType.SYMBOL_COMMA)) break;
            consumeToken();
            if (matchCurrentToken(TokenType.SYMBOL_RIGHT_BRACE)) break;
        }
        return items;
    }

    private EnumItem parseEnumItem() {
        Token name = expectTokenType(TokenType.ID_IDENTIFIER);
        EnumItem.Builder builder = EnumItem.builder(name.getValue());
        if (matchCurrentToken(TokenType.OPERATOR_ASSIGN)) {
            consumeToken();
            Token value = expectTokenType(TokenType.LITERAL_INTEGER);
            builder.value(parseInteger(value));
        }
        skipErrors(FOLLOW_ENUM_ITEM, FOLLOW_ENUM_ITEM, true);
        return builder.build();
    }

    private StructDeclaration parseStructDeclaration() {
        expectTokenType(TokenType.TYPE_STRUCT);
        String name = expectTokenType(TokenType.ID_IDENTIFIER).getValue();
        expectTokenType(TokenType.SYMBOL_LEFT_BRACE);
        List<StructField> fields = parseStructFields();
        expectTokenType(TokenType.SYMBOL_RIGHT_BRACE);
        return new StructDeclaration(name, fields);
    }

    private List<StructField> parseStructFields() {
        List<StructField> fields = new ArrayList<>();
        while (!matchCurrentToken(TokenType.SYMBOL_RIGHT_BRACE) && !isAtEnd()) {
            if (!skipErrors(FIRST_TYPE, EnumSet.of(TokenType.SYMBOL_RIGHT_BRACE), true)) break;
            fields.add(parseStructField());
        }
        return fields;
    }

    private StructField parseStructField() {
        TypeAstNode type = parseType();
        String name = expectTokenType(TokenType.ID_IDENTIFIER).getValue();
        expectTokenType(TokenType.SYMBOL_SEMICOLON);
        skipErrors(FOLLOW_STRUCT_FIELD, FOLLOW_STRUCT_FIELD, true);
        return new StructField(type, name);
    }

    private FunctionDeclaration parseFunctionDeclaration() {
        expectTokenType(TokenType.KEYWORD_FUNCTION);
        TypeAstNode returnType = parseReturnType();
        String functionName = expectTokenType(TokenType.ID_IDENTIFIER).getValue();
        expectTokenType(TokenType.SYMBOL_LEFT_PARENTHESIS);
        List<FunctionParameter> parameters = parseFunctionParameters();
        expectTokenType(TokenType.SYMBOL_RIGHT_PARENTHESIS);
        BlockStatement block = parseBlock();
        return new FunctionDeclaration(returnType, functionName, parameters, block);
    }

    private List<FunctionParameter> parseFunctionParameters() {
        List<FunctionParameter> parameters = new ArrayList<>();
        while (!matchCurrentToken(TokenType.SYMBOL_RIGHT_PARENTHESIS) && !isAtEnd()) {
            if (!skipErrors(FIRST_TYPE, EnumSet.of(TokenType.SYMBOL_RIGHT_PARENTHESIS), true)) break;
            parameters.add(parseFunctionParameter());
            if (!matchCurrentToken(TokenType.SYMBOL_COMMA)) break;
            consumeToken();
            if (matchCurrentToken(TokenType.SYMBOL_RIGHT_PARENTHESIS)) break;
        }
        return parameters;
    }

    private FunctionParameter parseFunctionParameter() {
        TypeAstNode type = parseType();
        String name = expectTokenType(TokenType.ID_IDENTIFIER).getValue();
        skipErrors(FOLLOW_PARAM, FOLLOW_PARAM, true);
        return new FunctionParameter(name, type);
    }

    private TypeAstNode parseReturnType() {
        if (!skipErrors(FIRST_RETURN_TYPE, FOLLOW_RETURN_TYPE, false)) return new VoidType();
        if (matchCurrentToken(TokenType.TYPE_VOID)) return parseVoidType();
        return parseType();
    }

    private TypeAstNode parseType() {
        if (!skipErrors(FIRST_TYPE, FOLLOW_TYPE, false)) return new PrimitiveType(PrimitiveTypeKind.INVALID);
        TypeAstNode type = parseTypeHead();
        while (matchCurrentToken(TokenType.SYMBOL_LEFT_BRACKET)) {
            consumeToken();
            expectTokenType(TokenType.SYMBOL_RIGHT_BRACKET);
            type = new ArrayType(type);
        }
        return type;
    }

    private TypeAstNode parseTypeHead() {
        if (currentIs(FIRST_PRIMITIVE_TYPE)) return parsePrimitiveType();
        return parseNamedType();
    }

    private PrimitiveType parsePrimitiveType() {
        Token token = expectTokenTypes(FIRST_PRIMITIVE_TYPE);
        return new PrimitiveType(toPrimitiveTypeKind(token));
    }

    private NamedType parseNamedType() {
        Token typeToken = expectTokenTypes(EnumSet.of(TokenType.TYPE_STRUCT, TokenType.TYPE_ENUM));
        Token nameToken = expectTokenType(TokenType.ID_IDENTIFIER);
        return new NamedType(toNameTypeKind(typeToken), nameToken.getValue());
    }

    private VoidType parseVoidType() {
        expectTokenType(TokenType.TYPE_VOID);
        return new VoidType();
    }

    private BlockStatement parseBlock() {
        List<StatementAstNode> statements = new ArrayList<>();
        expectTokenType(TokenType.SYMBOL_LEFT_BRACE);
        while (!matchCurrentToken(TokenType.SYMBOL_RIGHT_BRACE) && !isAtEnd()) {
            if (!skipErrors(FIRST_STATEMENT, EnumSet.of(TokenType.SYMBOL_RIGHT_BRACE), true)) break;
            statements.add(parseStatement());
        }
        expectTokenType(TokenType.SYMBOL_RIGHT_BRACE);
        return new BlockStatement(statements);
    }

    private StatementAstNode parseStatement() {
        if (!skipErrors(FIRST_STATEMENT, FOLLOW_STATEMENT, false)) return new BlockStatement(List.of());
        return switch (currentToken().getTokenType()) {
            case SYMBOL_LEFT_BRACE -> parseBlock();
            case KEYWORD_IF -> parseIfStatement();
            case KEYWORD_WHILE -> parseWhileStatement();
            case KEYWORD_DO -> parseDoWhileStatement();
            case KEYWORD_FOR -> parseForStatement();
            case KEYWORD_SWITCH -> parseSwitchStatement();
            case KEYWORD_RETURN, KEYWORD_BREAK, KEYWORD_CONTINUE -> parseJumpStatement();
            case KEYWORD_LET -> parseVarDeclarationStatement(true);
            default -> parseExpressionStatement();
        };
    }

    private ExpressionAstNode parseCheck() {
        expectTokenType(TokenType.SYMBOL_LEFT_PARENTHESIS);
        ExpressionAstNode expression = parseExpression();
        expectTokenType(TokenType.SYMBOL_RIGHT_PARENTHESIS);
        return expression;
    }

    private IfStatement parseIfStatement() {
        expectTokenType(TokenType.KEYWORD_IF);
        ExpressionAstNode condition = parseCheck();
        StatementAstNode thenBranch = parseStatement();
        IfStatement.Builder builder = IfStatement.builder(condition, thenBranch);
        if (matchCurrentToken(TokenType.KEYWORD_ELSE)) {
            consumeToken();
            builder.elseBranch(parseStatement());
        }
        return builder.build();
    }

    private WhileStatement parseWhileStatement() {
        expectTokenType(TokenType.KEYWORD_WHILE);
        ExpressionAstNode condition = parseCheck();
        StatementAstNode body = parseStatement();
        return new WhileStatement(condition, body);
    }

    private DoWhileStatement parseDoWhileStatement() {
        expectTokenType(TokenType.KEYWORD_DO);
        BlockStatement block = parseBlock();
        expectTokenType(TokenType.KEYWORD_WHILE);
        expectTokenType(TokenType.SYMBOL_LEFT_PARENTHESIS);
        ExpressionAstNode condition = parseExpression();
        expectTokenType(TokenType.SYMBOL_RIGHT_PARENTHESIS);
        expectTokenType(TokenType.SYMBOL_SEMICOLON);
        return new DoWhileStatement(condition, block);
    }

    private ForStatement parseForStatement() {
        expectTokenType(TokenType.KEYWORD_FOR);
        expectTokenType(TokenType.SYMBOL_LEFT_PARENTHESIS);
        ForInit init = null;
        if (!matchCurrentToken(TokenType.SYMBOL_SEMICOLON)) init = parseForInit();
        expectTokenType(TokenType.SYMBOL_SEMICOLON);
        ExpressionAstNode condition = null;
        if (!matchCurrentToken(TokenType.SYMBOL_SEMICOLON)) condition = parseExpression();
        expectTokenType(TokenType.SYMBOL_SEMICOLON);
        List<ExpressionAstNode> update = new ArrayList<>();
        if (!matchCurrentToken(TokenType.SYMBOL_RIGHT_PARENTHESIS)) update = parseExpressionList();
        expectTokenType(TokenType.SYMBOL_RIGHT_PARENTHESIS);
        StatementAstNode body = parseStatement();
        return ForStatement.builder(body).forInit(init).condition(condition).update(update).build();
    }

    private ForInit parseForInit() {
        if (matchCurrentToken(TokenType.KEYWORD_LET)) return new ForInitVarDeclaration(parseVarDeclarationStatement(false));
        return new ForInitExpressionList(parseExpressionList());
    }

    private SwitchStatement parseSwitchStatement() {
        expectTokenType(TokenType.KEYWORD_SWITCH);
        ExpressionAstNode condition = parseCheck();
        expectTokenType(TokenType.SYMBOL_LEFT_BRACE);
        List<SwitchCase> cases = parseSwitchCases();
        expectTokenType(TokenType.SYMBOL_RIGHT_BRACE);
        return new SwitchStatement(condition, cases);
    }

    private List<SwitchCase> parseSwitchCases() {
        List<SwitchCase> cases = new ArrayList<>();
        while (!matchCurrentToken(TokenType.SYMBOL_RIGHT_BRACE) && !isAtEnd()) {
            if (!skipErrors(FIRST_CASE, EnumSet.of(TokenType.SYMBOL_RIGHT_BRACE), true)) break;
            cases.add(parseSwitchCase());
        }
        return cases;
    }

    private SwitchCase parseSwitchCase() {
        if (matchCurrentToken(TokenType.KEYWORD_DEFAULT)) {
            consumeToken();
            expectTokenType(TokenType.SYMBOL_COLON);
            return new SwitchCase(true, new ArrayList<>(), parseStatement());
        }
        expectTokenType(TokenType.KEYWORD_CASE);
        List<CaseLabelAstNode> labels = parseCaseLabels();
        expectTokenType(TokenType.SYMBOL_COLON);
        StatementAstNode statement = parseStatement();
        skipErrors(FOLLOW_CASE, FOLLOW_CASE, true);
        return new SwitchCase(false, labels, statement);
    }

    private List<CaseLabelAstNode> parseCaseLabels() {
        List<CaseLabelAstNode> labels = new ArrayList<>();
        while (!matchCurrentToken(TokenType.SYMBOL_COLON) && !isAtEnd()) {
            if (!skipErrors(FIRST_CASE_LABEL, EnumSet.of(TokenType.SYMBOL_COLON), true)) break;
            labels.add(parseCaseLabel());
            if (!matchCurrentToken(TokenType.SYMBOL_COMMA)) break;
            consumeToken();
        }
        return labels;
    }

    private CaseLabelAstNode parseCaseLabel() {
        if (matchCurrentToken(TokenType.ID_IDENTIFIER) && matchNextToken(TokenType.SYMBOL_DOT)) return parseEnumCaseLabel();
        return parseLiteralCaseLabel();
    }

    private LiteralCaseLabel parseLiteralCaseLabel() {
        return new LiteralCaseLabel(parseLiteralExpression());
    }

    private EnumCaseLabel parseEnumCaseLabel() {
        String enumName = expectTokenType(TokenType.ID_IDENTIFIER).getValue();
        expectTokenType(TokenType.SYMBOL_DOT);
        String valueName = expectTokenType(TokenType.ID_IDENTIFIER).getValue();
        return new EnumCaseLabel(enumName, valueName);
    }

    private StatementAstNode parseJumpStatement() {
        if (matchCurrentToken(TokenType.KEYWORD_RETURN)) return parseReturnStatement();
        if (matchCurrentToken(TokenType.KEYWORD_BREAK)) return parseBreakStatement();
        return parseContinueStatement();
    }

    private BreakStatement parseBreakStatement() {
        expectTokenType(TokenType.KEYWORD_BREAK);
        expectTokenType(TokenType.SYMBOL_SEMICOLON);
        return new BreakStatement();
    }

    private ContinueStatement parseContinueStatement() {
        expectTokenType(TokenType.KEYWORD_CONTINUE);
        expectTokenType(TokenType.SYMBOL_SEMICOLON);
        return new ContinueStatement();
    }

    private ReturnStatement parseReturnStatement() {
        expectTokenType(TokenType.KEYWORD_RETURN);
        if (matchCurrentToken(TokenType.SYMBOL_SEMICOLON)) {
            consumeToken();
            return ReturnStatement.builder().build();
        }
        ExpressionAstNode expression = parseExpression();
        expectTokenType(TokenType.SYMBOL_SEMICOLON);
        return ReturnStatement.builder(expression).build();
    }

    private VarDeclarationStatement parseVarDeclarationStatement() {
        return parseVarDeclarationStatement(true);
    }

    private VarDeclarationStatement parseVarDeclarationStatement(boolean expectSemicolon) {
        expectTokenType(TokenType.KEYWORD_LET);
        TypeAstNode type = parseType();
        String name = expectTokenType(TokenType.ID_IDENTIFIER).getValue();
        VarDeclarationStatement.Builder builder = VarDeclarationStatement.builder(type, name);
        if (matchCurrentToken(TokenType.OPERATOR_ASSIGN)) {
            consumeToken();
            builder.initializer(parseVarInitializer());
        }
        if (expectSemicolon) expectTokenType(TokenType.SYMBOL_SEMICOLON);
        return builder.build();
    }

    private StatementAstNode parseExpressionStatement() {
        ExpressionAstNode expression = parseExpression();
        expectTokenType(TokenType.SYMBOL_SEMICOLON);
        return expression;
    }

    private ExpressionAstNode parseVarInitializer() {
        if (!skipErrors(FIRST_INITIALIZER, EnumSet.of(TokenType.SYMBOL_SEMICOLON, TokenType.SYMBOL_COMMA, TokenType.SYMBOL_RIGHT_BRACE), false)) return errorExpression();
        if (matchCurrentToken(TokenType.SYMBOL_LEFT_BRACE)) return parseArrayInitExpression();
        return parseExpression();
    }

    private ArrayInitExpression parseArrayInitExpression() {
        expectTokenType(TokenType.SYMBOL_LEFT_BRACE);
        List<ExpressionAstNode> elements = new ArrayList<>();
        while (!matchCurrentToken(TokenType.SYMBOL_RIGHT_BRACE) && !isAtEnd()) {
            if (!skipErrors(FIRST_INITIALIZER, EnumSet.of(TokenType.SYMBOL_RIGHT_BRACE), true)) break;
            elements.add(parseVarInitializer());
            if (!matchCurrentToken(TokenType.SYMBOL_COMMA)) break;
            consumeToken();
        }
        expectTokenType(TokenType.SYMBOL_RIGHT_BRACE);
        return new ArrayInitExpression(elements);
    }

    private List<ExpressionAstNode> parseExpressionList() {
        List<ExpressionAstNode> expressions = new ArrayList<>();
        expressions.add(parseExpression());
        while (matchCurrentToken(TokenType.SYMBOL_COMMA)) {
            consumeToken();
            expressions.add(parseExpression());
        }
        return expressions;
    }

    private List<ExpressionAstNode> parseOptionalExpressionList() {
        if (!currentIs(FIRST_EXPRESSION)) return new ArrayList<>();
        return parseExpressionList();
    }

    private ExpressionAstNode parseExpression() {
        if (!skipErrors(FIRST_EXPRESSION, FOLLOW_EXPRESSION, false)) return errorExpression();
        return parseAssignment();
    }

    private ExpressionAstNode parseAssignment() {
        ExpressionAstNode left = parseConditional();
        if (currentIs(FIRST_ASSIGN_OPERATOR)) {
            AssignOperator operator = parseAssignOperator();
            ExpressionAstNode value = parseAssignment();
            return AssignExpression.builder(left).operator(operator).value(value).build();
        }
        return left;
    }

    private AssignOperator parseAssignOperator() {
        Token token = expectTokenTypes(FIRST_ASSIGN_OPERATOR);
        return toAssignOperator(token);
    }

    private ExpressionAstNode parseConditional() {
        ExpressionAstNode condition = parseLogicalOr();
        if (matchCurrentToken(TokenType.SYMBOL_QUESTION_MARK)) {
            consumeToken();
            ExpressionAstNode whenTrue = parseExpression();
            expectTokenType(TokenType.SYMBOL_COLON);
            ExpressionAstNode whenFalse = parseConditional();
            return ConditionalExpression.builder(condition).whenTrue(whenTrue).whenFalse(whenFalse).build();
        }
        return condition;
    }

    private ExpressionAstNode parseLogicalOr() { return parseBinaryLeftAssociative(this::parseLogicalAnd, EnumSet.of(TokenType.OPERATOR_OR)); }
    private ExpressionAstNode parseLogicalAnd() { return parseBinaryLeftAssociative(this::parseInclusiveOr, EnumSet.of(TokenType.OPERATOR_AND)); }
    private ExpressionAstNode parseInclusiveOr() { return parseBinaryLeftAssociative(this::parseExclusiveOr, EnumSet.of(TokenType.OPERATOR_BITWISE_OR)); }
    private ExpressionAstNode parseExclusiveOr() { return parseBinaryLeftAssociative(this::parseAndExpression, EnumSet.of(TokenType.OPERATOR_BITWISE_XOR)); }
    private ExpressionAstNode parseAndExpression() { return parseBinaryLeftAssociative(this::parseEqualityExpression, EnumSet.of(TokenType.OPERATOR_BITWISE_AND)); }
    private ExpressionAstNode parseEqualityExpression() { return parseBinaryLeftAssociative(this::parseRelationalExpression, EnumSet.of(TokenType.OPERATOR_EQUALS, TokenType.OPERATOR_NOT_EQUALS)); }
    private ExpressionAstNode parseRelationalExpression() { return parseBinaryLeftAssociative(this::parseShiftExpression, EnumSet.of(TokenType.OPERATOR_LESS_THAN, TokenType.OPERATOR_LESS_EQUAL, TokenType.OPERATOR_GREATER_THAN, TokenType.OPERATOR_GREATER_EQUAL)); }
    private ExpressionAstNode parseShiftExpression() { return parseBinaryLeftAssociative(this::parseAdditiveExpression, EnumSet.of(TokenType.OPERATOR_BITSHIFT_LEFT, TokenType.OPERATOR_BITSHIFT_RIGHT)); }
    private ExpressionAstNode parseAdditiveExpression() { return parseBinaryLeftAssociative(this::parseMultiplicativeExpression, EnumSet.of(TokenType.OPERATOR_PLUS, TokenType.OPERATOR_MINUS)); }
    private ExpressionAstNode parseMultiplicativeExpression() { return parseBinaryLeftAssociative(this::parseUnaryExpression, EnumSet.of(TokenType.OPERATOR_MULTIPLY, TokenType.OPERATOR_DIVIDE, TokenType.OPERATOR_MODULO)); }

    private ExpressionAstNode parseBinaryLeftAssociative(ExpressionParser operandParser, Set<TokenType> operators) {
        ExpressionAstNode left = operandParser.parse();
        while (currentIs(operators)) {
            Token operator = currentToken();
            consumeToken();
            ExpressionAstNode right = operandParser.parse();
            left = new BinaryExpression(toBinaryExpressionKind(operator), left, right);
        }
        return left;
    }

    @FunctionalInterface
    private interface ExpressionParser { ExpressionAstNode parse(); }

    private ExpressionAstNode parseUnaryExpression() {
        Set<TokenType> preOps = EnumSet.of(
                TokenType.OPERATOR_LOGICAL_NOT, TokenType.OPERATOR_BITWISE_NOT,
                TokenType.OPERATOR_PLUS, TokenType.OPERATOR_MINUS,
                TokenType.OPERATOR_DECREMENT, TokenType.OPERATOR_INCREMENT
        );
        if (currentIs(preOps)) {
            Token operator = currentToken();
            consumeToken();
            return new UnaryExpression(toUnaryExpressionKind(operator), parseUnaryExpression());
        }
        return parsePostfixExpression();
    }

    private ExpressionAstNode parsePostfixExpression() {
        ExpressionAstNode expression = parsePrimaryExpression();
        while (true) {
            if (matchCurrentToken(TokenType.SYMBOL_LEFT_BRACKET)) {
                expression = parseIndexExpression(expression);
            } else if (matchCurrentToken(TokenType.SYMBOL_DOT)) {
                expression = parseMemberAccessExpression(expression);
            } else {
                break;
            }
        }
        if (matchCurrentToken(TokenType.OPERATOR_INCREMENT) || matchCurrentToken(TokenType.OPERATOR_DECREMENT)) {
            Token operator = currentToken();
            consumeToken();
            expression = new PostfixExpression(expression, toPostfixOperationKind(operator));
        }
        return expression;
    }

    private ExpressionAstNode parsePrimaryExpression() {
        if (!skipErrors(EnumSet.of(TokenType.SYMBOL_LEFT_PARENTHESIS, TokenType.KEYWORD_CALL, TokenType.LITERAL_BOOLEAN, TokenType.LITERAL_CHAR, TokenType.LITERAL_DOUBLE, TokenType.ID_IDENTIFIER, TokenType.LITERAL_INTEGER, TokenType.KEYWORD_NEW, TokenType.LITERAL_NULL, TokenType.LITERAL_STRING), FOLLOW_EXPRESSION, false)) return errorExpression();
        return switch (currentToken().getTokenType()) {
            case SYMBOL_LEFT_PARENTHESIS -> {
                consumeToken();
                ExpressionAstNode expression = parseExpression();
                expectTokenType(TokenType.SYMBOL_RIGHT_PARENTHESIS);
                yield expression;
            }
            case KEYWORD_CALL -> parseCallExpression();
            case ID_IDENTIFIER -> parseNameExpression();
            case KEYWORD_NEW -> parseNewExpression();
            default -> parseLiteralExpression();
        };
    }

    private ExpressionAstNode parseCallExpression() {
        expectTokenType(TokenType.KEYWORD_CALL);
        String name = expectTokenType(TokenType.ID_IDENTIFIER).getValue();
        expectTokenType(TokenType.SYMBOL_LEFT_PARENTHESIS);
        List<ExpressionAstNode> arguments = parseOptionalExpressionList();
        expectTokenType(TokenType.SYMBOL_RIGHT_PARENTHESIS);
        return new CallExpression(name, arguments);
    }

    private ExpressionAstNode parseNameExpression() {
        return new NameExpression(expectTokenType(TokenType.ID_IDENTIFIER).getValue());
    }

    private IndexExpression parseIndexExpression(ExpressionAstNode target) {
        expectTokenType(TokenType.SYMBOL_LEFT_BRACKET);
        ExpressionAstNode index = parseExpression();
        expectTokenType(TokenType.SYMBOL_RIGHT_BRACKET);
        return new IndexExpression(target, index);
    }

    private MemberAccessExpression parseMemberAccessExpression(ExpressionAstNode target) {
        expectTokenType(TokenType.SYMBOL_DOT);
        String memberName = expectTokenType(TokenType.ID_IDENTIFIER).getValue();
        return new MemberAccessExpression(target, memberName);
    }

    private NewExpression parseNewExpression() {
        expectTokenType(TokenType.KEYWORD_NEW);
        TypeAstNode type = parseTypeHead();
        List<ExpressionAstNode> dimensions = parseNewArrayDimensions();
        NewExpression.Builder builder = NewExpression.builder(type).dimensions(dimensions);
        if (matchCurrentToken(TokenType.SYMBOL_LEFT_BRACE)) builder.arrayInit(parseArrayInitExpression());
        return builder.build();
    }

    private List<ExpressionAstNode> parseNewArrayDimensions() {
        List<ExpressionAstNode> dimensions = new ArrayList<>();
        while (matchCurrentToken(TokenType.SYMBOL_LEFT_BRACKET)) {
            consumeToken();
            dimensions.add(parseExpression());
            expectTokenType(TokenType.SYMBOL_RIGHT_BRACKET);
        }
        return dimensions;
    }

    private LiteralExpression<?> parseLiteralExpression() {
        return switch (currentToken().getTokenType()) {
            case LITERAL_INTEGER, LITERAL_DOUBLE -> parseNumberLiteral();
            case LITERAL_BOOLEAN -> parseBooleanLiteral();
            case LITERAL_STRING -> parseStringLiteral();
            case LITERAL_CHAR -> parseCharLiteral();
            case LITERAL_NULL -> parseNullLiteral();
            default -> {
                reportExpectedTokens(FIRST_CASE_LABEL);
                yield errorExpression();
            }
        };
    }

    private LiteralExpression<?> parseNumberLiteral() {
        if (matchCurrentToken(TokenType.LITERAL_DOUBLE)) {
            Token token = expectTokenType(TokenType.LITERAL_DOUBLE);
            return new LiteralExpression<>(LiteralKind.DOUBLE, parseDouble(token));
        }
        Token token = expectTokenType(TokenType.LITERAL_INTEGER);
        return new LiteralExpression<>(LiteralKind.INT, parseInteger(token));
    }

    private LiteralExpression<Boolean> parseBooleanLiteral() {
        Token token = expectTokenType(TokenType.LITERAL_BOOLEAN);
        return new LiteralExpression<>(LiteralKind.BOOLEAN, Boolean.parseBoolean(token.getValue()));
    }

    private LiteralExpression<String> parseStringLiteral() {
        Token token = expectTokenType(TokenType.LITERAL_STRING);
        return new LiteralExpression<>(LiteralKind.STRING, stripQuotes(token.getValue()));
    }

    private LiteralExpression<Character> parseCharLiteral() {
        Token token = expectTokenType(TokenType.LITERAL_CHAR);
        String value = stripQuotes(token.getValue());
        return new LiteralExpression<>(LiteralKind.CHAR, value.isEmpty() ? '\0' : value.charAt(0));
    }

    private LiteralExpression<Void> parseNullLiteral() {
        expectTokenType(TokenType.LITERAL_NULL);
        return new LiteralExpression<>(LiteralKind.NULL, null);
    }

    private LiteralExpression<Integer> errorExpression() {
        return new LiteralExpression<>(LiteralKind.INT, 0);
    }

    private int parseInteger(Token token) {
        try { return Integer.parseInt(token.getValue()); }
        catch (NumberFormatException ignored) { return 0; }
    }

    private double parseDouble(Token token) {
        try { return Double.parseDouble(token.getValue()); }
        catch (NumberFormatException ignored) { return 0.0; }
    }

    private String stripQuotes(String value) {
        if (value == null || value.length() < 2) return value == null ? "" : value;
        return value.substring(1, value.length() - 1);
    }

    private BinaryExpressionKind toBinaryExpressionKind(Token token) {
        return switch (token.getTokenType()) {
            case OPERATOR_PLUS -> BinaryExpressionKind.ADD;
            case OPERATOR_MINUS -> BinaryExpressionKind.SUBTRACT;
            case OPERATOR_MULTIPLY -> BinaryExpressionKind.MULTIPLY;
            case OPERATOR_DIVIDE -> BinaryExpressionKind.DIVIDE;
            case OPERATOR_MODULO -> BinaryExpressionKind.MODULO;
            case OPERATOR_AND -> BinaryExpressionKind.AND;
            case OPERATOR_OR -> BinaryExpressionKind.OR;
            case OPERATOR_BITWISE_OR -> BinaryExpressionKind.BITWISE_OR;
            case OPERATOR_BITWISE_AND -> BinaryExpressionKind.BITWISE_AND;
            case OPERATOR_BITWISE_XOR -> BinaryExpressionKind.BITWISE_XOR;
            case OPERATOR_EQUALS -> BinaryExpressionKind.EQUALS;
            case OPERATOR_NOT_EQUALS -> BinaryExpressionKind.NOT_EQUALS;
            case OPERATOR_LESS_THAN -> BinaryExpressionKind.LESS;
            case OPERATOR_LESS_EQUAL -> BinaryExpressionKind.LESS_EQUALS;
            case OPERATOR_GREATER_THAN -> BinaryExpressionKind.GREATER;
            case OPERATOR_GREATER_EQUAL -> BinaryExpressionKind.GREATER_EQUALS;
            case OPERATOR_BITSHIFT_LEFT -> BinaryExpressionKind.SHIFT_LEFT;
            case OPERATOR_BITSHIFT_RIGHT -> BinaryExpressionKind.SHIFT_RIGHT;
            default -> BinaryExpressionKind.INVALID;
        };
    }

    private UnaryExpressionKind toUnaryExpressionKind(Token token) {
        return switch (token.getTokenType()) {
            case OPERATOR_PLUS -> UnaryExpressionKind.PLUS;
            case OPERATOR_MINUS -> UnaryExpressionKind.MINUS;
            case OPERATOR_LOGICAL_NOT -> UnaryExpressionKind.LOGICAL_NOT;
            case OPERATOR_BITWISE_NOT -> UnaryExpressionKind.BITWISE_NOT;
            case OPERATOR_INCREMENT -> UnaryExpressionKind.PRE_INCREMENT;
            case OPERATOR_DECREMENT -> UnaryExpressionKind.PRE_DECREMENT;
            default -> UnaryExpressionKind.INVALID;
        };
    }

    private PostfixOperationKind toPostfixOperationKind(Token token) {
        return switch (token.getTokenType()) {
            case OPERATOR_INCREMENT -> PostfixOperationKind.INCREMENT;
            case OPERATOR_DECREMENT -> PostfixOperationKind.DECREMENT;
            default -> PostfixOperationKind.INVALID;
        };
    }

    private AssignOperator toAssignOperator(Token token) {
        return switch (token.getTokenType()) {
            case OPERATOR_ASSIGN -> AssignOperator.ASSIGN;
            case OPERATOR_PLUS_ASSIGN -> AssignOperator.ADD_ASSIGN;
            case OPERATOR_MINUS_ASSIGN -> AssignOperator.SUB_ASSIGN;
            case OPERATOR_MULTIPLY_ASSIGN -> AssignOperator.MUL_ASSIGN;
            case OPERATOR_DIVIDE_ASSIGN -> AssignOperator.DIV_ASSIGN;
            case OPERATOR_MODULO_ASSIGN -> AssignOperator.MOD_ASSIGN;
            case OPERATOR_BITWISE_OR_ASSIGN -> AssignOperator.BITWISE_OR_ASSIGN;
            case OPERATOR_BITWISE_AND_ASSIGN -> AssignOperator.BITWISE_AND_ASSIGN;
            case OPERATOR_BITWISE_XOR_ASSIGN -> AssignOperator.BITWISE_XOR_ASSIGN;
            case OPERATOR_BITSHIFT_LEFT_ASSIGN -> AssignOperator.LEFT_SHIFT_ASSIGN;
            case OPERATOR_BITSHIFT_RIGHT_ASSIGN -> AssignOperator.RIGHT_SHIFT_ASSIGN;
            default -> AssignOperator.INVALID;
        };
    }

    private PrimitiveTypeKind toPrimitiveTypeKind(Token token) {
        return switch (token.getTokenType()) {
            case TYPE_INTEGER -> PrimitiveTypeKind.INT;
            case TYPE_DOUBLE -> PrimitiveTypeKind.DOUBLE;
            case TYPE_BOOLEAN -> PrimitiveTypeKind.BOOL;
            case TYPE_STRING -> PrimitiveTypeKind.STRING;
            case TYPE_CHARACTER -> PrimitiveTypeKind.CHAR;
            default -> PrimitiveTypeKind.INVALID;
        };
    }

    private NameTypeKind toNameTypeKind(Token token) {
        return switch (token.getTokenType()) {
            case TYPE_STRUCT -> NameTypeKind.STRUCT;
            case TYPE_ENUM -> NameTypeKind.ENUM;
            default -> NameTypeKind.INVALID;
        };
    }
}
