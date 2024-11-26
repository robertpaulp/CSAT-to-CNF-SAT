.PHONY: build run

all: build

# Rule for compilation
build:
	javac Solve.java

# Rule for execution
run:
	java Solve $(INPUT) $(OUTPUT)

# Target for cleaning generated files
clean:
	rm -f *.class

# Target for installing dependencies
install:
	sudo apt install openjdk-11-jdk-headless