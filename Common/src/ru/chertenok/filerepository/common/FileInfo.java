package ru.chertenok.filerepository.common;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileInfo implements Serializable {
    // имя файла
    public  String fileName;
    // полный исходный путь
    public  String SourceFileName;
    // дата модицикации
    public  String fileDT;
    public  long fileSize;
    public String ID;
    //public final

    public FileInfo(String fullFileName) throws IOException {
        Path p = Paths.get(fullFileName);
        if (!Files.exists(p,LinkOption.NOFOLLOW_LINKS)) throw  new IOException("File not exist "+fullFileName );

        fileDT = Files.getLastModifiedTime(p, LinkOption.NOFOLLOW_LINKS).toString();
        SourceFileName = fullFileName;
        fileName = p.getFileName().toString();
        fileSize = Files.size(p);

    }

    public FileInfo() {
    }

    public FileInfo(String fileName, String sourceFileName, String fileDT, long fileSize, String ID) {
        this.fileName = fileName;
        SourceFileName = sourceFileName;
        this.fileDT = fileDT;
        this.fileSize = fileSize;
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", SourceFileName='" + SourceFileName + '\'' +
                ", fileDT='" + fileDT + '\'' +
                ", fileSize=" + fileSize +
                ", ID='" + ID + '\'' +
                '}';
    }
}
