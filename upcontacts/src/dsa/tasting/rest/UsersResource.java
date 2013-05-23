package dsa.tasting.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import dsa.tasting.rest.model.User;
import dsa.tasting.rest.util.APIErrorBuilder;
import dsa.tasting.rest.util.DataSourceSAP;

@Path("/users")
public class UsersResource {

	/*
	 * En esta parte del codigo estan las funciones de: crear un usuario nuevo
	 * en base a todos los parametros preguntar por un usuario en concreto
	 * listar todos los usuarios
	 */
	@Context
	protected HttpServletRequest request;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getUsersJSON() {
		return getUsers();
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<JAXBElement<User>> getUsersXML() {
		Iterator<User> it = getUsers().iterator();
		List<JAXBElement<User>> users = new ArrayList<>();
		while (it.hasNext()) {
			users.add(new JAXBElement<User>(new QName("user"), User.class, it
					.next()));

		}
		return users;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUserJSON(User user) {
		insertUser(user);
		Response response = null;
		try {
			response = Response.status(204)
					.location(new URI("/users" + user.getUser())).build();
		} catch (Exception e) {
		}

		return response;
	}

	private List<User> getUsers() {// esta va de puta madre
		Connection connection = null;
		try {
			connection = DataSourceSAP.getInstance().getDataSource()
					.getConnection();
		} catch (SQLException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE
											.getStatusCode(),
									"Service unavailable.", request)).build());
		}
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("select * from usuarios");
			List<User> users = new ArrayList<>();
			while (rs.next()) {
				User user = new User();
				// obligatorios: id_user, user, nombre, telefono, email, sexo,
				user.setId_user(rs.getInt("id_user"));
				user.setUser(rs.getString("user"));
				user.setNombre(rs.getString("nombre"));
				user.setTelefono(rs.getString("telefono"));
				user.setEmail(rs.getString("email"));
				user.setFecha(rs.getString("fecha"));
				user.setSexo(rs.getString("sexo"));
				user.setCiudad(rs.getString("ciudad"));
				// no obligatorios: los demás
				user.setNacionalidad(rs.getString("nacionalidad"));
				user.setEstado_civil(rs.getString("estado_civil"));
				user.setPelo(rs.getString("pelo"));
				user.setOjos(rs.getString("ojos"));
				user.setEstatura(rs.getString("estatura"));
				user.setComplexion(rs.getString("complexion"));
				user.setEmpleo(rs.getString("empleo"));
				user.setHijos(rs.getString("hijos"));
				user.setDescripcion(rs.getString("descripcion"));
				user.setPreferencias(rs.getString("Preferencias"));
				// las añadimos al objeto users
				users.add(user);
			}

