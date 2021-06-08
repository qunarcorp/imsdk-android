package com.qunar.im.base.module;

import java.io.Serializable;
import java.util.List;

public class WorkWorldDetailsCommenData implements MultiItemEntity{


    /**
     * data : {"deleteComments":[{"id":3.6033941311864406E7,"isDelete":-5.025863007142992E7,"uuid":"eiusmod ex"},{"id":4668110.913675427,"isDelete":-5.385126468435759E7,"uuid":"consequat sed"},{"id":-9.766324515943916E7,"isDelete":-1.8191352936777115E7,"uuid":"et rep"},{"id":-3.8994287108716644E7,"isDelete":-2.9203935351248235E7,"uuid":"Duis Lorem cillum culpa nostrud"}],"newComment":[{"anonymousName":"Excepteur qui deserunt","anonymousPhoto":"adipisicing","commentUUID":"do in irure eiusmod","content":"sed sunt fugiat","createTime":8.269868233724448E7,"fromHost":"laboris amet","fromUser":"sunt","id":5.5106199377797574E7,"isAnonymous":-6.002114353700061E7,"isDelete":2.0608622232486784E7,"isLike":9.480324758136329E7,"likeNum":-9.569283989306125E7,"parentCommentUUID":"id dolore culpa ea","postUUID":"do adipisicing labore voluptate","reviewStatus":-9.108365232187854E7,"toHost":"nostrud Duis eu consequat elit","toUser":"ex exercitation","updateTime":-3.375192692442859E7},{"anonymousName":"pariatur est quis","anonymousPhoto":"amet proident occaecat sed","commentUUID":"culpa sit non","content":"consectetur consequat esse deserunt","createTime":-5.014410198148607E7,"fromHost":"eu ea sunt minim aliqua","fromUser":"Excepteur mollit quis incididunt qui","id":4277638.217013329,"isAnonymous":-7.166272955039333E7,"isDelete":-1.9037268230086908E7,"isLike":-6.2670810202829316E7,"likeNum":-2.3485653962281823E7,"parentCommentUUID":"sit","postUUID":"dolor","reviewStatus":1636712.345532924,"toHost":"tempor aliqua","toUser":"ut sit ullamco","updateTime":-6.207830242742385E7},{"anonymousName":"dolor consequat","anonymousPhoto":"ullamco cupidatat","commentUUID":"et qui","content":"in cillum est","createTime":-9.27229672293822E7,"fromHost":"sit officia anim","fromUser":"tempor voluptate d","id":-9.893990032504632E7,"isAnonymous":6.0902244994333625E7,"isDelete":8.826085528554559E7,"isLike":2.1911243080731735E7,"likeNum":3718514.5491362065,"parentCommentUUID":"in id","postUUID":"veniam ut tempor eiusmod","reviewStatus":-6.4379554008966975E7,"toHost":"consectetur ex dolor tempor","toUser":"nisi est exercitation","updateTime":-1.3217958678290024E7},{"anonymousName":"laboris enim dolor officia","anonymousPhoto":"nisi dolor sunt","commentUUID":"culpa Ut labori","content":"ea e","createTime":-4.362360140504462E7,"fromHost":"non occaecat dolor","fromUser":"adipisicing consectetur sunt aliquip","id":-6.111658485990157E7,"isAnonymous":-685218.0025397539,"isDelete":6245656.283641487,"isLike":2.5905933911559448E7,"likeNum":4.2436587661426425E7,"parentCommentUUID":"adipisicing eu Ut dolore","postUUID":"occaecat adipisicing sint co","reviewStatus":1.1382645928428262E7,"toHost":"veniam Excepteur","toUser":"proident Excepteur aute Duis Lorem","updateTime":3.414881860202326E7},{"anonymousName":"nulla cupidatat in","anonymousPhoto":"sint","commentUUID":"fugiat","content":"id","createTime":6.4599552259430915E7,"fromHost":"","fromUser":"ess","id":2.9420297731194586E7,"isAnonymous":2.0085099531801417E7,"isDelete":5.3549766918773085E7,"isLike":-5.969312330317349E7,"likeNum":9.181758713220432E7,"parentCommentUUID":"commodo non quis aliquip","postUUID":"laborum eu","reviewStatus":-3.944499129626475E7,"toHost":"est aliqua","toUser":"in","updateTime":7.355894704371163E7}]}
     * errcode : -6.346568970119884E7
     * errmsg : minim
     * ret : true
     */

    private DataBean data;
    private double errcode;
    private String errmsg;
    private boolean ret;

    @Override
    public int getItemType() {
        return 0;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public double getErrcode() {
        return errcode;
    }

    public void setErrcode(double errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public static class DataBean implements MultiItemEntity{
        private List<DeleteCommentsBean> deleteComments;
        private List<WorkWorldNewCommentBean> newComment;
        private List<WorkWorldOutCommentBean> attachCommentList;
        private int postCommentNum;
        private int postLikeNum;
        private int isPostLike;
        private int returnType;

        public List<WorkWorldOutCommentBean> getAttachCommentList() {
            return attachCommentList;
        }

        public void setAttachCommentList(List<WorkWorldOutCommentBean> attachCommentList) {
            this.attachCommentList = attachCommentList;
        }

        public int getReturnType() {
            return returnType;
        }

        public void setReturnType(int returnType) {
            this.returnType = returnType;
        }

        public int getIsPostLike() {
            return isPostLike;
        }

        public void setIsPostLike(int isPostLike) {
            this.isPostLike = isPostLike;
        }

        public int getPostCommentNum() {
            return postCommentNum;
        }

        public void setPostCommentNum(int postCommentNum) {
            this.postCommentNum = postCommentNum;
        }

        public int getPostLikeNum() {
            return postLikeNum;
        }

        public void setPostLikeNum(int postLikeNum) {
            this.postLikeNum = postLikeNum;
        }

        public List<DeleteCommentsBean> getDeleteComments() {
            return deleteComments;
        }

        public void setDeleteComments(List<DeleteCommentsBean> deleteComments) {
            this.deleteComments = deleteComments;
        }

        public List<WorkWorldNewCommentBean> getNewComment() {
            return newComment;
        }

        public void setNewComment(List<WorkWorldNewCommentBean> newComment) {
            this.newComment = newComment;
        }

        @Override
        public int getItemType() {
            return 0;
        }

        public static class DeleteCommentsBean implements Serializable {
            /**
             * id : 3.6033941311864406E7
             * isDelete : -5.025863007142992E7
             * uuid : eiusmod ex
             */

            private double id;
            private double isDelete;
            private String uuid;

            public double getId() {
                return id;
            }

            public void setId(double id) {
                this.id = id;
            }

            public double getIsDelete() {
                return isDelete;
            }

            public void setIsDelete(double isDelete) {
                this.isDelete = isDelete;
            }

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }
        }


    }


}
