package localDB;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




public class DatabaseTest
{
    final Jdbi jdbi;
    final TestDB testDB;

    {
        jdbi = Jdbi.create("jdbc:h2:file:./src/test/resources/db/test");
        jdbi.installPlugin(new SqlObjectPlugin());

        testDB = jdbi.onDemand(TestDB.class);
        testDB.create();
    }

    @Test
    public void checkFunctioning() {
        String result = testDB.addTest();
        Assertions.assertEquals("Test", result);

        Boolean res = testDB.delete(1594915878144L);
        Assertions.assertEquals(true, res);

        result = testDB.get(1);
        Assertions.assertNull(result);
    }

    @Test
    public void db(){
        Database db = Database.getInstance("./src/test/resources/db/");

        db.put("1", 1594915878144L, "ola");

        db.get("1");
        db.getAll();
        db.delete("1");

        db.put("1", 1594915878144L, "ola");
        db.put("2", 1594915878144L, "ola");

        db.deleteAll();
        db.getAll();
    }
}
