package phramusca.com.jamuzremote;

import com.google.common.collect.EvictingQueue;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * Created by raph on 01/05/17.
 */
public class Benchmark {

    private final int size;
    private int index;
    private final long startTime;
    private long partialTime;
    private final Collection<Long> partialTimes;
    private final Collection<Long> partialSizes;

    Benchmark(int size, int max) {
        this.size=size;
        index=0;
        startTime = System.currentTimeMillis();
        partialTime = startTime;
        partialTimes = EvictingQueue.create(max);
        partialSizes = EvictingQueue.create(max);
    }

    public synchronized String get(long fileSize) {
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

    public synchronized String getLast() {
        return lastMsg;
    }

    private synchronized static long mean(Collection<Long> numbers, long sum) {
        return sum/numbers.size();
    }

    private synchronized static long sum(Collection<Long> numbers) {
        long sum=0;
        for(long number : numbers) {
            sum+=number;
        }
        return sum;
    }
}
