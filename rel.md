# Relatório 2ª entrega - Lexer + Parser + Analisador Semântico

## Linguagem C

C é uma linguagem de programação compilada de propósito geral, estruturada, imperativa, procedural, padronizada pela Organização Internacional para Padronização (ISO), criada em 1972 por Dennis Ritchie na empresa AT&T Bell Labs para desenvolvimento do sistema operacional Unix (originalmente escrito em Assembly).

C é uma das linguagens de programação mais populares e existem poucas arquiteturas para as quais não existem compiladores para C. C tem influenciado muitas outras linguagens de programação (por exemplo, a linguagem Java), mais notavelmente C++, que originalmente começou como uma extensão para C.

Usamos a ferramenta [ANTLR](https://www.antlr.org/) que é implementada em JAVA e cria automaticamente o lexer e o parser do compilador diante da gramática representada em um arquivo _.g4_. Como já existem muitas referências na internet sobre gramáticas de linguagens de programação, em particular de C que é bastante famoso, fizemos o uso deste [repositório](https://github.com/antlr/grammars-v4/tree/master/c).

---

## Desenvolvimento

#### CP1

    Ao utilizar o ANTLR nos deparamos com muitos erros ao tentar reconhecer as entradas pois quando pegamos a gramática do C no gitHub, o autor não indicou qual era a regra inicial da gramática para que o ANTLR inicasse a varredura dos tokens. Esse ponto específico nos gastou boas horas.

    Outra dificuldade foi conseguir identificar quando havia um erro no parser, pois o antlr faz toda a leitura e no final devolve os tokens sem uma mensagem muito clara de erro. Nós conseguimos alterar essa detecção e o programa para com uma mensagem ao se deparar com algum erro na entrada.

#### CP2

    Ao passarmos para a etapa de desenvolvimento semântico tivemos grande dificuldade na compreenção. Ao usarmos uma gramática muito extensa como base, explorar o visitador gerado pela mesma foi de grande dificuldade, tomando muito tempo. As arvores de precedência grandes, regras que são reutilizadas no mesmo contexto mas com finalidades diferentes dificultaram o tratamento dos dados de entrada para a produção da AST.

    Erros eram difíceis de serem localizados ao longo do desenvolvimento, pois poderiam estar em qualquer parte da arvore de métodos pela qual as informações precisavam passar para serem processadas.

    A declaração de variaveis e funções, execução de blocos sequenciais de código, declaração e manipulação de tipos básicos como int, real, string e bool (quando aplicável à LP) foram implementadas de forma simples.

    Um enorme tempo foi despendido para a confecção das operações aritiméticas, pois era necessário fazer casting dos tipos envolvidos,  organizar a ordem de avaliação dos operandos (ordem das operações aritimédicas, presença de parenteses que ditam uma certa ordem de execução dentre outras complexidades), além de análise de cada uma das variaveis e funções contidas nas operações. Foi a parte mais complexa.

### Lista mínima elementos que o compilador deve tratar corretamente

- Comandos de atribuição. ✔️
- Execução de blocos sequenciais de código. ✔️
- Declaração e manipulação de tipos básicos como int, real, string e bool (quando aplicável à LP). ✔️
- Declaração e execução correta de chamadas de função com número de parâmetros fixos (não precisa ser varargs). ✔️
- Sistema de tipos que trata adequadamente todos os tipos permitidos. ✔️
- Operações aritméticas e de comparação básicas (+, ∗, <, ==, etc, etc). 🕓
- Pelo menos uma estrutura de escolha (if-then-else) e uma de repetição (while, for, etc). ❌
- Declaração e manipulação de pelo menos um tipo composto (vetores, listas em Python, etc). ❌
- Operações de IO básicas sobre stdin e stdout para permitir testes. ❌

🕓 No caso das operações aritiméticas, o analisador semântico consegue reconhecer a hierarquia de precedência entre as operações artiméticas básicas, tais como (+, -, *, / e %) e também expressões aninhadas (exemplo: 1+3 * (3-5) ). Porém, a AST gerada não consegue diferenciar entre caracteres no mesmo nível de precedência, tais como (+ ou -) e ( *, / e % ), exibindo assim uma informação dúvia como um nó contendo (+ ou -). As operações binárias, tais como ==, !=, < ou > não foram implementadas.
### Testes

#### CP1
    Executamos testes sobre if else aninhados, alguns if's sem else e if com apenas umas linha de comando. Sobre parenteses encaixados (exceto o erro relatado acima). Nomes de variáveis, falta de ';', operações aritméticas sem um dos operandos, ou operando incorreto. Falta de chaves. Reconhecimento de estruturas e palavras reservadas da linguagem.

    Até onde o grupo conseguiu cobrir, o analisador léxico/sintático está correto, conseguindo indicar os erros e reconhecer entradas corretas.
#### CP2
    Executamos testes principalemente, em anúncio de funções e variáveis, e utilização dos mesmos em contextos aritiméticos e de atribuição. Para if/else, loops e formas compostas a linguagem não está apta a gerar erros no momento.
