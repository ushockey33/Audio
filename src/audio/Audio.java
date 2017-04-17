/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audio;
//import java.io.*;

import java.awt.BorderLayout;
import java.io.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.util.*;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;

/**
 *
 * @author seandubiel
 */
public class Audio {
    public static int bufferNum =0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        readAud("lib/test.wav");
        //readAud("lib/test2.wav");
        //readAud("lib/test2.wav");
        Number[] array = new Number[10];
        array[0] = 1;
        array[1] = 2;
        array[2] = 3;

        //test swingWorkerRealTime = new test();
        //swingWorkerRealTime.go();

    }

    public static double getTime(String pathName) {
        double durationInSeconds= 0;
        try{
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(pathName));
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        durationInSeconds = (frames + 0.0) / format.getFrameRate();
        }catch(Exception ex){
            System.out.println("Could not get length of file");
        }
        return durationInSeconds;
    }
    public static void readAud(String pathName) {
        bufferNum = 0;
        try {
            // Open the wav file specified as the first argument
            WavFile wavFile = WavFile.openWavFile(new File(pathName));

            // Display information about the wav file
            wavFile.display();

            // Get the number of audio channels in the wav file
            int numChannels = wavFile.getNumChannels();

            // Create a buffer of 100 frames
            double[] buffer = new double[100 * numChannels];

            int framesRead;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            do {
                // Read frames into buffer
                framesRead = wavFile.readFrames(buffer, 100);

                // Loop through frames and look for minimum and maximum value
                
                for (int s = 0; s < framesRead * numChannels; s++) {
                    if (buffer[s] > max) {
                        max = buffer[s];
                    }
                    if (buffer[s] < min) {
                        min = buffer[s];
                    }
                    bufferNum++;
                    //System.out.println(buffer[s]);
                }
            } while (framesRead != 0);

            // Close the wavFile
            wavFile.close();

            // Output the minimum and maximum value
            System.out.printf("Min: %f, Max: %f\n", min, max);
            double time = getTime(pathName);
            System.out.println("Time in seconds "+ time);
            System.out.println("Number of buffers "+ bufferNum);
        } catch (Exception e) {
            System.err.println(e);
        }

    }

}
