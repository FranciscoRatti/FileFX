clear
rm -R ../bin

export DIR=/home/fran-linux/Documents/Programacion/Proyectos/FileFX/

javac --module-path $DIR/lib --add-modules javafx.controls,javafx.graphics -d $DIR/bin ../src/*/*
java --module-path $DIR/lib --add-modules javafx.controls,javafx.graphics --enable-native-access=javafx.controls,javafx.graphics -cp $DIR/bin main.FileFX
