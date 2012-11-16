# CS 3240-A
# Project 1 - Phase 1
#
# Compiles the java files included in this project
#
# Build procedure:
# 1. On Linux, open a terminal and navigate to the directory where this file is
# located.
# 2. Type make
# 3. Press Enter
#

JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Token.java \
	GetChar.java \
	NFA.java \
	DFA.java \
	SpecParser.java \
	TableWalker.java \
	Driver.java

build: classes

classes: $(CLASSES:.java=.class)

#
# Removes all .class files
#
# To execute this command only, follow steps 1-3 above, substituting the words
# "make clean" for "make" in step 2
#

clean:
	$(RM) *.class
