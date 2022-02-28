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
            return "+";
        }
	},
    STR_VAL_NODE {
		public String toString() {
            return "";
        }
	},
    TIMES_NODE {
		public String toString() {
            return "*";
        }
	},
    NULL_NODE {
        public String toString() {
            return null;
        }
    },
    INT2FLOAT{
        public String toString() {
            return null;
        }
    },
    CHAR2INT{
        public String toString() {
            return null;
        }
    },
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
                return true;
            default:
                return false;
        }
    }
}
