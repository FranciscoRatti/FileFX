export DIR=$(pwd)/FileFX

# Configuracion
mkdir -p ~/.config/filefx

cp -n $DIR/resources/config.properties ~/.config/filefx/
cp -n $DIR/resources/key_binding.properties ~/.config/filefx/
cp -n $DIR/resources/init_values.properties ~/.config/filefx/
cp -n $DIR/resources/icons_binding.properties ~/.config/filefx/
cp -n $DIR/resources/colors_binding.properties ~/.config/filefx/
cp -n $DIR/resources/theme.css ~/.config/filefx/

# Estaticos
sudo mkdir -p /usr/share/filefx/

sudo cp $DIR/resources/*.ttf /usr/share/filefx/
sudo cp $DIR/resources/icon.png /usr/share/filefx/
sudo cp $DIR/resources/notFound.png /usr/share/filefx/

# Dinamicos
sudo mkdir -p /var/lib/filefx/
sudo cp $DIR/resources/metadata.properties /var/lib/filefx/
sudo chmod 777 /var/lib/filefx/metadata.properties

# Binarios
sudo mkdir -p /usr/lib/filefx/
sudo cp $DIR/shell/update.sh /usr/lib/filefx/
sudo cp $DIR/out/filefx /usr/bin/

# Entradas
sudo -n cp $DIR/resources/filefx.desktop /usr/share/applications/
sudo -n cp $DIR/resources/openWith_filefx.desktop /usr/share/applications/
sudo update-desktop-database