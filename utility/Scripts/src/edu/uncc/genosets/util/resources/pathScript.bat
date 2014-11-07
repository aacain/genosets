@echo off
set file=%~1
echo ^<tests^> > %file%
call:TESTCOMMAND 0 perl
call:TESTCOMMAND 1 blastp
call:TESTCOMMAND 2 mcl
call:TESTCOMMAND 3 mysql
call:TESTCOMMAND 4 orthomclLoadBlast

echo ^</tests^> >> %file%


GOTO:eof

:TESTCOMMAND
where %~2
set currentError=%errorlevel%
echo ^<test id="%~1" command="%~2" result="%currentError%"^> >> %file%
if %currentError% == 0 (
	call:GETWHERE "%~2"
)
echo ^</test^> >> %file%
GOTO:eof

:GETWHERE
echo my command is %~1
set n=0
FOR /F "delims=" %%i IN ('where %~1') DO (
	echo ^<value^>%%i^</value^> >> %file%
)
GOTO:eof
