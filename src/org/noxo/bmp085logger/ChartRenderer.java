package org.noxo.bmp085logger;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

import org.noxo.bmp085logger.dao.Database;
import org.noxo.bmp085logger.dao.Database.MeasurementRange;
import org.noxo.bmp085logger.model.Pressure;
import org.noxo.bmp085logger.model.Temperature;

public class ChartRenderer extends AbstractHandler {

    private IntervalXYDataset createWeekPressureDataset() throws Exception {
        
        final TimeSeries series1 = new TimeSeries("Pressure", Day.class);
        List<Pressure> pressureList = Database.getPressureList(MeasurementRange.DAILY);
        
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        
        for (Pressure p : pressureList)
        {
        	Date date = formatter.parse(p.getFormattedTime());
        	cal.setTime(date);        	
        	Day day = new Day(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1 , cal.get(Calendar.YEAR));
        	series1.add(day, p.getValue());
        }
       
        cal.setTime(new Date());
        
        // set days without measurement to zero
        
        for (int i=0;i<15;i++)
        {
        	Day day = new Day(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1 , cal.get(Calendar.YEAR));
        	if (series1.getValue(day) == null)
        		series1.add(day, 0);
        	cal.roll(Calendar.DAY_OF_YEAR, -1);
        }
        
        final TimeSeriesCollection result = new TimeSeriesCollection(series1);
        return result;
 
    }
   
    private XYDataset createWeekTemperatureDataset() throws Exception {
 
        final TimeSeries series2 = new TimeSeries("Temperature", Day.class);
        
        List<Temperature> temperatureList = Database.getTemperatureList(MeasurementRange.DAILY);
        
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        
        for (Temperature t : temperatureList)
        {
        	Date date = formatter.parse(t.getFormattedTime());
           	cal.setTime(date);        	
        	Day day = new Day(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1 , cal.get(Calendar.YEAR));
         	series2.add(day, t.getValue());
        }
        
        cal.setTime(new Date());
        
        // set days without measurement to zero
        
        for (int i=0;i<15;i++)
        {
        	Day day = new Day(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1 , cal.get(Calendar.YEAR));
        	if (series2.getValue(day) == null)
        		series2.add(day, 0);
        	cal.roll(Calendar.DAY_OF_YEAR, -1);
        }
        
        final TimeSeriesCollection result = new TimeSeriesCollection(series2);
        result.setXPosition(TimePeriodAnchor.MIDDLE);
        return result;
 
    }
 
    private XYDataset createHourTemperatureDataset() throws Exception {
    	
        final TimeSeries series2 = new TimeSeries("Temperature", Hour.class);
        List<Temperature> temperatureList = Database.getTemperatureList(MeasurementRange.HOURLY);
        Calendar cal = Calendar.getInstance();
        
        int hourRolls = 0;
        
        while (true)
        {
        	int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        	Day day = new Day(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1 , cal.get(Calendar.YEAR));
        	Hour hour = new Hour(currentHour, day);
        	
        	for (Temperature t : temperatureList)
            {
        		if (Integer.parseInt(t.getFormattedTime()) == currentHour)
        		{
        			series2.add(hour, t.getValue());
        			break;
        		}
            }
        	
        	// no value recorded from sensor, use zero 
        	if (series2.getValue(hour) == null)
        		series2.add(hour, 0);
        	
        	cal.roll(Calendar.HOUR_OF_DAY, -1);
        	
        	if (hourRolls++ > 20)
        	{
        		break;
        	}
        	
        }
        
        final TimeSeriesCollection result = new TimeSeriesCollection(series2);
        result.setXPosition(TimePeriodAnchor.MIDDLE);
        return result;
 
    }
    
   private IntervalXYDataset createHourlyPressureDataset() throws Exception {
        
        final TimeSeries series1 = new TimeSeries("Pressure", Hour.class);
        List<Pressure> pressureList = Database.getPressureList(MeasurementRange.HOURLY);
        Calendar cal = Calendar.getInstance();
        
        for (int i=0;i<23;i++)
        {
        	int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        	
        	Day day = new Day(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1 , cal.get(Calendar.YEAR));
        	Hour hour = new Hour(currentHour, day);
        	
        	for (Pressure p : pressureList)
            {
        		if (Integer.parseInt(p.getFormattedTime()) == currentHour)
        		{
        			series1.add(hour, p.getValue());
        			break;
        		}
            }
        	
        	// no value recorded from sensor, use zero 
        	if (series1.getValue(hour) == null)
        		series1.add(hour, 0);
        	
        	cal.roll(Calendar.HOUR_OF_DAY, -1);
        }
        
       
       
        final TimeSeriesCollection result = new TimeSeriesCollection(series1);
        return result;
 
    }
   
    @Override
    public void handle(String arg0, Request request, HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) throws IOException, ServletException {
 
        // http://www.javaworld.com/article/2072527/generating-a-climograph-with-jfreechart.html?page=2
        // http://www.java2s.com/Code/Java/Chart/JFreeChartOverlaidXYPlotDemo2.htm
 
        OutputStream out = servletResponse.getOutputStream(); 
        String rangeParam = request.getParameter("range");
        boolean hourChart = rangeParam != null && rangeParam.equals("hourly");
    
        try {
 
            final DateAxis domainAxis = new DateAxis(hourChart ? "Time" : "Date");
            domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

            final ValueAxis pressureRangeAxis = new NumberAxis("Pressure (hPa)");
            pressureRangeAxis.setRange(1000, 1030);
            
            // #1 create pressure plot
            
            final IntervalXYDataset pressureData = hourChart ? createHourlyPressureDataset() : createWeekPressureDataset();
            final XYBarRenderer pressureRenderer = new XYBarRenderer(0.40);
            pressureRenderer.setShadowVisible(false);
            pressureRenderer.setDrawBarOutline(false);
            pressureRenderer.setSeriesPaint(0, new GradientPaint(0.0F, 0.0F, Color.GRAY, 0.0F, 0.0F, Color.LIGHT_GRAY));

            final XYPlot plot = new XYPlot(pressureData, domainAxis, pressureRangeAxis,
                    pressureRenderer);
             
            final ValueAxis temperatureRangeAxis = new NumberAxis("Temperature (°C)");
            temperatureRangeAxis.setRange(0, 35);

            // #2 create temperature plot
            
            final XYDataset temperatureData = hourChart ? createHourTemperatureDataset() : createWeekTemperatureDataset();
            final XYItemRenderer temperatureRenderer = new StandardXYItemRenderer();
            
            temperatureRenderer.setStroke(new BasicStroke(3));
            temperatureRenderer.setSeriesPaint(0, new Color(0xff,0x00,0x80));
             
            plot.setDataset(1, temperatureData);
            plot.setRenderer(1, temperatureRenderer);
            plot.setRangeAxis(1, temperatureRangeAxis);
            
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
            plot.setOrientation(PlotOrientation.VERTICAL);
 
            plot.mapDatasetToRangeAxis(1, 1);
            servletResponse.setContentType("image/png");
           
            JFreeChart mychart = new JFreeChart(null, null, plot, true);
            mychart.setBackgroundPaint(Color.WHITE);
            
            ChartUtilities.writeChartAsPNG(out, mychart, 600, 180);
 
        } catch (Exception e) {
            System.err.println(e.toString()); /* Throw exceptions to log files */
        } finally {
            out.close();/* Close the output stream */
        }
 
    }

}
