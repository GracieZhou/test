package scifly.datacache;

import java.io.File;

import android.os.Environment;

public class DataCacheConfiguration {
    public static final int DEFAULT_DISK_CACHE_POLICY = DataCacheManager.DISK_CACHE_POLICY_LRU;
    public static final int DEFAULT_MAX_DISK_CACHE_SIZE = 100 * 1024 * 1024;// 100M
    public static final int DEFAULT_DISK_CACHE_SIZE = 5 * 1024 * 1024;// 5M
    public static final File DEFAULT_DISK_CACHE_DIR =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    public static final int DEFAULT_MEMORY_CACHE_POLICY = DataCacheManager.MEMORY_CACHE_POLICY_LFU;
    public static final int DEFAULT_MAX_MEMORY_CACHE_SIZE = 16 * 1024 * 1024;// 16M
    public static final int DEFAULT_MEMORY_CACHE_SIZE = 1 * 1024 * 1024;// 1M
    public static final int DEFAULT_THREAD_POOL_SIZE = 5;
    public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY;
    public static final int DEFAULT_EXPIRE_AGE = 60;

    final int diskCachePolicy;
    final int maxDiskCacheSize;
    final int diskCacheSize;
    final File diskCacheDir;

    final int memoryCachePolicy;
    final int maxMemoryCacheSize;
    final int memoryCacheSize;

    final int expireAge;
    final int threadPoolSize;
    final int threadPriority;

    public DataCacheConfiguration(Builder builder) {
        diskCachePolicy = builder.diskCachePolicy;
        maxDiskCacheSize = builder.maxDiskCacheSize;
        diskCacheSize = builder.diskCacheSize;
        diskCacheDir = builder.diskCacheDir;

        memoryCachePolicy = builder.memoryCachePolicy;
        maxMemoryCacheSize = builder.maxMemoryCacheSize;
        memoryCacheSize = builder.memoryCacheSize;

        expireAge = builder.expireAge;
        threadPoolSize = builder.threadPoolSize;
        threadPriority = builder.threadPriority;
    }

    public static class Builder {
        private int diskCachePolicy = -1;
        private int maxDiskCacheSize = -1;
        private int diskCacheSize = -1;
        private File diskCacheDir;

        private int memoryCachePolicy = -1;
        private int maxMemoryCacheSize = -1;
        private int memoryCacheSize = -1;

        private int expireAge = -1;
        private int threadPoolSize = -1;
        private int threadPriority = -1;

        private void initEmptyFieldsWithDefaultValues() {
            if (diskCachePolicy < 0) {
                diskCachePolicy = DEFAULT_DISK_CACHE_POLICY;
            }
            if (maxDiskCacheSize < 0) {
                maxDiskCacheSize = DEFAULT_MAX_DISK_CACHE_SIZE;
            }
            if (diskCacheSize < 0) {
                diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
            }
            if (diskCacheDir == null || !diskCacheDir.exists() || !diskCacheDir.isDirectory()) {
                diskCacheDir = DEFAULT_DISK_CACHE_DIR;
            }
            if (memoryCachePolicy < 0) {
                memoryCachePolicy = DEFAULT_MEMORY_CACHE_POLICY;
            }
            if (maxMemoryCacheSize < 0) {
                maxMemoryCacheSize = DEFAULT_MAX_MEMORY_CACHE_SIZE;
            }
            if (memoryCacheSize < 0) {
                memoryCacheSize = DEFAULT_MEMORY_CACHE_SIZE;
            }
            if (expireAge < 0) {
                expireAge = DEFAULT_EXPIRE_AGE;
            }
            if (threadPoolSize < 0) {
                threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
            }
            if (threadPriority < 0) {
                threadPriority = DEFAULT_THREAD_PRIORITY;
            }
        }

        public Builder diskCachePolicy(int diskCachePolicy) {
            if (diskCachePolicy < DataCacheManager.DISK_CACHE_POLICY_UNLIMITED
                    || diskCachePolicy > DataCacheManager.DISK_CACHE_POLICY_LRU) {
                throw new IllegalArgumentException("diskCachePolicy must be between 0 and 2");
            }

            this.diskCachePolicy = diskCachePolicy;
            return this;
        }

        public Builder maxDiskCacheSize(int maxDiskCacheSize) {
            if (maxDiskCacheSize < 0) {
                throw new IllegalArgumentException("maxDiskCacheSize must be a positive number");
            }

            this.maxDiskCacheSize = maxDiskCacheSize * 1024 * 1024;
            return this;
        }

        public Builder diskCacheSize(int diskCacheSize) {
            if (diskCacheSize <= 0) {
                throw new IllegalArgumentException("diskCacheSize must be a positive number");
            }

            this.diskCacheSize = diskCacheSize * 1024 * 1024;
            return this;
        }

        public Builder diskCacheDir(File diskCacheDir) {
            if (!((diskCacheDir.exists() && diskCacheDir.isDirectory()) || diskCacheDir.mkdir())) {
                throw new IllegalArgumentException("diskCacheDir is not existed");
            }

            this.diskCacheDir = diskCacheDir;
            return this;
        }

        public Builder memoryCachePolicy(int memoryCachePolicy) {
            if (memoryCachePolicy < DataCacheManager.MEMORY_CACHE_POLICY_FIFO
                    || memoryCachePolicy > DataCacheManager.MEMORY_CACHE_POLICY_WEAK) {
                throw new IllegalArgumentException("memoryCachePolicy must be between 3 and 9");
            }

            this.memoryCachePolicy = memoryCachePolicy;
            return this;
        }

        public Builder maxMemoryCacheSize(int maxMemoryCacheSize) {
            if (maxMemoryCacheSize < 0) {
                throw new IllegalArgumentException("maxMemoryCacheSize must be a positive number");
            }

            this.maxMemoryCacheSize = maxMemoryCacheSize * 1024 * 1024;
            return this;
        }

        public Builder memoryCacheSize(int memoryCacheSize) {
            if (memoryCacheSize <= 0) {
                throw new IllegalArgumentException("memoryCacheSize must be a positive number");
            }

            this.memoryCacheSize = memoryCacheSize * 1024 * 1024;
            return this;
        }

        public Builder expireAge(int expireAge) {
            this.expireAge = expireAge;
            return this;
        }

        public Builder threadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder threadPriority(int threadPriority) {
            if (threadPriority < Thread.MIN_PRIORITY) {
                this.threadPriority = Thread.MIN_PRIORITY;
            } else {
                if (threadPriority > Thread.MAX_PRIORITY) {
                    this.threadPriority = Thread.MAX_PRIORITY;
                } else {
                    this.threadPriority = threadPriority;
                }
            }
            return this;
        }

        public DataCacheConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new DataCacheConfiguration(this);
        }
    }
}
