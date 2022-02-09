package checker;
import org.antlr.v4.runtime.Token;

import parser.CParser;
import parser.CParser.visitForExpression;
import parser.CParser.visitDeclaration;
import parser.CParser.visitUnaryOperator;
import parser.CParser.visitMultiplicativeExpression;
import parser.CParser.visitAdditiveExpression;
import parser.CParser.visitLogicalAndExpression;
import parser.CParser.visitConditionalExpression;
import parser.CParser.visitAssignmentExpression;
import parser.CParser.visitStructDeclaration;
import parser.CParser.visitDeclaration;
import parser.CParser.visitTypeName;
import parser.CParser.visitRelationalExpression;
import parser.CParser.visitEqualityExpression;
import parser.CParser.visitAssignmentOperator;
import parser.CParser.visitConstantExpression;
import parser.CParser.visitTypeSpecifier;
import parser.CParser.visitPrimaryExpression;
import parser.CBaseVisitor;

import tables.StrTable;
import tables.VarInfo;
import tables.VarTable;

public class SemanticChecker extends CBaseVisitor<Void> {
    private VarTable vt = new VarTable(); // Tabela de variáveis.
    private StrTable st = new StrTable(); // Tabela de variáveis.

    private boolean passed = true;

    private String type;// Armazenar os tipos das variáveis

    void checkVar(Token token) {
        String text = token.getText();
        int line = token.getLine();

        Boolean idx = vt.verifyIfAlreadyExists(text);

        if (idx == false) {
            System.err.printf(
                    "SEMANTIC _Y_ERROR (%d): variable '%s' was not declared.\n",
                    line, text);
            passed = false;
            return;
        }

    }

    void newVar(Token token) {
        String text = token.getText();
        int line = token.getLine();
        Boolean idx = vt.verifyIfAlreadyExists(text);

        if (idx == true) {
            System.err.printf(
                    "SEMANTIC _X_ERROR (%d): variable '%s' already exists.\n",
                    line, text);
            passed = false;
            return;
        }

        VarInfo nova = new VarInfo(text, type, line);

        vt.insert(nova);
    }

    // Exibe o conteúdo das tabelas em stdout.
    void printTables() {
        System.out.print("\n\n");
        vt.showTable();
        System.out.print("\n\n");
        st.showTable();
    }

    /**
     * Loop Exp
     */

    @Override
    public Void visitForExpression(CParser.ForExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitUnaryOperator(CParser.UnaryOperatorContext ctx) {
        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitAdditiveExpression(CParser.AdditiveExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }
   
    @Override
    public Void visitLogicalAndExpression(CParser.LogicalAndExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }   

    @Override
    public Void visitLogicalOrExpression(CParser.LogicalOrExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }
    
    @Override
    public Void visitConditionalExpression(CParser.ConditionalExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }
    
    @Override
    public Void visitAssignmentExpression(CParser.AssignmentExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }
    
    // Possível tipo composto (Struct)
    @Override
    public Void visitStructDeclaration(CParser.StructDeclarationContext ctx) {
        visitChildren(ctx);
        return null;
    }
    
    //! AQUI ----------------
    @Override
    public Void visitDeclaration(CParser.DeclarationContext ctx) {
        visit(ctx.type_spec());
        newVar(ctx.ID().getSymbol());
        visitChildren(ctx);
        return null;
    }

    // Typedef ?
    @Override
    public Void visitTypeName(CParser.TypeNameContext ctx) {
        visitChildren(ctx);
        return null;
    }

    
    @Override
    public Void visitRelationalExpression(CParser.RelationalExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }

   
    @Override
    public Void visitEqualityExpression(CParser.EqualityExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }

    
    @Override
    public Void visitAssignmentOperator(CParser.AssignmentOperatorContext ctx) {
        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitConstantExpression(CParser.ConstantExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }

    //! AAAAAAAAAAAAAAAAAAAA
    @Override
    public Void visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {
        ctx.ID().getSymbol();
        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitPrimaryExpression(CParser.PrimaryExpressionContext ctx) {
        visitChildren(ctx);
        return null;
    }
}
