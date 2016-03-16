/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuroJExceptions;

/**
 *
 * @author matthias
 */
public class BadChannelException extends Exception{
    public BadChannelException(){}
    
    public BadChannelException(String msg){
        super(msg);
    }
}
