package com.progartisan.module.security.model;

import com.progartisan.component.common.BizException;
import com.progartisan.component.common.Util;
import com.progartisan.component.framework.AuthInfo;
import com.progartisan.component.framework.Command;
import com.progartisan.component.framework.Metadata.PermissionDef;
import com.progartisan.component.framework.Metadata.ServiceDef;
import com.progartisan.component.framework.Service;
import com.progartisan.component.security.AuthResult;
import com.progartisan.component.security.ITokenUtil;
import com.progartisan.component.spi.MetadataProvider;
import com.progartisan.module.security.api.SecurityService;
import com.progartisan.module.user.api.Role.RoleType;
import com.progartisan.module.user.model.domain.UserPO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SecurityPermissions {
	static final String AdminPermissions = "***";
	static final String TestPermissions = "test*";
	public static List<PermissionDef> permissionDefList = List.of( //
			new PermissionDef(AdminPermissions, "系统管理(所有权限)"), //
			new PermissionDef(TestPermissions, "测试"));
}

@Service(type = Service.Type.Mixed, name = "security", value = "系统 - 安全", permissions = SecurityPermissions.class, order = 103)
@RequiredArgsConstructor
@Named
public class SecurityServiceImpl implements SecurityService {

    private final AuthenticationManager authenticationManager;
    protected final MetadataProvider metadataProvider;
    private final ITokenUtil tokenUtil;
	// private final Set<PermissionProvider> allPermissionProviders;

	// private final ConvertUser convertUser; // TODO

    @Command(value = "用户登录", logParam = false)
    // @AnonymousAccess
    @Override
    public com.progartisan.component.framework.AuthInfo login(@Validated @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        // 密码解密
        /*
        RSA rsa = new RSA(privateKey, null);
        String password = new String(rsa.decrypt(authUser.getPassword(), KeyType.PrivateKey));
        */
        /* TODO 验证码
        // 查询验证码
        String code = (String) redisUtils.get(authUser.getUuid());
        // 清除验证码
        redisUtils.del(authUser.getUuid());
        if (StringUtils.isBlank(code)) {
            throw new BadRequestException("验证码不存在或已过期");
        }
        if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
            throw new BadRequestException("验证码错误");
        }
        */
        var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword());

        var authentication = authenticationManager.authenticate(authenticationToken);

        // 生成令牌
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var authInfo = (AuthInfo) userDetails;
        SecurityContextHolder.getContext().setAuthentication(authentication);
		// var user = convertUser.poToDto((UserPO) userDetails); // TODO

		this.processUserPermissions(userDetails);

        return tokenUtil.generateAuthResult(authInfo, request);
    }

	private void processUserPermissions(UserDetails user) {
		var userPO = (UserPO) user;
		var metadata = this.metadataProvider.getMetadata(null);
		var permissionMap = new HashMap<String, PermissionDef>();
        metadata.getServices().forEach(func -> {
			func.getPermissions().forEach(perm -> {
				permissionMap.put(func.getName() + "." + perm.getName(), perm);
				// this.permissionMap.put(perm.getName(), perm);
			});
		});

		Set<String> permissions = new HashSet<String>();
		for (var ur : userPO.getRoles()) {
			var role = ur.getRole();
			if (role != null && ur.getRole().get_permissions() != null)
				for (var rp : role.get_permissions()) {
				var permission = rp.getPermission();
				// TODO not necessary
				var permissionDef = permissionMap.get(permission);
				if (permissionDef != null) {
					if (permissionDef.getName().endsWith("@") && role.getRoleType() != RoleType.Global) {
						permission = permission + ur.getOrgId();
					}
				}
				permissions.add(permission); // for menu permission = function name
			}
		}
		userPO.setPermissions(permissions);

	}

    //@ApiOperation("获取用户信息")
    @Override
    public AuthResult getUserInfo(HttpServletRequest request) {
        return tokenUtil.getAuthInfoFromToken(request);
    }

    /*
    //@AnonymousAccess
    @ApiOperation("获取验证码")
    @GetMapping(value = "/code")
    public ResponseEntity<Object> getCode() {
        // 算术类型 https://gitee.com/whvse/EasyCaptcha
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(111, 36);
        // 几位数运算，默认是两位
        captcha.setLen(2);
        // 获取运算的结果
        String result = "";
        try {
            result = new Double(Double.parseDouble(captcha.text())).intValue() + "";
        } catch (Exception e) {
            result = captcha.text();
        }
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        // 保存
        redisUtils.set(uuid, result, expiration, TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {
            {
                put("img", captcha.toBase64());
                put("uuid", uuid);
            }
        };
        return ResponseEntity.ok(imgResult);
    }
    */

    //@AnonymousAccess
    @Override
    @Command(value = "退出登录", logParam = false)
    public void logout(HttpServletRequest request) {
        /*
        onlineUserService.logout(tokenUtil.getToken(request));
        */
        tokenUtil.invalidateToken(request);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Override
    public List<ServiceDef> getAllFunctionDefs() {
        var metadata = this.metadataProvider.getMetadata(null);
        return Util.toList(metadata.getServices().stream().filter(x -> x.getOrder() > 0)
                .sorted((x, y) -> x.getOrder() - y.getOrder()));
    }

    @Override
    public void testException() throws BizException {
        throw new BizException("EX-001", "test exception");

    }

}
