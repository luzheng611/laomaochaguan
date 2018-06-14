package com.laomachaguan.Editor.interfaces;

import com.even.mricheditor.ActionType;

/**
 * OnActionPerformListener
 * Created by even.wu on 17/8/17.
 */

public interface OnActionPerformListener {
    void onActionPerform(ActionType type, Object... values);
}
