package LZW.Guard;

public class GuardExecption extends Exception{
    public GuardExecption() {
    }
    public GuardExecption(String message, Throwable throwable) {
        super(message, throwable);
    }
}
