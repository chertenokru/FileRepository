package ru.chertenok.filerepository.common;

import ru.chertenok.filerepository.common.WorkRequest;

public class MyCalculation extends WorkRequest{
    private final int n;
    public MyCalculation(int i) {
        n=i;
    }

    @Override
    public Object execute() {
        return n*n;
    }
}
