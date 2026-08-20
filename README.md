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

Debian = `sudo apt install librsvg2-bin` <br>
Fedora = `sudo dnf install librsvg2-tools` <br>
Arch = `sudo pacman -Syu librsvg` <br>
openSUSE = `sudo zypper install librsvg-2-2` <br>
Alpine = `sudo apk add librsvgr`

Para verificar ejecuta `rsvg-convert --version`

### XCLIP

##### Para leer el portapapeles del sistema.

Debian = `sudo apt install xclip` <br>
Fedora = `sudo dnf install xclip` <br>
Arch = `sudo pacman -S xclip` <br>
openSUSE = `sudo zypper install xclip` <br>
Alpine = `sudo apk add xclip`

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

Para ejecutar podés usar el menu de aplicaciones que es lo mismo que ejecutar
el comando:

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
archivos comparten la sintaxis de "**nombre**=**valor**", a continuacion se enumeran los tipos de
datos que pueden ir en _valor_ seguido de los archivos y sus configuraciones:

|    Tipo     |                                                                                        Valor                                                                                        |
|:-----------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|   boolean   |                                                                                    true o false                                                                                     |
|   double    |                                                                                   Numero con coma                                                                                   |
|   String    |                                                                                     Texto plano                                                                                     |
|  String[]   |                                                                Lista de texto, su<br/>sintaxis es [valor,valor,...]                                                                 |
| String[][]  |                                               Lista de listas de texto, su<br/>sintaxis es [{valor;valor;...},{valor;valor;...},...]                                                |
|    ORDER    |                                                                               NAME, DATE, SIZE o MIME                                                                               |
|   COLUMNS   |                                                              PERMISSIONS, OWNER, GROUP, SIZE, MODIFIED, CREATED, TYPE                                                               |
| TOP_BUTTONS |                                                                  BACKWARD, FORWARD, PARENT, SEARCH, CLEAN, RELOAD                                                                   |
|    ITEMS    | BACKWARD, FORWARD, OPEN, OPEN_WITH, CREATE_FILE, CREATE_DIR, CREATE_LINK, RENAME, PERMISSIONS, COPY, CUT, PASTE, RESTORE, TRASH, REMOVE, EXTRACT, COMPRESS, SHELL, ADMIN, SEPARATOR |

- **_config.properties_**: Configuraciones principales.
  - **General :**
    - `terminal = String` : Comando a ejecutar al abrir una terminal.
    - `save_bounds = boolean` : Si es true se guarda el tamaño de la ventana al cerrarse.
    - `save_path = boolean` : Si es true guarda la ultima ubicación.
    - `save_selection = boolean` : Si es true guarda el ultimo item seleccionado.
  - **Top Pane :**
    - `top_buttons = String[][]` : Define los botones que aparecerán en el TopPane. La sintaxis es
      _[{boton;icono},{boton;icono},...]_, el primer valor es TOP_BUTTON y el segundo String.
  - **Right Pane :**
    - `save_right_width = true` : Si es true se guarda el tamaño del panel derecho.
    - `show_right_pane = boolean` : Define si se muestra el RightPane al iniciar.
    - `show_miniatura = boolean` : Dentro del RightPane hay una miniatura, si es true en caso de
      seleccionar una imagen esta se mostrará, si es false se muestra siempre el icono.
    - `fill_miniatura_like_icon = boolean` : Si es true, pinta las miniaturas con el mismo color que
      el icono.
    - `show_inside_directories` : Si es true se muestran los archivos dentro de directorios en las
      miniaturas.
    - `show_inside_files` : Si es true se muestran las lineas de texto de archivos lejibles en las
      miniaturas.
  - **Bottom Pane :**
    - `bottom_buttons = String[]` : Define los botones que aparecen en el BottomPane. Los posibles
      valores son _order_, _filter_.
    - `order_icons = String[]` : Define los iconos de los botones para cambiar el orden. El orden es
      [NAME,DATE,SIZE,MIME].
  - **Left Pane :**
    - `save_left_width = true` : Si es true se guarda el tamaño del panel izquierda.
    - `show_places = boolean` : Define si se muestran las ubicaciones en el LeftPane.
    - `places = String[][]` : Define ubicaciones personalizadas que aparecerán en Lugares en el
      LeftPane. Su sintaxis es _[{nombre;icono;direccion},{nombre;icono;direccion},...]_.
    - `show_devices = boolean` : Si es true apareceran los discos y particiones en el LeftPane.
    - `partition_labels = String[][]` : Define la etiqueta de particiones especificas, el resto
      tendrá el nombre predeterminado. La sintaxis es _[{punto de montaje;nombre},...]_.
    - `show_unmounted = boolean` : Si es true se muestran las particiones que no estan montadas en
      el LeftPane.
    - `unmount_icon = String` : Define el icono del boton de desmontar.
  - **Center Pane :**
    - `is_directory_first = boolean` : Si es true se muestran los directorios primero.
    - `show_hidden = boolean` : Si es true se muestran los archivos y directorios que empiezan por
      "."
    - `show_this = boolean` : Si es true aparecerá un directorio llamado "." que hace referencia a
      la ubicacion actual.
    - `show_parent = boolean` : Si es true aparece un directorio llamado ".." que hace referencia al
      directorio padre.
    - `fill_text_file_like_icon = boolean` : Si es true los nombres de los archivos tendrán el mismo
      color que sus iconos, si es false el color será el definido por la propiedad "**unknow**" en
      _colors_binding.properties_.
    - `fill_text_dir_like_icon = boolean` : Lo mismo que el anterior pero con los directorios.
    - `default_order = ORDER` : Define el orden predeterminado de los archivos y directorios.
    - `custom_order = String[][]` : Define el orden para directorios especificos. Su sintaxis es
      _[{path;orden},{path;orden},...]_, el primer valor es String y el segundo es ORDER.
    - `columns = COLUMNS[]` : Define las columnas del CenterPane.
  - **Context Menu :**
    - `context_menu_items = ITEMS[]` : Define los items del menu contextual y su orden
    - `context_menu_icons = String[]` : Define los iconos del menu contextual.
      El orden es el mismo de _context_menu_items_.
    - `check_clipboard_paste = boolean` : Si es true revisará el portapapeles del sistema antes de
      abrir el menu contextual, si no lo hará cuando se presione el item "pegar".

