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

package tw.marku.Jhronos.Exmaples;

import tw.marku.Jhronos.Jhronos;
import tw.marku.Jhronos.JhronosJob;

/**
 *
 * @author marku
 */
public class MainExmaple {
    
    public static void main(String[] args) {
        
        // job 1, this is equivalent to crontab expression "30 20,21,22,23 * * *".
        // execute at time - 20:30, 21:30, 22:30, 23:30
        int[] hourList= {20, 21, 22, 23};
        Jhronos.addJob(new JhronosJobExample()
                .setJobName("job1")
                .set(JhronosJob.DateType.MINUTE, 
                        JhronosJob.ExpressionValueType.VALUE,
                        30)
                .set(JhronosJob.DateType.HOUR, 
                        JhronosJob.ExpressionValueType.LIST,
                        hourList)
                .setExecuteAtStart(true)); // execute at start, regardless of time scheduled, default is false.
        
        // job 2, equivalent to " */7 * * * *".
        // execute at minute 0, 7, 14, 21... of every hour every day.
        JhronosJobExample2 example2 = new JhronosJobExample2();
        example2.setMessageToPrint(" executing exmaple 2 ");
        example2.set(JhronosJob.DateType.MINUTE, 
                        JhronosJob.ExpressionValueType.INCREMENT,                        
                        7)
                .setJobName("job2");
        Jhronos.addJob(example2);
        
        // start Jhronos with debug messages.
        Jhronos.execute(Jhronos.DEBUG);
    }
    
}
