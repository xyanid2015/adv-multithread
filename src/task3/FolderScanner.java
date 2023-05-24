package task3;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Task 3 - File Scanner via FJP
 * <p>
 * Cost: 0.5 points.
 * <p>
 * Create CLI application that scans a specified folder and provides detailed statistics:
 * <p>
 * 1. File count.
 * 2. Folder count.
 * 3. Size (sum of all files size) (similar like Windows context menu Properties). Since the folder may contain huge number of files the scanning process should be executed in a separate thread displaying an informational message with some simple animation like progress bar in CLI (up to you, but I'd like to see that task is in progress).
 * <p>
 * Once task is done, the statistics should be displayed in the output immediately. Additionally, there should be ability to interrupt the process pressing some reserved key (for instance c). Of course, use Fork-Join Framework for implementation parallel scanning.
 */
public class FolderScanner {

    public static class CountingTask extends RecursiveAction {
        private final Path dir;
        private final AtomicLong filesCount;
        private final AtomicLong directoryCount;
        private final AtomicLong totalSize;
        private final AtomicBoolean interrupted;

        public CountingTask(Path dir, AtomicLong filesCount, AtomicLong directoryCount, AtomicLong totalSize, AtomicBoolean interrupted) {
            this.dir = dir;
            this.directoryCount = directoryCount;
            this.filesCount = filesCount;
            this.totalSize = totalSize;
            this.interrupted = interrupted;
        }

        @Override
        protected void compute() {
            if (interrupted.get()) {
                return;
            }
            List<CountingTask> subTasks = new ArrayList<>();
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
                for (Path subPath : ds) {
                    if (Files.isDirectory(subPath, LinkOption.NOFOLLOW_LINKS)) {
                        subTasks.add(new CountingTask(subPath, filesCount, directoryCount, totalSize, interrupted));
                        directoryCount.incrementAndGet();
                    } else {
                        totalSize.addAndGet(Files.size(subPath));
                        filesCount.incrementAndGet();
                    }
                }
                if (!subTasks.isEmpty()) {
                    invokeAll(subTasks);
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static void main(String[] args) {
        final Character interruptFlag = 'c';
        Scanner scanner = new Scanner(System.in);
        System.out.print("To stop execution, press 'c'. Enter the folder path: ");
        String folderPath = scanner.nextLine();

        AtomicLong filesCount = new AtomicLong();
        AtomicLong directoryCount = new AtomicLong();
        AtomicLong totalSize = new AtomicLong();
        AtomicBoolean interrupted = new AtomicBoolean(false);

        Thread interruptThread = new Thread(() -> {
            while (!interrupted.get()) {
                char c = scanner.next().charAt(0);
                if (interruptFlag.equals(c)) {
                    System.err.println("Exit program");
                    interrupted.set(true);
                }
            }
        });
        interruptThread.setDaemon(true);
        interruptThread.start();

        Thread progressThread = new Thread(new ProgressThread());
        progressThread.setDaemon(true);
        progressThread.start();

        ForkJoinPool.commonPool().invoke(new CountingTask(Paths.get(folderPath), filesCount, directoryCount, totalSize, interrupted));

        System.out.println(totalSize + "\ttotalSize");
        System.out.println(filesCount + "\tfilesCount");
        System.out.println(directoryCount + "\tdirectoryCount");
    }
}

