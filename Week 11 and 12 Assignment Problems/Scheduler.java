import java.util.*;
//import java.util.stream.Collectors;

/**
 * Single-thread fair scheduler with deadlines & aging (logic-only).
 *
 * Usage: modify main() or call Scheduler.schedule(...) from your code.
 */
public class Scheduler {

    // ---------- Output classes ----------
    public static class Slice {
        public final int id;
        public final long start, end;
        public Slice(int id, long start, long end) { this.id = id; this.start = start; this.end = end; }
        @Override public String toString() { return String.format("Slice(id=%d, start=%d, end=%d)", id, start, end); }
    }

    public static class SchedResult {
        public final List<Slice> slices;
        // metrics per job (index maps to original job order): [CT, TAT, WT, Lateness]
        public final long[][] metrics;
        public final long maxLateness;
        public final double avgWT;
        public final long fairness; // maxWT - minWT

        public SchedResult(List<Slice> slices, long[][] metrics, long maxLateness, double avgWT, long fairness) {
            this.slices = slices;
            this.metrics = metrics;
            this.maxLateness = maxLateness;
            this.avgWT = avgWT;
            this.fairness = fairness;
        }
    }

    // ---------- Internal Job + queue node ----------
    private static class Job {
        final int id;           // user id (not index)
        final long arrival;
        long remaining;
        final int basePri;      // 1..K
        int effPri;             // current effective priority (1..K)
        final long deadline;

        // queue node reference for O(1) removal
        Deque.Node node;

        // used for aging
        long nextAgingTime;     // next time when effPri should be bumped (only valid when waiting in queue)
        boolean completed = false;

        // metrics
        long completionTime = -1;

        Job(int id, long arrival, long duration, int basePri, long deadline) {
            this.id = id;
            this.arrival = arrival;
            this.remaining = duration;
            this.basePri = basePri;
            this.effPri = basePri;
            this.deadline = deadline;
            this.nextAgingTime = Long.MAX_VALUE;
        }
    }

    // Doubly linked list used for per-priority FIFO with Node reference inside Job
    private static class Deque {
        static class Node {
            Job job;
            Node prev, next;
            Node(Job j) { this.job = j; }
        }
        Node head, tail;
        int size = 0;
        void addLast(Job j) {
            Node n = new Node(j);
            j.node = n;
            if (tail == null) { head = tail = n; }
            else {
                tail.next = n;
                n.prev = tail;
                tail = n;
            }
            size++;
        }
        Job pollFirst() {
            if (head == null) return null;
            Job j = head.job;
            removeNode(head);
            return j;
        }
        void removeNode(Node n) {
            if (n == null) return;
            if (n.prev != null) n.prev.next = n.next; else head = n.next;
            if (n.next != null) n.next.prev = n.prev; else tail = n.prev;
            n.prev = n.next = null;
            n.job.node = null;
            size--;
        }
        boolean isEmpty() { return size == 0; }
    }

