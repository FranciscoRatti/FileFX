clear
rm -R ../bin

export NIK=/opt/bellsoft/liberica-vm-full-23.1.12-openjdk21/bin
export DIR=/home/fran-linux/Documents/Programacion/Proyectos/FileFX

javac --module-path $DIR/lib --add-modules javafx.controls,javafx.graphics -d $DIR/bin ../src/*/*
sudo $NIK/native-image --module-path $DIR/lib --add-modules javafx.controls,javafx.graphics --enable-native-access=javafx.controls,javafx.graphics --class-path $DIR/bin main.FileFX ../out/filefx