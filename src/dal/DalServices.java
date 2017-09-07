package dal;

public interface DalServices {

  public void startTransaction();

  public void commitTransaction();

  public void rollbackTransaction();

  public void openConnection();

  public void closeConnection();

}
