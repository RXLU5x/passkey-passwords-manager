package localDB;

import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface TestDB
{
    @SqlUpdate("CREATE TABLE IF NOT EXISTS user (id BIGINT PRIMARY KEY, name VARCHAR)")
    void create();

    @SqlUpdate("INSERT INTO user(id, name) VALUES (?, ?)")
    void add(long id, String string);

    default String addTest() {

        add(1594915878144L, "Test");
        return get(1594915878144L);
    };

    @SqlQuery("SELECT (name) FROM user WHERE id=(?)")
    String get(long id);

    @SqlUpdate("DELETE FROM user WHERE id=(?)")
    boolean delete(long id);
}
