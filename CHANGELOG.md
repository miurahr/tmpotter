# Change Log
All notable changes to this project will be documented in this file.

## [Unreleased]
### Add
- NetBeans project files. It uses for editing UI forms.

### Changed

### Fixed
- Fix OutOfIndex error when exporting.


## [0.8.5]
### Add
- Introduce Debian package.(#7)
- TmxReader2 test added.(but some still fails)

### Changed
- Drop JDK8_HOME property from build.gradle and simplify
- Document: use 'bitext' only for single file with source/translation texts.

### Fixed
- SLF4J multiple binding warning.(#52)
- File not found tmx11.dtd error.(#51)
- First new import cause NPE.
- Fix NPE on importing TMX 1.4 file.(#53)


## [0.8.4] - 2017-01-02
### Add
- Level2 TMX export support.
- Highlight a clicked table row.(#8)
- XLIFF v2.0 and XLIFF v1.1/v1.4 import feature

### Changed
- Undo menu/buttons enable/disable support(imcomplete).
- Open project file without language selection.


## [0.8.3] - 2016-12-29
### Add
- Wiki download and import feature.(#49)

### Fixed
- Update gradle properties template for using Java8


## [0.8.2] - 2016-12-24
### Add
- PO file import filter.
- TMX file import filter.
- TMX file export functionarity.
- Import wizard dialog for new project.
- It can save project as .tmpx file.
- Filter plugin fasicility.
  New filter should be added to manifest file.
- Add NetBeans and IntelliJ IDEA project files.

### Changed
- Implement GUI with netbeans matisse gui designer.
- Move segmentation engine into sub project.

## 0.8.0 - 2016-12-7
- Integrate bitext2tmx and OmegaT code base.

[Unreleased]: https://github.com/miurahr/tmpotter/compare/v0.8.5...HEAD
[0.8.5]: https://github.com/miurahr/tmpotter/compare/v0.8.5...v0.8.4
[0.8.4]: https://github.com/miurahr/tmpotter/compare/v0.8.3...v0.8.4
[0.8.3]: https://github.com/miurahr/tmpotter/compare/v0.8.2...v0.8.3
[0.8.2]: https://github.com/miurahr/tmpotter/compare/v0.8.0...v0.8.2
