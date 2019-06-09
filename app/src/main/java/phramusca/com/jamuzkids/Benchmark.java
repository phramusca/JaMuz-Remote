package phramusca.com.jamuzkids;

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
    private Collection<Long> partialSizes;

    private Benchmark(int size) {
        this.size=size;
        index=0;
        startTime = System.currentTimeMillis();
        partialTime = startTime;
        partialTimes = new ArrayList<>();
        partialSizes = new ArrayList<>();
    }

    Benchmark(int size, int max) {
        this.size=size;
        index=0;
        startTime = System.currentTimeMillis();
        partialTime = startTime;
        partialTimes = EvictingQueue.create(max);
        partialSizes = EvictingQueue.create(max);
    }

    public String get(long fileSize) {
        long currentTime=System.currentTimeMillis();
        long elapsedTime=currentTime-startTime;
        long actionTime=currentTime-partialTime;
        partialTime=currentTime;
        partialTimes.add(actionTime);
        partialSizes.add(fileSize);
        long elapseSum=sum(partialTimes);
        long remainingTime = mean(partialTimes, elapseSum)*(size-index);
        index++;
        String elapsed = StringManager.humanReadableSeconds(elapsedTime/1000, "+");
        String remaining = StringManager.humanReadableSeconds(remainingTime/1000, "-");
        String speed = StringManager.humanReadableBitCount((sum(partialSizes)*8)/((elapseSum<1?1:elapseSum)/1000), true);
        lastMsg=MessageFormat.format(" | {1}@{2}ps ({0})", elapsed, remaining, speed); //NOI18N
        return lastMsg;
    }

    private String lastMsg="";

    public String getLast() {
        return lastMsg;
    }

    public void setSize(int size) {
        this.size = size;
    }

    private static long mean(Collection<Long> numbers) {
        return sum(numbers)/numbers.size();
    }

    private static long mean(Collection<Long> numbers, long sum) {
        return sum/numbers.size();
    }

    private static long sum(Collection<Long> numbers) {
        long sum=0;
        for(long number : numbers) {
            sum+=number;
        }
        return sum;
    }
}