    // Aging heap entry
    private static class AgingEntry implements Comparable<AgingEntry> {
        final long time;
        final Job job;
        AgingEntry(long time, Job job) { this.time = time; this.job = job; }
        @Override public int compareTo(AgingEntry o) { return Long.compare(this.time, o.time); }
    }
    public static SchedResult schedule(int[] ids, long[] arrival, long[] duration, int[] basePri,
                                       long[] deadline, long q, long A, int K) {
        final int n = ids.length;
        // build jobs in same order as inputs
        Job[] jobs = new Job[n];
        for (int i = 0; i < n; ++i) {
            jobs[i] = new Job(ids[i], arrival[i], duration[i], basePri[i], deadline[i]);
        }

        // Per-priority queues (1..K). index 1..K inclusive (we'll use array length K+1)
        Deque[] queues = new Deque[K+1];
        for (int i = 1; i <= K; ++i) queues[i] = new Deque();

        // Set of non-empty priorities for quick highest-priority lookup
        TreeSet<Integer> nonEmpty = new TreeSet<>();

        // Aging min-heap (nextAgingTime -> job)
        PriorityQueue<AgingEntry> agingHeap = new PriorityQueue<>();

        // Helper: enqueue job at current time (job.effPri must be set to its priority before call)
        final long INF = Long.MAX_VALUE;

        class Enqueuer {
            void enqueue(Job j, long now) {
                // if job already completed, ignore
                if (j.completed) return;
                queues[j.effPri].addLast(j);
                nonEmpty.add(j.effPri);
                if (A <= 0) {
                    // treat as instant max-age -> cap immediately
                    if (j.effPri < K) {
                        // raise to K immediately in a loop (but since we just enqueued at effPri,
                        // we will schedule next aging entries only if <K; easier: set nextAgingTime INF)
                        j.nextAgingTime = INF;
                    } else j.nextAgingTime = INF;
                } else {
                    if (j.effPri < K) {
                        j.nextAgingTime = now + A;
                        agingHeap.add(new AgingEntry(j.nextAgingTime, j));
                    } else {
                        j.nextAgingTime = INF;
                    }
                }
            }
        }
        Enqueuer enq = new Enqueuer();

        // index for next arrival to process
        int idx = 0;
        long time = 0L;
        List<Slice> slices = new ArrayList<>(Math.max(16, n));

        // initialization: if some jobs arrive at time 0 we will process them in loop
        while (true) {
            // If no ready jobs, jump to next arrival (if any)
            boolean anyReady = !nonEmpty.isEmpty();
            if (!anyReady) {
                if (idx < n) {
                    // jump time to next arrival
                    time = Math.max(time, arrival[idx]);
                    // process all arrivals at this time
                    while (idx < n && arrival[idx] <= time) {
                        Job j = jobs[idx++];
                        // set initial effPri to base
                        j.effPri = j.basePri;
                        enq.enqueue(j, time);
                    }
                } else {
                    // no more arrivals, and no ready jobs -> done
                    break;
                }
            }

            // process all arrivals that have arrival <= time (arrivals that came during previous quantum)
            while (idx < n && arrival[idx] <= time) {
                Job j = jobs[idx++];
                j.effPri = j.basePri;
                enq.enqueue(j, time);
            }

            // process aging events that happen at or before current time
            while (!agingHeap.isEmpty() && agingHeap.peek().time <= time) {
                AgingEntry ae = agingHeap.poll();
                Job j = ae.job;
                // ignore stale or non-waiting jobs
                if (j.completed) continue;
                if (j.node == null) continue; // not waiting in queue currently
                // ensure this entry matches current job.nextAgingTime (skip stale)
                if (j.nextAgingTime != ae.time) continue;
                // if already at cap, nothing to do
                if (j.effPri >= K) {
                    j.nextAgingTime = INF;
                    continue;
                }
                // remove from its current queue and re-enqueue at higher priority
                int old = j.effPri;
                queues[old].removeNode(j.node);
                if (queues[old].isEmpty()) nonEmpty.remove(old);
                j.effPri = old + 1;
                // re-enqueue at time (it has been waiting until 'time')
                enq.enqueue(j, time);
            }

            // pick highest non-empty priority
            Integer p = nonEmpty.isEmpty() ? null : nonEmpty.last();
            if (p == null) {
                // no ready job after processing aging => loop to allow arrival jump
                continue;
            }

            // dispatch next job (round-robin: pollFirst)
            Job cur = queues[p].pollFirst();
            if (queues[p].isEmpty()) nonEmpty.remove(p);
            if (cur == null) continue; // defensive

            // Running: job is removed from queue, so it is not waiting -> disable aging entry
            cur.nextAgingTime = Long.MAX_VALUE;
            cur.node = null; // already removed

            // compute run time
            long run = Math.min(q, cur.remaining);
            long start = time, end = time + run;
            slices.add(new Slice(cur.id, start, end));
            cur.remaining -= run;
            time = end;

            // after run, process arrivals that arrived during the quantum (they are simply enqueued and will not preempt)
            while (idx < n && arrival[idx] <= time) {
                Job j = jobs[idx++];
                j.effPri = j.basePri;
                enq.enqueue(j, time);
            }

            // Now process any aging events that became due at or before current time (they occur while this job was running)
            while (!agingHeap.isEmpty() && agingHeap.peek().time <= time) {
                AgingEntry ae = agingHeap.poll();
                Job j = ae.job;
                if (j.completed) continue;
                if (j.node == null) continue; // not waiting
                if (j.nextAgingTime != ae.time) continue; // stale
                if (j.effPri >= K) { j.nextAgingTime = Long.MAX_VALUE; continue; }
                int old = j.effPri;
                queues[old].removeNode(j.node);
                if (queues[old].isEmpty()) nonEmpty.remove(old);
                j.effPri = old + 1;
                enq.enqueue(j, time);
            }

            if (cur.remaining == 0) {
                // completion
                cur.completed = true;
                cur.completionTime = time;
                cur.nextAgingTime = Long.MAX_VALUE;
            } else {
                // preempted: re-enqueue at same effective priority (no priority drop), aging continues only while waiting
                enq.enqueue(cur, time);
            }
        } // main loop

        // compute metrics
        long[][] metrics = new long[n][4];
        long totalWT = 0;
        long maxLateness = 0;
        long maxWT = Long.MIN_VALUE, minWT = Long.MAX_VALUE;
        for (int i = 0; i < n; ++i) {
            Job j = jobs[i];
            long CT = j.completed ? j.completionTime : time; // should be completed normally
            long TAT = CT - j.arrival;
            long WT = TAT - ( (duration[i]) );
            long lateness = Math.max(0L, CT - j.deadline);
            metrics[i][0] = CT;
            metrics[i][1] = TAT;
            metrics[i][2] = WT;
            metrics[i][3] = lateness;
            totalWT += WT;
            maxLateness = Math.max(maxLateness, lateness);
            maxWT = Math.max(maxWT, WT);
            minWT = Math.min(minWT, WT);
        }
        double avgWT = n == 0 ? 0.0 : (double) totalWT / n;
        long fairness = (n == 0) ? 0 : (maxWT - minWT);

        return new SchedResult(slices, metrics, maxLateness, avgWT, fairness);
    }

