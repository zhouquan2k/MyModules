package com.progartisan.module.bpm.model;

import java.util.Arrays;

import javax.inject.Named;

import org.flowable.engine.IdentityService;

import com.progartisan.module.bpm.api.UserGroupService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Named
public class UserGroupServiceImpl implements UserGroupService {

	private final IdentityService identityService;

	@Override
	public void createGroups(Group...  groups) {
		Arrays.stream(groups).forEach(group -> {
			var existingGroup = identityService.createGroupQuery().groupId(group.getGroupId()).singleResult();
			// 如果组存在，则先删除
			if (existingGroup != null) {
				identityService.deleteGroup(existingGroup.getId());
			}
			org.flowable.idm.api.Group fGroup = identityService.newGroup(group.getGroupId());
			fGroup.setName(group.getGroupName());
			fGroup.setType(group.getOrgId() + "." + group.getRoleId());
			identityService.saveGroup(fGroup);
			group.setGroupId(fGroup.getId());
		});
	}

}
