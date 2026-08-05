#!/bin/bash
set -e
rm -rf /tmp/filefx-build
mkdir -p /tmp/filefx-build
cd /tmp/filefx-build/

CURRENT_VERSION=$(grep version /var/lib/filefx/metadata.properties | cut -c 9-)

echo -e "[\e[32mUPDATE\e[0m]   Tomando version"
curl -s -o github.rest https://api.github.com/repos/FranciscoRatti/FileFX/releases/latest

LATEST_VERSION=$(grep '"tag_name"' github.rest | cut -d'"' -f4 | sed 's/v//')

if [ "$CURRENT_VERSION" != "$LATEST_VERSION" ]; then
  echo -e "[\e[32mUPDATE\e[0m]   Nueva actualizacion disponible: $LATEST_VERSION"

  curl -L -s -o FileFX.zip $(grep '"browser_download_url"' github.rest | cut -d'"' -f4)
  unzip -q FileFX.zip
  ./install.sh
  rm -rf /tmp/filefx-build

  echo -e "[ \e[32mOK\e[0m ]     \e[32mActualizando\e[0m"
  notify-send -i /usr/share/filefx/icon.png "Nueva actualizacion" "FileFX se actualizo a la version $LATEST_VERSION\nVe los cambios en github.com/FranciscoRatti/FileFX/releases/tag/v$LATEST_VERSION"
else
  echo -e "[ \e[32mOK\e[0m ]     \e[32mAplicacion en su ultima version\e[0m"
fi