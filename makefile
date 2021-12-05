# Modifique as variaveis conforme o seu setup.

JAVA=java
JAVAC=javac

# Eu uso ROOT como o diretório raiz para os meus labs.
YEAR=$(shell pwd | grep -o '20..-.')
ROOT=/usr/local/lib

ANTLR_PATH=$(ROOT)/antlr-4.9.3-complete.jar
CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)

# Diretório para os arquivos .class
BIN_PATH=bin

# Comandos como descritos na página do ANTLR.
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) org.antlr.v4.gui.TestRig

# Diretório para aonde vão os arquivos gerados.
GEN_PATH=parser

# Diretório para os casos de teste
DATA=$(ROOT)/tests
IN1=$(DATA)/entrada/corretas
IN2=$(DATA)/entrada/incorretas

all: antlr javac
	@echo "Done."

# Opção -no-listener foi usada para que o ANTLR não gere alguns arquivos
# desnecessários para o momento. Isto será explicado melhor nos próximos labs.
antlr: C.g4
	$(ANTLR4) -no-listener -o $(GEN_PATH) C.g4

javac:
	rm -r $(BIN_PATH)
	mkdir $(BIN_PATH)
	$(JAVAC) $(CLASS_PATH_OPTION) -d $(BIN_PATH) */*.java Main.java

run:
	$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) Main $(FILE)

runall:
	echo Entrada de dados que o lexer+parser reconhece \
    -for FILE in $(IN1)/*.c; do \
         echo -e "\nRunning $${FILE}" && \
		 $(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) Main $${FILE}; \
    done;
	echo Entrada de dados que o lexer+parser nao reconhece \
	-for FILE in $(IN2)/*.c; do \
         echo -e "\nRunning $${FILE}" && \
		 $(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) Main $${FILE}; \
    done;

clean:
	@rm -rf $(GEN_PATH) $(BIN_PATH)