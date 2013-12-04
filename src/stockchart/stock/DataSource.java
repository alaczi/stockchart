package stockchart.stock;

import java.util.Collection;
import java.util.Date;
/**
 * Interface for data providers
 * @author Andras Laczi
 */
public interface DataSource {

    /**
     * Returns the set of data from the given dates
     * @param from
     * @param to
     * @return Collection of data
     * @throws Exception
     */
    public Collection getData(Date from, Date to) throws Exception;

}
