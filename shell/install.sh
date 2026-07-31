mkdir ~/.config/filefx

cp resources/config.properties ~/.config/filefx/
cp resources/key_binding.properties ~/.config/filefx/
cp resources/init_values.properties ~/.config/filefx/
cp resources/icons_binding.properties ~/.config/filefx/
cp resources/colors_binding.properties ~/.config/filefx/
cp resources/theme.css ~/.config/filefx/

sudo mkdir /usr/share/filefx/

sudo cp resources/*.ttf /usr/share/filefx/
sudo cp resources/icon.png /usr/share/filefx/
sudo cp resources/notFound.png /usr/share/filefx/
sudo cp out/filefx /usr/bin/

sudo cp resources/filefx.desktop /usr/share/applications/
sudo cp resources/openWith_filefx.desktop /usr/share/applications/
sudo update-desktop-database