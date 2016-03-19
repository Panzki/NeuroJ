/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cntFile;

import shared.MyConstans;
import shared.ChannelHeader;
import neuroJExceptions.BadChannelException;
import neuroJExceptions.BadIntervallException;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 *
 * @author matthias
 */
public class cntFile {
    private ChannelHeader[] channelHeaders;
    private long channelOffset;
    private long eventTablePos;
    private String fileVersion;
    private int nchannels;
    private long nums;
    //todo: rename vars
    private RandomAccessFile rawFile;
    private int samplingRate;
    
    public cntFile(String path){
        try{
            this.rawFile = new RandomAccessFile(path, "r");
            this.readFileHeader();
            this.channelHeaders = this.readChannelHeader();
        } catch(IOException ex){
            System.out.println(ex);
        }
    }
    
    /**
     * Calculate the size an array has to be to fit all the samples for a given
     * intervall (in sec).
     * @param intervall in SEC
     * @return 
     */
    public int calculateArraySizeFromSecIntervall(double intervall){
        return (int)Math.ceil(intervall*this.samplingRate);
    }
    
    public int getNumberOfChannels(){
        return this.nchannels;
    }
    
    public long getNumberOfSamples(){
        return this.nums;
    }
    
    public int getSamplingRate(){
        return this.samplingRate;
    }
    
    /**
     * Reads all data for all channels from the .cnt file.
     * @param data[number of channels][number of samples]
     * @throws IOException 
     */
    public void readRawData(double data[][]) throws IOException,
            ArrayIndexOutOfBoundsException{
        if(data.length != this.nchannels || data[0].length<this.nums){
            throw new ArrayIndexOutOfBoundsException("Failed to read data to"
                    + " the given array. Make sure that the dimesions of the"
                    + " array are nchannels*nsamples!");
        }
        //seek 900bytes, for the file header and 75bytes for each channel header
        this.rawFile.seek(nchannels*75 + 900);
        byte[] buffer = new byte[2];
        long bytesToRead = channelOffset*nchannels;
        /*
        The channel offset determines how the data is stored in the file. If the
        channel offset is <=1 data is ordered by channels. It starts with the first
        sample for the first channel, folllowd continously by all following
        samples for this channel.
        If the channelOffset is >1 data is stored in chunks. Each chunk contais
        channelOffset/2 (one sample = 2 bytes) samples for each channel. The
        chunk begins with channelOffset many bytes for channel 1 then channelOffset
        many bytes for channel 2 and so on.
        */
        if(channelOffset <= 1){
            for(int currentOffset=0;currentOffset<nums;currentOffset++){
                for(int currentChannel=0;currentChannel<nchannels;currentChannel++){   
                    this.rawFile.read(buffer);
                    byte[] bb = {0,0,buffer[1],buffer[0]};
                    //this will give us a correct signed value corresponding to int16
                    data[currentChannel][currentOffset] = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
                }
            }
        }
        else{
            //read first chunk of data
            for(int currentChannel=0;currentChannel<nchannels;currentChannel++){
                for(int currentOffset=0;currentOffset<(channelOffset/2);currentOffset++){
                    this.rawFile.read(buffer);
                    byte[] bb = {0,0,buffer[1],buffer[0]};
                    //this will give us a correct signed value corresponding to int16
                    data[currentChannel][currentOffset] = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
                }
            }

            int chunkCounter = 1;
            while(chunkCounter*(channelOffset/2) < nums){
                for(int currentChannel=0;currentChannel<nchannels;currentChannel++){
                    for(int currentOffset=0;currentOffset<(channelOffset/2);currentOffset++){
                        //it is possible that the last chunk is not completly filled with data,
                        //to prevent an array out of bounds exception we have to leave the loops
                        //after reading the last datapoint
                        if((int)((chunkCounter*(channelOffset/2))+(currentOffset)) == (data[0].length)-1){
                            return;
                        }
                        this.rawFile.read(buffer);
                        byte[] bb = {0,0,buffer[1],buffer[0]};
                        //this will give us a correct signed value corresponding to int16
                        data[currentChannel][(int)((chunkCounter*(channelOffset/2))+(currentOffset))]
                                = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
                    }
                }
                chunkCounter++;
            }
        }
    }
    
