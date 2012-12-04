package catchup;

import java.util.concurrent.CountDownLatch;

import static java.lang.System.out;

public final class LongCatchup
{
    private static final long ITERATIONS = 100L * 1000L * 1000L;

    private static long pingValue = -1L;
    private static long pongValue = -1L;

    private static final CountDownLatch latch = new CountDownLatch(2);

    public static void main(final String[] args)
        throws Exception
    {
        Thread pongThread = new Thread(new PongRunner());
        Thread pingThread = new Thread(new PingRunner());
        pongThread.start();
        pingThread.start();

        latch.await();

        long start = System.nanoTime();

        pongThread.join();

        long duration = System.nanoTime() - start;

		out.printf("duration %,d (ns);", duration);
		out.printf("%,d ns/op;", duration / ITERATIONS);
		out.printf("%,d ops/s;", (ITERATIONS * 1000000000L) / duration);
		out.println("pingValue = " + pingValue + ", pongValue = " + pongValue);
    }

    public static final class PingRunner implements Runnable
    {
        public void run()
        {
            latch.countDown();
            for(long l=0;l<ITERATIONS;l++){
            	pingValue = l;
            }
        }
    }

    public static final class PongRunner implements Runnable
    {
        public void run()
        {
            latch.countDown();
			long executions = 0;
            while(pongValue < ITERATIONS-1){
				executions++;
            	long l = pingValue;
            	while(pongValue < l)
            		pongValue++;
            }
            out.printf("executions %,d;\t", executions);
        }
    }
}
