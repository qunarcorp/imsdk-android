package com.qunar.im.base.jsonbean;

public final class UpdateResponse extends BaseResult{

  /**
   * 服务器端最新版本号,如果和客户端版本号相同则无升级需求
   */
  public  Integer version;

  /**
   * 平台
   */
  public  String platform;

  /**
   * 更新消息
   */
  public  String message;

  /**
   * 是否强制升级
   */
  public  Boolean forceUpdate;

  /**
   * 下载链接地址
   */
  public  String linkUrl;

 public long fileSize;

  /**
   * MD5
   */
  public  String md5;

    /**
     * 是否有更新
     */
  public boolean isUpdated;
}
