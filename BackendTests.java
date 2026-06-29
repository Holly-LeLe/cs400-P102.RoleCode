import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

public class BackendTests {

    /**
     * Checks that addRecord inserts a record and that getAndSetRange can
     * retrieve records within a collectables range.
     */
    @Test
    public void roleTest1() {
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);

        GameRecord record = new GameRecord("testPlayer",
                GameRecord.Continent.ASIA, 1000, 100, 125, "010:00:00");

        backend.addRecord(record);
        List<String> names = backend.getAndSetRange(120, 130);

        assertTrue(names.contains("testPlayer"));
    }

    /**
     * Checks that applyAndSetFilter filters records by continent.
     */
    @Test
    public void roleTest2() {
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);

        List<String> names = backend.applyAndSetFilter(GameRecord.Continent.EUROPE);

        assertTrue(names.contains("speedRoyalty"));
        assertFalse(names.contains("xXxgamer47xXx"));
    }

    /**
     * Checks that readData can load records from the CSV file and that
     * getTopTen returns a list of matching record names.
     */
    @Test
    public void roleTest3() throws Exception {
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);

        backend.readData("records.csv");
        List<String> topTen = backend.getTopTen();

        assertNotNull(topTen);
        assertTrue(topTen.size() > 0);
    }
}
