# Lexer e Parser para Linguagem C, baseado no ANTLR
## Linguagem C

C é uma linguagem de programação compilada de propósito geral, estruturada, imperativa, procedural, padronizada pela Organização Internacional para Padronização (ISO), criada em 1972 por Dennis Ritchie na empresa AT&T Bell Labs para desenvolvimento do sistema operacional Unix (originalmente escrito em Assembly).

C é uma das linguagens de programação mais populares e existem poucas arquiteturas para as quais não existem compiladores para C. C tem influenciado muitas outras linguagens de programação (por exemplo, a linguagem Java), mais notavelmente C++, que originalmente começou como uma extensão para C.

Usamos a ferramenta [ANTLR](https://www.antlr.org/) que é implementada em JAVA e cria automaticamente o lexer e o parser do compilador diante da gramática representada em um arquivo _.g4_. Como já existem muitas referências na internet sobre gramáticas de linguagens de programação, em particular de C que é bastante famoso, fizemos o uso deste [repositório](https://github.com/antlr/grammars-v4/tree/master/c).

Gramática derivada de https://docs.microsoft.com/pt-br/cpp/c-language e https://github.com/antlr/grammars-v4/tree/master/c

## Uso
1. Instalar o ANTLR:

 * Download ANTLR: https://www.antlr.org/ exatamente como o especificado. (Versão 4.9.2 ou altere o a variável ANTLR_PATH para a sua versão do antlr)

2. Alterar a variável ROOT no makefile para o onde está o arquivo _.jar_ do ANTLR

3. Executar os passos abaixo para reconhecer as entradas:

 * Digite no terminal na pasta do projeto que o antlr gere o lexe e o parser:

		$ make antlr

* Digite no terminal para compilar os arquivos _.java_

		$ make javac

* Digite no terminal para executar o antlr na grmática do C:

		$ make run FILE=arquivo_de_teste

* Para rodar os testes Corretos execute o comando
		
		$ make runall
		
* Para rodar os testes Errados execute o comando
		
		$ make runallFalse

## Código MIPS

O compilador desenvolvido gera código para a arquitetura MIPS. O MIPS é uma arquitetura baseada em registrador, ou seja, a CPU usa apenas registradores para realizar as suas operações aritméticas e lógicas. Existem outros tipos de processadores, tais como processadores baseados em pilha e processadores baseados em acumuladores. Processadores baseados no conjunto de instruções do MIPS estão em produção desde 1988. Ao longo do tempo foram feitas várias melhorias do conjunto de instruções.

Você pode baixar uma versão do compilador de MIPS [aqui](http://courses.missouristate.edu/kenvollmar/mars/download.htm) e testar o código gerado. Lembrando que a saída não é gerada em um arquivo, mas você pode redirecionar a saída para um arquivo.

Um exemplo simples que soma x com y e armazena o resultado em x:

		.data
		x: 1
		y: 3
		.text
		lw $t1, x
		lw $t2, y
		add $t3, $t2, $t1
		sw $t3, x

## Funcionalidades implementadas

Durante o processo de desenvolvimento overam fatores como tempo e decisões de implementação que fizeram com que o escopo do compilador fosse reduzido.

* IMPORTANTE: Bibliotecas não foram "importadas" pois não é papel do compilador em si fazer a linkedição dos arquivos. Portanto você não conseguirá usar stdio.h para funções de escrita e leitura na saída padrão.

* Expressões aritméticas (apenas + e *). Com excessão de números negativos

* Declaração, inicialização e uso de variáveis

* Conversões de tipos

* Analisador semântico e sintático que identifica se o código faz sentido. Por exemplo, variáveis inexistentes ou se você esqueceu um ';'.

Para vizualizar um conjunto maior de funcionalidades vide [analisador sintático](link) ou [analisador semântico](link).