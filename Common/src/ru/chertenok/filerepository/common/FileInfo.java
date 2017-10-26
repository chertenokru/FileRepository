package ru.chertenok.filerepository.common;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileInfo implements Serializable {
    // имя файла
    public final String fileName;
    // полный исходный путь
    public final String SourceFileName;
    // дата модицикации
    public final String fileDT;
    public final long fileSize;
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
}
