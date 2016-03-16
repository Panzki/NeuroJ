package shared;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matthias
 */
public class ChannelHeader{
    private short baseline;
    private double calib;
    private double sensitivity;

    public ChannelHeader(){
        super();
    }


    public short getBaseline(){
        return this.baseline;
    }
    public void setBaseline(short baseline){
        this.baseline = baseline;
    }
    public double getCalib(){
        return this.calib;
    }

    public void setCalib(double calib){
        this.calib = calib;
    }
    public double getSensitivity(){
        return this.sensitivity;
    }
    public void setSensitivity(double sensitivity){
        this.sensitivity = sensitivity;
    }
}