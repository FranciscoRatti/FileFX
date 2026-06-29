# File FX

Explorador de archivo desarrollado en Java 21 utilizando el framework
**JavaFX** disponible solo en Linux. Pensado para utilizarse con atajos de
teclado, ser altamente configurable y ligero. Para los iconos utiliza una
**Nerd fonts**. Tiene revisualization de imágenes configurable.

### Indices
- [Requisitos](#requisitos)
- [Instalación](#instalacion)
- [Configuración](#configuracion)
- [Tema](#tema)

## Requisitos

- Java JDK 21 o superior.
- Linux.

## Instalación

Para obtener los archivos podés entrar al [repositorio en github](https://github.com/FranciscoRatti/FileFX)
y descargar el archivo llamado **FileFX.zip** que contiene solo los archivos
necesarios para la instalación y luego ejecutar:

```
unzip FileFX.zip -d FileFX && rm FileFX.zip
```

O también se puede clonar todo el repositorio utilizando:

```
git clone https://github.com/FranciscoRatti/FileFX.git
```

Dentro de los archivos se encuentra un **install.sh** que copia los archivos
binarios, archivos estáticos y archivos de configuración a sus ubicaciones
correctas, te pedirá la contraseña porque debe copiar archivos a
_/usr/share/filefx/_.

```
./FileFX/install.sh
```

Por último puedes borrar los archivos utilizando:

```
rm -rf FileFX
```

Para ejecutar podés usar el menu de aplicaciones que es lo mismo que ejecutar
el comando:

```
java --module-path /usr/lib/filefx/lib --add-modules javafx.controls,javafx.graphics --enable-native-access=javafx.controls,javafx.graphics -cp /usr/lib/filefx/bin main.FileFX
```

Dentro de los archivos también se puede encontrar un **package.sh**, este es
usado para generar un zip con los archivos necesarios para la installation.
Dentro del directorio _shell/_ hay un script para solo compilar llamado
**compile.sh** y otro para compilar y ejecutar llamado **compile_and_run.sh**,
estos los uso para el desarrollo y no están incluidos en.

## Configuración

Todo se configura a traves de cinco archivos de configuración, estos se
encuentran en **~/.config/filefx/**. La sintaxis es _clave=valor_, los arrays
deben estar entre "[]" y cada item separado por ",". A continuación se enumeran
los archivos y sus posibles configuraciones.

- ***config.properties***: Configuraciones generales.
  - `terminal = String` : Comando a ejecutar al abrir una terminal.
  - `save_bounds = boolean` : Si es true se guarda el tamaño de la ventana al
  cerrarse.
  - `save_path = boolean` : Si es true guarda la ultima ubicación.
  - `save_selection = boolean` : Si es true guarda el ultimo item seleccionado.
  - `top_buttons = String[][]` : Define los botones que aparecerán en el TopPane.
  Los posibles valores son _back_, _forward_, _parent_, _search_ (sin icono),
  _clean_, _reload_. La sintaxis es _[{nombre;icono},{nombre;icono},...]_
  - `right_width = double` : Ancho fijo del RightPane.
  - `show_right_pane = boolean` : Define si se muestra el RightPane al iniciar.
  - `show_miniatura = boolena` : Dentro del RightPane hay una miniatura, si es
  true en caso de seleccionar una imagen esta se mostrará, si es false se
  muestra siempre el icono.
  - `fill_miniatura_like_icon = boolean` : Si es true, pinta las miniaturas con
  el mismo color que el icono.
  - `show_places = boolean` : Define si se muestran las ubicaciones en el LeftPane.
  - `places = String[][]` : Define los lugares que aparecerán en Lugares. Su
  sintaxis es _[{nombre;icono;path},{nombre;icono;path},{...]_.
  - `is_directory_first = boolean` : Si es true se muestran los directorios
  primero.
  - `show_hidden = boolean` : Si es true se muestran los archivos y directorios
  que empiezan por "."
  - `show_this = boolean` : Si es true aparecerá una carpeta llamada "." que
  hace referencia a la ubicacion actual.
  - `show_parent = boolean` : Si es true aparece una carpeta llamada ".." que
  hace referencia al directorio padre.
  - `fill_text_file_like_icon = boolean` : Si es true los nombres de los archivos
  tendrán el mismo color que sus iconos, si es false el color será el definido
  como "unknow".
  - `fill_text_dir_like_icon = boolean` : Lo mismo que el anterior pero con los
  directorios.
  - `context_menu_icons = String[]` : Define los iconos del menu contextual que
  aparece al hacer clic derecho sobre el CenterPane. El orden es el mismo que
  aparece al abrir el menu.
  - `check_clipboard_paste = boolean` : Si es true revisará el portapapeles del
  sistema antes de abrir el menu contextual, si no lo hará cuando se presione el
  item "pegar".

- ***dynamica_values.properties***: Valores iniciales.
  - `height = double` : Alto inicial.
  - `width = double` : Ancho inicial.
  - `init_path = String` : Ubicación inicial.
  - `init_selection = String` : Selección inicial, puede estar vacío.

- ***key_binding.properties***: Atajos de teclado. No distingue mayúsculas ni 
minúsculas y se pueden definir varias separadas por coma. Los nombres de cada
tecla está especificado en la [API de JavaFX](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/input/KeyCode.html).
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
  - `select_up` : Seleccionar arriba.
  - `select_down` : Seleccionar abajo.
  - `select_up_step` : Seleccionar arriba 3 posiciones.
  - `select_down_step` : Seleccionar abajo 3 posiciones.
  - `back` : Deshacer.
  - `forward` : Rehacer.
  - `open_shell` : Abrir una terminal aquí.
  - `show_menu` : Mostrar menu contextual, equivalente a hacer click derecho.
  - `show_menu_create` : Crear menu o directorio.
  - `focus_path` : Pasarle el foco a la barra de busqueda.
  - `deselect_all` : Deseleccionar todo.
  - `update_all` : Actualizar todo.
  - `change_show_right_pane` : Mostrar o esconder RightPane.

- ***colors_binding.properties***, ***icons_binding.properties***: Define los
iconos y los colores que aparecerán al lado de cada archivo o directorio. Para
definir el que icono asignarle a cada archivo se fija en la extension, si no
encuentra una propiedad con su extension busca por tipo mime, si no le asigna
el icono y el color de la propiedad "unknow".

## Tema

Dentro del directorio de configuración **~/.config/filefx/** se encuentra un
archivo llamado ***theme.css***, aquí se especifica el estilo de los componentes
en formato css. Las posibles propiedades están definidas en la
[Guía de referencias CSS](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/doc-files/cssref.html).
Si no sabes css o no querés revisar la guía la inteligencia artificial es muy util.
A continuación se puede ver cada componente etiquetado y de que clase es:

![componentes.png](images/componentes.png)
