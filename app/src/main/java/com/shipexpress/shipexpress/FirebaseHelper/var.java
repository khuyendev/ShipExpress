package com.shipexpress.shipexpress.FirebaseHelper;

/**
 * Created by QuangCoi on 10/27/2016.
 */

public class var {
    //name child
    public static final int TYPE_SHOP = 0;
    public static final int TYPE_SHIP = 1;
    public static final String CHILD_USER = "USER";
    public static final String CHILD_SHOP = "SHOP";
    public static final String CHILD_SHIP = "SHIP";
    public static final String CHILD_MAPSHOP = "MAPSHOP";
    public static final String CHILD_MAPSHIP = "MAPSHIPPER";
    public static final String CHILD_INFO = "INFOR";
    public static final String CHILD_LISTORDER = "ListOrder";
    public static final String CHILD_TYPE_USER = "TYPE_USER";
    //status order
    public static final String isWAITTING = "isWaitting";//đơn hàng đang đợi
    public static final String isCOMMIT = "isCommit";//đơn hàng đang chờ shop xác nhận
    public static final String isDeposit = "isDeposit";//đơn hàng đã nhận cọc - hiện chỉ đường cho shop
    public static final String onProgress = "onProgress";//đơn hàng trong quá trình vận chuyển- hiện chỉ đường tới ng nhận
    public static final String isREJECTED = "isRejected";//đơn hàng bị hủy xác nhận của shop (chỉ ship dùng)
    public static final String isSUCCESS = "isSuccess";//đơn hàng thành công hoàn toàn
    public static final String isCANCEL = "isCancel";//đơn hàng hủy do người nhận từ chối - trở về nhận tiền cọc
    public static final String isFAIL = "isFail";//đơn hàng hủy bởi ship - không nhận tiền cọc -  hoặc đơn hàng lỗi !!!
    //pemission
    public static final int ACCESS_FINE_LOCATION_CODE = 1;
    public static final int INT_isWAITTING = 1;
    public static final int INT_isCOMMIT = 2;
    public static final int INT_isREJECTED = 3;
    public static final int INT_isSUCCESS = 4;
    public static final int INT_isCANCEL = 5;
    public static final int INT_isFAIL = 6;
}
