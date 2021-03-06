package wtf.sdk;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

import java.util.ArrayList;
import java.util.List;

public final class JSO {

    private JsonValue _jv = Json.NULL;

    //@warning: the key is not in standard.
    protected static JsonObject ja2jo(JsonArray ja) {
        JsonObject rt = new JsonObject();
        if (null != ja) {
            int c = ja.size();
            //for (JsonValue value : ja)
            for (int i = 0; i < c; i++) {
                rt.set("" + i, ja.get(i));
            }
        }
        return rt;
    }

    //@warning: some meaning of the original json might changed.
    protected static JsonArray jo2ja(JsonObject jo) {
        JsonArray rt = new JsonArray();
        for (JsonObject.Member jo_mb : jo) {
            rt.add(jo_mb.getValue());
        }
        return rt;
    }

    //shallow merge
    public static JSO basicMerge(JSO... jsos) {
        JSO rt = new JSO();
        for (JSO temp : jsos) {
            rt.merge(temp);
        }
        return rt;
    }

    static public String o2s(JSO o) {
        return (o == null) ? null : o.toString();
    }

    final static public JSO s2o(String s) {
        JsonValue jv = null;
        try {
            if (s == null) jv = Json.NULL;
            else jv = Json.parse(s);
        } catch (ParseException ex) {
            //ex.printStackTrace();
            jv = Json.value(s);//if ParseException, regard as a "String" type only...
        }
        JSO jso = new JSO();
        jso.setValue(jv);
        return jso;
    }

    //暂时兼容旧的，稍后移除 TODO
    public static JSO valueOf(String s) {
        JSO jso = new JSO();
        jso.setValue(Json.value(s));
        return jso;
    }

    public static JSO id2o(String s) {
        JSO jso = new JSO();
        jso.setValue(Json.value(s));
        return jso;
    }

    public static JSO id2o(int s) {
        JSO jso = new JSO();
        jso.setValue(Json.value(s));
        return jso;
    }

    public static JSO id2o(double s) {
        JSO jso = new JSO();
        jso.setValue(Json.value(s));
        return jso;
    }

    public static JSO id2o(float s) {
        JSO jso = new JSO();
        jso.setValue(Json.value(s));
        return jso;
    }

    public static JSO id2o(long s) {
        JSO jso = new JSO();
        jso.setValue(Json.value(s));
        return jso;
    }

    public static JSO id2o(boolean s) {
        JSO jso = new JSO();
        jso.setValue(Json.value(s));
        return jso;
    }

    //@warning, the merge() will change the inner jv
    public void merge(JSO jso) {
        JsonValue jv2 = jso.getValue();
        if (_jv == null || _jv.isNull()) {
            if (jv2 instanceof JsonObject) {
                _jv = new JsonObject();
            } else if (jv2 instanceof JsonArray) {
                _jv = new JsonArray();
            } else {
                _jv = new JsonObject();
            }
        }
        if (_jv instanceof JsonObject) {
            if (jv2 instanceof JsonObject) {
                //JO<=JO
                JsonObject _jv2 = jv2.asObject();
                ((JsonObject) _jv).merge(_jv2);
            } else if (jv2 instanceof JsonArray) {
                //JO<= ja2jo(JA)
                ((JsonObject) _jv).merge(ja2jo((JsonArray) jv2));
            } else {
                //SKIP
            }
        } else if (_jv instanceof JsonArray) {
            if (jv2 instanceof JsonObject) {
                //JA<= jo2ja(JO)
                JsonArray _jv2 = jo2ja(jv2.asObject());
                int c = _jv2.size();
                for (int i = 0; i < c; i++) {
                    ((JsonArray) _jv).add(_jv2.get(i));
                }
            } else if (jv2 instanceof JsonArray) {
                JsonArray _jv2 = jv2.asArray();
                int c = _jv2.size();
                for (int i = 0; i < c; i++) {
                    ((JsonArray) _jv).add(_jv2.get(i));
                }
            } else {
                //SKIP
            }
        } else {
            //SKIP
        }
    }

    protected JsonValue getValue() {
        return _jv;
    }

    private void setValue(Object v) {
        if (v instanceof JsonValue) _jv = (JsonValue) v;
        else {
            if (v == null) _jv = Json.NULL;
            else {
                try {
                    _jv = Json.parse(v.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (null == _jv) _jv = Json.NULL;
            }
        }
    }

    public String toString() {
        return toString(false);
    }

    public String asString() {
        if (_jv != null) {
            try {
                return _jv.asString();
            } catch (UnsupportedOperationException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public ArrayList<JSO> asArrayList() {
        ArrayList<JSO> rt = new ArrayList<JSO>();
        if (_jv instanceof JsonArray) {
            JsonArray _jva = (JsonArray) _jv;
            int c = _jva.size();
            for (int i = 0; i < c; i++) {
                JSO jso = new JSO();
                jso.setValue(_jva.get(i));
                rt.add(jso);
            }
        }
        return rt;
    }

    public String toString(boolean quote) {
        if (_jv == null) return null;
        if (_jv.isNull()) {
            if (quote) {
                return _jv.toString();
            } else {
                return null;
            }
        }
        if (_jv.isString()) {
            if (quote) {
                return _jv.toString();
            } else {
                return _jv.asString();
            }
        } else {
            return _jv.toString();
        }
    }

    //NOTES: this is tricky or not very good here.... if the child is "json-string", will try convert....which not standard...
    public void setChild(String k, String childAsString) {
        this.setChild(k, JSO.id2o(childAsString));
    }

    public void setChild(String k, JSO chd) {
        if (_jv == null || _jv.isNull()) {
            _jv = Json.object();
        }
        if (null == chd) return;
        if (null == k) return;
        if (_jv instanceof JsonObject) {
            ((JsonObject) _jv).set(k, chd.getValue());
        }
    }

    //for JO
    public JSO getChild(String k) {
        JSO jso = new JSO();
        if (k != null && _jv instanceof JsonObject) {
            JsonValue jv = _jv.asObject().get(k);
            jso.setValue(jv);
        }
        return jso;
    }

    public List<String> getChildKeys() {
        if (_jv instanceof JsonObject) {
            return ((JsonObject) _jv).names();
        }
        return new ArrayList<String>();
    }

    public boolean isNull() {
        return _jv.isNull();
    }

}
