package tables;

import java.util.ArrayList;

public class VarTable{

	VarInfo info;
	ArrayList<VarInfo> list = new ArrayList<VarInfo>();

	public Boolean lookUp(String name, int escopo){

		for (VarInfo varInfo : list) {
			//compara se a variável pertence ao escopo
			if(varInfo.getName().compareTo(name) == 0 && (varInfo.getEscopo() == escopo)){
				return true;
			}
		}
		return false;
	}

	public void insert(VarInfo i){
		if(this.lookUp(i.name, i.escopo)){
			return;
		}
		list.add(i);
	}

	public void showTable(){
		for (VarInfo info : list) {
			info.showInfo();
		}
	}
}

