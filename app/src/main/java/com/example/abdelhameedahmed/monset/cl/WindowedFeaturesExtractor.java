/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.abdelhameedahmed.monset.cl;


/**
 * Base class for windowed features extractor
 * <p>
 * Constructor computes default window size by calling {@link #getWindowSize(float)}. <br/>
 * @see {@link #getWindowSize(float)}
 * </p>
 * @author Amaury Crickx
 * @param <T> the kind of features to extract, specified by implementing classes
 */
public abstract class WindowedFeaturesExtractor<T> 
        implements FeaturesExtractor<T> {
    
    private static final int DEFAULT_TARGET_WINDOW_LENGTH_IN_MILLIS = 24;
    private static final float MIN_SAMPLE_RATE = 8000.0F;

    protected final int windowSize;
    protected final float sampleRate;

    /**
     * Base constructor required by this abstract class
     * @param sampleRate the sample rate of the voice samples, minimum 8000.0
     */
    public WindowedFeaturesExtractor(float sampleRate) {
        if(sampleRate < MIN_SAMPLE_RATE) {
            throw new IllegalArgumentException("Sample rate should be at least 8000 Hz");
        }
        this.sampleRate = sampleRate;
        this.windowSize = getWindowSize(sampleRate);
    }

    /* (non-Javadoc)
     * @see com.recognito.processing.features.FeaturesExtractor#extractFeatures(double[])
     */
    public abstract T extractFeatures(double[] voiceSample);

    /**
     * Called by the constructor of this class.
     * This implementation delegates to {@link #getClosestPowerOfTwoWindowSize(float, int)}
     * with default targetSizeInMillis value
     * <p>
     * Implementing classes may wish to override this method by delegating with another target value in millis
     * or implement another logic alltogether
     * </p>
     * @param sampleRate the sample rate in Hz (times per second), minimum 8000.0
     * @return the window size
     */
    protected int getWindowSize(float sampleRate) {
        return getClosestPowerOfTwoWindowSize(sampleRate, DEFAULT_TARGET_WINDOW_LENGTH_IN_MILLIS);
    }

    /**
     * Computes the window size that is both the closest to targetSizeInMillis and a power of 2
     * <p>
     * Note : window size using a power of 2 is required e.g. when using discrete FFT algorithm
     * </p>
     * @param sampleRate the sample rate in Hz (times per second), minimum 8000.0
     * @param targetSizeInMillis the target size in millis
     * @return the window size
     */
    protected final int getClosestPowerOfTwoWindowSize(float sampleRate, int targetSizeInMillis) {
        boolean done = false;
        int pow = 8; // 8 bytes == 1ms at 8000 Hz
        float previousMillis = 0.0f;
        
        while(!done) {
            float millis = 1000 / sampleRate * pow;
            if(millis < targetSizeInMillis) {
                previousMillis = millis;
                pow *= 2;
            } else {
                // closest value to target wins
                if(Math.abs(targetSizeInMillis - millis) > targetSizeInMillis - previousMillis) {
                    pow /= 2; // previousMillis was closer
                }
                done = true;
            }
        }
        return pow;
    }
    
}