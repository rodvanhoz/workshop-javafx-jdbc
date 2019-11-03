package model.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {
	
	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("insert into Department (Name) values (?)", Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0) {
				throw new DbException("Erro inesperado ao fazer o insert no Department");
			}
			ResultSet rs = st.getGeneratedKeys();
			if (rs.next()) {
				obj.setId(rs.getInt(1));
			}
			
			conn.commit();
		}
		catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new DbException("Erro inesperado ao fazer o rollback: " + e1.getMessage());
			}
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("update Department set Name = ? where Id = ?", Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0) {
				throw new DbException("Nenhum registro foi afetado pelo update");
			}
			
			conn.commit();
		}
		catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new DbException("Erro inesperado ao fazer o rollback: " + e1.getMessage());
			}
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deletById(Integer id) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("delete from Department where id = ?", Statement.RETURN_GENERATED_KEYS);
			
			st.setInt(1, id);
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0) {
				throw new DbException("Nenhum registro foi deletado");
			}
			
			conn.commit();
		}
		catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new DbException("Erro inesperado ao fazer o rollback: " + e1.getMessage());
			}
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("select * "
					+ "from Department "
					+ "where id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				return new Department(rs.getInt("Id"), rs.getString("Name"));
			}
			else {
				return null;
			}
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("select * "
					+ "from Department ");
			
			rs = st.executeQuery();
			
			List<Department> lista = new ArrayList<>();
			while (rs.next()) {
				lista.add(new Department(rs.getInt("Id"), rs.getString("Name")));
			}
			
			return lista;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

}
