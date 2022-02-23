package tables;

import java.util.ArrayList;

public class FuncTable {

    //Em implementações reais isso seria um tanela hash
    ArrayList<FunctionInfo> list = new ArrayList<FunctionInfo>();

    //Chamada da função
    public Boolean verifyIfAlreadyExists(String nome){

        for (FunctionInfo i : list) {

            if(i.getNome().equals(nome)){//Aqui verifica se a funçao está na tabela
                return true;
            }

        }
        return false;
    }

    public Boolean lookUp(String nome, ArrayList<String> args){

        for (FunctionInfo i : list) {

            if(i.getNome().equals(nome)){//Aqui verifica se a funçao está na tabela

                ArrayList<String> funcArgs = i.getParametros();//Recupera os parâmetros da função

                //Verifica se o tipo dos argumentos são válidos
                for(int j = 0; j < args.size(); j++){
                    if(!args.get(j).equals(funcArgs.get(j))){
                        return false;
                    }
                }
            }

        }
        return true;
    }

    public Boolean insert(FunctionInfo n){

        for (FunctionInfo i : list) {
            if(i.getNome().equals(n.getNome())){//Aqui verifica se a funçao está na tabela
                return false;
            }
        }

        list.add(n);
        return true;
    }

    public void addParams(String nome, ArrayList<String> params){
        for (FunctionInfo i : list) {
            if(i.getNome().equals(nome)){//Aqui verifica se a funçao está na tabela
                i.inicializaParametros(params);
            }
        }
    }

    public void addType(String nome, String tipo){
        for (FunctionInfo i : list) {
            if(i.getNome().equals(nome)){//Aqui verifica se a funçao está na tabela
                i.setTipo(tipo);
            }
        }
    }

    public String getType(String name){
        for (FunctionInfo i : list) {
            if(i.getNome().equals(name)){//Aqui verifica se a funçao está na tabela
                return i.getTipo();
            }
        }
        return null;
    }

    public void imprime(){
        for (FunctionInfo f : list) {
            f.imprime();
        }
    }

    public int getListSize(String nome) {
        for (FunctionInfo i : list) {
            if(i.getNome().equals(nome)){//Aqui verifica se a funçao está na tabela
                return i.getParametros().size();
            }
        }
        return -1;
    }
}
