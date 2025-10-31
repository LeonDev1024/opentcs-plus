package org.opentcs.common.translation.core.impl;

import org.opentcs.common.core.service.UserService;
import org.opentcs.common.translation.annotation.TranslationType;
import org.opentcs.common.translation.constant.TransConstant;
import org.opentcs.common.translation.core.TranslationInterface;
import lombok.AllArgsConstructor;

/**
 * 用户名翻译实现
 *
 * @author Lion Li
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NAME)
public class UserNameTranslationImpl implements TranslationInterface<String> {

    private final UserService userService;

    @Override
    public String translation(Object key, String other) {
        if (key instanceof Long id) {
            return userService.selectUserNameById(id);
        }
        return null;
    }
}
