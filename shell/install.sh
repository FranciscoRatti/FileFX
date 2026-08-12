DIR=$(pwd)/FileFX
EXEC="[\e[33mEXEC\e[0m]    "
INFO="[\e[34mINFO\e[0m]    "

# Logo
echo "\n"
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
echo "\n"

echo "\e[1;32m  Instalador de JavaFX\e[0m"
echo "Se copiaran todos los archivos a sus debidos lugares en el sistema."
sudo -v

# Configuracion
echo "\n$INFO ARCHIVOS DE CONFIGURACION:"

if [ ! -d ~/.config/filefx/ ]; then
  echo "$EXEC Creando directorio \e[33m~/.config/filefx/\e[0m"
  mkdir -p ~/.config/filefx
else
  echo "$INFO El directorio \e[34m~/.config/filefx/\e[0m ya existe"
fi

echo "$EXEC Copiando \e[33mconfig.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
cp --update=none $DIR/resources/config.properties ~/.config/filefx/config.properties
echo "$EXEC Copiando \e[33mkey_binding.properties0m a \e[34m~/.config/filefx/\e[0m"
cp --update=none $DIR/resources/key_binding.properties ~/.config/filefx/key_binding.properties
echo "$EXEC Copiando \e[33minit_values.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
cp --update=none $DIR/resources/init_values.properties ~/.config/filefx/init_values.properties
echo "$EXEC Copiando \e[33micons_binding.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
cp --update=none $DIR/resources/icons_binding.properties ~/.config/filefx/icons_binding.properties
echo "$EXEC Copiando \e[33mcolors_binding.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
cp --update=none $DIR/resources/colors_binding.properties ~/.config/filefx/colors_binding.properties
./FileFX/shell/copy.sh $DIR/resources/theme.css ~/.config/filefx/theme.css

# Estaticos
echo "\n$INFO ARCHIVOS ESTATICOS:"

if [ ! -d ~/.config/filefx/ ]; then
  echo "Creando directorio \e[33m/usr/share/filefx/\e[0m"
  sudo mkdir -p /usr/share/filefx/
else
  echo "$INFO El directorio \e[34m/usr/share/filefx/\e[0m ya existe"
fi

echo "$EXEC Copiando \e[33mcolors_binding.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
sudo cp $DIR/resources/*.ttf /usr/share/filefx/
echo "$EXEC Copiando \e[33mcolors_binding.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
sudo cp $DIR/resources/icon.png /usr/share/filefx/
echo "$EXEC Copiando \e[33mcolors_binding.properties\e[0m a \e[34m~/.config/filefx/\e[0m"
sudo cp $DIR/resources/notFound.png /usr/share/filefx/

# Dinamicos
echo "\n$INFO ARCHIVOS DINAMICOS:"

if [ ! -d ~/.config/filefx/ ]; then
  echo "Creando directorio \e[33m/var/lib/filefx/\e[0m"
  sudo mkdir -p /var/lib/filefx/
else
  echo "$INFO El directorio \e[34m/var/lib/filefx/\e[0m ya existe"
fi

echo "$EXEC Copiando \e[33mmetadata.properties\e[0m a \e[34m/var/lib/filefx/\e[0m"
sudo cp $DIR/resources/metadata.properties /var/lib/filefx/
sudo chmod 666 /var/lib/filefx/metadata.properties

# Binarios
echo "\n$INFO ARCHIVOS BINARIOS:"

if [ ! -d ~/.config/filefx/ ]; then
  echo "Creando directorio \e[33m/usr/lib/filefx/\e[0m"
  sudo mkdir -p /usr/lib/filefx/
else
  echo "$INFO El directorio \e[34m/usr/lib/filefx/\e[0m ya existe"
fi

echo "$EXEC Copiando \e[33mupdate.sh\e[0m a \e[34m/usr/lib/filefx/\e[0m"
sudo cp $DIR/shell/update.sh /usr/lib/filefx/
echo "$EXEC Copiando \e[33mfilefx\e[0m a \e[34m/usr/bin/\e[0m"
sudo cp $DIR/out/filefx /usr/bin/

# Entradas
echo "\n$INFO ENTRADAS DE ESCRITORIO:"

echo "$EXEC Copiando \e[33mfilefx.desktop\e[0m a \e[34m/usr/share/applications/\e[0m"
sudo cp --update=none $DIR/resources/filefx.desktop /usr/share/applications/
echo "$EXEC Copiando \e[33mopenWith_filefx.desktop\e[0m a \e[34m/usr/share/applications/\e[0m"
sudo cp --update=none $DIR/resources/openWith_filefx.desktop /usr/share/applications/
sudo update-desktop-database

echo "\n \e[32mInstalacion finalizada con exito\e[0m\nPara finalizar la instalacion ejecute \e[1;47;30mrm -rf FileFX\e[0m"
