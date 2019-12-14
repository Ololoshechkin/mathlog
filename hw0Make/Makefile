.PHONY: all compile run pack clean out

MAINCLASS = hw3.MainKt
KOTLIN_PATH=src/main/kotlin/
GENERATED=antlr/generated

all: prepare-kotlin out compile

run:
	java -cp hello.jar ${MAINCLASS}
	
pack:
	zip hw1.zip -r Makefile src kotlin.mk textProofs

clean:
	rm -rf out
	rm hello.jar

compile:
	${KOTLINC} -d hello.jar -include-runtime ${KOTLIN_PATH}/hw1/*.kt ${KOTLIN_PATH}/hw2/*.kt ${KOTLIN_PATH}/hw3/*.kt ${KOTLIN_PATH}/grammar/*.kt

out:
	mkdir -p out

include kotlin.mk
