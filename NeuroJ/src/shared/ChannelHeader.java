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
    private double sensitivity;
    private double calib;

    public ChannelHeader(){
        super();
    }

    public void setBaseline(short baseline){
        this.baseline = baseline;
    }

    public short getBaseline(){
        return this.baseline;
    }

    public void setSensitivity(double sensitivity){
        this.sensitivity = sensitivity;
    }

    public double getSensitivity(){
        return this.sensitivity;
    }

    public void setCalib(double calib){
        this.calib = calib;
    }

    public double getCalib(){
        return this.calib;
    }
}