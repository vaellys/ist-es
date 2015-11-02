package com.ist.ioc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ist.common.es.util.LogUtils;
import com.ist.dto.bmp.T12_es_organ_index_assoDto;

public class T12_es_organ_index_assoDao extends JdbcDaoSupport {

    /**
     * 增加关联表，添加字段：机构 索引库 id 类型
     * 
     * @param dto
     * @return boolean
     * @throws SQLException
     */
    public boolean add(T12_es_organ_index_assoDto dto) throws SQLException {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            String sql = "insert into t12_es_organ_index_asso (es_id, organ_key, es_index, es_type) values(?,?,?,?)";
            connection = this.getConnection();
            prepareStatement = connection.prepareStatement(sql);
            prepareStatement.setString(1, dto.getEs_id());
            prepareStatement.setString(2, dto.getOrgankey());
            prepareStatement.setString(3, dto.getEs_index());
            prepareStatement.setString(4, dto.getEs_type());
            int result = prepareStatement.executeUpdate();
            if (result > 0) {
                return true;
            }
            return false;
        } catch (CannotGetJdbcConnectionException e) {
            logger.error("插入失败", e);
            throw e;
        } catch (SQLException e) {
            logger.error("插入失败", e);
            throw e;
        } finally {
            if (null != connection) {
                connection.close();
            }
            if (null != prepareStatement) {
                prepareStatement.close();
            }
        }
    }

    /**
     * 根据id和类型删除关联表
     * 
     * @param id
     * @param type
     *            有三种：1 sql类型 2 附件类型 3 目录类型
     * @return boolean
     * @throws SQLException
     */
    public boolean delete(String id, String type) throws SQLException {
        String sql = "delete from t12_es_organ_index_asso where es_id=? and es_type=?";
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = this.getConnection();
            prepareStatement = connection.prepareStatement(sql);
            prepareStatement.setString(1, id);
            prepareStatement.setString(2, type);
            int result = prepareStatement.executeUpdate();
            if (result > 0) {
                return true;
            }
            return false;
        } catch (CannotGetJdbcConnectionException e) {
            logger.error("删除失败", e);
            throw e;
        } catch (SQLException e) {
            logger.error("删除失败", e);
            throw e;
        } finally {
            if (null != connection) {
                connection.close();
            }
            if (null != prepareStatement) {
                prepareStatement.close();
            }
        }
    }

    /**
     * 修改关联表
     * 
     * @param dto
     * @return
     */
    public boolean update(T12_es_organ_index_assoDto dto) {
        try {
            String sql = "update t12_es_organ_index_asso set organ_key=?,es_index=?,es_type=? where es_id=?";
            int update = this.getJdbcTemplate().update(sql, new Object[] { dto.getOrgankey(), dto.getEs_index(), dto.getEs_type(), dto.getEs_id() });
            if (update > 0) {
                return true;
            }
            return false;
        } catch (DataAccessException e) {
            logger.error("查询失败", e);
            throw e;
        }
    }

    /**
     * 根据id和type进行查询
     * 
     * @param id
     * @param type
     * @return
     */
    public List<T12_es_organ_index_assoDto> query(String id, String type) {
        List<T12_es_organ_index_assoDto> queryForList;
        try {
            String sql = "select es_id, es_type, organ_key as organkey, es_index from t12_es_organ_index_asso where es_id=? and es_type=?";
            RowMapper<T12_es_organ_index_assoDto> rowMapper = new BeanPropertyRowMapper<T12_es_organ_index_assoDto>(T12_es_organ_index_assoDto.class);
            queryForList = this.getJdbcTemplate().query(sql, new Object[] { id, type }, rowMapper);
            logger.debug(LogUtils.format("result", queryForList));
            return queryForList;
        } catch (DataAccessException e) {
            logger.error("查询失败", e);
            throw e;
        }
    }

    /**
     * 根据机构keys进行查询
     * 
     * @param organkeys
     * @return
     */
    public List<T12_es_organ_index_assoDto> queryByOrgankey(List<String> organkeys) {
        List<T12_es_organ_index_assoDto> queryForList;
        try {
            String sql = "select es_id, es_type, organ_key as organkey, es_index from t12_es_organ_index_asso where organ_key in('"
                    + org.apache.commons.lang.StringUtils.join(organkeys, "','") + "')";
            RowMapper<T12_es_organ_index_assoDto> rowMapper = new BeanPropertyRowMapper<T12_es_organ_index_assoDto>(T12_es_organ_index_assoDto.class);
            queryForList = this.getJdbcTemplate().query(sql, rowMapper);
            logger.debug(LogUtils.format("result", queryForList));
            return queryForList;
        } catch (DataAccessException e) {
            logger.error("查询失败", e);
            throw e;
        }
    }
}
