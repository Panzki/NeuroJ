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