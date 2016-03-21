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
package shared;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This class is a data structure, that holds all the header information
 * for a channel.
 * @author matthias
 */
public class ChannelHeader{
    private short baseline;
    private double calib;
    private double sensitivity;
    
    /**
     * Default constructor for ChannelHeader
     */
    public ChannelHeader(){
        super();
    }
    
    /**
     * Returns the baseline value for this channel.
     * @return baseline value for this channel
     */
    public short getBaseline(){
        return this.baseline;
    }
    /**
     * Sets the new baseline value for this channel.
     * @param baseline new baseline value
     */
    public void setBaseline(short baseline){
        this.baseline = baseline;
    }
    
    /**
     * Returns the calibration value for this cahnnel.
     * @return calibration value for this channel
     */
    public double getCalib(){
        return this.calib;
    }

    /**
     * Sets a new calibration value for this channel.
     * @param calib new calibration value
     */
    public void setCalib(double calib){
        this.calib = calib;
    }
    
    /**
     * Returns the sensitivity value for this channel.
     * @return sensitivity value
     */
    public double getSensitivity(){
        return this.sensitivity;
    }
    
    /**
     * Sets a new sensitivity value for this channel.
     * @param sensitivity new sensitivity value
     */
    public void setSensitivity(double sensitivity){
        this.sensitivity = sensitivity;
    }
}