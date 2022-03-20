package checker;

import java.util.Stack;

public class Pilha {

    Stack<String> p = new Stack<String>();
    
    public void push(String str){
        p.push(str);
    }

    public String pop(){
        return p.pop();
    }

    public int popi(){
        return Integer.parseInt(p.pop());
    }

    public char popc(){
        return p.pop().charAt(0);
    }

    public float popf(){
        return Float.parseFloat(p.pop());
    }
}
