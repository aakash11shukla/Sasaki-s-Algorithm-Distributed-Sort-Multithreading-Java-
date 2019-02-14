/*  This is a class which defines state channel.
    It containes parameter such as message, unique value
    and a boolean value which tells whether send can be done or not.
    Initially send is true and when a send is made it is
    set to false.
*/

public class StateChannel{

    private boolean sendFreeState = true;
    private int message = -1;
    private boolean unique = false;

    StateChannel(){}

    public void setMessage(int message){
        this.message = message;
    }

    public void setUnique(boolean unique){
        this.unique = unique;
    }

    public int getMessage(){
        return this.message;
    }

    public boolean getUnique(){
        return this.unique;
    }

    public void changeSendFreeState(boolean state){
        this.sendFreeState = state;
    }

    public boolean isSendFree(){
        return this.sendFreeState;
    }

}