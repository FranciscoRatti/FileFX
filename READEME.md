# File FX

Explorador de archivo desarrollado en Java 21 utilizando el framework **JavaFX** disponible solo en linux. Pensado para utilizarse con atajos de teclado y ser altamente configurable. Para los iconos utiliza las **Nerd fonts**.

### Indices

## Prerequisitos

- Java JDK 21 o superior.
- Linux.

## Instalacion

Para obtener los archivos se puede clonar el repositorio utilizando:

```
git clone https://github.com/FranciscoRatti/FileFX.git
```

Dentro de los archivos se encuentra un **install.sh** que copia los archivos binarios y estaticos a sus ubicaciones correctas, te pedira la contraseña porque debe copiar archivos a /usr/share/.

```
./FileFX/install.sh
```

Por ultimo puedes borrar los archivos utilizando:

```
rm -rf FileFX
```

Para ejecutar podes usar el menu de aplicaciones o ejecutar el comando:

```
java --module-path /usr/lib/filefx/lib --add-modules javafx.controls,javafx.graphics --enable-native-access=javafx.controls,javafx.graphics -cp /usr/lib/filefx/bin main.FileFX
```

## Configuracion

Todo se configura a traves de cinco archivos de configuracion y uno sobre el tema, estos se encuentran en **~/.config/filefx/**. La sintaxis es _calve=valor_, los arrays de una dimension ([]) deben estar entre _[]_ y cada item separado por _,_. A continuacion se enumeran los archivos y sus posibles configuraciones.

- **config.properties** : Configuraciones generales.
  - `terminal = String` : Comando a ejecutar al abrir una terminal.
  - `save_bounds = boolean` : Si es true se guarda el tamaño de la ventana al cerrarse.
  - `save_path = boolean` : Si es true guarda la ultima ubicacion.
  - `save_selection = boolean` : Si es true guarda el ultimo item seleccionado
  - `top_buttons = String[][]` : Define los botones que apareceran en el TopPane. Los posibles valores son _back_, _forward_, _parent_, _search_ (sin icono), _clean_, _reload_. La sintaxis es _[{nombre;icono},{nombre;icono},{...]_
  - `right_width = double` : Ancho fijo del RightPane
  - `show_right_pane = boolean` : Define si se muestra el RightPane al iniciar
  - `show_miniatura = boolena` : Dentro del RightPane hay una miniatura, si es true en caso de seleccionar una imagen esta se mostrara, si es false se muestra siempre el icono
  - `fill_miniatura_like_icon = boolean` : Si es true, pinta las miniaturas con el mismo color que el icono.
  - `show_places = boolean` : Define si se muestran las ubicaciones en el LeftPane
  - `places = String[][]` : Define los lugares que apareceran en places. Su sintaxis es _[{nombre;icono;path},{nombre;icono;path},{...]_
  - `is_directory_first = boolean` : Si es true se muestran los directorios primero.
  - `show_hidden = boolean` : Si es true se muestran los archivos y directorios que empiezan por "."
  - `show_this = boolean` : Si es true aparecera una carpeta llamada "." que hace referencia a la ubicacion actual.
  - `show_parent = boolean` : Si es true aparece una carpeta llamada ".." que hace referencia al directorio padre.
  - `fill_text_file_like_icon = boolean` : Si es true los nombres de los archivos tendran el mismo color que sus iconos, si es false el color sera el definido como "unknow".
  - `fill_text_dir_like_icon = boolean` : Lo mismo que el anterior pero con los directorios.
  - `context_menu_icons = String[]` : Define los iconos del menu contextual que aparece al hacer click derecho sobre el CenterPane. El orden es el mismo que aparece al abrir el menu.
  - `check_clipboard_paste = boolean` : Si es true revisara el portapapeles del sistema antes de abrir el menu contextual, si no lo hara cuando se presione el item "pegar".

- **dynamica_values.properties** : Valores iniciales.
  - `height = double` : Alto inicial.
  - `width = double` : Ancho inicial.
  - `init_path = String` : Ubicacion.
  - `init_selection = String` : Seleccion.

- **key_binding.properties** : Atajos de teclado. No distingue mayusculas ni minusculas y se pueden definir varias separadas por coma. Las posibles teclas estan en la [API de JavaFX](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/input/KeyCode.html)
  - `cut` : Cortar.
  - `copy` : Copiar.
  - `paste` : Pegar.
  - `remove` : Eliminar permanentemente.
  - `trash` : Mandar a la papelera.
  - `rename` : Renombrar.
  - `up` : Arriba.
  - `open` : Abrir o entrar
  - `down` : Abajo.
  - `parent` : Atras.
  - `up_step` : Arriba 3 posiciones.
  - `down_step` : Abajo 3 posiciones.
  - `select_up` : Seleccionar arriba.
  - `select_down` : Seleccionar abajo.
  - `select_up_step` : Seleccionar arriba 3 posiciones.
  - `select_down_step` : Seleccionar abajo 3 posiciones.
  - `back` : Deshacer.
  - `forward` : Rehacer.
  - `open_shell` : Abrir una terminal aqui.
  - `show_menu` : Mostrar menu contextual, equivalente a hacer click derecho.
  - `show_menu_create` : Crear menu o directorio.
  - `focus_path` : Pasarle el foco a la barra de busqueda.
  - `deselect_all` : Deseleccionar todo.
  - `update_all` : Actualizar todo.
  - `change_show_right_pane` : Mostrar o esconder RightPane.

- **colors_binding.properties**, **icons_binding.properties** : Define los iconos y los colores que apareceran al lado de cada archivo o directorio. Para definir el que icono asignarle a cada archivo se fija en la extension, si no encuentra una propiedad con su extension busca por tipo mime, si no le asigna el icono y el color de la propiedad "unknow".

- **theme.css** : Dentro se especifica los colores, bordes, fuentes, padding, u otras propiedades en formato css. Las posibles propiedades estan definidas en la [Guia de referencias CSS](https://docs.oracle.com/en/java/java-components/javafx/21/docs/javafx.graphics/javafx/scene/doc-files/cssref.html). Si no sabes css o no queres revisar la guia la inteligencia artifical es muy util.
  Todos los componentes estan etiquetados y muchos tienen propiedades estaticas definidas en el codigo como el tamaño de algunos componentes, a continuacion se especifica que etiqueta y que es cada componente.
