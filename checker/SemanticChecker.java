package checker;
import java.util.ArrayList;

import javax.sound.midi.SysexMessage;

import org.antlr.v4.runtime.Token;

import parser.CBaseVisitor;
import parser.CParser;
import tables.FuncTable;
import tables.FunctionInfo;
import tables.StrTable;
import tables.VarInfo;
import tables.VarTable;
import ast.AST;
import ast.NodeKind;

public class SemanticChecker extends CBaseVisitor<AST> {

    private VarTable vt = new VarTable(); //    Tabela de variáveis.
    private StrTable st = new StrTable(); //    Tabela de variáveis.
    private FuncTable ft = new FuncTable(); //  Tabela de funções
    private String lastF;//Auxiliar para o nome da função

    private boolean passed = true;
    private boolean isInBlock = false;

    private String type;// Armazenar os tipos das variáveis
    private int line;
    private int lineFunc;

    private int escopoAtual = 0;

    /**
     * ~ TODOS
     * *Funções e variáveis estão nas tabelas
     * *Permite declarar variáveis em escopo diferente
     * *Imprime o número da linha da variável
     * *Tratar manipulação de variáveis
     * * Chamadas de função, verificação de parâmetros <----Isso não tem!!!
     *
     * ^Tratar InitDeclarator '='   "ERROR em Downcast"
     *      ? Atribuição simples
     *      ? Atribuição com operações artméticas
     *
     * ^Uma função de IO -> Função padrão na tabela de Funções
     *
     * ! Uma declaração e manipulação de tipo composto
     * ! Um loop
     * todo Tratar operadores Binários (Lógicos e/ou aritméticos)    "O que é booleano? É de comer?"
     * todo Funções n recebem outras funções nos argumentos
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


	// @Override
    // public String visitDeclarator(CParser.DeclaratorContext ctx) {
    //     System.out.println("Direct " + ctx.directDeclarator().getText());
    //     String lol =  visitChildren(ctx);
    //     //System.out.println("Children " + lol);
    //     return lol;
    // }


    //initDeclaratorList : initDeclarator (',' initDeclarator)*
	// @Override
    // public String visitInitDeclaratorList(CParser.InitDeclaratorListContext ctx) {
    //     if(ctx.Comma(0) != null){
    //         this.line = ctx.Comma(0).getSymbol().getLine();
    //     }

	// 	return visitChildren(ctx);
	// }

    // initDeclarator : declarator ('=' initializer)?
    //! É aqui que devemos tratar se a inicialização está correta!!!!!!!!
	// @Override
    // public String visitInitDeclarator(CParser.InitDeclaratorContext ctx) {

    //     String nome = ctx.declarator().getText();
    //     String typeAtt = visit(ctx.initializer()
    //     int escopo = 0;
    //     VarInfo nv;

    //     if(this.isInBlock){
    //         escopo = this.escopoAtual;
    //     }

    //     if(ctx.Assign() != null){
    //         this.line = ctx.Assign().getSymbol().getLine();

    //         nv = new VarInfo(nome, this.type, this.line, escopo, null);

    //         System.out.println("A variavel " + nome + " esta recebendo " + typeAtt);
    //         int x = 's';

    //         float z = 3;
    //         float z = 'b';

    //         if(this.type == "float"){
    //             if(typeAtt == "int"){
    //                 //Nó de conversão int2Float
    //             }
    //             if(typeAtt == "char"){
    //                 //char2Int
    //                 //Int2Float
    //             }
    //         }
    //         if(this.type == "int"){
    //             if(typeAtt == "char"){
    //                 //char2Int
    //             }
    //         }

    //         if(!vt.insert(nv)){
    //             System.out.println("A variável : " + nome + " já foi declarada.");
    //         }

    //     }else{
    //         nv = new VarInfo(nome, this.type, this.line, escopo, null);
    //         if(!vt.insert(nv)){
    //             System.out.println("A variável : " + nome + " já foi declarada.");
    //         }
    //     }

	// 	return ctx.declarator().getText();
	// }


    // Por algum motivo o retorno que definimos na função additiveExpression Não alcança o Itializer;
    // por isso fizemos o Overrride de TODAS as funções intermediárias até alnaçar o Initializaer, e assim
    // acessar os tipos corretamente.
    // @Override public String visitInitializer(CParser.InitializerContext ctx) { return visitChildren(ctx);}
    // @Override public String visitAssignmentExpression(CParser.AssignmentExpressionContext ctx) { return visitChildren(ctx); }
    // @Override public String visitConditionalExpression(CParser.ConditionalExpressionContext ctx) { return visitChildren(ctx); }
    // @Override public String visitLogicalOrExpression(CParser.LogicalOrExpressionContext ctx) { return visitChildren(ctx); }
    // @Override public String visitLogicalAndExpression(CParser.LogicalAndExpressionContext ctx) { return visitChildren(ctx); }
    // @Override public String visitInclusiveOrExpression(CParser.InclusiveOrExpressionContext ctx) { return visitChildren(ctx); }
    // @Override public String visitExclusiveOrExpression(CParser.ExclusiveOrExpressionContext ctx) { return visitChildren(ctx); }
    // @Override public String visitAndExpression(CParser.AndExpressionContext ctx) { return visitChildren(ctx); }
    // @Override public String visitEqualityExpression(CParser.EqualityExpressionContext ctx) { return visitChildren(ctx); }
    // @Override public String visitRelationalExpression(CParser.RelationalExpressionContext ctx) { return visitChildren(ctx); }
    // @Override public String visitShiftExpression(CParser.ShiftExpressionContext ctx) { return visitChildren(ctx); }

    //TODO ir desecebdo até encontrar onde retorna
    //additiveExpression :  multiplicativeExpression (('+'|'-') multiplicativeExpression)*
	// @Override
    // public String visitAdditiveExpression(CParser.AdditiveExpressionContext ctx) {


    //     String tipo = this.type;
    //     String nome;

    //     if(ctx.Plus() == null){
    //         //System.out.println(ctx.multiplicativeExpression(1).getText());

    //     }

    //     String bigger_typer = new String();
    //     String bigger_type_aux = new String();

    //     for(int i = 0; i < ctx.multiplicativeExpression().size(); i++){
    //         bigger_type_aux = visit(ctx.multiplicativeExpression(i));

    //         if(bigger_type_aux.equals("float")){
    //             bigger_typer = "float";
    //         } else if(bigger_type_aux.equals("int") && !bigger_typer.equals("float")){
    //             bigger_typer = "int";
    //         } else if(bigger_type_aux.equals("char") && !bigger_typer.equals("float") && !bigger_typer.equals("int")){
    //             bigger_typer = "char";
    //         }

    //         if(i == ctx.multiplicativeExpression().size()-1){
    //             System.out.print(" (" + bigger_type_aux + ") ");
    //         }else{
    //             System.out.print( " (" + bigger_type_aux + ") " + " op ");
    //         }

    //     }

    //     System.out.println();
    //     // System.out.println("Result of multi: " + bigger_typer);
    //     // System.out.println();

    //     this.type = tipo;

    //     return bigger_typer;
    // }


    //multiplicativeExpression : castExpression (('*'|'/'|'%') castExpression)*
	@Override
    public AST visitMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) {

        // visitChildren(ctx);
        // String a = visit(ctx.castExpression(0));
        AST node;
        // System.out.println("Teste unary: " + a);
        String bigger_typer = new String();
        String bigger_type_aux = new String();

        ArrayList<AST> listTypes = new ArrayList<AST>();
        AST aux;
        // System.out.println("\nImprimindo expMult: ");
        for(int i = 0; i < ctx.castExpression().size(); i++){

            aux = visit(ctx.castExpression(i));

            bigger_type_aux = aux.getText();

            if(bigger_type_aux.equals("float")){
                bigger_typer = "float";
            } else if(bigger_type_aux.equals("int") && !bigger_typer.equals("float")){
                bigger_typer = "int";
            } else if(bigger_type_aux.equals("char") && !bigger_typer.equals("float") && !bigger_typer.equals("int")){
                bigger_typer = "char";
            }

            listTypes.add(aux);
        }

        AST multOP = new AST(NodeKind.TIMES_NODE);
        for(int i = 0; i < ctx.castExpression().size(); i++){
            
            multOP.addChild(listTypes.get(i));
        }

        // System.out.println();
        // System.out.println("Result of multi: " + bigger_typer);
        // System.out.println();

        AST.printDot(multOP);
        return new AST(NodeKind.NULL_NODE);
    }

    /**
     *
     * postfixExpression
    :
    (   primaryExpression
    |   '__extension__'? '(' typeName ')' '{' initializerList ','? '}'
    )
    ('[' expression ']'
    | '(' argumentExpressionList? ')'
    | ('.' | '->') Identifier
    | ('++' | '--')
    )*
     */
    // @Override
    // public String visitPostfixExpression(CParser.PostfixExpressionContext ctx) {

