package exception;

@SuppressWarnings("serial")
public class FatalException extends RuntimeException {

  public FatalException() {
    super();
  }

  public FatalException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public FatalException(String message, Throwable cause) {
    super(message, cause);
  }

  public FatalException(String message) {
    super(message);
  }

  public FatalException(Throwable cause) {
    super(cause);
  }

}