    public void readRawIntervallData(int channel, double sTimeSec, double eTimeSec, double data[])
        throws IOException, BadChannelException, BadIntervallException, 
            ArrayIndexOutOfBoundsException{
        if(channel>nchannels-1 || channel<0){
            throw new BadChannelException("Invalid channel! Channel number given: " 
                    +  channel);
        }
        else if(sTimeSec < 0 || eTimeSec < sTimeSec || 
                eTimeSec > (this.nums/this.samplingRate)){
            throw new BadIntervallException("Invalid intervall! sTime: " + 
                    sTimeSec + " eTime: " + eTimeSec);
        }
        else if(data.length < (int)Math.ceil((eTimeSec-sTimeSec)*samplingRate)){
            throw new ArrayIndexOutOfBoundsException("Data array to small for choosen intervall!");
        }
        byte[] buffer = new byte[2];
        long sTimeTick = (long)Math.floor(sTimeSec*samplingRate);
        long eTimeTick = (long)Math.ceil(eTimeSec*samplingRate);
        //base offset for file header and channel headers
        int dataOffset = nchannels*75 + 900;
        if(channelOffset<=1){
            dataOffset += (sTimeTick*this.nchannels) + channel;
            this.rawFile.seek(dataOffset);
            //i<Math.floor((eTimeTick-sTimeTick-1)*2)
            for(int i=0;i<data.length;i++){
                this.rawFile.read(buffer);
                byte[] bb = {0,0,buffer[1],buffer[0]};
                //this will give us a correct signed value corresponding to int16
                data[i] = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
                dataOffset += (this.nchannels-1);
                this.rawFile.seek(dataOffset);
            }
        }
        else{
            int numberOfDataPointsToRead = (int)((eTimeSec-sTimeSec)*samplingRate);
            int chunkSizeBytes = (int)(channelOffset*nchannels);
            int numberOfChunksToRead = (int)Math.ceil((numberOfDataPointsToRead*2)/channelOffset);
            int firstChunkToRead = (int)Math.floor((2*sTimeTick)/channelOffset);
            //seek to the right chunk
            dataOffset += firstChunkToRead*chunkSizeBytes;
            //ssek to the right channel in the chunk
            dataOffset += channel*channelOffset;
            this.rawFile.seek(dataOffset);
            double[] chunkData = new double[(int)Math.floor(channelOffset/2)];
            for(int i=0;i<chunkData.length;i++){
                this.rawFile.read(buffer);
                byte[] bb = {0,0,buffer[1],buffer[0]};
                //this will give us a correct signed value corresponding to int16
                chunkData[i] = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
            }
            numberOfChunksToRead--;
            int sTickChunk = (int)Math.floor(firstChunkToRead*(channelOffset/2));
            //determine which datapoint in the chunk is the first one for the given intervall,
            //since the chunk's data could begin earlyer
            int firstTickInChunk = ((int)sTimeTick-sTickChunk);
            int firstChunkOffset = 0;
            for(int i=firstTickInChunk;i<chunkData.length;i++){
                data[firstChunkOffset] = chunkData[i];
                firstChunkOffset++;
            }
            //skip to the second chunk and the right channel in the chunk
            dataOffset += chunkSizeBytes+(channel*channelOffset);
            this.rawFile.seek(dataOffset);
            int n = 0;
            //read all the contius chunks
            for(int chunkCounter=0;chunkCounter<(numberOfChunksToRead-1);chunkCounter++){
                //System.out.println("Reading chunk number: " + (chunkCounter+1));
                //read one chunk of data for the channel
                for(int i=0;i<(int)Math.floor(channelOffset/2);i++){
                    this.rawFile.read(buffer);
                    byte[] bb = {0,0,buffer[1],buffer[0]};
                    //this will give us a correct signed value corresponding to int16
                    data[firstChunkOffset+n] = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
                    //System.out.println("index: "+(firstChunkOffset+n) + " data: " + data[firstChunkOffset+n]);
                    n++;
                }
                //skip to the next chunk and the channel in this chunk
                dataOffset += chunkSizeBytes+(channel*channelOffset);
                this.rawFile.seek(dataOffset);
            }
            //read last chunk
            for(int i=0;i<chunkData.length;i++){
                this.rawFile.read(buffer);
                byte[] bb = {0,0,buffer[1],buffer[0]};
                //this will give us a correct signed value corresponding to int16
                chunkData[i] = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
            }
            //fill the x remaining elements of the data array with the first x datapoints from the last chunk
            int x = 0;
            while((firstChunkOffset+n)<data.length){
                data[firstChunkOffset+n] = chunkData[x];
                n++;
                x++;
            }
        }
    }
    
    /**
     * Scales the raw data for on specific channel to micro volts.
     * @param data
     * @param channelNumber 
     */
    public void scaleChannelData(double[] data, int channelNumber){
        int baseline = (int)this.channelHeaders[channelNumber].getBaseline();
        double sensitivity = (double)this.channelHeaders[channelNumber].getSensitivity();
        double calib = (double)this.channelHeaders[channelNumber].getCalib();
        double scaleFactor = sensitivity*(calib/204.8);
        for(int n=0;n<data.length;n++){
            data[n] = (data[n]-baseline)*scaleFactor;
        }
    }
    
