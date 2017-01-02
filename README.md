# tmpotter
TMPotter - source/translation text aligner/TMX converter for Computer Aided Translation


## Introduction

TMPotter produces TMX(Translation Memory eXchange) files for OmegaT translation process from your
existent translation assets.
It accepts Bi-Text(Two text file - one is source language and another is translated),
PO catalog file and TMX file.
You can edit alignment of two sentences or clauses on the TMPotter GUI pane.


## Install

You can install a TMPotter on any Operating Systems which support Java 8 SE runtime.
TMPotter run on Java 8 and later.

### Debian/Ubuntu

TMPotter distribution includes Deb package.
you can install it onto Debian/Ubuntu and its deliverable Operating Systems.
It depends on 'openjdk-8-jre' package which is not supported natively in Ubuntu Trusty.
You should solve its dependency by your own.

### Windows

Please unzip zip distribution in any your preferable directory.
To launch TMPotter, you can hit bin/tmpotter.bat command.
Please aware that you need to set PATH to tmpotter.bat or JAVA_HOME to
proper value before starting TMPotter.


## Segmentation engine

TMPotter uses OmegaT segmentation engine for bi-text imports.
It results an output TMX file is suitable for OmegaT.


## Project file

TMPotter can save a project progress as a .tmpx file.
You can continue your alignment edit by opening project file.


## Import

TMPotter can import following file formats:

- BiText files (Two text files: one is original article, the other is translation)

- Wikimedia article downloads from source and translation URL.

- PO file.

- TMX file.

- XLIFF file.


## Export

TMPotter can export alignment result as TMX file.


## Test environment

TMPotter is tested on OpenJDK 8u111 b14 on Mint Linux (x86-64).

## License

TMPotter is licensed under GPL3 and later.


## Copyright and acknowledgement

Copyright 2015-2017 Hiroshi Miura

Many part of TMPotter code are borrowed from OmegaT project.
Some are delived from bitext2tmx project.
Thank you for OmegaT project and bitext2tmx authors.

It also uses many libraries.
XLIFF imports depends on OKAPI framework library.
