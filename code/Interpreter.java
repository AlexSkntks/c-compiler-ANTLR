package code;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ast.*;
import checker.Pilha;
import tables.FuncTable;
import tables.VarInfo;
import tables.VarTable;

public class Interpreter extends ASTBaseVisitor<AST>{

    private final Pilha stack;
    private final Memory memory;
    private final VarTable vt;
    private final FuncTable ft;
    private int escopo = 0;
    private Boolean isInBlock = false;
    private Map<Integer, Integer> map = new HashMap<>();

    public Interpreter(VarTable vt, FuncTable ft){
        this.vt = vt;
        this.ft = ft;
        this.stack = new Pilha();
		this.memory = new Memory(vt);
        initMap();   
    }

    // Gera um ID único para cada variável, e essa informação é usada no
    // map para recuperar o ID da varipavel na memória
    private int hash(String name, int escopo){
        String conc = name + Integer.toString(escopo);
        return conc.hashCode();
    }

    //Adiciona o hash da variável no map com indices sequenciais de memória
    private void initMap(){
        ArrayList<VarInfo> list = vt.getList();
        int i = 0;
        
        for (VarInfo var : list) {
            map.put(hash(var.getName(), var.getEscopo()), i++);
        }

    }

    public void printMap(){
        String name;
        for (VarInfo var : vt.getList()) {
            name = var.getName();
            System.out.println(name + " - " + map.get(this.hash(name, var.getEscopo())));
        }
    }

    @Override
    protected AST visitCompilationUnitNode(AST node) {// 

        for(int i = 0; i < node.getChildrenSize(); i++){
            visit(node.getChild(i));//AST
        }
        
        return null;
    }

