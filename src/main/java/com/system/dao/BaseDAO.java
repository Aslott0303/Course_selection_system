package com.system.dao;

import java.sql.SQLException;
import java.util.List;

public interface BaseDAO<T> {

    /**
     * 插入实体，返回自增主键（或影响行数）
     */
    int insert(T entity) throws SQLException;

    /**
     * 更新实体
     */
    void update(T entity) throws SQLException;

    /**
     * 根据主键删除
     */
    void deleteById(int id) throws SQLException;

    /**
     * 根据主键查询单个实体
     */
    T findById(int id) throws SQLException;

    /**
     * 查询所有记录
     */
    List<T> findAll() throws SQLException;
}