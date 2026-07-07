package entity;

import javafx.scene.image.Image;
import main.Lib;
import node.CenterNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static main.Lib.*;

public class DesktopApplication {
    private File desktopFile;
    private String exec;
    private boolean hasParameter = false;
    private String name;
    private String[] mimeTypes;
    private boolean isDisplay = true;
    private String iconText;
    private Image icon;

    public DesktopApplication(File desktopFile) {
        this.desktopFile=desktopFile;
        iconText = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(desktopFile))) {
            String line;

            boolean isNameFound = false;
            do {
                line = reader.readLine();

                if (line != null && !line.isEmpty()) {
                    if (exec == null && line.startsWith("Exec")) {
                        exec = line.substring(5);
                        hasParameter = exec.contains("%f") || exec.contains("%F") || exec.contains("%u") || exec.contains("%U");
                    }
                    if (!isNameFound && line.startsWith("Name")) {
                        if (line.charAt(4) == '[') {
                            if (line.startsWith("es]", 5)) {
                                name = line.substring(9);
                                isNameFound = true;
                            }
                        } else if (name == null) {
                            name = line.substring(5);
                        }
                    }
                    if (mimeTypes == null && line.startsWith("MimeType")) {
                        mimeTypes = line.substring(9).split(";");
                    }
                    if (line.startsWith("NoDisplay")) {
                        isDisplay = !Boolean.parseBoolean(line.substring(10, 14));
                    }
                    if (iconText.isEmpty() && line.startsWith("Icon")) {
                        iconText = line.substring(5);
                    }
                }
            } while(line != null);
        } catch (IOException e) {
            printError("Error al leer archivo '"+desktopFile.getAbsolutePath()+"'", e);
        }

        if (name == null) name = "";
    }

    public void openWith(CenterNode selectedItem) {
        if (hasParameter) {
            File file = selectedItem.getFile();
            String parameterPath = file.getAbsolutePath();
            String parameterUrl = "file://"+file.getAbsolutePath();

            String comand = exec;
            comand = comand
                    .replaceAll("%f", parameterPath)
                    .replaceAll("%u", parameterUrl)
                    .replaceAll("%F", parameterPath)
                    .replaceAll("%U", parameterUrl);

            try {
                printExecute("Abriendo archivo '"+YELLOW+parameterPath+RESET+"' con "+YELLOW+getName()+RESET);
                ProcessBuilder pb = new ProcessBuilder(comand.split("\\s+"));
                pb.start();
            } catch (Exception e) {
                printError("Error al abrir ejecutando '"+comand+"'", e);
            }
        } else {
            printError("El comando no recibe parametros", null);
        }
    }

    public Image getIcon() {
        if (icon == null) {
            if (new File(iconText).exists()) {
                icon = new Image("file://"+iconText);
            } else {
                try {
                    printExecute("Buscando icono de '" + YELLOW + name + RESET + "'");

                    Process process = new ProcessBuilder(
                            "python3", "-c",
                            """
                                    import gi, sys
                                    gi.require_version('Gtk', '3.0')
                                    from gi.repository import Gtk
                                    theme = Gtk.IconTheme.get_default()
                                    icon = theme.lookup_icon(sys.argv[1], int(24), 0)
                                    print(icon.get_filename() if icon else '')
                                    """,
                            iconText
                    ).start();

                    String iconPath = new String(process.getInputStream().readAllBytes()).strip();
                    if (iconPath.endsWith(".svg")) {
                        File png = new File(Lib.CONFIG_PATH + "tmp.png");
                        try {
                            Process parseSvg = new ProcessBuilder("rsvg-convert", "-w", "24", "-h", "24", "-o",
                                    png.getAbsolutePath(), iconPath).start();
                            parseSvg.waitFor();

                            icon = new Image("file://"+png.getAbsolutePath());
                        } catch (Exception e) {
                            printError("Error al parsear " + RED + iconPath + RESET + " a .svg", e);
                            icon = new Image("file://" + ABSOLUTE_PATH + "share/filefx/notFound.png");
                        } finally {
                            png.delete();
                        }
                    } else if (!iconPath.isEmpty()) icon = new Image("file://" + iconPath);
                    else icon = new Image("file://" + ABSOLUTE_PATH + "share/filefx/notFound.png");
                } catch (IOException e) {
                    printError("Error al carga icono de " + name, e);
                }
            }
        }
        return icon;
    }

    public File getDesktopFile() {return desktopFile;}
    public String getExec() {return exec;}
    public boolean hasParameter() {return hasParameter;}
    public String getName() {return name;}
    public String[] getMimeTypes() {return mimeTypes;}
    public boolean isDisplay() {return isDisplay;}
}
