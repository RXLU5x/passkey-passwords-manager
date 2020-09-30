package localDB;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.List;

/**
 * Central Database management.
 * Defines access to the H2 using JDBI, and auxiliary methods
 */
public class Database
{
    private static Database singleton;
    private static Jdbi jdbi;
    private static final String defaultLocation = "./db/";

    private final DbAccess db;

    /**
     * Class constructor.
     * Sets connection to embedded database
     */
    private Database(String location){
        jdbi = Jdbi.create("jdbc:h2:file:" + location + "local");
        jdbi.installPlugin(new SqlObjectPlugin());
        db = jdbi.onDemand(DbAccess.class);
        db.initDB();
    }

    /**
     * Factory method. Enforces Singleton pattern.
     * @param location location to add DB file, <code>NULL</code> ro use default locaation
     * @return <code>Database</code> singleton instance
     */
    public static Database getInstance(String location) {
        if (location == null){
            location = defaultLocation;
        }

        if(singleton == null)
            singleton = new Database(location);

        return singleton;
    }

    /**
     * Add a record of a vault to local db. If exists replace it.
     * @param id
     * @param timeTag
     * @param vaultJSON
     * @return
     */
    public boolean put(String id, long timeTag, String vaultJSON){
        return db.add(id, timeTag, vaultJSON);
    }

    /**
     * Get <code>DbRecordDAO</code> by id.
     * @param id a string identifying a vault
     * @return a representation of the vault, his id and timeTag
     */
    public DbRecordDAO get(String id) {
        return db.get(id);
    }

    /**
     * Get all <code>DbRecordDAO</code>s.
     * @return representations of the vaults, their ids and timeTags
     */
    public List<DbRecordDAO> getAll(){
        return db.getAll();
    }

    /**
     * Delete <code>DbRecordDAO</code> by id.
     * @param id id a string identifying a vault
     * @return
     */
    public boolean delete(String id){
        return db.delete(id);
    }

    /**
     * Delete all <code>DbRecordDAO</code>s.
     * @return a <code>boolean</code>, true if success, false if not.
     */
    public boolean deleteAll(){
        return db.deleteAll();
    }
}
