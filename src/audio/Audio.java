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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import javax.sound.sampled.*;
/**
 *
 * @author seandubiel
 */
public class Audio {
    public static int bufferNum =0;
    public static long SLEEP_PRECISION = TimeUnit.MILLISECONDS.toNanos(1); //2
    public static long SPIN_YIELD_PRECISION = TimeUnit.MILLISECONDS.toNanos(1);//2
    private static Map<Long, Double> timeFreq = new HashMap<Long, Double>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JSONObject obj = new JSONObject();
        
        //readAud("lib/Larry.wav");
        readAud("lib/test2.wav");
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
            File curr = new File(pathName);
            
            WavFile wavFile = WavFile.openWavFile(curr);

            // Display information about the wav file
            wavFile.display();

            // Get the number of audio channels in the wav file
            int numChannels = wavFile.getNumChannels();

            // Create a buffer of 100 frames
            double[] buffer = new double[200 * numChannels];//100
            ArrayList output = new ArrayList();
            int framesRead;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            long outputFrame = 0;

            long startTime = System.nanoTime();
            
            
            do {
                // Read frames into buffer
                framesRead = wavFile.readFrames(buffer, 200); //100

                // Loop through frames and look for minimum and maximum value
                outputFrame++;
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
                double avg = getArrayAvg(buffer);
                //System.out.println(avg);
                output.add(avg);

            } while (framesRead != 0);

            // Close the wavFile
            wavFile.close();


            double time = getTime(pathName);
            double timePerD = (time / outputFrame) * 1000000;
            long timePer = Math.round(timePerD);
            //System.out.println("TIME PER MAIN "+ timePer);
            int x = 0;
            while(x<outputFrame){
                System.out.println("INPUT TIME PER MAIN " +(x+1)*timePer);
                timeFreq.put((x+1)*timePer, (double)output.get(x));
                x++;
            }
            
            
            
            System.out.println("time " + time);
            System.out.println("outputFrames " + outputFrame);
            runTimeFreq(time,outputFrame);
            //runVisual(output, time, outputFrame, curr);

            // Output the minimum and maximum value
            System.out.printf("Min: %f, Max: %f\n", min, max);
            System.out.println("Length of buffer " + buffer.length);
            System.out.println("while loops " + outputFrame);

        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public static void runTimeFreq(double time, long outputFrame) {
        int i = 0;
        
        //System.out.println("Start Time "+startTime);
//        for (Long key : timeFreq.keySet()) {
//            long curr = System.nanoTime()-startTime;
//            //System.out.println("Key "+timeFreq.get(key));
//            //System.out.println("curr Time "+curr);
//            if(key > curr - 100000 &&  key < curr + 100000){
//                System.out.println(timeFreq.get(key));
//            }
//        }
        
        double d = (time / outputFrame);
        long timePerOut = Math.round(d);
        System.out.println("TIME PER OUT "+d);
        long startTime = System.nanoTime();
        long curr;
        while (true) {
            curr = (System.nanoTime() - startTime);
            System.out.println("CURR " + curr);
            if (timeFreq.get(curr) != null) {
                System.out.println(timeFreq.get(curr));
            }
            i++;
        }

        

    }

    public static void runVisual(ArrayList output, double time, long outputFrame, File curr) {
        double d = (time / outputFrame) *1000000000;
        long timePerOut = Math.round(d);
        System.out.println(timePerOut);
        int x = 0;
        long startTime = System.nanoTime();
        play(curr);
        while (x < output.size()) {
            System.out.println(output.get(x));
            x++;
            try {
                //TODO Find good way to have real time output of datapoints
                //Thread.sleep(timePerOut);
                //java.util.concurrent.locks.LockSupport.parkNanos(timePerOut);
                sleepNanos(timePerOut);
            } catch (InterruptedException ex) {
                Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime); 
        System.out.println("Run time " +duration);
        System.out.println("Actual time " +time);
    }

    public static double getArrayAvg(double[] numbers) {
        double sum = 0;

        for (int i = 0; i < numbers.length; i++) {
            sum = sum + numbers[i];
        }

        //calculate average value
        double average = sum / numbers.length;
        return average;

    }
    /**
     * Sleeps thread with nanoseconds however it will sacrifice some of your cpu
     * @param nanoDuration
     * @throws InterruptedException 
     */
    public static void sleepNanos(long nanoDuration) throws InterruptedException {
        final long end = System.nanoTime() + nanoDuration;
        long timeLeft = nanoDuration;
        do {
            if (timeLeft > SLEEP_PRECISION) {
                Thread.sleep(1);
            } else if (timeLeft > SPIN_YIELD_PRECISION) {
                Thread.yield();
            }

            timeLeft = end - System.nanoTime();
        } while (timeLeft > 0);
    }

    public static void play(File file) {
        try {
            final Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));

            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                }
            });

            clip.open(AudioSystem.getAudioInputStream(file));
            clip.start();
        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

}
