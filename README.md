# NeuroJ
Java implementation for the Neuroscan cnt/eeg file format.

# Example Usage
```java
import NeuroJ.cntFile.cntFile;
import java.io.IOException;

    try{
        //make new cntFile
        cntFile file = new cntFile("PATH_TO_CNT_FILE");
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
```
