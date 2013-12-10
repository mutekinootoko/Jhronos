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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author marku
 */
class JhronosManager extends Thread {

    private Map<String, JhronosJob> jobs;
    private boolean keepRunning;
    private boolean debug;
    private List<Thread> threads;

    JhronosManager() {
        this.jobs = Collections.synchronizedMap(new LinkedHashMap<String, JhronosJob>());
        this.threads = Collections.synchronizedList(new ArrayList<Thread>());
        this.keepRunning = true;
        this.debug = false;
    }

    void setDebug(boolean debug) {
        this.debug = debug;
    }

    void addJob(JhronosJob job) {
        this.jobs.put(job.getJobName(), job);
    }

    JhronosJob removeJob(String jobName) {
        return this.jobs.remove(jobName);
    }

    void stopAtNextCycle() {
        this.keepRunning = false;
    }

    @Override
    public void run() {

        // shorten clock tickes for better accuracy, check for the 'next minute'. 
        int lastRanMinute = -1;
        for (;;) {
            if (!this.keepRunning) {
                break;
            }

            Calendar now = GregorianCalendar.getInstance();
            if (lastRanMinute < 0
                    || lastRanMinute < now.get(Calendar.MINUTE)
                    || (lastRanMinute == 59 && now.get(Calendar.MINUTE) == 0)) {
                lastRanMinute = now.get(Calendar.MINUTE);
                this.debug("the next minute, do some work.");

                for (Entry<String, JhronosJob> entry : this.jobs.entrySet()) {
                    try {
                        this.debug("job:" + entry.getValue().getJobName() + " at " + new Date());
                        if (this.doIRunItNow(entry.getValue())) {
                            this.debug(" executing job the " + (entry.getValue().getExecuteCount() + 1) + " times");
                            //entry.getValue().startJob();
                            entry.getValue().executeCountPlus();
                            Thread t = new Thread(entry.getValue());
                            this.threads.add(t);
                            t.start();
                        } else {
                            this.debug(" no execution at " + new Date());
                        }
                    } catch (JhronosBadExpressionException jbee) {
                        jbee.printStackTrace();
                    }
                }
            } else {
                this.debug("a minute is not yet past.");
            }

            //clearn up finished thread
            Iterator iterator = this.threads.iterator();
            int terminatedThreadRemovedCount = 0;
            this.debug("Anything left in thread pool?");
            while (iterator.hasNext()) {
                Thread t = (Thread) iterator.next();
                if (t.getState() == Thread.State.TERMINATED) {
                    iterator.remove();
                    terminatedThreadRemovedCount++;
                } else {
                    this.debug(t.getState().toString());
                }
            }
            this.debug(terminatedThreadRemovedCount + " terminated thread/s removed.");

            try {
                this.debug("wait for another ~40sec.");
                this.debug("-");
                this.sleep(1000 * 40);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

    }

    private boolean blahblah(JhronosJob.DateType dt, int CalendarTimeUnit, Calendar c, JhronosJob job) {
        switch (job.getExpressionValueType(dt)) {
            case ASTERISK:
                // do nothing
                break;
            case LIST:
                ArrayList<Integer> list = job.getValueArrays(dt);
                if (list.contains(Integer.valueOf(c.get(CalendarTimeUnit))) == false) {
                    return false;
                }
                break;
            case RANGE:
                // sorted range, has only 2 elements
                ArrayList<Integer> range = job.getValueArrays(dt);
                if ((range.get(0) <= c.get(CalendarTimeUnit)
                        && range.get(1) >= c.get(CalendarTimeUnit))
                        == false) {
                    return false;
                }
                break;
            case VALUE:
                if (c.get(CalendarTimeUnit) != job.getValue(dt)) {
                    return false;
                }
                break;
            case INCREMENT:
                if ((c.get(CalendarTimeUnit) == 0
                        || c.get(CalendarTimeUnit) % job.getValue(dt) == 0)
                        == false) {
                    return false;
                }
        }
        return true;
    }

    private boolean doIRunItNow(JhronosJob job) throws JhronosBadExpressionException {
        if (job.getExecuteAtStart() && job.getLasteExecuteTime() == null) {
            this.debug("execute initially.");
            return true;
        }

        Calendar c = GregorianCalendar.getInstance();
        //c.setTimeZone(TimeZone.getTimeZone("Asia/Taipei")); // time zone?

        // YEAR
        if (this.blahblah(JhronosJob.DateType.YEAR, Calendar.YEAR, c, job) == false) {
            return false;
        }
        // MONTH
        if (this.blahblah(JhronosJob.DateType.MONTH, Calendar.MONTH, c, job) == false) {
            return false;
        }
        // DAY
        if (this.blahblah(JhronosJob.DateType.DAY_OF_MONTH, Calendar.DAY_OF_MONTH, c, job) == false) {
            return false;
        }
        // HOUR
        if (this.blahblah(JhronosJob.DateType.HOUR, Calendar.HOUR_OF_DAY, c, job) == false) {
            return false;
        }
        // MINUTE
        if (this.blahblah(JhronosJob.DateType.MINUTE, Calendar.MINUTE, c, job) == false) {
            return false;
        }

        // passed everything, execute!
        return true;
    }

    private void debug(String s) {
        if (this.debug) {
            System.out.println(s);
        }
    }

    public void forceStop() {
        try {
            for (Thread t : this.threads) {
                t.stop();
            }
            this.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
