    char seila(char letra, int size){//1
        return 'a';
    }

    int seila(int b){
        return 0;
    }

    //funções nome/tipos de parâmetro funcInfo
    //variaveis / nome/ tipo / escopo
    int soma(int a, int b){//2
        int r = a + b;
        return 10;
    }

    int main()
    {//3
        int i = 0;
		int sum = 0;
		float f = 0.0;
        double dist = 0.0;
        char letra = 'a';
        printf("Hello");
        for ( i = 1; i <= LAST; i++ ) {
          sum += i;
        } /*-for-*/
        printf("sum = %d\n", sum);

        return 0;
    }

