package ast;

import java.util.ArrayList;
import java.util.List;

import tables.VarTable;
// Implementação dos nós da AST.
public class AST {

	// Todos os campos são finais para simplificar, assim não precisa de getter/setter.
	// Note que não há union em Java, então aquele truque de ler
	// e/ou escrever o campo com formatos diferentes não funciona aqui.
	// Os campos 'data' NÃO ficam sincronizados!
	public  final NodeKind kind;
	public  final int intData;
    public  final char charData;
	public  final float floatData;

    public String text = null;

	private final List<AST> children; // Privado para que a manipulação da lista seja controlável.

    static String [][] unification_table = {
        {"char",  "int",   "float"},
        {"int",   "int",   "float"},
        {"float", "float", "float"}
    };

	// private AST(){}

	// Construtor completo para poder tornar todos os campos finais.
	// Privado porque não queremos os dois campos 'data' preenchidos ao mesmo tempo.
	private AST(NodeKind kind, int intData, float floatData, char charData, String text) {
		this.kind = kind;
		this.intData = intData;
        this.charData = charData;
		this.floatData = floatData;
        this.text = text;
		this.children = new ArrayList<AST>();
	}

	// Cria o nó com um dado inteiro.
	public AST(NodeKind kind, int intData, String value) {
		this(kind, intData, 0.0f, ' ', value);
	}

	// Cria o nó com um dado float.
	public AST(NodeKind kind, float floatData, String value) {
		this(kind, 0, floatData, ' ', null);
	}

    // Cria o nó com um dado char.
	public AST(NodeKind kind, char charData, String value) {
		this(kind, 0, 0.0f, charData, null);
	}

    public AST(NodeKind kind){
		this(kind, 0, "");//Talvez precise trocar o 0
	}
	
	//Adiciona um campo texto de informações extras ao nó
	public void addInfo(String info){
		this.text = info;
	}

	// Adiciona um novo filho ao nó.
	public void addChild(AST child) {
		// A lista cresce automaticamente, então nunca vai dar erro ao adicionar.
		this.children.add(child);
	}

	// Retorna o filho no índice passado.
	// Não há nenhuma verificação de erros!
	public AST getChild(int idx) {
		// Claro que um código em produção precisa testar o índice antes para
		// evitar uma exceção.
	    return this.children.get(idx);
	}

	public String getText(){
		return this.text;
	}

	public NodeKind getNodeKind(){
		return this.kind;
	}

	public int getChildrenSize(){
		return this.children.size();
	}
	
	// Cria um nó e pendura todos os filhos passados como argumento.
	public static AST newSubtree(NodeKind kind, AST... children) {
		AST node = new AST(kind, 0, "");
	    for (AST child: children) {
	    	node.addChild(child);
	    }
	    return node;
	}

	// Variáveis internas usadas para geração da saída em DOT.
	// Estáticas porque só precisamos de uma instância.
	private static int nr;
	// private static VarTable vt;