    // ---------- Example main with the mini-example ----------
    public static void main(String[] args) {
        // Example from prompt:
        // q=2, A=3, K=3
        // Job1: arr=0,dur=5,p1,dl=10
        // Job2: arr=2,dur=4,p1,dl=12
        // Job3: arr=4,dur=2,p1,dl=11
        int[] ids = new int[] {1,2,3};
        long[] arr = new long[] {0,2,4};
        long[] dur = new long[] {5,4,2};
        int[] bp = new int[] {1,1,1};
        long[] dl = new long[] {10,12,11};
        long q = 2, A = 3; int K = 3;

        SchedResult res = schedule(ids, arr, dur, bp, dl, q, A, K);

        System.out.println("Slices:");
        for (Slice s : res.slices) System.out.println(s);
        System.out.println("\nPer-job metrics (CT, TAT, WT, Lateness):");
        for (int i = 0; i < ids.length; ++i) {
            System.out.printf("Job id=%d -> CT=%d, TAT=%d, WT=%d, Lateness=%d%n",
                    ids[i], res.metrics[i][0], res.metrics[i][1], res.metrics[i][2], res.metrics[i][3]);
        }
        System.out.printf("%nGlobal: maxLateness=%d, avgWT=%.4f, fairness(maxWT-minWT)=%d%n",
                res.maxLateness, res.avgWT, res.fairness);
    }
}
