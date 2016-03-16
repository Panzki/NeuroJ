/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cntFile;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matthias
 */
public class cntFileTest {
    
    cntFile file01;
    cntFile file02;
    
    public cntFileTest() {
        this.file01 = new cntFile("./test_data/test01.cnt");
        this.file02 = new cntFile("./test_data/test02.cnt");
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void file01NumberHeaderTest(){
        int expectedNChannels = 31;
        assertEquals(expectedNChannels, this.file01.getNumberOfChannels());
        long expectedNSamples = 227240;
        assertEquals(this.file01.getNumberOfSamples(), expectedNSamples);
        int expectedSamplingRate = 1000;
        assertEquals(expectedSamplingRate, this.file01.getSamplingRate());
    }
    
    @Test
    public void file02NumberHeaderTest(){
        int expectedNChannels = 18;
        assertEquals(expectedNChannels, this.file02.getNumberOfChannels());
        long expectedNSamples = 304900;
        assertEquals(this.file02.getNumberOfSamples(), expectedNSamples);
        int expectedSamplingRate = 500;
        assertEquals(expectedSamplingRate, this.file02.getSamplingRate());
    }
    
    @Test
    public void file01ReadDataTest(){
        try{
            double[] expectedChannel1 = new double[]{-3073.0, -3065.0, -3073.0,
                -3093.0, -3118.0};
            double[] expectedChannel23 = new double[]{2296.0, 2311.0, 2323.0,
                2328.0, 2326.0};
            double[][] data = new double[this.file01.getNumberOfChannels()]
                    [(int)this.file01.getNumberOfSamples()];
            this.file01.readData(data);
            for(int i=0;i<expectedChannel1.length;i++){
                assertEquals(expectedChannel1[i], data[0][i],0);
            }
            for(int i=50;i<expectedChannel23.length;i++){
                assertEquals(expectedChannel23[i], data[23][i],0);
            }
        } catch(IOException ex){
            System.out.println(ex);
            fail("Failed file01ReadDataTest");
        }
    }
    
    @Test
    public void file02ReadDataTest(){
        try{
            double[] expectedChannel8 = new double[]{343.0, 0.0, 0.0, 0.0 ,-1154.0};
            double[] expectedChannel15 = new double[]{-24567.0, -1.0, -17436.0,
                -1.0, -18227.0};
            double[][] data = new double[this.file02.getNumberOfChannels()]
                    [(int)this.file02.getNumberOfSamples()];
            this.file02.readData(data);
            for(int i=50;i<expectedChannel8.length;i++){
                assertEquals(expectedChannel8[i], data[8][i],0);
            }
            for(int i=1000;i<expectedChannel15.length;i++){
                assertEquals(expectedChannel15[i], data[15][i],0);
            }
        } catch(IOException ex){
            System.out.println(ex);
            fail("Failed file02ReadDataTest");
        }
    }
}
