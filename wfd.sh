PROFILE=$1
METHOD=$2
NORM=$3
java -cp ./dist/lib/*:./dist/*  de.joint.WordFrameDisambiguation $PROFILE $METHOD $NORM 
