package com.example.abhiraj.cardviewwithrecyclerview;

/**
 * Created by Abhiraj on 12-03-2017.
 */

public class MyHolder<T> {

    private T value;

    public MyHolder(){}

    public MyHolder(T val)
    {
        value = val;
    }

    public void setValue(T val)
    {
        value = val;
    }

    public T getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return String.valueOf(value);
    }
}
