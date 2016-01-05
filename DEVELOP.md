TMPotter Developers guide
==========================





Sending patch
-------------

Contributions are welcome. You can PR on github.

Before sending PR, please check again with
ant test.



Coding style
--------------

TMPotter has a coding style rule that is modifiied version
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
$ ant checkstyle
```

Internatinalization(i18n) and Localization(l10n)
------------------------------------

TM Potter is i18n-ed using resource bundle.
Properties files are stored in UTF-8 encoding.

To use UTF-8 in properties file in Netbeans IDE,
it is recommended to set each properties file to open
in UTF-8 encoding by "Right-click" on prpoerties file ->"Property"
-> check on "Open with project encoding".

Bundle key naming rule
~~~~~~~~~~~~~

Bundle key should have 3 parts at least.

- First part indicate source in Abbrav
- Second part indicate type of message.
- Third part indicate message itself.

ex.
```
XSR.ERROR.BAD_XML_TAG
```

Document versions
----------------------------

Last Update: 5, Jan. 2016
Initial version: Jan. 2016
