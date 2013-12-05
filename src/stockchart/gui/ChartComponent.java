package stockchart.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * Chart component for drawing the chart itself
 * @author Andras Laczi
 */
public class ChartComponent extends Component {

    int width;
    int height;
    Map data;
    Date xScaleMin;
    Date xScaleMax;
    int xScaleLenghtPixel;
    BigDecimal yScaleMin;
    BigDecimal yScaleMax;
    int yScaleLenghtPixel;


    private final static int ORIGO_X = 30;
    private final static int ORIGO_Y_OFFSET = 30;
    private final static int BORDER_RIGHT = 20;
    private final static int BORDER_TOP = 10;

    /**
     * Inits a chart with the given dimensions and data
     * @param width
     * @param height
     * @param data
     */
    public ChartComponent(int width, int height, Map data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    /**
     * Paints the component when the parent setVisible or paint invoked
     * @param g
     */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.drawRect(1, 1, width - 1, height - 1);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(2, 2, width - 3, height - 3);
        g2d.setColor(Color.BLACK);
        if (data == null || data.isEmpty()) {
            g2d.setColor(Color.RED);
            g2d.drawString("No DATA", (width - 30) / 2, (height - 10) / 2);
        } else {
            this.drawYAxis(g2d);
            this.drawXAxis(g2d);
            this.drawChart(g2d);
        }
    }

    /**
     * Calculates the Y dimensions and draws the y axis
     * @param g
     */
    private void drawYAxis(Graphics2D g) {
        g.setColor(Color.BLACK);
        yScaleLenghtPixel = this.height - ChartComponent.ORIGO_Y_OFFSET -ChartComponent.BORDER_TOP;
        g.drawLine(ChartComponent.ORIGO_X, (this.height - ChartComponent.ORIGO_Y_OFFSET), this.ORIGO_X, ChartComponent.BORDER_TOP);
        BigDecimal min = getMin();
        BigDecimal max = getMax();
        BigDecimal valueRange = max.subtract(min);
        BigDecimal rangeOrderOfMagnitude = this.calculateOrderOfMagnitude(valueRange);
        yScaleMin= new BigDecimal(Math.floor(min.doubleValue() / Math.pow(10, rangeOrderOfMagnitude.doubleValue())) *  Math.pow(10, rangeOrderOfMagnitude.doubleValue()));
        yScaleMax= new BigDecimal(Math.ceil(max.doubleValue() / Math.pow(10, rangeOrderOfMagnitude.doubleValue())) *  Math.pow(10, rangeOrderOfMagnitude.doubleValue()));
        BigDecimal scaleRange = yScaleMax.subtract(yScaleMin);
        BigDecimal stepValue = new BigDecimal(Math.pow(10, rangeOrderOfMagnitude.doubleValue()));
        BigDecimal stepCount = scaleRange.divide(stepValue);
        int stepPixelValue = yScaleLenghtPixel / stepCount.intValue();
        BigDecimal step = yScaleMin.add(stepValue);
        while (step.doubleValue() <= yScaleMax.doubleValue()){
            int yposition = (this.height - ChartComponent.ORIGO_Y_OFFSET - stepPixelValue * (step.divide(stepValue).intValue()));
            g.drawString(step.toString(), 5, yposition +5);
            g.drawLine(this.ORIGO_X -3 , yposition, this.ORIGO_X + 3, yposition);
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(this.ORIGO_X + 3 , yposition, this.width - ChartComponent.BORDER_RIGHT+3, yposition);
            g.setColor(Color.BLACK);
            step = step.add(stepValue);
        }
        g.drawString(yScaleMin.toString(), 5, (this.height - ChartComponent.ORIGO_Y_OFFSET + 5));
    }

