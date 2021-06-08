package com.qunar.im.base.jsonbean;

import com.qunar.im.base.structs.FuncButtonDesc;

import java.util.List;
import java.util.Map;

/**
 * Created by saber on 15-12-31.
 */
public class CapabilityResult extends BaseJsonResult{
    public int version;
    public boolean exists;
    public String company;
    public Ability ability;
    public Map<String,Object> otherconfig;
    public List<FuncButtonDesc> trdextendmsg;

    public static class Ability
    {
        public List<String> base;
        public List<String> group;
    }
}
