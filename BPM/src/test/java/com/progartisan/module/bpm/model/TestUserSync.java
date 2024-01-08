package com.progartisan.module.bpm.model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.progartisan.component.framework.EntityUpdatedEvent;
import com.progartisan.module.user.api.User;
import com.progartisan.module.user.api.User.UserRole;

@RunWith(MockitoJUnitRunner.class)
public class TestUserSync {

	@Mock
	IdentityService identityService;

	@InjectMocks
	UserSync userSync;

	@Test
	public void testSyncUserRole() {
		var groupQueryMock = mock(GroupQuery.class);
		Group mockGroup1 = mock(Group.class);
		when(mockGroup1.getId()).thenReturn("orgId-roleId1");
		Group mockGroup2 = mock(Group.class);
		when(mockGroup2.getId()).thenReturn("orgId-roleId2");
		var currentGroups = List.of(mockGroup1, mockGroup2);

		// 当调用 createGroupQuery 时返回 groupQueryMock
		when(identityService.createGroupQuery()).thenReturn(groupQueryMock);
		// 当调用 groupMember 和 list 方法时返回 expectedGroups
		when(groupQueryMock.groupMember("userId")).thenReturn(groupQueryMock);
		when(groupQueryMock.list()).thenReturn(currentGroups);

		var userId = "userId";
		var user = User.builder().userId(userId).build();
		user.setRoles(Set.of(UserRole.builder().userId(userId).roleId("roleId1").orgId("orgId").build()));
		var userUpdatedEvent = new EntityUpdatedEvent(user);
		userSync.handleEntityUpdatedEvent(userUpdatedEvent);
		verify(identityService, times(0)).createMembership(any(), any());
		verify(identityService, times(1)).deleteMembership(userId, "orgId-roleId2");
	}

	@Test
	public void testSyncUserRole2() {
		var groupQueryMock = mock(GroupQuery.class);
		Group mockGroup1 = mock(Group.class);
		when(mockGroup1.getId()).thenReturn("orgId-roleId1");
		Group mockGroup2 = mock(Group.class);
		when(mockGroup2.getId()).thenReturn("orgId-roleId2");
		var currentGroups = List.of(mockGroup1, mockGroup2);
		// 当调用 createGroupQuery 时返回 groupQueryMock
		when(identityService.createGroupQuery()).thenReturn(groupQueryMock);
		// 当调用 groupMember 和 list 方法时返回 expectedGroups
		when(groupQueryMock.groupMember("userId")).thenReturn(groupQueryMock);
		when(groupQueryMock.list()).thenReturn(currentGroups);

		var userId = "userId";
		var user = User.builder().userId(userId).build();
		user.setRoles(Set.of(UserRole.builder().userId(userId).roleId("roleId1").orgId("orgId").build(),
				UserRole.builder().userId(userId).roleId("roleId3").orgId("orgId").build()));
		var userUpdatedEvent = new EntityUpdatedEvent(user);
		userSync.handleEntityUpdatedEvent(userUpdatedEvent);
		verify(identityService, times(1)).createMembership(userId, "orgId-roleId3");
		verify(identityService, times(1)).deleteMembership(userId, "orgId-roleId2");

	}
}
