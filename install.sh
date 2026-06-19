mkdir ~/.config/filefx
sudo mkdir /usr/share/filefx /usr/lib/filefx/bin /usr/lib/filefx/lib

cp share/filefx/config.properties ~/.config/filefx/
cp share/filefx/key_binding.properties ~/.config/filefx/
cp share/filefx/dynamic_values.properties ~/.config/filefx/
cp share/filefx/icons_binding.properties ~/.config/filefx/
cp share/filefx/theme.css ~/.config/filefx/

sudo cp share/filefx/*.ttf /usr/share/filefx/
sudo cp share/filefx/icon.png /usr/share/filefx/
sudo cp -R share/filefx/icons /usr/share/filefx/
sudo cp -R bin /usr/lib/filefx/
sudo cp -R lib /usr/lib/filefx/

sudo cp filefx.desktop /usr/share/applications/
sudo update-desktop-database