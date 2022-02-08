package tables;

public class VarInfo{
	String type;
	int line;
	String name;

	public VarInfo(String name, String type, int line){
		this.name = name;
		this.type = type;
		this.line = line;
	}

    //Provável erro
	void showInfo(){
		System.out.println("Name: " + this.name + " type: " + this.type + " Line: " + String.valueOf(this.line));
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
}