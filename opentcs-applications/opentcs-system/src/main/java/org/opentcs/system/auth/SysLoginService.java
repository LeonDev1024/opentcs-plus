package org.opentcs.system.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.lock.annotation.Lock4j;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.opentcs.common.core.constant.CacheConstants;
import org.opentcs.common.core.constant.Constants;
import org.opentcs.common.core.domain.dto.PostDTO;
import org.opentcs.common.core.domain.dto.RoleDTO;
import org.opentcs.common.core.domain.model.LoginUser;
import org.opentcs.common.core.enums.LoginType;
import org.opentcs.common.core.exception.ServiceException;
import org.opentcs.common.core.exception.user.UserException;
import org.opentcs.common.core.utils.*;
import org.opentcs.common.log.event.LogininforEvent;
import org.opentcs.common.mybatis.helper.DataPermissionHelper;
import org.opentcs.common.redis.utils.RedisUtils;
import org.opentcs.common.satoken.utils.LoginHelper;
import org.opentcs.security.api.AuthApi;
import org.opentcs.system.domain.SysUser;
import org.opentcs.system.domain.bo.SysSocialBo;
import org.opentcs.system.domain.vo.*;
import org.opentcs.system.mapper.SysUserMapper;
import org.opentcs.system.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * 登录校验方法（从 opentcs-admin 迁移至 opentcs-system 应用层）
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysLoginService {

    @Value("${user.password.maxRetryCount}")
    private Integer maxRetryCount;

    @Value("${user.password.lockTime}")
    private Integer lockTime;

    private final ISysPermissionService permissionService;
    private final ISysSocialService sysSocialService;
    private final ISysRoleService roleService;
    private final ISysDeptService deptService;
    private final ISysPostService postService;
    private final SysUserMapper userMapper;
    private final AuthApi authApi;

    @Lock4j
    public void socialRegister(AuthUser authUserData) {
        String authId = authUserData.getSource() + authUserData.getUuid();
        SysSocialBo bo = BeanUtil.toBean(authUserData, SysSocialBo.class);
        BeanUtil.copyProperties(authUserData.getToken(), bo);
        Long userId = LoginHelper.getUserId();
        bo.setUserId(userId);
        bo.setAuthId(authId);
        bo.setOpenId(authUserData.getUuid());
        bo.setUserName(authUserData.getUsername());
        bo.setNickName(authUserData.getNickname());
        List<SysSocialVo> checkList = sysSocialService.selectByAuthId(authId);
        if (CollUtil.isNotEmpty(checkList)) {
            throw new ServiceException("此三方账号已经被绑定!");
        }
        SysSocialBo params = new SysSocialBo();
        params.setUserId(userId);
        params.setSource(bo.getSource());
        List<SysSocialVo> list = sysSocialService.queryList(params);
        if (CollUtil.isEmpty(list)) {
            sysSocialService.insertByBo(bo);
        } else {
            bo.setId(list.get(0).getId());
            sysSocialService.updateByBo(bo);
        }
    }

    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (ObjectUtil.isNotNull(loginUser)) {
                recordLogininfor(loginUser.getUsername(), Constants.LOGOUT, MessageUtils.message("user.logout.success"));
            }
        } catch (NotLoginException ignored) {
        } finally {
            authApi.revokeToken();
        }
    }

    public void recordLogininfor(String username, String status, String message) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(status);
        logininforEvent.setMessage(message);
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
    }

    public LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        Long userId = user.getUserId();
        loginUser.setUserId(userId);
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        loginUser.setUserType(user.getUserType());
        loginUser.setMenuPermission(permissionService.getMenuPermission(userId));
        loginUser.setRolePermission(permissionService.getRolePermission(userId));
        if (ObjectUtil.isNotNull(user.getDeptId())) {
            Opt<SysDeptVo> deptOpt = Opt.of(user.getDeptId()).map(deptService::selectDeptById);
            loginUser.setDeptName(deptOpt.map(SysDeptVo::getDeptName).orElse(StringUtils.EMPTY));
            loginUser.setDeptCategory(deptOpt.map(SysDeptVo::getDeptCategory).orElse(StringUtils.EMPTY));
        }
        List<SysRoleVo> roles = roleService.selectRolesByUserId(userId);
        List<SysPostVo> posts = postService.selectPostsByUserId(userId);
        loginUser.setRoles(BeanUtil.copyToList(roles, RoleDTO.class));
        loginUser.setPosts(BeanUtil.copyToList(posts, PostDTO.class));
        return loginUser;
    }

    public void recordLoginInfo(Long userId, String ip) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(ip);
        sysUser.setLoginDate(DateUtils.getNowDate());
        DataPermissionHelper.ignore(() -> userMapper.updateById(sysUser));
    }

    public void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier) {
        String errorKey = CacheConstants.PWD_ERR_CNT_KEY + username;
        String loginFail = Constants.LOGIN_FAIL;

        int errorNumber = ObjectUtil.defaultIfNull(RedisUtils.getCacheObject(errorKey), 0);
        if (errorNumber >= maxRetryCount) {
            recordLogininfor(username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
            throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
        }

        if (supplier.get()) {
            errorNumber++;
            RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));
            if (errorNumber >= maxRetryCount) {
                recordLogininfor(username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
                throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
            } else {
                recordLogininfor(username, loginFail, MessageUtils.message(loginType.getRetryLimitCount(), errorNumber));
                throw new UserException(loginType.getRetryLimitCount(), errorNumber);
            }
        }

        RedisUtils.deleteObject(errorKey);
    }
}
