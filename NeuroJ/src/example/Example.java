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
package example;

import cntFile.cntFile;
import java.io.IOException;

/**
 * Basic example on how to read data from a cnt file.
 * @author Matthias Steffen
 */
public class Example {
    public static void main(String args[]){
        try{
            //make new cntFile
            cntFile file = new cntFile("./test_data/test01.cnt");
            //make a array for the data x dimension is channels
            //y dimension are the samples
            double[][] data = new double[file.getNumberOfChannels()]
                    [(int)file.getNumberOfSamples()];
            //read the data from the file
            file.readRawData(data);
            //and scale the data to micro volts
            file.scaleData(data);
            for(int i=0;i<10;i++){
                //channel counting starts at 0 !!!
                System.out.println(data[0][i]);
            }
        }catch(IOException ex){
            System.out.println(ex);
        }
    }
}
