package phramusca.com.jamuzremote;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raph on 01/05/17.
 */
public class Benchmark {

    private int size;
    private int index;
    private final long startTime;
    private long partialTime;
    private final List<Long> partialTimes;

    public Benchmark(int size) {
        this.size=size;
        this.index=0;
        this.partialTimes = new ArrayList<>();
        this.startTime = System.currentTimeMillis();
        this.partialTime = this.startTime;
    }

    public String get() {
        long currentTime=System.currentTimeMillis();
        long elapsedTime=currentTime-this.startTime;
        long actionTime=currentTime-this.partialTime;
        this.partialTime=currentTime;
        this.partialTimes.add(actionTime);
        long remainingTime = mean(this.partialTimes)*(this.size-this.index);
        this.index++;

        String elapsed = StringManager.humanReadableSeconds(elapsedTime/1000);
        String remaining = StringManager.humanReadableSeconds(remainingTime/1000);

        lastMsg=MessageFormat.format("{0} | {1}", elapsed, remaining); //NOI18N
        return lastMsg;
    }

    private String lastMsg="";

    public String getLast() {
        return lastMsg;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static long mean(List<Long> numbers) {
        return sum(numbers)/numbers.size();
    }

    public static long sum(List<Long> numbers) {
        long sum=0;
        for(long number : numbers) {
            sum+=number;
        }
        return sum;
    }
}
