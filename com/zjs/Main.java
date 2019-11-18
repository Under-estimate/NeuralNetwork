
package com.zjs;

import com.zjs.toolbox.Toolbox;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author ZhouJingsen
 */
public class Main {
    static final double[] DIVH={100,100,30,1,1,10,100,20,30,800,30,400,40,50};
    static final double[] DIVI={10,5,10,5};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Configure train data generator
        DataGenerator Iris=new DataGenerator(){
            Scanner s=null;
            int times=0;
            @Override
            public double[][] getData() {
                if(s==null)initScanner();
                if(times>=150)initScanner();
                double[][] ret=new double[1][4];
                result=new double[1][3];
                String[] dat=s.next().split(",");
                for(int i=0;i<1;i++){
                    for(int j=0;j<4;j++){
                        ret[i][j]=Double.parseDouble(dat[j])/DIVI[j];
                    }
                    result[i][0]=(dat[4].equalsIgnoreCase("Iris-setosa"))?1.0:0.0;
                    result[i][1]=(dat[4].equalsIgnoreCase("Iris-versicolor"))?1.0:0.0;
                    result[i][2]=(dat[4].equalsIgnoreCase("Iris-virginica"))?1.0:0.0;
                }
                times+=1;
                return ret;
            }
            public void initScanner(){
                if(s!=null)s.close();
                times=0;
                try{
                    s=new Scanner(new FileInputStream("D:\\APPS\\神经网络基本原理简明教程\\bezdekIris.data"));
                }catch(Exception e){
                    Toolbox.ExceptionHandler(e);
                }
            }
        };
        
        DataGenerator House=new DataGenerator(){
            Scanner s=null;
            int times=0;
            @Override
            public double[][] getData() {
                if(s==null)initScanner();
                if(times>=501)initScanner();
                double[][] ret=new double[5][13];
                result=new double[5][1];
                for(int k=0;k<5;k++){
                    for(int i=0;i<13;i++){
                        ret[k][i]=s.nextDouble()/DIVH[i];
                    }
                    result[k][0]=s.nextDouble()/DIVH[13];
                }
                times+=5;
                return ret;
            }
            public void initScanner(){
                if(s!=null)s.close();
                times=0;
                try{
                    s=new Scanner(new FileInputStream("D:\\APPS\\神经网络基本原理简明教程\\housing.data"));
                }catch(Exception e){
                    Toolbox.ExceptionHandler(e);
                }
            }
        };
        //configure visualizer
        Visualizer.VisualConstraints vc=new Visualizer.VisualConstraints();
        vc.Xmagnify=3;
        vc.maxMemory=300;
        vc.maxValue=0.5;
        vc.Ymagnify=1200;
        vc.targetValue=0.005;
        Visualizer v=new Visualizer(vc);
        v.setVisible(true);
        v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        String ss[]={"Iris data set","Housing data set"};
        String cho=(String)JOptionPane.showInputDialog(v,"Choose a data set:","Select data set...",JOptionPane.QUESTION_MESSAGE,null,ss,"Housing data set");
        if(cho==null)System.exit(0);
        SNN s;
        if(cho.equalsIgnoreCase(ss[0])){
            vc.targetValue=0.06;
            vc.Ymagnify=300;
            vc.maxValue=2;
            double[][] sh=new double[1][4];
            sh[0]=DIVI;
            //configure neuron network
            s=new SNN(4,3,1,ActiveFunction.Softmax);
            //start train & present result
            if(s.train(Integer.MAX_VALUE, 0.06, v, Iris,0))JOptionPane.showMessageDialog(v, 
                    "Success\nWeight matrix:"+s.w.toString()
                            +"\nbias matrix:"+s.b.toString()
                            +"\ninput divided:"+new Matrix(sh).toString(), "Success", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(v, "Fail","Fail",JOptionPane.INFORMATION_MESSAGE);
            
        }else{
            double[][] sh=new double[1][13];
            sh[0]=Arrays.copyOf(DIVH,13);
            //configure neuron network
            s=new SNN(13,1,5,ActiveFunction.Default);
            //start train & present result
            if(s.train(10000,0.005, v, House,10))JOptionPane.showMessageDialog(v, 
                    "Success\nWeight matrix:"+s.w.toString()
                            +"\nbias matrix:"+s.b.toString()
                            +"\ninput divided:"+new Matrix(sh).toString()
                            +"\noutput divided:"+DIVH[13], "Success", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(v, "Fail","Fail",JOptionPane.INFORMATION_MESSAGE);
        }
        System.exit(0);
    }
}
