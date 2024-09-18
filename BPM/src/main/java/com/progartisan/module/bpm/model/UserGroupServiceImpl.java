package com.progartisan.module.bpm.model;

import com.progartisan.component.common.Util;
import com.progartisan.module.bpm.api.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.IdentityService;

import javax.inject.Named;
import java.util.Arrays;

@RequiredArgsConstructor
@Named
public class UserGroupServiceImpl implements UserGroupService {

	private final IdentityService identityService;

	@Override
	public void createGroups(Group...  groups) {
		Arrays.stream(groups).forEach(group -> {
			var existingGroup = identityService.createGroupQuery().groupId(group.getGroupId()).singleResult();
			// 如果组存在，则忽略
			if (existingGroup == null) {
				org.flowable.idm.api.Group fGroup = identityService.newGroup(group.getGroupId());
				fGroup.setName(group.getGroupName());
				fGroup.setType(Util.isNotEmpty(group.getOrgId()) ? group.getOrgId() + "." + group.getRoleId() : group.getRoleId());
				identityService.saveGroup(fGroup);
				group.setGroupId(fGroup.getId());
			}
		});
	}

	@Override
	public boolean groupExists(String groupId) {
		return false;
	}
}
