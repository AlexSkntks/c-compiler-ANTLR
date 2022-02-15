package tables;
import java.util.ArrayList;

public class FuncTable {

    //Em implementações reais isso seria um tanela hash
    ArrayList<FunctionInfo> list = new ArrayList<FunctionInfo>();

    //Chamada da função
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

        if(lookUp(n.nome, ))

        list.add(n);
        return true;
    }
}
