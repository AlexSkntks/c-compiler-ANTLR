package tables;

public class VarInfo{
	String type;
	int line;
	String name;
	int escopo;
	String valor;

	public VarInfo(String name, String type, int line, int escopo, String valor){
		this.name = name;
		this.type = type;
		this.line = line;
		this.escopo = escopo;
		this.valor = valor;
	}

    //Provável erro
	void showInfo(){
		if(this.valor == null){
			System.out.println("NOME: [" + this.name + "], TIPO: [" + this.type + "], LINHA: " + String.valueOf(this.line) + ",  VALOR: [null], ESCOPO : "  + this.escopo);
		}else{
			System.out.println("NOME: [" + this.name + "], TIPO: [" + this.type + "], LINHA: " + String.valueOf(this.line) + "VALOR: [" + this.valor +"], ESCOPO : "  + this.escopo);
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
}