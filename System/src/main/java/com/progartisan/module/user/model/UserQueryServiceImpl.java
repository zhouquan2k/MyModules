package com.progartisan.module.user.model;

import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Context;
import com.progartisan.component.framework.Service;
import com.progartisan.component.framework.Service.Type;
import com.progartisan.module.user.api.User;
import com.progartisan.module.user.api.UserQueryService;
import com.progartisan.module.user.infra.ConvertUser;
import com.progartisan.module.user.infra.UserMapper;

import lombok.RequiredArgsConstructor;

@Service(type = Type.Query)
@Named
@RequiredArgsConstructor
class UserQueryServiceImpl implements UserQueryService, UserDetailsService {

	private final ConvertUser convert;
    private final UserMapper userMapper;

    @Override
    public List<User> queryByExample(Map<String, Object> example) {
        return convert.poToDto(userMapper.queryByExample(example));
    }

    @Override
    public List<User> getUsers() {
        var users = userMapper.getUsers();
        return Util.mapToList(users.stream(), convert::poToDto);
    }

    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        var user = userMapper.getUserByLoginName(loginName);
        if (user == null)
            throw new UsernameNotFoundException("no user: " + loginName);
        return user;
    }

	public User getUserByUsername(String loginName) {
		return convert.poToDto(userMapper.getUserByLoginName(loginName));
	}

	@Override
	public List<User> queryUsersByOrg(String orgId) {
		return Util.mapToList(userMapper.queryUsersByOrg(orgId).stream(), convert::poToDto);
	}

	@Override
	public User getMyProfile() {
		var userId = Context.getUserId();
		return convert.poToDto(userMapper.getUser(userId));
	}

}