    /**
     * Calculates the x dimensions and draws the x axis
     * @param g
     */
    private void drawXAxis(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawLine(this.ORIGO_X, (this.height - ChartComponent.ORIGO_Y_OFFSET), this.width - ChartComponent.BORDER_RIGHT +3, (this.height - ChartComponent.ORIGO_Y_OFFSET));
        xScaleLenghtPixel = this.width - ChartComponent.ORIGO_X - ChartComponent.BORDER_RIGHT;
        List sortedKeys=new ArrayList(data.keySet());
        Collections.sort(sortedKeys);
        xScaleMin = (Date)sortedKeys.toArray()[0];
        xScaleMax = (Date)sortedKeys.toArray()[sortedKeys.size() -1];
        long stepSize = (xScaleMax.getTime() - xScaleMin.getTime()) / 10;
        long step = xScaleMin.getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        for (int i = 0; i <= 10; i++){
            int xposition = xScaleLenghtPixel*i/10 +this.ORIGO_X;
            g.drawLine(xposition, this.height - ChartComponent.ORIGO_Y_OFFSET -3, xposition, this.height - ChartComponent.ORIGO_Y_OFFSET +3);
            g.drawString(format.format(new Date(step)), xposition -30,  this.height - ChartComponent.ORIGO_Y_OFFSET +15 );
            g.setColor(Color.LIGHT_GRAY);
            if (i != 0) {
                g.drawLine(xposition, this.height - ChartComponent.ORIGO_Y_OFFSET -3, xposition, ChartComponent.BORDER_TOP);
            }
            g.setColor(Color.BLACK);
            step += stepSize;
        }
    }

    /**
     * Places the data points in the chart
     * @param g
     */
    private void drawChart(Graphics2D g){
        List sortedKeys=new ArrayList(data.keySet());
        Collections.sort(sortedKeys);
        Iterator it = sortedKeys.iterator();
        BigDecimal yRange = this.yScaleMax.subtract(this.yScaleMin);
        long xRange = this.xScaleMax.getTime() - this.xScaleMin.getTime();
        int[] xPositions = new int[sortedKeys.size()];
        int[] yPositions = new int[sortedKeys.size()];
        int i = 0;
        g.setColor(Color.BLUE);
        while (it.hasNext()){
            Date key = (Date) it.next();
            BigDecimal value = (BigDecimal) data.get(key);
            try {
                yPositions[i]= this.height - ChartComponent.ORIGO_Y_OFFSET - (value.subtract(this.yScaleMin).multiply(new BigDecimal(this.yScaleLenghtPixel)).divide(yRange, 2, BigDecimal.ROUND_HALF_DOWN).intValue());
                BigDecimal diff = new BigDecimal(key.getTime()- this.xScaleMin.getTime());
                BigDecimal pixelValue = diff.multiply(new BigDecimal( this.xScaleLenghtPixel)).divide(new BigDecimal(xRange), 2, BigDecimal.ROUND_HALF_DOWN);
                xPositions[i] =  pixelValue.intValue() + ChartComponent.ORIGO_X;
            } catch (java.lang.ArithmeticException e) {
                System.out.println(key.getTime()- this.xScaleMin.getTime());
                e.printStackTrace();
            }
            i++;
        }
        g.drawPolyline(xPositions, yPositions, sortedKeys.size());
    }

    /**
     * Returns the maximum value from the datasource
     * @return
     */
    private BigDecimal getMax() {
        BigDecimal max = new BigDecimal("0");
        Iterator it = data.values().iterator();
        while (it.hasNext()) {
            max = max.max((BigDecimal) it.next());
        }
        return max;
    }

    /**
     * Returns the minimum value from the datasource
     * @return
     */
    private BigDecimal getMin() {
        BigDecimal min = new BigDecimal("0");
        Iterator it = data.values().iterator();
        while (it.hasNext()) {
            min = min.min((BigDecimal) it.next());
        }
        return min;
    }

    /**
     * Calculates a magnitude for the y axis display
     * @param range
     * @return
     */
    private BigDecimal calculateOrderOfMagnitude(BigDecimal range) {
        double rangeDouble = range.doubleValue();
        double log = Math.floor(Math.log(rangeDouble) / Math.log(10));
        return new BigDecimal(log);
    }

}
