package entity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.*;
import java.util.Arrays;

import static main.Lib.*;

public class FileProperties extends File{
    public FileProperties(File file) {
        super(file.toURI());

        try {
            Path path = toPath();
            PosixFileAttributes attributes = Files.readAttributes(path, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

            String permissions = PosixFilePermissions.toString(attributes.permissions());
            ownerPermissions = permissions.substring(0, 3).toCharArray();
            groupPermissions = permissions.substring(3, 6).toCharArray();
            otherPermissions = permissions.substring(6).toCharArray();

            owner = attributes.owner().getName();
            group = attributes.group().getName();

            size = attributes.size();
            dateTime = LocalDateTime.ofInstant(attributes.lastModifiedTime().toInstant(), ZoneId.systemDefault());

            try {
                ProcessBuilder pb = new ProcessBuilder("file", "--mime-type", "-b", file.getAbsolutePath());
                Process process = pb.start();
                mimeType = new String(process.getInputStream().readAllBytes()).trim();
            } catch (Exception ex) {
                mimeType = "?";
                printError("Error al leer tipo mime de '"+file.getAbsolutePath()+"'", ex);
            }
        } catch (Exception e) {
            ownerPermissions = new char[]{'-', '-', '-'};
            groupPermissions = new char[]{'-', '-', '-'};
            otherPermissions = new char[]{'-', '-', '-'};

            owner = "?";
            group = "?";

            size = 0L;
            dateTime = LocalDateTime.now();
            mimeType = "?";
            printError("Error al leer las propiedades de '"+getAbsolutePath()+"'", e);
        }
    }

    public String toString() {
        return "FileProperties["+
                "ownerPermissions="+Arrays.toString(ownerPermissions)+
                ", groupPermissions="+Arrays.toString(groupPermissions)+
                ", otherPermissions="+Arrays.toString(otherPermissions)+
                ", owner="+owner+
                ", group="+group+
                ", size="+size+
                ", dateTime="+dateTime.toString()+
                ", mimeType="+mimeType+"]";
    }

    private char[] ownerPermissions;
    private char[] groupPermissions;
    private char[] otherPermissions;
    private String owner;
    private String group;
    private long size;
    private LocalDateTime dateTime;
    private String mimeType;

    public char[] getOwnerPermissions() {return ownerPermissions;}
    public char[] getGroupPermissions() {return groupPermissions;}
    public char[] getOtherPermissions() {return otherPermissions;}
    public String getOwner() {return owner;}
    public String getGroup() {return group;}
    public long getSize() {return size;}
    public LocalDateTime getDateTime() {return dateTime;}
    public String getMimeType() {return mimeType;}
}
