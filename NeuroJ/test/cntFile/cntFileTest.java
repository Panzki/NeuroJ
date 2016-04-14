/* 
 * Copyright (C) 2016 Matthias Steffen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cntFile;

import java.io.IOException;
import neuroJExceptions.BadChannelException;
import neuroJExceptions.BadIntervallException;
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
    
    static cntFile file01;
    static cntFile file02;

    @BeforeClass
    public static void setUpClass() {
        file01 = new cntFile("./test_data/test01.cnt");
        file02 = new cntFile("./test_data/test02.cnt");
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
        assertEquals(expectedNChannels, file01.getNumberOfChannels());
        long expectedNSamples = 227240;
        assertEquals(file01.getNumberOfSamples(), expectedNSamples);
        int expectedSamplingRate = 1000;
        assertEquals(expectedSamplingRate, file01.getSamplingRate());
    }
    
    @Test
    public void file02NumberHeaderTest(){
        int expectedNChannels = 18;
        assertEquals(expectedNChannels, file02.getNumberOfChannels());
        long expectedNSamples = 304900;
        assertEquals(file02.getNumberOfSamples(), expectedNSamples);
        int expectedSamplingRate = 500;
        assertEquals(expectedSamplingRate, file02.getSamplingRate());
    }
    
    @Test
    public void file01ReadDataTest(){
        try{
            double[] expectedChannel1 = new double[]{-3073.0, -3065.0, -3073.0,
                -3093.0, -3118.0};
            double[] expectedChannel23 = new double[]{2296.0, 2311.0, 2323.0,
                2328.0, 2326.0};
            double[][] data = new double[file01.getNumberOfChannels()]
                    [(int)file01.getNumberOfSamples()];
            file01.readRawData(data);
            for(int i=0;i<expectedChannel1.length;i++){
                assertEquals(expectedChannel1[i], data[0][i],0);
            }
            for(int i=0;i<expectedChannel23.length;i++){
                assertEquals(expectedChannel23[i], data[23][i+50],0);
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
            double[] expectedChannel15 = new double[]{0.0, -1.0, 0.0, -1.0, 0.0};
            double[][] data = new double[file02.getNumberOfChannels()]
                    [(int)file02.getNumberOfSamples()];
            file02.readRawData(data);
            for(int i=0;i<expectedChannel8.length;i++){
                assertEquals(expectedChannel8[i], data[8][i],0);
            }
            for(int i=0;i<expectedChannel15.length;i++){
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
           double[][] data = new double[file01.getNumberOfChannels()]
                    [(int)file01.getNumberOfSamples()];
            file01.readRawData(data);
            file01.scaleData(data);
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
           double[][] data = new double[file02.getNumberOfChannels()]
                    [(int)file02.getNumberOfSamples()];
            file02.readRawData(data);
            file02.scaleData(data);
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
    
    @Test
    public void file01readRawIntervallDataTest(){
        try{
            double[] expectedChannel5 = new double[]{-970.0, -963.0, -961.0,
                -959.0, -956.0};
            double[] expectedChannel6 = new double[]{-47.0, -38.0, -31.0,
                -30.0, -38.0};
            double[] data = new double[file01.calculateArraySizeFromSecIntervall(0.005)];
            file01.readRawIntervallData(5,0.001,0.006,data);
            for(int i=0;i<expectedChannel5.length;i++){
                assertEquals(expectedChannel5[i], data[i],0);
            }
            file01.readRawIntervallData(6, 0.005, 0.01, data);
            for(int i=0;i<expectedChannel6.length;i++){           
                assertEquals(expectedChannel6[i], data[i],0);
            }
        } catch(IOException | BadChannelException | BadIntervallException ex){
            System.out.println(ex);
            fail("Failed file02radRawIntervallDataTest");
        }
    }
    
    @Test
    public void file02readRawIntervallDataTest(){
        try{
            double[] expectedChannel8 = new double[]{11995, -10538, 9635, 9310, 11557};
            double[] expectedChannel2 = new double[]{31665.0, -5115.0, -28314.0,
                3928.0, -27383.0};
            double[] data = new double[file02.calculateArraySizeFromSecIntervall(0.1)];
            file02.readRawIntervallData(8,0.0,0.01,data);
            for(int i=0;i<expectedChannel8.length;i++){
                assertEquals(expectedChannel8[i], data[i],0);
            }
            file02.readRawIntervallData(2, 0.008, 0.018, data);
            for(int i=0;i<expectedChannel2.length;i++){
                assertEquals(expectedChannel2[i], data[i],0);
            }
        } catch(IOException | BadChannelException | BadIntervallException ex){
            System.out.println(ex);
            fail("Failed file02radRawIntervallDataTest");
        }
    }
    
    @Test
    public void file01getChannelNamesTest(){
        System.out.println("cntFile.cntFileTest.file01getChannelNamesTest()");
        String[] channelNames = file01.getChannelNames();
        assertEquals("FP1", channelNames[0]);
        assertEquals("FP2", channelNames[1]);
        assertEquals("T3", channelNames[12]);
        assertEquals("Pz", channelNames[17]);
        assertEquals("CB2", channelNames[30]);
    }
}
