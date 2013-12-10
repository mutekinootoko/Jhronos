/*
 * The MIT License
 *
 * Copyright 2013 marku.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package tw.marku.Jhronos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 *
 * @author marku
 */
public abstract class JhronosJob implements Runnable {

    protected String jobName;
    protected int executeCount = 0;
    protected Date lastExecuteTime = null;
    protected boolean executeAtStart = false;

    public enum DateType {

        YEAR, MONTH, DAY_OF_MONTH, HOUR, MINUTE
    }

    public enum ExpressionValueType {

        ASTERISK, //"* * * * *" will run the command every minute
        VALUE, //"30 * * * *" will run the command whenever the minute is 30
        LIST, //example, "0 1,2,3 * * *" will run the command between 1am and 3am (inclusive
        RANGE, //"0 1 1-5 * *" will run the command at 1am on the first, second, third, fourth and fifth days of the month. 
        INCREMENT //"*/15 * * * *" will run the command every 15 minutes starting on the hour. i.e. this example is the same as the list example given above to run whenever the minute is 0, 15, 30 or 45. 
    }

    HashMap<DateType, ExpressionValueType> valueTypes;
    HashMap<DateType, Integer> values;
    HashMap<DateType, ArrayList<Integer>> valueArrays; // ArrayList is sorted if used for Range value type.
    
    public JhronosJob() {
        this.valueTypes = new HashMap<DateType, ExpressionValueType>();
        this.values = new HashMap<DateType, Integer>();      
        this.valueArrays = new HashMap<DateType, ArrayList<Integer>>();
    }
    
    public boolean getExecuteAtStart() {
        return this.executeAtStart;
    }
    
    public JhronosJob setExecuteAtStart(boolean executeAtStart) {
        this.executeAtStart = executeAtStart;
        return this;
    }
    
    /**
     * OVERRIDE THIS METHOD, 
     * IF ANY GLOBAL VARIABLES NEEDS TO BE RE-INITIALIZE.
     */
    public void reset() {
        // OVERRIDE ME
    }
    
    /*
    void startJob() {
        this.run();
        this.executeCountPlus();
    }
    */
    
    void executeCountPlus() {
        this.executeCount++;
        this.lastExecuteTime = GregorianCalendar.getInstance().getTime();
    }
    
    public int getExecuteCount() {
        return this.executeCount;
    }

    public Date getLasteExecuteTime() {
        return this.lastExecuteTime;
    }

    public String getJobName() {
        return jobName;
    }

    public JhronosJob setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    

    public JhronosJob set(DateType dt, ExpressionValueType valueType, int value) {
        this.valueTypes.put(dt, valueType);
        this.values.put(dt, value);
        return this;
    }

    public JhronosJob set(DateType dt, ExpressionValueType valueType) {
        this.valueTypes.put(dt, valueType);
        return this;
    }

    public JhronosJob set(DateType dt, ExpressionValueType valueType, int[] values) {
        this.valueTypes.put(dt, valueType);
        switch (valueType) {
            case LIST:
                this.valueArrays.put(dt, this.intArrToIntegerArr(values));
                break;
            case RANGE:
                ArrayList<Integer> bar = this.intArrToIntegerArr(values);
                Collections.sort(bar);
                this.valueArrays.put(dt, bar);
                break;
        }
        return this;
    }

    public ExpressionValueType getExpressionValueType(DateType dt) {
        if(this.valueTypes.containsKey(dt)) {
            return this.valueTypes.get(dt);
        } else {
            // by default
            return ExpressionValueType.ASTERISK;
        }
    }
    
    public ArrayList<Integer> getValueArrays(DateType dt) {
        return this.valueArrays.get(dt);
    }
    
    public Integer getValue(DateType dt) {
        return this.values.get(dt);
    }

    private ArrayList<Integer> intArrToIntegerArr(int[] ia) {
        ArrayList<Integer> foo = new ArrayList<Integer>(ia.length);
        for (int value : ia) {
            foo.add(Integer.valueOf(value));
        }
        return foo;
    }

}
