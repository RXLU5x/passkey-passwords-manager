package localDB;

import org.jdbi.v3.core.mapper.reflect.ColumnName;


public class DbRecordDAO
{
    @ColumnName("id")
    private String id;

    @ColumnName("timeTag")
    private long timeTag;

    @ColumnName("vault")
    private String vault;

    public DbRecordDAO(String id, long timeTag, String vault) {
        this.id = id;
        this.timeTag = timeTag;
        this.vault = vault;
    }

    public DbRecordDAO(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimeTag() {
        return timeTag;
    }

    public void setTimeTag(long timeTag) {
        this.timeTag = timeTag;
    }

    public String getVault() {
        return vault;
    }

    public void setVault(String vault) {
        this.vault = vault;
    }
}
