import java.util.ArrayList;

public class FunctionInfo{
    
    String nome;
    String tipo;//Retonrno
	ArrayList<String> parametros = new ArrayList<String>();

    FunctionInfo(String nome, String tipo, ArrayList<String> args){
        this.nome = nome;
        this.tipo = tipo;
        this.parametros = args;
    }

    public void imprime(){

        System.out.print("F - Nome : " nome + " parametros ");
        for (String i : this.parametros) {
            System.out.print(i + ", ");
        }
        System.out.println();

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
    
}



