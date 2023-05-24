//package task3;
//
//import java.io.IOException;
//import java.nio.file.DirectoryStream;
//import java.nio.file.Files;
//import java.nio.file.LinkOption;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ForkJoinPool;
//import java.util.concurrent.RecursiveTask;
//import java.util.concurrent.atomic.AtomicLong;
//
//public class JavaDU {
//
//    public static class CountingTask extends RecursiveTask<Long> {
//        private Path dir;
//        private final AtomicLong filesCount;
//        private final AtomicLong directoryCount;
//        private final AtomicLong totalSize;
//
//        public CountingTask(Path dir, AtomicLong filesCount, AtomicLong directoryCount, AtomicLong totalSize) {
//            this.dir = dir;
//            this.directoryCount = directoryCount;
//            this.filesCount = filesCount;
//            this.totalSize = totalSize;
//        }
//
//        @Override
//        protected Long compute() {
//            long size = 0;
//            List<CountingTask> subTasks = new ArrayList<>();
//
//            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
//                for (Path subPath : ds) {
//                    if (Files.isDirectory(subPath, LinkOption.NOFOLLOW_LINKS)) {
//                        subTasks.add(new CountingTask(subPath, filesCount, directoryCount, totalSize));
//                        directoryCount.incrementAndGet();
//                    } else {
//                        totalSize.addAndGet(Files.size(subPath));
//                        filesCount.incrementAndGet();
//                    }
//                }
//
//                if (!subTasks.isEmpty()) {
//                    invokeAll(subTasks);
////                    for (CountingTask subTask : invokeAll(subTasks)) {
////                        long s = subTask.join();
////                        size += s;
////                    }
//                }
//
//            } catch (IOException ex) {
//                System.out.println("error: " + ex.getMessage());
//                return 0L;
//            }
//            return size;
//        }
//    }
//
//    public static void main(String[] args) {
//        AtomicLong filesCount = new AtomicLong();
//        AtomicLong directoryCount = new AtomicLong();
//        AtomicLong totalSize = new AtomicLong();
//        Long size = ForkJoinPool.commonPool().invoke(new CountingTask(Paths.get("/Users/mvv"), filesCount, directoryCount, totalSize));
//
//        System.out.println(totalSize + "\ttotalSize");
//        System.out.println(filesCount + "\tfilesCount");
//        System.out.println(directoryCount + "\tdirectoryCount");
//    }
//
//}
