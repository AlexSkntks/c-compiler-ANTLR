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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.jdi.Value;

import ast.AST;
import ast.ASTBaseVisitor;
import tables.FuncTable;
import tables.StrTable;
import tables.VarInfo;
import tables.VarTable;


public final class CodeGen extends ASTBaseVisitor<Integer> {

	private final Instruction code[]; // Code memory
	private final FuncTable ft;
	private final VarTable vt;
    private Memory memory;
    BufferedWriter writer;

    // Registradores são vetor de Integer,
    // dessa forma podemos colocar null nas posições
    // e procurar por um registrador vazio
    private Integer[] intRegister = new Integer[32];
    private Float[] floatRegister = new Float[32];


    private int escopo = 0;
    private Boolean isInBlock = false;
    private Map<Integer, Integer> map = new HashMap<>();
	
	// Contadores para geração de código.
	// Próxima posição na memória de código para emit.
	private static int nextInstr;
	
	public CodeGen(VarTable vt, FuncTable ft) {
		this.code = new Instruction[INSTR_MEM_SIZE];
		this.vt = vt;
        this.ft = ft;
        initMap();
        this.memory = new Memory(vt);
        try {
            this.writer = new BufferedWriter(new FileWriter("saida.asm"));
            
        } catch (IOException e) {
            System.out.println("Um erro ocorreu.");
            System.exit(1);
        }
	}
	