	// Imprime recursivamente a codificação em DOT da subárvore começando no nó atual.
	// Usa stderr como saída para facilitar o redirecionamento, mas isso é só um hack.
	private int printNodeDot() {
		int myNr = nr++;

	    System.err.printf("node%d[label=\"", myNr);

	    if (this.kind.toString() != "no_type") {
	    	System.err.printf("(%s) ", this.kind.toString());
	    }
        
        if (NodeKind.hasData(this.kind)) {
	        if (this.kind == NodeKind.FLOAT_VAL_NODE) {
                // System.out.println("---- TEST FLOAT ---");
	        	if(this.text.contains("(func)")){
                    System.err.printf("%s", this.text);
                }else{
                    System.err.printf("%.2f", this.floatData);
                }
	        } else if (this.kind == NodeKind.STR_VAL_NODE) {
	        	System.err.printf("@%d", this.intData);
	        } else if(this.kind == NodeKind.INT_VAL_NODE) {
                // System.out.println("TEST INT");
                if(this.text.contains("(func)")){
                    System.err.printf("%s", this.text);
                }else{
                    System.err.printf("%d", this.intData);
                }
	        } else if (this.kind == NodeKind.CHAR_VAL_NODE){
                // System.out.println("TEST CHAR");
	        	if(this.text.contains("(func)")){
                    System.err.printf("%s", this.text);
                }else{
                    System.err.printf("%c", this.charData);
                }
            } else if(this.kind == NodeKind.PARAMETER_INT_NODE){
	        	System.err.printf("%s", this.getText());
			} else if(this.kind == NodeKind.PARAMETER_CHAR_NODE){
	        	System.err.printf("%s", this.getText());
			}else if(this.kind == NodeKind.PARAMETER_FLOAT_NODE){
	        	System.err.printf("%s", this.getText());
			}else if(this.kind == NodeKind.FUNCTION_NODE){
	        	System.err.printf("%s", this.getText());
			}else if(this.kind == NodeKind.VAR_CHAR_NODE){
	        	System.err.printf("%s", this.getText());
			}else if(this.kind == NodeKind.VAR_INT_NODE){
	        	System.err.printf("%s", this.getText());
			}else if(this.kind == NodeKind.VAR_FLOAT_NODE){
	        	System.err.printf("%s", this.getText());
			}
            
	    }
        System.out.println("TEST");

	    System.err.printf("\"];\n");

	    for (int i = 0; i < this.children.size(); i++) {
	        int childNr = this.children.get(i).printNodeDot();
	        System.err.printf("node%d -> node%d;\n", myNr, childNr);
	    }
	    return myNr;
	}

	// Imprime a árvore toda em stderr.
	public static void printDot(AST tree) {
	    nr = 0;
	    // vt = table;
	    System.err.printf("digraph {\ngraph [ordering=\"out\"];\n");
	    tree.printNodeDot();
	    System.err.printf("}\n");
	}
    //        char:  int:   float:
    // char:  char,  int,   float
    // int:   int,   int,   float
    // float: float, float, float
    public static String unification(NodeKind n1, NodeKind n2){
        int index1 = 0;
        int index2 = 1;

        switch (n1.toString()) {
            case "char":
                index1 = 0;
                break;
            case "int":
                index1 = 1;
                break;
            case "float":
                index1 = 2;
                break;
            default:
                return "no_type";
        }

        switch (n2.toString()) {
            case "char":
                index2 = 0;
                break;
            case "int":
                index2 = 1;
                break;
            case "float":
                index2 = 2;
                break;
            default:
                return "no_type";
        }       

        return unification_table[index1][index2];
    }

	//O objetivo da função é retornar a unificação de tipos em atribuição ou
	//operação aritmética.

	//Quando é uma atribuição
	//A variável que está sofrenado cast (old_type), vai ganhar um cast para new_type (lado esquerdo)
    public static AST convertion_node_generator(String old_type, String new_type){
        
        if(old_type.equals("no_type")){
            return new AST(NodeKind.NULL_NODE);
        }
        
        // UPCAST
        if (old_type.equals("char") && new_type.equals("int") ){
            return new AST(NodeKind.CHAR2INT);
        } else if (old_type.equals("char") && new_type.equals("float") ){
            return new AST(NodeKind.CHAR2FLOAT);
        } else if (old_type.equals("int") && new_type.equals("float") ){
            return new AST(NodeKind.INT2FLOAT);
        } else{
            // DOWNCAST
            if (old_type.equals("int") && new_type.equals("char") ){
                return new AST(NodeKind.INT2CHAR);
            } else if (old_type.equals("float") && new_type.equals("char") ){
                return new AST(NodeKind.FLOAT2CHAR);   
            } else if (old_type.equals("float") && new_type.equals("int") ){
                return new AST(NodeKind.FLOAT2INT);    
            }
        }

        return new AST(NodeKind.NULL_NODE);
    }

    public static NodeKind string_to_nodekind(String s){
        switch(s) {
            case "char":
                return NodeKind.CHAR_VAL_NODE;
            case "int":
                return NodeKind.INT_VAL_NODE;
            case "float":
                return NodeKind.FLOAT_VAL_NODE;
            default:
                return NodeKind.NULL_NODE;
        }
    }

    public static boolean is_tree(AST a){
        switch(a.getNodeKind()) {
            case PLUS_NODE:
            case TIMES_NODE:
                return true;
            default:
                return false;
        }
    }
    
}
