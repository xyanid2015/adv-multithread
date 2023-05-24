package task3;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FolderScanner extends RecursiveAction {
    private final File folder;
    private final AtomicInteger fileCount;
    private final AtomicInteger folderCount;
    private final AtomicLong totalSize;

    public FolderScanner(File folder, AtomicInteger fileCount, AtomicInteger folderCount, AtomicLong totalSize) {
        this.folder = folder;
        this.fileCount = fileCount;
        this.folderCount = folderCount;
        this.totalSize = totalSize;
    }

    @Override
    protected void compute() {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (Thread.currentThread().isInterrupted()) {
                    return; // Exit if interrupted
                }
                if (file.isFile()) {
                    fileCount.incrementAndGet();
                    totalSize.addAndGet(file.length());
                } else if (file.isDirectory()) {
                    folderCount.incrementAndGet();
                    FolderScanner subTask = new FolderScanner(file, fileCount, folderCount, totalSize);
                    subTask.fork();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the folder path: ");
        String folderPath = scanner.nextLine();
        File folder = new File(folderPath);

        AtomicInteger fileCount = new AtomicInteger(0);
        AtomicInteger folderCount = new AtomicInteger(0);
        AtomicLong totalSize = new AtomicLong(0);

        FolderScanner folderScanner = new FolderScanner(folder, fileCount, folderCount, totalSize);
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.execute(folderScanner);

        // Wait for scanning to complete
        folderScanner.join();

        // Scanning is complete, display the statistics
        System.out.println("\n\nScanning complete!");
        System.out.println("Files: " + fileCount.get());
        System.out.println("Folders: " + folderCount.get());
        System.out.println("Total Size: " + totalSize.get() + " bytes");

        // Shutdown the common pool
        commonPool.shutdown();
    }
}
