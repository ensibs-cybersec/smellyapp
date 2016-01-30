package sparkexample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Persistence  {

	private final static String URL = "jdbc:mysql://mysql:3306/test";
	private final static String LOGIN = "ensibs";
	private final static String PASSWORD = "Ensibs56";
	private final static String QUERY_FIND = "SELECT * FROM PERSON";
	private final static String QUERY_FIND_PARAM = "SELECT * FROM PERSON WHERE lastname = ? ";

	private Connection getConnexion() throws SQLException {
		final Connection con = DriverManager.getConnection(URL, LOGIN, PASSWORD);
		return con;
	}

	public Person createPerson(Person p) {
		System.out.println("Calling Java createPerson method with " + p.getLastName() + " as last name");
		Connection con = null;
		PreparedStatement stmt = null;
		
		try {
			con = getConnexion();
			stmt = con.prepareStatement("INSERT INTO PERSON (lastname, firstname, birthdate) VALUES ('" + p.getLastName() + "', '" + p.getFirstName() + "', ?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setDate(1, new java.sql.Date(p.getBirthDate().getTime()));
			stmt.executeUpdate();
			ResultSet rset = stmt.getGeneratedKeys();
			if (rset.next()) {
				p.setId(rset.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (stmt != null) {
				try {
					stmt.close(); // PreparedStatement.close automatically closes the ResultSet
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return p;
	}

	public List<Person> findPersons() {
		List<Person> persons = new ArrayList<Person>();

		Connection con = null;
		Statement stmt = null;

		try {
			con = getConnexion();
			stmt = con.createStatement();
			final ResultSet rset = stmt.executeQuery(QUERY_FIND);

			while (rset.next()) {
				persons.add(rsetToPerson(rset));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (stmt != null) {
				try {
					stmt.close(); // PreparedStatement.close automatically closes the ResultSet
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return persons;
	}

	public List<Person> findPersonsByLastName(String requestedLastName) {
		List<Person> persons = new ArrayList<Person>();

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = getConnexion();
			stmt = con.prepareStatement(QUERY_FIND_PARAM);
			stmt.setString(1, requestedLastName);

			final ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				persons.add(rsetToPerson(rset));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (stmt != null) {
				try {
					stmt.close(); // PreparedStatement.close automatically closes the ResultSet
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return persons;
	}

	private Person rsetToPerson(final ResultSet rset) throws SQLException {
		return new Person(
            rset.getInt("id"), 
            rset.getString("lastname"), 
            rset.getString("firstname"), 
            rset.getDate("birthdate"));
	}

}
