#!/bin/bash
DIR=$(pwd)/FileFX
EXEC="[\e[33mEXEC\e[0m]    "
INFO="[\e[34mINFO\e[0m]    "

# Logo
echo -e "\n"
echo "    okkkkkkkkkkkkkd.                    "
echo "    NM.           dNd                   "
echo "    NM              NXkkkkkkkkkkkkkd    "
echo "    NM                            MW    "
echo "    NM  xkkkkkkkkkkkkkkkkkkkkkkx  MW    "
echo "    NM.KM                      MK.MW    "
echo "    NMXMWkkkkkkkkkkkkkkkkkkkkkkWMXMW    "
echo "    NM.                           MW    "
echo "    NM      ,kkkkk.               MW    "
echo "    NM      oM;     .xl   'x:     MW    "
echo "    NM      oMKk.     XXoOM.      MW    "
echo "    NM      oM;       oMMM0       MW    "
echo "    NM      oM'     .KW   ON:     MW    "
echo "    NM                            MW    "
echo "    NMOkkkkkkkkkkkkkkkkkkkkkkkkkkOMW    "
echo -e "\n"

echo -e "\e[1;32m  Instalador de JavaFX\e[0m"
echo -e "Se copiaran todos los archivos a sus debidos lugares en el sistema."

if [ "$1" = "--use-pkexec" ]; then
  pkexec sudo bash -c "
    cp $DIR/resources/themes/default.css /usr/share/filefx/
    mkdir -p /usr/share/filefx/
    cp $DIR/resources/*.ttf /usr/share/filefx/
    cp $DIR/resources/icon.png /usr/share/filefx/
    cp $DIR/resources/notFound.png /usr/share/filefx/
    mkdir -p /var/lib/filefx/
    cp $DIR/resources/metadata.properties /var/lib/filefx/
    chmod 777 /var/lib/filefx/metadata.properties
    mkdir -p /usr/lib/filefx/
    cp $DIR/shell/update.sh /usr/lib/filefx/
    cp $DIR/out/filefx /usr/bin/
    cp --update=none $DIR/resources/filefx.desktop /usr/share/applications/
    cp --update=none $DIR/resources/openWith_filefx.desktop /usr/share/applications/
  "
  exit 0
  echo "fin"
fi

sudo -v

# Configuracion
echo -e "\n$INFO ARCHIVOS DE CONFIGURACION:"

if [ ! -d ~/.config/filefx/ ]; then
  echo -e "$EXEC Creando directorio \e[33m~/.config/filefx/\e[0m"
  mkdir -p ~/.config/filefx
else
  echo -e "$INFO El directorio \e[34m~/.config/filefx/\e[0m ya existe"
fi

echo -e "$EXEC Copiando \e[33mconfig.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
cp --update=none $DIR/resources/config.properties ~/.config/filefx/config.properties
echo -e "$EXEC Copiando \e[33mkey_binding.properties0m a \e[34m~/.config/filefx/\e[0m"
cp --update=none $DIR/resources/key_binding.properties ~/.config/filefx/key_binding.properties
echo -e "$EXEC Copiando \e[33minit_values.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
cp --update=non -ee $DIR/resources/init_values.properties ~/.config/filefx/init_values.properties
echo -e "$EXEC Copiando \e[33micons_binding.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
cp --update=none $DIR/resources/icons_binding.properties ~/.config/filefx/icons_binding.properties
echo -e "$EXEC Copiando \e[33mcolors_binding.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
cp --update=none $DIR/resources/colors_binding.properties ~/.config/filefx/colors_binding.properties
./FileFX/shell/copy.sh $DIR/resources/themes/default.css ~/.config/filefx/themes/default.css
sudo cp $DIR/resources/themes/default.css /usr/share/filefx/

# Estaticos
echo -e "\n$INFO ARCHIVOS ESTATICOS:"

if [ ! -d /usr/share/filefx/ ]; then
  echo -e "$INFO Creando directorio \e[33m/usr/share/filefx/\e[0m"
  sudo mkdir -p /usr/share/filefx/
else
  echo -e "$INFO El directorio \e[34m/usr/share/filefx/\e[0m ya existe"
fi

echo -e "$EXEC Copiando \e[33mcolors_binding.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
sudo cp $DIR/resources/*.ttf /usr/share/filefx/
echo -e "$EXEC Copiando \e[33mcolors_binding.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
sudo cp $DIR/resources/icon.png /usr/share/filefx/
echo -e "$EXEC Copiando \e[33mcolors_binding.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
sudo cp $DIR/resources/notFound.png /usr/share/filefx/

# Dinamicos
echo -e "\n$INFO ARCHIVOS DINAMICOS:"

if [ ! -d /var/lib/filefx/ ]; then
  echo -e "$INFO Creando directorio \e[33m/var/lib/filefx/\e[0m"
  sudo mkdir -p /var/lib/filefx/
else
  echo -e "$INFO El directorio \e[34m/var/lib/filefx/\e[0m ya existe"
fi

echo -e "$EXEC Copiando \e[33mmetadata.properties\e[0m a \e[34m/var/lib/filefx/\e[0m"
sudo cp $DIR/resources/metadata.properties /var/lib/filefx/
sudo chmod 777 /var/lib/filefx/metadata.properties

# Binarios
echo -e "\n$INFO ARCHIVOS BINARIOS:"

if [ ! -d /usr/lib/filefx/ ]; then
  echo -e "$INFO Creando directorio \e[33m/usr/lib/filefx/\e[0m"
  sudo mkdir -p /usr/lib/filefx/
else
  echo -e "$INFO El directorio \e[34m/usr/lib/filefx/\e[0m ya existe"
fi

echo -e "$EXEC Copiando \e[33mupdate.sh\e[0m a \e[34m/usr/lib/filefx/\e[0m"
sudo cp $DIR/shell/update.sh /usr/lib/filefx/
echo -e "$EXEC Copiando \e[33mfilefx\e[0m a \e[34m/usr/bin/\e[0m"
sudo cp $DIR/out/filefx /usr/bin/

# Entradas
echo -e "\n$INFO ENTRADAS DE ESCRITORIO:"

echo -e "$EXEC Copiando \e[33mfilefx.desktop\e[0m a \e[34m/usr/share/applications/\e[0m"
sudo cp --update=none $DIR/resources/filefx.desktop /usr/share/applications/
echo -e "$EXEC Copiando \e[33mopenWith_filefx.desktop\e[0m a \e[34m/usr/share/applications/\e[0m"
sudo cp --update=none $DIR/resources/openWith_filefx.desktop /usr/share/applications/
sudo update-desktop-database

echo -e "\n \e[32mInstalacion finalizada con exito\e[0m\nPara finalizar la instalacion ejecute \e[1;47;30mrm -rf FileFX\e[0m"