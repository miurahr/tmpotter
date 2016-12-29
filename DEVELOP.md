TMPotter Developers guide
==========================

Sending patches
----------------

Contributions are welcome. You can PR on github.

Before sending PR, please check again with
`gradle check`.


Coding style
--------------

TMPotter has a coding style rule that is modified version
of Google Java coding rule.

see.
https://google.github.io/styleguide/javaguide.html

Special rules

1. Each line should be less than 100 chars.

2. Every File should encode with ASCII-7bit or UTF-8

3. Sources and comments should be written only in ASCII characters.
   Comments should be written in English.

4. All localization part should be placed in Bundle properties file

You can check coding style with ant command

```
$ ./gradlew check
```

Internationalization(i18n) and Localization(l10n)
------------------------------------

TM Potter is i18n-ed using resource bundle.
Properties files are stored in UTF-8 encoding.

To use UTF-8 in properties file in NetBeans IDE,
it is recommended to set each properties file to open
in UTF-8 encoding by "Right-click" on properties file ->"Property"
-> check on "Open with project encoding".

Bundle key naming rule
------------------------------------

Bundle key should have 3 parts at least.

- First part indicate source in Abbrav
- Second part indicate type of message.
- Third part indicate message itself.

ex.
```
XSR.ERROR.BAD_XML_TAG
```

Import/Export Filter
------------------------------------

Import/Export Filter should be registered as service.
Your filter implement IFilter can be registered with putting class name on resource
`META-INF/services/org.tmpotter.filters.IFilter`

Import wizard
----------------------------

TMPotter provides Import Wizard.
An import filter may have a wizard and it should be registered
class which implements IImportWizardPanel interface on
`META-INF/services/org.tmpotter.ui.wizard.IImportWizardPanel`:w



Document versions
----------------------------

Last Update: 28, Dec. 2016
Initial version: Jan. 2016
