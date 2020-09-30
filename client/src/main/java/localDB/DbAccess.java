package localDB;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface DbAccess
{
    @SqlUpdate("CREATE TABLE IF NOT EXISTS vaults (id VARCHAR PRIMARY KEY, timeTag BIGINT, vault VARCHAR)")
    boolean initDB();

    @SqlUpdate("MERGE INTO vaults (id, timeTag, vault) VALUES (?, ?, ?)")
    boolean add(String id, long timeTag, String vault);

    @SqlQuery("SELECT *  FROM vaults WHERE id = ?")
    @RegisterBeanMapper(DbRecordDAO.class)
    DbRecordDAO get(String id);

    @SqlQuery("SELECT * FROM vaults")
    @RegisterBeanMapper(DbRecordDAO.class)
    List<DbRecordDAO> getAll();

    @SqlUpdate("DELETE FROM vaults WHERE id=(?)")
    boolean delete(String id);

    @SqlUpdate("DELETE FROM vaults")
    boolean deleteAll();
}
