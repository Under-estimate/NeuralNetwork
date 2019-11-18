
package com.zjs;

/**
 * Simple Neuron Network.
 * 
 * @author ZhouJingsen
 */
public class SNN {
    /**
     * Learning rate.
     */
    static final double ETA=0.1;
    /**
     * Network arguments.
     */
    Matrix x,y,z,b,w,dz,dw,db;
    /**
     * Batch size.
     */
    int size;
    /**
     * Active function type.
     */
    ActiveFunction type;
    /**
     * Create a simple neruon network.
     * 
     * @param in input count
     * @param out output count
     * @param batchsize data size per batch
     * @param type Active function type
     */
    public SNN(int in,int out,int batchsize,ActiveFunction type){
        w=new Matrix(in,out,true);
        b=new Matrix(1,out,true);
        x=new Matrix(batchsize,in,false);
        y=new Matrix(batchsize,out,false);
        z=new Matrix(batchsize,out,false);
        dz=new Matrix(batchsize,out,false);
        dw=new Matrix(in,out,false);
        db=new Matrix(1,out,false);
        size=batchsize;
        this.type=type;
    }
    /**
     * Forward calculation.
     * Z contains all the training samples' calculated results.
     * Updates:z
     * Uses:x,w
     */
    public void forward(){
        z=Matrix.multiply(x,w);
        Matrix eb=Matrix.expand(b, Matrix.COLUMN,size-1);
        z=Matrix.add(z,eb);
        
        if(type==ActiveFunction.Logistic){
            for(int i=0;i<size;i++)
                for(int j=0;j<z.column();j++)
                    z.content[i][j]=1.0/(1.0+Math.exp(-z.content[i][j]));
        }else if(type==ActiveFunction.Softmax){
            double max=-Double.MAX_VALUE;
            for(int i=0;i<z.column();i++){
                max=Math.max(z.content[0][i], max);
            }
            double sum=0;
            for(int i=0;i<z.column();i++){
                sum+=Math.exp(z.content[0][i]-max);
            }
            for(int i=0;i<z.column();i++){
                z.content[0][i]=Math.exp(z.content[0][i]-max)/sum;
            }
        }else if(type!=ActiveFunction.Default){
            throw new IllegalArgumentException("Active function type not found.");
        }
    }
    /**
     * Backward calculation.
     * dz contains all the training samples' calculated results.
     * Updates:dz,db,dw
     * Uses:x,y,z
     */
    public void feedback(){
        dz=Matrix.substract(z, y);
        db=Matrix.divide(dz.sum(Matrix.COLUMN),size);
        dw=Matrix.divide(Matrix.multiply(x.T(), dz),size);
    }
    /**
     * Update weights.
     * Updates:w,b
     * Uses:dw,db
     */
    public void update(){
        w=Matrix.substract(w, Matrix.multiply(ETA, dw));
        b=Matrix.substract(b, Matrix.multiply(ETA, db));
    }
    /**
     * "Standard" loss.
     * Simply z-y;
     * 
     * @return calculated loss.
     */
    public double stdLoss(){
        return Matrix.abs(dz).sum(Matrix.COLUMN)
                .sum(Matrix.ROW).content[0][0]/size;
    }
    /**
     * Average Variannce loss.
     * (z-y)/2
     * 
     * @return calculated loss.
     */
    public double AvarLoss(){
        double sum=0.0;
        for(int i=0;i<z.row();i++)
            for(int j=0;j<z.column();j++)
                sum+=Math.pow(z.content[i][j]-y.content[i][j],2);
        return sum/(2*size);
    }
    /**
     * Cross Quotient loss.
     * -[ylnz+(1-y)ln(1-z)]
     * 
     * @return calculated loss.
     */
    public double CQuoLoss(){
        double sum=0.0;
        for(int i=0;i<z.row();i++)
            for(int j=0;j<z.column();j++)
                sum+=-Math.log(y.content[i][j]*Math.log(z.content[i][j])+(1-y.content[i][j]*Math.log(1-z.content[i][j])));
        return sum/size;
    }
    /**
     * Multiple Cross Quotient loss.
     * -ylnz
     * @return calculated loss.
     */
    public double MCQuoLoss(){
        double sum=0.0;
        for(int i=0;i<z.row();i++)
            for(int j=0;j<z.column();j++)
                sum+=-y.content[i][j]*Math.log(z.content[i][j]);
        return sum/size;
    }
    /**
     * Automatically choose the required loss function by Active function type.
     * @return 
     */
    public double AutoLoss(){
        if(type==ActiveFunction.Logistic){
            return CQuoLoss();
        }else if(type==ActiveFunction.Softmax){
            return MCQuoLoss();
        }else if(type==ActiveFunction.Default){
            return AvarLoss();
        }else{
            throw new IllegalArgumentException("Active function type not found.");
        }
    }
    public boolean train(int maxCycle,double targetLoss,
            Visualizer v,DataGenerator data,int interval){
        int cycle=0;
        while(cycle<=maxCycle){
            x=new Matrix(data.getData());
            y=new Matrix(data.result);
            forward();
            feedback();
            update();
            v.input(AutoLoss());
            if(v.average<targetLoss)break;
            v.repaint();
            cycle++;
            try{Thread.sleep(interval);}catch(Exception e){}
        }
        return v.average<targetLoss;
    }
}
