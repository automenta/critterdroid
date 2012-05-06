/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.simulation;

import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * interface to espeak , text to speech
 * @author me
 */
public class Speak {
    
    final static int num_voices = 4;
    
    public static ExecutorService e = Executors.newFixedThreadPool(num_voices);
    
    public static void speak(final String text) {
        e.submit(new Runnable() {
            @Override public void run() {
                try {
                    Process p = Runtime.getRuntime().exec("festival --tts");
                    PrintStream ps = new PrintStream(p.getOutputStream());
                    ps.append(text + "  \n");
                    ps.flush();
                    ps.close();
                    Thread.sleep(100);
                    p.waitFor();
                    
                } catch (Exception ex) {
                    Logger.getLogger(Speak.class.getName()).log(Level.SEVERE, null, ex);
                }
            }            
        });
    }

    public static void main(String[] args) {
        speak("this is a test message.");
        speak("this is a test message.");
        speak("this is a test message.");
        speak("this is a test message.");
        speak("this is a test message.");
        speak("this is a test message.");
    }
}
