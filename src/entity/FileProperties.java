package entity;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static main.Lib.*;
import static panel.MainPane.selectedItem;

public class FileProperties extends File{
    public FileProperties(File file) {
        super(file.toURI());

        String result = null;
        try {
            ProcessBuilder pb = new ProcessBuilder("ls", "-la", "--block-size=1", file.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = out.readLine();
                if (line.startsWith("total")) line = out.readLine();
                result = line;
            } catch (IOException e) {
                printError("No se pudo tomar la salida de '"+YELLOW+"ls -la --block-size=1 "+file.getAbsolutePath()+RESET, e);
            }
        } catch (IOException e) {
            printError("No se pudo ejecutar '"+YELLOW+"ls -la --block-size=1 "+file.getAbsolutePath()+RESET, e);
        }

        if (result.startsWith("ls")) {
            type='?';
            ownerPermissions = new char[]{'?', '?', '?'};
            groupPermissions = new char[]{'?', '?', '?'};
            otherPermissions = new char[]{'?', '?', '?'};
            owner="unknow";
            group="unknow";
            size=0;
            dateTime=LocalDateTime.now();
        } else {
            String[] fields = result.trim().split("\\s+");

            type=fields[0].charAt(0);
            boolean isBlockOrCharset = type == 'b' || type == 'c';
            ownerPermissions=fields[0].substring(1, 4).toCharArray();
            groupPermissions=fields[0].substring(4, 7).toCharArray();
            otherPermissions=fields[0].substring(7, 10).toCharArray();
            owner=fields[2];
            group=fields[3];
            size= isBlockOrCharset ? 0 : Long.parseLong(fields[4]);
            dateTime=LocalDateTime.of(0,
                    toMonth(fields[isBlockOrCharset ? 6 : 5]),
                    Integer.parseInt(fields[isBlockOrCharset ? 7 : 6]),
                    Integer.parseInt(fields[isBlockOrCharset ? 8 : 7].substring(0, 2)),
                    Integer.parseInt(fields[isBlockOrCharset ? 8 : 7].substring(3))
            );
        }


        try {
            ProcessBuilder pb = new ProcessBuilder("file", "--mime-type", "-b", file.getAbsolutePath());
            Process process = pb.start();
            mimeType = new String(process.getInputStream().readAllBytes()).trim();
        } catch (Exception ex) {
            printError("Error al leer tipo mime de '"+file.getAbsolutePath()+"'", ex);
        }
    }

    public String toString() {
        return "FileProperties["+
                "type="+type+
                ", ownerPermissions="+Arrays.toString(ownerPermissions)+
                ", groupPermissions="+Arrays.toString(groupPermissions)+
                ", otherPermissions="+Arrays.toString(otherPermissions)+
                ", owner="+owner+
                ", group="+group+
                ", size="+size+
                ", dateTime="+dateTime.toString()+
                ", mimeType="+mimeType+"]";
    }

    public Month toMonth(String month) {
        return switch (month) {
            case "jan", "ene" -> Month.JANUARY;
            case "feb" -> Month.FEBRUARY;
            case "mar" -> Month.MARCH;
            case "apr", "abr" -> Month.APRIL;
            case "may" -> Month.MAY;
            case "jun" -> Month.JUNE;
            case "jul" -> Month.JULY;
            case "aug" -> Month.AUGUST;
            case "sep", "ago" -> Month.SEPTEMBER;
            case "oct" -> Month.OCTOBER;
            case "nov" -> Month.NOVEMBER;
            case "dec", "dic" -> Month.DECEMBER;

            default -> null;
        };
    }

    private char type;
    private char[] ownerPermissions;
    private char[] groupPermissions;
    private char[] otherPermissions;
    private String owner;
    private String group;
    private long size;
    private LocalDateTime dateTime;
    private String mimeType;

    public char getType() {return type;}
    public void setType(char type) {this.type = type;}

    public char[] getOwnerPermissions() {return ownerPermissions;}
    public void setOwnerPermissions(char[] ownerPermissions) {this.ownerPermissions = ownerPermissions;}

    public char[] getGroupPermissions() {return groupPermissions;}
    public void setGroupPermissions(char[] groupPermissions) {this.groupPermissions = groupPermissions;}

    public char[] getOtherPermissions() {return otherPermissions;}
    public void setOtherPermissions(char[] otherPermissions) {this.otherPermissions = otherPermissions;}

    public String getOwner() {return owner;}
    public void setOwner(String owner) {this.owner = owner;}

    public String getGroup() {return group;}
    public void setGroup(String group) {this.group = group;}

    public long getSize() {return size;}
    public void setSize(long size) {this.size = size;}

    public LocalDateTime getDateTime() {return dateTime;}
    public void setDateTime(LocalDateTime dateTime) {this.dateTime = dateTime;}

    public String getMimeType() {return mimeType;}
    public void setMimeType(String mimeType) {this.mimeType = mimeType;}
}
