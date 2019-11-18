
package com.zjs;

import com.zjs.toolbox.Toolbox;

/** column
 * r000000
 * o000000
 * w000000
 * @author ZhouJingsen
 */
public class Matrix implements Cloneable{
    public double[][] content;
    public static final int ROW=1,COLUMN=0;
    /**
     * Create a matrix with the given row and column number.
     * 
     * @param row
     * @param column
     * @param random Whether to initialize this matrix with random value
     *      between 0 and 1. If set to false, all the elements in this matrix
     *      will be 0.
     */
    public Matrix(int row,int column,boolean random){
        content=new double[row][column];
        if(random)
            for(int i=0;i<row();i++)
                for(int j=0;j<column();j++)
                    content[i][j]=Math.random();
    }
    /**
     * Create a matrix according to the given array.
     * 
     * @param content content[i][j] represents the element in row i column j.
     */
    public Matrix(double[][] content){
        this.content=content;
    }
    /**
     * Create a matrix with only one given element.
     * 
     * @param content content[i][j] represents the element in row i column j.
     */
    public Matrix(double content){
        this.content=new double[1][1];
        this.content[0][0]=content;
    }
    /**
     * Multiply two matrix.
     * 
     * @param m1
     * @param m2
     * @return 
     */
    public static Matrix multiply(Matrix m1,Matrix m2){
        if(m1.column()!=m2.row())
            throw new IllegalArgumentException("Cannot multiply matrixs.");
        double[][] result=new double[m1.row()][m2.column()];
        for(int i=0;i<m1.row();i++)
            for(int j=0;j<m2.column();j++)
                for(int k=0;k<m1.column();k++)
                    result[i][j]+=m1.content[i][k]*m2.content[k][j];
        return new Matrix(result);
    }
    /**
     * Multiply every element in matrix with given number.
     * 
     * @param d
     * @param m
     * @return 
     */
    public static Matrix multiply(double d,Matrix m){
        Matrix cm=null;
        try{
            cm=m.clone();
        }catch(CloneNotSupportedException e){
            Toolbox.ExceptionHandler(e);
            System.exit(1);
        }
        for(int i=0;i<cm.row();i++)
            for(int j=0;j<cm.column();j++)
                cm.content[i][j]*=d;
        return cm;
    }
    /**
     * Add two matrix.
     * 
     * @param m1
     * @param m2
     * @return 
     */
    public static Matrix add(Matrix m1,Matrix m2){
        if(m1.row()!=m2.row()||
                m1.column()!=m2.column())
            throw new IllegalArgumentException("Cannot add matrixs.");
        double[][] result=new double[m1.row()][m1.column()];
        for(int i=0;i<m1.row();i++)
            for(int j=0;j<m1.column();j++)
                result[i][j]=m1.content[i][j]+m2.content[i][j];
        return new Matrix(result);
    }
    /**
     * Sunstract two matrix.
     * 
     * @param m1
     * @param m2
     * @return 
     */
    public static Matrix substract(Matrix m1,Matrix m2){
        Matrix nm2=null;
        try{
            nm2=m2.clone();
        }catch(CloneNotSupportedException e){
            Toolbox.ExceptionHandler(e);
            System.exit(1);
        }
        for(int i=0;i<nm2.row();i++)
            for(int j=0;j<nm2.column();j++)
                nm2.content[i][j]=-nm2.content[i][j];
        return add(m1,nm2);
    }
    /**
     * Get the transition of this matrix.
   ``* 
     * @return 
     */
    public Matrix T(){
        Matrix result=new Matrix(column(),row(),false);
        for(int i=0;i<row();i++)
            for(int j=0;j<column();j++)
                result.content[j][i]=content[i][j];
        return result;
    }
    /**
     * Get the String representation of this matrix.
   ``* 
     * @return 
     */
    @Override
    public String toString(){
        String result="";
        for(int i=0;i<row();i++){
            result=result+"\n";
            for(int j=0;j<column();j++)
                result=result+content[i][j]+",";
        }
        return result;
    }
    /**
     * "Compress" this matrix to 1 dimension via adding elements up.
   ``* 
     * @param type The type of result matrix.ROW or COLUMN.
     * @return 
     */
    public Matrix sum(int type){
        double[][] result=null;
        double temp=0;
        switch (type) {
            case ROW:
                result=new double[row()][1];
                for(int i=0;i<row();i++){
                    for(int j=0;j<column();j++)
                        temp+=content[i][j];
                    result[i][0]=temp;
                    temp=0;
                }
                break;
            case COLUMN:
                result=new double[1][column()];
                for(int i=0;i<column();i++){
                    for(int j=0;j<row();j++)
                        temp+=content[j][i];
                    result[0][i]=temp;
                    temp=0;
                }
                break;
            default:
                throw new IllegalArgumentException("type must be ROW or COLUMN");
        }
        return new Matrix(result);
    }
    /**
     * Get the absolute matrix of a matrix.
     * 
     * @param m
     * @return 
     */
    public static Matrix abs(Matrix m){
        Matrix cm=null;
        try{
            cm=m.clone();
        }catch(CloneNotSupportedException e){
            Toolbox.ExceptionHandler(e);
            System.exit(1);
        }
        for(int i=0;i<cm.row();i++)
            for(int j=0;j<cm.column();j++)
                cm.content[i][j]=Math.abs(cm.content[i][j]);
        return cm;
    }
    /**
     * Divide all the elements in the matrix by the given double.
     * 
     * @param m
     * @param d
     * @return 
     */
    public static Matrix divide(Matrix m,double d){
        return Matrix.multiply(1/d, m);
    }
    /**
     * Get the row number of this matrix.
     * @return 
     */
    public final int row(){
        return content.length;
    }
    /**
     * Get the column number of this matrix.
     * @return 
     */
    public final int column(){
        return content[0].length;
    }
    /**
     * Expand a row or column matrix to a full matrix.
     * Or expand a single element matrix to a row/column matrix.
     * 
     * @param m
     * @param type which dimension will be expanded.
     * @param num expand times.1 will expand the dimension to 2.
     * @return 
     */
    public static Matrix expand(Matrix m,int type,int num){
        if(num<0)
            throw new IllegalArgumentException("num can't be negative");
        double[][] replace=null;
        switch(type){
            case ROW:
                if(m.column()!=1)
                    throw new UnsupportedOperationException("Expanding row dimension requires column matrix");
                replace=new double[m.row()][num+1];
                for(int i=0;i<num+1;i++)
                    for(int j=0;j<m.row();j++)
                        replace[j][i]=m.content[j][0];
                break;
            case COLUMN:
                if(m.row()!=1)
                    throw new UnsupportedOperationException("Expanding column dimension requires row matrix");
                replace=new double[num+1][m.column()];
                for(int i=0;i<num+1;i++)
                    for(int j=0;j<m.column();j++)
                        replace[i][j]=m.content[0][j];
                break;
            default:
                throw new IllegalArgumentException("type must be ROW or COLUMN");
        }
        return new Matrix(replace);
    }
    @Override
    public Matrix clone() throws CloneNotSupportedException{
        super.clone();
        Matrix m=new Matrix(row(),column(),false);
        for(int i=0;i<row();i++)
            for(int j=0;j<column();j++)
                m.content[i][j]=content[i][j];
        return m;
    }
}
