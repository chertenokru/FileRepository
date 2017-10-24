package ru.chertenok.filerepository.common;

import java.io.Serializable;

public abstract  class WorkRequest implements Serializable {
    public abstract Object execute();

}
