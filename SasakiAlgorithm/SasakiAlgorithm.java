import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class SasakiAlgorithm{

    private int numOfProcess;
    private Process[] processes;

    /*  This function creates input number of processes with random numbers.
        Each process has been assigned with an id which is its position + 1.
        It also sets data of the leftmost and rightmost process as unique.
    */
    public void createProcesses(){

        Process processes[] = new Process[this.numOfProcess];

        Random rand = new Random();

        int x;

        for(int i=0;i<this.numOfProcess;i++){
            processes[i] = new Process();
            processes[i].setId(i+1);

            x = rand.nextInt(this.numOfProcess * this.numOfProcess) + 1;
            System.out.print(x + " ");

            if(i==0){
                processes[i].setRightValue(x);
                processes[i].setArea(-1);
                processes[i].setRightUnique(true);
                processes[i].setRightStateChannel(new StateChannel());
            }else if(i<this.numOfProcess-1){
                processes[i].setLeftValue(x);
                processes[i].setRightValue(x);
                processes[i].setLeftStateChannel(new StateChannel());
                processes[i].setRightStateChannel(new StateChannel());
            }else{
                processes[i].setLeftValue(x);
                processes[i].setLeftUnique(true);
                processes[i].setLeftStateChannel(new StateChannel());
            }
        }

        System.out.println();

        this.processes = processes;

    }

    /*  This function is simulating a send event with arguments sendProcess
        and receiveProcess of type Process and flag.
        Flag tells whether data is send is to the left or right, corresponding to the value 1 or 2,
        of the process, since the processes are on a lined network.
    */
    public void send(Process sendProcess, Process receiveProcess, int flag){
        if(flag == 1){      
            receiveProcess.getRightStateChannel().setMessage(sendProcess.getLeftValue());
            receiveProcess.getRightStateChannel().setUnique(sendProcess.getLeftUnique());
            sendProcess.getLeftStateChannel().changeSendFreeState(false);
        }else{
            receiveProcess.getLeftStateChannel().setMessage(sendProcess.getRightValue());
            receiveProcess.getLeftStateChannel().setUnique(sendProcess.getRightUnique());
            sendProcess.getRightStateChannel().changeSendFreeState(false);
        }
    }

    /*  This function updatea the area of the process.
        If flag is 1 it increments the area by 1,
        else it decrements it by 1.
    */
    public void updateArea(Process process, int state){
        if(state == 1){
            process.setArea(process.getArea()+1);
        }else{
            process.setArea(process.getArea()-1);
        }  
    }

    /*  This process takes left and right value of process
        and sorts them and also updates their unique value.
    */
    public void localComputation(Process process){
     
        if(process.getLeftStateChannel() != null && process.getRightStateChannel() != null){
            if(process.getLeftValue() > process.getRightValue()){
                int tmp = process.getLeftValue();
                boolean tmpUnique = process.getLeftUnique();
                process.setLeftUnique(process.getRightUnique());
                process.setLeftValue(process.getRightValue());
                process.setRightValue(tmp);
                process.setRightUnique(tmpUnique);
            }
        }

    }

    /*  This process simulates the receive event by the process.
        Here, process takes the values sent to its channel
        and update its left and right value, also its unique value.
        Area is updated respectively.
    */
    public void receive(Process[] processes, Process process, int flag){

        if(flag == 1){
            int max = Math.max(process.getLeftValue(), process.getLeftStateChannel().getMessage());
            if(process.getLeftValue() != max){
                process.setLeftValue(max);
                process.setLeftUnique(process.getLeftStateChannel().getUnique());
                if(process.getLeftStateChannel().getUnique()){
                    updateArea(process, 2);
                }
            }
            process.getLeftStateChannel().changeSendFreeState(true);
        }else{
            int min = Math.min(process.getRightValue(), process.getRightStateChannel().getMessage());
            if(process.getRightValue() != min){
                process.setRightValue(min);
                process.setRightUnique(process.getRightStateChannel().getUnique());
                if(process.getRightStateChannel().getUnique()){
                    updateArea(processes[process.getId()], 1);
                }
            }
            process.getRightStateChannel().changeSendFreeState(true);
        }

    }

    /*/*  This function just prints the corresponding values of every process.*/
    public void printState(){
        for(int i=0;i<this.numOfProcess;i++){
            if(this.processes[i].getArea() == -1){
                System.out.print(this.processes[i].getRightValue() + ", ");
                // System.out.printf("%d (%d), ", this.processes[i].getRightValue(), this.processes[i].getArea());
            }else{
                System.out.print(this.processes[i].getLeftValue() + ", ");
                // System.out.printf("%d (%d), ", this.processes[i].getLeftValue(), this.processes[i].getArea());
            }
        }
        System.out.println();
    }

    /*  This process creates a thread for each process.
        A thread within which a left and right thread, for each process
        is created to receive and send from and to left and right process.
        Threads are join so that main thread waits for all the threads to finish for
        a round and then goes for next round.
    */
    public void createThreads(){

        System.out.println("\nPrinting states of each round...");

        ArrayList<Thread> threads;

        for(int i=1;i<this.numOfProcess;i++){

            threads = new ArrayList<>();

            for(int j=0;j<this.numOfProcess;j++){
                final int index = j;

                Process[] processes = this.processes;

                Thread thread = new Thread(new CustomRunnable(processes){
                
                    @Override
                    public void run() {

                        Thread leftChannelThread = null;
                        Thread rightChannelThread = null;
                        if(processes[index].getLeftStateChannel() != null){

                            leftChannelThread = new Thread(new CustomRunnable(processes){
                            
                                @Override
                                public void run() {

                                    send(processes[index], processes[index-1], 1);

                                    synchronized(processes[index].getLeftStateChannel()){
                                        if(processes[index-1].getRightStateChannel().isSendFree()){
                                            try {
                                                processes[index].getLeftStateChannel().wait();
                                            } catch (Exception e) {
                                                
                                            }
                                        }
                                        receive(processes, processes[index], 1);
                                    }

                                    synchronized(processes[index-1].getRightStateChannel()){
                                        processes[index-1].getRightStateChannel().notify();
                                    }
                                }
                            });

                            leftChannelThread.start();
                        }
                        if(processes[index].getRightStateChannel() != null){

                            rightChannelThread = new Thread(new CustomRunnable(processes){
                            
                                @Override
                                public void run() {

                                    send(processes[index], processes[index+1], 2);

                                    synchronized(processes[index].getRightStateChannel()){
                                        if(processes[index+1].getLeftStateChannel().isSendFree()){
                                            try {
                                                processes[index].getRightStateChannel().wait();
                                            } catch (Exception e) {
                                                
                                            }
                                        }
                                        receive(processes, processes[index], 2);
                                    }

                                    synchronized(processes[index+1].getLeftStateChannel()){
                                        processes[index+1].getLeftStateChannel().notify();
                                    }
                                    
                                }
                            });

                            rightChannelThread.start();
                            
                        }

                        if(leftChannelThread != null){
                            try {
                                leftChannelThread.join();
                            } catch (Exception e) {
                            }
                        }
                        if(rightChannelThread != null){
                            try {
                                rightChannelThread.join();
                            } catch (Exception e) {
                            }
                        }

                        leftChannelThread = null;
                        rightChannelThread = null;

                        localComputation(processes[index]);
                    }
                });
                threads.add(thread);
            }

            for(Thread thread: threads){
                thread.start();
            }

            for(Thread thread: threads){
                try {
                    thread.join();
                } catch (Exception e) {
                }
                
            }

            threads = null;

            printState();

            if(i+1 == this.numOfProcess){
                break;
            }

        }
    }

    /*  This is the main function where the execution of program starts.
        It takes input number of processes.
        It first creates the object of type SasakiAlgorithm and sets the number
        of processes.
        Then functions createProcess, createThreads and printState are called
        one by one.
    */
    public static void main(String args[])throws IOException{

        SasakiAlgorithm sasakiAlgorithm = new SasakiAlgorithm();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter the number of processes.");
        
        while(true){
            try {
                sasakiAlgorithm.numOfProcess = Integer.parseInt(br.readLine());
                break;
            } catch (Exception e) {
                System.out.println("Not a number.");
            }
        }

        System.out.printf("\nGeneratin %d processes with random numbers...", sasakiAlgorithm.numOfProcess);
        System.out.println();

        long startTime = System.nanoTime();

        sasakiAlgorithm.createProcesses();

        System.out.println("\nCreating threads for each process...");

        sasakiAlgorithm.createThreads();

        System.out.println("\nFinal sorted list of numbers...");

        sasakiAlgorithm.printState();

        sasakiAlgorithm = null;

        long endTime = System.nanoTime();

        System.out.printf("\nSorted in %.3f milliseconds", (float)((endTime - startTime)/1000000.0));
        System.out.println();

        return;
    }
}