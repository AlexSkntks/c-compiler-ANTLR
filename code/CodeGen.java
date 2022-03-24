package code;

import static code.Instruction.INSTR_MEM_SIZE;
import static code.OpCode.ADDf;
import static code.OpCode.ADDi;
import static code.OpCode.B2Ss;
import static code.OpCode.BOFb;
import static code.OpCode.CALL;
import static code.OpCode.CATs;
import static code.OpCode.DIVf;
import static code.OpCode.DIVi;
import static code.OpCode.EQUf;
import static code.OpCode.EQUi;
import static code.OpCode.EQUs;
import static code.OpCode.HALT;
import static code.OpCode.I2Ss;
import static code.OpCode.JUMP;
import static code.OpCode.LDIf;
import static code.OpCode.LDIi;
import static code.OpCode.LDWf;
import static code.OpCode.LDWi;
import static code.OpCode.LTHf;
import static code.OpCode.LTHi;
import static code.OpCode.LTHs;
import static code.OpCode.MULf;
import static code.OpCode.MULi;
import static code.OpCode.OROR;
import static code.OpCode.R2Ss;
import static code.OpCode.STWf;
import static code.OpCode.STWi;
import static code.OpCode.SUBf;
import static code.OpCode.SUBi;
import static code.OpCode.WIDf;
// import static typing.Type.BOOL_TYPE;
// import static typing.Type.INT_TYPE;
// import static typing.Type.REAL_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ast.AST;
import ast.ASTBaseVisitor;
import tables.FuncTable;
import tables.StrTable;
import tables.VarInfo;
import tables.VarTable;
// import typing.Type;

/*
 * Visitador da AST para geração básica de código. Funciona de
 * forma muito similar ao interpretador do laboratório anterior,
 * mas agora estamos trabalhando com um ambiente de execução 
 * com código de 3 endereços. Isto quer dizer que não existe mais
 * pilha e todas as operações são realizadas via registradores.
 * 
 * Note que não há uma área de memória de dados no código abaixo.
 * Esta área fica agora na TM, que a "arquitetura" de execução.
 */
public final class CodeGen extends ASTBaseVisitor<Integer> {

	private final Instruction code[]; // Code memory
	private final FuncTable ft;
	private final VarTable vt;
    private Memory memory;
    // Registradores
    private Integer[] intRegister = new Integer[32];
    private Float[] floatRegister = new Float[32];
    private int escopo = 0;
    private Boolean isInBlock = false;
    private Map<Integer, Integer> map = new HashMap<>();
	
	// Contadores para geração de código.
	// Próxima posição na memória de código para emit.
	private static int nextInstr;
	// Número de registradores temporários já utilizados.
	// Usamos um valor arbitrário, mas depois seria necessário
	// fazer o processo de alocação de registradores. Isto está
	// fora do escopo da disciplina.
	private static int intRegsCount;
	private static int floatRegsCount;
	
	public CodeGen(VarTable vt, FuncTable ft) {
		this.code = new Instruction[INSTR_MEM_SIZE];
		this.vt = vt;
        this.ft = ft;
        initMap();
        this.memory = new Memory(vt);
	}
	
	// Função principal para geração de código.
	@Override
	public void execute(AST root) {
		nextInstr = 0;
		intRegsCount = 0;
		floatRegsCount = 0;
	    // dumpStrTable();
	    visit(root);
	    // emit(HALT);
	    // dumpProgram();
	}
    // ----------------------------------------------------------------------------
	// Utils ---------------------------------------------------------------------
	
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
    // ----------------------------------------------------------------------------
	// Reg ---------------------------------------------------------------------
	private int newIntReg() {
		for (int i = 0; i < 32; i++) {
            if(this.intRegister[i] == null){
                return i;
            }
        }
        System.out.println("Número máximo(32) de registradores (int) foi excedido.");
        System.exit(1);
        return 0;
	}
    
	private int newFloatReg() {
		for (int i = 0; i < 32; i++) {
            if(this.floatRegister[i] == null){
                return i;
            }
        }
        System.out.println("Número máximo(32) de registradores (float) foi excedido.");
        System.exit(1);
        return 0;
	}
	// ----------------------------------------------------------------------------
	// Emits ----------------------------------------------------------------------
	
	private void emit(OpCode op, int o1, int o2, int o3) {
		Instruction instr = new Instruction(op, o1, o2, o3);
		// Em um código para o produção deveria haver uma verificação aqui...
	    code[nextInstr] = instr;
	    nextInstr++;
	}
	
	private void emit(OpCode op) {
		emit(op, 0, 0, 0);
	}
	
	private void emit(OpCode op, int o1) {
		emit(op, o1, 0, 0);
	}
	
	private void emit(OpCode op, int o1, int o2) {
		emit(op, o1, o2, 0);
	}

	private void backpatchJump(int instrAddr, int jumpAddr) {
	    code[instrAddr].o1 = jumpAddr;
	}

	private void backpatchBranch(int instrAddr, int offset) {
	    code[instrAddr].o2 = offset;
	}
	
	// ----------------------------------------------------------------------------
	// AST Traversal --------------------------------------------------------------
	
