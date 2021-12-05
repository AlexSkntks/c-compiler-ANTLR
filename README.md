# Lexer para Linguagem C, baseado no ANTLR

Gramática derivada de https://docs.microsoft.com/pt-br/cpp/c-language e https://github.com/antlr/grammars-v4/tree/master/c

## Uso
Download ANTLR: https://www.antlr.org/ exatamente como o especificado.

```
$ make run FILE=arquivo_de_teste
... prints parse tree ...
```

```
$ make runall
... prints parse tree of all tests files ...
