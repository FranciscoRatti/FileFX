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
import static main.Main.desktopApplications;
import static panel.MainPane.selectedFile;
import static panel.MainPane.selectedItem;

public class FileProperties extends File{
    public FileProperties(File file) {
        super(file.toURI());

        String result = null;
        try {
            ProcessBuilder pb = new ProcessBuilder("ls", "-la", "--block-size=1", file.getAbsolutePath());
            pb.redirectErrorStream(false);
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

        String[] fields = result.trim().split("\\s+");

        type=fields[0].charAt(0);
        ownerPermissions=fields[0].substring(1, 4).toCharArray();
        groupPermissions=fields[0].substring(4, 7).toCharArray();
        otherPermissions=fields[0].substring(7, 10).toCharArray();
        owner=fields[2];
        group=fields[3];
        size=Long.parseLong(fields[4]);
        dateTime=LocalDateTime.of(0,
                toMonth(fields[5]),
                Integer.parseInt(fields[6]),
                Integer.parseInt(fields[7].substring(0, 2)),
                Integer.parseInt(fields[7].substring(3))
        );

        try {
            ProcessBuilder pb = new ProcessBuilder("file", "--mime-type", "-b", selectedItem.getFile().getAbsolutePath());
            Process process = pb.start();
            mimeType = new String(process.getInputStream().readAllBytes()).trim();
        } catch (Exception ex) {
            printError("Error al leer tipo mime de '"+selectedItem.getFile()+"'", ex);
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

    public static Month toMonth(String month) {
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

    private static char type;
    private static char[] ownerPermissions;
    private static char[] groupPermissions;
    private static char[] otherPermissions;
    private static String owner;
    private static String group;
    private static long size;
    private static LocalDateTime dateTime;
    private static String mimeType;

    public static char getType() {return type;}
    public static void setType(char type) {FileProperties.type = type;}

    public static char[] getOwnerPermissions() {return ownerPermissions;}
    public static void setOwnerPermissions(char[] ownerPermissions) {FileProperties.ownerPermissions = ownerPermissions;}

    public static char[] getGroupPermissions() {return groupPermissions;}
    public static void setGroupPermissions(char[] groupPermissions) {FileProperties.groupPermissions = groupPermissions;}

    public static char[] getOtherPermissions() {return otherPermissions;}
    public static void setOtherPermissions(char[] otherPermissions) {FileProperties.otherPermissions = otherPermissions;}

    public static String getOwner() {return owner;}
    public static void setOwner(String owner) {FileProperties.owner = owner;}

    public static String getGroup() {return group;}
    public static void setGroup(String group) {FileProperties.group = group;}

    public static long getSize() {return size;}
    public static void setSize(long size) {FileProperties.size = size;}

    public static LocalDateTime getDateTime() {return dateTime;}
    public static void setDateTime(LocalDateTime dateTime) {FileProperties.dateTime = dateTime;}

    public static String getMimeType() {return mimeType;}
    public static void setMimeType(String mimeType) {FileProperties.mimeType = mimeType;}
}
