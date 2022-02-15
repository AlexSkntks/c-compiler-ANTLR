package checker;
import org.antlr.v4.runtime.Token;

import parser.CBaseVisitor;
import parser.CParser;
import tables.StrTable;
import tables.VarInfo;
import tables.VarTable;

public class SemanticChecker extends CBaseVisitor<String> {
    private VarTable vt = new VarTable(); // Tabela de variáveis.
    private StrTable st = new StrTable(); // Tabela de variáveis.

    private boolean passed = true;

    private String type;// Armazenar os tipos das variáveis

    private int escopoAtual = 0;

    void checkVar(Token token) {
        String text = token.getText();
        int line = token.getLine();

        Boolean idx = vt.lookUp(text, this.escopoAtual);

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

        Boolean idx = vt.lookUp(text, this.escopoAtual);//todo oaosaoposoaosasopop

        if (idx == true) {
            System.err.printf(
                    "SEMANTIC _X_ERROR (%d): variable '%s' already exists.\n",
                    line, text);
            passed = false;
            return;
        }

        VarInfo nova = new VarInfo(text, type, line, 0);//todo jakjksaksjajkajs

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
    public String visitForExpression(CParser.ForExpressionContext ctx) {
        visitChildren(ctx);
        return new String();
    }

    //! Declaração de função
    //!functionDefinition : declarationSpecifiers? declarator declarationList? compoundStatement
    @Override public String visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        //System.out.println("Funcao detectada: " + visit(ctx.declarator()) + " - type: " + this.type);
        if(ctx.declarationSpecifiers() != null){
             System.out.println("Funcao detectada: " + visit(ctx.declarator()) + " - type: " + ctx.declarationSpecifiers().getText());
            visit(ctx.declarationSpecifiers());
        }
        this.escopoAtual++;
        return new String();
    }

    @Override
    public String visitUnaryOperator(CParser.UnaryOperatorContext ctx) {
        visitChildren(ctx);
        return new  String();
    }

    @Override
    public String visitMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) {
        visitChildren(ctx);
        return new  String();
    }

    @Override
    public String visitAdditiveExpression(CParser.AdditiveExpressionContext ctx) {
        visitChildren(ctx);
        return new  String();
    }
   
    @Override
    public String visitLogicalAndExpression(CParser.LogicalAndExpressionContext ctx) {
        visitChildren(ctx);
        return new  String();
    }   

    @Override
    public String visitLogicalOrExpression(CParser.LogicalOrExpressionContext ctx) {
        visitChildren(ctx);
        return new  String();
    }
    
    @Override
    public String visitConditionalExpression(CParser.ConditionalExpressionContext ctx) {
        visitChildren(ctx);
        return new  String();
    }
    
    @Override
    public String visitAssignmentExpression(CParser.AssignmentExpressionContext ctx) {
        visitChildren(ctx);
        return new  String();
    }
    
    // Possível tipo composto (Struct)
    @Override
    public String visitStructDeclaration(CParser.StructDeclarationContext ctx) {
        visitChildren(ctx);
        return new  String();
    }
    
    /**
     * declaration : declarationSpecifiers initDeclaratorList? ';'
     */
    // @Override
    // public Void visitDecVar(CParser.DecVarContext ctx){

    //     visit(ctx.declarationSpecifiers());

    //     if(ctx.initDeclaratorList() != null){
    //      visit(ctx.initDeclaratorList());
    //     }
    //     return null;
    // }

    /**
     * declarationSpecifiers : declarationSpecifier+
     */
    // @Override
    // public Void visitDeclarationSpecifiers(CParser.DeclarationSpecifiersContext ctx){
    //     visitChildren(ctx);
        
    //     return null;
    // }

    // Esta é uma regra terminal, ela retorna o tipo
    @Override
    public String visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {
        this.type = ctx.getText();
        visitChildren(ctx);
        return ctx.getText();
    }

    /**
     * initDeclaratorList : initDeclarator (',' initDeclarator)*
     */
    // @Override
    // public Void visitInitDeclaratorList(CParser.InitDeclaratorListContext ctx){
    //     visitChildren(ctx);
    //     return null; 
    // }

    //initDeclarator : declarator ('=' initializer)?
	// @Override
    // public Void visitInitDeclarator(CParser.InitDeclaratorContext ctx) {        
    //     visit(ctx.declarator());

    //     if(ctx.initializer() != null){
    //         visit(ctx.initializer());
    //     }
    //     return null;
    // }

    //declarator : pointer? directDeclarator gccDeclaratorExtension*
    // @Override 
    // public String visitDeclarator(CParser.DeclaratorContext ctx) {
    //     return visitChildren(ctx);
    // }

//    // directDeclarator : Identifier | ... | directDeclarator '(' parameterTypeList ')'
//     @Override 
//     public String visitDirectAbstractDeclarator(CParser.DirectAbstractDeclaratorContext ctx) {
//         //No esquema, tem uma recursão, mas nesse método, quero que retorne apenas
//         //o nome da função, então, não tem visitação dos filhos
        
//         visit(ctx.);
//         return visit(ctx.VarNameContext());
//     }

    //Identifier
	@Override 
    public String visitVarName(CParser.VarNameContext ctx) {
        visitChildren(ctx);
        return ctx.Identifier().getText();
    }

    //directDeclarator '(' parameterTypeList ')' #funcDeclaration1
	@Override 
    public String visitFuncDeclaration1(CParser.FuncDeclaration1Context ctx) {
        visit(ctx.parameterTypeList());
        return visit(ctx.directDeclarator());
    }

    //directDeclarator '(' identifierList? ')' #funcDeclaration2
    @Override 
    public String visitFuncDeclaration2(CParser.FuncDeclaration2Context ctx) {  
        return visit(ctx.directDeclarator());
    }

    // '(' declarator ')'
	@Override public String visitParams(CParser.ParamsContext ctx) {
        return visitChildren(ctx); 
    }


    // Typedef ?
    @Override
    public String visitTypeName(CParser.TypeNameContext ctx) {
        visitChildren(ctx);
        return new  String();
    }

    
    @Override
    public String visitRelationalExpression(CParser.RelationalExpressionContext ctx) {
        visitChildren(ctx);
        return new  String();
    }

   
    @Override
    public String visitEqualityExpression(CParser.EqualityExpressionContext ctx) {
        visitChildren(ctx);
        return new  String();
    }

    
    @Override
    public String visitAssignmentOperator(CParser.AssignmentOperatorContext ctx) {
        visitChildren(ctx);
        return new  String();
    }

    @Override
    public String visitConstantExpression(CParser.ConstantExpressionContext ctx) {
        visitChildren(ctx);
        return new  String();
    }

    @Override
    public String visitPrimaryExpression(CParser.PrimaryExpressionContext ctx) {
        visitChildren(ctx);
        return new  String();
    }

	@Override public String visitParameterList(CParser.ParameterListContext ctx) {
        System.out.println("---------------------------------------------------");
        System.out.println("Lista de parametros: ");
        for(int i = 0; i < ctx.parameterDeclaration().size(); i++){
            System.out.println("Tipo: " + ctx.parameterDeclaration().get(i).declarationSpecifiers().getText() + " - nome: " + ctx.parameterDeclaration().get(i).declarator().getText());
        }
        visitChildren(ctx);
        return new String(); 
    }

}
