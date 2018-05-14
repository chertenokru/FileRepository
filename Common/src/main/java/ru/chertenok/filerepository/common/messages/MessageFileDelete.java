package ru.chertenok.filerepository.common.messages;

import ru.chertenok.filerepository.common.FileInfo;

public class MessageFileDelete extends Message{
    public FileInfo fileInfo;

    public MessageFileDelete(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }
}
