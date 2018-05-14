package ru.chertenok.filerepository.common.messages;

import ru.chertenok.filerepository.common.FileInfo;

public class MessageGetFile extends Message{
    public FileInfo fileInfo;

    public MessageGetFile(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }
}
