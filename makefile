.PHONY:  compile jar dirmake clean

PROJECT    := JFXMeter
SRCDIR     := .
BINDIR     := .
LIBRARIES  := .
CLASSPATH  := .
CLASSFILES  = $(shell find $(BINDIR) -type f -regextype "posix-extended" -regex ".+\.class$$" | sed -re "s/\\$$/\\\\\$$/g")
MANIFEST   := $(SRCDIR)/META-INF/MANIFEST.MF
MAINFILE   := $(SRCDIR)/$(PROJECT).java
LOGFILE    := $(SRCDIR)/$(PROJECT).log
JARFILE    := $(BINDIR)/$(PROJECT).jar

compile: dirmake
	javac -verbose -g -cp $(CLASSPATH) -d $(BINDIR) $(MAINFILE) 2>&1 | tee $(LOGFILE)

jar: compile
	jar cvmf $(MANIFEST) $(JARFILE) -C $(BINDIR) $(PROJECT).class ${CLASSFILES} $(LIBRARIES) 2>&1 | tee -a $(LOGFILE)
	chmod u+x $(JARFILE)

dirmake:
	mkdir --parents $(SRCDIR)
	mkdir --parents $(BINDIR)

clean:
	rm -f $(JARFILE) $(LOGFILE) ${CLASSFILES}

