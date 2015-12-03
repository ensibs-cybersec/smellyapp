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

public class DBAccess  {

	private final static String URL = "jdbc:mysql://mysql:3306/test";

	private final static String LOGIN = "root";

	private final static String PASSWORD = "Ensibs56";

	private final static String QUERY_FIND_ELEVES = "SELECT * FROM personnes ";

	private final static String QUERY_FIND_ELEVES_BY_CLASSE = "SELECT * FROM personnes WHERE lastname = ? ";

	private Connection getConnexion() throws SQLException {
		final Connection con = DriverManager.getConnection(URL, LOGIN, PASSWORD);
		return con;
	}

	public Personne createPersonne(Personne p) {

		Connection con = null;
		PreparedStatement stmt = null;
		
		try {
			con = getConnexion();
			stmt = con.prepareStatement("INSERT INTO personnes (lastname, firstname, birthdate) VALUES ('" + p.getNom() + "', '" + p.getPrenom() + "', ?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setDate(1, new java.sql.Date(p.getDateNaissance().getTime()));
			stmt.executeUpdate();
			ResultSet rset = stmt.getGeneratedKeys();
			if (rset.next()) {
				p.setId(rset.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			//System.out.println("ERROR : " + e.toString());
		} finally {

			if (stmt != null) {
				try {
					// Le stmt.close ferme automatiquement le rset
					stmt.close();
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

	public List<Personne> findEleves() {

		List<Personne> eleves = new ArrayList<Personne>();

		Connection con = null;
		Statement stmt = null;

		try {
			con = getConnexion();
			stmt = con.createStatement();
			final ResultSet rset = stmt.executeQuery(QUERY_FIND_ELEVES);

			while (rset.next()) {
				final Personne eleve = rsetToEleve(rset);
				eleves.add(eleve);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (stmt != null) {
				try {
					// Le stmt.close ferme automatiquement le rset
					stmt.close();
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

		return eleves;
	}

	public List<Personne> findElevesByClasse(Integer classe) {
		List<Personne> eleves = new ArrayList<Personne>();

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = getConnexion();
			stmt = con.prepareStatement(QUERY_FIND_ELEVES_BY_CLASSE);
			stmt.setInt(1, classe);

			final ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				final Personne eleve = rsetToEleve(rset);
				eleves.add(eleve);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (stmt != null) {
				try {
					// Le stmt.close ferme automatiquement le rset
					stmt.close();
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

		return eleves;
	}

	private Personne rsetToEleve(final ResultSet rset) throws SQLException {
		final Integer id = rset.getInt("id");
		final String nom = rset.getString("lastname");
		final String prenom = rset.getString("firstname");
		final Date dateNaissance = rset.getDate("birthdate");

		final Personne eleve = new Personne(id, nom, prenom, dateNaissance);
		return eleve;

	}

}
