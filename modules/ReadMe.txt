
Parallel Sets
=============

About
-----

Parallel Sets is a visualization technique for categorical data. This
implementation was written in Java, using OpenGL (via JOGL) and SQLite.

Dependencies
------------

The source code packages do not contain all the libraries anymore. To get the
complete project, check out the source using Mercurial from
http://code.google.com/p/parsets/source/checkout

This is an Eclipse project, so you should be able to import it directly
into your workspace (or even open it in place).

Below is a list of the libraries used, and where to get them.

	JOGL, https://jogl.dev.java.net/
		Java OpenGL bindings

	JDOM, http://www.jdom.org/
		Efficient and easy-to-use XML parsing library

	JNA, https://jna.dev.java.net/
		Java Native Access, useful for OS-dependent things

	JSON-simple, http://code.google.com/p/json-simple/
		Lightweight JSON implementation
	
	JUnit, http://junit.org/
		Unit testing
		
	CheckboxTree, http://zeus.pin.unifi.it/projectsSites/lablib-checkboxtree/
		Tree with checkboxes
	
	log4j, http://logging.apache.org/log4j/
		Logging
	
	MigLayout, http://www.miglayout.com/
		Sane layout manager for Swing
	
	opencsv, http://opencsv.sourceforge.net/
		CSV parsing
	
	SQLiteJDBC, http://www.zentus.com/sqlitejdbc/
		SQLite java package that uses native libraries when possible

Files
-----

	Authors.txt
		List of authors who contributed to the program

	License.txt
		The license this program has been released under, the New BSD License.

	docs/
		Some documentation of the program code

	edu/
		Source code. All the source is in the package edu.uncc.parsets

	*.dll
		JOGL libraries for Windows

	*.jnilib
		JOGL libraries for Mac OS X

	*.so
		JOGL libraries for Linux

	libs/
		Libraries used, including JOGL java code, SQLite, and a few others

	local.db
		The local db that contains all the data the program works with

	ReadMe.txt
		This file ;)

	resources/
		Additional files, like the logo and icon that need to be packaged
		with the program

	support/
		Other files that are not packaged with the program, like the Windows
		installer setup files, more logos, etc.
