package dataaccess;

public class DataAccessObject {
    private static DataAccessObject singleInstance;

    public static DataAccessObject getInstance() {
        return singleInstance;
    }
}
