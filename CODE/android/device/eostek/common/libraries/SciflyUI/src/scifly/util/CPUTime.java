
package scifly.util;

public class CPUTime {
    private long totalTime;

    private long idleTime;

    public CPUTime() {
        totalTime = 0;
        idleTime = 0;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }
}
