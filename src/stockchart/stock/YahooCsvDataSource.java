package stockchart.stock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import stockchart.stock.data.Record;

/**
 * CSV Data Sorce from yahoo
 * @author Andras Laczi
 */
public class YahooCsvDataSource implements DataSource{


    /**
     * Yahoo csv datasource
     */
    private final static String url = "http://ichart.finance.yahoo.com/table.csv?s=YHOO&d=%1$d&e=%2$d&f=%3$d&g=d&a=%4$d&b=%5$d&c=%6$d&ignore=.csv";

    /**
     * Gets the data from yahoo
     * @param from filter from
     * @param to filter to
     * @return the collection of Record objects
     * @throws Exception
     */
    public Collection getData(Date from, Date to) throws Exception{
        ArrayList data = new ArrayList();
        try {
            Calendar calFrom = new GregorianCalendar();
            calFrom.setTime(from);
            Calendar calTo = new GregorianCalendar();
            calTo.setTime(to);
            System.out.println(String.format(YahooCsvDataSource.url,
                    calTo.get(Calendar.MONTH),
                    calTo.get(Calendar.DAY_OF_MONTH-1),
                    calTo.get(Calendar.YEAR),
                    calFrom.get(Calendar.MONTH),
                    calFrom.get(Calendar.DAY_OF_MONTH),
                    calFrom.get(Calendar.YEAR)));
            URL url = new URL(String.format(YahooCsvDataSource.url,
                    calTo.get(Calendar.MONTH),
                    calTo.get(Calendar.DAY_OF_MONTH-1),
                    calTo.get(Calendar.YEAR),
                    calFrom.get(Calendar.MONTH),
                    calFrom.get(Calendar.DAY_OF_MONTH),
                    calFrom.get(Calendar.YEAR)));
            URLConnection urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            BufferedReader dis = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String s;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat decimalFormat= new DecimalFormat();
            DecimalFormatSymbols custom=new DecimalFormatSymbols();
            custom.setDecimalSeparator('.');
            decimalFormat.setDecimalFormatSymbols(custom);
            decimalFormat.setParseBigDecimal(true);
            boolean firstrow = true;
            while ((s = dis.readLine()) != null) {
                if (firstrow) {
                    firstrow = false;
                    continue;
                }
                String[] rowfields = s.split("[,]+");
                Date date = dateFormat.parse(rowfields[0]);
                if (from != null && date.before(from)) {
                    continue;
                }
                if (to != null && date.after(to)){
                    continue;
                }
                Record rec = new Record();
                //Date,Open,High,Low,Close,Volume,Adj Close
                rec.setDate(date);
                rec.setOpen((BigDecimal) decimalFormat.parse(rowfields[1]));
                rec.setHigh((BigDecimal) decimalFormat.parse(rowfields[2]));
                rec.setLow((BigDecimal) decimalFormat.parse(rowfields[3]));
                rec.setClose((BigDecimal) decimalFormat.parse(rowfields[4]));
                rec.setVolume(Integer.parseInt(rowfields[5]));
                rec.setAdjClose((BigDecimal) decimalFormat.parse(rowfields[6]));
                data.add(rec);
            }
            dis.close();
        } catch (MalformedURLException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        } catch (ParseException e) {
            throw new Exception(e);
        };
        return data;
    }

}
