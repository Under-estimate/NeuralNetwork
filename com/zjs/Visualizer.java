package com.zjs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFrame;

/**
 * Visualize the training process.
 * Only used in developing phase.
 * 
 * @author ZhouJingsen
 */
public class Visualizer extends JFrame{
    ArrayList<Double> Data;
    VisualConstraints settings;
    Object lock;
    double average=Double.NaN;
    public Visualizer(VisualConstraints settings){
        super();
        this.settings=settings;
        lock=new Object();
        settings.lock=lock;
        setSize(settings.getFrameWidth(),settings.getFrameHeight());
        Data=new ArrayList();
    }
    public void input(double data){
        //Synchorized to prevent concurrent modification.
        synchronized(lock){
            while(true){
                //Remove the first data if owning too many data.
                if(Data.size()>=settings.maxMemory*0.8)Data.remove(0);
                else break;
            }
            Data.add(data);
        }
    }
    @Override
    public void paint(Graphics g){
        Graphics2D g2=(Graphics2D)g;
        settings.g=g2;
        average=settings.drawData(Data);
        if(Data.size()<=settings.maxMemory*0.75)average=Double.NaN;
    }
    public static class VisualConstraints{
        /**
         * The y axis magnify times of data.
         */
        public int Ymagnify;
        /**
         * The x axis magnify times of data.
         * Default settings is one pixel for one data.
         */
        public int Xmagnify;
        /**
         * Will draw a red line to represent.
         */
        public double targetValue;
        /**
         * The max displayed value.
         */
        public double maxValue;
        /**
         * The max data that will be draw at same time.
         */
        public int maxMemory;
        public Object lock;
        public Graphics2D g;
        public int getFrameHeight(){
            return (int)(20+Ymagnify*maxValue);
        }
        public int getFrameWidth(){
            return 10+Xmagnify*maxMemory;
        }
        public double convert(double value){
            return maxValue*Ymagnify-value;
        }
        /**
         * Draw the diagram.
         */
        public double drawData(ArrayList<Double> data){
            //Synchorized to prevent concurrent modification.
            synchronized(lock){
                g.setColor(Color.black);
                g.fillRect(0,0,Xmagnify*maxMemory,(int)(Ymagnify*maxValue));
                g.drawLine(Xmagnify*maxMemory,0,Xmagnify*maxMemory,(int)(Ymagnify*maxValue));
                g.setColor(Color.green);
                g.drawLine(0,(int)convert(Ymagnify*targetValue), Xmagnify*maxMemory,(int)convert(Ymagnify*targetValue));
                g.setColor(Color.cyan);
                if(data.size()<=1)return Double.NaN;
                Iterator<Double> dataIterator=data.iterator();
                double former=dataIterator.next();
                double latter;
                int xpos=0;
                double sum=0;
                double max=0;
                max=Math.max(max,former);
                sum+=former;
                while(dataIterator.hasNext()){
                    latter=dataIterator.next();
                    sum+=latter;
                    max=Math.max(latter, max);
                    g.drawLine((int)(xpos*Xmagnify), (int)convert(former*Ymagnify), (int)((xpos+1)*Xmagnify), (int)convert(latter*Ymagnify));
                    former=latter;
                    xpos++;
                }
                g.setColor(Color.RED);
                g.drawLine(0, (int)convert(max*Ymagnify), Xmagnify*maxMemory,(int)convert(max*Ymagnify));
                g.setColor(Color.magenta);
                g.drawLine(0, (int)convert(sum*Ymagnify/data.size()), Xmagnify*maxMemory,(int)convert(sum*Ymagnify/data.size()));
                g.setFont(new Font("Arial",Font.PLAIN,10));
                g.drawString("AVERAGE VALUE IN PAGE:  "+Double.toString(sum/data.size()),10,50);
                g.setColor(Color.cyan);
                g.drawString("LAST VALUE:                         "+Double.toString(data.get(data.size()-1)),10,40);
                g.setColor(Color.red);
                g.drawString("MAX VALUE IN PAGE:           "+Double.toString(max),10,70);
                g.setColor(Color.green);
                g.drawString("TARGET LOSS VALUE:         "+Double.toString(targetValue), 10,60);
                return sum/data.size();
            }
        }
    }
}
