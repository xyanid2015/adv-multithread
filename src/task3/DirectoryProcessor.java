package task3;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class DirectoryProcessor extends RecursiveTask<DirectoryStats> {

    private final File directory;

    public DirectoryProcessor(File directory) {
        this.directory = directory;
    }

    @Override
    protected DirectoryStats compute() {
        long size = 0;
        long filesCount = 0;
        long directoriesCount = 0;
        List<DirectoryProcessor> subTasks = new LinkedList<>();

        if (directory.isFile()) {
            return new DirectoryStats(0, 1, directory.length());
        } else {
            File[] files = directory.listFiles();
            if (files != null) {
                directoriesCount++;
                for (File file : files) {
                    DirectoryProcessor subTask = new DirectoryProcessor(file);
                    subTask.fork();
                    subTasks.add(subTask);
                }

                for (DirectoryProcessor subTask : subTasks) {
                    DirectoryStats subTaskResult = subTask.join();

                    size += subTaskResult.getSize();
                    filesCount += subTaskResult.getFileCount();
                    directoriesCount += subTaskResult.getDirectoryCount();
                }
            }
            return new DirectoryStats(directoriesCount, filesCount, size);
        }
    }

    public static void main(String[] args) {
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        DirectoryProcessor task = new DirectoryProcessor(new File("/Users/mvv"));
        DirectoryStats result = commonPool.invoke(task);

        System.out.println("Number of directories: " + result.getDirectoryCount());
        System.out.println("Number of files: " + result.getFileCount());
        System.out.println("Total size: " + result.getSize() + " bytes");
    }
}

class DirectoryStats {
    private final long directoryCount;
    private final long fileCount;
    private final long size;

    public DirectoryStats(long directoryCount, long fileCount, long size) {
        this.directoryCount = directoryCount;
        this.fileCount = fileCount;
        this.size = size;
    }

    public long getDirectoryCount() {
        return directoryCount;
    }

    public long getFileCount() {
        return fileCount;
    }

    public long getSize() {
        return size;
    }
}
