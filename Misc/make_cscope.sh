PROJ=$(pwd)

find $PROJ/ -name '*.c' -o -name '*.h' > cscope.files

cscope -b -q -i cscope.files
