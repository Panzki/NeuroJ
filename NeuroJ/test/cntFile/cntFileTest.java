/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cntFile;

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
    
    /*
    todo: test channel headers, readData
    */
    
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
}
