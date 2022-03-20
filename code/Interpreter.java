package code;
import ast.*;
import tables.FuncTable;
import tables.VarTable;

public class Interpreter extends ASTBaseVisitor<AST>{

    private final DataStack stack;
    private final Memory memory;
    private final VarTable vt;
    private final FuncTable ft;

    public Interpreter(VarTable vt, FuncTable ft){
        this.vt = vt;
        this.ft = ft;
        this.stack = new DataStack();
		this.memory = new Memory(vt);
    }

    @Override
    protected AST vistAssignNode(AST node) {
        System.out.println("SUBTREE-AST");
        AST.printDot(node);
        return null;
    }

    @Override
    protected AST visitintValNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitFloatValNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitCharValNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitMinusNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitPlusNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitTimesNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitNullNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitChar2Int(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitchar2Float(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitInt2Float(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitInt2Char(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitFloat2Char(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitFloat2Int(AST node) {
        // TODO Auto-generated method stub
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
    protected AST visitFunctionDeclarationNode(AST node) {
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
    protected AST visitvarDeclarationListNode(AST node) {
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
    protected AST visitCompilationUnitNode(AST node) {// 

        for(int i = 0; i < node.getChildrenSize(); i++){
            visit(node.getChild(i));//AST
            //System.out.println("FILHO " + node.getChild(i).getNodeKind().toString());
        }
        
        return null;
    }

    @Override
    protected AST visitExternalDeclaration(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AST visitBlockItemList(AST node) {
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

    @Override
    protected AST visitvarDeclarationNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
