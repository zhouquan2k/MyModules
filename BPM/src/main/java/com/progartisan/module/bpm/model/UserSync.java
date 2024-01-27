package com.progartisan.module.bpm.model;

import com.progartisan.component.common.Util;
import com.progartisan.component.framework.EntityCreatedEvent;
import com.progartisan.component.framework.EntityUpdatedEvent;
import com.progartisan.module.user.api.User.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.User;
import org.springframework.context.event.EventListener;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class UserSync {
	private final IdentityService identityService;

	private String roleToGroup(UserRole role) {
		Util.check(Util.isNotEmpty(role.getOrgId()) && !Util.equals(role.getRoleId(), "0"));
		return String.format("%s-%s", role.getOrgId(), role.getRoleId());
	}

	private void syncRoles(String userId, Set<UserRole> roles) {
		if (roles == null) roles = Set.of();
		var targetRoleMap = Util.toMap(roles.stream().filter(role -> Util.isNotEmpty(role.getOrgId()) && !Util.equals(role.getRoleId(), "0")), role -> roleToGroup(role));

		var groups = identityService.createGroupQuery().groupMember(userId).list();

		var alreadyExistSet = groups.stream().flatMap(group -> {
			String groupId = group.getId();
			// String roleId = groupId.substring(groupId.indexOf('-') + 1);
			if (!targetRoleMap.containsKey(groupId)) {
				log.debug(String.format("user %s remove from group: %s", userId, groupId));
				identityService.deleteMembership(userId, groupId);
				return Stream.of();
			} else {
				return Stream.of(groupId);
			}
		}).collect(Collectors.toSet()) ;

		var rolesToAdd = roles.stream().filter(role -> Util.isNotEmpty(role.getOrgId()) && !Util.equals(role.getRoleId(), "0")
				&& !alreadyExistSet.contains(roleToGroup(role)));
		rolesToAdd.forEach(role -> {
			// 设置分组
			var groupId = roleToGroup(role);
			if (groupId != null) {
				log.debug(String.format("user %s join to group: %s", userId, groupId));
				identityService.createMembership(userId, groupId);
			}
		});
	}

	void createUser(com.progartisan.module.user.api.User user) {
		// 创建一个新用户
		var existingUser = identityService.createUserQuery().userId(user.getUserId()).singleResult();
		// 如果用户存在，则忽略
		if (existingUser == null) {
			User fUser = identityService.newUser(user.getUserId());
			fUser.setFirstName(user.getUsername());

			// 保存用户
			identityService.saveUser(fUser);
			// 同步角色到组
			var roles = user.getRoles();
			syncRoles(user.getUserId(), roles);
		}
	}

	@EventListener
	public void handleEntityCreatedEvent(EntityCreatedEvent event) {
		log.info("> handle EntityCreatedEvent");
		// TODO
		if (event.getEntity() instanceof com.progartisan.module.user.api.User) {
			log.info("> user created");
			createUser((com.progartisan.module.user.api.User) event.getEntity());
		}
	}

	@EventListener
	public void handleEntityUpdatedEvent(EntityUpdatedEvent event) {
		log.info("> handle EntityUpdatedEvent");
		// TODO
		if (event.getEntity() instanceof com.progartisan.module.user.api.User) {
			log.info("> user updated");
			var user = (com.progartisan.module.user.api.User) event.getEntity();
			syncRoles(user.getUserId(), user.getRoles());
		}
	}
}