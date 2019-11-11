package com.qunar.im.ui.entity;

import android.text.TextUtils;

/**
     * 导航配置
     */
    public class NavConfigInfo {
        private String name;//配置名称
        private String url;//配置地址
        private boolean isSelected;//是否被选中

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null) return false;
            if(getClass() != o.getClass()) return false;
            NavConfigInfo info = (NavConfigInfo) o;
            if(TextUtils.isEmpty(name)
                    || TextUtils.isEmpty(info.getName()))
                return false;
            if(info.getName().equals(name)){
                return true;
            }
            return false;
        }
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.name.hashCode();
            result = prime * result + this.name.hashCode();
            return result;
        }
    }