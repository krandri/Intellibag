package com.example.kevin.intellibag;



/**
 * Created by Kevin on 20/05/2016.
 */
public abstract class Functionality {
    private String funcName;
    private String txtFunc;
    private double funcValue;

    public Functionality(final String name, final String text, final double val)
    {
        this.funcName = name;
        this.txtFunc = text;
        this.funcValue = val;
    }

    public double getFuncValue() {
        return funcValue;
    }

    public void setFuncValue(double funcValue) {
        this.funcValue = funcValue;
    }

    public String getTxtFunc() {
        return txtFunc;
    }

    public void setTxtFunc(String txtFunc) {
        this.txtFunc = txtFunc;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

}