    @Override
    protected AST visitExternalDeclaration(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    // function_definition == FunctionDeclaration
    @Override
    protected AST visitFunctionDeclarationNode(AST node) {
        // TODO Auto-generated method stub
        // TODO visitador dos parametros do function
        //System.out.println("Tipo da função: " + visit(node.getChild(0)));
        //System.out.println("Nome da função: " + visit(node.getChild(1)));
        visit(node.getChild(0));
        visit(node.getChild(1));
        // BlocItemList   
        visit(node.getChild(2)); 
        return null;
    }

    // Corpo da função
    @Override
    protected AST visitBlockItemList(AST node) {
        // TODO Falta tratar o jump_node, ou seja o retorno da função 
        for(int i = 0; i < node.getChildrenSize(); i++){
            visit(node.getChild(i));//AST
        }
        return null;
    }

    @Override
    protected AST visitvarDeclarationNode(AST node) {
        // TODO Auto-generated method stub
        return visit(node.getChild(0));
    }

    @Override
    protected AST visitvarDeclarationListNode(AST node) {
        // TODO Auto-generated method stub
        for(int i = 0; i < node.getChildrenSize(); i++){
            visit(node.getChild(i));//AST
        }
        return null;
    }

    // "=" == assignNode
    // Chamar o filho da direita
    // Olhar o tipo do lado esquerdo para fazer pop com o tipo de dado correto
    @Override
    protected AST vistAssignNode(AST node) {
        // Não é necessário fazer a visitação do filho da esqeurda
        // pois as informações que necessitamos para verificar e 
        // armazenar são obtidas de forma mais simples como demonstrado a baixo.
        String name = node.getChild(0).getText();
        String type = node.getChild(0).getNodeKind().toString();
        // Lado direito da equação, tudo q será atribuido a variável.
        visit(node.getChild(1));
        System.out.print("A variavel " + name + " Esta recebendo ");
        // TODO artimética na pilha seguindo o exemplo do char
        switch (node.getChild(1).getNodeKind().toString()) {
            case "char":
                // TODO char value = stack.popc()
                // TODO memory.insert(value)
                System.out.println(" " + stack.popc());
                break;
            case "int":
                System.out.println(" " + stack.popi());
                break;
            case "float":
                System.out.println(" " + stack.popf());
                break;
            case "+":
                visit(node.getChild(0));
                System.out.println(" " + stack.pop());
                break;
            case "*":
                visit(node.getChild(0));
                System.out.println(" " + stack.pop());
                break;
            default:
                System.out.println("Erro inesperado ocorreu!");
                System.exit(1);
                break;
        }

        return null;
    }
    
    @Override
    protected AST visitintValNode(AST node) {
        System.out.println("Valor da variável inteira: " + node.getText());
        stack.push(node.getText());
        return null;
    }

    @Override
    protected AST visitFloatValNode(AST node) {
        //System.out.println("Valor da variável float: " + node.getText());
        stack.push(node.getText());
        return null;
    }

    @Override
    protected AST visitCharValNode(AST node) {
        stack.push(node.getText());
        return null;
    }

    @Override
    protected AST visitMinusNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    //Verificar quandoo um dos lados é arvore. Pelo print ele tá pegando errado quando vem de arvore
    protected AST visitPlusNode(AST node) {
        switch (node.getText()) {
            case "char":
                break;
            case "int":
                // Chama o visitador para o filho da esq

                visit(node.getChild(0));
                visit(node.getChild(1));

                int rInt = stack.popi();
                int lInt = stack.popi();

                int resultInt = lInt + rInt;
                stack.push(Integer.toString(resultInt));
                break;
            case "float":
                // Chama o visitador para o filho da esq
                visit(node.getChild(0));
                // Chama o visitador para o filho da dir
                visit(node.getChild(1));
                float rFloat = stack.popf();
                float lFloat = stack.popf();
                float resultFloat = lFloat + rFloat;
                stack.push(Float.toString(resultFloat));
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    protected AST visitTimesNode(AST node) {
        switch (node.getText()) {
            case "char":
                break;
            case "int":
                // Chama o visitador para o filho da esq
                visit(node.getChild(0));
                // Chama o visitador para o filho da dir
                visit(node.getChild(1));
                int rInt = stack.popi();
                int lInt = stack.popi();
                int resultInt = lInt * rInt;
                stack.push(Integer.toString(resultInt));
                break;
            case "float":
                // Chama o visitador para o filho da esq
                visit(node.getChild(0));
                // Chama o visitador para o filho da dir
                visit(node.getChild(1));
                float rFloat = stack.popf();
                float lFloat = stack.popf();
                float resultFloat = lFloat * rFloat;
                stack.push(Float.toString(resultFloat));
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    protected AST visitNullNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    //Pega da pilha
    //Converte
    //Joga na Pilha
    protected AST visitChar2Int(AST node) {
        visit(node.getChild(0));
        char c = this.stack.popc();
        Integer i = (int)c;
        String s = i.toString();
        this.stack.push(s);
        return null;
    }

    @Override
    protected AST visitchar2Float(AST node) {
        visit(node.getChild(0));
        char c = this.stack.popc();
        Float f = (float)c;
        String s = f.toString();
        this.stack.push(s);
        return null;
    }

    @Override
    //Pega da pilha
    //Converte
    //Joga na Pilha
    protected AST visitInt2Float(AST node) {
        visit(node.getChild(0));
        // Pega o valor da stack em int
        Integer i = this.stack.popi();
        // Conversão feita para float
        Float f = i.floatValue();
        // Para salvar na pilha deve-se converter o valor
        // para String.
        String s = f.toString();
        this.stack.push(s);
        return null;
    }

    @Override
    //Pega da pilha
    //Converte
    //Joga na Pilha
    protected AST visitInt2Char(AST node) {
        visit(node.getChild(0));
        int i = this.stack.popi();
        char c = (char)i;
        String s = Character.toString(c);
        this.stack.push(s);
        return null;
    }

    @Override
    //Pega da pilha
    //Converte
    //Joga na Pilha
    protected AST visitFloat2Char(AST node) {
        visit(node.getChild(0));
        float f = this.stack.popf();
        int i = Math.round(f);
        char c = (char)i;
        String s = Character.toString(c);
        this.stack.push(s);
        return null;
    }

    @Override
    //Pega da pilha
    //Converte
    //Joga na Pilha
    protected AST visitFloat2Int(AST node) {
        visit(node.getChild(0));
        float f = this.stack.popf();
        Integer i = Math.round(f);
        String s = i.toString();
        this.stack.push(s);
        System.out.println("chegou no nó de conversão");
        return null;
    }

    @Override
    protected AST visitParameterIntNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitParameterCharNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitParameterFloatNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitParamsNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitFunctionNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    protected AST visitVarIntNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitVarFloatNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitVarCharNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitFuncTypeFloatNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitFuncTypeIntNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitFuncTypeFCharNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitFuncTypeVoidNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitJumpNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitReturnNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