    //     String name = ctx.primaryExpression().getText();

    //     //Verifica se o retorno foi uma função
    //     if(!ctx.LeftParen().isEmpty()){

    //         if(!ft.verifyIfAlreadyExists(name)){
    //             System.out.println("Simbolo " + name + " nao encontrado.");
    //             return "no_type";
    //         }

    //         String argumentList[] = ctx.argumentExpressionList(0).getText().split(",");

    //         //Posteriormente tratar os tipos

    //         // for(int i = 0; i < aux.length; i++){
    //         //     System.out.println(aux[i]);
    //         // }

    //         //System.out.println(ft.getListSize(name) + " , " + ctx.argumentExpressionList().size());

    //         //Verifica se o tamanho da lista argumentos é coerente
    //         if(ft.getListSize(name) == argumentList.length){
    //             return ft.getType(name);
    //         }else{
    //             System.out.println("Funcao " + name + " tem argumentos demais.");
    //             return "no_type";
    //         }

    //     } else {
    //         String aux = this.type;

    //         String aux1 = visitChildren(ctx);

    //         // System.out.println("argumentExpressionList: "+aux1);

    //         // String s = ctx.argumentExpressionList(0).getText();
    //         // System.out.println("Test argument List: "+s);

    //         if(this.type.equals("NAME")){//Variável
    //             // Escopo atual = 0 siginifica que a variável está fora
    //             // de escopo, ou seja fora de uma função.
    //             int scopo = 0;
    //             // Caso ela esteja em uma função, ou bloco (inBlock),
    //             // ela terá escopo diferente de 0
    //             if(this.isInBlock == true){
    //                 scopo = this.escopoAtual;
    //             }
    //             if(vt.lookUp(name, scopo)){
    //                 //System.out.println("Variavel " + name + " esta ok");
    //                 return vt.getType(name, scopo);
    //             }else{
    //                 System.out.println("Simbolo " + name + " nao encontrado");
    //                 return "no_type";
    //             }

