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
    };

    public static boolean hasData(NodeKind kind) {
        switch(kind) {
            case INT_VAL_NODE:
            case FLOAT_VAL_NODE:
            case STR_VAL_NODE:
            case CHAR_VAL_NODE:
                return true;
            default:
                return false;
        }
    }
}
