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

    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.file01 = new cntFile("./test_data/test01.cnt");
        this.file02 = new cntFile("./test_data/test02.cnt");
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
            double[] expectedChannel8 = new double[]{11995, -10538, 9635, 9310, 11557};
            double[] expectedChannel15 = new double[]{0.0, -1.0, 0.0, -1.0, 0,0};
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
    
    @Test
    public void file01ScaleDataTest(){
       try{
           double[] expectedChannel1 = new double[]{-265.4630, -264.7719,
               -265.4630, -267.1907, -269.3504};
           double[] expectedChannel14 = new double[]{-114.4631, -117.6985,
               -121.0213, -123.4697, -124.8688};
           double[] expectedChanne21 = new double[]{-32.6188, -25.2839,
               -19.8474, -18.0350, -18.8982};
           double[][] data = new double[this.file01.getNumberOfChannels()]
                    [(int)this.file01.getNumberOfSamples()];
            this.file01.readData(data);
            this.file01.scaleData(data);
            for(int i=0;i<expectedChannel1.length;i++){
                assertEquals(expectedChannel1[i], data[0][i],0.0009);
            }
            for(int i=0;i<expectedChannel14.length;i++){
                assertEquals(expectedChannel14[i], data[14][i+4], 0.0009);
            }
            for(int i=0;i<expectedChanne21.length;i++){
                assertEquals(expectedChanne21[i], data[21][i], 0.0009);
            }
       }catch(IOException ex){
           System.out.println(ex);
           fail("failed file01ScaleDataTest");
       } 
    }
    
    @Test
    public void file02ScaleDataTest(){
       try{
           double[] expectedChannel1 = new double[]{-9.5471, 6.6719, -12.8906, 
               -4.9683, -10.8362};
           double[] expectedChannel3 = new double[]{0, -0.0017, 0, -0.0017, 0};
           double[] expectedChanne10 = new double[]{-19.6415, -5.0018, -15.2506,
               -5.0086, -15.2657};
           double[][] data = new double[this.file02.getNumberOfChannels()]
                    [(int)this.file02.getNumberOfSamples()];
            this.file02.readData(data);
            this.file02.scaleData(data);
            for(int i=0;i<expectedChannel1.length;i++){
                assertEquals(expectedChannel1[i], data[0][i],0.0009);
                //System.out.println(data[0][i]);
            }
            for(int i=0;i<expectedChanne10.length;i++){
                assertEquals(expectedChanne10[i], data[10][i+4], 0.0009);
            }
            for(int i=0;i<expectedChannel3.length;i++){
                assertEquals(expectedChannel3[i], data[3][i], 0.0009);
            }
       }catch(IOException ex){
           System.out.println(ex);
           fail("failed file02ScaleDataTest");
       } 
    }
}
