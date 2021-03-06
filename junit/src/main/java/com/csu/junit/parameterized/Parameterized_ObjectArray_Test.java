package com.csu.junit.parameterized;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.LinkedList;
import java.util.List;

@RunWith(Parameterized.class)
public class Parameterized_ObjectArray_Test {

    public String param;

    public Parameterized_ObjectArray_Test(String param) {
        this.param = param;
    }

    @Test
    public void test() {
        System.out.println(param);
    }

    @Parameterized.Parameters
    public static Object[] parameters() {

        List<String> list = new LinkedList<>();
        list.add("one");
        list.add("two");
        list.add("three");

        return list.toArray(new String[0]);
    }
}
