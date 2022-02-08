package tables;

import java.util.ArrayList;

public class StrTable {

	VarInfo info;
	ArrayList<String> list = new ArrayList<String>();

	public Boolean verifyIfAlreadyExists(String str){

		for (String strInfo : list) {
			if(strInfo == str){
				return true;
			}
		}

		return false;
	}

	/**
	 * Deveria lançar uma exessão quando tenta inserir algo que já existe (declarar novamente a variável)
	 */
	public void insert(String str){
		if(this.verifyIfAlreadyExists(str)){
			return;
		}
		list.add(str);
	}

	public void showTable(){
		for (String strInfo : list) {
			System.out.println("String: " + strInfo);
		}
	}
}