	// Função principal para geração de código.
	@Override
	public void execute(AST root) {
		nextInstr = 0;
	    visit(root);
        try {
            this.writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    // Retorna um dado da memória
    // Recebe um nó de variável
    private Word getFromMemory(AST node){
        // Pega o escopo da variável
        int escopoAtual = 0;
        if(this.isInBlock){
            escopoAtual = this.escopo;
        }
        String name = node.getText();
        int floatKey = hash(node.getText(), escopoAtual);
        Integer id = map.get(floatKey);
        // Verifica se existe uma chave válida para essa variável no escopo da função
        // Caso não exista, o escopo é zero
        if(id == null){
            floatKey= hash(name, 0);
            id = map.get(floatKey);
        }
        Word w = memory.get(id);
        return w;
    }
    // ----------------------------------------------------------------------------
	// Reg ---------------------------------------------------------------------
	
    //  Ve
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
	
    @Override
    protected Integer visitCompilationUnitNode(AST node){
        // TODO Auto-generated method stub
        ArrayList<VarInfo> list = this.vt.getList();
        System.out.println(".data");
        try {
            this.writer.write(".data\n");
            
        } catch (Exception e) {
            System.out.println("Error na escrita.");
        }

        // Aloca espaço na memória para as variáveis que foram declaradas
        // no programa. E inicializa as variáveis que recebem um valor 
        // logo na inicialização
        for (VarInfo var : list) {
            if (var.getValue() != null){
                System.out.println(var.getName() + ": " + var.getValue());
                try {
                    this.writer.write(var.getName() + ": " + var.getValue() + "\n");
                    
                } catch (Exception e) {
                    System.out.println("Error na escrita.");
                }
            } else{
                System.out.println(var.getName() + ": 0");
                try {
                    this.writer.write(var.getName() + ": 0" + "\n");
                    
                } catch (Exception e) {
                    System.out.println("Error na escrita.");
                    System.exit(1);
                }
            }
        }

        System.out.println(".text");
        try {
            this.writer.write(".text" + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

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
        String name = node.getChild(0).getText();
        String type = node.getChild(0).getNodeKind().toString();
        int escopoAtual = 0;
        if(isInBlock){
            escopoAtual = escopo;
        }
        System.out.println(" # assign ");
        try {
            this.writer.write(" # assign " + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }
        // Lado direito da equação, tudo que será atribuido a variável.
        int r = visit(node.getChild(1));
        switch (type) {
            case "char":
                // Os valores em mmória são todos INT, então o tratamento para 
                // char é o mesmo
            case "int":
                int intValue = this.intRegister[r];
                //Pega o hash da variável para obter o seu índice em memória
                int intKey = hash(name, escopoAtual);
                //Utilza a chave para obter indice em memória e altera o valor
                memory.set(map.get(intKey), Word.fromInt(intValue));

                System.out.println("sw $t" + r + ", " + name);
                try {
                    this.writer.write("sw $t" + r + ", " + name + "\n");
                } catch (Exception e) {
                    System.out.println("Error na escrita.");
                    System.exit(1);
                }

                break;
            case "float":
                float floatValue = this.floatRegister[r];
                //Pega o hash da variável para obter o seu índice em memória
                int floatKey = hash(name, escopoAtual);
                //Utilza a chave para obter indice em memória e altera o valor
                memory.set(map.get(floatKey), Word.fromFloat(floatValue));//Insere em byte
                
                System.out.println("s.s $f" + r + ", " + name);
                try {
                    this.writer.write("s.s $f" + r + ", " + name + "\n");
                } catch (Exception e) {
                    System.out.println("Error na escrita.");
                    System.exit(1);
                }

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
        try {
            this.writer.write("li $t" + r + ", " + node.getText() + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        return r;
    }

    @Override
    protected Integer visitFloatValNode(AST node) {
        // Retorna um indice de registrador válido
        int r = newFloatReg();
        // Carrega o valor inteiro no registrador
        this.floatRegister[r] = Float.valueOf(node.getText());

        System.out.println("li $t" + r + ", " + node.getText());
        try {
            this.writer.write("li $t" + r + ", " + node.getText() + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        return r;
    }

    @Override
    protected Integer visitCharValNode(AST node) {
        // Retorna um indice de registrador válido
        int r = newIntReg();
        // Carrega o valor inteiro no registrador
        this.intRegister[r] = (int)node.getText().charAt(1);

        System.out.println("li $t" + r + ", " + node.getText());
        try {
            this.writer.write("li $t" + r + ", " + node.getText() + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        return r;
    }

    @Override
    protected Integer visitMinusNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitPlusNode(AST node) {
        System.out.println(" # sum");
        try {
            this.writer.write(" # sum" + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        switch (node.getText()) {
            case "char":
            //É o mesmo caso que o int, pois um char é representado por um número inteiro
            case "int":
                // Chama o visitador para o filho da esq
                // temp1 e 2 são os indices dos valores para soma
                // enquanto temp3 será o resultado
                int temp1 = visit(node.getChild(0));
                int temp2 = visit(node.getChild(1));
                int temp3 = this.newIntReg();

                // Pega os valores a serem somados pelos seus indices
                int rInt = this.intRegister[temp1];
                int lInt = this.intRegister[temp2];

                // Soma os valores
                int resultInt = lInt + rInt;
                // guarda o resultado da soma no indice correto
                this.intRegister[temp3] = resultInt;

                // Printa a soma em mips
                System.out.println("add $t" + temp3 + ", $t" + temp2 + ", $t" + temp1);
                try {
                    this.writer.write("add $t" + temp3 + ", $t" + temp2 + ", $t" + temp1 + "\n");
                } catch (Exception e) {
                    System.out.println("Error na escrita.");
                    System.exit(1);
                }
                
                this.intRegister[temp1] = null;
                this.intRegister[temp2] = null;
                // retorna indice da soma
                return temp3;
            case "float":
                int t1 = visit(node.getChild(0));
                int t2 = visit(node.getChild(1));
                int t3 = this.newFloatReg();

                // Pega os valores a serem somados pelos seus indices
                Float rFloat = this.floatRegister[t1];
                Float lFloat= this.floatRegister[t2];

                // Soma os valores
                Float resultFloat = lFloat + rFloat;
                // guarda o resultado da soma no indice correto
                this.floatRegister[t3] = resultFloat;

                // Printa a soma em mips
                System.out.println("add.s $f" + t3 + ", $f" + t2 + ", $f" + t1);
                try {
                    this.writer.write("add.s $f" + t3 + ", $f" + t2 + ", $f" + t1 + "\n");
                } catch (Exception e) {
                    System.out.println("Error na escrita.");
                    System.exit(1);
                }

                this.floatRegister[t1] = null;
                this.floatRegister[t2] = null;
                return t3;
            default:

                System.out.println("Bad operation with +");
                System.exit(1);
                break;
        }
        return null;
    }

    @Override
    protected Integer visitTimesNode(AST node) {

        System.out.println(" # times");
        try {
            this.writer.write(" # times" + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        switch (node.getText()) {
            case "char":
            //É o mesmo caso que o int, pois um char é representado por um número inteiro
            case "int":
                // Chama o visitador para o filho da esq
                // temp1 e 2 são os indices dos valores para soma
                // enquanto temp3 será o resultado
                int temp1 = visit(node.getChild(0));
                int temp2 = visit(node.getChild(1));
                int temp3 = this.newIntReg();

                // Pega os valores a serem somados pelos seus indices
                int rInt = this.intRegister[temp1];
                int lInt = this.intRegister[temp2];

                // Soma os valores
                int resultInt = lInt * rInt;
                // guarda o resultado da soma no indice correto
                this.intRegister[temp3] = resultInt;

                // Printa a soma em mips
                System.out.println("mult $t" + temp2 + ", $t" + temp1);
                try {
                    this.writer.write("mult $t" + temp2 + ", $t" + temp1 + "\n");
                } catch (Exception e) {
                    System.out.println("Error na escrita.");
                    System.exit(1);
                }

                //  O resultado da multiplicação em MIPS é armazenado em dois registradores
                //  hi e lo. Não estamos tratando Overflow, portanto apenas multiplicações dentro
                //  da faixa de valores de 32bits serão exibidos corretamente

                // Traz o resultado do lo
                System.out.println("mflo $t" + temp3);
                try {
                    this.writer.write("mflo $t" + temp3 + "\n");
                } catch (Exception e) {
                    System.out.println("Error na escrita.");
                    System.exit(1);
                }

                this.intRegister[temp1] = null;
                this.intRegister[temp2] = null;
                // retorna indice da soma
                return temp3;
            case "float":
                int t1 = visit(node.getChild(0));
                int t2 = visit(node.getChild(1));
                int t3 = this.newFloatReg();

                // Pega os valores a serem somados pelos seus indices
                Float rFloat = this.floatRegister[t1];
                Float lFloat= this.floatRegister[t2];

                // Soma os valores
                Float resultFloat = lFloat + rFloat;
                // guarda o resultado da soma no indice correto
                this.floatRegister[t3] = resultFloat;

                // Printa a soma em mips
                System.out.println("add.s $f" + t3 + ", $f" + t2 + ", $f" + t1);
                try {
                    this.writer.write("add.s $f" + t3 + ", $f" + t2 + ", $f" + t1 + "\n");
                } catch (Exception e) {
                    System.out.println("Error na escrita.");
                    System.exit(1);
                }

                this.floatRegister[t1] = null;
                this.floatRegister[t2] = null;
                return t3;
            default:
                System.out.println("Bad operation with +");
                System.exit(1);
                break;
        }
        return null;
    }

    @Override
    protected Integer visitNullNode(AST node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer visitChar2Int(AST node) {
        return visit(node.getChild(0));
    }

    @Override
    protected Integer visitchar2Float(AST node) {

        System.out.println("# char to float");
        try {
            this.writer.write("# char to float" + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        int r = visit(node.getChild(0));
        int i = this.intRegister[r];
        this.intRegister[r] = null;
        float f = (float) i;

        //Coloca o valor inteiro em um registrador
        int tempRt = newIntReg();
        System.out.println("li $t" + tempRt + ", " + i);
        try {
            this.writer.write("li $t" + tempRt + ", " + i + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        //Coloca o valor para um registrador de float
        int tempFloatRt = newFloatReg();
        this.floatRegister[tempFloatRt] = f;

        System.out.println("mtc1 $t" + tempRt + ", $f" + tempFloatRt);
        try {
            this.writer.write("mtc1 $t" + tempRt + ", $f" + tempFloatRt + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        //Converte para IEEE
        System.out.println("cvt.s.w $f" + tempFloatRt + ", $f" + tempFloatRt);
        try {
            this.writer.write("cvt.s.w $f" + tempFloatRt + ", $f" + tempFloatRt + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        return tempFloatRt;
    }


    @Override
    protected Integer visitInt2Float(AST node) {

        System.out.println("# int to float");
        try {
            this.writer.write("# int to float" + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        int r = visit(node.getChild(0));
        int i = this.intRegister[r];
        this.intRegister[r] = null;
        float f = (float) i;

        //Coloca o valor inteiro em um registrador
        int tempRt = newIntReg();
        System.out.println("li $t" + tempRt + ", " + i);
        try {
            this.writer.write("li $t" + tempRt + ", " + i + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        //Coloca o valor para um registrador de float
        int tempFloatRt = newFloatReg();
        this.floatRegister[tempFloatRt] = f;
        System.out.println("mtc1 $t" + tempRt + ", $f" + tempFloatRt);
        try {
            this.writer.write("mtc1 $t" + tempRt + ", $f" + tempFloatRt + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        //Converte para IEEE
        System.out.println("cvt.s.w $f" + tempFloatRt + ", $f" + tempFloatRt);
        try {
            this.writer.write("cvt.s.w $f" + tempFloatRt + ", $f" + tempFloatRt + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        return tempFloatRt;
    }

    @Override
    protected Integer visitInt2Char(AST node) {
        return visit(node.getChild(0));
    }

    @Override
    protected Integer visitFloat2Char(AST node) {
        int r = visit(node.getChild(0));
        float f = this.floatRegister[r];
        this.floatRegister[r] = null;
        Integer i = Math.round(f);
        r = newIntReg();
        this.intRegister[r] = i;
        return r;
    }

    @Override
    protected Integer visitFloat2Int(AST node) {

        System.out.println("# float to int");
        try {
            this.writer.write("# float to int" + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }

        //Recebe o registrador com o valor a ser convertido
        int rF = visit(node.getChild(0));
        float f = this.floatRegister[rF];
        this.floatRegister[rF] = null;

        //Converte para inteiro
        Integer i = Math.round(f);

        //Armazena em um registrador inteiro
        int rI = newIntReg();
        this.intRegister[rI] = i;

        // Como o valor é convertido nesse trecho de código
        // basta alocar um registrador inteiro e colocar esse valors
        System.out.println("li $t" + rI + ", " + i);
        try {
            this.writer.write("li $t" + rI + ", " + i + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }
        return rI;
    }

    @Override
    protected Integer visitParameterIntNode(AST node) {
        // TODO Auto-generated method stub
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
        String name = node.getChild(1).getText();
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
        Word w = getFromMemory(node);
        int i = w.toInt();
        int r = this.newIntReg();
        this.intRegister[r]= i;
        System.out.println("lw $t" + r + ", " + node.getText());
        try {
            this.writer.write("lw $t" + r + ", " + node.getText() + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }
        return r;
    }

    @Override
    protected Integer visitVarFloatNode(AST node) {
        Word w = getFromMemory(node);
        float f = w.toFloat();
        int r = this.newFloatReg();
        this.floatRegister[r] = f;
        System.out.println("lw $t" + r + ", " + node.getText());
        try {
            this.writer.write("lw $t" + r + ", " + node.getText() + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }
        return r;
    }

    @Override
    protected Integer visitVarCharNode(AST node) {
        Word w = getFromMemory(node);
        int i = w.toInt();
        int r = this.newIntReg();
        this.intRegister[r]= i;
        System.out.println("lw $t" + r + ", " + node.getText());
        try {
            this.writer.write("lw $t" + r + ", " + node.getText() + "\n");
        } catch (Exception e) {
            System.out.println("Error na escrita.");
            System.exit(1);
        }
        return r;
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
