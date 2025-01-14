package com.pi4j.component.lcd;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  LCDBase.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2024 Pi4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.UnsupportedEncodingException;
import com.pi4j.component.ComponentBase;
import com.pi4j.util.StringUtil;

public abstract class LCDBase extends ComponentBase implements LCD {

    @Override
    public abstract int getRowCount();

    @Override
    public abstract int getColumnCount();

    @Override
    public void clear() {
	for(int i=0; i<getRowCount(); i++) {
		clear(i,0,getColumnCount());
	}
    }

    @Override
    public void clear(int row) {
        validateRowIndex(row);
        clear(row,0,getColumnCount());
    }

    @Override
    public void clear(int row, int column, int length){
        StringBuilder sb = new StringBuilder(getColumnCount());
        int maxLen = getColumnCount() - column; //Maximum length you can write spaces
        for(int index = 0; (index < length && index < maxLen); index++) {
            sb.append(" ");
        }
        write(row, column, sb.toString());
    }

    @Override
    public void setCursorHome() {
        setCursorPosition(0);
    }

    @Override
    public void setCursorPosition(int row) {
        validateRowIndex(row);
        setCursorPosition(row, 0);
    }

    @Override
    public abstract void setCursorPosition(int row, int column);

    @Override
    public void write(String data) {
        try{
            write(data.getBytes("UTF-8"));
         }
        catch(UnsupportedEncodingException e){
            throw new RuntimeException(e);
         }
    }

    @Override
    public void write(String data, Object...arguments) {
        write(String.format(data, arguments));
    }

    @Override
    public void write(char[] data) {
        for(char c : data)
            write(c);
    }

    @Override
    public void write(byte[] data) {
        for(byte b : data)
            write(b);
    }

    @Override
    public void write(char data) {
        write((byte)data);
    }

    @Override
    public abstract void write(byte data);

    @Override
    public void write(int row, String data) {
        write(row, 0, data);
    }

    @Override
    public void write(int row, String data, LCDTextAlignment alignment) {
        int columnIndex = 0;
        if(alignment != LCDTextAlignment.ALIGN_LEFT && data.length() < getColumnCount()){
            int remaining = getColumnCount() - data.length();
            if(alignment == LCDTextAlignment.ALIGN_RIGHT) {
                columnIndex = remaining;
            }
            else if(alignment == LCDTextAlignment.ALIGN_CENTER) {
                columnIndex = (remaining/2);
            }
        }
        write(row, columnIndex, data);
    }

    @Override
    public void write(int row, String data, Object...arguments) {
        write(row, 0, data, arguments);
    }

    @Override
    public void write(int row, String data, LCDTextAlignment alignment, Object...arguments) {
        write(row, String.format(data, arguments), alignment);
    }

    @Override
    public void write(int row, char[] data) {
        write(row, 0, data);
    }

    @Override
    public void write(int row, byte[] data) {
        write(row, 0, data);
    }

    @Override
    public void write(int row, char data) {
        write(row, 0, data);
    }

    @Override
    public void write(int row, byte data) {
        write(row, 0, data);
    }

    @Override
    public void write(int row, int column, String data){
        validateCoordinates(row, column);
        setCursorPosition(row, column);
        write(data);
    }

    @Override
    public void write(int row, int column, String data, Object...arguments){
        validateCoordinates(row, column);
        setCursorPosition(row, column);
        write(data, arguments);
    }

    @Override
    public void write(int row, int column, char[] data) {
        validateCoordinates(row, column);
        setCursorPosition(row, column);
        write(data);
    }

    @Override
    public void write(int row, int column, byte[] data) {
        validateCoordinates(row, column);
        setCursorPosition(row, column);
        write(data);
    }

    @Override
    public void write(int row, int column, char data) {
        validateCoordinates(row, column);
        setCursorPosition(row, column);
        write(data);
    }

    @Override
    public void write(int row, int column, byte data) {
        validateCoordinates(row, column);
        setCursorPosition(row, column);
        write(data);
    }

    @Override
    public void writeln(int row, String data) {
        writeln(row, data, LCDTextAlignment.ALIGN_LEFT);
    }

    @Override
    public void writeln(int row, String data, LCDTextAlignment alignment) {
        String result = data;
        if(data.length() < this.getColumnCount()){
            if(alignment == LCDTextAlignment.ALIGN_LEFT)
                result = StringUtil.padRight(data, (getColumnCount() - data.length()));
            else if(alignment == LCDTextAlignment.ALIGN_RIGHT)
                result = StringUtil.padLeft(data, (getColumnCount() - data.length()));
            else if(alignment == LCDTextAlignment.ALIGN_CENTER)
                result = StringUtil.padCenter(data, getColumnCount());
        }
        write(row, 0, result);
    }

    @Override
    public void writeln(int row, String data, Object...arguments) {
        writeln(row, String.format(data, arguments));
    }

    @Override
    public void writeln(int row, String data, LCDTextAlignment alignment, Object...arguments) {
        writeln(row, String.format(data, arguments), alignment);
    }

    protected void validateCoordinates(int row, int column) {
        validateRowIndex(row);
        validateColumnIndex(column);
    }

    protected void validateRowIndex(int row) {
        if(row >= getRowCount() || row < 0)
            throw new RuntimeException("Invalid row index.");
    }

    protected void validateColumnIndex(int column) {
        if(column >= getColumnCount() || column < 0)
            throw new RuntimeException("Invalid column index.");
    }
}
