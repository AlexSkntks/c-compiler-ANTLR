# Lexer e Parser para Linguagem C, baseado no ANTLR

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

