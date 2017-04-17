/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package audio;

/**
 *
 * @author seandubiel
 */
import processing.core.PApplet;
public class Processing extends PApplet{
    public static void main(String[] args){
        PApplet.main("audio.Processing");
        
    }
    public void settings(){
        
        background(153);

        sketchHeight();
    }
    public void setup(){
       //size(150, 200, P3D);  // Specify P3D renderer
        //fill(120,50,240);
     }
    public void draw(){
        ellipse(width/2,height/2,second(),second());
        ellipse(width/2,height/2,second(),second());
    }
}