    /**
     * Scale the raw data from the file to micro volts.
     * @param data
     */
    public void scaleData(double[][] data){
        for(int i=0;i<this.nchannels;i++){
            int baseline = (int)this.channelHeaders[i].getBaseline();
            double sensitivity = (double)this.channelHeaders[i].getSensitivity();
            double calib = (double)this.channelHeaders[i].getCalib();
            double scaleFactor = sensitivity*(calib/204.8);
            for(int n=0;n<data[i].length;n++){
                data[i][n] = (data[i][n]-baseline)*scaleFactor;
            }
        }
    }
    
    /**
     * Convert time from tick to sec.
     * @param timeInTicks
     * @return 
     */
    private double getSecFromTick(long timeInTicks){
        return (double)(timeInTicks/samplingRate);
    }
    
    /**
     * Convert time from sec to "number of the sample" = tick.
     * @param timeInSec
     * @return
     */
    private long getTickFromSec(double timeInSec){
        return (long)timeInSec*samplingRate;
    }
    
    /**
     * Reads the header informations for all channels form the .cnt file.
     * @return ChannelHeader[] channel header informations
     * @throws IOException 
     */
    private ChannelHeader[] readChannelHeader() throws IOException{
        ChannelHeader[] channelHeaderInformation = new ChannelHeader[nchannels];
        //skip file header
        this.rawFile.seek(900);
        byte[] buffer = new byte[4];
        for(int i=0;i<nchannels;i++){
            channelHeaderInformation[i] = new ChannelHeader();
            this.rawFile.skipBytes(47);
            channelHeaderInformation[i].setBaseline((short)this.rawFile.read());
            this.rawFile.skipBytes(11);
            this.rawFile.read(buffer);
            channelHeaderInformation[i].setSensitivity(
                ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getFloat());
            this.rawFile.skipBytes(8);
            this.rawFile.read(buffer);
            channelHeaderInformation[i].setCalib(
                ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        }
        return channelHeaderInformation;
    }
    
    /**
     * Reads the channel offset from the .cnt file.
     * @return long cahnnel offset
     * @throws IOException
     */
    private long readChannelOffset() throws IOException{
        this.rawFile.seek(894);
        byte[] val = new byte[4];
        this.rawFile.read(val);
        long result = 0;
        for(int i=0;i<val.length;i++){
            result += ((long)val[i]&0xffL) << (8*i);
        }
        return result;
    }
    
    /**
     * Reads the position of the event table from the .cnt file.
     * @return long position of the event table in bytes
     * @throws IOException 
     */
    private long readEventTablePos() throws IOException{
        byte[] val = new byte[4];
        this.rawFile.seek(886);
        this.rawFile.read(val);
        //convert unsigned value from file to a signed value that can be used
        long result = 0;
        for(int i=0;i<val.length;i++){
            result += ((long)val[i]&0xffL) << (8*i);
        }
        return result;
    }
    
    /**
     * Read the header informations from the .cnt file.
     * @throws IOException
     */
    private void readFileHeader() throws IOException{
        this.fileVersion = this.readFileVersion();
        this.nchannels = this.readNumberOfChannels();
        this.eventTablePos = this.readEventTablePos();
        this.samplingRate = this.readSamplingRate();
        this.channelOffset = this.readChannelOffset();
        //calculate the number of samples
        this.nums = (eventTablePos-(nchannels*75 + 900))/(nchannels*2);
        if(MyConstans.DEBUG){
            System.out.println("File Header:");
            System.out.println("\t File version: " + this.fileVersion);
            System.out.println("\t Number of channels: " + this.nchannels);
            System.out.println("\t Sampling Rate: " + this.samplingRate);
            System.out.println("\t channelOffset: " + this.channelOffset);
            System.out.println("\t Number of samples: " + this.nums);
            System.out.println("\t Channel offset: " + this.channelOffset);
            System.out.println("\t Event table position: " + this.eventTablePos);
        }
    }
    
    /**
     * Reads the file version from the .cnt file
     * @return String file version
     * @throws IOException 
     */
    private String readFileVersion() throws IOException{
        this.rawFile.seek(0);
        byte[] version = new byte[12];
        this.rawFile.read(version);
        return new String(version);
    }
    
    /**
     * Reads the number of channels used in the file from the .cnt  file
     * @return int number of channels
     * @throws IOException 
     */
    private int readNumberOfChannels() throws IOException{
        this.rawFile.seek(370);
        return (int)this.rawFile.read();
    }  
    
    /**
     *Reads the sampling rate from the cnt file.
     * The sampling rate can not be fractional.
     * https://github.com/fieldtrip/fieldtrip/blob/master/external/eeglab/loadcnt.m
     * @return
     * @throws IOException 
     */
    private int readSamplingRate() throws IOException{
        byte[] sampleRate = new byte[2];
        this.rawFile.seek(376);
        this.rawFile.read(sampleRate);
        int result = 0;
        for(int i=0;i<sampleRate.length;i++){
            result += ((int)sampleRate[i]&0xffL) << (8*i);
        }
        return result;
    }   
}
