package dal;

import java.sql.ResultSet;

public interface DalBackendServices {

  public ResultSet prepare(String requete);

}
