package task3;

public class ProgressThread implements Runnable {
    private static final String[] ANIMATION_FRAMES = {"|", "/", "-", "\\"};

    @Override
    public void run() {
        int frameIndex = 0;
        while (true) {
            System.out.print("Progress: " + ANIMATION_FRAMES[frameIndex] + "\r");
            frameIndex = (frameIndex + 1) % ANIMATION_FRAMES.length;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
