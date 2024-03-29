package exception;

// etends runtimeException
@SuppressWarnings("serial")
public class BizzException extends RuntimeException {

  public BizzException() {
    super();
  }

  public BizzException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public BizzException(String message, Throwable cause) {
    super(message, cause);
  }

  public BizzException(String message) {
    super(message);
  }

  public BizzException(Throwable cause) {
    super(cause);
  }

}
