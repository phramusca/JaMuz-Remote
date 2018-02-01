package phramusca.com.jamuzremote;

import com.google.common.collect.EvictingQueue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by raph on 01/05/17.
 */
public class Benchmark {

    private int size;
    private int index;
    private final long startTime;
    private long partialTime;
    private Collection<Long> partialTimes;

    public Benchmark(int size) {
        this.size=size;
        index=0;
        partialTimes = new ArrayList<>();
        startTime = System.currentTimeMillis();
        partialTime = startTime;
    }

    public Benchmark(int size, int max) {
        this.size=size;
        index=0;
        partialTimes = EvictingQueue.create(max);
        startTime = System.currentTimeMillis();
        partialTime = startTime;
    }

    public String get() {
        long currentTime=System.currentTimeMillis();
        long elapsedTime=currentTime-startTime;
        long actionTime=currentTime-partialTime;
        partialTime=currentTime;
        partialTimes.add(actionTime);
        long remainingTime = mean(partialTimes)*(size-index);
        index++;

        String elapsed = StringManager.humanReadableSeconds(elapsedTime/1000, "+");
        String remaining = StringManager.humanReadableSeconds(remainingTime/1000, "-");

        lastMsg=MessageFormat.format("| {0}/{1} |", elapsed, remaining); //NOI18N
        return lastMsg;
    }

    private String lastMsg="";

    public String getLast() {
        return lastMsg;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static long mean(Collection<Long> numbers) {
        return sum(numbers)/numbers.size();
    }

    public static long sum(Collection<Long> numbers) {
        long sum=0;
        for(long number : numbers) {
            sum+=number;
        }
        return sum;
    }
}
