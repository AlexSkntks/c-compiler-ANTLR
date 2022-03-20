package ast;

/*
 * Classe abstrata que define a interface do visitor para a AST.
 * Implementa o despacho do método 'visit' conforme o 'kind' do nó.
 * Com isso, basta herdar desta classe para criar um interpretador
 * ou gerador de código.
 */
public abstract class ASTBaseVisitor<T> {

	// Único método público. Começa a visita a partir do nó raiz
	// passado. Precisa ter outro nome porque tem a mesma assinatura
	// que o método "genérico" 'visit'.
	public void execute(AST root) {
		visit(root);
	}

	// Método "genérico" que despacha a visitação para os métodos
	// especializados conforme o 'kind' do nó atual. Igual ao código
	// em C. Novamente fica o argumento sobre usar OO ou não aqui.
	// Se tivéssemos trocentas classes especializando o nó da AST
	// esse despacho seria feito pela JVM. Aqui precisa fazer na mão.
	// Por outro lado, assim não precisa de trocentas classes com o
	// código todo espalhado entre elas...
	public T visit(AST node) {
		switch (node.kind) {
			case ASSIGN_NODE:
				return vistAssignNode(node);
			case INT_VAL_NODE:
				return visitintValNode(node);
			case FLOAT_VAL_NODE:
				return visitFloatValNode(node);
			case CHAR_VAL_NODE:
				return visitCharValNode(node);
			case PLUS_NODE:
				return visitPlusNode(node);
			case TIMES_NODE:
				return visitTimesNode(node);
			case NULL_NODE:
				return visitNullNode(node);
			case CHAR2INT:
				return visitChar2Int(node);
			case CHAR2FLOAT:
				return visitchar2Float(node);
			case INT2FLOAT:
				return visitInt2Float(node);
			case INT2CHAR:
				return visitInt2Char(node);
			case FLOAT2CHAR:
				return visitFloat2Char(node);
			case FLOAT2INT:
				return visitFloat2Int(node);
			case PARAMETER_INT_NODE:
				return visitParameterIntNode(node);
			case PARAMETER_CHAR_NODE:
				return visitParameterCharNode(node);
			case PARAMETER_FLOAT_NODE:
				return visitParameterFloatNode(node);
			case TYPE_VOID_NODE:
				return visitFuncTypeVoidNode(node);
			case PARAMS_NODE:
				return visitParamsNode(node);
			case FUNCTION_NODE:
				return visitFunctionNode(node);
			case FUNCTION_DECLARATION_NODE:
				return visitFunctionDeclarationNode(node);
			case VAR_INT_NODE:
				return visitVarIntNode(node);
			case VAR_FLOAT_NODE:
				return visitVarFloatNode(node);
			case VAR_CHAR_NODE:
				return visitVarCharNode(node);
			case VAR_DECLARATION_LIST_NODE:
				return visitvarDeclarationListNode(node);
			case VAR_DECLARATION_NODE:
				return visitvarDeclarationNode(node);
			case FUNC_TYPE_FLOAT_NODE:
				return visitFuncTypeFloatNode(node);
			case FUNC_TYPE_INT_NODE:
				return visitFuncTypeIntNode(node);
			case FUNC_TYPE_CHAR_NODE:
				return visitFuncTypeFCharNode(node);
			case FUNC_TYPE_VOID_NODE:
				return visitFuncTypeVoidNode(node);
			case COMPILATION_UNIT_NODE:
				return visitCompilationUnitNode(node);
			case EXTERNAL_DECLARATION:
				return visitExternalDeclaration(node);
			case BLOCK_ITEM_LIST:
				return visitBlockItemList(node);
			case JUMP_NODE:
				return visitJumpNode(node);
			case RETURN_NODE:
				return visitReturnNode(node);
			default:
				System.err.printf("Invalid kind: %s!\n", node.kind.toString());
				System.exit(1);
				return null;
		}
	}

	protected abstract T vistAssignNode(AST node);

	protected abstract T visitintValNode(AST node);

	protected abstract T visitFloatValNode(AST node);

	protected abstract T visitCharValNode(AST node);

	protected abstract T visitMinusNode(AST node);

	protected abstract T visitPlusNode(AST node);

	protected abstract T visitTimesNode(AST node);

	protected abstract T visitNullNode(AST node);

	protected abstract T visitChar2Int(AST node);

	protected abstract T visitchar2Float(AST node);

	protected abstract T visitInt2Float(AST node);

	protected abstract T visitInt2Char(AST node);

	protected abstract T visitFloat2Char(AST node);

	protected abstract T visitFloat2Int(AST node);

	protected abstract T visitParameterIntNode(AST node);

	protected abstract T visitParameterCharNode(AST node);

	protected abstract T visitParameterFloatNode(AST node);

	protected abstract T visitParamsNode(AST node);

	protected abstract T visitFunctionNode(AST node);

	protected abstract T visitFunctionDeclarationNode(AST node);

	protected abstract T visitVarIntNode(AST node);

	protected abstract T visitVarFloatNode(AST node);

	protected abstract T visitVarCharNode(AST node);

	protected abstract T visitvarDeclarationListNode(AST node);

	protected abstract T visitFuncTypeFloatNode(AST node);

	protected abstract T visitFuncTypeIntNode(AST node);

	protected abstract T visitFuncTypeFCharNode(AST node);

	protected abstract T visitFuncTypeVoidNode(AST node);

	protected abstract T visitCompilationUnitNode(AST node);

	protected abstract T visitExternalDeclaration(AST node);

	protected abstract T visitBlockItemList(AST node);

	protected abstract T visitJumpNode(AST node);

	protected abstract T visitReturnNode(AST node);

	protected abstract T visitvarDeclarationNode(AST node);
}
