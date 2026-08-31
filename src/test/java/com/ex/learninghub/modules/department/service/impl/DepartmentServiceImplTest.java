package com.ex.learninghub.modules.department.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.department.dto.request.DepartmentRequest;
import com.ex.learninghub.modules.department.dto.response.DepartmentResponse;
import com.ex.learninghub.modules.department.entity.Department;
import com.ex.learninghub.modules.department.repository.DepartmentRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private User headUser;

    @BeforeEach
    void setUp() {
        headUser = User.builder().fullName("Trưởng Khoa").email("head@test").role(Role.LECTURER).build();
        headUser.setId(100L);
    }

    @Test
    void create_succeeds() {
        when(departmentRepository.existsByCode("CNTT")).thenReturn(false);
        when(userRepository.findById(100L)).thenReturn(Optional.of(headUser));
        when(departmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DepartmentResponse resp = departmentService.create(
                DepartmentRequest.builder().code("CNTT").name("Khoa CNTT").description("Mô tả").headUserId(100L).isActive(true).build());

        assertThat(resp.getCode()).isEqualTo("CNTT");
        assertThat(resp.getName()).isEqualTo("Khoa CNTT");
        assertThat(resp.getHeadUserId()).isEqualTo(100L);
        assertThat(resp.getHeadUserFullName()).isNull(); // không fetch thêm
    }

    @Test
    void create_throws_whenCodeExists() {
        when(departmentRepository.existsByCode("CNTT")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.create(
                DepartmentRequest.builder().code("CNTT").name("Khoa CNTT").build()))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DEPARTMENT_ALREADY_EXISTS);
    }

    @Test
    void create_throws_whenHeadUserNotFound() {
        when(departmentRepository.existsByCode("CNTT")).thenReturn(false);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.create(
                DepartmentRequest.builder().code("CNTT").name("Khoa CNTT").headUserId(999L).build()))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void update_succeeds() {
        Department dept = Department.builder().code("CNTT").name("Khoa CNTT").isActive(true).build();
        dept.setId(1L);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(userRepository.findById(200L)).thenReturn(Optional.of(headUser));
        when(departmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DepartmentResponse resp = departmentService.update(1L,
                DepartmentRequest.builder().name("Khoa CNTT mới").headUserId(200L).isActive(false).build());

        assertThat(resp.getName()).isEqualTo("Khoa CNTT mới");
        assertThat(resp.getIsActive()).isFalse();
        assertThat(resp.getHeadUserId()).isEqualTo(200L);
    }

    @Test
    void update_throws_whenNotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.update(99L,
                DepartmentRequest.builder().name("X").build()))
                .isInstanceOf(AppException.class);
    }

    @Test
    void delete_succeeds() {
        doNothing().when(departmentRepository).deleteById(1L);

        departmentService.delete(1L);

        verify(departmentRepository).deleteById(1L);
    }

    @Test
    void list_returnsAll() {
        Department d1 = Department.builder().code("CNTT").name("Khoa CNTT").isActive(true).build(); d1.setId(1L);
        Department d2 = Department.builder().code("KTCN").name("Khoa KTCN").isActive(true).build(); d2.setId(2L);
        when(departmentRepository.findAll()).thenReturn(List.of(d1, d2));

        List<DepartmentResponse> resp = departmentService.list();

        assertThat(resp).hasSize(2);
        assertThat(resp.get(0).getCode()).isEqualTo("CNTT");
        assertThat(resp.get(1).getCode()).isEqualTo("KTCN");
    }

    @Test
    void get_returnsById() {
        Department d1 = Department.builder().code("CNTT").name("Khoa CNTT").isActive(true).build(); d1.setId(1L);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(d1));

        DepartmentResponse resp = departmentService.get(1L);

        assertThat(resp.getCode()).isEqualTo("CNTT");
    }

    @Test
    void get_throws_whenNotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.get(99L))
                .isInstanceOf(AppException.class);
    }
}
