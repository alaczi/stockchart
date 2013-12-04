package stockchart.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JOptionPane;

import stockchart.stock.data.Record;
import stockchart.stock.DataSource;
import stockchart.stock.YahooCsvDataSource;

/**
 * The Main window for the application gui
 * @author Andras Laczi
 */
public class MainWindow extends Frame implements ActionListener {

  Button fetchButton;
  Label startDate, endDate;
  TextField startDay, startMonth, startYear;
  TextField endDay, endMonth, endYear;
  ChartComponent chart;

  public MainWindow() {
    super("Stock Rate chart demo");
    setSize(800, 600);
    addWindowListener(new BasicWindowMonitor());

    Panel toolbar = new Panel();
    toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
    startDay = new TextField("1", 2);
    startMonth = new TextField("1", 2);
    startYear = new TextField("2013", 4);
    endDay = new TextField("31", 2);
    endMonth = new TextField("12", 2);
    endYear = new TextField("2013", 4);
    fetchButton = new Button("Fetch");
    fetchButton.addActionListener(this);
    toolbar.add(new Label("Start date:"));
    toolbar.add(startDay);
    toolbar.add(new Label("/"));
    toolbar.add(startMonth);
    toolbar.add(new Label("/"));
    toolbar.add(startYear);
    toolbar.add(new Label("End date:"));
    toolbar.add(endDay);
    toolbar.add(new Label("/"));
    toolbar.add(endMonth);
    toolbar.add(new Label("/"));
    toolbar.add(endYear);
    toolbar.add(fetchButton);
    add(toolbar, BorderLayout.NORTH);
  }

  /**
   * Data fetch button clicked
   * @param ae
   */
  public void actionPerformed(ActionEvent ae) {
    try {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        Date start = format.parse(startYear.getText() + '.' + startMonth.getText() + '.'+ startDay.getText());
        Date end = format.parse(endYear.getText() + '.' + endMonth.getText() + '.'+ endDay.getText());
        this.drawChart(start, end);
    } catch (ParseException e){
        JOptionPane.showMessageDialog(this, "Invalid dates provided");
    }
  }

  /**
   * Draws the chart, tries to get the data from datasource
   * @param start
   * @param end
   */
  private void drawChart(Date start, Date end){
      DataSource ds = new YahooCsvDataSource();
      try {
        Collection data = ds.getData(start, end);
        if (chart != null) {
            remove(chart);
        }
        HashMap map = new HashMap();
        Iterator it = data.iterator();
        while(it.hasNext()){
            Record rec = (Record) it.next();
            map.put(rec.getDate(), rec.getClose());
        }

        chart = new ChartComponent(795, 530, map);
        add(chart, BorderLayout.CENTER);
        this.setVisible(true);
      } catch (Exception e){
          JOptionPane.showMessageDialog(this, "Exception while fetching data.\nMessage:" + e.getMessage());
          e.printStackTrace();
      }
  }

}
