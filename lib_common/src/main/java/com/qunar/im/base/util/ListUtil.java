package com.qunar.im.base.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingchao.song on 7/20/2015.
 */
public class ListUtil {
    public interface ListFilter<Z>{
        boolean accept(Z source);
    }

    public static <T> List<T> filter(List<T> fromList , ListFilter<T> filter){
    ArrayList<T> toList = new ArrayList<T>();
        for(int i=0;i<fromList.size();i++){
            if(filter.accept(fromList.get(i)))
            {
                toList.add(fromList.get(i));
            }
        }
        return toList;
    }

    public static <T> void toArray(List<T> list,T[] ts)
    {

    }

    /**
     * list是否为空
     *
     * @param list
     * @return
     */
    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }
}
