clear
rm -R ../bin

export LIB="/home/fran-linux/Documents/Programacion/Proyectos/FileFX/lib"
export MODULES="javafx.controls,javafx.graphics"
export MAIN_CLASS="main.FileFX"
export BIN="/home/fran-linux/Documents/Programacion/Proyectos/FileFX/bin"

javac --module-path $LIB --add-modules $MODULES -d $BIN ../src/*/*
java --module-path $LIB --add-modules $MODULES --enable-native-access=$MODULES -cp $BIN $MAIN_CLASS
