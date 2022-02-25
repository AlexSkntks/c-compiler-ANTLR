# Modifique as variaveis conforme o seu setup.

JAVA=java
JAVAC=javac

# Eu uso ROOT como o diretório raiz para os meus labs.
YEAR=$(shell pwd | grep -o '20..-.')
# Aleks
ROOT=/usr/local/lib
ANTLR_PATH=$(ROOT)/antlr-4.9.2-complete.jar
# Lucas
# ROOT=/home/lucas/desktop/ufes/compiladores
# ANTLR_PATH=$(ROOT)/antlr-4.9.3-complete.jar

CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)

# Diretório para os arquivos .class
BIN_PATH=bin

# Comandos como descritos na página do ANTLR.
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) org.antlr.v4.gui.TestRig

# Diretório para aonde vão os arquivos gerados.
GEN_PATH=parser

# Diretório para os casos de teste
DATA=./entradas
IN1=$(DATA)/corretas
IN2=$(DATA)/incorretas

all: antlr javac
	@echo "Done."

# Opção -no-listener foi usada para que o ANTLR não gere alguns arquivos
# desnecessários para o momento. Isto será explicado melhor nos próximos labs.
antlr: C.g4
	$(ANTLR4) -no-listener -visitor -o $(GEN_PATH) C.g4

javac:
	if [ -d "$(BIN_PATH)" ]; then \
		rm -r $(BIN_PATH); \
	fi; 
	mkdir $(BIN_PATH)
	
	$(JAVAC) $(CLASS_PATH_OPTION) -d $(BIN_PATH) */*.java

run:
	$(GRUN) C compilationUnit -gui $(FILE)

clean:
	@rm -rf $(GEN_PATH) $(BIN_PATH)