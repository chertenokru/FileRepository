package ru.chertenok.filerepository.common.messages;

import ru.chertenok.filerepository.common.FileInfo;

public class MessageFileList extends Message {
    public FileInfo[] fileInfos;

    public MessageFileList(FileInfo[] fileInfos) {
        this.fileInfos = fileInfos;
    }
}
