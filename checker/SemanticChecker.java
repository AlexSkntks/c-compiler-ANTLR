package checker;
import java.util.ArrayList;

import org.antlr.v4.runtime.Token;

import parser.CBaseVisitor;
import parser.CParser;
import tables.FuncTable;
import tables.FunctionInfo;
import tables.StrTable;
import tables.VarInfo;
import tables.VarTable;

public class SemanticChecker extends CBaseVisitor<String> {

    private VarTable vt = new VarTable(); //    Tabela de variáveis.
    private StrTable st = new StrTable(); //    Tabela de variáveis.
    private FuncTable ft = new FuncTable(); //  Tabela de funções
    private String lastF;//Auxiliar para o nome da função
   
    private boolean passed = true;
    private boolean isInBlock = false;

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
        // System.out.print("\n\n");
        vt.showTable();
        System.out.println();
        // st.showTable();
        ft.imprime();
    }

    // ^Declaração de função
    //functionDefinition : declarationSpecifiers? declarator declarationList? compoundStatement
    @Override public String visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {

        if(ctx.declarator() != null && ctx.declarationSpecifiers() != null){//?
            
            String nome = visit(ctx.declarator());

            String tipo = ctx.declarationSpecifiers().getText();

            ft.addType(nome, tipo);

            visit(ctx.declarationSpecifiers());

        }
        this.escopoAtual++;
        return new String();
        
    }

    // Esta é uma regra terminal, ela retorna o tipo
    @Override
    public String visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {
        this.type = ctx.getText();
        visitChildren(ctx);
        return ctx.getText();
    }

    //Identifier #varName
	@Override 
    public String visitVarName(CParser.VarNameContext ctx) {
        String nome = ctx.Identifier().getText();
        int escopo = 0;

        if(this.isInBlock){
            escopo = 10;
        }

        VarInfo nv = new VarInfo(nome, this.type, 0, this.escopoAtual);

        vt.insert(nv);

        visitChildren(ctx);
        return ctx.Identifier().getText();
    }

    //!directDeclarator '(' parameterTypeList ')' #funcDeclaration1
	@Override 
    public String visitFuncDeclaration1(CParser.FuncDeclaration1Context ctx) {

        String nome = visit(ctx.directDeclarator());
        
        FunctionInfo nf = new FunctionInfo(nome, "no_type");

        if(!ft.insert(nf)){
            System.out.println("Function: " + nome + " already exists.");
            return null;//Para o processo de análise semântica.
        }
        String params = visit(ctx.parameterTypeList());

        String[] aux = params.split(",");

        ArrayList<String> list = new ArrayList<String>();

        for(int i = 0; i < aux.length; i++){
            list.add(aux[i]);
        }

        ft.addParams(nome, list);

        return nome;
    }

    //pelo que percebi por tentativa e erro, isso identifica uma função sem parâmetros
    //directDeclarator '(' identifierList? ')' #funcDeclaration2
    @Override 
    public String visitFuncDeclaration2(CParser.FuncDeclaration2Context ctx) {  
        
        String nome = visit(ctx.directDeclarator());
        
        FunctionInfo nf = new FunctionInfo(nome, "no_type");

        if(!ft.insert(nf)){
            System.out.println("Function: " + nome + " already exists.");
            return null;//Para o processo de análise semântica.
        }

        return visitChildren(ctx);
    }

    //Coloquei a regra, mas enfim, tem uma lista no nome :)
    //parameterList :  parameterDeclaration (',' parameterDeclaration)*
    @Override 
    public String visitParameterList(CParser.ParameterListContext ctx) {
        String params = new String();
        
        //Cria uma string com os tipos separados por ','
        for(int i = 0; i < ctx.parameterDeclaration().size(); i++){
            //Não deixa ',' sobrando
            if(i == (ctx.parameterDeclaration().size()-1)){
                params += "," + ctx.parameterDeclaration().get(i).declarationSpecifiers().getText();
            }else{
                params += ctx.parameterDeclaration().get(i).declarationSpecifiers().getText();
            }
        }
        visitChildren(ctx);
        return params;
    }

	@Override 
    public String visitBlockItemList(CParser.BlockItemListContext ctx) {
        this.isInBlock = true;
        visitChildren(ctx);
        this.isInBlock = false;
        return new String();
    }


    @Override 
    public String visitStaticAssertDeclaration(CParser.StaticAssertDeclarationContext ctx) {

        return visitChildren(ctx); 
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

}
