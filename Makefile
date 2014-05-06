JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
Demo.java \
Demo1.java \
ClientThread.java \
Client.java \
Server.java 


default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class