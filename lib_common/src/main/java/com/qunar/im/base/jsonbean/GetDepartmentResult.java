package com.qunar.im.base.jsonbean;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * create by haibin
 * 解析组织架构
 */
public class GetDepartmentResult extends BaseResult {
    public String D;
    public List<UserItem> UL;
    public List<GetDepartmentResult> SD;

    public GetDepartmentResult(String d) {
        D = d;
        UL = new ArrayList<>();
        SD = new ArrayList<>();
    }

    public GetDepartmentResult() {
        UL = new ArrayList<>();
        SD = new ArrayList<>();
    }

    public boolean addUser(UserItem userInfo) {
        return UL.add(userInfo);
    }

    public List<GetDepartmentResult> getChild() {
        return SD;
    }

    public String getDep() {
        return D;
    }

    public Integer containChild(String str) {
        int index = 0;
        boolean flag = false;
        for (GetDepartmentResult dep : SD) {
            if (dep.getDep().equals(str)) {
                flag = true;
                break;
            }
            index++;
        }
        if (flag) {
            return index;
        } else {
            return -1;
        }
    }
    public static List<GetDepartmentResult> Structured(List<UserItem> usersInfo) {

        Logger.i("数据进行组织结构化开始.");
        if (usersInfo == null || usersInfo.isEmpty()) {
            return new GetDepartmentResult().getChild();
        }
        GetDepartmentResult company = new GetDepartmentResult();
        for (UserItem user : usersInfo) {

            GetDepartmentResult orgaStructure = company;
            List<String> OrganTemp;
            List<String> Organ = new ArrayList<>();
            try {
                OrganTemp = Arrays.asList(user.D.split("/"));
                if (OrganTemp == null || OrganTemp.isEmpty()) {
                    continue;
                }
            } catch (Exception e) {
                Logger.e("{}员工的部门:{}不合法", user.U, user.N);
                continue;
            }
            Organ.addAll(OrganTemp);
            Iterator<String> iter = Organ.iterator();
            while (iter.hasNext()) {
                String dep = iter.next();
                if (dep.equals(null) || dep.equals("")) {
                    continue;
                }
                int indexChild = orgaStructure.containChild(dep);
                if (indexChild == -1) {
                    orgaStructure.getChild().add(new GetDepartmentResult(dep));
                    indexChild = orgaStructure.getChild().size() - 1;
                }
                orgaStructure = orgaStructure.getChild().get(indexChild);
            }
            orgaStructure.addUser(user);
        }
        Logger.i("数据进行组织结构化完成！");
        return company.getChild();
    }

    static public class UserItem implements BaseData
    {
        public String U;
        public String D;
        public String N;
        public int S;
        public String Fp;
        public String Sp;
    }
}
