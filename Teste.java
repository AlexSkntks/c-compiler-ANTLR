import ast.AST;
import ast.NodeKind;

public class Teste {
    public static void main(String[] args) {

        /**
         * 1+1*1*d
         */

        //Criando root com NO_TYPE... Não tem opção de criar sem valor
        AST root = new AST( NodeKind.PLUS_NODE, 0);

        //Criação dos filhos
        AST node_multi = new AST(NodeKind.TIMES_NODE);
        AST node_multi2 = new AST(NodeKind.TIMES_NODE);
        AST node1 = new AST(NodeKind.FLOAT_VAL_NODE, 1.0f);
        AST node2 = new AST(NodeKind.INT_VAL_NODE, 1);
        AST node3 = new AST(NodeKind.FLOAT_VAL_NODE, 1.0f);
        AST node4 = new AST(NodeKind.CHAR_VAL_NODE, 'd');

        //Adicionando os filhos
        root.addChild(node1);
        root.addChild(node_multi);
        node_multi.addChild(node2);
        node_multi.addChild(node_multi2);
        node_multi2.addChild(node3);
        node_multi2.addChild(node4);

        // root.addChild(child);

        AST.printDot(root);
    }
}