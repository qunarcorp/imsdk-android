package com.qunar.im.base.jsonbean;

import java.util.List;

public class RbtNewSuggestionList {


    /**
     * question : qtalk是什么
     * content : qatlk是一款IM的聊天软件
     * listTips :
     * listArea : {"type":"list","style":{"defSize":3},"items":[{"text":"qtalk是什么","event":{"type":"interface","url":"","msgText":"qtalk是什么"}},{"text":"如何报销","event":{"type":"interface","url":"","msgText":"如何报销"}},{"text":"这是测试","event":{"type":"interface","url":"","msgText":"这是测试"}}]}
     * bottom_tips : aaa
     * bottom : [{"id":1,"text":"有用","url":"http://127.0.0.1:8025/qtalk_robot/yn_feedback?id=161&is_worked=1&rexian_id=qtalkrexian&m_from=qtalkrexian@ejabhost1&realfrom=hotline_robot@ejabhost1&m_to=hubin.hu@ejabhost1&realto=hubin.hu@ejabhost1"},{"id":0,"text":"没用","url":"http://127.0.0.1:8025/qtalk_robot/yn_feedback?id=161&is_worked=0&rexian_id=qtalkrexian&m_from=qtalkrexian@ejabhost1&realfrom=hotline_robot@ejabhost1&m_to=hubin.hu@ejabhost1&realto=hubin.hu@ejabhost1"}]
     * word_limit : {"words":100,"lines":6}
     */

    private String question;
    private String content;
    private String listTips;
    private ListAreaBean listArea;
    private String bottom_tips;
    private WordLimitBean word_limit;
    private List<BottomBean> bottom;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getListTips() {
        return listTips;
    }

    public void setListTips(String listTips) {
        this.listTips = listTips;
    }

    public ListAreaBean getListArea() {
        return listArea;
    }

    public void setListArea(ListAreaBean listArea) {
        this.listArea = listArea;
    }

    public String getBottom_tips() {
        return bottom_tips;
    }

    public void setBottom_tips(String bottom_tips) {
        this.bottom_tips = bottom_tips;
    }

    public WordLimitBean getWord_limit() {
        return word_limit;
    }

    public void setWord_limit(WordLimitBean word_limit) {
        this.word_limit = word_limit;
    }

    public List<BottomBean> getBottom() {
        return bottom;
    }

    public void setBottom(List<BottomBean> bottom) {
        this.bottom = bottom;
    }

    public static class ListAreaBean {
        /**
         * type : list
         * style : {"defSize":3}
         * items : [{"text":"qtalk是什么","event":{"type":"interface","url":"","msgText":"qtalk是什么"}},{"text":"如何报销","event":{"type":"interface","url":"","msgText":"如何报销"}},{"text":"这是测试","event":{"type":"interface","url":"","msgText":"这是测试"}}]
         */

        private String type;
        private StyleBean style;
        private List<ItemsBean> items;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public StyleBean getStyle() {
            return style;
        }

        public void setStyle(StyleBean style) {
            this.style = style;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public static class StyleBean {
            /**
             * defSize : 3
             */

            private int defSize;

            public int getDefSize() {
                return defSize;
            }

            public void setDefSize(int defSize) {
                this.defSize = defSize;
            }
        }

        public static class ItemsBean {
            /**
             * text : qtalk是什么
             * event : {"type":"interface","url":"","msgText":"qtalk是什么"}
             */

            private String text;
            private EventBean event;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public EventBean getEvent() {
                return event;
            }

            public void setEvent(EventBean event) {
                this.event = event;
            }

            public static class EventBean {
                /**
                 * type : interface
                 * url :
                 * msgText : qtalk是什么
                 */

                private String type;
                private String url;
                private String msgText;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getMsgText() {
                    return msgText;
                }

                public void setMsgText(String msgText) {
                    this.msgText = msgText;
                }
            }
        }
    }

    public static class WordLimitBean {
        /**
         * words : 100
         * lines : 6
         */

        private int words;
        private int lines;

        public int getWords() {
            return words;
        }

        public void setWords(int words) {
            this.words = words;
        }

        public int getLines() {
            return lines;
        }

        public void setLines(int lines) {
            this.lines = lines;
        }
    }

    public static class BottomBean {
        /**
         * id : 1
         * text : 有用
         * url : http://127.0.0.1:8025/qtalk_robot/yn_feedback?id=161&is_worked=1&rexian_id=qtalkrexian&m_from=qtalkrexian@ejabhost1&realfrom=hotline_robot@ejabhost1&m_to=hubin.hu@ejabhost1&realto=hubin.hu@ejabhost1
         */

        private int id;
        private String text;
        private String url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
