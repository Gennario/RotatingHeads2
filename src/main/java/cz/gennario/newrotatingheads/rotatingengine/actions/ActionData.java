package cz.gennario.newrotatingheads.rotatingengine.actions;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ActionData {

    private Map<String, Object> data;
    private Section section;

    public ActionData(Section section) {
        data = new HashMap<>();
        this.section = section;

        for (String key : section.getRoutesAsStrings(false)) {
            data.put(key, section.get(key));
        }
    }

    public boolean isExist(String key) {
        return data.containsKey(key);
    }

    public String getString(String key) {
        return String.valueOf(data.get(key));
    }

    public String getString(String key, String def) {
        return isExist(key) ? String.valueOf(data.get(key)) : def;
    }


    public int getInt(String key) {
        return (int) data.get(key);
    }

    public int getInt(String key, int def) {
        return isExist(key) ? (int) data.get(key) : def;
    }


    public double getDouble(String key) {
        try {
            return (double) data.get(key);
        } catch (Exception e) {
            return (getInt(key));
        }
    }

    public double getDouble(String key, double def) {
        try {
            return isExist(key) ? (double) data.get(key) : def;
        } catch (Exception e) {
            return (getInt(key, (int) def));
        }
    }


    public float getFloat(String key) {
        try {
            return (float) data.get(key);
        } catch (Exception e) {
            return (getInt(key));
        }
    }

    public float getFloat(String key, float def) {
        try {
            return isExist(key) ? (float) data.get(key) : def;
        } catch (Exception e) {
            return (getInt(key, (int) def));
        }
    }

    public boolean getBoolean(String key) {
        return (boolean) data.get(key);
    }

    public boolean getBoolean(String key, boolean def) {
        return isExist(key) ? (boolean) data.get(key) : def;
    }


    public List<Object> getList(String key) {
        return (List<Object>) data.get(key);
    }

    public List<String> getListString(String key) {
        return (List<String>) data.get(key);
    }

    public Object getCustom(String path) {
        return section.get(path);
    }

    public String getCustomString(String path) {
        return section.getString(path);
    }

    public double getCustomDouble(String path) {
        return section.getDouble(path);
    }

    public int getCustomInt(String path) {
        return section.getInt(path);
    }

}