			stmt.close();
			connection.close();
			return users;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}

	}

	private void insertUser(User user) {
		Connection connection = null;
		try {
			connection = DataSourceSAP.getInstance().getDataSource()
					.getConnection();
		} catch (SQLException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE
											.getStatusCode(),
									"Service unavailable.", request)).build());
		}

		try {
			// si alguno de los parámetros obligatorios es null se devuelve un
			// Bad request donde te lo pone claro
			System.out.println("lalalallalallalal "+user.getUser());
			System.out.println("lalalallalallalal "+user.getNombre());
			System.out.println("lalalallalallalal "+user.getTelefono());
			System.out.println("lalalallalallalal "+user.getClave());
			if (user.getUser() == null || user.getClave() == null
					|| user.getCiudad() == null || user.getNombre() == null
					|| user.getEmail() == null || user.getFecha() == null
					|| user.getTelefono() == null || user.getSexo() == null) {
				throw new WebApplicationException(
						Response.status(Response.Status.BAD_REQUEST)
								.entity(APIErrorBuilder.buildError(
										Response.Status.BAD_REQUEST
												.getStatusCode(),
										"Bad Request. User, Clave, Ciudad, Nombre, Fecha, Email Sexo y Telefono no pueden ser null.",
										request)).build());
			} else if (getUser(user.getUser()) != null) {
				throw new WebApplicationException(
						Response.status(Response.Status.CONFLICT)
								.entity(APIErrorBuilder.buildError(
										Response.Status.CONFLICT
												.getStatusCode(),
										"Conflict. El usuario ya existe con ese username, elige otro.",
										request)).build());
			}
			System.out.println("lalalol");

			Statement stmt = connection.createStatement();
			System.out.println("lalalol");
			connection.setAutoCommit(false);
			// ahora hacemos la MACRO COMANDA de insertar
			// primero le ponemos del tiron todo los parametros obligatorios,
			// porque ya sabemos que si que existen
			System.out.println("lalalol");

			StringBuilder sb = new StringBuilder(
					"INSERT INTO usuarios (user, clave, nombre, fecha, telefono, email, sexo, "
							+ "ciudad, nacionalidad, estado_civil, pelo, ojos, estatura, complexion, empleo, "
							+ "hijos, descripcion, preferencias) ");
			sb.append("values ('" + user.getUser() + "'," + " SHA1('"
					+ user.getClave() + "'), " + " '" + user.getNombre() + "',"
					+ " '" + user.getFecha() + "', '" +user.getTelefono()
					+ "', " + " '" + user.getEmail() + "', " + " '"
					+ user.getSexo() + "', '" + user.getCiudad() + "',");
			// ahora le ponemos todos los parámetros no obligatorios
			if (user.getNacionalidad() == "") {
				sb.append(" NULL,");
			} else {
				sb.append(" '" + user.getNacionalidad() + "',");
			}
			if (user.getEstado_civil() == "") {
				sb.append(" NULL,");
			} else {
				sb.append(" '" + user.getEstado_civil() + "',");
			}
			if (user.getPelo() == "") {
				sb.append(" NULL,");
			} else {
				sb.append(" '" + user.getPelo() + "',");
			}
			if (user.getOjos() == "") {
				sb.append(" NULL,");
			} else {
				sb.append(" '" + user.getOjos() + "',");
			}
			if (user.getEstatura() == "") {
				sb.append(" NULL,");
			} else {
				sb.append(" '" + user.getEstatura() + "',");
			}
			if (user.getComplexion() == "") {
				sb.append(" NULL,");
			} else {
				sb.append(" '" + user.getComplexion() + "',");
			}
			if (user.getEmpleo() == "") {
				sb.append(" NULL,");
			} else {
				sb.append(" '" + user.getEmpleo() + "',");
			}
			if (user.getHijos() == "") {
				sb.append(" NULL,");
			} else {
				sb.append(" '" + user.getHijos() + "',");
			}
			if (user.getDescripcion() == "") {
				sb.append(" NULL,");
			} else {
				sb.append(" '" + user.getDescripcion() + "',");
			}
			if (user.getPreferencias() == "") {
				sb.append(" NULL)");
			} else {
				sb.append(" '" + user.getPreferencias() + "')");
			}

			System.out.println("laoaoaooaoaooao - - - -"+sb.toString());
			// la ejecutamos dentro del statement del mysql
			int i = stmt.executeUpdate(sb.toString());
			ResultSet rs = stmt.executeQuery("select * from usuarios where user='"+user.getUser()+"'");

			User user2 = new User();
			while (rs.next()) {
				// obligatorios: id_user, user, nombre, telefono, email, sexo,
				user2.setId_user(rs.getInt("id_user"));
				user2.setUser(rs.getString("user"));
				user2.setNombre(rs.getString("nombre"));
				user2.setTelefono(rs.getString("telefono"));
				user2.setEmail(rs.getString("email"));
				user2.setSexo(rs.getString("sexo"));
				user2.setCiudad(rs.getString("ciudad"));
				// no obligatorios: los demás
				user2.setNacionalidad(rs.getString("nacionalidad"));
				user2.setEstado_civil(rs.getString("estado_civil"));
				user2.setPelo(rs.getString("pelo"));
				user2.setOjos(rs.getString("ojos"));
				user2.setEstatura(rs.getString("estatura"));
				user2.setComplexion(rs.getString("complexion"));
				user2.setEmpleo(rs.getString("empleo"));
				user2.setHijos(rs.getString("hijos"));
				user2.setDescripcion(rs.getString("descripcion"));
				user2.setPreferencias(rs.getString("Preferencias"));
				
			}

			StringBuilder sb2 = new
			  StringBuilder(
			  "insert into roles (user_role, id_user, user) values('registered', "
			  + user2.getId_user() + ", '"+user2.getUser()+"')");

			  System.out.println("laolalalalalla" +sb2);
			  int i2 = stmt.executeUpdate(sb2.toString());
			  System.out.println("laolalalalalla" +sb2);
			  connection.commit();
			 
			stmt.close();

		} catch (Exception e) {

			try {
				connection.rollback();
			} catch (Exception e1) {
			}

			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		} finally {

			try {
				connection.setAutoCommit(true);
				connection.close();
			} catch (Exception e2) {
			}
		}

	}

	private User getUser(String username) {

		Connection connection = null;
		try {
			connection = DataSourceSAP.getInstance().getDataSource()
					.getConnection();
		} catch (SQLException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE
											.getStatusCode(),
									"Service unavailable.", request)).build());
		}

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt
					.executeQuery("select * from usuarios where user='"
							+ username + "'");
			if (!rs.next()) {
				return null;
			}

			User user = new User();
			// obligatorios: id_user, user, nombre, telefono, email, sexo,
			user.setId_user(rs.getInt("id_user"));
			user.setUser(rs.getString("user"));
			user.setNombre(rs.getString("nombre"));
			user.setTelefono(rs.getString("telefono"));
			user.setEmail(rs.getString("email"));
			user.setSexo(rs.getString("sexo"));
			user.setCiudad(rs.getString("ciudad"));
			// no obligatorios: los demás
			user.setNacionalidad(rs.getString("nacionalidad"));
			user.setEstado_civil(rs.getString("estado_civil"));
			user.setPelo(rs.getString("pelo"));
			user.setOjos(rs.getString("ojos"));
			user.setEstatura(rs.getString("estatura"));
			user.setComplexion(rs.getString("complexion"));
			user.setEmpleo(rs.getString("empleo"));
			user.setHijos(rs.getString("hijos"));
			user.setDescripcion(rs.getString("descripcion"));
			user.setPreferencias(rs.getString("Preferencias"));

			stmt.close();
			connection.close();
			return user;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

}