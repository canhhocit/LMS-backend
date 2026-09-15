package com.ex.learninghub.modules.course.repository;

import com.ex.learninghub.modules.course.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {

    List<ClassSchedule> findByClazzId(Long clazzId);

    List<ClassSchedule> findByDayOfWeek(Integer dayOfWeek);

    List<ClassSchedule> findByRoom(String room);

    List<ClassSchedule> findByRoomAndDayOfWeek(String room, Integer dayOfWeek);
}
