.PHONY: all compile run pack clean out

MAINCLASS = MainKt
KOTLIN_PATH=src/main/kotlin/
ANTLR_PATH=lib/antlr-4.7.1-complete.jar
ANTLR4=java -jar ${ANTLR_PATH}
GENERATED=antlr/generated

all: prepare-kotlin out compile

run:
	${KOTLIN} -cp out:${ANTLR_PATH} ${MAINCLASS}

pack:
	zip hw0.zip -r Makefile src lib axioms.txt antlr kotlin.mk

clean:
	rm -rf out

compile:
	${ANTLR4} ${GENERATED}/Expression.g4 -visitor -package antlr.generated 
	javac -d out -cp ${ANTLR_PATH} ${GENERATED}/*.java
	-${KOTLINC} -d out -cp out:${ANTLR_PATH} ${KOTLIN_PATH}/*.kt

out:
	mkdir -p out

include kotlin.mk
