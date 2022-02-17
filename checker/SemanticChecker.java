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

    /** TODO
     * Funções e variáveis estão nas tabelas
     * Tratar InitDeclarator '='
     * 
     */

    // Exibe o conteúdo das tabelas em stdout.
    void printTables() {
        System.out.println();
        vt.showTable();
        System.out.println("--------------------------------");
        System.out.println("Tabela de Funcoes: ");
        // st.showTable();
        ft.imprime();
    }

	// Equivalente a declaration
	@Override public String visitDeclarationVar(CParser.DeclarationVarContext ctx) { 
        
		// String variaveis = visit(ctx.initDeclaratorList());
		// String tipo = ctx.declarationSpecifiers().getText();

		//System.out.println("Tipo " + tipo + " nome " + variaveis);
		return visitChildren(ctx);

	}

	// @Override 
    // public String visitDeclarator(CParser.DeclaratorContext ctx) { 
    //     System.out.println("Direct " + ctx.directDeclarator().getText());
    //     String lol =  visitChildren(ctx);
    //     //System.out.println("Children " + lol);
    //     return lol; 
    // }


	// @Override 
    // public String visitInitDeclaratorList(CParser.InitDeclaratorListContext ctx) { 
    //     visitChildren(ctx);
	// 	String aux = new String();
	// 	for(int i = 0; i < ctx.initDeclarator().size(); i++){
    //         //Não deixa ',' sobrando
    //         // System.out.println(ctx.initDeclarator(i).getText());
    //         // aux += ", " + ctx.initDeclarator(i).getText();
    //         aux += ", " + ctx.initDeclarator().get(i).getText();
    //         System.out.println(ctx.initDeclarator().get(i).getText());
    //     }
	// 	// return visitChildren(ctx); 
		
	// 	return aux;
	// }

    // initDeclarator : declarator ('=' initializer)?
    // É aqui que devemos tratar se a inicialização está correta!!!!!!!!
	@Override 
    public String visitInitDeclarator(CParser.InitDeclaratorContext ctx) {

        String nome = ctx.declarator().getText();
        int escopo = 0;

        if(this.isInBlock){
            escopo = this.escopoAtual;
        }

        VarInfo nv = new VarInfo(nome, this.type, 0, escopo);

        if(!vt.insert(nv)){
            System.out.println("A variável : " + nome + " já foi declarada.");
        }

        visitChildren(ctx);
        //System.out.println("Init declarator [" + nome + "] tipo " + this.type);
		return ctx.declarator().getText();
	}



	@Override 
    public String visitTypedefName(CParser.TypedefNameContext ctx) {
        visitChildren(ctx);

        String nome = ctx.Identifier().getText();
        int escopo = 0;

        if(this.isInBlock){
            escopo = this.escopoAtual;
        }

        VarInfo nv = new VarInfo(nome, this.type, 0, escopo);

        if(!vt.insert(nv)){
            System.out.println("A variável : " + nome + " já foi declarada anteriormente.");
        }
        

        //System.out.println("TypeDefname [" + nome + "] tipo " + this.type);
        return nome;
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
        this.isInBlock = true;
        visit(ctx.compoundStatement());
        this.isInBlock = false;
        return new String();
    }

    // Esta é uma regra terminal, ela retorna o tipo
    @Override
    public String visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {

        //Caso esse tratamento não seja feito. O type é definido como o nome da variável
        if(ctx.typedefName() == null){
            this.type = ctx.getText();
        }
        //this.type = ctx.getText();

        visitChildren(ctx);
        return ctx.getText();
    }

    //directDeclarator '(' parameterTypeList ')' #funcDeclaration1
	@Override 
    public String visitFuncDeclaration1(CParser.FuncDeclaration1Context ctx) {

        String nome = visit(ctx.directDeclarator());
        
        FunctionInfo nf = new FunctionInfo(nome, "no_type");

        if(!ft.insert(nf)){
            System.out.println("A função: " + nome + " já existe.");
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
            System.out.println("A função: " + nome + " já existe.");
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
    public String visitVarName(CParser.VarNameContext ctx) {
        return ctx.Identifier().getText();
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
