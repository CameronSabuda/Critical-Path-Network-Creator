/*public class CriticalPath {
    public static void main(String[] args) {

    }

    public static class Task {
        //the actual cost of the task
        public int cost;
        //the cost of the task along the critical path
        public int criticalCost;
        //a name for the task for printing
        public String name;
        //the tasks on which this task is dependant
        public HashSet<Task> dependencies = new HashSet<Task>();

        public Task(String name, int cost, Task... dependencies) {
            this.name = name;
            this.cost = cost;
            for (Task t : dependencies) {
                this.dependencies.add(t);
            }
        }

        private Integer[] _adjacencyList;

    }
}*/