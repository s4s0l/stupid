package com.madisp.stupid.context;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.StupidRuntimeException;
import com.madisp.stupid.StupidTimeExeededException;
import com.madisp.stupid.Value;

/**
 * @author Marcin Wielgus
 */
public class TimeTrackingDereferencer extends Dereferencer {

    private long maxTimeInNanos;
    private int measurementInterval;
    private long startTime = System.nanoTime();
    private int stepsPassed = 0;

    public TimeTrackingDereferencer(long maxTimeInNanos, int measurementInterval) {
        this.maxTimeInNanos = maxTimeInNanos;
        this.measurementInterval = measurementInterval;
    }

    public void resetTimer() {
        startTime = System.nanoTime();
        stepsPassed = 0;
    }

    public long getElapsedTimeInNanos() {
        return System.nanoTime() - startTime;
    }

    @Override
    public <T> T dereference(Value<T> value, ExecContext context) throws StupidRuntimeException {
        if (maxTimeInNanos > 0) {
            stepsPassed = stepsPassed + 1;
            if (stepsPassed % measurementInterval == 0) {
                long elapsedTimeInNanos = getElapsedTimeInNanos();
                if (elapsedTimeInNanos > maxTimeInNanos) {
                    throw new StupidTimeExeededException("Script execution took too long and was terminated.");
                }
            }
        }
        return super.dereference(value, context);
    }
}
