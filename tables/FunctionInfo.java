package tables;

import java.util.ArrayList;

public class FunctionInfo{
    
    private String nome;
    private String tipo;//Retonrno
	private ArrayList<String> parametros = new ArrayList<String>();

    public FunctionInfo(String nome, String tipo){
        this.nome = nome;
        this.tipo = tipo;
    }

    public void imprime(){

        System.out.print("F-Nome: [" + this.nome + "] parametros ");
        if(this.parametros.isEmpty()){
            System.out.println("NO PARAMS");
        }else{
            for (String i : this.parametros) {
                System.out.print(i + ", ");
            }
        }

        System.out.println();

    }

    public void inicializaParametros(ArrayList<String> params){
        this.parametros = params;
    }

    public String getNome() {
        return nome;
    }

    public ArrayList<String> getParametros() {
        return parametros;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
}



