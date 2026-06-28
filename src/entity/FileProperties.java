package entity;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Properties;

import static main.Lib.TRASH;
import static main.Lib.printError;

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

            // Mime Type
            try {
                ProcessBuilder pb = new ProcessBuilder("file", "--mime-type", "-b", file.getAbsolutePath());
                Process process = pb.start();
                mimeType = new String(process.getInputStream().readAllBytes()).trim();
            } catch (Exception ex) {
                mimeType = "?";
                printError("Error al leer tipo mime de '"+file.getAbsolutePath()+"'", ex);
            }

            // Trash Path
            if (path.startsWith(TRASH+"files")) {
                for (File trashInfo : new File(TRASH+"info").listFiles()) {
                    String trashInfoName = trashInfo.getName();
                    if (trashInfoName.substring(0, trashInfoName.length()-10).equals(file.getName())) {

                        this.trashInfo=trashInfo;

                        try (FileInputStream input = new FileInputStream(trashInfo)) {
                            Properties trashProperties = new Properties();
                            trashProperties.load(input);

                            setTrashPath(trashProperties.getProperty("Path"));
                            setDateTime(LocalDateTime.parse(trashProperties.getProperty("DeletionDate")));
                        } catch (Exception e) {
                            printError("Error al leer archivo '"+trashInfo.getAbsolutePath()+"'", e);
                        }
                        break;
                    }
                }
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
            trashPath = null;
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
    public String getMimeType() {return mimeType;}

    public void setDateTime(LocalDateTime dateTime) {this.dateTime=dateTime;}
    public LocalDateTime getDateTime() {return dateTime;}

    private String trashPath;
    public void setTrashPath(String trashPath) {this.trashPath=trashPath;}
    public String getTrashPath() {return trashPath;}
    private File trashInfo;
    public File getTrashInfo() {return trashInfo;}
}
