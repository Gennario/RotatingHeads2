package cz.gennario.newrotatingheads.utils;

import com.comphenix.protocol.wrappers.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringReader {

    private String string;
    private String mainValue;
    private List<String> dataList;

    public StringReader(String string) {
        this.string = string;
        this.mainValue = "none";
        this.dataList = new ArrayList<>();
    }

    public boolean convert() {
        if(string.contains("(") && string.endsWith(")") && !string.contains("()")) {
            String[] split = string.split("\\(");
            mainValue = split[0];
            if(split[1].replace(")", "").contains(",")) {
                dataList.addAll(Arrays.asList(split[1].replace(")", "").split(",")));
            }else {
                dataList.add(split[1].replace(")", ""));
            }
            return true;
        }else {
            return false;
        }
    }

    public String getDataByValue(String value) {
        for(String data : dataList) {
            if(data.contains(value)) {
                return data;
            }
        }
        return null;
    }

    public Pair<String, Integer> getIntFromData(String data) {
        if(data == null) return null;
        String[] split = data.split("=");
        if(split.length < 2) return null;
        return new Pair<>(split[0], Integer.parseInt(split[1]));
    }

    public Pair<String, Double> getDoubleFromData(String data) {
        if(data == null) return null;
        String[] split = data.split("=");
        if(split.length < 2) return null;
        return new Pair<>(split[0], Double.parseDouble(split[1]));
    }

    public Pair<String, Byte> getByteFromData(String data) {
        if(data == null) return null;
        String[] split = data.split("=");
        if(split.length < 2) return null;
        return new Pair<>(split[0], Byte.parseByte(split[1]));
    }

    public Pair<String, String> getStringFromData(String data) {
        if(data == null) return null;
        String[] split = data.split("=");
        if(split.length < 2) return null;
        return new Pair<>(split[0], split[1]);
    }

    public Pair<String, Boolean> getBooleanFromData(String data) {
        if(data == null) return null;
        String[] split = data.split("=");
        if(split.length < 2) return null;
        return new Pair<>(split[0], Boolean.parseBoolean(split[1]));
    }

    public Pair<String, List<String>> getStringListFromData(String data, String splitArg) {
        if(data == null) return null;
        String[] split = data.split("=");
        if(split.length < 2) return null;
        List<String> strings = new ArrayList<>(Arrays.asList(split[1].split(splitArg)));
        return new Pair<>(split[0], strings);
    }

    public Pair<String, List<Integer>> getIntListFromData(String data, String splitArg) {
        if(data == null) return null;
        String[] split = data.split("=");
        if(split.length < 2) return null;
        List<Integer> integers = new ArrayList<>();
        for(String s : split[1].split(splitArg)) {
            integers.add(Integer.parseInt(s));
        }
        return new Pair<>(split[0], integers);
    }

    public String getMainValue() {
        return mainValue;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public String getString() {
        return string;
    }
}