package task3;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicLong;

public class JavaDU2 {

    public static class CountingTask extends RecursiveAction {
        private final Path dir;
        private final AtomicLong filesCount;
        private final AtomicLong directoryCount;
        private final AtomicLong totalSize;

        public CountingTask(Path dir, AtomicLong filesCount, AtomicLong directoryCount, AtomicLong totalSize) {
            this.dir = dir;
            this.directoryCount = directoryCount;
            this.filesCount = filesCount;
            this.totalSize = totalSize;
        }

        @Override
        protected void compute() {
            List<CountingTask> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
                for (Path subPath : ds) {
                    if (Files.isDirectory(subPath, LinkOption.NOFOLLOW_LINKS)) {
                        subTasks.add(new CountingTask(subPath, filesCount, directoryCount, totalSize));
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
        AtomicLong filesCount = new AtomicLong();
        AtomicLong directoryCount = new AtomicLong();
        AtomicLong totalSize = new AtomicLong();
        ForkJoinPool.commonPool().invoke(new CountingTask(Paths.get("/Users/mvv/Games"), filesCount, directoryCount, totalSize));

        System.out.println(totalSize + "\ttotalSize");
        System.out.println(filesCount + "\tfilesCount");
        System.out.println(directoryCount + "\tdirectoryCount");
    }

}

