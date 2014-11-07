#!/bin/bash  
file=$1


testCommand()
{
	which $2
	errLev=$?
	echo "<"test id=\"$1\" command=\"$2\" result=\"$errLev\"">" >> $file
		if [ $errLev = 0 ]; then
			getWhich $2
            	fi
	echo "</test>" >> $file
}

getWhich(){
	result=$(which $1)
	echo "<value>"$result"</value>" >> $file
}


echo "<tests>" > $file
testCommand 0 perl
testCommand 1 blastp
testCommand 2 mcl
testCommand 3 mysql
testCommand 4 orthomclLoadBlast
echo "</tests>" >> $file
