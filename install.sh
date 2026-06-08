mkdir ~/.config/filefx
sudo mkdir /usr/share/filefx /usr/lib/filefx/bin /usr/lib/filefx/lib

cp share/config.properties ~/.config/filefx/
sudo cp shell/filefx.sh /usr/bin/
sudo cp -R share/filefx /usr/share/
sudo cp -R bin /usr/lib/filefx/
sudo cp -R lib /usr/lib/filefx/

sudo cp filefx.desktop /usr/share/applications/
sudo update-desktop-database
