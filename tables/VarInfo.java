package tables;

public class VarInfo{
	String type;
	int line;
	String name;
	int escopo;
	String value;

	public VarInfo(String name, String type, int line, int escopo, String value){
		this.name = name;
		this.type = type;
		this.line = line;
		this.escopo = escopo;
		this.value = value;
	}

    //Provável erro
	void showInfo(){
		if(this.value == null){
			System.out.println("NOME: [" + this.name + "], TIPO: [" + this.type + "], LINHA: " + String.valueOf(this.line) + ",  VALOR: [null], ESCOPO : "  + this.escopo);
		}else{
			System.out.println("NOME: [" + this.name + "], TIPO: [" + this.type + "], LINHA: " + String.valueOf(this.line) + "VALOR: [" + this.value +"], ESCOPO : "  + this.escopo);
		}
	}

	public String getType() {
		return type;
	}

	public int getLine() {
		return line;
	}

	public String getName() {
		return name;
	}

	public int getEscopo() {
		return escopo;
	}

	public String getValue() {
		return value;
	}
}