	// private int newIntReg() {
	// 	return intRegsCount++; 
	// }
    
	// private int newFloatReg() {
	// 	return floatRegsCount++;
	// }
	
    @Override
    protected Integer visitCompilationUnitNode(AST node) {
        // TODO Auto-generated method stub
        ArrayList<VarInfo> list = this.vt.getList();
        System.out.println(".data");
        for (VarInfo var : list) {
            if (var.getValue() != null){
                System.out.println(var.getName() + " : " + var.getValue());
            } else{
                System.out.println(var.getName() + " : 0");
            }
        }
        System.out.println(".text");
        for(int i = 0; i < node.getChildrenSize(); i++){
            visit(node.getChild(i));//AST
        }
        return null;
    }

    @Override
    protected Integer visitExternalDeclaration(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer vistAssignNode(AST node) {
        // TODO Auto-generated method stub
        String name = node.getChild(0).getText();
        String type = node.getChild(0).getNodeKind().toString();
        int escopoAtual = 0;
        if(isInBlock){
            escopoAtual = escopo;
        }
        // Lado direito da equação, tudo que será atribuido a variável.
        int r = visit(node.getChild(1));

        switch (type) {
            case "char":
                System.out.println("assign n foi implementado para char");
                System.exit(1);
                break;
            case "int":
                //Pega o valor da pilha
                int intValue = this.intRegister[r];
                //Pega o hash da variável para obter o seu índice em memória
                int intKey = hash(name, escopoAtual);
                //Utilza a chave para obter indice em memória e altera o valor
                memory.set(map.get(intKey), Word.fromInt(intValue));
                System.out.println("sw $t" + r + ", " + name);
                break;
            case "float":
                //Pega o valor da pilha
                float floatValue = this.floatRegister[r];
                //Pega o hash da variável para obter o seu índice em memória
                int floatKey = hash(name, escopoAtual);
                //Utilza a chave para obter indice em memória e altera o valor
                memory.set(map.get(floatKey), Word.fromFloat(floatValue));//Insere em byte
                break;
            default:
                System.out.println("Erro inesperado ocorreu!");
                System.exit(1);
                break;
        }

        return null;
    }

    // Carrega o valor inteiro num registrador válido
    @Override
    protected Integer visitintValNode(AST node) {
        // Retorna um indice de registrador válido
        int r = newIntReg();
        // Carrega o valor inteiro no registrador
        this.intRegister[r] = Integer.valueOf(node.getText());
        System.out.println("li $t" + r + ", " + node.getText());
        return r;
    }

    @Override
    protected Integer visitFloatValNode(AST node) {
        // Retorna um indice de registrador válido
        int r = newFloatReg();
        // Carrega o valor inteiro no registrador
        this.floatRegister[r] = Float.valueOf(node.getText());
        System.out.println("li $t" + r + ", " + node.getText());
        return r;
    }

    @Override
    protected Integer visitCharValNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitMinusNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitPlusNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitTimesNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitNullNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitChar2Int(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitchar2Float(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitInt2Float(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitInt2Char(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitFloat2Char(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitFloat2Int(AST node) {
        // TODO Auto-generated method stub
        int r = visit(node.getChild(0));
        float f = this.floatRegister[r];
        this.floatRegister[r] = null;
        Integer i = Math.round(f);
        r = newIntReg();
        this.intRegister[r] = i;
        return r;
    }

    @Override
    protected Integer visitParameterIntNode(AST node) {
        // TODO Auto-generated method stub
        // visit(node.getChild(idx));
        for(int i = 0; i < node.getChildrenSize(); i++){
            visit(node.getChild(i));//AST
        }
        return null;
    }

    @Override
    protected Integer visitParameterCharNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitParameterFloatNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitParamsNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitFunctionNode(AST node) {
        // TODO Auto-generated method stub
        for(int i = 0; i < node.getChildrenSize(); i++){
            visit(node.getChild(i));//AST
        }
        return null;
    }

    @Override
    protected Integer visitFunctionDeclarationNode(AST node) {
        // TODO Auto-generated method stub
        this.isInBlock = true;
        this.escopo++;
        visit(node.getChild(0));
        visit(node.getChild(1));
        // BlocItemList   
        visit(node.getChild(2)); 
        this.isInBlock = false;
        return null;
    }

    @Override
    protected Integer visitVarIntNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitVarFloatNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitVarCharNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitvarDeclarationListNode(AST node) {
        // TODO Auto-generated method stub
        for(int i = 0; i < node.getChildrenSize(); i++){
            visit(node.getChild(i));//AST
        }
        return null;
    }

    @Override
    protected Integer visitFuncTypeFloatNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitFuncTypeIntNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitFuncTypeFCharNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitFuncTypeVoidNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitBlockItemList(AST node) {
        // TODO Auto-generated method stub
        for(int i = 0; i < node.getChildrenSize(); i++){
            visit(node.getChild(i));//AST
        }
        return null;
    }

    @Override
    protected Integer visitJumpNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitReturnNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitvarDeclarationNode(AST node) {
        // TODO Auto-generated method stub
        visit(node.getChild(0));
        return null;
    }

}
