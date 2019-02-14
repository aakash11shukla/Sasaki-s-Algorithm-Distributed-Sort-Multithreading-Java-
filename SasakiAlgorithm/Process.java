/*  This class defines the Process.
    It containes arguments such as id, left and right value,
    left and right unique value and also area.
    Each process also has left and right state channels, which is
    set to null, initially.
*/
public class Process{

    private int id;
    private int leftValue = -1;
    private int rightValue = -1;
    private int area = 0;
    private boolean leftUnique = false;
    private boolean rightUnique = false;

    private StateChannel leftSC = null;
    private StateChannel rightSC = null;

    Process(){}

    public void setId(int id){
        this.id = id;
    }
    public void setLeftValue(int value){
        this.leftValue = value;
    }
    public void setRightValue(int value){
        this.rightValue = value;
    }
    public void setArea(int area){
        this.area = area;
    }
    public void setLeftUnique(boolean unique){
        this.leftUnique = unique;
    }
    public void setRightUnique(boolean unique){
        this.rightUnique = unique;
    }
    public void setLeftStateChannel(StateChannel channel){
        this.leftSC = channel;
    }
    public void setRightStateChannel(StateChannel channel){
        this.rightSC = channel;
    }

    public int getId(){
        return this.id;
    }
    public int getLeftValue(){
        return this.leftValue;
    }
    public int getRightValue(){
        return this.rightValue;
    }
    public int getArea(){
        return this.area;
    }
    public boolean getLeftUnique(){
        return this.leftUnique;
    }
    public boolean getRightUnique(){
        return this.rightUnique;
    }
    public StateChannel getLeftStateChannel(){
        return this.leftSC;
    }
    public StateChannel getRightStateChannel(){
        return this.rightSC;
    }
}