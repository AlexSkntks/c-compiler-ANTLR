package tables;

import java.util.ArrayList;

import javax.lang.model.element.VariableElement;

public class VarTable{

	VarInfo info;
	ArrayList<VarInfo> list = new ArrayList<VarInfo>();

	public Boolean verifyIfAlreadyExists(String name){

		for (VarInfo varInfo : list) {
			if(varInfo.getName().compareTo(name) == 0){
				return true;
			}
		}
		return false;
	}

	public void insert(VarInfo i){
		if(this.verifyIfAlreadyExists(i.name)){
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

