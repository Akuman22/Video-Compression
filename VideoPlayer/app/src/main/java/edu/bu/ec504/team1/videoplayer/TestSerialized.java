package edu.bu.ec504.team1.videoplayer;

import java.io.Serializable;

/**
 * Created by heqifan on 10/28/17.
 */

public class TestSerialized implements Serializable {
    private String val1;
    private int val2;

    TestSerialized(){
        val1 = "Hello world!";
        val2 = 100;
    }

}
