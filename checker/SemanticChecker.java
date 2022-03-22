package checker;
import java.util.ArrayList;

import javax.print.DocFlavor.STRING;
import javax.sound.midi.SysexMessage;

import org.antlr.v4.runtime.Token;
import org.w3c.dom.Node;

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
    private AST ast = new AST(NodeKind.NULL_NODE);
    
    private String lastF;//Auxiliar para o nome da função

    private boolean passed = true;
    private boolean isInBlock = false;

    private String type;// Armazenar os tipos das variáveis
    private int line;
    private int lineFunc;

    private int escopoAtual = 0;

    /**
     * ~ TODOS
     * * Funções e variáveis estão nas tabelas
     * * Permite declarar variáveis em escopo diferente
     * * Imprime o número da linha da variável
     * * Tratar InitDeclarator (Liberado para conversões de todos os tipos)
     *      * Atribuição simples
     *      * Atribuição com operações artméticas
     * * Chamada de variáveis
     * 
     * ^ Chamadas de função, verificação de parâmetros
     * 
     * ^ Tratar operadores Aritméticos
     *
     * ^ Uma função de IO -> Função padrão na tabela de Funções
     *
     * todo Tratar operadores Lógicos
     * ! Uma declaração e manipulação de tipo composto
     * ! Identificar qual é o operador
     * ! Um loop
     * 
     * ~ Limitações
     *  Funções não recebem outras funções nos argumentos
     *  Atribuições fora de escopo com inicializador não estático pode ter comportamento inesperado
     */

    AST getAST(){
        return this.ast;
    }

    VarTable getVarTable(){
        return this.vt;
    }

    FuncTable getFuncTable(){
        return this.ft;
    }
    // Exibe o conteúdo das tabelas em stdout.
    void printTables() {
        System.out.println();
        vt.showTable();
        System.out.println("--------------------------------");
        System.out.println("Tabela de Funcoes: ");
        // st.showTable();
        ft.imprime();
    }

    //Inicio da parseTree
    // @Override public AST visitCompilationUnit(CParser.CompilationUnitContext ctx){

    //     AST root = new AST(NodeKind.COMPILATION_UNIT_NODE);
    //     root.addChild(visitChildren(ctx));
    //     return null; 

    // }

	@Override 
    // Não é o root mas o nó tem o nome do root na parseTree 
    // Primeira função a ser chamada ao criar a parse tree
    public AST visitTranslationUnit(CParser.TranslationUnitContext ctx) {

        AST root = new AST(NodeKind.COMPILATION_UNIT_NODE);
        AST child;

        for(int i = 0; i < ctx.externalDeclaration().size(); i++){
            child = visit(ctx.externalDeclaration(i));
            root.addChild(child);
        }
        this.ast = root;
        return null;
    }

    //externalDeclaration : functionDefinition | declaration |';'
	@Override 
    public AST visitExternalDeclaration(CParser.ExternalDeclarationContext ctx) {

        if(ctx.functionDefinition() != null){
            return visit(ctx.functionDefinition());
        }else{
            return visit(ctx.declaration());
        }
    }

	@Override
    public AST visitCompoundStatement(CParser.CompoundStatementContext ctx) {

        AST aux = visit(ctx.blockItemList());

        // if(aux.getNodeKind() != NodeKind.NULL_NODE){
        //     AST.printDot(aux);
        // }
        return aux;
    }


    //declaration : declarationSpecifiers initDeclaratorList? ';' | ...
	@Override 
    public AST visitDeclaration(CParser.DeclarationContext ctx) {
        AST node = new AST(NodeKind.VAR_DECLARATION_NODE);
        AST typeChild = new AST(NodeKind.NULL_NODE);
        AST varDec = new AST(NodeKind.NULL_NODE);
        AST varList = new AST(NodeKind.VAR_DECLARATION_LIST_NODE);

        if(ctx.initDeclaratorList() == null){//Ver visitDeclarationSpecifiers
            varDec = visit(ctx.declarationSpecifiers());
            varList.addChild(varDec);
            node.addChild(varList);
            // AST.printDot(node);
        }else{
            //Deve retornar o node com informações de tipo, mas aqui não é necessário colocar na AST
            visit(ctx.declarationSpecifiers());

            varDec = visit(ctx.initDeclaratorList());
            node.addChild(varDec);
            // AST.printDot(node);
        }

        return node;
    }

    //assignmentExpression : conditionalExpression | unaryExpression assignmentOperator assignmentExpression | DigitSequence 
    @Override
    public AST visitAssignmentExpression(CParser.AssignmentExpressionContext ctx){

        //Está acontecendo alguma alteração no valor de variável
        if(ctx.assignmentOperator() != null){

            AST assign = new AST(NodeKind.ASSIGN_NODE);

            AST rChild = visit(ctx.assignmentExpression());//initializer

            AST lChild = visit(ctx.unaryExpression());//Variável

            String varName = lChild.getText();

            assign.addChild(lChild);

            if(AST.is_tree(rChild)){

                if( lChild.getNodeKind().toString().equals(rChild.getText()) ){
                    assign.addChild(rChild);
                } else{

                    AST cast_type = AST.convertion_node_generator(
                        rChild.getText(), // -> ladoDireito
                        lChild.getNodeKind().toString() //node -> ladoEsquerdo
                    );

                    cast_type.addChild(rChild);
                    assign.addChild(cast_type);

                }
                
            }else{
                if( lChild.getNodeKind().toString().equals(rChild.getNodeKind().toString()) ){
                    assign.addChild(rChild);
                } else{

                    AST cast_type = AST.convertion_node_generator(
                        rChild.getNodeKind().toString(),
                        lChild.getNodeKind().toString()
                    );

                    cast_type.addChild(rChild);
                    assign.addChild(cast_type);
                }
                
            }
            //AST.printDot(assign);
            return assign;
        }
        return visitChildren(ctx); 
    }

    //initDeclaratorList : initDeclarator (',' initDeclarator)*
	@Override
    public AST visitInitDeclaratorList(CParser.InitDeclaratorListContext ctx) {
        AST list = new AST(NodeKind.VAR_DECLARATION_LIST_NODE);
        AST child;

        if(ctx.Comma(0) != null){
            this.line = ctx.Comma(0).getSymbol().getLine();
        }

        for(int i = 0; i < ctx.initDeclarator().size(); i++){
            child = visit(ctx.initDeclarator(i));
            list.addChild(child);
        }
        // AST.printDot(list);
        
		return list;
	}

    // initDeclarator : declarator ('=' initializer)?
	@Override
    public AST visitInitDeclarator(CParser.InitDeclaratorContext ctx) {

        AST node = new AST(NodeKind.NULL_NODE);
        AST assign = new AST(NodeKind.ASSIGN_NODE);
        String nome = visit(ctx.declarator()).getText();

        int escopo = 0;
        VarInfo nv;

        if(this.isInBlock){
            escopo = this.escopoAtual;
        }

        switch (this.type) {
            case "int":
                node = new AST(NodeKind.VAR_INT_NODE);
                break;
            case "float":
                node = new AST(NodeKind.VAR_FLOAT_NODE);
                break;
            case "char":
                node = new AST(NodeKind.VAR_CHAR_NODE);
                break;
            default://ERRO DE TIPO INDEFINIDO, ver visitTypeSpecifier
                System.out.println("DEU UM ERRÃO, ABORTANDO O PROGRAMA...");
                System.exit(1);
                break;
        }

        node.addInfo(nome);
        
        // Caso exista o sinal de igualdade
        if(ctx.Assign() != null){
            this.line = ctx.Assign().getSymbol().getLine();

            String aux = this.type;
            //! Quando operações aritméticas estiverem completas, adicionar o child de atribuição
            AST chieldInitializer = visit(ctx.initializer());//Arvore de initializer

            assign.addChild(node);

            //Trata quando algum valor de inicialização é incorreto
            //função com parâmetro errado, variável não declarada, função indefinida
            if(chieldInitializer.getNodeKind() == NodeKind.NULL_NODE){
                assign.addChild(chieldInitializer);
                return assign;
            }

            String varType = node.getNodeKind().toString();

            if(AST.is_tree(chieldInitializer)){
                
                nv = new VarInfo(nome, varType, this.line, escopo, null);
                if( varType.equals(chieldInitializer.getText()) ){
                    assign.addChild(chieldInitializer);
                } else{

                    AST cast_type = AST.convertion_node_generator(
                        chieldInitializer.getText(), // -> ladoDireito
                        node.getNodeKind().toString() //node -> ladoEsquerdo
                    );

                    cast_type.addChild(chieldInitializer);
                    assign.addChild(cast_type);
                }
                
            }else{//Verificando os tipos da "atribuição"
                
                String constType = null;
                switch (chieldInitializer.getNodeKind().toString()) {
                    case "int":
                        constType = Integer.toString(chieldInitializer.intData);
                        break;
                    case "char":
                        constType = Character.toString(chieldInitializer.charData);
                        break;
                    case "float":
                        constType = Float.toString(chieldInitializer.floatData);
                        break;
                    default:
                }

                nv = new VarInfo(nome, varType, this.line, escopo, constType);

                if( varType.equals(chieldInitializer.getNodeKind().toString()) ){
                    assign.addChild(chieldInitializer);
                } else{
     
                    AST cast_type = AST.convertion_node_generator(
                        chieldInitializer.getNodeKind().toString(),
                        varType
                    );

                    cast_type.addChild(chieldInitializer);
                    assign.addChild(cast_type);
                }
                
            }
            if(!vt.insert(nv)){
                System.out.println("A variável : " + nome + " já foi declarada.");
                System.out.println("Abortando o programa.");
                System.exit(1);
            }
            this.type = aux;

        }else{
            nv = new VarInfo(nome, this.type, this.line, escopo, null);
            if(!vt.insert(nv)){
                System.out.println("A variável : " + nome + " já foi declarada.");
                System.out.println("Abortando o programa.");
                System.exit(1);
            }
            return node;
        }
        
		return assign;
	}

    // declarationSpecifiers : declarationSpecifier+
    @Override 
    public AST visitDeclarationSpecifiers(CParser.DeclarationSpecifiersContext ctx) {

        AST node;
        if(ctx.declarationSpecifier().size() == 1){//Variável com initDeclarator
            node = visit(ctx.declarationSpecifier(0));
            return node;//Retornando node com type
        }else{//Variável sem initDeclarator
            visit(ctx.declarationSpecifier(0));
            node = visit(ctx.declarationSpecifier(1));
            return node;//Retornando node de variável
        }

    }

    //additiveExpression :  multiplicativeExpression (('+'|'-') multiplicativeExpression)*
	@Override
    public AST visitAdditiveExpression(CParser.AdditiveExpressionContext ctx) {
        AST node;
        String bigger_typer = new String();
        String bigger_type_aux = new String();

        ArrayList<AST> listTypes = new ArrayList<AST>();
        AST aux;
        
        for(int i = 0; i < ctx.multiplicativeExpression().size(); i++){

            aux = visit(ctx.multiplicativeExpression(i));

            listTypes.add(aux);
        }

        // Caso tenhamos apenas um tipo na lista significa que
        // temos um assign de uma variavel, assim basta retornar
        // a mesma.
        if(listTypes.size() == 1){
            AST ax = listTypes.get(0);
            return ax;
        }

        AST multOP;
        AST c1, c2;
        String bigger_type;

        for(int i = 0; i < ctx.multiplicativeExpression().size()-1; i++){
            
            multOP= new AST(NodeKind.PLUS_NODE);
            
            if(i == 0){
                c1 = listTypes.get(0);
                c2 = listTypes.get(1);     
            } else {
                c1 = listTypes.get(i-1);
                c2 = listTypes.get(i);
            }

            // Checa se um dos filhos da operação e uma arvore.
            // c1 será sempre uma arvore nesse caso, pois é averiguado
            // no if acima deste.
            NodeKind c1_NodeKind = null;
            NodeKind c2_NodeKind = null;

            if(AST.is_tree(c1) && AST.is_tree(c2)){
                
                c1_NodeKind = AST.string_to_nodekind(c1.getText());
                c2_NodeKind = AST.string_to_nodekind(c2.getText());
                
                // Pega o tipo maior entre as duas arvores
                bigger_type = AST.unification(
                    c1_NodeKind, 
                    c2_NodeKind
                );

                multOP.addInfo(bigger_type);

                // Atribui o tipo das mesmas a uma variável para
                // facilitar

                // Caso o tipo da primeira arvore seja difente do 
                // tipo maior é necessário um nó de conversão.
                // Não haverá o caso onde ambas sejam diferentes do 
                // tipo maior, pois o tipo maior pega o maior entre 
                // os tipos dos filhos.
                if(!c1_NodeKind.toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c1_NodeKind.toString(), bigger_type);
                    unification_node.addChild(c1);
                    multOP.addChild(unification_node);
                    multOP.addChild(c2);

                } else if(!c2_NodeKind.toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c2_NodeKind.toString(), bigger_type);
                    multOP.addChild(c1);
                    unification_node.addChild(c2);
                    multOP.addChild(unification_node);

                }else{
                    multOP.addChild(c1);
                    multOP.addChild(c2);
                }

            } else if (AST.is_tree(c1) || AST.is_tree(c2)){
 
                if (AST.is_tree(c2)){
                    AST aux_node = c2;
                    c2 = c1;
                    c1 = aux_node;
                }
                // AST.printDot(c1);
                // AST.printDot(c2);
                
                // Atribui o tipo da arvore para facilitar
                c1_NodeKind = AST.string_to_nodekind(c1.getText());

                // Pega o tipo maior entre uma arvore e um nó simples
                bigger_type = AST.unification(
                    c1_NodeKind, 
                    c2.getNodeKind()
                );
                multOP.addInfo(bigger_type);

                if(!c1_NodeKind.toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c1_NodeKind.toString(), bigger_type);
                    unification_node.addChild(c1);
                    multOP.addChild(unification_node);
                    multOP.addChild(c2);

                } else if(!c2.getNodeKind().toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c2.getNodeKind().toString(), bigger_type);
                    multOP.addChild(c1);
                    unification_node.addChild(c2);
                    multOP.addChild(unification_node);
                }else{
                    multOP.addChild(c1);
                    multOP.addChild(c2);
                }
                
            } else {

                // Pega o tipo maior entre uma arvore e um nó simples
                bigger_type = AST.unification(
                    c1.getNodeKind(), 
                    c2.getNodeKind()
                );
                multOP.addInfo(bigger_type);

                if(!c1.getNodeKind().toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c1.getNodeKind().toString(), bigger_type);
                    unification_node.addChild(c1);
                    multOP.addChild(unification_node);
                    multOP.addChild(c2);
                
                } else if(!c2.getNodeKind().toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c2.getNodeKind().toString(), bigger_type);
                    multOP.addChild(c1);
                    unification_node.addChild(c2);
                    multOP.addChild(unification_node);
                }else{
                    multOP.addChild(c1);
                    multOP.addChild(c2);
                }

            }
               
                
            if(i == 0){
                listTypes.remove(0);
                listTypes.remove(0);
                listTypes.add(0, multOP);
            } else {
                listTypes.remove(i);
                listTypes.add(i, multOP);
            }

        }
        
        return listTypes.get(listTypes.size()-1);
    }


    //multiplicativeExpression : castExpression (('*'|'/'|'%') castExpression)*
	@Override
    public AST visitMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) {
       
        AST node;
        String bigger_typer = new String();
        String bigger_type_aux = new String();

        ArrayList<AST> listTypes = new ArrayList<AST>();
        AST aux;

        for(int i = 0; i < ctx.castExpression().size(); i++){

            aux = visit(ctx.castExpression(i));

            listTypes.add(aux);
        }

        // Caso tenhamos apenas um tipo na lista significa que
        // temos um assign de uma variavel, assim basta retornar
        // a mesma.
        if(listTypes.size() == 1){
            AST ax = listTypes.get(0);
            return ax;
        }

        AST multOP;
        AST c1, c2;
        String bigger_type;

        for(int i = 0; i < ctx.castExpression().size()-1; i++){
            
            multOP= new AST(NodeKind.TIMES_NODE);
            
            if(i == 0){
                c1 = listTypes.get(0);
                c2 = listTypes.get(1);     
            } else {
                c1 = listTypes.get(i-1);
                c2 = listTypes.get(i);
            }

            // Checa se um dos filhos da operação e uma arvore.
            // c1 será sempre uma arvore nesse caso, pois é averiguado
            // no if acima deste.
            NodeKind c1_NodeKind = null;
            NodeKind c2_NodeKind = null;

            if(AST.is_tree(c1) && AST.is_tree(c2)){
                
                c1_NodeKind = AST.string_to_nodekind(c1.getText());
                c2_NodeKind = AST.string_to_nodekind(c2.getText());
                
                // Pega o tipo maior entre as duas arvores
                bigger_type = AST.unification(
                    c1_NodeKind, 
                    c2_NodeKind
                );

                multOP.addInfo(bigger_type);

                // Atribui o tipo das mesmas a uma variável para
                // facilitar

                // Caso o tipo da primeira arvore seja difente do 
                // tipo maior é necessário um nó de conversão.
                // Não haverá o caso onde ambas sejam diferentes do 
                // tipo maior, pois o tipo maior pega o maior entre 
                // os tipos dos filhos.
                if(!c1_NodeKind.toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c1_NodeKind.toString(), bigger_type);
                    unification_node.addChild(c1);
                    multOP.addChild(unification_node);
                    multOP.addChild(c2);

                } else if(!c2_NodeKind.toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c2_NodeKind.toString(), bigger_type);
                    multOP.addChild(c1);
                    unification_node.addChild(c2);
                    multOP.addChild(unification_node);

                }else{
                    multOP.addChild(c1);
                    multOP.addChild(c2);
                }

            } else if (AST.is_tree(c1) || AST.is_tree(c2)){

                if (AST.is_tree(c2)){
                    AST aux_node = c2;
                    c2 = c1;
                    c1 = aux_node;
                }
                
                // Atribui o tipo da arvore para facilitar
                c1_NodeKind = AST.string_to_nodekind(c1.getText());

                // Pega o tipo maior entre uma arvore e um nó simples
                bigger_type = AST.unification(
                    c1_NodeKind, 
                    c2.getNodeKind()
                );
                multOP.addInfo(bigger_type);

                if(!c1_NodeKind.toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c1_NodeKind.toString(), bigger_type);
                    unification_node.addChild(c1);
                    multOP.addChild(unification_node);
                    multOP.addChild(c2);

                } else if(!c2.getNodeKind().toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c2.getNodeKind().toString(), bigger_type);
                    multOP.addChild(c1);
                    unification_node.addChild(c2);
                    multOP.addChild(unification_node);
                }else{
                    multOP.addChild(c1);
                    multOP.addChild(c2);
                }
                
            } else {

                // Pega o tipo maior entre uma arvore e um nó simples
                bigger_type = AST.unification(
                    c1.getNodeKind(), 
                    c2.getNodeKind()
                );
                multOP.addInfo(bigger_type);

                if(!c1.getNodeKind().toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c1.getNodeKind().toString(), bigger_type);
                    unification_node.addChild(c1);
                    multOP.addChild(unification_node);
                    multOP.addChild(c2);
                
                } else if(!c2.getNodeKind().toString().equals(bigger_type)){
                    AST unification_node = AST.convertion_node_generator(c2.getNodeKind().toString(), bigger_type);
                    multOP.addChild(c1);
                    unification_node.addChild(c2);
                    multOP.addChild(unification_node);
                }else{
                    multOP.addChild(c1);
                    multOP.addChild(c2);
                }

            }
               
                
            if(i == 0){
                listTypes.remove(0);
                listTypes.remove(0);
                listTypes.add(0, multOP);
            } else {
                listTypes.remove(i);
                listTypes.add(i, multOP);
            }

        }
        return listTypes.get(listTypes.size()-1);
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
    @Override
    public AST visitPostfixExpression(CParser.PostfixExpressionContext ctx) {

        String aux_type = this.type;//O visitador do filho altera o type

        AST node = visit(ctx.primaryExpression());
        String name = node.getText();

        if(node.kind == NodeKind.PLUS_NODE || node.kind == NodeKind.TIMES_NODE){
            return visit(ctx.primaryExpression());
        }

        // Verifica se o retorno foi uma função
        if(!ctx.LeftParen().isEmpty()){

            if(!ft.verifyIfAlreadyExists(name)){ 
                System.out.println("Simbolo " + name + " nao encontrado.");
                System.out.println("Abortando o prgrama");
                System.exit(1);
                return new AST(NodeKind.NULL_NODE);
            }

            String string_args = ctx.argumentExpressionList(0).getText();

            if(string_args.contains("(")){
                System.out.println(name + " - O compilador não aceita funções como argumento de função.");
                System.out.println("Abortando o prgrama");
                System.exit(1);
                return new AST(NodeKind.NULL_NODE);
            }
           

            String argumentList[] = ctx.argumentExpressionList(0).getText().split(",");

            //Verifica se o tamanho da lista argumentos é coerente
            if(ft.getListSize(name) == argumentList.length){
                                    
                ArrayList<String> args = new ArrayList<String>();
                String auxArgs = "";
                Boolean err = false;
                String arg = "";
                for(int i = 0; i < argumentList.length; i++){
                    arg = const_type_checker(argumentList[i]);

                    if(i == argumentList.length-1){
                        auxArgs += argumentList[i];
                    }else{
                        auxArgs += argumentList[i] + ", ";
                    }
                    if(arg.equals("no_type")){
                        err = true;
                    }
                    args.add(arg);
                }

                if(err){
                    AST voidNode = new AST(NodeKind.NULL_NODE);
                    voidNode.addInfo("void");
                    return voidNode;
                }

                if(ft.lookUp(name, args)){

                    node = new AST(NodeKind.NULL_NODE);

                    String func_type = ft.getType(name);

                    switch (func_type) {
                        case "int":
                            node = new AST(NodeKind.FUNC_TYPE_INT_NODE);
                            break;
                        case "char":
                            node = new AST(NodeKind.FUNC_TYPE_CHAR_NODE);
                            break;
                        case "float":
                            node = new AST(NodeKind.FUNC_TYPE_FLOAT_NODE);
                            break;
                        case "void":
                            node = new AST(NodeKind.FUNC_TYPE_VOID_NODE);
                            break;
                        default:
                            System.out.println("Tipo inválido " + func_type + ". " + lineFunc + ".");
                            System.out.println("Abortando o programa...");
                            System.exit(1);
                    }

                    node.addInfo(name + "(" + auxArgs + ")");

                    return node;
                }else{
                    System.out.println("Erro de tipo inconsistente na função " + name + ".");
                    System.out.println("Abortando o programa.");
                    System.exit(1);
                    return new AST(NodeKind.NULL_NODE);//O JAVA reclama que deve retornal algum valor
                }

            }else{
                System.out.println("Funcao " + name + " tem argumentos demais.");
                System.out.println("Abortando o programa.");
                System.exit(1);
                return new AST(NodeKind.NULL_NODE);//O JAVA reclama que deve retornal algum valor
            }

            //Inicializando a lista de argumentos passada para a função

        } else {//Verifica se foi constante ou variável
            if(this.type.equals("NAME")){//Variável
                // Escopo atual = 0 siginifica que a variável está fora
                // de escopo, ou seja fora de uma função.
                int escopo = 0;
                // Caso ela esteja em uma função, ou bloco (inBlock),
                // ela terá escopo diferente de 0
                if(this.isInBlock == true){
                    escopo = this.escopoAtual;
                }

                if(vt.lookUp(name, escopo)){//A variável existe
                    //Criando node correspondente
                    
                    String var_type = vt.getType(name, escopo);

                    switch (var_type){
                        case "int":
                            node = new AST(NodeKind.VAR_INT_NODE);
                            break;
                        case "char":
                            node = new AST(NodeKind.VAR_CHAR_NODE);
                            break;
                        case "float":
                            node = new AST(NodeKind.VAR_FLOAT_NODE);
                            break;
                        default:
                            break;
                    }
                    node.addInfo(name);

                    return node;//NODE_VAR*
                }else if(vt.lookUp(name, 0)){

                    String var_type = vt.getType(name, 0);

                    switch (var_type){
                        case "int":
                            node = new AST(NodeKind.VAR_INT_NODE);
                            break;
                        case "char":
                            node = new AST(NodeKind.VAR_CHAR_NODE);
                            break;
                        case "float":
                            node = new AST(NodeKind.VAR_FLOAT_NODE);
                            break;
                        default:
                            break;
                    }
                    node.addInfo(name);

                    return node;//NODE_VAR*
                }
                else{
                    System.out.println("Simbolo " + name + " nao encontrado");
                    System.out.println("Abortando o programa");
                    System.exit(1);
                    return new AST(NodeKind.NULL_NODE);
                }

            }
            //Valor constante
            this.type = aux_type;
            return  node;
        }
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

        if(ctx.expression() != null){
            return visit(ctx.expression());
        }
        
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
                    node = new AST(NodeKind.INT_VAL_NODE, Integer.valueOf(value), value);
                    return node;
                case "float":
                    node = new AST(NodeKind.FLOAT_VAL_NODE, Float.valueOf(value), value);
                    return node;
                case "char":
                    node = new AST(NodeKind.CHAR_VAL_NODE, value.charAt(1), value);
                    return node;
                default:
                    node = new AST(NodeKind.NULL_NODE);
                    node.addInfo(null);
                    return node;
            }
        }
        
        return visitChildren(ctx);
    }

    // Isso aqui converte as constantes para tipo.
    // O tipo é retornado em String.
    public String const_type_checker(String s){

        int escopo = 0;

        if(s.contains("(")){
            return "no_type";
        }else{

            if(this.isInBlock){
                escopo = escopoAtual;
            }

            if(s.contains("\'")){
                return "char";
            }else if(s.contains(".")){
                return "float";
            } else{
                if(Character.isDigit(s.charAt(0))){
                    return "int";
                }else{
                    if(vt.lookUp(s, escopo)){
                        return vt.getType(s, escopo);
                    }else{
                        System.out.println("Simbolo4 " + s + " nao encontrado.");
                        System.out.println("Abortando o programa");
                        System.exit(1);
                        return "no_type";
                    }
                }
            }

        }
    }

    //typedefName : Identifier
	@Override
    public AST visitTypedefName(CParser.TypedefNameContext ctx) {

        AST node = new AST(NodeKind.NULL_NODE);
        String nome = ctx.Identifier().getText();
        int escopo = 0;

        if(this.isInBlock){
            escopo = this.escopoAtual;
        }

        switch (this.type) {
            case "int":
                node = new AST(NodeKind.VAR_INT_NODE);
                break;
            case "float":
                node = new AST(NodeKind.VAR_FLOAT_NODE);
                break;
            case "char":
                node = new AST(NodeKind.VAR_CHAR_NODE);
                break;
            default://ERRO DE TIPO INDEFINIDO
                System.out.println("O tipo " + this.type + " é suportado pela linguagem.");
                System.out.println("Abortando o prgrama");
                System.exit(1);
                break;
        }

        VarInfo nv = new VarInfo(nome, this.type, ctx.Identifier().getSymbol().getLine(), escopo, null);

        if(!vt.insert(nv)){
            System.out.println("A variável : " + nome + " já foi declarada anteriormente.");
            System.out.println("Abortando o prgrama");
            System.exit(1);
        }
        node.addInfo(nome);
        return node;
    }

    //functionDefinition : declarationSpecifiers? declarator declarationList? compoundStatement
    @Override
    public AST visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        AST funcDec = new AST(NodeKind.FUNCTION_DECLARATION_NODE);
        AST aux;
        AST typeNode;
        AST nameAndParamsNode;

        this.escopoAtual++;
        this.isInBlock = true;

        if(ctx.declarator() != null && ctx.declarationSpecifiers() != null){
            aux = visit(ctx.declarationSpecifiers());

            //Isto é um reaproveitamento de node
            //Definimos o tipo da função como um PARAM_TYPE com text = null
            if(aux.getText().equals("char")){
                typeNode = new AST(NodeKind.PARAMETER_CHAR_NODE);
            }else if(aux.getText().equals("int")){
                typeNode = new AST(NodeKind.PARAMETER_INT_NODE);
            }else if(aux.getText().equals("float")){
                typeNode = new AST(NodeKind.PARAMETER_FLOAT_NODE);
            }else if(aux.getText().equals("void")){
                typeNode =  new AST(NodeKind.TYPE_VOID_NODE);
            }else{
                typeNode = aux;
            }
            
            nameAndParamsNode = visit(ctx.declarator());
            funcDec.addChild(typeNode);
            funcDec.addChild(nameAndParamsNode);
            
            aux = visit(ctx.compoundStatement());

            if(aux.getNodeKind() != NodeKind.NULL_NODE){  
                funcDec.addChild(aux);
            }

            this.isInBlock = false;
            return funcDec;
        }
        return funcDec;
    }

    //blockItemList : blockItem+
	@Override 
    public AST visitBlockItemList(CParser.BlockItemListContext ctx) {

        AST blockList = new AST(NodeKind.BLOCK_ITEM_LIST);
        AST block;

        for(int i = 0; i < ctx.blockItem().size(); i++){
            block = visit(ctx.blockItem(i));
            if(block.getNodeKind() != NodeKind.NULL_NODE){
                blockList.addChild(block);
            }
        }
        return blockList;
    }

    //expressionStatement : expression? ';'
	@Override
    public AST visitExpressionStatement(CParser.ExpressionStatementContext ctx) {
        if(ctx.expression() != null){
            return visit(ctx.expression());
        }
        return visitChildren(ctx);
    }

    /**
     * statement
    :   labeledStatement
    |   compoundStatement
    |   expressionStatement
    |   selectionStatement
    |   iterationStatement
    |   jumpStatement
    |   ('__asm' | '__asm__') ('volatile' | '__volatile__') '(' (logicalOrExpression (',' logicalOrExpression)*)? (':' (logicalOrExpression (',' logicalOrExpression)*)?)* ')' ';'
    ;
     */
    @Override 
    public AST visitStatement(CParser.StatementContext ctx){

        if(ctx.expressionStatement() != null){
            return visit(ctx.expressionStatement());
        }

        if(ctx.jumpStatement() != null){
            AST node = new AST(NodeKind.JUMP_NODE);
            AST returnNode = new AST(NodeKind.RETURN_NODE);
            node.addChild(returnNode);
            if(visit(ctx.jumpStatement()) != null){
                node.addChild(visit(ctx.jumpStatement()));
            }
            return node;
        }
        return new AST(NodeKind.NULL_NODE);
    }

    //jumpStatement
    // : ('goto' Identifier
    // |   ('continue'| 'break')
    // |   'return' expression?
    // |   'goto' unaryExpression // GCC extension
    // )
    // ';'
	@Override
    public AST visitJumpStatement(CParser.JumpStatementContext ctx) {
        if(ctx.expression() != null){
            return visit(ctx.expression());
        }
        return visitChildren(ctx);
    }

    // typeSpecifier : ('void' | 'char' | 'int' | 'float' | ...
    @Override
    public AST visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {


        AST node = new AST(NodeKind.NULL_NODE);
        String type = ctx.getText();

        //Caso esse tratamento não seja feito. O type é definido como o nome da variável
        //Ver C.g4 declarationSpecifiers
        if(ctx.typedefName() == null){
            switch (type) {
                case "int":
                case "char":
                case "float":
                case "void":
                    node.addInfo(type);
                    this.type = type;
                    break;
                default:
                    System.out.println("Simbolo desconhecido1: " + type + ".");
                    System.out.println("Abortando...");
                    System.exit(1);
            }
        }else{
            return visit(ctx.typedefName());
        }

        return node;
    }

    //directDeclarator '(' parameterTypeList ')' #funcDeclaration1
	@Override
    public AST visitFuncDeclaration1(CParser.FuncDeclaration1Context ctx) {

        String name = visit(ctx.directDeclarator()).getText();
        String paramName, paramType;

        AST func = new AST(NodeKind.FUNCTION_NODE);
        func.addInfo(name);
        AST nodeAux;
        AST params = visit(ctx.parameterTypeList());

        FunctionInfo nf = new FunctionInfo(name, this.type);
        VarInfo nv;
        int line = ctx.LeftParen().getSymbol().getLine();

        ArrayList<String> list = new ArrayList<String>();

        if(!ft.insert(nf)){
            System.out.println("A função: " + name + " já existe. " + line + ".");
            System.out.println("Abortando...");
            System.exit(1);
        }

        //Cria a lista de tipos que a função aceita
        //Cria as variáveis declaradas como parâmetros da função
        for(int i = 0; i < params.getChildrenSize(); i++){
            nodeAux = params.getChild(i);

            paramName = nodeAux.getText();
            paramType = nodeAux.getNodeKind().toString();

            nv = new VarInfo(paramName, paramType, line, this.escopoAtual, null);
            list.add(paramType);
            vt.insert(nv);
        }

        func.addChild(params);
        ft.addParams(name, list);

        return func;
    }

    //Funções sem parâmetros
    //directDeclarator '(' identifierList? ')' #funcDeclaration2
    @Override
    public AST visitFuncDeclaration2(CParser.FuncDeclaration2Context ctx) {

        String nome = visit(ctx.directDeclarator()).getText();

        AST func = new AST(NodeKind.FUNCTION_NODE);
        func.addInfo(nome);
    
        FunctionInfo nf = new FunctionInfo(nome, this.type);

        if(!ft.insert(nf)){
            System.out.println("A função: " + nome + " já existe.");
            System.out.println("Abortando...");
            System.exit(1);
        }

        return func;
    }

    //É o filho identifier de DirectDeclarator, 
    //Identifica nomes de função e de variáveis quando são declarados
	@Override
    public AST visitFuncName(CParser.FuncNameContext ctx) {
        AST node = new AST(NodeKind.NULL_NODE);
        String nome = ctx.Identifier().getText();

        node.addInfo(nome);
        this.lineFunc = ctx.Identifier().getSymbol().getLine();
        return node;
    }

    //parameterList : parameterDeclaration (',' parameterDeclaration)*
	@Override
    public AST visitParameterList(CParser.ParameterListContext ctx) {
        AST node = new AST(NodeKind.PARAMS_NODE);
        AST aux;
        for(int i = 0; i < ctx.parameterDeclaration().size(); i++){
            aux = visit(ctx.parameterDeclaration(i));
            node.addChild(aux);
        }
        return node;
    }


    //parameterDeclaration : declarationSpecifiers declarator | declarationSpecifiers2 abstractDeclarator?
    @Override
    public AST visitParameterDeclaration(CParser.ParameterDeclarationContext ctx) {
        if(ctx.declarationSpecifiers() != null){
            String paramType = ctx.declarationSpecifiers().getText();
            String paramName = ctx.declarator().getText();

            AST node;
            if(paramType.equals("char")){
                node = new AST(NodeKind.PARAMETER_CHAR_NODE);
                node.addInfo(paramName);
                return node;
            }

            if(paramType.equals("int")){
                node = new AST(NodeKind.PARAMETER_INT_NODE);
                node.addInfo(paramName);
                return node;
            }

            if(paramType.equals("float")){
                node = new AST(NodeKind.PARAMETER_FLOAT_NODE);
                node.addInfo(paramName);
                return node;
            }
        }
        return null;
    }
}
