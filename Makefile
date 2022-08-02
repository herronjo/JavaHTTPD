JC=javac
BUILDDIR=./build/
JFLAGS=-d $(BUILDDIR)
ifeq ($(DEBUG), yes)
	JFLAGS=-g $(JFLAGS)
endif
JAR=jar
MANIFEST=manifest.txt
JVM=java
JVMFLAGS=-jar
MAIN=JavaHTTPD
MAINPATH=

default:
build:
	mkdir build
	cp $(MANIFEST) build/
	$(JC) $(JFLAGS) $(MAINPATH)$(MAIN).java
	cd $(BUILDDIR) && $(JAR) -cvfm $(MAIN).jar $(MANIFEST) * && cd ..
	cp $(BUILDDIR)$(MAIN).jar .

run:
	$(JVM) $(JVMFLAGS) $(MAIN).jar

clean:
	find . -name "*.class" -type f -exec rm {} +
	find . -name "*.jar" -type f -exec rm {} +
	rm -rf $(BUILDDIR)