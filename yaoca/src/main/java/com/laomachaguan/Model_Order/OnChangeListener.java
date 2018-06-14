package com.laomachaguan.Model_Order;

/**
 * Created by Administrator on 2016/12/2.
 */
public interface OnChangeListener {

        void OnNumChanged(double money, boolean flag);
        void OnChooseChanged(double choosedNum, boolean flag);
        void OnDeleteChoosed(double needToCutMoney);

}
