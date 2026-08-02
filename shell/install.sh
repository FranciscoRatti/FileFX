mkdir -p ~/.config/filefx

cp -n resources/config.properties ~/.config/filefx/
cp -n resources/key_binding.properties ~/.config/filefx/
cp -n resources/init_values.properties ~/.config/filefx/
cp -n resources/icons_binding.properties ~/.config/filefx/
cp -n resources/colors_binding.properties ~/.config/filefx/
cp -n resources/theme.css ~/.config/filefx/

sudo -p mkdir /usr/share/filefx/

sudo cp resources/*.ttf /usr/share/filefx/
sudo cp resources/icon.png /usr/share/filefx/
sudo cp resources/notFound.png /usr/share/filefx/
sudo cp out/filefx /usr/bin/

sudo -n cp resources/filefx.desktop /usr/share/applications/
sudo -n cp resources/openWith_filefx.desktop /usr/share/applications/
sudo update-desktop-database