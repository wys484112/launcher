package com.example.launcher.database.activeandroid.service;

import com.example.launcher.database.activeandroid.Student;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StudentService {
    int deleteByPrimaryKey(Long id);

    long insert(Student record);

    int insertSelective(Student record);

    Student selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);

    Set<String> findRoleByUserId(Long id);

    List<Student> findAllStudent();

    void initData();
}
