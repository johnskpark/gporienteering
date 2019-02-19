
#!/bin/bash

RULE_DIR=$1

for file in `find ./$RULE_DIR -name "*.txt" | egrep -v "mo[^\]+.txt" | sort`
do 
    newfile=`echo $file | sed -r 's/([^/]+.txt)/mo\1/g'`
    python moconverter.py $file $newfile
done
