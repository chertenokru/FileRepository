package ru.chertenok.filerepository.common.messages;

import ru.chertenok.filerepository.common.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageFile extends Message {
    public FileInfo fileInfo;
    public byte[] data;

    public MessageFile(String file) throws IOException {

            fileInfo = new FileInfo(file);
            Path p = Paths.get(file);
            data = Files.readAllBytes(p);
    }
}