    //         }
    //         //Valor constante
    //         this.type = aux;
    //         return aux1;
    //     }
    // }

	@Override
    public AST visitConstantExpression(CParser.ConstantExpressionContext ctx) {
        System.out.println("CONSTANTEXP " + ctx.getText());
        return visitChildren(ctx);
    }

    // primaryExpression
    // :   Identifier
    // |   Constant
    // |   StringLiteral+
    // |   '(' expression ')'
    // |   genericSelection
    // |   '__extension__'? '(' compoundStatement ')' // Blocks (GCC extension)
    // |   '__builtin_va_arg' '(' unaryExpression ',' typeName ')'
    // |   '__builtin_offsetof' '(' typeName ',' unaryExpression ')'
	@Override
    public AST visitPrimaryExpression(CParser.PrimaryExpressionContext ctx) {
        AST null_node;
        null_node = new AST(NodeKind.NULL_NODE);

        if(ctx.Identifier() != null){
            this.type = "NAME";
            String name = ctx.Identifier().getText();

            null_node.addInfo(name);

            return null_node;
        }
        else if(ctx.Constant() != null){
            this.type = "CONST";
            AST node;

            String value = ctx.Constant().getText();

            String type = const_type_checker(value);

            switch (type) {
                case "int":
                    node = new AST(NodeKind.INT_VAL_NODE, Integer.valueOf(value));
                    break;
                case "float":
                    node = new AST(NodeKind.FLOAT_VAL_NODE, Float.valueOf(value));
                case "char":
                    node = new AST(NodeKind.CHAR_VAL_NODE, value.charAt(0));
                default:
                    node = new AST(NodeKind.NULL_NODE);
                    node.addInfo(null);
            }

            return null_node;
        }
        return visitChildren(ctx);
    }

    // Isso aqui converte as constantes para tipo.
    // O tipo é retornado em String.
    public String const_type_checker(String s){
        if(s.contains("\'")){
            // System.out.println(s+"=char");
            return "char";
        }else if(s.contains(".")){
            // System.out.println(s+"=float");
            return "float";
        } else{
            // System.out.println(s+"=int");
            return "int";
        }
    }


