# File FX

Explorador de archivo desarrollado en Java 21 utilizando el framework **JavaFX** y compilado con a
imagen nativa con **Liberica NIK** (Basado en GraalVM), disponible solo en Linux.

![vista-previa.png](images/vista-previa.png)

### Indices

- [Caracteristicas](#caracteristicas)
- [Depedencias](#dependencias)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Tema](#tema)

## Caracteristicas

Para los iconos utiliza una **Nerd fonts**, estos se pueden encontrar en la [cheat sheet](https://www.nerdfonts.com/cheat-sheet), esta
pensado para utilizarse con **atajos de teclado**, ser muy configurable y modificar contenido de
archivos rapidamente, todo esto inspirado en Yazi, tiene **previsualization de imágenes**, soporta png,
jpg, jpeg, bmp, gif y svg, haciendo click derecho en las particiones o discos (Panel izquierdo) se
pueden ver sus caracteristicas. <br>
El estilo de la aplicacion esta definido en **un archivo css**, pensado para que la comunidad haga
sus temas y pueda compartilos eficazmente. <br>
Se **actualiza automaticamente** cada tres dias o mas directamente desde github.

## Dependencias

### RSVG

##### Para pasar imagenes de .svg a .png.

Debian=`sudo apt install librsvg2-bin` <br>
Fedora=`sudo dnf install librsvg2-tools` <br>
Arch=`sudo pacman -Syu librsvg` <br>
openSUSE=`sudo zypper install librsvg-2-2` <br>
Alpine=`sudo apk add librsvgr`

Para verificar ejecuta `rsvg-convert --version`

### XCLIP

##### Para leer el portapapeles del sistema.

Debian=`sudo apt install xclip` <br>
Fedora=`sudo dnf install xclip` <br>
Arch=`sudo pacman -S xclip` <br>
openSUSE=`sudo zypper install xclip` <br>
Alpine=`sudo apk add xclip`

Para verificar instalacion ejecuta `xclip --version`

## Instalación

Para obtener los archivos podés descargar el archivo llamado **[FileFX.zip](https://raw.githubusercontent.com/FranciscoRatti/FileFX/main/FileFX.zip)**, que contiene
solo los archivos necesarios, desde _github_ o usando _curl_ o _wget_.

```
curl -L -O https://github.com/FranciscoRatti/FileFX/releases/download/latest/FileFX.zip
```

Luego hay que descomprimir el archivo ejecutando:

```
unzip FileFX.zip -d FileFX && rm FileFX.zip
```

Dentro de los archivos se encuentra un **install.sh** que copia los archivos binarios, archivos
estáticos y archivos de configuración a sus ubicaciones correctas.

```
./FileFX/shell/install.sh
```

Por último podes borrar los archivos de instalacion utilizando:

```
rm -rf FileFX
```

Para ejecutar podés usar el menu de aplicaciones o podes ver los logs ejecutando:

```
filefx
```

Si se usa el parametro `--disable-auto-update` se ejecutara la aplicacion sin actualizar automaticamente

Para **DESINSTALAR** la aplicacion se deben borrar los directorios creados con _install.sh_, los
archivos _.desktop_ y el binario.

```
sudo rm -R /usr/share/filefx /usr/lib/filefx /var/lib/filefx /usr/share/applications/openWith_filefx.desktop /usr/share/applications/filefx.desktop /usr/bin/filefx
```

Si queres que quede todo limpio tambien podes borrar la configuracion ejecutando `rm -R ~/.config/filefx`

## Configuración

Todo se configura a traves de cinco archivos de configuracion en **~/.config/filefx/**. Todos los
archivos comparten la sintaxis de "**nombre**=**valor**", a continuacion se muestran las posibles
propiedades seguidas del valor predeterminado.

- **_config.properties_**: Configuraciones principales.
  - **General :**
    - `theme=theme_default` : Tema, debe coincidir con el nombre del archivo .css en
      _~/.config/filefx/_ 
    - `terminal=xterm` : Comando a ejecutar al abrir una terminal.
    - `templates_dir=~/Templates` : Ubicación donde se guardan las plantillas de archivos que se
      usan al crear un nuevo archivo.
    - `save_bounds=true` : Si es true se guarda el tamaño de la ventana al cerrarse.
    - `save_path=true` : Si es true guarda la ultima ubicación.
    - `save_selection=true` : Si es true guarda el ultimo item seleccionado.
    - `goto_places=[{~;h},{~/Documents;d},{trash;t},{/;r}]` : Define los lugares y las combinaciones
      de teclado que aparecen en la ventana goto, su sintaxis es
      [{lugar;combinacion},{lugar;combinacion},...], puede estar vacio. Al igual que en el archivo
      "key_binding.properties" las combinaciones  no distingue mayusculas ni minusculas. Los nombre
      de cada tecla son los mismos que aparecen en la [API de JavaFX](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/input/KeyCode.html).
  - **Top Pane :**
    - `top_buttons=[{BACKWARD;},{FORWARD;},{PARENT;󰙅},{SEARCH},{CLEAN;󰃢},{RELOAD;}]` : Define
      los botones que aparecerán en el TopPane. La sintaxis es [{boton;icono},{boton;icono},...].
      Los posibles valores son BACKWARD, FORWARD, PARENT, SEARCH, CLEAN, RELOAD.
  - **Right Pane :**
    - `save_right_width=false` : Si es true se guarda el tamaño del panel derecho en
      _init_values.properties_.
    - `show_right_pane=true` : Define si se muestra el RightPane al iniciar.
    - `show_miniatura=true` : Dentro del RightPane hay una miniatura, si es true en caso de
      seleccionar una imagen esta se mostrará, si es false se muestra siempre el icono.
    - `fill_miniatura_like_icon=true` : Si es true, pinta las miniaturas con el mismo color que
      el icono.
    - `show_inside_directories=false` : Si es true se muestran los archivos dentro de directorios
      en las miniaturas. Puede influir en el rendimiento.
    - `show_inside_files=true` : Si es true se muestran las líneas de texto de archivos lejibles
      en las miniaturas.
  - **Bottom Pane :**
    - `bottom_buttons=[ORDER,FILTER]` : Define los botones que aparecen en el BottomPane. Los posibles
      valores son _ORDER_, _FILTER_.
    - `order_icons=[,,󰲎,]` : Define los iconos de los botones para cambiar el orden. El orden es
      [NAME,DATE,SIZE,MIME].
  - **Left Pane :**
    - `save_left_width=false` : Si es true se guarda el tamaño del panel izquierdo.
    - `show_places=true` : Define si se muestran los _Lugares_ en el LeftPane.
    - `places=[{Home;;~/},{Descargas;;~/Downloads/},{Documentos;󱔗;~/Documents/},{Imagenes;;~/Images/},{Papelera;;trash/},{Config;;~/.config/filefx/}]` :
      Define ubicaciones personalizadas que aparecerán en Lugares en el LeftPane. Su sintaxis es
      _[{nombre;icono;dirección},{nombre;icono;dirección},...]_.
    - `show_devices=true` : Si es true aparecerán los discos y particiones en el LeftPane.
    - `partition_labels=[{/;Raiz},{/boot/efi;Boot}]` : Define la etiqueta de particiones específicas,
      el resto tendrá el nombre predeterminado. La sintaxis es _[{punto de montaje;nombre},...]_.
    - `show_unmounted=false` : Si es true se muestran las particiones que no están montadas en
      el LeftPane.
    - `unmount_icon=󰚦` : Define el icono del botón de desmontar.
  - **Center Pane :**
    - `is_directory_first=true` : Si es true se muestran los directorios primero.
    - `show_hidden=true` : Si es true se muestran los archivos y directorios que empiezan por
      "."
    - `show_this=true` : Si es true aparecerá un directorio llamado "." que hace referencia a
      la ubicacion actual.
    - `show_parent=true` : Si es true aparece un directorio llamado ".." que hace referencia al
      directorio padre.
    - `fill_text_file_like_icon=false` : Si es true los nombres de los archivos tendrán el mismo
      color que sus iconos, si es false el color será el definido por la propiedad "**unknow**" en
      _colors_binding.properties_.
    - `fill_text_dir_like_icon=true` : Lo mismo que el anterior pero con los directorios.
    - `default_order=NAME` : Define el orden predeterminado de los archivos y directorios. Los
      posibles valores son NAME, DATE, SIZE o MIME.
    - `custom_order=[{~/Downloads/;DATE},{~/Images/;DATE},{~/Videos/;DATE},{trash/;DATE}]` :
      Define el orden para directorios especificos. Su sintaxis es _[{path;orden},{path;orden},...]_,
      el segundo valor puede ser NAME, DATE, SIZE o MIME.
    - `columns=[SIZE]` : Define las columnas del CenterPane. Los posibles valores son PERMISSIONS,
      OWNER, GROUP, SIZE, MODIFIED, CREATED y TYPE.
  - **Context Menu :**
    - `context_menu_items=[BACKWARD,FORWARD,SEPARATOR,OPEN,OPEN_WITH,CREATE_FILE,CREATE_DIR,CREATE_LINK,SEPARATOR,RENAME,PERMISSIONS,SEPARATOR,COPY,CUT,PASTE,SEPARATOR,RESTORE,TRASH,REMOVE,EXTRACT,COMPRESS,SHELL,ADMIN]` :
      Define los items del menu contextual y su orden. Los posibles valores son BACKWARD, FORWARD,
      OPEN, OPEN_WITH, CREATE_FILE, CREATE_DIR, CREATE_LINK, RENAME, PERMISSIONS, COPY, CUT, PASTE,
      RESTORE, TRASH, REMOVE, EXTRACT, COMPRESS, SHELL, ADMIN, SEPARATOR
    - `context_menu_icons=[,, ,󰷏,󰷏,,,, ,󰘎,, ,,󰆐,󰆒, ,,,,󰏖,󰏗,,]` : Define los iconos del
      menu contextual. El orden es el mismo de _context_menu_items_.
    - `check_clipboard_paste=true` : Si es true revisará el portapapeles del sistema antes de
      abrir el menu contextual, si no lo hará cuando se presione el item "pegar".

- **_init_values.properties_**: Valores iniciales.
  - `width=950` : Ancho inicial.
  - `height=525` : Alto inicial.
  - `init_path=~/` : Ubicación inicial.
  - `init_selection=` : Selección inicial, puede estar vacío.
  - `right_width=200.0` : Ancho inicial del panel derecho.
  - `left_width=130.0` : Ancho inicial del panel izquierdo.

- **_key_binding.properties_**: Atajos de teclado. No distingue mayúsculas ni minúsculas y se pueden
  definir varias separadas por coma. Los nombres de cada tecla son los mismo que aparecen en la
  [API de JavaFX](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/input/KeyCode.html).
  - `cut=ctrl+x` : Cortar.
  - `copy=ctrl+c` : Copiar.
  - `paste=ctrl+v` : Pegar.
  - `remove=ctrl+delete` : Eliminar permanentemente.
  - `trash=delete` : Mandar a la papelera.
  - `rename=f4` : Renombrar.
  - `up=up` : Arriba.
  - `open=enter,right` : Abrir o entrar
  - `down=down` : Abajo.
  - `parent=backspace,left` : Atrás.
  - `up_step=page up` : Arriba 3 posiciones.
  - `down_step=page down` : Abajo 3 posiciones.
  - `first=home` : Primer elemento.
  - `last=end` : Ultimo elemento.
  - `select_up=shift+up` : Seleccionar arriba.
  - `select_down=shift+down` : Seleccionar abajo.
  - `select_up_step=shift+page up` : Seleccionar arriba 3 posiciones.
  - `select_down_step=shift+page down` : Seleccionar abajo 3 posiciones.
  - `select_first=shift+home` : Seleccionar hasta el primero.
  - `select_last=shift+end` : Seleccionar hasta el ultimo.
  - `deselect_all=esc` : Deseleccionar todo.
  - `close=esc,q` : Cerrar ventana.
  - `goto=g` : Muestra la ventana goto
  - `backward=ctrl+z` : Deshacer.
  - `forward=ctrl+y` : Rehacer.
  - `open_shell=ctrl+t` : Abrir una terminal aquí.
  - `show_menu=context menu,ctrl+space` : Mostrar menu contextual, equivalente a hacer click derecho.
  - `show_menu_create=n` : Crear archivo o directorio.
  - `focus_path=s` : Pasarle el foco a la barra de busqueda.
  - `focus_filter=f` : Pasarle el foco a la barra de filtro.
  - `focus_inside=i` : Si _show_inside_files_ es true, le pasa el foco al interior del archivo
    seleccionado.
  - `save_inside=ctrl+s` : Si _show_inside_files_ es true, guarda los cambios del interior del archivo
      seleccionado.
  - `update_all=f5` : Actualizar todo.
  - `change_show_right_pane=space` : Mostrar o esconder RightPane.
  - `change_show_hidden=h` : Cambiar mostrar archivos ocultos.
  - `change_permissions=p` : Cambiar permisos.

- **_colors_binding.properties_**, **_icons_binding.properties_**: Define los iconos y los colores
  que aparecerán al lado de cada archivo o directorio. Para definir que icono y color asignarle a
  cada archivo primero se fija en la extension, sino la encuentra definida en los archivos busca por
  tipo mime, sino le asigna el icono y el color de la propiedad llamada "**unknow**". <br/>
  Los colores pueden estar en hexadecimal o pueden ser los nombres de las constantes que aparecen en
  la [Api de JavaFX](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/paint/Color.html). Existen algunas propiedades especiales estas son:
  - `focus=white` : Usado cuando un archivo o directorio esta seleccionado (Solo color).
  - `unknow=white,?,` : Es el valor que se utilizara en ultima instancia.
  - `lock=#FF0000,` : Utilizado para directorios bloqueados o archivos sin permisos de lectura.
  - `this=#ffe066,` : Utilizado para el directorio "." si _show_this_ es true.
  - `parent=#ffe066,` : Se usa para el directorio ".." si _show_parent_ es true.
  - `disc=white,󰋊` : Utilizado para los discos en el LeftPane si _show_devices_ es true.
  - `partition=white,` : Igual que _disc_ pero para las particiones.

## Tema

Dentro del directorio de configuración **~/.config/filefx/** se encuentra un archivo llamado
**_theme.css_**, aquí se especifica el estilo de los componentes en formato css. Las posibles
propiedades están definidas en la [Guía de referencias CSS](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/doc-files/cssref.html) y los colores en la [Api de JavaFX](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/paint/Color.html)
son soportados. <br/>
Si no sabes css o no querés revisar la guía, la inteligencia artificial es muy util. A continuación
se puede ver la etiqueta de cada componente y de que clase es:

![componentes.png](images/componentes.png)

