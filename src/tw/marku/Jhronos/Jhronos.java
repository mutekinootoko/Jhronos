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

/**
 *
 * @author marku
 */
public class Jhronos {
    
    public static int NO_DEBUG=0;
    public static int DEBUG=1;
    
    private static JhronosManager manager;
    static{
        if(Jhronos.manager == null) {
            Jhronos.manager = new JhronosManager();
        }
    }
    
    public static void addJob(JhronosJob job) {
        Jhronos.manager.addJob(job);
    }
    
    public static JhronosJob removeJob(String jobName) {
        return Jhronos.manager.removeJob(jobName);
    }
    
    public static void execute(int arg) {
        if(arg == DEBUG) {
            Jhronos.manager.setDebug(true);
        }
        Jhronos.manager.start();
    }
    
    public static void execute() {
        Jhronos.execute(NO_DEBUG);
    }
    
    public static void saveStop() {
        Jhronos.manager.stopAtNextCycle();
    }
    
    public static void forceStop() {
        Jhronos.manager.forceStop();
    }
    
    
    
    
}