	// @Override
    // public String visitTypedefName(CParser.TypedefNameContext ctx) {
    //     visitChildren(ctx);

    //     String nome = ctx.Identifier().getText();
    //     int escopo = 0;

    //     if(this.isInBlock){
    //         escopo = this.escopoAtual;
    //     }

    //     VarInfo nv = new VarInfo(nome, this.type, ctx.Identifier().getSymbol().getLine(), escopo, null);

    //     if(!vt.insert(nv)){
    //         System.out.println("A variável : " + nome + " já foi declarada anteriormente.");
    //     }

    //     return nome;
    // }

    //functionDefinition : declarationSpecifiers? declarator declarationList? compoundStatement
    // @Override public String visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {

    //     if(ctx.declarator() != null && ctx.declarationSpecifiers() != null){

    //         String nome = visit(ctx.declarator());

    //         String tipo = ctx.declarationSpecifiers().getText();

    //         ft.addType(nome, tipo);

    //         visit(ctx.declarationSpecifiers());

    //     }

    //     this.escopoAtual++;
    //     this.isInBlock = true;
    //     visit(ctx.compoundStatement());
    //     this.isInBlock = false;
    //     return new String();
    // }

    // Esta é uma regra terminal, ela retorna o tipo
    // @Override
    // public String visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {

    //     //Caso esse tratamento não seja feito. O type é definido como o nome da variável
    //     if(ctx.typedefName() == null){
    //         this.type = ctx.getText();//!Switch case
    //     }
    //     //this.type = ctx.getText();

    //     visitChildren(ctx);
    //     return ctx.getText();
    // }

    //directDeclarator '(' parameterTypeList ')' #funcDeclaration1
	// @Override
    // public String visitFuncDeclaration1(CParser.FuncDeclaration1Context ctx) {

    //     String nome = visit(ctx.directDeclarator());

    //     FunctionInfo nf = new FunctionInfo(nome, "no_type");

    //     if(!ft.insert(nf)){
    //         System.out.println("A função: " + nome + " já existe.");
    //         return null;//Para o processo de análise semântica.
    //     }

    //     String params = visit(ctx.parameterTypeList());

    //     String[] aux = params.split(",");
    //     System.out.println("params: " + params);

    //     ArrayList<String> list = new ArrayList<String>();

    //     for(int i = 0; i < aux.length; i++){
    //         if(!aux[i].equals("")){
    //             list.add(aux[i]);
    //         }

    //     }

    //     ft.addParams(nome, list);

    //     return nome;
    // }

    //pelo que percebi por tentativa e erro, isso identifica uma função sem parâmetros
    //directDeclarator '(' identifierList? ')' #funcDeclaration2
    // @Override
    // public String visitFuncDeclaration2(CParser.FuncDeclaration2Context ctx) {

    //     String nome = visit(ctx.directDeclarator());

    //     FunctionInfo nf = new FunctionInfo(nome, "no_type");

    //     if(!ft.insert(nf)){
    //         System.out.println("A função: " + nome + " já existe.");
    //         return null;//Para o processo de análise semântica.
    //     }

    //     return visitChildren(ctx);
    // }

    //Coloquei a regra, mas enfim, tem uma lista no nome :)
    //parameterList :  parameterDeclaration (',' parameterDeclaration)*
    // @Override
    // public String visitParameterList(CParser.ParameterListContext ctx) {
    //     String params = new String();

    //     //Cria uma string com os tipos separados por ','
    //     for(int i = 0; i < ctx.parameterDeclaration().size(); i++){
    //         //Não deixa ',' sobrando
    //         if(i == (ctx.parameterDeclaration().size()-1)){
    //             params += "," + ctx.parameterDeclaration().get(i).declarationSpecifiers().getText();
    //         }else{
    //             params += ctx.parameterDeclaration().get(i).declarationSpecifiers().getText();
    //         }
    //     }
    //     visitChildren(ctx);
    //     return params;
    // }

    // Abaixo: Identifier
    // Acima: DirectDeclaretor
    // É uma reescrita do identifier. Funciona como uma interface entre
    // Identifier e DirectDeclarator para guardar o nome de uma função.
	// @Override
    // public String visitFuncName(CParser.FuncNameContext ctx) {
    //     visitChildren(ctx);
    //     this.lineFunc = ctx.Identifier().getSymbol().getLine();
    //     return ctx.Identifier().getText();
    // }


}