- **_init_values.properties_**: Valores iniciales.
  - `height = double` : Alto inicial.
  - `width = double` : Ancho inicial.
  - `init_path = String` : Ubicación inicial.
  - `init_selection = String` : Selección inicial, puede estar vacío.
  - `right_width = double` : Ancho inicial del panel derecho.
  - `left_width = double` : Ancho inicial del panel izquierdo.

- **_key_binding.properties_**: Atajos de teclado. No distingue mayúsculas ni minúsculas y se pueden
  definir varias separadas por coma. Los nombres de cada tecla son los mismo que aparecen en la
  [API de JavaFX](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/input/KeyCode.html).
  - `cut` : Cortar.
  - `copy` : Copiar.
  - `paste` : Pegar.
  - `remove` : Eliminar permanentemente.
  - `trash` : Mandar a la papelera.
  - `rename` : Renombrar.
  - `up` : Arriba.
  - `open` : Abrir o entrar
  - `down` : Abajo.
  - `parent` : Atrás.
  - `up_step` : Arriba 3 posiciones.
  - `down_step` : Abajo 3 posiciones.
  - `first` : Primer elemento.
  - `last` : Ultimo elemento.
  - `select_up` : Seleccionar arriba.
  - `select_down` : Seleccionar abajo.
  - `select_up_step` : Seleccionar arriba 3 posiciones.
  - `select_down_step` : Seleccionar abajo 3 posiciones.
  - `select_first` : Seleccionar hasta el primero.
  - `select_last` : Seleccionar hasta el ultimo.
  - `back` : Deshacer.
  - `forward` : Rehacer.
  - `open_shell` : Abrir una terminal aquí.
  - `show_menu` : Mostrar menu contextual, equivalente a hacer click derecho.
  - `show_menu_create` : Crear menu o directorio.
  - `focus_path` : Pasarle el foco a la barra de busqueda.
  - `focus_filter` : Pasarle el foco a la barra de filtro.
  - `focus_inside` : Si _show_inside_files_ es true, le pasa el foco al interior del archivo
    seleccionado.
  - `save_inside` : Si _show_inside_files_ es true, guarda los cambios del interior del archivo
    seleccionado.
  - `deselect_all` : Deseleccionar todo.
  - `update_all` : Actualizar todo.
  - `change_show_right_pane` : Mostrar o esconder RightPane.
  - `change_show_hidden` : Cambiar mostrar archivos ocultos.

- **_colors_binding.properties_**, **_icons_binding.properties_**: Define los iconos y los colores
  que aparecerán al lado de cada archivo o directorio. Para definir que icono y color asignarle a
  cada archivo primero se fija en la extension, sino la encuentra definida en los archivos busca por
  tipo mime, sino le asigna el icono y el color de la propiedad llamada "**unknow**". <br/>
  Los colores pueden estar en hexadecimal o pueden ser los nombres de las constantes que aparecen en
  la [Api de JavaFX](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/paint/Color.html). Existen algunas propiedades especiales que siempre deben estar presentes,
  estas son:
  - `focus` : Usado cuando un archivo o directorio esta seleccionado (Solo color).
  - `unknow` : Es el valor que se utilizara en ultima instancia.
  - `lock` : Utilizado para directorios bloqueados o archivos sin permisos de lectura.
  - `this` : Utilizado para el directorio "." si _show_this_ es true.
  - `parent` : Se usa para el directorio ".." si _show_parent_ es true.
  - `disc` : Utilizado para los discos en el LeftPane si _show_devices_ es true.
  - `partition` : Igual que _disc_ pero para las particiones.

## Tema

Dentro del directorio de configuración **~/.config/filefx/** se encuentra un archivo llamado
**_theme.css_**, aquí se especifica el estilo de los componentes en formato css. Las posibles
propiedades están definidas en la [Guía de referencias CSS](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/doc-files/cssref.html) y los colores en la [Api de JavaFX](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/paint/Color.html)
son soportados. <br/>
Si no sabes css o no querés revisar la guía, la inteligencia artificial es muy util. A continuación
se puede ver la etiqueta de cada componente y de que clase es:

![componentes.png](images/componentes.png)

