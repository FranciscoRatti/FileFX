package entity;

import javafx.scene.image.Image;
import node.FileLabel;
import java.io.*;

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

            do {
                line = reader.readLine();

                if (line != null && !line.equals("")) {
                    if (exec == null && line.startsWith("Exec")) {
                        exec = line.substring(5);
                        hasParameter = exec.contains("%f") || exec.contains("%F") || exec.contains("%u") || exec.contains("%U");
                    }
                    if (name == null && line.startsWith("Name")) {
                        if (line.charAt(4) == '[' && !line.startsWith("es", 5)) continue;
                        name = line.charAt(4) == '[' ? line.substring(9) : line.substring(5);

                    }
                    if (mimeTypes == null && line.startsWith("MimeType")) {
                        mimeTypes = line.substring(9).split(";");
                        if (mimeTypes == null) mimeTypes = new String[]{""};
                    }
                    if (line.startsWith("NoDisplay")) {
                        isDisplay = !Boolean.parseBoolean(line.substring(10, 14));
                    }
                    if (iconText.equals("") && line.startsWith("Icon")) {
                        iconText = line.substring(5);
                    }
                }
            } while(line != null);
        } catch (IOException e) {
            printError("Error al leer archivo '"+desktopFile.getAbsolutePath()+"'", e);
        }

        if (name == null) name = "";
    }

    public void openWith(FileLabel selectedItem) {
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
            File file = new File(iconText);
            if (file.exists()) {
                icon = new Image("file://"+iconText);
            } else {
                try {
                    printExecute("Buscando icono de '"+YELLOW+name+RESET+"'");

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

                    icon = new Image("file://"+new String(process.getInputStream().readAllBytes()).strip());
                } catch (IOException e) {
                    printError("Error al carga icono de "+name, e);
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
