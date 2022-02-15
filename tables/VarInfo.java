package tables;

public class VarInfo{
	String type;
	int line;
	String name;
	int escopo;

	public VarInfo(String name, String type, int line, int escopo){
		this.name = name;
		this.type = type;
		this.line = line;
		this.escopo = escopo;
	}

    //Provável erro
	void showInfo(){
		System.out.println("Name: " + this.name + " type: " + this.type + " Line: " + String.valueOf(this.line) + " Escopo : "  + this.escopo);
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