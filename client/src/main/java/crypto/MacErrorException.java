package crypto;

public class MacErrorException extends Exception
{
    public MacErrorException(String message) {
        super(message);
    }

    public MacErrorException() { }
}
