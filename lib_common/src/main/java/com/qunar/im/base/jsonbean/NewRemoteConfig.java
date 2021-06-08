package com.qunar.im.base.jsonbean;

import java.util.List;

public class NewRemoteConfig {

    /**
     * ret : true
     * errcode : 0
     * errmsg : null
     * data : {"version":3,"clientConfigInfos":[{"key":"kStickJidDic","infos":[{"subkey":"xuejie.bi@ejabhost1-xuejie.bi@ejabhost1","configinfo":"0","isdel":0}]}]}
     */

    private boolean ret;
    private int errcode;
    private Object errmsg;
    private DataBean data;

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public Object getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(Object errmsg) {
        this.errmsg = errmsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * version : 3
         * clientConfigInfos : [{"key":"kStickJidDic","infos":[{"subkey":"xuejie.bi@ejabhost1-xuejie.bi@ejabhost1","configinfo":"0","isdel":0}]}]
         */

        private int version;
        private List<ClientConfigInfosBean> clientConfigInfos;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public List<ClientConfigInfosBean> getClientConfigInfos() {
            return clientConfigInfos;
        }

        public void setClientConfigInfos(List<ClientConfigInfosBean> clientConfigInfos) {
            this.clientConfigInfos = clientConfigInfos;
        }

        public static class ClientConfigInfosBean {
            /**
             * key : kStickJidDic
             * infos : [{"subkey":"xuejie.bi@ejabhost1-xuejie.bi@ejabhost1","configinfo":"0","isdel":0}]
             */

            private String key;
            private List<InfosBean> infos;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public List<InfosBean> getInfos() {
                return infos;
            }

            public void setInfos(List<InfosBean> infos) {
                this.infos = infos;
            }

            public static class InfosBean {
                /**
                 * subkey : xuejie.bi@ejabhost1-xuejie.bi@ejabhost1
                 * configinfo : 0
                 * isdel : 0
                 */

                private String subkey;
                private String configinfo;
                private int isdel;

                public String getSubkey() {
                    return subkey;
                }

                public void setSubkey(String subkey) {
                    this.subkey = subkey;
                }

                public String getConfiginfo() {
                    return configinfo;
                }

                public void setConfiginfo(String configinfo) {
                    this.configinfo = configinfo;
                }

                public int getIsdel() {
                    return isdel;
                }

                public void setIsdel(int isdel) {
                    this.isdel = isdel;
                }
            }
        }
    }
}
