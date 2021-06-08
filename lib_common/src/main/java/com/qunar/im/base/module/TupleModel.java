package com.qunar.im.base.module;

/**
 * Created by saber on 15-9-23.
 */
public class TupleModel implements Comparable<TupleModel> {
    public String key;
    public String value;

    @Override
    public boolean equals(Object object) {
        return key != null && object != null && object instanceof TupleModel && key.equals(((TupleModel) object).key);
    }

    @Override
    public int compareTo(TupleModel another) {
        if(key!=null&&key.equals(another.key))
            return 0;
        else
            return 1;
    }
}
