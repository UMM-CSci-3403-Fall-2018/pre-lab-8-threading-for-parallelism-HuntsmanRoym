package search;

import java.util.List;

public class ThreadedSearch<T> implements Searcher<T>, Runnable {

    private int numThreads;
    private T target;
    private List<T> list;
    private int begin;
    private int end;
    private Answer answer;

    public ThreadedSearch(int numThreads) {
        this.numThreads = numThreads;
    }

    private ThreadedSearch(T target, List<T> list, int begin, int end, Answer answer) {
        this.target = target;
        this.list = list;
        this.begin = begin;
        this.end = end;
        this.answer = answer;
    }

    /**
     * Searches `list` in parallel using `numThreads` threads.
     * <p>
     * You can assume that the list size is divisible by `numThreads`
     */
    public boolean search(T target, List<T> list) throws InterruptedException {
        Answer answer = new Answer();

        Thread[] threads = new Thread[numThreads];

        //Starts and creates threads
        for (int i=0; i<numThreads; i++) {
            //Sets begin as first position in the list for each i'th thread
            int begin = ((int)Math.floor(list.size() / numThreads)) * i;
            //Sets end as final position for each individual i'th thread as the i+1's start position
            int end = ((int)Math.floor(list.size() / numThreads) * (i+1));

            //Create and start thread i based off ThreadedSearch
            ThreadedSearch<T> threadedSearch = new ThreadedSearch<>(target, list, begin, end, answer);
            threads[i] = new Thread(threadedSearch);
            threads[i].start();
        }

        //Wait for all threads
        for (int i=0; i<numThreads; i++) {
            threads[i].join();
        }

        //Returns if thread found 'answer' or not
        return answer.getAnswer();
    }

    public void run() {
        for (int i=begin; i<end; i++) {

            if (answer.getAnswer() == true) {

                break;
            }

            else {

                if(list.get(i) == target){
                    answer.setAnswer(true);
                }
            }
        }
    }

    private class Answer {
        private boolean answer = false;

        // In a more general setting you would typically want to synchronize
        // this method as well. Because the answer is just a boolean that only
        // goes from initial initial value (`false`) to `true` (and not back
        // again), we can safely not synchronize this, and doing so substantially
        // speeds up the lookup if we add calls to `getAnswer()` to every step in
        // our threaded loops.
        public boolean getAnswer() {

            return answer;
        }

        // This has to be synchronized to ensure that no two threads modify
        // this at the same time, possibly causing race conditions.
        // Actually, that's not really true here, because we're just overwriting
        // the old value of answer with the new one, and no one will actually
        // call with any value other than `true`. In general, though, you do
        // need to synchronize update methods like this to avoid race conditions.
        public synchronized void setAnswer(boolean newAnswer) {

            answer = newAnswer;
        }
    }

}
