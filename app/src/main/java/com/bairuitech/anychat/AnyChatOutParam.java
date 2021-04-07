package com.bairuitech.anychat;

import java.util.Arrays;

public class AnyChatOutParam {
    private byte[] byteArray;
    private double fValue = 0.0d;
    private int iValue = 0;
    private int[] intArray;
    private String szValue = "";

    public int GetIntValue() {
        return this.iValue;
    }

    public void SetIntValue(int v) {
        this.iValue = v;
    }

    public double GetFloatValue() {
        return this.fValue;
    }

    public void SetFloatValue(double f) {
        this.fValue = f;
    }

    public String GetStrValue() {
        return this.szValue;
    }

    public void SetStrValue(String s) {
        this.szValue = s;
    }

    public int[] GetIntArray() {
        return this.intArray;
    }

    public void SetIntArray(int[] a) {
        this.intArray = a;
    }

    public byte[] GetByteArray() {
        return this.byteArray;
    }

    public void SetByteArray(byte[] b) {
        this.byteArray = b;
    }

    @Override
    public String toString() {
        return "AnyChatOutParam{" +
                "byteArray=" + Arrays.toString(byteArray) +
                ", fValue=" + fValue +
                ", iValue=" + iValue +
                ", intArray=" + Arrays.toString(intArray) +
                ", szValue='" + szValue + '\'' +
                '}';
    }
}
