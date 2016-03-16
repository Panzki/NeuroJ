/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cntFile;

import shared.MyConstans;
import shared.ChannelHeader;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 *
 * @author matthias
 */
public class cntFile {
    //todo: rename vars
    private RandomAccessFile rawFile;
    private String fileVersion;
    private int nchannels;
    private long eventTablePos;
    private int samplingRate;
    private long channelOffset;
    private long nums;
    private ChannelHeader[] channelHeaders;
    
    public cntFile(String path){
        try{
            rawFile = new RandomAccessFile(path, "r");
            this.readFileHeader();
            this.channelHeaders = this.readChannelHeader();
        } catch(IOException ex){
            System.out.println(ex);
        }
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
     * Reads all data for all channels from the .cnt file.
     * @param data[number of channels][number of samples]
     * @throws IOException 
     */
    public void readData(double data[][]) throws IOException,
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
            for(int currentChannel=0;currentChannel<nchannels;currentChannel++){
                for(int currentOffset=0;currentOffset<nums;currentOffset++){
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
    
    public long getNumberOfSamples(){
        return this.nums;
    }
    
    public int getNumberOfChannels(){
        return this.nchannels;
    }
    
    public int getSamplingRate(){
        return this.samplingRate;
    }
}
