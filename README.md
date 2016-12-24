# tmpotter
TMPotter - source/translation text aligner/TMX converter for Computer Aided Translation

## Introduction

TMPotter produces TMX(Translation Memory eXchange) files for OmegaT translation process from your existent translation assets.
It accepts Bi-Text(Two text file - one is source language and another is translated), PO catalog file and TMX file.
You can edit alignment of two sentences or clauses on the TMPotter GUI pane.

## Segmentation engine

TMPotter uses OmegaT segmentation engine. It results an output TMX file is suitable for OmegaT.

## Dependency

TMPotter run on Java 8 and later. It is tested on OpenJDK 8u111 b14 on Mint Linux (x86-64).

## Project file

TMPotter saves project progress as a .tmpx file.
It also read it from open menu.

## Export

TMPotter currently export result as TMX level1 file.

## License

TMPotter is licensed under GPL3 and later.

## Copyright and acknowledgement

Copyright 2015-2016 Hiroshi Miura

Many part of TMPotter code are borrowed from OmegaT project.
Some are delived from bitext2tmx project.

Thank you for OmegaT project and bitext2tmx authors a lot.
