package rocks.zipcode.calcskin;

import org.junit.Assert;
import org.junit.Test;

public class WhiteBoardTest {

    @Test
    public void sortTest(){
        int[] input = new int[]{2, 5, -55, 8, -72, -1, 53};
        String arrayToString = "";
        for (int j = 0; j<input.length; j++)
        for (int i = 0; i < input.length-1; i++) {
            int temp = input[i++];
            if(input[i]>input[i++]){
                input[i++] = input[i];
                input[i] = temp;
                arrayToString += input[i] + " ";
                System.out.println(input[i]);
            }


        }

        String actual = "-72 -55 -1 2 5 8 53";

        Assert.assertEquals(arrayToString, actual);
    }
}