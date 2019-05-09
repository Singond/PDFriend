PDFriend
========
PDFriend is a simple command-line utility for modifying PDF documents.
It was developed primarily to perform _imposition_, that is, arranging
multiple pages of an input document into a single page in the output.

Getting started
===============

Requirements
------------
PDFriend requires Java SE 1.8 or later to run.
Java is available for Windows, Mac OS X and Linux.
You can install Java by going to its
[website](https://java.com/en/download/manual.jsp)
and selecting the version appropriate for your system.

Manual installation
-------------------
Standalone executable jars are provided at the
[releases page](https://github.com/Singond/PDFriend/releases).
Download the `pdfriend-*-standalone.jar` and put it anywhere on your computer.
You can then run the application like any other executable jar:

    java -jar pdfriend-???-standalone.jar <arguments>

where `???` is the version number.

You can simplify this by creating a script called `pdfriend`,
where you call the command above and pass any arguments given
to the script into the command.
If you put the script somewhere in your `PATH` and make it executable,
you can then invoke pdfriend by simply issuing:

    pdfriend <arguments>

Using the installer
-------------------
An experimental cross-platform installer is available from
the [releases page](https://github.com/Singond/PDFriend/releases)
as the `pdfriend-*-installer.jar` file.
This installer requires Java to be installed. It will unpack PDFriend
into a directory of your choice.
On Linux, it will also install a shell script and add it to the `PATH`
for the current user, enabling you to conveniently run PDFriend from
the terminal.

Built With
===============
 - [Apache PDFBox](https://pdfbox.apache.org/)
   – PDF manipulation
 - [JCommander](http://jcommander.org/)
   – parsing command-line arguments
 - [Apache Commons](http://commons.apache.org/)
   – Java components
 - [Apache Log4J2](https://logging.apache.org/log4j/2.x/)
   – logging and output
 - [IzPack](http://izpack.org/)
   – providing cross-platform installer

Authors
=======
 - Singon (Jan Slaný) – [Singond](https://github.com/Singond)

Acknowledgments
===============
 - Special thanks goes to the developers of the
   [Apache PDFBox](https://pdfbox.apache.org/) library,
   whose work made this application possible.
 - Cédric Beust, the developer of [JCommander](http://jcommander.org/)
 - the developers of [Apache Commons](http://commons.apache.org/)
 - the developers of [Apache Log4J2](https://logging.apache.org/log4j/2.x/)
 - the developers of [IzPack](https://github.com/izpack/izpack)
 