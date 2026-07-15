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
            modifiedDateTime = LocalDateTime.ofInstant(attributes.lastModifiedTime().toInstant(), ZoneId.systemDefault());
            creationDateTime = LocalDateTime.ofInstant(attributes.creationTime().toInstant(), ZoneId.systemDefault());

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
                            setModifiedDateTime(LocalDateTime.parse(trashProperties.getProperty("DeletionDate")));
                        } catch (Exception e) {
                            printError("Error al leer archivo '"+trashInfo.getAbsolutePath()+"'", e);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            if (ownerPermissions == null) ownerPermissions = new char[]{'-', '-', '-'};
            if (groupPermissions == null) groupPermissions = new char[]{'-', '-', '-'};
            if (otherPermissions == null) otherPermissions = new char[]{'-', '-', '-'};

            if (owner == null) owner = "?";
            if (group == null) group = "?";

            if (size == 0) size = 0L;
            if (modifiedDateTime == null) modifiedDateTime = LocalDateTime.now();
            if (creationDateTime == null) creationDateTime = LocalDateTime.now();
            if (mimeType == null) mimeType = "?";
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
                ", dateTime="+ modifiedDateTime.toString()+
                ", mimeType="+mimeType+"]";
    }

    private char[] ownerPermissions;
    private char[] groupPermissions;
    private char[] otherPermissions;
    private String owner;
    private String group;
    private long size;
    private LocalDateTime modifiedDateTime;
    private LocalDateTime creationDateTime;
    private String mimeType;
    private String trashPath;
    private File trashInfo;

    public char[] getOwnerPermissions() {return ownerPermissions;}
    public char[] getGroupPermissions() {return groupPermissions;}
    public char[] getOtherPermissions() {return otherPermissions;}
    public String getOwner() {return owner;}
    public String getGroup() {return group;}
    public long getSize() {return size;}
    public String getSizeString() {
        String sizeText = String.valueOf(size);
        int sizeTextLength = sizeText.length();

        if (size >= 1000000000000L)
            sizeText = sizeText.substring(0, sizeText.length() - 12) + "," + sizeText.substring(sizeTextLength - 12, sizeTextLength - 10) + " TB";
        else if (size >= 1000000000)
            sizeText = sizeText.substring(0, sizeText.length() - 9) + "," + sizeText.substring(sizeTextLength - 9, sizeTextLength - 7) + " GB";
        else if (size >= 1000000)
            sizeText = sizeText.substring(0, sizeText.length() - 6) + "," + sizeText.substring(sizeTextLength - 6, sizeTextLength - 4) + " MB";
        else if (size >= 1000)
            sizeText = sizeText.substring(0, sizeTextLength - 3) + "," + sizeText.substring(sizeTextLength - 3, sizeTextLength - 1) + " KB";
        else sizeText += " BI";

        return sizeText;
    }
    public LocalDateTime getModifiedDateTime() {return modifiedDateTime;}
    public String getModifiedString() {
        LocalDateTime now = LocalDateTime.now();

        String hour = String.valueOf(modifiedDateTime.getHour());
        String minute = String.valueOf(modifiedDateTime.getMinute());
        String day = String.valueOf(modifiedDateTime.getDayOfMonth());
        String month = String.valueOf(modifiedDateTime.getMonthValue());

        return  modifiedDateTime.isAfter(now.minusDays(1)) ? (hour.length() == 1 ? "0"+hour : hour) + ":" + (minute.length() == 1 ? "0"+minute : minute) :
                (day.length() == 1 ? "0"+day : day) + "/" + (month.length() == 1 ? "0"+month : month) +
                (modifiedDateTime.isAfter(now.minusYears(1)) ? "" : "/" + modifiedDateTime.getYear());
    }
    public LocalDateTime getCreationDateTime() {return creationDateTime;}
    public String getCreationString() {
        LocalDateTime now = LocalDateTime.now();

        String hour = String.valueOf(creationDateTime.getHour());
        String minute = String.valueOf(creationDateTime.getMinute());
        String day = String.valueOf(creationDateTime.getDayOfMonth());
        String month = String.valueOf(creationDateTime.getMonthValue());

        return  creationDateTime.isAfter(now.minusDays(1)) ? (hour.length() == 1 ? "0"+hour : hour) + ":" + (minute.length() == 1 ? "0"+minute : minute) :
                (day.length() == 1 ? "0"+day : day) + "/" + (month.length() == 1 ? "0"+month : month) +
                (creationDateTime.isAfter(now.minusYears(1)) ? "" : "/" + creationDateTime.getYear());
    }
    public String getMimeType() {return mimeType;}
    public String getTrashPath() {return trashPath;}
    public File getTrashInfo() {return trashInfo;}

    public void setModifiedDateTime(LocalDateTime modifiedDateTime) {this.modifiedDateTime = modifiedDateTime;}
    public void setTrashPath(String trashPath) {this.trashPath=trashPath;}
}
