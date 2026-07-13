import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

/**
 * Backend implementation that stores game records in an iterable sorted tree
 * and supports loading, filtering, and ranking records.
 */
public class Backend implements BackendInterface {

    private IterableSortedCollection<GameRecord> tree;
    private Integer low;
    private Integer high;
    private GameRecord.Continent filter;

    /**
     * Creates a backend that stores records in the provided tree.
     *
     * @param tree collection used to store game records
     */
    public Backend(IterableSortedCollection<GameRecord> tree) {
        this.tree = tree;
        this.low = null;
        this.high = null;
        this.filter = null;
    }

    /**
     * Adds one game record to the backend.
     *
     * @param record game record to add
     */
    @Override
    public void addRecord(GameRecord record) {
        this.tree.insert(record);
    }

    /**
     * Reads game records from a CSV file and inserts them into the tree.
     *
     * @param filename path to the CSV file
     * @throws IOException if the file cannot be read
     */
    @Override
    public void readData(String filename) throws IOException {
        try (Scanner scnr = new Scanner(new File(filename))) {

        if (!scnr.hasNextLine()) {
            return;
        }

        String header = scnr.nextLine();
        String[] headers = header.split(",");

        int nameIndex = -1;
        int continentIndex = -1;
        int scoreIndex = -1;
        int maxHealthIndex = -1;
        int collectablesIndex = -1;
        int completionTimeIndex = -1;

        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals("name")) nameIndex = i;
            else if (headers[i].equals("continent")) continentIndex = i;
            else if (headers[i].equals("score")) scoreIndex = i;
            else if (headers[i].equals("max_health")) maxHealthIndex = i;
            else if (headers[i].equals("collectables")) collectablesIndex = i;
            else if (headers[i].equals("completion_time")) completionTimeIndex = i;
        }

        while (scnr.hasNextLine()) {
            String line = scnr.nextLine();
            if (line.trim().isEmpty()) continue;

            String[] data = line.split(",");

            String name = data[nameIndex];
            GameRecord.Continent continent =
                GameRecord.Continent.valueOf(data[continentIndex].trim().toUpperCase());
            int score = Integer.parseInt(data[scoreIndex]);
            int maxHealth = Integer.parseInt(data[maxHealthIndex]);
            int collectables = Integer.parseInt(data[collectablesIndex]);
            String completionTime = data[completionTimeIndex];

            GameRecord record = new GameRecord(
                name, continent, score, maxHealth, collectables, completionTime);

            addRecord(record);
        }

        }
    }

    /**
     * Sets the collectables range and returns matching record names.
     *
     * @param low minimum collectables value, or null for no lower bound
     * @param high maximum collectables value, or null for no upper bound
     * @return names of records matching the current criteria
     */
    @Override
    public List<String> getAndSetRange(Integer low, Integer high) {
        this.low = low;
        this.high = high;
        return getMatchingNames();
    }

    /**
     * Sets the continent filter and returns matching record names.
     *
     * @param continent continent to filter by, or null for no filter
     * @return names of records matching the current criteria
     */
    @Override
    public List<String> applyAndSetFilter(GameRecord.Continent continent) {
        this.filter = continent;
        return getMatchingNames();
    }

    /**
     * Returns up to ten matching record names sorted by fastest completion time.
     *
     * @return names of the top ten fastest matching records
     */
    @Override
    public List<String> getTopTen() {
        ArrayList<GameRecord> matches = new ArrayList<GameRecord>();

        for (GameRecord record : tree) {
            if (matchesCurrentRangeAndFilter(record)) {
                matches.add(record);
            }
        }

        matches.sort((a, b) -> timeToSeconds(a.getCompletionTime())
                - timeToSeconds(b.getCompletionTime()));

        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < matches.size() && i < 10; i++) {
            names.add(matches.get(i).getName());
        }

        return names;
    }

    private List<String> getMatchingNames() {
        ArrayList<String> names = new ArrayList<String>();

        for (GameRecord record : tree) {
            if (matchesCurrentRangeAndFilter(record)) {
                names.add(record.getName());
            }
        }

        return names;
    }

    private boolean matchesCurrentRangeAndFilter(GameRecord record) {
        if (low != null && record.getCollectables() < low) return false;
        if (high != null && record.getCollectables() > high) return false;
        if (filter != null && record.getContinent() != filter) return false;
        return true;
    }

    private int timeToSeconds(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }
}
