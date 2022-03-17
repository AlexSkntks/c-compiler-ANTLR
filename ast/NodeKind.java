package ast;

// Enumeração dos tipos de nós de uma AST.
// Adaptado da versão original em C.
// Algumas pessoas podem preferir criar uma hierarquia de herança para os
// nós para deixar o código "mais OO". Particularmente eu não sou muito
// fã, acho que só complica mais as coisas. Imagine uma classe abstrata AST
// com mais de 20 classes herdando dela, uma classe para cada tipo de nó...
public enum NodeKind {
	ASSIGN_NODE {
		public String toString() {
            return "=";
        }
	},
    IF_NODE {
		public String toString() {
            return "if";
        }
	},
    INT_VAL_NODE {
		public String toString() {
            return "int";
        }
	},
    FLOAT_VAL_NODE {
		public String toString() {
            return "float";
        }
	},
    CHAR_VAL_NODE {
		public String toString() {
            return "char";
        }
	},
    LT_NODE {
		public String toString() {
            return "<";
        }
	},
    MINUS_NODE {
		public String toString() {
            return "-";
        }
	},
    OVER_NODE {
		public String toString() {
            return "/";
        }
	},
    PLUS_NODE {
		public String toString() {
            return "+ ou -";
        }
	},
    STR_VAL_NODE {
		public String toString() {
            return "";
        }
	},
    TIMES_NODE {
		public String toString() {
            return "* ou /";
        }
	},
    NULL_NODE {
        public String toString() {
            return "null";
        }
    },
    
    // Nós de conversão
    // downcast (ou narrowing) não serão aceitos na linguagem
    // porém estamos refletindo sobre a forma de lidar com
    // atribuição que irá receber um tipo maior q o dela.
    // Nesse caso, deve ocorrer um narrowing? Ou retornar
    // um no_type seria o bastante para o caso da linguagem 
    // não aceitar a operação de narrowing?
    
    // UPCAST (ou widening)
    CHAR2INT{
        public String toString() {
            return "int";
        }
    },
    CHAR2FLOAT{
        public String toString() {
            return "float";
        }
    },
    INT2FLOAT{
        public String toString() {
            return "float";
        }
    },
    
    // DOWNCAST (ou narrowing)
    INT2CHAR{
        public String toString() {
            return "char";
        }
    },
    FLOAT2CHAR{
        public String toString() {
            return "char";
        }
    },
    FLOAT2INT{
        public String toString() {
            return "int";
        }
    },   

    // Leitura de parametro de função
    PARAMETER_INT_NODE{
        public String toString() {
            return "int";
        }
    },
    PARAMETER_CHAR_NODE{
        public String toString() {
            return "char";
        }
    },
    PARAMETER_FLOAT_NODE{
        public String toString() {
            return "float";
        }
    },
    TYPE_VOID_NODE{
        public String toString() {
            return "void";
        }
    },
    PARAMS_NODE{
        public String toString() {
            return "params";
        }
    },
    FUNCTION_NODE{
        public String toString() {
            return "function";
        }
    },
    FUNCTION_DECLARATION_NODE{
        public String toString() {
            return "function_definition";
        }
    },
    VAR_INT_NODE{
        public String toString() {
            return "int";
        }
    },
    VAR_FLOAT_NODE{
        public String toString() {
            return "float";
        }
    },
    VAR_CHAR_NODE{
        public String toString() {
            return "char";
        }
    },
    VAR_DECLARATION_LIST_NODE{
        public String toString() {
            return "var_declaration_list";
        }
    },
    VAR_DECLARATION_NODE{
        public String toString() {
            return "var_declaration";
        }   
    },
    FUNC_TYPE_FLOAT_NODE{
        public String toString() {
            return "float";
        }
    },
    FUNC_TYPE_INT_NODE{
        public String toString() {
            return "int";
        }
    },
    FUNC_TYPE_CHAR_NODE{
        public String toString() {
            return "char";
        }
    },
    FUNC_TYPE_VOID_NODE{
        public String toString() {
            return "void";
        }
    },
    COMPILATION_UNIT_NODE{
        public String toString() {
            return "AST";
        }
    },
    EXTERNAL_DECLARATION{
        public String toString() {
            return "external";
        }
    },
    BLOCK_ITEM_LIST{
        public String toString() {
            return "block_item_list";
        }
    },
    JUMP_NODE{
        public String toString() {
            return "jump_node";
        }
    },
    RETURN_NODE{
        public String toString() {
            return "return_node";
        }
    }
    ;

    public static boolean hasData(NodeKind kind) {
        switch(kind) {
            case INT_VAL_NODE:
            case FLOAT_VAL_NODE:
            case STR_VAL_NODE:
            case CHAR_VAL_NODE:
            case PARAMETER_CHAR_NODE:
            case PARAMETER_FLOAT_NODE:
            case PARAMETER_INT_NODE:
            case FUNCTION_NODE:
            case VAR_INT_NODE:
            case VAR_CHAR_NODE:
            case VAR_FLOAT_NODE:
            case FUNC_TYPE_FLOAT_NODE:
            case FUNC_TYPE_INT_NODE:
            case FUNC_TYPE_CHAR_NODE:
            case FUNC_TYPE_VOID_NODE:
            case INT2CHAR:
            case INT2FLOAT:
            case CHAR2FLOAT:
                return true;
            default:
                return false;
        }
    }
}
