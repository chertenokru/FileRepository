package ru.chertenok.filerepository.common.messages;

import ru.chertenok.filerepository.common.FileInfo;

public class MessageResultFile extends Message{
    public FileInfo fileInfo;
    public byte[] data;

    public MessageResultFile(FileInfo fileInfo, byte[] data) {
        this.fileInfo = fileInfo;
        this.data = data;
    }
}
